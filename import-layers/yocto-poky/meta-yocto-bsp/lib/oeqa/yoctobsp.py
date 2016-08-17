import unittest
import os
import logging
import tempfile
import shutil

from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd
from oeqa.utils.decorators import skipUnlessPassed

class YoctoBSP(oeSelfTest):

    @classmethod
    def setUpClass(self):
        result = runCmd("yocto-bsp list karch")
        self.karchs = [karch.lstrip() for karch in result.output.splitlines()][1:]

    def test_yoctobsp_listproperties(self):
        for karch in self.karchs:
            result = runCmd("yocto-bsp list %s properties" % karch)
            self.assertEqual(0, result.status, msg="Properties from %s architecture could not be listed" % karch)

    def test_yoctobsp_create(self):
        # Generate a temporal file and folders for each karch
        json = "{\"use_default_kernel\":\"yes\"}\n"
        fd = tempfile.NamedTemporaryFile(delete=False)
        fd.write(json)
        fd.close()
        tmpfolders = dict([(karch, tempfile.mkdtemp()) for karch in self.karchs])
        # Create BSP
        for karch in self.karchs:
            result = runCmd("yocto-bsp create test %s -o %s -i %s" % (karch, "%s/unitest" % tmpfolders[karch], fd.name))
            self.assertEqual(0, result.status, msg="Could not create a BSP with architecture %s using %s " % (karch, fd.name))
        # Remove tmp file/folders
        os.unlink(fd.name)
        self.assertFalse(os.path.exists(fd.name), msg = "Temporal file %s could not be removed" % fd.name)
        for tree in tmpfolders.values():
            shutil.rmtree(tree)
            self.assertFalse(os.path.exists(tree), msg = "Temporal folder %s could not be removed" % tree)
