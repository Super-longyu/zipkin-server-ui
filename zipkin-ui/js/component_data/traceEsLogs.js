import {component} from 'flightjs';
import $ from 'jquery';
import {getError} from '../../js/component_ui/error';

export default component(function TraceEsLogsData() {
  this.traceEsLogData = function (e, data) {
    const traceId = data.traceId;
    const serviceName = data.serviceName;
    $.ajax(`api/v1/trace/logs?serviceName=${serviceName}&traceId=${traceId}`, {
      type: 'GET',
      dataType: 'json'
    }).done(traceEsLogs => {
      this.trigger('traceEsLogsPageModelView', { traceEsLogs});
    }).fail(e => {
      this.trigger('uiServerError',
      getError(`Cannot load traceLogs of ${this.attr.traceId}`, e));
    });
  };

  this.after('initialize', function() {
    this.on(document, 'traceEsLogData', this.traceEsLogData);
  });
});
