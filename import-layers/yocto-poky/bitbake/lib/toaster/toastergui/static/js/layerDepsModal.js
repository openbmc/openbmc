/*
 * layer: Object representing the parent layer { id: .. name: ... url }
 * dependencies: array of dependency layer objects { id: .. name: ..}
 * title: optional override for title
 * body: optional override for body
 * addToProject: Whether to add layers to project on accept
 * successAdd: function to run on success
 */
function showLayerDepsModal(layer,
                            dependencies,
                            title,
                            body,
                            addToProject,
                            successAdd) {

  if ($("#dependencies-modal").length === 0) {
    $.get(libtoaster.ctx.htmlUrl + "/layer_deps_modal.html", function(html){
      $("body").append(html);
      setupModal();
    });
  } else {
    setupModal();
  }

  function setupModal(){

    if (title) {
      $('#dependencies-modal #title').text(title);
    } else {
      $('#dependencies-modal #title').text(layer.name);
    }

    if (body) {
      $("#dependencies-modal #body-text").html(body);
    } else {
      $("#dependencies-modal #layer-name").text(layer.name);
    }

    var deplistHtml = "";
    for (var i = 0; i < dependencies.length; i++) {
      deplistHtml += "<li><div class=\"checkbox\"><label><input name=\"dependencies\" value=\"";
      deplistHtml += dependencies[i].id;
      deplistHtml +="\" type=\"checkbox\" checked=\"checked\"/>";
      deplistHtml += dependencies[i].name;
      deplistHtml += "</label></div></li>";
    }
    $('#dependencies-list').html(deplistHtml);

    $("#dependencies-modal").data("deps", dependencies);

    /* Clear any alert notifications before showing the modal */
    $(".alert").fadeOut(function(){
      $('#dependencies-modal').modal('show');
    });

    /* Discard the old submission function */
    $("#dependencies-modal-form").unbind('submit');

    $("#dependencies-modal-form").submit(function (e) {
      e.preventDefault();
      var selectedLayerIds = [];
      var selectedLayers = [];

      $("input[name='dependencies']:checked").each(function () {
        selectedLayerIds.push(parseInt($(this).val()));
      });

      /* -1 is a special dummy Id which we use when the layer isn't yet in the
       * system, normally we would add the current layer to the selection.
       */
      if (layer.id != -1)
        selectedLayerIds.push(layer.id);

      /* Find the selected layer objects from our original list */
      for (var i = 0; i < selectedLayerIds.length; i++) {
        for (var j = 0; j < dependencies.length; j++) {
          if (dependencies[j].id == selectedLayerIds[i]) {
            selectedLayers.push(dependencies[j]);
          }
        }
      }

      if (addToProject) {
        libtoaster.editCurrentProject({ 'layerAdd': selectedLayerIds.join(",") }, function () {
          if (successAdd) {
            successAdd(selectedLayers);
          }
        }, function () {
          console.warn("Adding layers to project failed");
        });
      } else {
        successAdd(selectedLayers);
      }

      $('#dependencies-modal').modal('hide');
    });
  }
}
