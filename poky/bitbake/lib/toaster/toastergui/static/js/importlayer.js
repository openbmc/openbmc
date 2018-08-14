"use strict"

function importLayerPageInit (ctx) {

  var layerDepBtn = $("#add-layer-dependency-btn");
  var importAndAddBtn = $("#import-and-add-btn");
  var layerNameInput = $("#import-layer-name");
  var vcsURLInput = $("#layer-git-repo-url");
  var gitRefInput = $("#layer-git-ref");
  var layerDepInput = $("#layer-dependency");
  var layerNameCtrl = $("#layer-name-ctrl");
  var duplicatedLayerName = $("#duplicated-layer-name-hint");
  var localDirPath = $("#local-dir-path");

  var layerDeps = {};
  var layerDepsDeps = {};
  var currentLayerDepSelection;
  var validLayerName = /^(\w|-)+$/;

  libtoaster.makeTypeahead(layerDepInput,
                           libtoaster.ctx.layersTypeAheadUrl,
                           { include_added: "true" }, function(item){
    currentLayerDepSelection = item;
    layerDepBtn.removeAttr("disabled");
  });

  layerDepInput.on("typeahead:select", function(event, data){
    currentLayerDepSelection = data;
  });

  // Disable local dir repo when page is loaded.
  $('#local-dir').hide();

  // disable the "Add layer" button when the layer input typeahead is empty
  // or not in the typeahead choices
  layerDepInput.on("input change", function(){
    layerDepBtn.attr("disabled","disabled");
  });

  /* We automatically add "openembedded-core" layer for convenience as a
   * dependency as pretty much all layers depend on this one
   */
  $.getJSON(libtoaster.ctx.layersTypeAheadUrl,
    { include_added: "true" , search: "openembedded-core" },
    function(layer) {
    if (layer.results.length > 0) {
      currentLayerDepSelection = layer.results[0];
        layerDepBtn.click();
    }
  });

  layerDepBtn.click(function(){
    if (currentLayerDepSelection == undefined)
      return;

    layerDeps[currentLayerDepSelection.id] = currentLayerDepSelection;

    /* Make a list item for the new layer dependency */
    var newLayerDep = $("<li><a></a><span class=\"glyphicon glyphicon-trash\" data-toggle=\"tooltip\" title=\"Remove\"></span></li>");

    newLayerDep.data('layer-id', currentLayerDepSelection.id);
    newLayerDep.children("span").tooltip();

    var link = newLayerDep.children("a");
    link.attr("href", currentLayerDepSelection.layerdetailurl);
    link.text(currentLayerDepSelection.name);
    link.tooltip({title: currentLayerDepSelection.tooltip, placement: "right"});

    var trashItem = newLayerDep.children("span");
    trashItem.click(function () {
      var toRemove = $(this).parent().data('layer-id');
      delete layerDeps[toRemove];
      $(this).parent().fadeOut(function (){
        $(this).remove();
      });
    });

    $("#layer-deps-list").append(newLayerDep);

    libtoaster.getLayerDepsForProject(currentLayerDepSelection.layerdetailurl,
                                      function (data){
        /* These are the dependencies of the layer added as a dependency */
        if (data.list.length > 0) {
          currentLayerDepSelection.url = currentLayerDepSelection.layerdetailurl;
          layerDeps[currentLayerDepSelection.id].deps = data.list;
        }

        /* Clear the current selection */
        layerDepInput.val("");
        currentLayerDepSelection = undefined;
        layerDepBtn.attr("disabled","disabled");
      }, null);
  });

  importAndAddBtn.click(function(e){
    e.preventDefault();
    /* This is a list of the names from layerDeps for the layer deps
     * modal dialog body
     */
    var depNames = [];

    /* arrray of all layer dep ids includes parent and child deps */
    var allDeps = [];

    /* temporary object to use to do a reduce on the dependencies for each
     * layer dependency added
     */
    var depDeps = {};

    /* the layers that have dependencies have an extra property "deps"
     * look in this for each layer and reduce this to a unquie object
     * of deps.
     */
    for (var key in layerDeps){
      if (layerDeps[key].hasOwnProperty('deps')){
        for (var dep in layerDeps[key].deps){
          var layer = layerDeps[key].deps[dep];
          depDeps[layer.id] = layer;
        }
      }
      depNames.push(layerDeps[key].name);
      allDeps.push(layerDeps[key].id);
    }

    /* we actually want it as an array so convert it now */
    var depDepsArray = [];
    for (var key in depDeps)
      depDepsArray.push (depDeps[key]);

    if (depDepsArray.length > 0) {
      var layer = { name: layerNameInput.val(), url: "#", id: -1 };
      var title = "Layer";
      var body = "<strong>"+layer.name+"</strong>'s dependencies ("+
        depNames.join(", ")+"</span>) require some layers that are not added to your project. Select the ones you want to add:</p>";

      showLayerDepsModal(layer,
                         depDepsArray,
                         title, body, false, function(layerObsList){
        /* Add the accepted layer dependencies' ids to the allDeps array */
        for (var key in layerObsList){
          allDeps.push(layerObsList[key].id);
        }
        import_and_add ();
      });
    } else {
      import_and_add ();
    }

    function import_and_add () {
      /* convert to a csv of all the deps to be added */
      var layerDepsCsv = allDeps.join(",");

      var layerData = {
        name: layerNameInput.val(),
        vcs_url: vcsURLInput.val(),
        git_ref: gitRefInput.val(),
        dir_path: $("#layer-subdir").val(),
        project_id: libtoaster.ctx.projectId,
        layer_deps: layerDepsCsv,
        local_source_dir: $('#local-dir-path').val(),
        add_to_project: true,
      };

      if ($('input[name=repo]:checked').val() == "git") {
        layerData.local_source_dir = "";
      } else {
        layerData.vcs_url = "";
        layerData.git_ref = "";
      }

      $.ajax({
          type: "PUT",
          url: ctx.xhrLayerUrl,
          data: JSON.stringify(layerData),
          headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
          success: function (data) {
            if (data.error != "ok") {
              console.log(data.error);
              /* let the user know why nothing happened */
              alert(data.error)
            } else {
              createImportedNotification(data);
              window.location.replace(libtoaster.ctx.projectPageUrl);
            }
          },
          error: function (data) {
            console.log("Call failed");
            console.log(data);
          }
      });
    }
  });

  /* Layer imported notification */
  function createImportedNotification(imported){
    var message = "Layer imported";

    if (imported.deps_added.length === 0) {
      message = "You have imported <strong><a class=\"alert-link\" href=\""+imported.imported_layer.layerdetailurl+"\">"+imported.imported_layer.name+"</a></strong> and added it to your project.";
    } else {

      var links = "<a href=\""+imported.imported_layer.layerdetailurl+"\">"+imported.imported_layer.name+"</a>, ";

      imported.deps_added.map (function(item, index){
        links +='<a href="'+item.layerdetailurl+'">'+item.name+'</a>';
        /*If we're at the last element we don't want the trailing comma */
        if (imported.deps_added[index+1] !== undefined)
          links += ', ';
      });

      /* Length + 1 here to do deps + the imported layer */
      message = 'You have imported <strong><a href="'+imported.imported_layer.layerdetailurl+'">'+imported.imported_layer.name+'</a></strong> and added <strong>'+(imported.deps_added.length+1)+'</strong> layers to your project: <strong>'+links+'</strong>';
    }

    libtoaster.setNotification("layer-imported", message);
  }

  function enable_import_btn(enabled) {
    var importAndAddHint = $("#import-and-add-hint");

    if (enabled) {
      importAndAddBtn.removeAttr("disabled");
      importAndAddHint.hide();
      return;
    }

    importAndAddBtn.attr("disabled", "disabled");
    importAndAddHint.show();
  }

  function check_form() {
    var valid = false;
    var inputs = $("input:required");
    var inputStr = inputs.val().split("");

    for (var i=0; i<inputs.val().length; i++){
      if (!(valid = inputStr[i])){
        enable_import_btn(false);
        break;
      }
    }

    if (valid) {
      if ($("#local-dir-radio").prop("checked") &&
          localDirPath.val().length > 0) {
        enable_import_btn(true);
      }

      if ($("#git-repo-radio").prop("checked")) {
        if (gitRefInput.val().length > 0 &&
            gitRefInput.val() == 'HEAD') {
          $('#invalid-layer-revision-hint').show();
          $('#layer-revision-ctrl').addClass('has-error');
          enable_import_btn(false);
        } else if (vcsURLInput.val().length > 0 &&
                   gitRefInput.val().length > 0) {
          $('#invalid-layer-revision-hint').hide();
          $('#layer-revision-ctrl').removeClass('has-error');
          enable_import_btn(true);
        }
      }
    }

    if (inputs.val().length == 0)
      enable_import_btn(false);
  }

  function layerExistsError(layer){
    var dupLayerInfo = $("#duplicate-layer-info");

    if (layer.local_source_dir) {
      $("#git-layer-dup").hide();
      $("#local-layer-dup").fadeIn();
      dupLayerInfo.find(".dup-layer-name").text(layer.name);
      dupLayerInfo.find(".dup-layer-link").attr("href", layer.layerdetailurl);
      dupLayerInfo.find("#dup-local-source-dir-name").text(layer.local_source_dir);
    } else {
      $("#git-layer-dup").fadeIn();
      $("#local-layer-dup").hide();
      dupLayerInfo.find(".dup-layer-name").text(layer.name);
      dupLayerInfo.find(".dup-layer-link").attr("href", layer.layerdetailurl);
      dupLayerInfo.find("#dup-layer-vcs-url").text(layer.vcs_url);
      dupLayerInfo.find("#dup-layer-revision").text(layer.vcs_reference);
    }
    $(".fields-apart-from-layer-name").fadeOut(function(){

      dupLayerInfo.fadeIn();
    });
  }

  layerNameInput.on('blur', function() {
    if (!$(this).val()){
      return;
    }
    var name = $(this).val();

    /* Check if the layer name exists */
    $.getJSON(libtoaster.ctx.layersTypeAheadUrl,
        { include_added: "true" , search: name, format: "json" },
        function(layer) {
          if (layer.results.length > 0) {
            for (var i in layer.results){
              if (layer.results[i].name == name) {
                layerExistsError(layer.results[i]);
              }
            }
          }
      });
  });

  vcsURLInput.on('input', function() {
    check_form();
  });

  gitRefInput.on('input', function() {
    check_form();
  });

  layerNameInput.on('input', function() {
    if ($(this).val() && !validLayerName.test($(this).val())){
      layerNameCtrl.addClass("has-error")
      $("#invalid-layer-name-hint").show();
      enable_import_btn(false);
      return;
    }

    if ($("#duplicate-layer-info").css("display") != "None"){
      $("#duplicate-layer-info").fadeOut(function(){
      $(".fields-apart-from-layer-name").show();
      radioDisplay();
    });

  }

    radioDisplay();

    /* Don't remove the error class if we're displaying the error for another
     * reason.
     */
    if (!duplicatedLayerName.is(":visible"))
      layerNameCtrl.removeClass("has-error")

    $("#invalid-layer-name-hint").hide();
    check_form();
  });

  /* Setup 'blank' typeahead */
  libtoaster.makeTypeahead(gitRefInput,
                           ctx.xhrGitRevTypeAheadUrl,
                           { git_url: null }, function(){});


  vcsURLInput.focusout(function (){
    if (!$(this).val())
      return;

    /* If we a layer name specified don't overwrite it or if there isn't a
     * url typed in yet return
     */
    if (!layerNameInput.val() && $(this).val().search("/")){
      var urlPts = $(this).val().split("/");
      /* Add a suggestion of the layer name */
      var suggestion = urlPts[urlPts.length-1].replace(".git","");
      layerNameInput.val(suggestion);
    }

    /* Now actually setup the typeahead properly with the git url entered */
    gitRefInput._typeahead('destroy');

    libtoaster.makeTypeahead(gitRefInput,
                             ctx.xhrGitRevTypeAheadUrl,
                             { git_url: $(this).val() },
                             function(selected){
                               gitRefInput._typeahead("close");
                             });

  });

  function radioDisplay() {
    if ($('input[name=repo]:checked').val() == "local") {
      $('#git-repo').hide();
      $('#import-git-layer-and-add-hint').hide();
      $('#local-dir').fadeIn();
      $('#import-local-dir-and-add-hint').fadeIn();
    } else {
      $('#local-dir').hide();
      $('#import-local-dir-and-add-hint').hide();
      $('#git-repo').fadeIn();
      $('#import-git-layer-and-add-hint').fadeIn();
    }
  }

  $('input:radio[name="repo"]').change(function() {
    radioDisplay();
    if ($("#local-dir-radio").prop("checked")) {
      if (localDirPath.val().length > 0) {
        enable_import_btn(true);
      } else {
        enable_import_btn(false);
      }
    }
    if ($("#git-repo-radio").prop("checked")) {
      if (vcsURLInput.val().length > 0 && gitRefInput.val().length > 0) {
        enable_import_btn(true);
      } else {
        enable_import_btn(false);
      }
    }
  });

  localDirPath.on('input', function(){
    if ($(this).val().trim().length == 0) {
      $('#import-and-add-btn').attr("disabled","disabled");
      $('#local-dir').addClass('has-error');
      $('#hintError-dir-abs-path').show();
      $('#hintError-dir-path-starts-with-slash').show();
    } else {
      var input = $(this);
      var reBeginWithSlash = /^\//;
      var reCheckVariable = /^\$/;
      var re = /([ <>\\|":%\?\*]+)/;

      var invalidDir = re.test(input.val());
      var invalidSlash = reBeginWithSlash.test(input.val());
      var invalidVar = reCheckVariable.test(input.val());

      if (!invalidSlash && !invalidVar) {
        $('#local-dir').addClass('has-error');
        $('#import-and-add-btn').attr("disabled","disabled");
        $('#hintError-dir-abs-path').show();
        $('#hintError-dir-path-starts-with-slash').show();
      } else if (invalidDir) {
        $('#local-dir').addClass('has-error');
        $('#import-and-add-btn').attr("disabled","disabled");
        $('#hintError-dir-path').show();
      } else {
        $('#local-dir').removeClass('has-error');
        if (layerNameInput.val().length > 0) {
          $('#import-and-add-btn').removeAttr("disabled");
        }
        $('#hintError-dir-abs-path').hide();
        $('#hintError-dir-path-starts-with-slash').hide();
        $('#hintError-dir-path').hide();
      }
    }
  });
}
