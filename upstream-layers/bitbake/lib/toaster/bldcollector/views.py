#
# BitBake Toaster Implementation
#
# Copyright (C) 2014        Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

from django.urls import reverse
from django.http import HttpResponseBadRequest, HttpResponse
import os
import tempfile
import subprocess
import toastermain
from django.views.decorators.csrf import csrf_exempt

from toastermain.logs import log_view_mixin


@csrf_exempt
@log_view_mixin
def eventfile(request):
    """ Receives a file by POST, and runs toaster-eventreply on this file """
    if request.method != "POST":
        return HttpResponseBadRequest("This API only accepts POST requests. Post a file with:\n\ncurl -F eventlog=@bitbake_eventlog.json %s\n" % request.build_absolute_uri(reverse('eventfile')), content_type="text/plain;utf8")

    # write temporary file
    (handle, abstemppath) = tempfile.mkstemp(dir="/tmp/")
    with os.fdopen(handle, "w") as tmpfile:
        for chunk in request.FILES['eventlog'].chunks():
            tmpfile.write(chunk)
    tmpfile.close()

    # compute the path to "bitbake/bin/toaster-eventreplay"
    from os.path import dirname as DN
    import_script = os.path.join(DN(DN(DN(DN(os.path.abspath(__file__))))), "bin/toaster-eventreplay")
    if not os.path.exists(import_script):
        raise Exception("script missing %s" % import_script)
    scriptenv = os.environ.copy()
    scriptenv["DATABASE_URL"] = toastermain.settings.getDATABASE_URL()

    # run the data loading process and return the results
    importer = subprocess.Popen([import_script, abstemppath], stdout=subprocess.PIPE, stderr=subprocess.PIPE, env=scriptenv)
    (out, err) = importer.communicate()
    if importer.returncode == 0:
        os.remove(abstemppath)
    return HttpResponse("== Retval %d\n== STDOUT\n%s\n\n== STDERR\n%s" % (importer.returncode, out, err), content_type="text/plain;utf8")
