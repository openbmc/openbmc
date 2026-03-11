import os
import re

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var

class CveCheckerTests(OESelftestTestCase):
    def test_cve_checker(self):
        image = "core-image-sato"

        deploy_dir = get_bb_var("DEPLOY_DIR_IMAGE")
        image_link_name = get_bb_var('IMAGE_LINK_NAME', image)

        manifest_link = os.path.join(deploy_dir, "%s.cve" % image_link_name)

        self.logger.info('CVE_CHECK_MANIFEST = "%s"' % manifest_link)
        if (not 'cve-check' in get_bb_var('INHERIT')):
            add_cve_check_config = 'INHERIT += "cve-check"'
            self.append_config(add_cve_check_config)
        self.append_config('CVE_CHECK_MANIFEST = "%s"' % manifest_link)
        result = bitbake("-k -c cve_check %s" % image, ignore_status=True)
        if (not 'cve-check' in get_bb_var('INHERIT')):
            self.remove_config(add_cve_check_config)

        isfile = os.path.isfile(manifest_link)
        self.assertEqual(True, isfile, 'Failed to create cve data file : %s' % manifest_link)

