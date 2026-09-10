#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
# Comprehensive tests for go-vendor.bbclass:
#   GoVendorSrcUriTests  -- unit tests for the go_src_uri() helper (offline, ~10 s)
#   GoVendorTaskTests    -- integration tests using the real recipetool-go-test project
#                           (network fetch of git.yoctoproject.org + github.com/godbus/dbus)

import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import bitbake, get_bb_var

# ---------------------------------------------------------------------------
# Class 1: go_src_uri() helper function
# ---------------------------------------------------------------------------

class GoVendorSrcUriTests(OESelftestTestCase):
    """Tests for the go_src_uri() Python helper in go-vendor.bbclass.

    Parses the go-vendor-src-uri fixture recipe (meta-selftest/recipes-test/
    govendor/go-vendor-src-uri_1.0.bb) via bb.tinfoil (config_only=True) once
    per class so the function runs inside the real BitBake class-loading
    machinery.  All tests are offline and complete in ~10 s total.
    """

    @classmethod
    def setUpClass(cls):
        import bb.tinfoil
        super().setUpClass()

        recipe_path = os.path.join(cls.testlayer_path, 'recipes-test',
                                    'govendor', 'go-vendor-src-uri_1.0.bb')

        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True, quiet=2)
            rd = tinfoil.parse_recipe_file(recipe_path)
            cls.bp = rd.getVar('BP')
            cls.uris = {k: rd.getVar('GO_SRC_URI_' + k)
                        for k in ('BASIC', 'PATH', 'PATHMAJOR', 'SUBDIR',
                                  'REPLACES', 'SVN')}

    # --- individual test methods -------------------------------------------

    def test_basic(self):
        """destsuffix uses expanded BP"""
        uri = self.uris['BASIC']
        expected_ds = 'destsuffix=%s/src/import/vendor.fetch/github.com/foo/bar@v1.0.0' % self.bp
        self.assertIn(expected_ds, uri)
        self.assertIn('go_module_path=github.com/foo/bar', uri)
        self.assertIn('nobranch=1', uri)
        self.assertIn('protocol=https', uri)
        self.assertIn('is_go_dependency=1', uri)

    def test_path_override(self):
        """Explicit path= sets go_module_path independently of the repo URL."""
        uri = self.uris['PATH']
        self.assertIn('go_module_path=github.com/foo/bar/v2', uri)
        expected_ds = 'destsuffix=%s/src/import/vendor.fetch/github.com/foo/bar@v2.1.0' % self.bp
        self.assertIn(expected_ds, uri)

    def test_pathmajor(self):
        """pathmajor is encoded as go_pathmajor in the URI."""
        self.assertIn('go_pathmajor=/v2', self.uris['PATHMAJOR'])

    def test_subdir(self):
        """subdir is encoded as go_subdir in the URI."""
        self.assertIn('go_subdir=sub/pkg', self.uris['SUBDIR'])

    def test_replaces(self):
        """replaces is encoded as go_module_replacement in the URI."""
        self.assertIn('go_module_replacement=../local/baz', self.uris['REPLACES'])

    def test_non_git_vcs(self):
        """Non-git VCS omits the git-specific nobranch and protocol=https flags."""
        uri = self.uris['SVN']
        self.assertIn('svn://example.com/svnmod', uri)
        self.assertNotIn('nobranch', uri)
        self.assertNotIn('protocol=https', uri)
        self.assertIn('is_go_dependency=1', uri)


# ---------------------------------------------------------------------------
# Class 2: do_go_vendor task integration — real project
# ---------------------------------------------------------------------------

class GoVendorTaskTests(OESelftestTestCase):
    """Integration tests for do_go_vendor using the real recipetool-go-test project.

    The govendor-real-test recipe (meta-selftest/recipes-test/govendor/
    govendor-real-test_1.0.bb) fetches git.yoctoproject.org/recipetool-go-test
    (commit c3e213c) as its main source and github.com/godbus/dbus/v5 v5.1.0
    as an external vendor dependency.  The project also has a local module
    replacement (github.com/matryer/is => ./is) that lives inside the main
    source tree.

    do_go_vendor runs ONCE in setUpClass; individual test methods inspect the
    resulting vendor directory without re-running bitbake.

    Verified behaviors:
      - External dependency files are copied to vendor/
      - *_test.go files are excluded from the vendor copy
      - pathmajor fallback: when the /v5 subdir is absent, repo root is used
      - modules.txt is reproduced inside vendor/
      - Local module replacement creates a relative symlink in vendor/
      - S/src/GO_IMPORT/vendor symlink is created pointing at S/src/import/vendor
    """

    GO_IMPORT = 'git.yoctoproject.org/recipetool-go-test'
    RECIPE_NAME = 'govendor-real-test'

    @classmethod
    def setUpClass(cls):
        super().setUpClass()

        bitbake('%s -c go_vendor' % cls.RECIPE_NAME)
        cls.s_dir = get_bb_var('S', cls.RECIPE_NAME)
        cls.vendor_dir = os.path.join(cls.s_dir, 'src', 'import', 'vendor')

    # --- test methods (all inspect cls.vendor_dir, no bitbake) ---------------

    def test_external_dep_copied_to_vendor(self):
        """godbus/dbus/v5 source files are copied into vendor/."""
        dbus_dir = os.path.join(self.vendor_dir, 'github.com', 'godbus', 'dbus', 'v5')
        self.assertTrue(os.path.isdir(dbus_dir),
            'vendor/github.com/godbus/dbus/v5/ not created')
        self.assertTrue(os.path.isfile(os.path.join(dbus_dir, 'conn.go')),
            'conn.go not found in vendor/github.com/godbus/dbus/v5/')

    def test_test_files_excluded_from_vendor(self):
        """*_test.go files from godbus/dbus are not copied into vendor/."""
        dbus_dir = os.path.join(self.vendor_dir, 'github.com', 'godbus', 'dbus', 'v5')
        self.assertFalse(
            os.path.isfile(os.path.join(dbus_dir, 'conn_test.go')),
            'conn_test.go should have been excluded from vendor by *_test.go pattern')

    def test_pathmajor_falls_back_to_root(self):
        """godbus/dbus v5.1.0 has no v5/ subdir; pathmajor=/v5 falls back to root."""
        dbus_dir = os.path.join(self.vendor_dir, 'github.com', 'godbus', 'dbus', 'v5')
        # auth.go is at the repo root of godbus/dbus, not inside a v5/ subdir.
        self.assertTrue(os.path.isfile(os.path.join(dbus_dir, 'auth.go')),
            'auth.go not found; pathmajor=/v5 fallback to repo root did not work')

    def test_modules_txt_in_vendor(self):
        """modules.txt is reproduced inside vendor/ and lists the external dep."""
        modules_txt = os.path.join(self.vendor_dir, 'modules.txt')
        self.assertTrue(os.path.isfile(modules_txt))
        with open(modules_txt) as fh:
            content = fh.read()
        self.assertIn('github.com/godbus/dbus/v5 v5.1.0', content)

    def test_local_replacement_symlink(self):
        """go.mod local replacement (./is) creates a symlink in vendor/ to S/src/GO_IMPORT/is."""
        symlink = os.path.join(self.vendor_dir, 'github.com', 'matryer', 'is')
        self.assertTrue(os.path.islink(symlink),
            'vendor/github.com/matryer/is should be a symlink for the ./is local replacement')
        expected_target = os.path.join(self.s_dir, 'src', self.GO_IMPORT, 'is')
        self.assertEqual(os.path.realpath(symlink), os.path.realpath(expected_target),
            'Local replacement symlink does not point to S/src/GO_IMPORT/is')

    def test_go_import_vendor_symlink(self):
        """do_go_vendor creates S/src/GO_IMPORT/vendor -> S/src/import/vendor."""
        link = os.path.join(self.s_dir, 'src', self.GO_IMPORT, 'vendor')
        self.assertTrue(os.path.islink(link),
            'S/src/GO_IMPORT/vendor symlink not created')
        self.assertEqual(os.path.realpath(link), os.path.realpath(self.vendor_dir),
            'S/src/GO_IMPORT/vendor does not point to S/src/import/vendor')

    def test_do_compile_succeeds(self):
        """The vendored tree is valid Go: the toolchain can actually build it.

        Runs against the -native variant so this only depends on go-native,
        not a full target Go cross-toolchain. The SRC_URI fetch is already
        cached in DL_DIR from setUpClass, so this mainly re-runs
        unpack/go_vendor/compile for the native variant.
        """
        bitbake('%s-native -c compile' % self.RECIPE_NAME)
