#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
import textwrap
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake
from oeqa.core.decorator.data import skipIfNotFeature

class IncompatibleLicenseTestObsolete(OESelftestTestCase):

    def lic_test(self, pn, pn_lic, lic, error_msg=None):
        if not error_msg:
            error_msg = 'ERROR: Nothing PROVIDES \'%s\'\n%s was skipped: it has incompatible license(s): %s' % (pn, pn, pn_lic)

        self.write_config("INCOMPATIBLE_LICENSE += \"%s\"" % (lic))

        result = bitbake('%s --dry-run' % (pn), ignore_status=True)
        if error_msg not in result.output:
            raise AssertionError(result.output)

    # Verify that a package with an SPDX license cannot be built when
    # INCOMPATIBLE_LICENSE contains an alias (in SPDXLICENSEMAP) of this SPDX
    # license
    def test_incompatible_alias_spdx_license(self):
        self.lic_test('incompatible-license', 'GPL-3.0-only', 'GPLv3', "is an obsolete license, please use an SPDX reference in INCOMPATIBLE_LICENSE")

    # Verify that a package with an SPDX license cannot be built when
    # INCOMPATIBLE_LICENSE contains a wildcarded alias license matching this
    # SPDX license
    def test_incompatible_alias_spdx_license_wildcard(self):
        self.lic_test('incompatible-license', 'GPL-3.0-only', '*GPLv3', "*GPLv3 is an invalid license wildcard entry")

    # Verify that a package with an alias (from SPDXLICENSEMAP) to an SPDX
    # license cannot be built when INCOMPATIBLE_LICENSE contains this alias
    def test_incompatible_alias_spdx_license_alias(self):
        self.lic_test('incompatible-license-alias', 'GPL-3.0-only', 'GPLv3', "is an obsolete license, please use an SPDX reference in INCOMPATIBLE_LICENSE")

    # Verify that a package with an alias (from SPDXLICENSEMAP) to an SPDX
    # license cannot be built when INCOMPATIBLE_LICENSE contains a wildcarded
    # license matching this SPDX license
    def test_incompatible_spdx_license_alias_wildcard(self):
        self.lic_test('incompatible-license-alias', 'GPL-3.0-only', '*GPL-3.0', "*GPL-3.0 is an invalid license wildcard entry")

    # Verify that a package with an alias (from SPDXLICENSEMAP) to an SPDX
    # license cannot be built when INCOMPATIBLE_LICENSE contains a wildcarded
    # alias license matching the SPDX license
    def test_incompatible_alias_spdx_license_alias_wildcard(self):
        self.lic_test('incompatible-license-alias', 'GPL-3.0-only', '*GPLv3', "*GPLv3 is an invalid license wildcard entry")


    # Verify that a package with multiple SPDX licenses cannot be built when
    # INCOMPATIBLE_LICENSE contains a wildcard to some of them
    def test_incompatible_spdx_licenses_wildcard(self):
        self.lic_test('incompatible-licenses', 'GPL-3.0-only LGPL-3.0-only', '*GPL-3.0-only', "*GPL-3.0-only is an invalid license wildcard entry")


    # Verify that a package with multiple SPDX licenses cannot be built when
    # INCOMPATIBLE_LICENSE contains a wildcard matching all licenses
    def test_incompatible_all_licenses_wildcard(self):
        self.lic_test('incompatible-licenses', 'GPL-2.0-only GPL-3.0-only LGPL-3.0-only', '*', "* is an invalid license wildcard entry")

class IncompatibleLicenseTests(OESelftestTestCase):

    def lic_test(self, pn, pn_lic, lic):
        error_msg = 'ERROR: Nothing PROVIDES \'%s\'\n%s was skipped: it has incompatible license(s): %s' % (pn, pn, pn_lic)

        self.write_config("INCOMPATIBLE_LICENSE += \"%s\"" % (lic))

        result = bitbake('%s --dry-run' % (pn), ignore_status=True)
        if error_msg not in result.output:
            raise AssertionError(result.output)

    # Verify that a package with an SPDX license cannot be built when
    # INCOMPATIBLE_LICENSE contains this SPDX license
    def test_incompatible_spdx_license(self):
        self.lic_test('incompatible-license', 'GPL-3.0-only', 'GPL-3.0-only')

    # Verify that a package with an SPDX license cannot be built when
    # INCOMPATIBLE_LICENSE contains a wildcarded license matching this SPDX
    # license
    def test_incompatible_spdx_license_wildcard(self):
        self.lic_test('incompatible-license', 'GPL-3.0-only', 'GPL-3.0*')

    # Verify that a package with an alias (from SPDXLICENSEMAP) to an SPDX
    # license cannot be built when INCOMPATIBLE_LICENSE contains this SPDX
    # license
    def test_incompatible_spdx_license_alias(self):
        self.lic_test('incompatible-license-alias', 'GPL-3.0-only', 'GPL-3.0-only')

    # Verify that a package with multiple SPDX licenses cannot be built when
    # INCOMPATIBLE_LICENSE contains some of them
    def test_incompatible_spdx_licenses(self):
        self.lic_test('incompatible-licenses', 'GPL-3.0-only LGPL-3.0-only', 'GPL-3.0-only LGPL-3.0-only')

    # Verify that a package with a non-SPDX license cannot be built when
    # INCOMPATIBLE_LICENSE contains this license
    def test_incompatible_nonspdx_license(self):
        self.lic_test('incompatible-nonspdx-license', 'LicenseRef-FooLicense', 'LicenseRef-FooLicense')

class IncompatibleLicensePerImageTests(OESelftestTestCase):
    def default_config(self):
        return """
IMAGE_INSTALL:append = " bash"
INCOMPATIBLE_LICENSE:pn-core-image-minimal = "GPL-3.0* LGPL-3.0*"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS:remove = "tar"
NO_GENERIC_LICENSE[SomeLicense] = "COPYING"
"""

    def test_bash_default(self):
        self.write_config(self.default_config())
        error_msg = "ERROR: core-image-minimal-1.0-r0 do_rootfs: Some packages cannot be installed into the image because they have incompatible licenses:\n\tbash (GPL-3.0-or-later)"

        result = bitbake('core-image-minimal', ignore_status=True)
        if error_msg not in result.output:
            raise AssertionError(result.output)

    def test_bash_and_license(self):
        self.disable_class("create-spdx")
        self.write_config(self.default_config() + '\nLICENSE:append:pn-bash = " AND LicenseRef-SomeLicense"\nERROR_QA:remove:pn-bash = "license-exists"')
        error_msg = "ERROR: core-image-minimal-1.0-r0 do_rootfs: Some packages cannot be installed into the image because they have incompatible licenses:\n\tbash (GPL-3.0-or-later)"

        result = bitbake('core-image-minimal', ignore_status=True)
        if error_msg not in result.output:
            raise AssertionError(result.output)

    def test_bash_or_license(self):
        self.disable_class("create-spdx")
        self.write_config(self.default_config() + '\nLICENSE:append:pn-bash = " OR LicenseRef-SomeLicense"\nERROR_QA:remove:pn-bash = "license-exists"\nERROR_QA:remove:pn-core-image-minimal = "license-file-missing"')

        bitbake('core-image-minimal')

    def test_bash_license_exceptions(self):
        self.write_config(self.default_config() + '\nINCOMPATIBLE_LICENSE_EXCEPTIONS:pn-core-image-minimal = "bash:GPL-3.0-or-later"\nERROR_QA:remove:pn-core-image-minimal = "license-exception"')

        bitbake('core-image-minimal')

    def test_spdx_exception(self):
        # Change bash license to have an SPDX exception, which will fail
        self.write_config(self.default_config() + textwrap.dedent(
            """\
            LICENSE:pn-bash = "GPL-3.0-or-later WITH GCC-exception-3.1"
            """))

        result = bitbake('core-image-minimal', ignore_status=True)
        error_msg = "ERROR: core-image-minimal-1.0-r0 do_rootfs: Some packages cannot be installed into the image because they have incompatible licenses:\n\tbash (GPL-3.0-or-later WITH GCC-exception-3.1)"

        # The SPDX exception can be explicitly allowed in INCOMPATIBLE_LICENSE_EXCEPTIONS
        self.write_config(self.default_config() + textwrap.dedent(
            """\
            LICENSE:pn-bash = "GPL-3.0-or-later WITH GCC-exception-3.1"
            INCOMPATIBLE_LICENSE_EXCEPTIONS:pn-core-image-minimal = "GCC-exception-3.1"
            ERROR_QA:remove:pn-core-image-minimal = "license-exception"
            """))
        bitbake('core-image-minimal')

class NoGPL3InImagesTests(OESelftestTestCase):
    def test_core_image_minimal(self):
        self.write_config("""
INCOMPATIBLE_LICENSE:pn-core-image-minimal = "GPL-3.0* LGPL-3.0*"

require conf/distro/include/no-gplv3.inc
""")
        bitbake('core-image-minimal')

    @skipIfNotFeature('wayland', 'Test requires wayland to be in DISTRO_FEATURES')
    def test_core_image_full_cmdline_weston(self):
        self.write_config("""
IMAGE_CLASSES += "testimage"
INCOMPATIBLE_LICENSE:pn-core-image-full-cmdline = "GPL-3.0* LGPL-3.0*"
INCOMPATIBLE_LICENSE:pn-core-image-weston = "GPL-3.0* LGPL-3.0*"
INCOMPATIBLE_LICENSE_EXCEPTIONS:pn-core-image-full-cmdline = "GCC-exception-3.1"
INCOMPATIBLE_LICENSE_EXCEPTIONS:pn-core-image-weston = "GCC-exception-3.1"
ERROR_QA:remove:pn-core-image-weston = "license-exception"
ERROR_QA:remove:pn-core-image-full-cmdline = "license-exception"

require conf/distro/include/no-gplv3.inc
""")
        bitbake('core-image-full-cmdline core-image-weston')
        bitbake('-c testimage core-image-full-cmdline core-image-weston')

