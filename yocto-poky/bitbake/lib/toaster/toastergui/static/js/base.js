'use strict';

function basePageInit(ctx) {

  var newBuildButton = $("#new-build-button");
  var newBuildTargetInput;
  var newBuildTargetBuildBtn;
  var projectNameForm = $("#project-name-change-form");
  var projectNameContainer = $("#project-name-container");
  var projectName = $("#project-name");
  var projectNameFormToggle = $("#project-change-form-toggle");
  var projectNameChangeCancel = $("#project-name-change-cancel");

  /* initially the current project is used unless overridden by the new build
   * button in top right nav
   */
  var selectedProject = libtoaster.ctx;

  var selectedTarget;

  var newBuildProjectInput = $("#new-build-button #project-name-input");
  var newBuildProjectSaveBtn = $("#new-build-button #save-project-button");

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
  });

  $("#project-name-change-btn").click(function(e){
    var newProjectName = $("#project-name-change-input").val();

    libtoaster.editCurrentProject({ projectName: newProjectName }, function (){
      projectName.html(newProjectName);
      libtoaster.ctx.projectName = newProjectName;
      projectNameChangeCancel.click();
    });
  });

  _checkProjectBuildable();

  $("#project-topbar .nav li a").each(function(){
    if (window.location.pathname === $(this).attr('href'))
      $(this).parent().addClass('active');
    else
      $(this).parent().removeClass('active');
  });

  if ($(".total-builds").length !== 0){
    libtoaster.getProjectInfo(libtoaster.ctx.projectPageUrl, function(prjInfo){
      if (prjInfo.completedbuilds)
        $(".total-builds").text(prjInfo.completedbuilds.length);
    });
  }

  /* Hide the button if we're on the project,newproject or importlyaer page
   * or if there are no projects yet defined
   * only show if there isn't already a build-target-input already
   */
  if (ctx.numProjects > 0 &&
      ctx.currentUrl.search('newproject') < 0 &&
      $(".build-target-input").length === 1) {

    newBuildTargetInput = $("#new-build-button .build-target-input");
    newBuildTargetBuildBtn = $("#new-build-button").find(".build-button");

    _setupNewBuildButton();
    newBuildButton.show();
  } else if ($(".build-target-input").length > 0) {
    newBuildTargetInput = $("#project-topbar .build-target-input");
    newBuildTargetBuildBtn = $("#project-topbar .build-button");
  } else {
    return;
  }

  /* Hide the change project icon when there is only one project */
  if (ctx.numProjects === 1) {
    $('#project .icon-pencil').hide();
  }

  /* If we have a project setup the typeahead */
  if (selectedProject.recipesTypeAheadUrl){
    libtoaster.makeTypeahead(newBuildTargetInput, selectedProject.recipesTypeAheadUrl, { format: "json" }, function (item) {
      selectedTarget = item;
      newBuildTargetBuildBtn.removeAttr("disabled");
    });
  }

  newBuildTargetInput.on('input', function () {
    if ($(this).val().length === 0) {
      newBuildTargetBuildBtn.attr("disabled", "disabled");
    } else {
      newBuildTargetBuildBtn.removeAttr("disabled");
    }
  });

  newBuildTargetBuildBtn.click(function (e) {
    e.preventDefault();

    if (!newBuildTargetInput.val()) {
      return;
    }

    /* We use the value of the input field so as to maintain any command also
     * added e.g. core-image-minimal:clean
     */
    selectedTarget = { name: newBuildTargetInput.val() };

    /* Fire off the build */
    libtoaster.startABuild(selectedProject.projectBuildsUrl,
      selectedProject.projectId, selectedTarget.name, function(){
      window.location.replace(selectedProject.projectBuildsUrl);
    }, null);
  });

  function _checkProjectBuildable() {
    if (selectedProject.projectId === undefined || selectedProject.projectIsDefault) {
      return;
    }

    libtoaster.getProjectInfo(selectedProject.projectPageUrl,
      function (data) {
        if (data.machine === null || data.machine.name === undefined || data.layers.length === 0) {
          /* we can't build anything without a machine and some layers */
          $("#new-build-button #targets-form").hide();
          $("#new-build-button .alert").show();
        } else {
          $("#new-build-button #targets-form").show();
          $("#new-build-button .alert").hide();

          /* we can build this project; enable input fields */
          newBuildTargetInput.removeAttr("disabled");
       }
      }, null);
  }

  /* Setup New build button in the top nav bar */
  function _setupNewBuildButton() {

    /* If we don't have a current project then present the set project
     * form.
     */
    if (selectedProject.projectId === undefined || selectedProject.projectIsDefault) {
      $('#change-project-form').show();
      $('#project .icon-pencil').hide();
    }

    libtoaster.makeTypeahead(newBuildProjectInput, selectedProject.projectsTypeAheadUrl, { format : "json" }, function (item) {
      /* successfully selected a project */
      newBuildProjectSaveBtn.removeAttr("disabled");
      selectedProject = item;
    });

    /* Any typing in the input apart from enter key is going to invalidate
     * the value that has been set by selecting a suggestion from the typeahead
     */
    newBuildProjectInput.on('input', function (event) {
      if (event.keyCode === 13) {
        return;
      }
      newBuildProjectSaveBtn.attr("disabled", "disabled");
    });


    newBuildProjectSaveBtn.click(function () {
      selectedProject.projectId = selectedProject.id;
      /* Update the typeahead project_id paramater */
      _checkProjectBuildable();

      newBuildTargetInput.removeAttr("disabled");

      /* We've got a new project so now we need to update the
       * target urls. We can get this from the new project's info
       */
      $.getJSON(selectedProject.projectPageUrl, { format: "json" },
        function(projectInfo){
          /* Update the typeahead to use the new selectedProject */
          selectedProject = projectInfo;

          libtoaster.makeTypeahead(newBuildTargetInput, selectedProject.recipesTypeAheadUrl, { format: "json" }, function (item) {
              /* successfully selected a target */
              selectedTarget = item;
              newBuildTargetBuildBtn.removeAttr("disabled");
          });

      });
      newBuildTargetInput.val("");

      /* set up new form aspect */
      $("#new-build-button #project a").text(selectedProject.name).attr('href', selectedProject.projectPageUrl);
      $("#new-build-button .alert a").attr('href', selectedProject.projectPageUrl);
      $("#project .icon-pencil").show();

      $("#change-project-form").slideUp({ 'complete' : function () {
          $("#new-build-button #project").show();
      }});
    });

    $('#new-build-button #project .icon-pencil').click(function () {
      newBuildProjectSaveBtn.attr("disabled", "disabled");
      newBuildProjectInput.val($("#new-build-button #project a").text());
      $("#cancel-change-project").show();
      $(this).parent().hide();
      $("#change-project-form").slideDown();
    });

    $("#new-build-button #cancel-change-project").click(function () {
      $("#change-project-form").hide(function () {
        $('#new-build-button #project').show();
      });

      newBuildProjectInput.val("");
      newBuildProjectSaveBtn.attr("disabled", "disabled");
    });

    /* Keep the dropdown open even unless we click outside the dropdown area */
    $(".new-build").click (function (event) {
      event.stopPropagation();
    });
  };

}
