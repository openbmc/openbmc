#
# BitBake Toaster Implementation
#
# Copyright (C) 2016        Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.


# Temporary home for the UI's misc API

from orm.models import Project, ProjectTarget, Build
from bldcontrol.models import BuildRequest
from bldcontrol import bbcontroller
from django.http import HttpResponse, JsonResponse
from django.views.generic import View


class XhrBuildRequest(View):

    def get(self, request, *args, **kwargs):
        return HttpResponse()

    def post(self, request, *args, **kwargs):
        """
          Build control

          Entry point: /xhr_buildrequest/<project_id>
          Method: POST

          Args:
              id: id of build to change
              buildCancel = build_request_id ...
              buildDelete = id ...
              targets = recipe_name ...

          Returns:
              {"error": "ok"}
            or
              {"error": <error message>}
        """

        project = Project.objects.get(pk=kwargs['pid'])

        if 'buildCancel' in request.POST:
            for i in request.POST['buildCancel'].strip().split(" "):
                try:
                    br = BuildRequest.objects.get(project=project, pk=i)

                    try:
                        bbctrl = bbcontroller.BitbakeController(br.environment)
                        bbctrl.forceShutDown()
                    except:
                        # We catch a bunch of exceptions here because
                        # this is where the server has not had time to start up
                        # and the build request or build is in transit between
                        # processes.
                        # We can safely just set the build as cancelled
                        # already as it never got started
                        build = br.build
                        build.outcome = Build.CANCELLED
                        build.save()

                    # We now hand over to the buildinfohelper to update the
                    # build state once we've finished cancelling
                    br.state = BuildRequest.REQ_CANCELLING
                    br.save()

                except BuildRequest.DoesNotExist:
                    return JsonResponse({'error':'No such build id %s' % i})

            return JsonResponse({'error': 'ok'})

        if 'buildDelete' in request.POST:
            for i in request.POST['buildDelete'].strip().split(" "):
                try:
                    BuildRequest.objects.select_for_update().get(project = project, pk = i, state__lte = BuildRequest.REQ_DELETED).delete()
                except BuildRequest.DoesNotExist:
                    pass
            return JsonResponse({'error': 'ok' })

        if 'targets' in request.POST:
            ProjectTarget.objects.filter(project = project).delete()
            s = str(request.POST['targets'])
            for t in s.translate(None, ";%|\"").split(" "):
                if ":" in t:
                    target, task = t.split(":")
                else:
                    target = t
                    task = ""
                ProjectTarget.objects.create(project = project,
                                             target = target,
                                             task = task)
            project.schedule_build()

            return JsonResponse({'error': 'ok' })

        response = HttpResponse()
        response.status_code = 500
        return response
