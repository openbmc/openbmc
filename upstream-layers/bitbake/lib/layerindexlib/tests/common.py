# Copyright (C) 2017-2018 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import tempfile
import os
import bb

import logging

class LayersTest(unittest.TestCase):

    def setUp(self):
        self.origdir = os.getcwd()
        self.d = bb.data.init()
        # At least one variable needs to be set
        self.d.setVar('DL_DIR', os.getcwd())

        if os.environ.get("BB_SKIP_NETTESTS") == "yes":
            self.d.setVar('BB_NO_NETWORK', '1')

        self.tempdir = tempfile.mkdtemp()
        self.logger = logging.getLogger("BitBake")

    def tearDown(self):
        os.chdir(self.origdir)
        if os.environ.get("BB_TMPDIR_NOCLEAN") == "yes":
            print("Not cleaning up %s. Please remove manually." % self.tempdir)
        else:
            bb.utils.prunedir(self.tempdir)

