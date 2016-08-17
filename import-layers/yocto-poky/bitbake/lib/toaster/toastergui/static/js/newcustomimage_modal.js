"use strict";

/*
Used for the newcustomimage_modal actions

The .data('recipe') value on the outer element determines which
recipe ID is used as the basis for the new custom image recipe created via
this modal.

Use newCustomImageModalSetRecipes() to set the recipes available as a base
for the new custom image. This will manage the addition of radio buttons
to select the base image (or remove the radio buttons, if there is only a
single base image available).
*/

function newCustomImageModalInit(){

  var newCustomImgBtn = $("#create-new-custom-image-btn");
  var imgCustomModal = $("#new-custom-image-modal");
  var invalidNameHelp = $("#invalid-name-help");
  var invalidRecipeHelp = $("#invalid-recipe-help");
  var nameInput = imgCustomModal.find('input');

  var invalidNameMsg = "Image names cannot contain spaces or capital letters. The only allowed special character is dash (-).";
  var duplicateNameMsg = "A recipe with this name already exists. Image names must be unique.";
  var duplicateImageInProjectMsg = "An image with this name already exists in this project."
  var invalidBaseRecipeIdMsg = "Please select an image to customise.";

  // capture clicks on radio buttons inside the modal; when one is selected,
  // set the recipe on the modal
  imgCustomModal.on("click", "[name='select-image']", function (e) {
    clearRecipeError();

    var recipeId = $(e.target).attr('data-recipe');
    imgCustomModal.data('recipe', recipeId);
  });

  newCustomImgBtn.click(function(e){
    e.preventDefault();

    var baseRecipeId = imgCustomModal.data('recipe');

    if (!baseRecipeId) {
      showRecipeError(invalidBaseRecipeIdMsg);
      return;
    }

    if (nameInput.val().length > 0) {
      libtoaster.createCustomRecipe(nameInput.val(), baseRecipeId,
      function(ret) {
        if (ret.error !== "ok") {
          console.warn(ret.error);
          if (ret.error === "invalid-name") {
            showNameError(invalidNameMsg);
            return;
          } else if (ret.error === "recipe-already-exists") {
            showNameError(duplicateNameMsg);
            return;
          } else if (ret.error === "image-already-exists") {
            showNameError(duplicateImageInProjectMsg);
            return;
          }
        } else {
          imgCustomModal.modal('hide');
          window.location.replace(ret.url + '?notify=new');
        }
      });
    }
  });

  function showNameError(text){
    invalidNameHelp.text(text);
    invalidNameHelp.show();
    nameInput.parent().addClass('error');
  }

  function showRecipeError(text){
    invalidRecipeHelp.text(text);
    invalidRecipeHelp.show();
  }

  function clearRecipeError(){
    invalidRecipeHelp.hide();
  }

  nameInput.on('keyup', function(){
    if (nameInput.val().length === 0){
      newCustomImgBtn.prop("disabled", true);
      return
    }

    if (nameInput.val().search(/[^a-z|0-9|-]/) != -1){
      showNameError(invalidNameMsg);
      newCustomImgBtn.prop("disabled", true);
      nameInput.parent().addClass('error');
    } else {
      invalidNameHelp.hide();
      newCustomImgBtn.prop("disabled", false);
      nameInput.parent().removeClass('error');
    }
  });
}

// Set the image recipes which can used as the basis for the custom
// image recipe the user is creating
//
// baseRecipes: a list of one or more recipes which can be
// used as the base for the new custom image recipe in the format:
// [{'id': <recipe ID>, 'name': <recipe name>'}, ...]
//
// if recipes is a single recipe, just show the text box to set the
// name for the new custom image; if recipes contains multiple recipe objects,
// show a set of radio buttons so the user can decide which to use as the
// basis for the new custom image
function newCustomImageModalSetRecipes(baseRecipes) {
  var imgCustomModal = $("#new-custom-image-modal");
  var imageSelector = $('#new-custom-image-modal [data-role="image-selector"]');
  var imageSelectRadiosContainer = $('#new-custom-image-modal [data-role="image-selector-radios"]');

  // remove any existing radio buttons + labels
  imageSelector.remove('[data-role="image-radio"]');

  if (baseRecipes.length === 1) {
    // hide the radio button container
    imageSelector.hide();

    // set the single recipe ID on the modal as it's the only one
    // we can build from
    imgCustomModal.data('recipe', baseRecipes[0].id);
  }
  else {
    // add radio buttons; note that the handlers for the radio buttons
    // are set in newCustomImageModalInit via event delegation
    for (var i = 0; i < baseRecipes.length; i++) {
      var recipe = baseRecipes[i];
      imageSelectRadiosContainer.append(
        '<label class="radio" data-role="image-radio">' +
        recipe.name +
        '<input type="radio" class="form-control" name="select-image" ' +
        'data-recipe="' + recipe.id + '">' +
        '</label>'
      );
    }

    // show the radio button container
    imageSelector.show();
  }
}
