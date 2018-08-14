"use strict";

function recipeDetailsPageInit(ctx){

  $(".customise-btn").click(function(e){
    e.preventDefault();
    var imgCustomModal = $("#new-custom-image-modal");

    if (imgCustomModal.length === 0)
      throw("Modal new-custom-image not found");

    var recipe = {id: $(this).data('recipe'), name: null}
    newCustomImageModalSetRecipes([recipe]);
    imgCustomModal.modal('show');
  });

  $("#add-layer-btn").click(function(){
    var btn = $(this);

    libtoaster.addRmLayer(ctx.recipe.layer_version,
      true,
      function (layersList){
        var msg = libtoaster.makeLayerAddRmAlertMsg(ctx.recipe.layer_version,
          layersList,
          true);

        libtoaster.showChangeNotification(msg);

        var toShow = $("#customise-build-btns");

        /* If we have no packages built yet also fade in the build packages
         * hint message
         */
        if (ctx.recipe.totalPackages === 0){
          toShow = toShow.add("#build-to-get-packages-msg");
        }

        $("#packages-alert").add(btn).fadeOut(function(){
          toShow.fadeIn();
        });
    });
  });

  /* Trigger a build of your custom image */
  $(".build-recipe-btn").click(function(){
    libtoaster.startABuild(null, ctx.recipe.name,
      function(){
        window.location.replace(libtoaster.ctx.projectBuildsUrl);
    });
  });
}
