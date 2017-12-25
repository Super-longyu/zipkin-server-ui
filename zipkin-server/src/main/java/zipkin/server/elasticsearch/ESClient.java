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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/11/23.
 */
public class ESClient {
  private ESCall esCall;
  public ESClient(String userName,String password){
    esCall = new ESCall(userName, password);
  }

  public List<String> getLogs(String esUrl,String serviceName, String traceId){
    String pattern = "http://(.*):(.*)";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(esUrl);

    String esHost;
    int esPort;
    if (m.find( )) {
      esHost = m.group(1);
      esPort = Integer.parseInt(m.group(2));
    }else{
      esHost = "localhost";
      esPort = 9200;
    }
    //System.out.println("esHots:"+esHost+"esPort:"+esPort);
    List<String> result = new ArrayList<>();
    try {
      result = esCall.match_query(esHost, esPort, serviceName, traceId);
      return result;
    } catch (IOException e) {
      return result;
    }
  }
}
