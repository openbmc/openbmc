# Copyright (C) 2018 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import tempfile
import os
import bb

import layerindexlib
from layerindexlib.tests.common import LayersTest

import logging

class LayerIndexCookerTest(LayersTest):

    def setUp(self):
        LayersTest.setUp(self)

        # Note this is NOT a comprehensive test of cooker, as we can't easily
        # configure the test data.  But we can emulate the basics of the layer.conf
        # files, so that is what we will do.

        new_topdir = os.path.join(os.path.dirname(os.path.realpath(__file__)), "testdata")
        new_bbpath = os.path.join(new_topdir, "build")

        self.d.setVar('TOPDIR', new_topdir)
        self.d.setVar('BBPATH', new_bbpath)

        self.d = bb.parse.handle("%s/conf/bblayers.conf" % new_bbpath, self.d, True)
        for layer in self.d.getVar('BBLAYERS').split():
            self.d = bb.parse.handle("%s/conf/layer.conf" % layer, self.d, True)

        self.layerindex = layerindexlib.LayerIndex(self.d)
        self.layerindex.load_layerindex('cooker://', load=['layerDependencies'])

    def test_layerindex_is_empty(self):
        self.assertFalse(self.layerindex.is_empty(), msg="Layerindex is not empty!")

    def test_dependency_resolution(self):
        # Verify depth first searching...
        (dependencies, invalidnames) = self.layerindex.find_dependencies(names=['meta-python'])

        first = True
        for deplayerbranch in dependencies:
            layerBranch = dependencies[deplayerbranch][0]
            layerDeps = dependencies[deplayerbranch][1:]

            if not first:
                continue

            first = False

            # Top of the deps should be openembedded-core, since everything depends on it.
            self.assertEqual(layerBranch.layer.name, "openembedded-core", msg='Top dependency not openembedded-core')

            # meta-python should cause an openembedded-core dependency, if not assert!
            for dep in layerDeps:
                if dep.layer.name == 'meta-python':
                    break
            else:
                self.assertTrue(False, msg='meta-python was not found')

            # Only check the first element...
            break
        else:
            if first:
                # Empty list, this is bad.
                self.assertTrue(False, msg='Empty list of dependencies')

            # Last dep should be the requested item
            layerBranch = dependencies[deplayerbranch][0]
            self.assertEqual(layerBranch.layer.name, "meta-python", msg='Last dependency not meta-python')

    def test_find_collection(self):
        def _check(collection, expected):
            self.logger.debug(1, "Looking for collection %s..." % collection)
            result = self.layerindex.find_collection(collection)
            if expected:
                self.assertIsNotNone(result, msg="Did not find %s when it shouldn't be there" % collection)
            else:
                self.assertIsNone(result, msg="Found %s when it should be there" % collection)

        tests = [ ('core', True),
                  ('openembedded-core', False),
                  ('networking-layer', True),
                  ('meta-python', True),
                  ('openembedded-layer', True),
                  ('notpresent', False) ]

        for collection,result in tests:
            _check(collection, result)

    def test_find_layerbranch(self):
        def _check(name, expected):
            self.logger.debug(1, "Looking for layerbranch %s..." % name)
            result = self.layerindex.find_layerbranch(name)
            if expected:
                self.assertIsNotNone(result, msg="Did not find %s when it shouldn't be there" % collection)
            else:
                self.assertIsNone(result, msg="Found %s when it should be there" % collection)

        tests = [ ('openembedded-core', True),
                  ('core', False),
                  ('networking-layer', True),
                  ('meta-python', True),
                  ('openembedded-layer', True),
                  ('notpresent', False) ]

        for collection,result in tests:
            _check(collection, result)

