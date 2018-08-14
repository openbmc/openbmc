"use strict";

function projectPageInit(ctx) {

  var layerAddInput = $("#layer-add-input");
  var layersInPrjList = $("#layers-in-project-list");
  var layerAddBtn = $("#add-layer-btn");

  var machineChangeInput = $("#machine-change-input");
  var machineChangeBtn = $("#machine-change-btn");
  var machineForm = $("#select-machine-form");
  var machineChangeFormToggle = $("#change-machine-toggle");
  var machineNameTitle = $("#project-machine-name");
  var machineChangeCancel = $("#cancel-machine-change");
  var machineInputForm = $("#machine-input-form");
  var invalidMachineNameHelp = $("#invalid-machine-name-help");

  var distroChangeInput = $("#distro-change-input");
  var distroChangeBtn = $("#distro-change-btn");
  var distroForm = $("#select-distro-form");
  var distroChangeFormToggle = $("#change-distro-toggle");
  var distroNameTitle = $("#project-distro-name");
  var distroChangeCancel = $("#cancel-distro-change");

  var freqBuildBtn =  $("#freq-build-btn");
  var freqBuildList = $("#freq-build-list");

  var releaseChangeFormToggle = $("#release-change-toggle");
  var releaseTitle = $("#project-release-title");
  var releaseForm = $("#change-release-form");
  var releaseModal = $("#change-release-modal");
  var cancelReleaseChange = $("#cancel-release-change");

  var currentLayerAddSelection;
  var currentMachineAddSelection = "";
  var currentDistroAddSelection = "";

  var urlParams = libtoaster.parseUrlParams();

  libtoaster.getProjectInfo(libtoaster.ctx.xhrProjectUrl, function(prjInfo){
    updateProjectLayers(prjInfo.layers);
    updateFreqBuildRecipes(prjInfo.freqtargets);
    updateProjectRelease(prjInfo.release);

    /* If we're receiving a machine set from the url and it's different from
     * our current machine then activate set machine sequence.
     */
    if (urlParams.hasOwnProperty('setMachine') &&
        urlParams.setMachine !== prjInfo.machine.name){
        machineChangeInput.val(urlParams.setMachine);
        machineChangeBtn.click();
    } else {
      updateMachineName(prjInfo.machine.name);
    }

    /* If we're receiving a distro set from the url and it's different from
     * our current distro then activate set machine sequence.
     */
    if (urlParams.hasOwnProperty('setDistro') &&
        urlParams.setDistro !== prjInfo.distro.name){
        distroChangeInput.val(urlParams.setDistro);
        distroChangeBtn.click();
    } else {
      updateDistroName(prjInfo.distro.name);
    }

   /* Now we're really ready show the page */
    $("#project-page").show();

    /* Set the project name in the delete modal */
    $("#delete-project-modal .project-name").text(prjInfo.name);
  });

  if (urlParams.hasOwnProperty('notify') && urlParams.notify === 'new-project'){
    $("#project-created-notification").show();
  }

  /* Add/Rm layer functionality */

  libtoaster.makeTypeahead(layerAddInput, libtoaster.ctx.layersTypeAheadUrl, { include_added: "false" }, function(item){
    currentLayerAddSelection = item;
    layerAddBtn.removeAttr("disabled");
  });

  layerAddInput.keyup(function() {
    if ($(this).val().length == 0) {
      layerAddBtn.attr("disabled", "disabled")
    }
  });

  layerAddBtn.click(function(e){
    e.preventDefault();
    var layerObj = currentLayerAddSelection;

    addRmLayer(layerObj, true);
    /* Reset the text input */
    layerAddInput.val("");
    /* Disable the add layer button*/
    layerAddBtn.attr("disabled", "disabled");
  });

  function addRmLayer(layerObj, add){

    libtoaster.addRmLayer(layerObj, add, function(layerDepsList){
      if (add){
        updateProjectLayers([layerObj]);
        updateProjectLayers(layerDepsList);
      }

      /* Show the alert message */
      var message = libtoaster.makeLayerAddRmAlertMsg(layerObj, layerDepsList, add);
      libtoaster.showChangeNotification(message);
    });
  }

  function updateProjectLayers(layers){

    /* No layers to add */
    if (layers.length === 0){
      updateLayersCount();
      return;
    }

    for (var i in layers){
      var layerObj = layers[i];

      var projectLayer = $("<li><a></a><span class=\"glyphicon glyphicon-trash\" data-toggle=\"tooltip\" title=\"Remove\"></span></li>");

      projectLayer.data('layer', layerObj);
      projectLayer.children("span").tooltip();

      var link = projectLayer.children("a");

      link.attr("href", layerObj.layerdetailurl);
      link.text(layerObj.name);

      if (layerObj.local_source_dir) {
        link.tooltip({title: layerObj.local_source_dir, placement: "right"});
      } else {
        link.tooltip({title: layerObj.vcs_url + " | "+ layerObj.vcs_reference, placement: "right"});
      }

      var trashItem = projectLayer.children("span");
      trashItem.click(function (e) {
        e.preventDefault();
        var layerObjToRm = $(this).parent().data('layer');

        addRmLayer(layerObjToRm, false);

        $(this).parent().fadeOut(function (){
          $(this).remove();
          updateLayersCount();
        });
      });

      layersInPrjList.append(projectLayer);

      updateLayersCount();
    }
  }

  function updateLayersCount(){
    var count = $("#layers-in-project-list").children().length;
    var noLayerMsg = $("#no-layers-in-project");
    var buildInput = $("#build-input");


    if (count === 0) {
      noLayerMsg.fadeIn();
      $("#no-layers-in-project").fadeIn();
      buildInput.attr("disabled", "disabled");
    } else {
      noLayerMsg.hide();
      buildInput.removeAttr("disabled");
    }

    $("#project-layers-count").text(count);

    return count;
  }

  /* Frequent builds functionality */
  function updateFreqBuildRecipes(recipes) {
    var noMostBuilt = $("#no-most-built");

    if (recipes.length === 0){
      noMostBuilt.show();
      freqBuildBtn.hide();
    } else {
      noMostBuilt.hide();
      freqBuildBtn.show();
    }

    for (var i in recipes){
      var freqTargetCheck = $('<li><div class="checkbox"><label><input type="checkbox" /><span class="freq-target-name"></span></label></li>');
      freqTargetCheck.find(".freq-target-name").text(recipes[i]);
      freqTargetCheck.find("input").val(recipes[i]);
      freqTargetCheck.click(function(){
        if (freqBuildList.find(":checked").length > 0)
          freqBuildBtn.removeAttr("disabled");
        else
          freqBuildBtn.attr("disabled", "disabled");
      });

      freqBuildList.append(freqTargetCheck);
    }
  }

  freqBuildBtn.click(function(e){
    e.preventDefault();

    var toBuild = "";
    freqBuildList.find(":checked").each(function(){
      toBuild += $(this).val() + ' ';
    });

    toBuild = toBuild.trim();

    libtoaster.startABuild(null, toBuild,
      function(){
        /* Build request started */
        window.location.replace(libtoaster.ctx.projectBuildsUrl);
      },
      function(){
        /* Build request failed */
        console.warn("Build request failed to be created");
    });
  });


  /* Change machine functionality */
  machineChangeInput.keyup(function(){
    if ($(this).val().indexOf(' ') >= 0) {
        machineChangeBtn.attr("disabled", "disabled");
        invalidMachineNameHelp.show();
        machineInputForm.addClass('has-error');
    } else {
        machineChangeBtn.removeAttr("disabled");
        invalidMachineNameHelp.hide();
        machineInputForm.removeClass('has-error');
    }
  });

  machineChangeFormToggle.click(function(){
    machineChangeInput.val(machineNameTitle.text());
    machineChangeBtn.removeAttr("disabled");
    invalidMachineNameHelp.hide();
    machineInputForm.removeClass('has-error');
    machineForm.slideDown();
    machineNameTitle.hide();
    $(this).hide();
  });

  machineChangeCancel.click(function(){
    machineForm.slideUp(function(){
      machineNameTitle.show();
      machineChangeFormToggle.show();
    });
  });

  function updateMachineName(machineName){
    machineChangeInput.val(machineName);
    machineNameTitle.text(machineName);
  }

  libtoaster.makeTypeahead(machineChangeInput,
                           libtoaster.ctx.machinesTypeAheadUrl,
                           { }, function(item){
    currentMachineAddSelection = item.name;
    machineChangeBtn.removeAttr("disabled");
  });

  machineChangeBtn.click(function(e){
    e.preventDefault();
    /* We accept any value regardless of typeahead selection or not */
    if (machineChangeInput.val().length === 0)
      return;

    currentMachineAddSelection = machineChangeInput.val();

    libtoaster.editCurrentProject(
      { machineName : currentMachineAddSelection },
      function(){
        /* Success machine changed */
        updateMachineName(currentMachineAddSelection);
        machineChangeCancel.click();

        /* Show the alert message */
        var message = $('<span>You have changed the machine to: <strong><span id="notify-machine-name"></span></strong></span>');
        message.find("#notify-machine-name").text(currentMachineAddSelection);
        libtoaster.showChangeNotification(message);
    },
      function(){
        /* Failed machine changed */
        console.warn("Failed to change machine");
    });
  });


  /* Change distro functionality */

  distroChangeFormToggle.click(function(){
    distroForm.slideDown();
    distroNameTitle.hide();
    $(this).hide();
  });

  distroChangeCancel.click(function(){
    distroForm.slideUp(function(){
      distroNameTitle.show();
      distroChangeFormToggle.show();
    });
  });

  function updateDistroName(distroName){
    distroChangeInput.val(distroName);
    distroNameTitle.text(distroName);
  }

  libtoaster.makeTypeahead(distroChangeInput,
                           libtoaster.ctx.distrosTypeAheadUrl,
                           { }, function(item){
    currentDistroAddSelection = item.name;
    distroChangeBtn.removeAttr("disabled");
  });

  distroChangeBtn.click(function(e){
    e.preventDefault();
    /* We accept any value regardless of typeahead selection or not */
    if (distroChangeInput.val().length === 0)
      return;

    currentDistroAddSelection = distroChangeInput.val();

    libtoaster.editCurrentProject(
      { distroName : currentDistroAddSelection },
      function(){
        /* Success machine changed */
        updateDistroName(currentDistroAddSelection);
        distroChangeCancel.click();

        /* Show the alert message */
        var message = $('<span>You have changed the distro to: <strong><span id="notify-machine-name"></span></strong></span>');
        message.find("#notify-machine-name").text(currentDistroAddSelection);
        libtoaster.showChangeNotification(message);
    },
      function(){
        /* Failed machine changed */
        console.warn("Failed to change distro");
    });
  });


  /* Change release functionality */
  function updateProjectRelease(release){
    releaseTitle.text(release.description);
  }


  $("#delete-project-confirmed").click(function(e){
    e.preventDefault();
    libtoaster.disableAjaxLoadingTimer();
    $(this).find('[data-role="submit-state"]').hide();
    $(this).find('[data-role="loading-state"]').show();
    $(this).attr("disabled", "disabled");
    $('#delete-project-modal [data-dismiss="modal"]').hide();

    $.ajax({
        type: 'DELETE',
        url: libtoaster.ctx.xhrProjectUrl,
        headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
        success: function (data) {
          if (data.error !== "ok") {
            console.warn(data.error);
          } else {
            var msg =  $('<span>You have deleted <strong>1</strong> project: <strong id="project-deleted"></strong></span>');

            msg.find("#project-deleted").text(libtoaster.ctx.projectName);
            libtoaster.setNotification("project-deleted", msg.html());

            window.location.replace(data.gotoUrl);
          }
        },
        error: function (data) {
          console.warn(data);
        }
    });
  });

}
