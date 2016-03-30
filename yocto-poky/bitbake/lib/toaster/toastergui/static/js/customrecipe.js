"use strict";

function customRecipePageInit(ctx) {

  var urlParams = libtoaster.parseUrlParams();

  (function notificationRequest(){
    if (urlParams.hasOwnProperty('notify') && urlParams.notify === 'new'){
      $("#image-created-notification").show();
    }
  })();

  $("#recipeselection").on('table-done', function(e, total, tableParams){
    /* Table is done so now setup the click handler for the package buttons */
    $(".add-rm-package-btn").click(function(e){
      e.preventDefault();
      addRemovePackage($(this), tableParams);
    });
  });

  function addRemovePackage(pkgBtn, tableParams){
    var pkgBtnData = pkgBtn.data();
    var method;
    var buttonToShow;

    if (pkgBtnData.directive == 'add') {
      method = 'PUT';
      buttonToShow = '#package-rm-btn-' + pkgBtnData.package;
    } else if (pkgBtnData.directive == 'remove') {
      method = 'DELETE';
      buttonToShow = '#package-add-btn-' + pkgBtnData.package;
    } else {
      throw("Unknown package directive: should be add or remove");
    }

    $.ajax({
        type: method,
        url: pkgBtnData.packageUrl,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function(data){
          /* Invalidate the Add | Rm package table's current cache */
          tableParams.nocache = true;
          $.get(ctx.tableApiUrl, tableParams);
          /* Swap the buttons around */
          pkgBtn.hide();
          $(buttonToShow).show();
        }
    });
  }
}
