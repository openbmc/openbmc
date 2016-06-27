#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2014        Intel Corporation
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


import os
import sys
import re
from django.db import transaction
from django.db.models import Q
from bldcontrol.models import BuildEnvironment, BRLayer, BRVariable, BRTarget, BRBitbake

# load Bitbake components
path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
sys.path.insert(0, path)
import bb.server.xmlrpc

class BitbakeController(object):
    """ This is the basic class that controlls a bitbake server.
        It is outside the scope of this class on how the server is started and aquired
    """

    def __init__(self, be):
        self.connection = bb.server.xmlrpc._create_server(be.bbaddress,
                                                          int(be.bbport))[0]

    def _runCommand(self, command):
        result, error = self.connection.runCommand(command)
        if error:
            raise Exception(error)
        return result

    def disconnect(self):
        return self.connection.removeClient()

    def setVariable(self, name, value):
        return self._runCommand(["setVariable", name, value])

    def getVariable(self, name):
        return self._runCommand(["getVariable", name])

    def triggerEvent(self, event):
        return self._runCommand(["triggerEvent", event])

    def build(self, targets, task = None):
        if task is None:
            task = "build"
        return self._runCommand(["buildTargets", targets, task])

    def forceShutDown(self):
        return self._runCommand(["stateForceShutdown"])



def getBuildEnvironmentController(**kwargs):
    """ Gets you a BuildEnvironmentController that encapsulates a build environment,
        based on the query dictionary sent in.

        This is used to retrieve, for example, the currently running BE from inside
        the toaster UI, or find a new BE to start a new build in it.

        The return object MUST always be a BuildEnvironmentController.
    """

    from localhostbecontroller import LocalhostBEController

    be = BuildEnvironment.objects.filter(Q(**kwargs))[0]
    if be.betype == BuildEnvironment.TYPE_LOCAL:
        return LocalhostBEController(be)
    else:
        raise Exception("FIXME: Implement BEC for type %s" % str(be.betype))


class BuildEnvironmentController(object):
    """ BuildEnvironmentController (BEC) is the abstract class that defines the operations that MUST
        or SHOULD be supported by a Build Environment. It is used to establish the framework, and must
        not be instantiated directly by the user.

        Use the "getBuildEnvironmentController()" function to get a working BEC for your remote.

        How the BuildEnvironments are discovered is outside the scope of this class.

        You must derive this class to teach Toaster how to operate in your own infrastructure.
        We provide some specific BuildEnvironmentController classes that can be used either to
        directly set-up Toaster infrastructure, or as a model for your own infrastructure set:

            * Localhost controller will run the Toaster BE on the same account as the web server
        (current user if you are using the the Django development web server)
        on the local machine, with the "build/" directory under the "poky/" source checkout directory.
        Bash is expected to be available.

    """
    def __init__(self, be):
        """ Takes a BuildEnvironment object as parameter that points to the settings of the BE.
        """
        self.be = be
        self.connection = None

    def setLayers(self, bitbake, ls):
        """ Checks-out bitbake executor and layers from git repositories.
            Sets the layer variables in the config file, after validating local layer paths.
            bitbake must be a single BRBitbake instance
            The layer paths must be in a list of BRLayer object

            a word of attention: by convention, the first layer for any build will be poky!
        """
        raise NotImplementedError("FIXME: Must override setLayers")

    def getArtifact(self, path):
        """ This call returns an artifact identified by the 'path'. How 'path' is interpreted as
            up to the implementing BEC. The return MUST be a REST URL where a GET will actually return
            the content of the artifact, e.g. for use as a "download link" in a web UI.
        """
        raise NotImplementedError("Must return the REST URL of the artifact")

    def triggerBuild(self, bitbake, layers, variables, targets):
        raise NotImplementedError("Must override BE release")

class ShellCmdException(Exception):
    pass


class BuildSetupException(Exception):
    pass

