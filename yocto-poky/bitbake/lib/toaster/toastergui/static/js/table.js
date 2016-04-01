'use strict';

function tableInit(ctx){

  if (ctx.url.length === 0) {
    throw "No url supplied for retreiving data";
  }

  var tableChromeDone = false;
  var tableTotal = 0;

  var tableParams = {
    limit : 25,
    page : 1,
    orderby : null,
    filter : null,
    search : null,
  };

  var defaultHiddenCols = [];

  var table =  $("#" + ctx.tableName);

  /* if we're loading clean from a url use it's parameters as the default */
  var urlParams = libtoaster.parseUrlParams();

  /* Merge the tableParams and urlParams object properties  */
  tableParams = $.extend(tableParams, urlParams);

  /* Now fix the types that .extend changed for us */
  tableParams.limit = Number(tableParams.limit);
  tableParams.page = Number(tableParams.page);

  loadData(tableParams);

  function loadData(tableParams){
    $.ajax({
        type: "GET",
        url: ctx.url,
        data: tableParams,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function(tableData) {
          updateTable(tableData);
          window.history.replaceState(null, null,
            libtoaster.dumpsUrlParams(tableParams));
        }
    });
  }

  function updateTable(tableData) {
    var tableBody = table.children("tbody");
    var pagination = $('#pagination-'+ctx.tableName);
    var paginationBtns = pagination.children('ul');
    var tableContainer = $("#table-container-"+ctx.tableName);

    tableContainer.css("visibility", "hidden");
    /* To avoid page re-layout flicker when paging set fixed height */
    table.css("padding-bottom", table.height());

    /* Reset table components */
    tableBody.html("");
    paginationBtns.html("");

    if (tableParams.search)
      $('.remove-search-btn-'+ctx.tableName).show();
    else
      $('.remove-search-btn-'+ctx.tableName).hide();

    $('.table-count-' + ctx.tableName).text(tableData.total);
    tableTotal = tableData.total;

    if (tableData.total === 0){
      tableContainer.hide();
      /* If we were searching show the new search bar and return */
      if (tableParams.search){
        $("#new-search-input-"+ctx.tableName).val(tableParams.search);
        $("#no-results-"+ctx.tableName).show();
      }
      table.trigger("table-done", [tableData.total, tableParams]);

      return;

    /* We don't want to clutter the place with the table chrome if there
     * are only a few results */
    } else if (tableData.total <= 10 &&
               !tableParams.filter &&
               !tableParams.search){
      $("#table-chrome-"+ctx.tableName).hide();
      pagination.hide();
    } else {
      tableContainer.show();
      $("#no-results-"+ctx.tableName).hide();
    }

    setupTableChrome(tableData);

    /* Add table data rows */
    var column_index;
    for (var i in tableData.rows){
      /* only display if the column is display-able */
      var row = $("<tr></tr>");
      column_index = -1;
      for (var key_j in tableData.rows[i]){

        /* if we have a static: version of a key, prefer the static: version for rendering */
        var orig_key_j = key_j;

        if (key_j.indexOf("static:") === 0) {
          if (key_j.substr("static:".length) in tableData.rows[i]) {
            continue;
          }
          orig_key_j = key_j.substr("static:".length)
        } else if (("static:" + key_j) in tableData.rows[i]) {
          key_j = "static:" + key_j;
        }

        /* we skip over un-displayable column entries */
        column_index += 1;
        if (! tableData.columns[column_index].displayable) {
          continue;
        }

        var td = $("<td></td>");
        td.prop("class", orig_key_j);
        if (tableData.rows[i][key_j]){
          td.html(tableData.rows[i][key_j]);
        }
        row.append(td);
      }
      tableBody.append(row);

      /* If we have layerbtns then initialise them */
      layerBtnsInit();

      /* If we have popovers initialise them now */
      $('td > a.btn').popover({
        html:true,
        placement:'left',
        container:'body',
        trigger:'manual'
      }).click(function(e){
        $('td > a.btn').not(this).popover('hide');
        /* ideally we would use 'toggle' here
         * but it seems buggy in our Bootstrap version
         */
        $(this).popover('show');
        e.stopPropagation();
      });

      /* enable help information tooltip */
      $(".get-help").tooltip({container:'body', html:true, delay:{show:300}});
    }

    /* Setup the pagination controls */

    var start = tableParams.page - 2;
    var end = tableParams.page + 2;
    var numPages = Math.ceil(tableData.total/tableParams.limit);

    if (numPages >  1){
      if (tableParams.page < 3)
        end = 5;

      for (var page_i=1; page_i <= numPages;  page_i++){
        if (page_i >= start && page_i <= end){
          var btn = $('<li><a href="#" class="page">'+page_i+'</a></li>');

          if (page_i === tableParams.page){
            btn.addClass("active");
          }

          /* Add the click handler */
          btn.click(pageButtonClicked);
          paginationBtns.append(btn);
        }
      }
    }

    loadColumnsPreference();

    table.css("padding-bottom", 0);
    tableContainer.css("visibility", "visible");

    table.trigger("table-done", [tableData.total, tableParams]);
  }

  function setupTableChrome(tableData){
    if (tableChromeDone === true)
      return;

    var tableHeadRow = table.find("thead");
    var editColMenu = $("#table-chrome-"+ctx.tableName).find(".editcol");

    tableHeadRow.html("");
    editColMenu.html("");

    if (!tableParams.orderby && tableData.default_orderby){
      tableParams.orderby = tableData.default_orderby;
    }

    /* Add table header and column toggle menu */
    for (var i in tableData.columns){
      var col = tableData.columns[i];
      if (col.displayable === false) {
        continue;
      }
      var header = $("<th></th>");
      header.prop("class", col.field_name);

      /* Setup the help text */
      if (col.help_text.length > 0) {
        var help_text = $('<i class="icon-question-sign get-help"> </i>');
        help_text.tooltip({title: col.help_text});
        header.append(help_text);
      }

      /* Setup the orderable title */
      if (col.orderable) {
        var title = $('<a href=\"#\" ></a>');

        title.data('field-name', col.field_name);
        title.text(col.title);
        title.click(sortColumnClicked);

        header.append(title);

        header.append(' <i class="icon-caret-down" style="display:none"></i>');
        header.append(' <i class="icon-caret-up" style="display:none"></i>');

        /* If we're currently ordered setup the visual indicator */
        if (col.field_name === tableParams.orderby ||
          '-' + col.field_name === tableParams.orderby){
          header.children("a").addClass("sorted");

          if (tableParams.orderby.indexOf("-") === -1){
            header.find('.icon-caret-down').show();
          } else {
            header.find('.icon-caret-up').show();
          }
        }

      } else {
        /* Not orderable */
        header.addClass("muted");
        header.css("font-weight", "normal");
        header.append(col.title+' ');
      }

      /* Setup the filter button */
      if (col.filter_name){
        var filterBtn = $('<a href="#" role="button" class="pull-right btn btn-mini" data-toggle="modal"><i class="icon-filter filtered"></i></a>');

        filterBtn.data('filter-name', col.filter_name);
        filterBtn.prop('id', col.filter_name);
        filterBtn.click(filterOpenClicked);

        /* If we're currently being filtered setup the visial indicator */
        if (tableParams.filter &&
            tableParams.filter.match('^'+col.filter_name)) {

            filterBtnActive(filterBtn, true);
        }
        header.append(filterBtn);
      }

      /* Done making the header now add it */
      tableHeadRow.append(header);

      /* Now setup the checkbox state and click handler */
      var toggler = $('<li><label class="checkbox">'+col.title+'<input type="checkbox" id="checkbox-'+ col.field_name +'" class="col-toggle" value="'+col.field_name+'" /></label></li>');

      var togglerInput = toggler.find("input");

      togglerInput.attr("checked","checked");

      /* If we can hide the column enable the checkbox action */
      if (col.hideable){
        togglerInput.click(colToggleClicked);
      } else {
        toggler.find("label").addClass("muted");
        togglerInput.attr("disabled", "disabled");
      }

      if (col.hidden) {
        defaultHiddenCols.push(col.field_name);
      }

      editColMenu.append(toggler);
    } /* End for each column */

    tableChromeDone = true;
  }

  /* Toggles the active state of the filter button */
  function filterBtnActive(filterBtn, active){
    if (active) {
      filterBtn.addClass("btn-primary");

      filterBtn.tooltip({
          html: true,
          title: '<button class="btn btn-small btn-primary" onClick=\'$("#clear-filter-btn-'+ ctx.tableName +'").click();\'>Clear filter</button>',
          placement: 'bottom',
          delay: {
            hide: 1500,
            show: 400,
          },
      });
    } else {
      filterBtn.removeClass("btn-primary");
      filterBtn.tooltip('destroy');
    }
  }

  /* Display or hide table columns based on the cookie preference or defaults */
  function loadColumnsPreference(){
    var cookie_data = $.cookie("cols");

    if (cookie_data) {
      var cols_hidden = JSON.parse($.cookie("cols"));

      /* For each of the columns check if we should hide them
       * also update the checked status in the Edit columns menu
       */
      $("#"+ctx.tableName+" th").each(function(){
        for (var i in cols_hidden){
          if ($(this).hasClass(cols_hidden[i])){
            $("."+cols_hidden[i]).hide();
            $("#checkbox-"+cols_hidden[i]).removeAttr("checked");
          }
        }
      });
      } else {
        /* Disable these columns by default when we have no columns
         * user setting.
         */
        for (var i in defaultHiddenCols) {
          $("."+defaultHiddenCols[i]).hide();
          $("#checkbox-"+defaultHiddenCols[i]).removeAttr("checked");
        }
    }
  }

  function sortColumnClicked(){

    /* We only have one sort at a time so remove any existing sort indicators */
    $("#"+ctx.tableName+" th .icon-caret-down").hide();
    $("#"+ctx.tableName+" th .icon-caret-up").hide();
    $("#"+ctx.tableName+" th a").removeClass("sorted");

    var fieldName = $(this).data('field-name');

    /* if we're already sorted sort the other way */
    if (tableParams.orderby === fieldName &&
        tableParams.orderby.indexOf('-') === -1) {
      tableParams.orderby = '-' + $(this).data('field-name');
      $(this).parent().children('.icon-caret-up').show();
    } else {
      tableParams.orderby = $(this).data('field-name');
      $(this).parent().children('.icon-caret-down').show();
    }

    $(this).addClass("sorted");

    loadData(tableParams);
  }

  function pageButtonClicked(e) {
    tableParams.page = Number($(this).text());
    loadData(tableParams);
    /* Stop page jumps when clicking on # links */
    e.preventDefault();
  }

  /* Toggle a table column */
  function colToggleClicked (){
    var col = $(this).val();
    var disabled_cols = [];

    if ($(this).prop("checked")) {
      $("."+col).show();
    }  else {
      $("."+col).hide();
      /* If we're ordered by the column we're hiding remove the order by */
      if (col === tableParams.orderby ||
          '-' + col === tableParams.orderby){
        tableParams.orderby = null;
        loadData(tableParams);
      }
    }

    /* Update the cookie with the unchecked columns */
    $(".col-toggle").not(":checked").map(function(){
      disabled_cols.push($(this).val());
    });

    $.cookie("cols", JSON.stringify(disabled_cols));
  }

  function filterOpenClicked(){
    var filterName = $(this).data('filter-name');

    /* We need to pass in the curren search so that the filter counts take
     * into account the current search filter
     */
    var params = {
      'name' : filterName,
      'search': tableParams.search,
      'cmd': 'filterinfo',
    };

    $.ajax({
        type: "GET",
        url: ctx.url,
        data: params,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (filterData) {
          var filterActionRadios = $('#filter-actions-'+ctx.tableName);

          $('#filter-modal-title-'+ctx.tableName).text(filterData.title);

          filterActionRadios.text("");

          for (var i in filterData.filter_actions){
            var filterAction = filterData.filter_actions[i];

            var action = $('<label class="radio"><input type="radio" name="filter" value=""><span class="filter-title"></span></label>');
            var actionTitle = filterAction.title + ' (' + filterAction.count + ')';

            var radioInput = action.children("input");

            if (Number(filterAction.count) == 0){
              radioInput.attr("disabled", "disabled");
            }

            action.children(".filter-title").text(actionTitle);

            radioInput.val(filterName + ':' + filterAction.name);

            /* Setup the current selected filter, default to 'all' if
             * no current filter selected.
             */
            if ((tableParams.filter &&
                tableParams.filter === radioInput.val()) ||
                filterAction.name == 'all') {
                radioInput.attr("checked", "checked");
            }

            filterActionRadios.append(action);
          }

          $('#filter-modal-'+ctx.tableName).modal('show');
        }
    });
  }


  $(".get-help").tooltip({container:'body', html:true, delay:{show:300}});

  /* Keep the Edit columns menu open after click by eating the event */
  $('.dropdown-menu').click(function(e) {
    e.stopPropagation();
  });

  $(".pagesize-"+ctx.tableName).val(tableParams.limit);

  /* page size selector  */
  $(".pagesize-"+ctx.tableName).change(function(e){
    tableParams.limit = Number(this.value);
    if ((tableParams.page * tableParams.limit) > tableTotal)
      tableParams.page = 1;

    loadData(tableParams);
    /* sync the other selectors on the page */
    $(".pagesize-"+ctx.tableName).val(this.value);
    e.preventDefault();
  });

  $("#search-submit-"+ctx.tableName).click(function(e){
    var searchTerm = $("#search-input-"+ctx.tableName).val();

    tableParams.page = 1;
    tableParams.search = searchTerm;

    /* If a filter was active we remove it */
    if (tableParams.filter) {
      var filterBtn = $("#" + tableParams.filter.split(":")[0]);
      filterBtnActive(filterBtn, false);
      tableParams.filter = null;
    }

    loadData(tableParams);

    e.preventDefault();
  });

  $('.remove-search-btn-'+ctx.tableName).click(function(e){
    e.preventDefault();

    tableParams.page = 1;
    tableParams.search = null;
    loadData(tableParams);

    $("#search-input-"+ctx.tableName).val("");
    $(this).hide();
  });

  $("#search-input-"+ctx.tableName).keyup(function(e){
    if (e.which === 13)
      $('#search-submit-'+ctx.tableName).click();
  });

  /* Stop page jumps when clicking on # links */
  $('a[href="#"]').click(function(e){
    e.preventDefault();
  });

  $("#clear-filter-btn-"+ctx.tableName).click(function(){
    var filterBtn = $("#" + tableParams.filter.split(":")[0]);
    filterBtnActive(filterBtn, false);

    tableParams.filter = null;
    loadData(tableParams);
  });

  $("#filter-modal-form-"+ctx.tableName).submit(function(e){
    e.preventDefault();

    tableParams.filter = $(this).find("input[type='radio']:checked").val();

    var filterBtn = $("#" + tableParams.filter.split(":")[0]);

    /* All === remove filter */
    if (tableParams.filter.match(":all$")) {
      tableParams.filter = null;
      filterBtnActive(filterBtn, false);
    } else {
      filterBtnActive(filterBtn, true);
    }

    loadData(tableParams);

    $(this).parent().modal('hide');
  });
}
