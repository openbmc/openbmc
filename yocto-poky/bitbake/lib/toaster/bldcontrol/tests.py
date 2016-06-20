"""
This file demonstrates writing tests using the unittest module. These will pass
when you run "manage.py test".

Replace this with more appropriate tests for your application.
"""

from django.test import TestCase

from bldcontrol.bbcontroller import BitbakeController, BuildSetupException
from bldcontrol.localhostbecontroller import LocalhostBEController
from bldcontrol.models import BuildEnvironment, BuildRequest
from bldcontrol.management.commands.runbuilds import Command

import socket
import subprocess
import os

# standard poky data hardcoded for testing
BITBAKE_LAYER = type('bitbake_info', (object,), { "giturl": "git://git.yoctoproject.org/poky.git", "dirpath": "", "commit": "HEAD"})
POKY_LAYERS = [
    type('poky_info', (object,), { "name": "meta", "giturl": "git://git.yoctoproject.org/poky.git", "dirpath": "meta", "commit": "HEAD"}),
    type('poky_info', (object,), { "name": "meta-yocto", "giturl": "git://git.yoctoproject.org/poky.git", "dirpath": "meta-yocto", "commit": "HEAD"}),
    type('poky_info', (object,), { "name": "meta-yocto-bsp", "giturl": "git://git.yoctoproject.org/poky.git", "dirpath": "meta-yocto-bsp", "commit": "HEAD"}),
    ]



# we have an abstract test class designed to ensure that the controllers use a single interface
# specific controller tests only need to override the _getBuildEnvironment() method

test_sourcedir = os.getenv("TTS_SOURCE_DIR")
test_builddir = os.getenv("TTS_BUILD_DIR")
test_address = os.getenv("TTS_TEST_ADDRESS", "localhost")

if test_sourcedir == None or test_builddir == None or test_address == None:
    raise Exception("Please set TTTS_SOURCE_DIR, TTS_BUILD_DIR and TTS_TEST_ADDRESS")

# The bb server will expect a toaster-pre.conf file to exist. If it doesn't exit then we make
# an empty one here.
open(test_builddir + 'conf/toaster-pre.conf', 'a').close()

class BEControllerTests(object):

    def _serverForceStop(self, bc):
        err = bc._shellcmd("netstat  -tapn 2>/dev/null | grep 8200 | awk '{print $7}' | sort -fu | cut -d \"/\" -f 1 | grep -v -- - | tee /dev/fd/2 | xargs -r kill")
        self.assertTrue(err == '', "bitbake server pid %s not stopped" % err)

    def test_serverStartAndStop(self):
        obe =  self._getBuildEnvironment()
        bc = self._getBEController(obe)
        try:
            # setting layers, skip any layer info
            bc.setLayers(BITBAKE_LAYER, POKY_LAYERS)
        except NotImplementedError:
            print "Test skipped due to command not implemented yet"
            return True
        # We are ok with the exception as we're handling the git already exists
        except BuildSetupException:
            pass

        bc.pokydirname = test_sourcedir
        bc.islayerset = True

        hostname = test_address.split("@")[-1]

        # test start server and stop
        bc.startBBServer()

        self.assertFalse(socket.socket(socket.AF_INET, socket.SOCK_STREAM).connect_ex((hostname, int(bc.be.bbport))), "Server not answering")

        self._serverForceStop(bc)

    def test_getBBController(self):
        obe = self._getBuildEnvironment()
        bc = self._getBEController(obe)
        layerSet = False
        try:
            # setting layers, skip any layer info
            layerSet = bc.setLayers(BITBAKE_LAYER, POKY_LAYERS)
        except NotImplementedError:
            print "Test skipped due to command not implemented yet"
            return True
        # We are ok with the exception as we're handling the git already exists
        except BuildSetupException:
            pass

        bc.pokydirname = test_sourcedir
        bc.islayerset = True

        bbc = bc.getBBController()
        self.assertTrue(isinstance(bbc, BitbakeController))

        self._serverForceStop(bc)

class LocalhostBEControllerTests(TestCase, BEControllerTests):
    def __init__(self, *args):
        super(LocalhostBEControllerTests, self).__init__(*args)


    def _getBuildEnvironment(self):
        return BuildEnvironment.objects.create(
                lock = BuildEnvironment.LOCK_FREE,
                betype = BuildEnvironment.TYPE_LOCAL,
                address = test_address,
                sourcedir = test_sourcedir,
                builddir = test_builddir )

    def _getBEController(self, obe):
        return LocalhostBEController(obe)

class RunBuildsCommandTests(TestCase):
    def test_bec_select(self):
        """
        Tests that we can find and lock a build environment
        """

        obe = BuildEnvironment.objects.create(lock = BuildEnvironment.LOCK_FREE, betype = BuildEnvironment.TYPE_LOCAL)
        command = Command()
        bec = command._selectBuildEnvironment()

        # make sure we select the object we've just built
        self.assertTrue(bec.be.id == obe.id, "Environment is not properly selected")
        # we have a locked environment
        self.assertTrue(bec.be.lock == BuildEnvironment.LOCK_LOCK, "Environment is not locked")
        # no more selections possible here
        self.assertRaises(IndexError, command._selectBuildEnvironment)

    def test_br_select(self):
        from orm.models import Project, Release, BitbakeVersion, Branch
        p = Project.objects.create_project("test", Release.objects.get_or_create(name = "HEAD", bitbake_version = BitbakeVersion.objects.get_or_create(name="HEAD", branch=Branch.objects.get_or_create(name="HEAD"))[0])[0])
        obr = BuildRequest.objects.create(state = BuildRequest.REQ_QUEUED, project = p)
        command = Command()
        br = command._selectBuildRequest()

        # make sure we select the object we've just built
        self.assertTrue(obr.id == br.id, "Request is not properly selected")
        # we have a locked environment
        self.assertTrue(br.state == BuildRequest.REQ_INPROGRESS, "Request is not updated")
        # no more selections possible here
        self.assertRaises(IndexError, command._selectBuildRequest)


class UtilityTests(TestCase):
    def test_reduce_path(self):
        from bldcontrol.management.commands.loadconf import _reduce_canon_path, _get_id_for_sourcetype

        self.assertTrue( _reduce_canon_path("/") == "/")
        self.assertTrue( _reduce_canon_path("/home/..") == "/")
        self.assertTrue( _reduce_canon_path("/home/../ana") == "/ana")
        self.assertTrue( _reduce_canon_path("/home/../ana/..") == "/")
        self.assertTrue( _reduce_canon_path("/home/ana/mihai/../maria") == "/home/ana/maria")

    def test_get_id_for_sorucetype(self):
        from bldcontrol.management.commands.loadconf import _reduce_canon_path, _get_id_for_sourcetype
        self.assertTrue( _get_id_for_sourcetype("layerindex") == 1)
        self.assertTrue( _get_id_for_sourcetype("local") == 0)
        self.assertTrue( _get_id_for_sourcetype("imported") == 2)
        with self.assertRaises(Exception):
            _get_id_for_sourcetype("unknown")
