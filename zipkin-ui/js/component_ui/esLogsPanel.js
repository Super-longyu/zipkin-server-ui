import {component} from 'flightjs';
import $ from 'jquery';

export default component(function esLogsPanel() {
  this.$esLogInfoTemplate = null;

  this.show = function(e, data) {
    const self = this;
    this.$node.find('.modal-title').text(data.title);
    this.$node.find('.modal-body pre').text('');

    const $esLogInfoBody = this.$node.find('#esLogInfo tbody').text('');
    const esLogInfo = data.obj;

    $.each(esLogInfo, (i, value) => {
      const $row = self.$esLogInfoTemplate.clone();
      $row.find('.value').text(value);
      $esLogInfoBody.append($row);
    });

    this.$node.modal('show');
  };

  this.after('initialize', function() {
    this.$node.modal('hide');
    this.$esLogInfoTemplate = this.$node.find('#esLogInfo tbody tr').remove();
    this.on(document, 'uiRequestEsLogsPanel', this.show);
  });
});
