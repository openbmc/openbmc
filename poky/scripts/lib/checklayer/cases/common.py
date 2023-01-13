# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import glob
import os
import unittest
import re
from checklayer import get_signatures, LayerType, check_command, get_depgraph, compare_signatures
from checklayer.case import OECheckLayerTestCase

class CommonCheckLayer(OECheckLayerTestCase):
    def test_readme(self):
        if self.tc.layer['type'] == LayerType.CORE:
            raise unittest.SkipTest("Core layer's README is top level")

        # The top-level README file may have a suffix (like README.rst or README.txt).
        readme_files = glob.glob(os.path.join(self.tc.layer['path'], '[Rr][Ee][Aa][Dd][Mm][Ee]*'))
        self.assertTrue(len(readme_files) > 0,
                        msg="Layer doesn't contain a README file.")

        # There might be more than one file matching the file pattern above
        # (for example, README.rst and README-COPYING.rst). The one with the shortest
        # name is considered the "main" one.
        readme_file = sorted(readme_files)[0]
        data = ''
        with open(readme_file, 'r') as f:
            data = f.read()
        self.assertTrue(data,
                msg="Layer contains a README file but it is empty.")

        # If a layer's README references another README, then the checks below are not valid
        if re.search('README', data, re.IGNORECASE):
            return

        self.assertIn('maintainer', data.lower())
        self.assertIn('patch', data.lower())
        # Check that there is an email address in the README
        email_regex = re.compile(r"[^@]+@[^@]+")
        self.assertTrue(email_regex.match(data))

    def test_parse(self):
        check_command('Layer %s failed to parse.' % self.tc.layer['name'],
                      'bitbake -p')

    def test_show_environment(self):
        check_command('Layer %s failed to show environment.' % self.tc.layer['name'],
                      'bitbake -e')

    def test_world(self):
        '''
        "bitbake world" is expected to work. test_signatures does not cover that
        because it is more lenient and ignores recipes in a world build that
        are not actually buildable, so here we fail when "bitbake -S none world"
        fails.
        '''
        get_signatures(self.td['builddir'], failsafe=False)

    def test_world_inherit_class(self):
        '''
        This also does "bitbake -S none world" along with inheriting "yocto-check-layer"
        class, which can do additional per-recipe test cases.
        '''
        msg = []
        try:
            get_signatures(self.td['builddir'], failsafe=False, machine=None, extravars='BB_ENV_PASSTHROUGH_ADDITIONS="$BB_ENV_PASSTHROUGH_ADDITIONS INHERIT" INHERIT="yocto-check-layer"')
        except RuntimeError as ex:
            msg.append(str(ex))
        if msg:
            msg.insert(0, 'Layer %s failed additional checks from yocto-check-layer.bbclass\nSee below log for specific recipe parsing errors:\n' % \
                self.tc.layer['name'])
            self.fail('\n'.join(msg))

    def test_signatures(self):
        if self.tc.layer['type'] == LayerType.SOFTWARE and \
           not self.tc.test_software_layer_signatures:
            raise unittest.SkipTest("Not testing for signature changes in a software layer %s." \
                     % self.tc.layer['name'])

        curr_sigs, _ = get_signatures(self.td['builddir'], failsafe=True)
        msg = compare_signatures(self.td['sigs'], curr_sigs)
        if msg is not None:
            self.fail('Adding layer %s changed signatures.\n%s' % (self.tc.layer['name'], msg))

    def test_layerseries_compat(self):
        for collection_name, collection_data in self.tc.layer['collections'].items():
            self.assertTrue(collection_data['compat'], "Collection %s from layer %s does not set compatible oe-core versions via LAYERSERIES_COMPAT_collection." \
                 % (collection_name, self.tc.layer['name']))
