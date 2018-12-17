# Copyright (C) 2017-2018 Wind River Systems, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

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

