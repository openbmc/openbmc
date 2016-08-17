#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2013-2015 Intel Corporation
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

"""Test cases for Toaster ORM."""

from django.test import TestCase, TransactionTestCase
from orm.models import LocalLayerSource, LayerIndexLayerSource, ImportedLayerSource, LayerSource
from orm.models import Branch, LayerVersionDependency

from orm.models import Project, Layer, Layer_Version, Branch, ProjectLayer
from orm.models import Release, ReleaseLayerSourcePriority, BitbakeVersion

from django.db import IntegrityError

import os

# set TTS_LAYER_INDEX to the base url to use a different instance of the layer index

class LayerSourceVerifyInheritanceSaveLoad(TestCase):
    """
    Tests to verify inheritance for the LayerSource proxy-inheritance classes.
    """
    def test_object_creation(self):
        """Test LayerSource object creation."""
        for name, sourcetype in [("a1", LayerSource.TYPE_LOCAL),
                                 ("a2", LayerSource.TYPE_LAYERINDEX),
                                 ("a3", LayerSource.TYPE_IMPORTED)]:
            LayerSource.objects.create(name=name, sourcetype=sourcetype)

        objects = LayerSource.objects.all()
        self.assertTrue(isinstance(objects[0], LocalLayerSource))
        self.assertTrue(isinstance(objects[1], LayerIndexLayerSource))
        self.assertTrue(isinstance(objects[2], ImportedLayerSource))

    def test_duplicate_error(self):
        """Test creation of duplicate LayerSource objects."""
        stype = LayerSource.TYPE_LOCAL
        LayerSource.objects.create(name="a1", sourcetype=stype)
        with self.assertRaises(IntegrityError):
            LayerSource.objects.create(name="a1", sourcetype=stype)


class LILSUpdateTestCase(TransactionTestCase):
    """Test Layer Source update."""

    def setUp(self):
        """Create release."""
        bbv = BitbakeVersion.objects.create(\
                  name="master", giturl="git://git.openembedded.org/bitbake")
        Release.objects.create(name="default-release", bitbake_version=bbv,
                               branch_name="master")

    def test_update(self):
        """Check if LayerSource.update can fetch branches."""
        url = os.getenv("TTS_LAYER_INDEX",
                        default="http://layers.openembedded.org/")

        lsobj = LayerSource.objects.create(\
                    name="b1", sourcetype=LayerSource.TYPE_LAYERINDEX,
                    apiurl=url + "layerindex/api/")
        lsobj.update()
        self.assertTrue(lsobj.branch_set.all().count() > 0,
                        "no branches fetched")

class LayerVersionEquivalenceTestCase(TestCase):
    """Verify Layer_Version priority selection."""

    def setUp(self):
        """Create required objects."""
        # create layer source
        self.lsrc = LayerSource.objects.create(name="dummy-layersource",
                                               sourcetype=LayerSource.TYPE_LOCAL)
        # create release
        bbv = BitbakeVersion.objects.create(\
                  name="master", giturl="git://git.openembedded.org/bitbake")
        self.release = Release.objects.create(name="default-release",
                                              bitbake_version=bbv,
                                              branch_name="master")
        # attach layer source to release
        ReleaseLayerSourcePriority.objects.create(\
            release=self.release, layer_source=self.lsrc, priority=1)

        # create a layer version for the layer on the specified branch
        self.layer = Layer.objects.create(name="meta-testlayer",
                                          layer_source=self.lsrc)
        self.branch = Branch.objects.create(name="master", layer_source=self.lsrc)
        self.lver = Layer_Version.objects.create(\
            layer=self.layer, layer_source=self.lsrc, up_branch=self.branch)

        # create project and project layer
        self.project = Project.objects.create_project(name="test-project",
                                                      release=self.release)
        ProjectLayer.objects.create(project=self.project,
                                    layercommit=self.lver)

        # create spoof layer that should not appear in the search results
        layer = Layer.objects.create(name="meta-notvalid",
                                     layer_source=self.lsrc)
        self.lver2 = Layer_Version.objects.create(layer=layer,
                                                  layer_source=self.lsrc,
                                                  up_branch=self.branch)

    def test_single_layersource(self):
        """
        When we have a single layer version,
        get_equivalents_wpriority() should return a list with
        just this layer_version.
        """
        equivqs = self.lver.get_equivalents_wpriority(self.project)
        self.assertEqual(list(equivqs), [self.lver])

    def test_dual_layersource(self):
        """
        If we have two layers with the same name, from different layer sources,
        we expect both layers in, in increasing priority of the layer source.
        """
        lsrc2 = LayerSource.objects.create(\
                    name="dummy-layersource2",
                    sourcetype=LayerSource.TYPE_LOCAL,
                    apiurl="test")

        # assign a lower priority for the second layer source
        self.release.releaselayersourcepriority_set.create(layer_source=lsrc2,
                                                           priority=2)

        # create a new layer_version for a layer with the same name
        # coming from the second layer source
        layer2 = Layer.objects.create(name="meta-testlayer",
                                      layer_source=lsrc2)
        lver2 = Layer_Version.objects.create(layer=layer2, layer_source=lsrc2,
                                             up_branch=self.branch)

        # expect two layer versions, in the priority order
        equivqs = self.lver.get_equivalents_wpriority(self.project)
        self.assertEqual(list(equivqs), [lver2, self.lver])

    def test_compatible_layer_versions(self):
        """
        When we have a 2 layer versions, get_all_compatible_layerversions()
        should return a queryset with both.
        """
        compat_lv = self.project.get_all_compatible_layer_versions()
        self.assertEqual(list(compat_lv), [self.lver, self.lver2])

    def test_layerversion_get_alldeps(self):
        """Test Layer_Version.get_alldeps API."""
        lvers = {}
        for i in range(10):
            name = "layer%d" % i
            lvers[name] = Layer_Version.objects.create(layer=Layer.objects.create(name=name),
                                                       project=self.project)
            if i:
                LayerVersionDependency.objects.create(layer_version=lvers["layer%d" % (i - 1)],
                                                      depends_on=lvers[name])
                # Check dinamically added deps
                self.assertEqual(lvers['layer0'].get_alldeps(self.project.id),
                                 [lvers['layer%d' % n] for n in range(1, i+1)])

        # Check chain of deps created in previous loop
        for i in range(10):
            self.assertEqual(lvers['layer%d' % i].get_alldeps(self.project.id),
                             [lvers['layer%d' % n] for n in range(i+1, 10)])
