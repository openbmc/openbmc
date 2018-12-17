# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Tests for cooker.py
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
#

import unittest
import tempfile
import os
import bb, bb.cooker
import re
import logging

# Cooker tests
class CookerTest(unittest.TestCase):
    def setUp(self):
        # At least one variable needs to be set
        self.d = bb.data.init()
        topdir = os.path.join(os.path.dirname(os.path.realpath(__file__)), "testdata/cooker")
        self.d.setVar('TOPDIR', topdir)

    def test_CookerCollectFiles_sublayers(self):
        '''Test that a sublayer of an existing layer does not trigger
           No bb files matched ...'''

        def append_collection(topdir, path, d):
            collection = path.split('/')[-1]
            pattern = "^" + topdir + "/" + path + "/"
            regex = re.compile(pattern)
            priority = 5

            d.setVar('BBFILE_COLLECTIONS', (d.getVar('BBFILE_COLLECTIONS') or "") + " " + collection)
            d.setVar('BBFILE_PATTERN_%s' % (collection), pattern)
            d.setVar('BBFILE_PRIORITY_%s' % (collection), priority)

            return (collection, pattern, regex, priority)

        topdir = self.d.getVar("TOPDIR")

        # Priorities: list of (collection, pattern, regex, priority)
        bbfile_config_priorities = []
        # Order is important for this test, shortest to longest is typical failure case
        bbfile_config_priorities.append( append_collection(topdir, 'first', self.d) )
        bbfile_config_priorities.append( append_collection(topdir, 'second', self.d) )
        bbfile_config_priorities.append( append_collection(topdir, 'second/third', self.d) )

        pkgfns = [ topdir + '/first/recipes/sample1_1.0.bb',
                   topdir + '/second/recipes/sample2_1.0.bb',
                   topdir + '/second/third/recipes/sample3_1.0.bb' ]

        class LogHandler(logging.Handler):
            def __init__(self):
                logging.Handler.__init__(self)
                self.logdata = []

            def emit(self, record):
                self.logdata.append(record.getMessage())

        # Move cooker to use my special logging
        logger = bb.cooker.logger
        log_handler = LogHandler()
        logger.addHandler(log_handler)
        collection = bb.cooker.CookerCollectFiles(bbfile_config_priorities)
        collection.collection_priorities(pkgfns, self.d)
        logger.removeHandler(log_handler)

        # Should be empty (no generated messages)
        expected = []

        self.assertEqual(log_handler.logdata, expected)
