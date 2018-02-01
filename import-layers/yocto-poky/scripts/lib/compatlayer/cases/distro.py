# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import unittest

from compatlayer import LayerType
from compatlayer.case import OECompatLayerTestCase

class DistroCompatLayer(OECompatLayerTestCase):
    @classmethod
    def setUpClass(self):
        if self.tc.layer['type'] != LayerType.DISTRO:
            raise unittest.SkipTest("DistroCompatLayer: Layer %s isn't Distro one." %\
                self.tc.layer['name'])

    def test_distro_defines_distros(self):
        self.assertTrue(self.tc.layer['conf']['distros'], 
                "Layer is BSP but doesn't defines machines.")

    def test_distro_no_set_distros(self):
        from oeqa.utils.commands import get_bb_var

        distro = get_bb_var('DISTRO')
        self.assertEqual(self.td['bbvars']['DISTRO'], distro,
                msg="Layer %s modified distro %s -> %s" % \
                    (self.tc.layer['name'], self.td['bbvars']['DISTRO'], distro))
