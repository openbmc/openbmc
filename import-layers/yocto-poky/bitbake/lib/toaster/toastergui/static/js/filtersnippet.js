"use strict"

// The disable removes the 'datepicker' attribute and
// settings, so you have to re-initialize it each time
// the date range is selected and enabled
// DOM is used instead of jQuery to find the elements
// in all contexts
function date_enable (key, action) {

    var elemFrom=document.getElementById("date_from_"+key);
    var elemTo=document.getElementById("date_to_"+key);

    if ('enable' == action) {
        elemFrom.removeAttribute("disabled");
        elemTo.removeAttribute("disabled");

        $(elemFrom).datepicker();
        $(elemTo).datepicker();

        $(elemFrom).datepicker( "option", "dateFormat", "dd/mm/yy" );
        $(elemTo).datepicker( "option", "dateFormat", "dd/mm/yy" );

        $(elemFrom).datepicker( "setDate", elemFrom.getAttribute( "data-setDate") );
        $(elemTo).datepicker( "setDate", elemTo.getAttribute( "data-setDate") );
        $(elemFrom).datepicker( "option", "minDate", elemFrom.getAttribute( "data-minDate"));
        $(elemTo).datepicker( "option", "minDate",  elemTo.getAttribute( "data-minDate"));
        $(elemFrom).datepicker( "option", "maxDate", elemFrom.getAttribute( "data-maxDate"));
        $(elemTo).datepicker( "option", "maxDate", elemTo.getAttribute( "data-maxDate"));
    } else {
        elemFrom.setAttribute("disabled","disabled");
        elemTo.setAttribute("disabled","disabled");
    }
}

// Initialize the date picker elements with their default state variables, and
// register the radio button and form actions
function date_init (key, from_date, to_date, min_date, max_date, initial_enable) {

    var elemFrom=document.getElementById("date_from_"+key);
    var elemTo=document.getElementById("date_to_"+key);

    // Were there any daterange filters instantiated? (e.g. no builds found)
    if (null == elemFrom) {
        return;
    }

    // init the datepicker context data
    elemFrom.setAttribute( "data-setDate", from_date );
    elemTo.setAttribute( "data-setDate", to_date );
    elemFrom.setAttribute( "data-minDate", min_date);
    elemTo.setAttribute( "data-minDate",  min_date);
    elemFrom.setAttribute( "data-maxDate", max_date);
    elemTo.setAttribute( "data-maxDate", max_date);

    // does the date set start enabled?
    if (key == initial_enable) {
        date_enable (key, "enable");
    } else {
        date_enable (key, "disable");
    }

    // catch the radio button selects for enable/disable
    $('input:radio[name="filter"]').change(function(){
        if ($(this).val() == 'daterange') {
            key=$(this).attr("data-key");
            date_enable (key, 'enable');
        } else {
            key=$(this).attr("data-key");
            date_enable (key, 'disable');
        }
    });

    // catch any new 'from' date as minDate for 'to' date
    $("#date_from_"+key).change(function(){
        from_date = $("#date_from_"+key).val();
        $("#date_to_"+key).datepicker( "option", "minDate", from_date );
    });

    // catch the submit (just once)
    $("form").unbind('submit');
    $("form").submit(function(e) {
        // format a composite daterange filter value so that it can be parsed and post-processed in the view
        if (key !== undefined) {
            if ($("#date_from_"+key).length) {
                var filter=key+"__gte!"+key+"__lt:"+$("#date_from_"+key).val()+"!"+$("#date_to_"+key).val()+"_daterange";
                $("#last_date_from_"+key).val($("#date_from_"+key).val());
                $("#last_date_to_"+key).val($("#date_to_"+key).val());
                $("#filter_value_"+key).val(filter);
            }
        }
        return true;
    });

};
