
function mrbSectionInit(ctx){

  var projectBuilds;

  if (ctx.mrbType === 'project')
    projectBuilds = true;

  $(".cancel-build-btn").click(function(e){
    e.preventDefault();

    var url = $(this).data('request-url');
    var buildReqIds = $(this).data('buildrequest-id');
    var banner = $(this).parents(".alert");

    banner.find(".progress-info").fadeOut().promise().done(function(){
      $("#cancelling-msg-" + buildReqIds).show();
      console.log("cancel build");
      libtoaster.cancelABuild(url, buildReqIds, function(){
        if (projectBuilds == false){
          /* the all builds page is not 'self updating' like thei
           * project Builds
           */
          window.location.reload();
        }
      }, null);
    });
  });

  $(".run-again-btn").click(function(e){
    e.preventDefault();

    var url = $(this).data('request-url');
    var target = $(this).data('target');

    libtoaster.startABuild(url, target, function(){
      window.location.reload();
    }, null);
  });


  var progressTimer;

  if (projectBuilds === true){
    progressTimer = window.setInterval(function() {
      libtoaster.getProjectInfo(libtoaster.ctx.projectPageUrl,
        function(prjInfo){
          /* These two are needed because a build can be 100% and still
           * in progress due to the fact that the % done is updated at the
           * start of a task so it can be doing the last task at 100%
           */
          var inProgress = 0;
          var allPercentDone = 0;
          if (prjInfo.builds.length === 0)
            return

          for (var i in prjInfo.builds){
            var build = prjInfo.builds[i];

            if (build.outcome === "In Progress" ||
               $(".progress .bar").length > 0){
              /* Update the build progress */
              var percentDone;

              if (build.outcome !== "In Progress"){
                /* We have to ignore the value when it's Succeeded because it
                *   goes back to 0
                */
                percentDone = 100;
              } else {
                percentDone = build.percentDone;
                inProgress++;
              }

              $("#build-pc-done-" + build.id).text(percentDone);
              $("#build-pc-done-title-" + build.id).attr("title", percentDone);
              $("#build-pc-done-bar-" + build.id).css("width",
                String(percentDone) + "%");

              allPercentDone += percentDone;
            }
          }

          if (allPercentDone === (100 * prjInfo.builds.length) && !inProgress)
            window.location.reload();

          /* Our progress bar is not still showing so shutdown the polling. */
          if ($(".progress .bar").length === 0)
            window.clearInterval(progressTimer);

      });
    }, 1500);
  }
}

