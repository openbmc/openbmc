"use strict";

function newCustomImagePageInit(ctx){

  var newCustomImgBtn = $("#create-new-custom-image-btn");
  var imgCustomModal = $("#new-custom-image-modal");

  newCustomImgBtn.click(function(e){
    e.preventDefault();

    var name = imgCustomModal.find('input').val();
    var baseRecipeId = imgCustomModal.data('recipe');

    if (name.length > 0) {
      createCustomRecipe(name, baseRecipeId);
      imgCustomModal.modal('hide');
    } else {
      console.warn("TODO No name supplied");
    }
  });

  function createCustomRecipe(name, baseRecipeId){
    var data = {
      'name' : name,
      'project' : libtoaster.ctx.projectId,
      'base' : baseRecipeId,
    };

    $.ajax({
        type: "POST",
        url: ctx.xhrCustomRecipeUrl,
        data: data,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (ret) {
          if (ret.error !== "ok") {
            console.warn(ret.error);
          } else {
            window.location.replace(ret.url + '?notify=new');
          }
        },
        error: function (ret) {
          console.warn("Call failed");
          console.warn(ret);
        }
    });
  }


}
