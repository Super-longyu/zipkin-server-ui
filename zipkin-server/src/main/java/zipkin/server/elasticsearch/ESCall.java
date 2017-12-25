/**
 * Copyright 2015-2017 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.server.elasticsearch;

import com.squareup.moshi.JsonReader;
import okhttp3.*;
import okio.BufferedSource;
import zipkin2.elasticsearch.internal.JsonReaders;
import zipkin2.elasticsearch.internal.client.HttpCall;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/23.
 */
public class ESCall {
  private Call call;
  private OkHttpClient ok;

  public ESCall(String userName,String password){
    this.ok = new OkHttpClient.Builder().authenticator(new Authenticator() {
      @Nullable
      @Override
      public Request authenticate(Route route, Response response) throws IOException {
        String basicCredentials = Credentials.basic(userName, password);
        return response.request().newBuilder().header("authorization", basicCredentials).build();
      }}).build();
  }

  public List<String> match_query(String esHost,int esPort,String indices, String query) throws IOException {
    String requestStr ="{\"query\":{\"match\" : {\"message\" : \""+ query + "\"}}}";
    MediaType APPLICATION_JSON = MediaType.parse("application/json");

    HttpUrl httpUrl = new HttpUrl.Builder()
      .scheme("http")
      .host(esHost)
      .port(esPort)
      .addPathSegment("log-"+indices+"*")
      .addPathSegment("_search")
      .build();
    Request httpRequest = new Request.Builder().url(httpUrl)
      .post(RequestBody.create(APPLICATION_JSON, requestStr))
      .tag("search")
      .build();

    this.call = this.ok.newCall(httpRequest);
    Response response = this.call.execute();
    return HttpCall.parseResponse(response, new HttpCall.BodyConverter<List<String>>() {
      @Override
      public List<String> convert(BufferedSource content) throws IOException {
        JsonReader hits = JsonReaders.enterPath(JsonReader.of(content), "hits", "hits");
        if (hits == null || hits.peek() != JsonReader.Token.BEGIN_ARRAY)
          return null;

        List<String> result = new ArrayList<>();
        hits.beginArray();
        while (hits.hasNext()) {
          JsonReader source = JsonReaders.enterPath(hits, "_source");
          if (source != null) {
            source.beginObject();
            while(source.hasNext()){
              if(source.nextName().equals("message")){
                result.add(source.nextString());
              }else{
                source.skipValue();
              }
            }
            source.endObject();
          }
          hits.endObject();
        }
        hits.endArray();
        return result.isEmpty() ? null : result;
      }
    });
  }

}
