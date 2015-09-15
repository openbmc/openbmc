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

    def __init__(self, connection):
        self.connection = connection

    def _runCommand(self, command):
        result, error = self.connection.connection.runCommand(command)
        if error:
            raise Exception(error)
        return result

    def disconnect(self):
        return self.connection.removeClient()

    def setVariable(self, name, value):
        return self._runCommand(["setVariable", name, value])

    def build(self, targets, task = None):
        if task is None:
            task = "build"
        return self._runCommand(["buildTargets", targets, task])



def getBuildEnvironmentController(**kwargs):
    """ Gets you a BuildEnvironmentController that encapsulates a build environment,
        based on the query dictionary sent in.

        This is used to retrieve, for example, the currently running BE from inside
        the toaster UI, or find a new BE to start a new build in it.

        The return object MUST always be a BuildEnvironmentController.
    """

    from localhostbecontroller import LocalhostBEController
    from sshbecontroller    import SSHBEController

    be = BuildEnvironment.objects.filter(Q(**kwargs))[0]
    if be.betype == BuildEnvironment.TYPE_LOCAL:
        return LocalhostBEController(be)
    elif be.betype == BuildEnvironment.TYPE_SSH:
        return SSHBEController(be)
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

            * SSH controller will run the Toaster BE on a remote machine, where the current user
        can connect without raise Exception("FIXME: implement")word (set up with either ssh-agent or raise Exception("FIXME: implement")phrase-less key authentication)

    """
    def __init__(self, be):
        """ Takes a BuildEnvironment object as parameter that points to the settings of the BE.
        """
        self.be = be
        self.connection = None

    @staticmethod
    def _updateBBLayers(bblayerconf, layerlist):
        conflines = open(bblayerconf, "r").readlines()

        bblayerconffile = open(bblayerconf, "w")
        skip = 0
        for i in xrange(len(conflines)):
            if skip > 0:
                skip =- 1
                continue
            if conflines[i].startswith("# line added by toaster"):
                skip = 1
            else:
                bblayerconffile.write(conflines[i])

        bblayerconffile.write("# line added by toaster build control\nBBLAYERS = \"" + " ".join(layerlist) + "\"")
        bblayerconffile.close()


    def writeConfFile(self, variable_list = None, raw = None):
        """ Writes a configuration file in the build directory. Override with buildenv-specific implementation. """
        raise Exception("FIXME: Must override to actually write a configuration file")


    def startBBServer(self):
        """ Starts a  BB server with Toaster toasterui set up to record the builds, an no controlling UI.
            After this method executes, self.be bbaddress/bbport MUST point to a running and free server,
            and the bbstate MUST be  updated to "started".
        """
        raise Exception("FIXME: Must override in order to actually start the BB server")

    def stopBBServer(self):
        """ Stops the currently running BB server.
            The bbstate MUST be updated to "stopped".
            self.connection must be none.
        """
        raise Exception("FIXME: Must override stoBBServer")

    def setLayers(self, bbs, ls):
        """ Checks-out bitbake executor and layers from git repositories.
            Sets the layer variables in the config file, after validating local layer paths.
            The bitbakes must be a 1-length list of BRBitbake
            The layer paths must be in a list of BRLayer object

            a word of attention: by convention, the first layer for any build will be poky!
        """
        raise Exception("FIXME: Must override setLayers")


    def getBBController(self):
        """ returns a BitbakeController to an already started server; this is the point where the server
            starts if needed; or reconnects to the server if we can
        """
        if not self.connection:
            self.startBBServer()
            self.be.lock = BuildEnvironment.LOCK_RUNNING
            self.be.save()

        server = bb.server.xmlrpc.BitBakeXMLRPCClient()
        server.initServer()
        server.saveConnectionDetails("%s:%s" % (self.be.bbaddress, self.be.bbport))
        self.connection = server.establishConnection([])

        self.be.bbtoken = self.connection.transport.connection_token
        self.be.save()

        return BitbakeController(self.connection)

    def getArtifact(self, path):
        """ This call returns an artifact identified by the 'path'. How 'path' is interpreted as
            up to the implementing BEC. The return MUST be a REST URL where a GET will actually return
            the content of the artifact, e.g. for use as a "download link" in a web UI.
        """
        raise Exception("Must return the REST URL of the artifact")

    def release(self):
        """ This stops the server and releases any resources. After this point, all resources
            are un-available for further reference
        """
        raise Exception("Must override BE release")

    def triggerBuild(self, bitbake, layers, variables, targets):
        raise Exception("Must override BE release")

class ShellCmdException(Exception):
    pass


class BuildSetupException(Exception):
    pass

