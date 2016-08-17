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

  var layerDeps = {};
  var layerDepsDeps = {};
  var currentLayerDepSelection;
  var validLayerName = /^(\w|-)+$/;

  libtoaster.makeTypeahead(layerDepInput, libtoaster.ctx.layersTypeAheadUrl, { include_added: "true" }, function(item){
    currentLayerDepSelection = item;
  });

  // choices available in the typeahead
  var layerDepsChoices = {};

  // when the typeahead choices change, store an array of the available layer
  // choices locally, to use for enabling/disabling the "Add layer" button
  layerDepInput.on("typeahead-choices-change", function (event, data) {
    layerDepsChoices = {};

    if (data.choices) {
      data.choices.forEach(function (item) {
        layerDepsChoices[item.name] = item;
      });
    }
  });

  // disable the "Add layer" button when the layer input typeahead is empty
  // or not in the typeahead choices
  layerDepInput.on("input change", function () {
    // get the choices from the typeahead
    var choice = layerDepsChoices[$(this).val()];

    if (choice) {
      layerDepBtn.removeAttr("disabled");
      currentLayerDepSelection = choice;
    }
    else {
      layerDepBtn.attr("disabled", "disabled");
      currentLayerDepSelection = undefined;
    }
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
    var newLayerDep = $("<li><a></a><span class=\"icon-trash\" data-toggle=\"tooltip\" title=\"Delete\"></span></li>");

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

    libtoaster.getLayerDepsForProject(currentLayerDepSelection.layerdetailurl, function (data){
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

  importAndAddBtn.click(function(){
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

      showLayerDepsModal(layer, depDepsArray, title, body, false, function(layerObsList){
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
      };

      $.ajax({
          type: "POST",
          url: ctx.xhrImportLayerUrl,
          data: layerData,
          headers: { 'X-CSRFToken' : $.cookie('csrftoken')},
          success: function (data) {
            if (data.error != "ok") {
              console.log(data.error);
            } else {
              /* Success layer import now go to the project page */
              $.cookie('layer-imported-alert', JSON.stringify(data), { path: '/'});
              window.location.replace(libtoaster.ctx.projectPageUrl+'?notify=layer-imported');
            }
          },
          error: function (data) {
            console.log("Call failed");
            console.log(data);
          }
      });
    }
  });

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

    for (var i=0; i<inputs.length; i++){
      if (!(valid = inputs[i].value)){
        enable_import_btn(false);
        break;
      }
    }

    if (valid)
      enable_import_btn(true);
  }

  function layerExistsError(layer){
    var dupLayerInfo = $("#duplicate-layer-info");
    dupLayerInfo.find(".dup-layer-name").text(layer.name);
    dupLayerInfo.find(".dup-layer-link").attr("href", layer.layerdetailurl);
    dupLayerInfo.find("#dup-layer-vcs-url").text(layer.vcs_url);
    dupLayerInfo.find("#dup-layer-revision").text(layer.vcs_reference);

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
      layerNameCtrl.addClass("error")
      $("#invalid-layer-name-hint").show();
      enable_import_btn(false);
      return;
    }

    if ($("#duplicate-layer-info").css("display") != "None"){
      $("#duplicate-layer-info").fadeOut(function(){
        $(".fields-apart-from-layer-name").show();
      });

    }

    /* Don't remove the error class if we're displaying the error for another
     * reason.
     */
    if (!duplicatedLayerName.is(":visible"))
      layerNameCtrl.removeClass("error")

    $("#invalid-layer-name-hint").hide();
    check_form();
  });

  /* Have a guess at the layer name */
  vcsURLInput.focusout(function (){
    /* If we a layer name specified don't overwrite it or if there isn't a
     * url typed in yet return
     */
    if (layerNameInput.val() || !$(this).val())
      return;

    if ($(this).val().search("/")){
      var urlPts = $(this).val().split("/");
      var suggestion = urlPts[urlPts.length-1].replace(".git","");
      layerNameInput.val(suggestion);
    }
  });

}
