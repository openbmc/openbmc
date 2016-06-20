"use strict";
/* All shared functionality to go in libtoaster object.
 * This object really just helps readability since we can then have
 * a traceable namespace.
 */
var libtoaster = (function (){

  /* makeTypeahead parameters
   * elementSelector: JQuery elementSelector string
   * xhrUrl: the url to get the JSON from expects JSON in the form:
   *  { "list": [ { "name": "test", "detail" : "a test thing"  }, .... ] }
   * xhrParams: the data/parameters to pass to the getJSON url e.g.
   *  { 'type' : 'projects' } the text typed will be passed as 'search'.
   *  selectedCB: function to call once an item has been selected one
   *  arg of the item.
   */
  function _makeTypeahead (jQElement, xhrUrl, xhrParams, selectedCB) {
    if (!xhrUrl || xhrUrl.length === 0)
      throw("No url to typeahead supplied");

    var xhrReq;

    jQElement.typeahead({
        // each time the typeahead's choices change, a
        // "typeahead-choices-change" event is fired with an object
        // containing the available choices in a "choices" property
        source: function(query, process){
          xhrParams.search = query;

          /* If we have a request in progress don't fire off another one*/
          if (xhrReq)
            xhrReq.abort();

          xhrReq = $.getJSON(xhrUrl, this.options.xhrParams, function(data){
            if (data.error !== "ok") {
              console.log("Error getting data from server "+data.error);
              return;
            }

            xhrReq = null;

            jQElement.trigger("typeahead-choices-change", {choices: data.results});

            return process(data.results);
          });
        },
        updater: function(item) {
          var itemObj = this.$menu.find('.active').data('itemObject');
          selectedCB(itemObj);
          return item;
        },
        matcher: function(item) {
          if (!item.hasOwnProperty('name')) {
            console.log("Name property missing in data");
            return 0;
          }

          if (this.$element.val().length === 0)
            return 0;

          return 1;
        },
        highlighter: function (item) {
          /* Use jquery to escape the item name and detail */
          var current = $("<span></span>").text(item.name + ' '+item.detail);
          current = current.html();

          var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, '\\$&')
          return current.replace(new RegExp('(' + query + ')', 'ig'), function ($1, match) {
            return '<strong>' + match + '</strong>'
          })
        },
        sorter: function (items) { return items; },
        xhrUrl: xhrUrl,
        xhrParams: xhrParams,
        xhrReq: xhrReq,
    });


    /* Copy of bootstrap's render func but sets selectedObject value */
    function customRenderFunc (items) {
      var that = this;

      items = $(items).map(function (i, item) {
        i = $(that.options.item).attr('data-value', item.name).data('itemObject', item);
        i.find('a').html(that.highlighter(item));
        return i[0];
      });

      items.first().addClass('active');
      this.$menu.html(items);
      return this;
    }

    jQElement.data('typeahead').render = customRenderFunc;
  }

  /* startABuild:
   * url: xhr_buildrequest or null for current project
   * targets: an array or space separated list of targets to build
   * onsuccess: callback for successful execution
   * onfail: callback for failed execution
   */
  function _startABuild (url, targets, onsuccess, onfail) {

    if (!url)
      url = libtoaster.ctx.xhrBuildRequestUrl;

    /* Flatten the array of targets into a space spearated list */
    if (targets instanceof Array){
      targets = targets.reduce(function(prevV, nextV){
        return prev + ' ' + next;
      });
    }

    $.ajax( {
        type: "POST",
        url: url,
        data: { 'targets' : targets },
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (_data) {
          if (_data.error !== "ok") {
            console.warn(_data.error);
          } else {
            if (onsuccess !== undefined) onsuccess(_data);
          }
        },
        error: function (_data) {
          console.warn("Call failed");
          console.warn(_data);
          if (onfail) onfail(data);
    } });
  }

  /* cancelABuild:
   * url: xhr_buildrequest url or null for current project
   * buildRequestIds: space separated list of build request ids
   * onsuccess: callback for successful execution
   * onfail: callback for failed execution
   */
  function _cancelABuild(url, buildRequestIds, onsuccess, onfail){
    if (!url)
      url = libtoaster.ctx.xhrBuildRequestUrl;

    $.ajax( {
        type: "POST",
        url: url,
        data: { 'buildCancel': buildRequestIds },
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (_data) {
          if (_data.error !== "ok") {
            console.warn(_data.error);
          } else {
            if (onsuccess) onsuccess(_data);
          }
        },
        error: function (_data) {
          console.warn("Call failed");
          console.warn(_data);
          if (onfail) onfail(_data);
        }
    });
  }

  /* Get a project's configuration info */
  function _getProjectInfo(url, onsuccess, onfail){
    $.ajax({
        type: "GET",
        data : { format: "json" },
        url: url,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (_data) {
          if (_data.error !== "ok") {
            console.warn(_data.error);
          } else {
            if (onsuccess !== undefined) onsuccess(_data);
          }
        },
        error: function (_data) {
          console.warn(_data);
          if (onfail) onfail(_data);
        }
    });
  }

  /* Properties for data can be:
   * layerDel (csv)
   * layerAdd (csv)
   * projectName
   * projectVersion
   * machineName
   */
  function _editCurrentProject(data, onSuccess, onFail){
    $.ajax({
        type: "POST",
        url: libtoaster.ctx.projectPageUrl + "?format=json",
        data: data,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (data) {
          if (data.error != "ok") {
            console.log(data.error);
            if (onFail !== undefined)
              onFail(data);
          } else {
            if (onSuccess !== undefined)
              onSuccess(data);
          }
        },
        error: function (data) {
          console.log("Call failed");
          console.log(data);
        }
    });
  }

  function _getLayerDepsForProject(url, onSuccess, onFail){
    /* Check for dependencies not in the current project */
    $.getJSON(url,
      { format: 'json' },
      function(data) {
        if (data.error != "ok") {
          console.log(data.error);
          if (onFail !== undefined)
            onFail(data);
        } else {
          var deps = {};
          /* Filter out layer dep ids which are in the
           * project already.
           */
          deps.list = data.layerdeps.list.filter(function(layerObj){
            return (data.projectlayers.lastIndexOf(layerObj.id) < 0);
          });

          onSuccess(deps);
        }
      }, function() {
        console.log("E: Failed to make request");
    });
  }

  /* parses the query string of the current window.location to an object */
  function _parseUrlParams() {
    var string = window.location.search;
    string = string.substr(1);
    var stringArray = string.split ("&");
    var obj = {};

    for (var i in stringArray) {
      var keyVal = stringArray[i].split ("=");
      obj[keyVal[0]] = keyVal[1];
    }

    return obj;
  }

  /* takes a flat object and outputs it as a query string
   * e.g. the output of dumpsUrlParams
   */
  function _dumpsUrlParams(obj) {
    var str = "?";

    for (var key in obj){
      if (!obj[key])
        continue;

      str += key+ "="+obj[key].toString();
      str += "&";
    }

    /* Maintain the current hash */
    str += window.location.hash;

    return str;
  }

  function _addRmLayer(layerObj, add, doneCb){
    if (add === true) {
      /* If adding get the deps for this layer */
      libtoaster.getLayerDepsForProject(layerObj.layerdetailurl,
        function (layers) {

        /* got result for dependencies */
        if (layers.list.length === 0){
          var editData = { layerAdd : layerObj.id };
          libtoaster.editCurrentProject(editData, function() {
            doneCb([]);
          });
          return;
        } else {
          try {
            showLayerDepsModal(layerObj, layers.list, null, null,  true, doneCb);
          }  catch (e) {
            $.getScript(libtoaster.ctx.jsUrl + "layerDepsModal.js", function(){
              showLayerDepsModal(layerObj, layers.list, null, null,  true, doneCb);
            }, function(){
              console.warn("Failed to load layerDepsModal");
            });
          }
        }
      }, null);
    } else if (add === false) {
      var editData = { layerDel : layerObj.id };

      libtoaster.editCurrentProject(editData, function () {
        doneCb([]);
      }, function () {
        console.warn ("Removing layer from project failed");
        doneCb(null);
      });
    }
  }

  function _makeLayerAddRmAlertMsg(layer, layerDepsList, add) {
    var alertMsg;

    if (layerDepsList.length > 0 && add === true) {
      alertMsg = $("<span>You have added <strong>"+(layerDepsList.length+1)+"</strong> layers to your project: <a id=\"layer-affected-name\"></a> and its dependencies </span>");

      /* Build the layer deps list */
      layerDepsList.map(function(layer, i){
        var link = $("<a></a>");

        link.attr("href", layer.layerdetailurl);
        link.text(layer.name);
        link.tooltip({title: layer.tooltip});

        if (i !== 0)
          alertMsg.append(", ");

        alertMsg.append(link);
      });
    } else if (layerDepsList.length === 0 && add === true) {
      alertMsg = $("<span>You have added <strong>1</strong> layer to your project: <a id=\"layer-affected-name\"></a></span></span>");
    } else if (add === false) {
      alertMsg = $("<span>You have removed <strong>1</strong> layer from your project: <a id=\"layer-affected-name\"></a></span>");
    }

    alertMsg.children("#layer-affected-name").text(layer.name);
    alertMsg.children("#layer-affected-name").attr("href", layer.layerdetailurl);

    return alertMsg.html();
  }

  function _showChangeNotification(message){
    var alertMsg = $("#change-notification-msg");

    alertMsg.html(message);
    $("#change-notification, #change-notification *").fadeIn();
  }

  function _createCustomRecipe(name, baseRecipeId, doneCb){
    var data = {
      'name' : name,
      'project' : libtoaster.ctx.projectId,
      'base' : baseRecipeId,
    };

    $.ajax({
        type: "POST",
        url: libtoaster.ctx.xhrCustomRecipeUrl,
        data: data,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (ret) {
          if (doneCb){
            doneCb(ret);
          } else if (ret.error !== "ok") {
            console.warn(ret.error);
          }
        },
        error: function (ret) {
          console.warn("Call failed");
          console.warn(ret);
        }
    });
  }


  return {
    reload_params : reload_params,
    startABuild : _startABuild,
    cancelABuild : _cancelABuild,
    makeTypeahead : _makeTypeahead,
    getProjectInfo: _getProjectInfo,
    getLayerDepsForProject : _getLayerDepsForProject,
    editCurrentProject : _editCurrentProject,
    debug: false,
    parseUrlParams : _parseUrlParams,
    dumpsUrlParams : _dumpsUrlParams,
    addRmLayer : _addRmLayer,
    makeLayerAddRmAlertMsg : _makeLayerAddRmAlertMsg,
    showChangeNotification : _showChangeNotification,
    createCustomRecipe: _createCustomRecipe,
  };
})();

/* keep this in the global scope for compatability */
function reload_params(params) {
    var uri = window.location.href;
    var splitlist = uri.split("?");
    var url = splitlist[0];
    var parameters = splitlist[1];
    // deserialize the call parameters
    var cparams = [];
    if(parameters)
      cparams = parameters.split("&");

    var nparams = {};
    for (var i = 0; i < cparams.length; i++) {
        var temp = cparams[i].split("=");
        nparams[temp[0]] = temp[1];
    }
    // update parameter values
    for (i in params) {
        nparams[encodeURIComponent(i)] = encodeURIComponent(params[i]);
    }
    // serialize the structure
    var callparams = [];
    for (i in nparams) {
        callparams.push(i+"="+nparams[i]);
    }
    window.location.href = url+"?"+callparams.join('&');
}


/* Things that happen for all pages */
$(document).ready(function() {

  var ajaxLoadingTimer;

  /* If we don't have a console object which might be the case in some
     * browsers, no-op it to avoid undefined errors.
     */
    if (!window.console) {
      window.console = {};
      window.console.warn = function() {};
      window.console.error = function() {};
    }

    /*
     * PrettyPrint plugin.
     *
     */
    // Init
    prettyPrint();

    // Prevent invalid links from jumping page scroll
    $('a[href=#]').click(function() {
        return false;
    });


    /* START TODO Delete this section now redundant */
    /* Belen's additions */

    // turn Edit columns dropdown into a multiselect menu
    $('.dropdown-menu input, .dropdown-menu label').click(function(e) {
        e.stopPropagation();
    });

    // enable popovers in any table cells that contain an anchor with the
    // .btn class applied, and make sure popovers work on click, are mutually
    // exclusive and they close when your click outside their area

    $('html').click(function(){
        $('td > a.btn').popover('hide');
    });

    $('td > a.btn').popover({
        html:true,
        placement:'left',
        container:'body',
        trigger:'manual'
    }).click(function(e){
        $('td > a.btn').not(this).popover('hide');
        // ideally we would use 'toggle' here
        // but it seems buggy in our Bootstrap version
        $(this).popover('show');
        e.stopPropagation();
    });

    // enable tooltips for applied filters
    $('th a.btn-primary').tooltip({container:'body', html:true, placement:'bottom', delay:{hide:1500}});

    // hide applied filter tooltip when you click on the filter button
    $('th a.btn-primary').click(function () {
        $('.tooltip').hide();
    });

    /* Initialise bootstrap tooltips */
    $(".get-help, [data-toggle=tooltip]").tooltip({
      container : 'body',
      html : true,
      delay: { show : 300 }
    });

    // show help bubble only on hover inside tables
    $(".hover-help").css("visibility","hidden");

    $("table").on("mouseover", "th, td", function () {
        $(this).find(".hover-help").css("visibility","visible");
    });

    $("table").on("mouseleave", "th, td", function () {
        $(this).find(".hover-help").css("visibility","hidden");
    });

    /* END TODO Delete this section now redundant */

    // show task type and outcome in task details pages
    $(".task-info").tooltip({ container: 'body', html: true, delay: {show: 200}, placement: 'right' });

    // initialise the tooltips for the icon-pencil icons
    $(".icon-pencil").tooltip({ container: 'body', html: true, delay: {show: 400}, title: "Change" });

    // initialise the tooltips for the download icons
    $(".icon-download-alt").tooltip({ container: 'body', html: true, delay: { show: 200 } });

    // initialise popover for debug information
    $(".icon-info-sign").popover( { placement: 'bottom', html: true, container: 'body' });

    // linking directly to tabs
    $(function(){
          var hash = window.location.hash;
          $('ul.nav a[href="' + hash + '"]').tab('show');

          $('.nav-tabs a').click(function () {
            $(this).tab('show');
            $('body').scrollTop();
          });
    });

    // toggle for long content (variables, python stack trace, etc)
    $('.full, .full-hide').hide();
    $('.full-show').click(function(){
        $('.full').slideDown(function(){
            $('.full-hide').show();
        });
        $(this).hide();
    });
    $('.full-hide').click(function(){
        $(this).hide();
        $('.full').slideUp(function(){
            $('.full-show').show();
        });
    });

    //toggle the errors and warnings sections
    $('.show-errors').click(function() {
        $('#collapse-errors').addClass('in');
    });
    $('.toggle-errors').click(function() {
        $('#collapse-errors').toggleClass('in');
    });
    $('.show-warnings').click(function() {
        $('#collapse-warnings').addClass('in');
    });
    $('.toggle-warnings').click(function() {
        $('#collapse-warnings').toggleClass('in');
    });
    $('.show-exceptions').click(function() {
        $('#collapse-exceptions').addClass('in');
    });
    $('.toggle-exceptions').click(function() {
        $('#collapse-exceptions').toggleClass('in');
    });


    $("#hide-alert").click(function(){
      $(this).parent().fadeOut();
    });

    //show warnings section when requested from the previous page
    if (location.href.search('#warnings') > -1) {
        $('#collapse-warnings').addClass('in');
    }

    /* Show the loading notification if nothing has happend after 1.5
     * seconds
     */
    $(document).bind("ajaxStart", function(){
      if (ajaxLoadingTimer)
        window.clearTimeout(ajaxLoadingTimer);

      ajaxLoadingTimer = window.setTimeout(function() {
        $("#loading-notification").fadeIn();
      }, 1200);
    });

    $(document).bind("ajaxStop", function(){
      if (ajaxLoadingTimer)
        window.clearTimeout(ajaxLoadingTimer);

      $("#loading-notification").fadeOut();
    });

    $(document).ajaxError(function(event, jqxhr, settings, errMsg){
      if (errMsg === 'abort')
        return;

      console.warn("Problem with xhr call");
      console.warn(errMsg);
      console.warn(jqxhr.responseText);
    });

    function check_for_duplicate_ids () {
      /* warn about duplicate element ids */
      var ids = {};
      $("[id]").each(function() {
        if (this.id && ids[this.id]) {
          console.warn('Duplicate element id #'+this.id);
        }
        ids[this.id] = true;
      });
    }

    if (libtoaster.debug) {
      check_for_duplicate_ids();
    } else {
      /* Debug is false so supress warnings by overriding the functions */
      window.console.warn = function () {};
      window.console.error = function () {};
   }
});
