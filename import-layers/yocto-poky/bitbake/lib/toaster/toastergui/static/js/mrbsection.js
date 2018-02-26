
function mrbSectionInit(ctx){
  $('#latest-builds').on('click', '.cancel-build-btn', function(e){
    e.stopImmediatePropagation();
    e.preventDefault();

    var url = $(this).data('request-url');
    var buildReqIds = $(this).data('buildrequest-id');

    libtoaster.cancelABuild(url, buildReqIds, function () {
      window.location.reload();
    }, null);
  });

  $('#latest-builds').on('click', '.rebuild-btn', function(e){
    e.stopImmediatePropagation();
    e.preventDefault();

    var url = $(this).data('request-url');
    var target = $(this).data('target');

    libtoaster.startABuild(url, target, function(){
      window.location.reload();
    }, null);
  });

  // cached version of buildData, so we can determine whether a build has
  // changed since it was last fetched, and update the DOM appropriately
  var buildData = {};

  // returns the cached version of this build, or {} is there isn't a cached one
  function getCached(build) {
    return buildData[build.id] || {};
  }

  // returns true if a build's state changed to "Succeeded", "Failed"
  // or "Cancelled" from some other value
  function buildFinished(build) {
    var cached = getCached(build);
    return cached.state &&
      cached.state !== build.state &&
      (build.state == 'Succeeded' || build.state == 'Failed' ||
       build.state == 'Cancelled');
  }

  // returns true if the state changed
  function stateChanged(build) {
    var cached = getCached(build);
    return (cached.state !== build.state);
  }

  // returns true if the tasks_complete_percentage changed
  function tasksProgressChanged(build) {
    var cached = getCached(build);
    return (cached.tasks_complete_percentage !== build.tasks_complete_percentage);
  }

  // returns true if the number of recipes parsed/to parse changed
  function recipeProgressChanged(build) {
    var cached = getCached(build);
    return (cached.recipes_parsed_percentage !== build.recipes_parsed_percentage);
  }

  // returns true if the number of repos cloned/to clone changed
  function cloneProgressChanged(build) {
    var cached = getCached(build);
    return (cached.repos_cloned_percentage !== build.repos_cloned_percentage);
  }

  function refreshMostRecentBuilds(){
    libtoaster.getMostRecentBuilds(
      libtoaster.ctx.mostRecentBuildsUrl,

      // success callback
      function (data) {
        var build;
        var tmpl;
        var container;
        var selector;
        var colourClass;
        var elements;

        for (var i = 0; i < data.length; i++) {
          build = data[i];

          if (buildFinished(build)) {
            // a build finished: reload the whole page so that the build
            // shows up in the builds table
            window.location.reload();
          }
          else if (stateChanged(build)) {
            // update the whole template
            build.warnings_pluralise = (build.warnings !== 1 ? 's' : '');
            build.errors_pluralise = (build.errors !== 1 ? 's' : '');

            tmpl = $.templates("#build-template");

            html = $(tmpl.render(build));

            selector = '[data-latest-build-result="' + build.id + '"] ' +
              '[data-role="build-status-container"]';
            container = $(selector);

            // initialize bootstrap tooltips in the new HTML
            html.find('span.glyphicon-question-sign').tooltip();

            container.html(html);
          }
          else if (cloneProgressChanged(build)) {
            // update the clone progress text
            selector = '#repos-cloned-percentage-' + build.id;
            $(selector).html(build.repos_cloned_percentage);

            // update the recipe progress bar
            selector = '#repos-cloned-percentage-bar-' + build.id;
            $(selector).width(build.repos_cloned_percentage + '%');
          }
          else if (tasksProgressChanged(build)) {
            // update the task progress text
            selector = '#build-pc-done-' + build.id;
            $(selector).html(build.tasks_complete_percentage);

            // update the task progress bar
            selector = '#build-pc-done-bar-' + build.id;
            $(selector).width(build.tasks_complete_percentage + '%');
          }
          else if (recipeProgressChanged(build)) {
            // update the recipe progress text
            selector = '#recipes-parsed-percentage-' + build.id;
            $(selector).html(build.recipes_parsed_percentage);

            // update the recipe progress bar
            selector = '#recipes-parsed-percentage-bar-' + build.id;
            $(selector).width(build.recipes_parsed_percentage + '%');
          }

          buildData[build.id] = build;
        }
      },

      // fail callback
      function (data) {
        console.error(data);
      }
    );
  }

  window.setInterval(refreshMostRecentBuilds, 1500);
  refreshMostRecentBuilds();
}
