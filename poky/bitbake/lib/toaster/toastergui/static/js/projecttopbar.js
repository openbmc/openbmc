'use strict';

function projectTopBarInit(ctx) {

  var projectNameForm = $("#project-name-change-form");
  var projectNameContainer = $("#project-name-container");
  var projectName = $(".project-name");
  var projectNameFormToggle = $("#project-change-form-toggle");
  var projectNameChangeCancel = $("#project-name-change-cancel");

  // this doesn't exist for command-line builds
  var newBuildTargetInput = $("#build-input");

  var newBuildTargetBuildBtn = $("#build-button");
  var selectedTarget;

  var updateProjectBtn = $("#update-project-button");
  var cancelProjectBtn = $("#cancel-project-button");

  /* Project name change functionality */
  projectNameFormToggle.click(function(e){
    e.preventDefault();
    projectNameContainer.hide();
    projectNameForm.fadeIn();
  });

  projectNameChangeCancel.click(function(e){
    e.preventDefault();
    projectNameForm.hide();
    projectNameContainer.fadeIn();
    $("#project-name-change-input").val(projectName.first().text());
  });

  $("#project-name-change-btn").click(function(){
    var newProjectName = $("#project-name-change-input").val();

    libtoaster.editCurrentProject({ projectName: newProjectName }, function (){
      projectName.text(newProjectName);
      libtoaster.ctx.projectName = newProjectName;
      projectNameChangeCancel.click();
    });
  });

  /* Nav bar activate state switcher */
  $("#project-topbar .nav li a").each(function(){
    if (window.location.pathname === $(this).attr('href'))
      $(this).parent().addClass('active');
    else
      $(this).parent().removeClass('active');
  });

  if (!newBuildTargetInput.length) {
    return;
  }

  /* the following only applies for non-command-line projects */

  /* Recipe build input functionality */
  if (ctx.numProjectLayers > 0 && ctx.machine){
    newBuildTargetInput.removeAttr("disabled");
  }

  libtoaster.makeTypeahead(newBuildTargetInput,
    libtoaster.ctx.recipesTypeAheadUrl, {}, function (item) {
     selectedTarget = item;
     newBuildTargetBuildBtn.removeAttr("disabled");
  });

  newBuildTargetInput.on('input', function () {
    if ($(this).val().length === 0) {
      newBuildTargetBuildBtn.attr("disabled", "disabled");
    } else {
      newBuildTargetBuildBtn.removeAttr("disabled");
    }
  });

  newBuildTargetBuildBtn.click(function (e) {
    e.preventDefault();
    if (!newBuildTargetInput.val().trim()) {
      return;
    }
    /* We use the value of the input field so as to maintain any command also
     * added e.g. core-image-minimal:clean and because we can build targets
     * that toaster doesn't yet know about
     */
    selectedTarget = { name: newBuildTargetInput.val().trim() };

    /* Fire off the build */
    libtoaster.startABuild(null, selectedTarget.name,
      function(){
        window.location.replace(libtoaster.ctx.projectBuildsUrl);
    }, null);
  });

  updateProjectBtn.click(function (e) {
    e.preventDefault();

    selectedTarget = { name: "_PROJECT_PREPARE_" };

    /* Save current default build image, fire off the build */
    libtoaster.updateProject(null, selectedTarget.name, newBuildTargetInput.val().trim(),
      function(){
        window.location.replace(libtoaster.ctx.projectSpecificPageUrl);
    }, null);
  });

  cancelProjectBtn.click(function (e) {
    e.preventDefault();

    /* redirect to 'done/canceled' landing page */
    window.location.replace(libtoaster.ctx.landingSpecificCancelURL);
  });

  /* Call makeProjectNameValidation function */
  libtoaster.makeProjectNameValidation($("#project-name-change-input"),
      $("#hint-error-project-name"), $("#validate-project-name"),
      $("#project-name-change-btn"));

}
