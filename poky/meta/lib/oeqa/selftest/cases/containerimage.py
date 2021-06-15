#
# SPDX-License-Identifier: MIT
#

import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_vars, runCmd

# This test builds an image with using the "container" IMAGE_FSTYPE, and
# ensures that then files in the image are only the ones expected.
#
# The only package added to the image is container_image_testpkg, which
# contains one file. However, due to some other things not cleaning up during
# rootfs creation, there is some cruft. Ideally bugs will be filed and the
# cruft removed, but for now we whitelist some known set.
#
# Also for performance reasons we're only checking the cruft when using ipk.
# When using deb, and rpm it is a bit different and we could test all
# of them, but this test is more to catch if other packages get added by
# default other than what is in ROOTFS_BOOTSTRAP_INSTALL.
#
class ContainerImageTests(OESelftestTestCase):

    # Verify that when specifying a IMAGE_TYPEDEP_ of the form "foo.bar" that
    # the conversion type bar gets added as a dep as well
    def test_expected_files(self):

        def get_each_path_part(path):
            if path:
                part = [ '.' + path + '/' ]
                result = get_each_path_part(path.rsplit('/', 1)[0])
                if result:
                    return part + result
                else:
                    return part
            else:
                return None

        self.write_config("""PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"
IMAGE_FSTYPES = "container"
PACKAGE_CLASSES = "package_ipk"
IMAGE_FEATURES = ""
IMAGE_BUILDINFO_FILE = ""
INIT_MANAGER = "sysvinit"
IMAGE_INSTALL_remove = "ssh-pregen-hostkeys"

""")

        bbvars = get_bb_vars(['bindir', 'sysconfdir', 'localstatedir',
                              'DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'],
                              target='container-test-image')
        expected_files = [
                    './',
                    '.{bindir}/theapp',
                    '.{sysconfdir}/default/',
                    '.{sysconfdir}/default/postinst',
                    '.{sysconfdir}/ld.so.cache',
                    '.{sysconfdir}/timestamp',
                    '.{sysconfdir}/version',
                    './run/',
                    '.{localstatedir}/cache/',
                    '.{localstatedir}/lib/'
                ]

        expected_files = [ x.format(bindir=bbvars['bindir'],
                                    sysconfdir=bbvars['sysconfdir'],
                                    localstatedir=bbvars['localstatedir'])
                                    for x in expected_files ]

        # Since tar lists all directories individually, make sure each element
        # from bindir, sysconfdir, etc is added
        expected_files += get_each_path_part(bbvars['bindir'])
        expected_files += get_each_path_part(bbvars['sysconfdir'])
        expected_files += get_each_path_part(bbvars['localstatedir'])

        expected_files = sorted(expected_files)

        # Build the image of course
        bitbake('container-test-image')

        image = os.path.join(bbvars['DEPLOY_DIR_IMAGE'],
                             bbvars['IMAGE_LINK_NAME'] + '.tar.bz2')

        # Ensure the files in the image are what we expect
        result = runCmd("tar tf {} | sort".format(image), shell=True)
        self.assertEqual(result.output.split('\n'), expected_files)
