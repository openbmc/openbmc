from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
import os
import glob
import re
import shutil
import tempfile
from oeqa.core.decorator.oeid import OETestID
from oeqa.utils.ftools import write_file


class Signing(OESelftestTestCase):

    gpg_dir = ""
    pub_key_path = ""
    secret_key_path = ""

    @classmethod
    def setUpClass(cls):
        super(Signing, cls).setUpClass()
        # Check that we can find the gpg binary and fail early if we can't
        if not shutil.which("gpg"):
            raise AssertionError("This test needs GnuPG")

        cls.gpg_dir = tempfile.mkdtemp(prefix="oeqa-signing-")

        cls.pub_key_path = os.path.join(cls.testlayer_path, 'files', 'signing', "key.pub")
        cls.secret_key_path = os.path.join(cls.testlayer_path, 'files', 'signing', "key.secret")

        runCmd('gpg --batch --homedir %s --import %s %s' % (cls.gpg_dir, cls.pub_key_path, cls.secret_key_path))

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.gpg_dir, ignore_errors=True)

    @OETestID(1362)
    def test_signing_packages(self):
        """
        Summary:     Test that packages can be signed in the package feed
        Expected:    Package should be signed with the correct key
        Expected:    Images can be created from signed packages
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        Author:      Alexander Kanavin <alexander.kanavin@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """
        import oe.packagedata

        package_classes = get_bb_var('PACKAGE_CLASSES')
        if 'package_rpm' not in package_classes:
            self.skipTest('This test requires RPM Packaging.')

        test_recipe = 'ed'

        feature = 'INHERIT += "sign_rpm"\n'
        feature += 'RPM_GPG_PASSPHRASE = "test123"\n'
        feature += 'RPM_GPG_NAME = "testuser"\n'
        feature += 'GPG_PATH = "%s"\n' % self.gpg_dir

        self.write_config(feature)

        bitbake('-c clean %s' % test_recipe)
        bitbake('-f -c package_write_rpm %s' % test_recipe)

        self.add_command_to_tearDown('bitbake -c clean %s' % test_recipe)

        needed_vars = ['PKGDATA_DIR', 'DEPLOY_DIR_RPM', 'PACKAGE_ARCH', 'STAGING_BINDIR_NATIVE']
        bb_vars = get_bb_vars(needed_vars, test_recipe)
        pkgdatadir = bb_vars['PKGDATA_DIR']
        pkgdata = oe.packagedata.read_pkgdatafile(pkgdatadir + "/runtime/ed")
        if 'PKGE' in pkgdata:
            pf = pkgdata['PN'] + "-" + pkgdata['PKGE'] + pkgdata['PKGV'] + '-' + pkgdata['PKGR']
        else:
            pf = pkgdata['PN'] + "-" + pkgdata['PKGV'] + '-' + pkgdata['PKGR']
        deploy_dir_rpm = bb_vars['DEPLOY_DIR_RPM']
        package_arch = bb_vars['PACKAGE_ARCH'].replace('-', '_')
        staging_bindir_native = bb_vars['STAGING_BINDIR_NATIVE']

        pkg_deploy = os.path.join(deploy_dir_rpm, package_arch, '.'.join((pf, package_arch, 'rpm')))

        # Use a temporary rpmdb
        rpmdb = tempfile.mkdtemp(prefix='oeqa-rpmdb')

        runCmd('%s/rpmkeys --define "_dbpath %s" --import %s' %
               (staging_bindir_native, rpmdb, self.pub_key_path))

        ret = runCmd('%s/rpmkeys --define "_dbpath %s" --checksig %s' %
                     (staging_bindir_native, rpmdb, pkg_deploy))
        # tmp/deploy/rpm/i586/ed-1.9-r0.i586.rpm: rsa sha1 md5 OK
        self.assertIn('rsa sha1 (md5) pgp md5 OK', ret.output, 'Package signed incorrectly.')
        shutil.rmtree(rpmdb)

        #Check that an image can be built from signed packages
        self.add_command_to_tearDown('bitbake -c clean core-image-minimal')
        bitbake('-c clean core-image-minimal')
        bitbake('core-image-minimal')


    @OETestID(1382)
    def test_signing_sstate_archive(self):
        """
        Summary:     Test that sstate archives can be signed
        Expected:    Package should be signed with the correct key
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        test_recipe = 'ed'

        builddir = os.environ.get('BUILDDIR')
        sstatedir = os.path.join(builddir, 'test-sstate')

        self.add_command_to_tearDown('bitbake -c clean %s' % test_recipe)
        self.add_command_to_tearDown('rm -rf %s' % sstatedir)

        feature = 'SSTATE_SIG_KEY ?= "testuser"\n'
        feature += 'SSTATE_SIG_PASSPHRASE ?= "test123"\n'
        feature += 'SSTATE_VERIFY_SIG ?= "1"\n'
        feature += 'GPG_PATH = "%s"\n' % self.gpg_dir
        feature += 'SSTATE_DIR = "%s"\n' % sstatedir
        # Any mirror might have partial sstate without .sig files, triggering failures
        feature += 'SSTATE_MIRRORS_forcevariable = ""\n'

        self.write_config(feature)

        bitbake('-c clean %s' % test_recipe)
        bitbake(test_recipe)

        recipe_sig = glob.glob(sstatedir + '/*/*:ed:*_package.tgz.sig')
        recipe_tgz = glob.glob(sstatedir + '/*/*:ed:*_package.tgz')

        self.assertEqual(len(recipe_sig), 1, 'Failed to find .sig file.')
        self.assertEqual(len(recipe_tgz), 1, 'Failed to find .tgz file.')

        ret = runCmd('gpg --homedir %s --verify %s %s' % (self.gpg_dir, recipe_sig[0], recipe_tgz[0]))
        # gpg: Signature made Thu 22 Oct 2015 01:45:09 PM EEST using RSA key ID 61EEFB30
        # gpg: Good signature from "testuser (nocomment) <testuser@email.com>"
        self.assertIn('gpg: Good signature from', ret.output, 'Package signed incorrectly.')


class LockedSignatures(OESelftestTestCase):

    @OETestID(1420)
    def test_locked_signatures(self):
        """
        Summary:     Test locked signature mechanism
        Expected:    Locked signatures will prevent task to run
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        test_recipe = 'ed'
        locked_sigs_file = 'locked-sigs.inc'

        self.add_command_to_tearDown('rm -f %s' % os.path.join(self.builddir, locked_sigs_file))

        bitbake(test_recipe)
        # Generate locked sigs include file
        bitbake('-S none %s' % test_recipe)

        feature = 'require %s\n' % locked_sigs_file
        feature += 'SIGGEN_LOCKEDSIGS_TASKSIG_CHECK = "warn"\n'
        self.write_config(feature)

        # Build a locked recipe
        bitbake(test_recipe)

        # Make a change that should cause the locked task signature to change
        recipe_append_file = test_recipe + '_' + get_bb_var('PV', test_recipe) + '.bbappend'
        recipe_append_path = os.path.join(self.testlayer_path, 'recipes-test', test_recipe, recipe_append_file)
        feature = 'SUMMARY += "test locked signature"\n'

        os.mkdir(os.path.join(self.testlayer_path, 'recipes-test', test_recipe))
        write_file(recipe_append_path, feature)

        self.add_command_to_tearDown('rm -rf %s' % os.path.join(self.testlayer_path, 'recipes-test', test_recipe))

        # Build the recipe again
        ret = bitbake(test_recipe)

        # Verify you get the warning and that the real task *isn't* run (i.e. the locked signature has worked)
        patt = r'WARNING: The %s:do_package sig is computed to be \S+, but the sig is locked to \S+ in SIGGEN_LOCKEDSIGS\S+' % test_recipe
        found_warn = re.search(patt, ret.output)

        self.assertIsNotNone(found_warn, "Didn't find the expected warning message. Output: %s" % ret.output)
