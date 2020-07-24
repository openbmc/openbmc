#
# BitBake Tests for cooker.py
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
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
        collection.collection_priorities(pkgfns, pkgfns, self.d)
        logger.removeHandler(log_handler)

        # Should be empty (no generated messages)
        expected = []

        self.assertEqual(log_handler.logdata, expected)
