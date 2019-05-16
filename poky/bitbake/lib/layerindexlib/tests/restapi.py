# Copyright (C) 2017-2018 Wind River Systems, Inc.
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

def skipIfNoNetwork():
    if os.environ.get("BB_SKIP_NETTESTS") == "yes":
        return unittest.skip("Network tests being skipped")
    return lambda f: f

class LayerIndexWebRestApiTest(LayersTest):

    @skipIfNoNetwork()
    def setUp(self):
        self.assertFalse(os.environ.get("BB_SKIP_NETTESTS") == "yes", msg="BB_SKIP_NETTESTS set, but we tried to test anyway")
        LayersTest.setUp(self)
        self.layerindex = layerindexlib.LayerIndex(self.d)
        self.layerindex.load_layerindex('http://layers.openembedded.org/layerindex/api/;branch=sumo', load=['layerDependencies'])

    @skipIfNoNetwork()
    def test_layerindex_is_empty(self):
        self.assertFalse(self.layerindex.is_empty(), msg="Layerindex is empty")

    @skipIfNoNetwork()
    def test_layerindex_store_file(self):
        self.layerindex.store_layerindex('file://%s/file.json' % self.tempdir, self.layerindex.indexes[0])

        self.assertTrue(os.path.isfile('%s/file.json' % self.tempdir), msg="Temporary file was not created by store_layerindex")

        reload = layerindexlib.LayerIndex(self.d)
        reload.load_layerindex('file://%s/file.json' % self.tempdir)

        self.assertFalse(reload.is_empty(), msg="Layerindex is empty")

        # Calculate layerItems in original index that should NOT be in reload
        layerItemNames = []
        for itemId in self.layerindex.indexes[0].layerItems:
            layerItemNames.append(self.layerindex.indexes[0].layerItems[itemId].name)

        for layerBranchId in self.layerindex.indexes[0].layerBranches:
            layerItemNames.remove(self.layerindex.indexes[0].layerBranches[layerBranchId].layer.name)

        for itemId in reload.indexes[0].layerItems:
            self.assertFalse(reload.indexes[0].layerItems[itemId].name in layerItemNames, msg="Item reloaded when it shouldn't have been")

        # Compare the original to what we wrote...
        for type in self.layerindex.indexes[0]._index:
            if type == 'apilinks' or \
               type == 'layerItems' or \
               type in self.layerindex.indexes[0].config['local']:
                continue
            for id in getattr(self.layerindex.indexes[0], type):
                self.logger.debug(1, "type %s" % (type))

                self.assertTrue(id in getattr(reload.indexes[0], type), msg="Id number not in reloaded index")

                self.logger.debug(1, "%s ? %s" % (getattr(self.layerindex.indexes[0], type)[id], getattr(reload.indexes[0], type)[id]))

                self.assertEqual(getattr(self.layerindex.indexes[0], type)[id], getattr(reload.indexes[0], type)[id], msg="Reloaded contents different")

    @skipIfNoNetwork()
    def test_layerindex_store_split(self):
        self.layerindex.store_layerindex('file://%s' % self.tempdir, self.layerindex.indexes[0])

        reload = layerindexlib.LayerIndex(self.d)
        reload.load_layerindex('file://%s' % self.tempdir)

        self.assertFalse(reload.is_empty(), msg="Layer index is empty")

        for type in self.layerindex.indexes[0]._index:
            if type == 'apilinks' or \
               type == 'layerItems' or \
               type in self.layerindex.indexes[0].config['local']:
                continue
            for id in getattr(self.layerindex.indexes[0] ,type):
                self.logger.debug(1, "type %s" % (type))

                self.assertTrue(id in getattr(reload.indexes[0], type), msg="Id number missing from reloaded data")

                self.logger.debug(1, "%s ? %s" % (getattr(self.layerindex.indexes[0] ,type)[id], getattr(reload.indexes[0], type)[id]))

                self.assertEqual(getattr(self.layerindex.indexes[0] ,type)[id], getattr(reload.indexes[0], type)[id], msg="reloaded data does not match original")

    @skipIfNoNetwork()
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
            self.assertEqual(layerBranch.layer.name, "openembedded-core", msg='OpenEmbedded-Core is no the first dependency')

            # meta-python should cause an openembedded-core dependency, if not assert!
            for dep in layerDeps:
                if dep.layer.name == 'meta-python':
                    break
            else:
                self.logger.debug(1, "meta-python was not found")
                self.assetTrue(False)

            # Only check the first element...
            break
        else:
            # Empty list, this is bad.
            self.logger.debug(1, "Empty list of dependencies")
            self.assertIsNotNone(first, msg="Empty list of dependencies")

            # Last dep should be the requested item
            layerBranch = dependencies[deplayerbranch][0]
            self.assertEqual(layerBranch.layer.name, "meta-python", msg="Last dependency not meta-python")

    @skipIfNoNetwork()
    def test_find_collection(self):
        def _check(collection, expected):
            self.logger.debug(1, "Looking for collection %s..." % collection)
            result = self.layerindex.find_collection(collection)
            if expected:
                self.assertIsNotNone(result, msg="Did not find %s when it should be there" % collection)
            else:
                self.assertIsNone(result, msg="Found %s when it shouldn't be there" % collection)

        tests = [ ('core', True),
                  ('openembedded-core', False),
                  ('networking-layer', True),
                  ('meta-python', True),
                  ('openembedded-layer', True),
                  ('notpresent', False) ]

        for collection,result in tests:
            _check(collection, result)

    @skipIfNoNetwork()
    def test_find_layerbranch(self):
        def _check(name, expected):
            self.logger.debug(1, "Looking for layerbranch %s..." % name)

            for index in self.layerindex.indexes:
                for layerbranchid in index.layerBranches:
                    self.logger.debug(1, "Present: %s" % index.layerBranches[layerbranchid].layer.name)
            result = self.layerindex.find_layerbranch(name)
            if expected:
                self.assertIsNotNone(result, msg="Did not find %s when it should be there" % collection)
            else:
                self.assertIsNone(result, msg="Found %s when it shouldn't be there" % collection)

        tests = [ ('openembedded-core', True),
                  ('core', False),
                  ('meta-networking', True),
                  ('meta-python', True),
                  ('meta-oe', True),
                  ('notpresent', False) ]

        for collection,result in tests:
            _check(collection, result)

