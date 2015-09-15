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


import sys
import re
from django.db import transaction
from django.db.models import Q
from bldcontrol.models import BuildEnvironment, BRLayer, BRVariable, BRTarget, BRBitbake
import subprocess

from toastermain import settings

from bbcontroller import BuildEnvironmentController, ShellCmdException, BuildSetupException

class NotImplementedException(Exception):
    pass

def DN(path):
    return "/".join(path.split("/")[0:-1])

class SSHBEController(BuildEnvironmentController):
    """ Implementation of the BuildEnvironmentController for the localhost;
        this controller manages the default build directory,
        the server setup and system start and stop for the localhost-type build environment

    """

    def __init__(self, be):
        super(SSHBEController, self).__init__(be)
        self.dburl = settings.getDATABASE_URL()
        self.pokydirname = None
        self.islayerset = False

    def _shellcmd(self, command, cwd = None):
        if cwd is None:
            cwd = self.be.sourcedir

        p = subprocess.Popen("ssh %s 'cd %s && %s'" % (self.be.address, cwd, command), stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        (out,err) = p.communicate()
        if p.returncode:
            if len(err) == 0:
                err = "command: %s \n%s" % (command, out)
            else:
                err = "command: %s \n%s" % (command, err)
            raise ShellCmdException(err)
        else:
            return out.strip()

    def _pathexists(self, path):
        try:
            self._shellcmd("test -e \"%s\"" % path)
            return True
        except ShellCmdException as e:
            return False

    def _pathcreate(self, path):
        self._shellcmd("mkdir -p \"%s\"" % path)

    def _setupBE(self):
        assert self.pokydirname and self._pathexists(self.pokydirname)
        self._pathcreate(self.be.builddir)
        self._shellcmd("bash -c \"source %s/oe-init-build-env %s\"" % (self.pokydirname, self.be.builddir))

    def startBBServer(self, brbe):
        assert self.pokydirname and self._pathexists(self.pokydirname)
        assert self.islayerset
        cmd = self._shellcmd("bash -c \"source %s/oe-init-build-env %s && DATABASE_URL=%s source toaster start noweb brbe=%s\"" % (self.pokydirname, self.be.builddir, self.dburl, brbe))

        port = "-1"
        for i in cmd.split("\n"):
            if i.startswith("Bitbake server address"):
                port = i.split(" ")[-1]
                print "Found bitbake server port ", port


        assert self.be.sourcedir and self._pathexists(self.be.builddir)
        self.be.bbaddress = self.be.address.split("@")[-1]
        self.be.bbport = port
        self.be.bbstate = BuildEnvironment.SERVER_STARTED
        self.be.save()

    def stopBBServer(self):
        assert self.pokydirname and self._pathexists(self.pokydirname)
        assert self.islayerset
        print self._shellcmd("bash -c \"source %s/oe-init-build-env %s && %s source toaster stop\"" %
            (self.pokydirname, self.be.builddir, (lambda: "" if self.be.bbtoken is None else "BBTOKEN=%s" % self.be.bbtoken)()))
        self.be.bbstate = BuildEnvironment.SERVER_STOPPED
        self.be.save()
        print "Stopped server"


    def _copyFile(self, filepath1, filepath2):
        p = subprocess.Popen("scp '%s' '%s'" % (filepath1, filepath2), stdout=subprocess.PIPE, stderr = subprocess.PIPE, shell=True)
        (out, err) = p.communicate()
        if p.returncode:
            raise ShellCmdException(err)

    def pullFile(self, local_filename, remote_filename):
        _copyFile(local_filename, "%s:%s" % (self.be.address, remote_filename))

    def pushFile(self, local_filename, remote_filename):
        _copyFile("%s:%s" % (self.be.address, remote_filename), local_filename)

    def setLayers(self, bitbakes, layers):
        """ a word of attention: by convention, the first layer for any build will be poky! """

        assert self.be.sourcedir is not None
        assert len(bitbakes) == 1
        # set layers in the layersource


        raise NotImplementedException("Not implemented: SSH setLayers")
        # 3. configure the build environment, so we have a conf/bblayers.conf
        assert self.pokydirname is not None
        self._setupBE()

        # 4. update the bblayers.conf
        bblayerconf = os.path.join(self.be.builddir, "conf/bblayers.conf")
        if not self._pathexists(bblayerconf):
            raise BuildSetupException("BE is not consistent: bblayers.conf file missing at %s" % bblayerconf)

        import uuid
        local_bblayerconf = "/tmp/" + uuid.uuid4() + "-bblayer.conf"

        self.pullFile(bblayerconf, local_bblayerconf)

        BuildEnvironmentController._updateBBLayers(local_bblayerconf, layerlist)
        self.pushFile(local_bblayerconf, bblayerconf)

        os.unlink(local_bblayerconf)

        self.islayerset = True
        return True

    def release(self):
        assert self.be.sourcedir and self._pathexists(self.be.builddir)
        import shutil
        shutil.rmtree(os.path.join(self.be.sourcedir, "build"))
        assert not self._pathexists(self.be.builddir)

    def triggerBuild(self, bitbake, layers, variables, targets):
        # set up the buid environment with the needed layers
        self.setLayers(bitbake, layers)
        self.writeConfFile("conf/toaster-pre.conf", )
        self.writeConfFile("conf/toaster.conf", raw = "INHERIT+=\"toaster buildhistory\"")

        # get the bb server running with the build req id and build env id
        bbctrl = self.getBBController()

        # trigger the build command
        task = reduce(lambda x, y: x if len(y)== 0 else y, map(lambda y: y.task, targets))
        if len(task) == 0:
            task = None

        bbctrl.build(list(map(lambda x:x.target, targets)), task)

        logger.debug("localhostbecontroller: Build launched, exiting. Follow build logs at %s/toaster_ui.log" % self.be.builddir)

        # disconnect from the server
        bbctrl.disconnect()
