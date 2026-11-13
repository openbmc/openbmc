#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import re

from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator import OETestTag
from oeqa.utils.commands import bitbake, runqemu

class FontCache(OESelftestTestCase):

    @OETestTag("runqemu")
    def test_image_font_cache_is_valid(self):
        """
        Summary:  Check the fontconfig caches built into an image are used
        Expected: fontconfig accepts the cache of every font directory and
                  writes no cache of its own, system or per-user
        Product:  oe-core
        """
        features = 'EXTRA_IMAGE_FEATURES = "empty-root-password allow-empty-password allow-root-login"\n'
        features += 'IMAGE_INSTALL:append = " ttf-bitstream-vera"\n'
        self.write_config(features)
        bitbake('core-image-minimal')

        with runqemu('core-image-minimal') as qemu:
            # FC_DEBUG=16 reports each cache fontconfig checks and each one it
            # writes. An empty HOME catches a per-user cache, which fontconfig
            # writes whenever it cannot use the system one.
            status, output = qemu.run_serial(
                'H=$(mktemp -d); HOME=$H XDG_CACHE_HOME=$H FC_DEBUG=16 fc-match sans-serif; echo "user cache: [$(ls -A $H)]"')
            self.assertEqual(status, 1, msg=output)

            checks = re.findall(r'FcCacheTimeValid dir "([^"]+)" cache checksum (\d+) dir checksum (\d+)', output)
            self.assertTrue(checks, msg="fontconfig checked no cache:\n" + output)
            for directory, cache, mtime in checks:
                self.assertEqual(cache, mtime, msg="%s: the cache records mtime %s, the directory has %s\n%s" % (directory, cache, mtime, output))

            self.assertNotIn('FcDirCacheWriteDir', output, msg="fontconfig rewrote a cache:\n" + output)
            self.assertIn('user cache: []', output, msg="fontconfig wrote a per-user cache:\n" + output)
