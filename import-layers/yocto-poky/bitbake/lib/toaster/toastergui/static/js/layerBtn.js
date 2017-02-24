"use strict";

function layerBtnsInit() {

  /* Remove any current bindings to avoid duplicated binds */
  $(".layerbtn").unbind('click');

  $(".layerbtn").click(function (){
    var layerObj = $(this).data("layer");
    var add = ($(this).data('directive') === "add");
    var thisBtn = $(this);

    libtoaster.addRmLayer(layerObj, add, function (layerDepsList){
      libtoaster.showChangeNotification(libtoaster.makeLayerAddRmAlertMsg(layerObj, layerDepsList, add));

      /* In-cell notification */
      var notification = $('<div id="temp-inline-notify" style="display: none; font-size: 11px; line-height: 1.3;" class="tooltip-inner"></div>');
      thisBtn.parent().append(notification);

      if (add){
        if (layerDepsList.length > 0)
          notification.text(String(layerDepsList.length + 1) + " layers added");
        else
          notification.text("1 layer added");

        var layerBtnsFadeOut = $();
        var layerExistsBtnFadeIn = $();

        layerBtnsFadeOut = layerBtnsFadeOut.add(".layer-add-" + layerObj.id);
        layerExistsBtnFadeIn = layerExistsBtnFadeIn.add(".layer-exists-" + layerObj.id);

        for (var i in layerDepsList){
          layerBtnsFadeOut = layerBtnsFadeOut.add(".layer-add-" + layerDepsList[i].id);
          layerExistsBtnFadeIn = layerExistsBtnFadeIn.add(".layer-exists-" + layerDepsList[i].id);
        }

        layerBtnsFadeOut.fadeOut().promise().done(function(){
          notification.fadeIn().delay(500).fadeOut(function(){
            /* Fade in the buttons */
            layerExistsBtnFadeIn.fadeIn();
            notification.remove();
          });
        });
      } else {
        notification.text("1 layer removed");
        /* Deleting a layer we only hanlde the one button */
        thisBtn.fadeOut(function(){
          notification.fadeIn().delay(500).fadeOut(function(){
            $(".layer-add-" + layerObj.id).fadeIn();
            notification.remove();
          });
        });
      }

    });
  });

  $("td .build-recipe-btn").unbind('click');
  $("td .build-recipe-btn").click(function(e){
    e.preventDefault();
    var recipe = $(this).data('recipe-name');

    libtoaster.startABuild(null, recipe,
      function(){
        /* Success */
        window.location.replace(libtoaster.ctx.projectBuildsUrl);
      });
  });


  $(".customise-btn").unbind('click');
  $(".customise-btn").click(function(e){
    e.preventDefault();
    var imgCustomModal = $("#new-custom-image-modal");

    if (imgCustomModal.length == 0)
      throw("Modal new-custom-image not found");

    var recipe = {id: $(this).data('recipe'), name: null}
    newCustomImageModalSetRecipes([recipe]);
    imgCustomModal.modal('show');
  });
}
