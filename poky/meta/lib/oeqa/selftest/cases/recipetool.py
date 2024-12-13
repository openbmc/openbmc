#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import errno
import os
import shutil
import tempfile
import urllib.parse

from oeqa.utils.commands import runCmd, bitbake, get_bb_var
from oeqa.utils.commands import get_bb_vars, create_temp_layer
from oeqa.selftest.cases import devtool

templayerdir = None

def setUpModule():
    global templayerdir
    templayerdir = tempfile.mkdtemp(prefix='recipetoolqa')
    create_temp_layer(templayerdir, 'selftestrecipetool')
    runCmd('bitbake-layers add-layer %s' % templayerdir)


def tearDownModule():
    runCmd('bitbake-layers remove-layer %s' % templayerdir, ignore_status=True)
    runCmd('rm -rf %s' % templayerdir)


def needTomllib(test):
    # This test require python 3.11 or above for the tomllib module or tomli module to be installed
    try:
        import tomllib
    except ImportError:
        try:
            import tomli
        except ImportError:
            test.skipTest('Test requires python 3.11 or above for tomllib module or tomli module')

class RecipetoolBase(devtool.DevtoolTestCase):

    def setUpLocal(self):
        super(RecipetoolBase, self).setUpLocal()
        self.templayerdir = templayerdir
        self.tempdir = tempfile.mkdtemp(prefix='recipetoolqa')
        self.track_for_cleanup(self.tempdir)
        self.testfile = os.path.join(self.tempdir, 'testfile')
        with open(self.testfile, 'w') as f:
            f.write('Test file\n')
        config = 'BBMASK += "meta-poky/recipes-core/base-files/base-files_%.bbappend"\n'
        self.append_config(config)

    def tearDownLocal(self):
        runCmd('rm -rf %s/recipes-*' % self.templayerdir)
        super(RecipetoolBase, self).tearDownLocal()

    def _try_recipetool_appendcmd(self, cmd, testrecipe, expectedfiles, expectedlines=None):
        result = runCmd(cmd)
        self.assertNotIn('Traceback', result.output)

        # Check the bbappend was created and applies properly
        recipefile = get_bb_var('FILE', testrecipe)
        bbappendfile = self._check_bbappend(testrecipe, recipefile, self.templayerdir)

        # Check the bbappend contents
        if expectedlines is not None:
            with open(bbappendfile, 'r') as f:
                self.assertEqual(expectedlines, f.readlines(), "Expected lines are not present in %s" % bbappendfile)

        # Check file was copied
        filesdir = os.path.join(os.path.dirname(bbappendfile), testrecipe)
        for expectedfile in expectedfiles:
            self.assertTrue(os.path.isfile(os.path.join(filesdir, expectedfile)), 'Expected file %s to be copied next to bbappend, but it wasn\'t' % expectedfile)

        # Check no other files created
        createdfiles = []
        for root, _, files in os.walk(filesdir):
            for f in files:
                createdfiles.append(os.path.relpath(os.path.join(root, f), filesdir))
        self.assertTrue(sorted(createdfiles), sorted(expectedfiles))

        return bbappendfile, result.output


class RecipetoolAppendTests(RecipetoolBase):

    @classmethod
    def setUpClass(cls):
        super(RecipetoolAppendTests, cls).setUpClass()
        # Ensure we have the right data in shlibs/pkgdata
        cls.logger.info('Running bitbake to generate pkgdata')
        bitbake('-c packagedata base-files coreutils busybox selftest-recipetool-appendfile')
        bb_vars = get_bb_vars(['COREBASE'])
        cls.corebase = bb_vars['COREBASE']

    def _try_recipetool_appendfile(self, testrecipe, destfile, newfile, options, expectedlines, expectedfiles):
        cmd = 'recipetool appendfile %s %s %s %s' % (self.templayerdir, destfile, newfile, options)
        return self._try_recipetool_appendcmd(cmd, testrecipe, expectedfiles, expectedlines)

    def _try_recipetool_appendfile_fail(self, destfile, newfile, checkerror):
        cmd = 'recipetool appendfile %s %s %s' % (self.templayerdir, destfile, newfile)
        result = runCmd(cmd, ignore_status=True)
        self.assertNotEqual(result.status, 0, 'Command "%s" should have failed but didn\'t' % cmd)
        self.assertNotIn('Traceback', result.output)
        for errorstr in checkerror:
            self.assertIn(errorstr, result.output)

    def test_recipetool_appendfile_basic(self):
        # Basic test
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                        '\n']
        _, output = self._try_recipetool_appendfile('base-files', '/etc/motd', self.testfile, '', expectedlines, ['motd'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_invalid(self):
        # Test some commands that should error
        self._try_recipetool_appendfile_fail('/etc/passwd', self.testfile, ['ERROR: /etc/passwd cannot be handled by this tool', 'useradd', 'extrausers'])
        self._try_recipetool_appendfile_fail('/etc/timestamp', self.testfile, ['ERROR: /etc/timestamp cannot be handled by this tool'])
        self._try_recipetool_appendfile_fail('/dev/console', self.testfile, ['ERROR: /dev/console cannot be handled by this tool'])

    def test_recipetool_appendfile_alternatives(self):
        lspath = '/bin/ls'
        dirname = "base_bindir"
        if "usrmerge" in get_bb_var('DISTRO_FEATURES'):
            lspath = '/usr/bin/ls'
            dirname = "bindir"

        # Now try with a file we know should be an alternative
        # (this is very much a fake example, but one we know is reliably an alternative)
        self._try_recipetool_appendfile_fail(lspath, self.testfile, ['ERROR: File %s is an alternative possibly provided by the following recipes:' % lspath, 'coreutils', 'busybox'])
        # Need a test file - should be executable
        testfile2 = os.path.join(self.corebase, 'oe-init-build-env')
        testfile2name = os.path.basename(testfile2)
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://%s"\n' % testfile2name,
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${%s}\n' % dirname,
                         '    install -m 0755 ${UNPACKDIR}/%s ${D}${%s}/ls\n' % (testfile2name, dirname),
                         '}\n']
        self._try_recipetool_appendfile('coreutils', lspath, testfile2, '-r coreutils', expectedlines, [testfile2name])
        # Now try bbappending the same file again, contents should not change
        bbappendfile, _ = self._try_recipetool_appendfile('coreutils', lspath, self.testfile, '-r coreutils', expectedlines, [testfile2name])
        # But file should have
        copiedfile = os.path.join(os.path.dirname(bbappendfile), 'coreutils', testfile2name)
        result = runCmd('diff -q %s %s' % (testfile2, copiedfile), ignore_status=True)
        self.assertNotEqual(result.status, 0, 'New file should have been copied but was not %s' % result.output)

    def test_recipetool_appendfile_binary(self):
        # Try appending a binary file
        # /bin/ls can be a symlink to /usr/bin/ls
        ls = os.path.realpath("/bin/ls")
        result = runCmd('recipetool appendfile %s /bin/ls %s -r coreutils' % (self.templayerdir, ls))
        self.assertIn('WARNING: ', result.output)
        self.assertIn('is a binary', result.output)

    def test_recipetool_appendfile_add(self):
        # Try arbitrary file add to a recipe
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${datadir}/something\n',
                         '}\n']
        self._try_recipetool_appendfile('netbase', '/usr/share/something', self.testfile, '-r netbase', expectedlines, ['testfile'])
        # Try adding another file, this time where the source file is executable
        # (so we're testing that, plus modifying an existing bbappend)
        testfile2 = os.path.join(self.corebase, 'oe-init-build-env')
        testfile2name = os.path.basename(testfile2)
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile \\\n',
                         '            file://%s \\\n' % testfile2name,
                         '            "\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${datadir}/something\n',
                         '    install -m 0755 ${UNPACKDIR}/%s ${D}${datadir}/scriptname\n' % testfile2name,
                         '}\n']
        self._try_recipetool_appendfile('netbase', '/usr/share/scriptname', testfile2, '-r netbase', expectedlines, ['testfile', testfile2name])

    def test_recipetool_appendfile_add_bindir(self):
        # Try arbitrary file add to a recipe, this time to a location such that should be installed as executable
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${bindir}\n',
                         '    install -m 0755 ${UNPACKDIR}/testfile ${D}${bindir}/selftest-recipetool-testbin\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('netbase', '/usr/bin/selftest-recipetool-testbin', self.testfile, '-r netbase', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_add_machine(self):
        # Try arbitrary file add to a recipe, this time to a location such that should be installed as executable
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'PACKAGE_ARCH = "${MACHINE_ARCH}"\n',
                         '\n',
                         'SRC_URI:append:mymachine = " file://testfile"\n',
                         '\n',
                         'do_install:append:mymachine() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${datadir}/something\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('netbase', '/usr/share/something', self.testfile, '-r netbase -m mymachine', expectedlines, ['mymachine/testfile'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_orig(self):
        # A file that's in SRC_URI and in do_install with the same name
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-orig', self.testfile, '', expectedlines, ['selftest-replaceme-orig'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_todir(self):
        # A file that's in SRC_URI and in do_install with destination directory rather than file
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-todir', self.testfile, '', expectedlines, ['selftest-replaceme-todir'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_renamed(self):
        # A file that's in SRC_URI with a different name to the destination file
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-renamed', self.testfile, '', expectedlines, ['file1'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_subdir(self):
        # A file that's in SRC_URI in a subdir
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${datadir}/selftest-replaceme-subdir\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-subdir', self.testfile, '', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_inst_glob(self):
        # A file that's in do_install as a glob
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-inst-globfile', self.testfile, '', expectedlines, ['selftest-replaceme-inst-globfile'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_inst_todir_glob(self):
        # A file that's in do_install as a glob with destination as a directory
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-inst-todir-globfile', self.testfile, '', expectedlines, ['selftest-replaceme-inst-todir-globfile'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_patch(self):
        # A file that's added by a patch in SRC_URI
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${sysconfdir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${sysconfdir}/selftest-replaceme-patched\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/etc/selftest-replaceme-patched', self.testfile, '', expectedlines, ['testfile'])
        for line in output.splitlines():
            if 'WARNING: ' in line:
                self.assertIn('add-file.patch', line, 'Unexpected warning found in output:\n%s' % line)
                break
        else:
            self.fail('Patch warning not found in output:\n%s' % output)

    def test_recipetool_appendfile_script(self):
        # Now, a file that's in SRC_URI but installed by a script (so no mention in do_install)
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${datadir}/selftest-replaceme-scripted\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-scripted', self.testfile, '', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_inst_func(self):
        # A file that's installed from a function called by do_install
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-inst-func', self.testfile, '', expectedlines, ['selftest-replaceme-inst-func'])
        self.assertNotIn('WARNING: ', output)

    def test_recipetool_appendfile_postinstall(self):
        # A file that's created by a postinstall script (and explicitly mentioned in it)
        # First try without specifying recipe
        self._try_recipetool_appendfile_fail('/usr/share/selftest-replaceme-postinst', self.testfile, ['File /usr/share/selftest-replaceme-postinst may be written out in a pre/postinstall script of the following recipes:', 'selftest-recipetool-appendfile'])
        # Now specify recipe
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install:append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${UNPACKDIR}/testfile ${D}${datadir}/selftest-replaceme-postinst\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-postinst', self.testfile, '-r selftest-recipetool-appendfile', expectedlines, ['testfile'])

    def test_recipetool_appendfile_extlayer(self):
        # Try creating a bbappend in a layer that's not in bblayers.conf and has a different structure
        exttemplayerdir = os.path.join(self.tempdir, 'extlayer')
        self._create_temp_layer(exttemplayerdir, False, 'oeselftestextlayer', recipepathspec='metadata/recipes/recipes-*/*')
        result = runCmd('recipetool appendfile %s /usr/share/selftest-replaceme-orig %s' % (exttemplayerdir, self.testfile))
        self.assertNotIn('Traceback', result.output)
        createdfiles = []
        for root, _, files in os.walk(exttemplayerdir):
            for f in files:
                createdfiles.append(os.path.relpath(os.path.join(root, f), exttemplayerdir))
        createdfiles.remove('conf/layer.conf')
        expectedfiles = ['metadata/recipes/recipes-test/selftest-recipetool-appendfile/selftest-recipetool-appendfile.bbappend',
                         'metadata/recipes/recipes-test/selftest-recipetool-appendfile/selftest-recipetool-appendfile/selftest-replaceme-orig']
        self.assertEqual(sorted(createdfiles), sorted(expectedfiles))

    def test_recipetool_appendfile_wildcard(self):

        def try_appendfile_wc(options):
            result = runCmd('recipetool appendfile %s /etc/profile %s %s' % (self.templayerdir, self.testfile, options))
            self.assertNotIn('Traceback', result.output)
            bbappendfile = None
            for root, _, files in os.walk(self.templayerdir):
                for f in files:
                    if f.endswith('.bbappend'):
                        bbappendfile = f
                        break
            if not bbappendfile:
                self.fail('No bbappend file created')
            runCmd('rm -rf %s/recipes-*' % self.templayerdir)
            return bbappendfile

        # Check without wildcard option
        recipefn = os.path.basename(get_bb_var('FILE', 'base-files'))
        filename = try_appendfile_wc('')
        self.assertEqual(filename, recipefn.replace('.bb', '.bbappend'))
        # Now check with wildcard option
        filename = try_appendfile_wc('-w')
        self.assertEqual(filename, recipefn.split('_')[0] + '_%.bbappend')


class RecipetoolCreateTests(RecipetoolBase):

    def test_recipetool_create(self):
        # Try adding a recipe
        tempsrc = os.path.join(self.tempdir, 'srctree')
        os.makedirs(tempsrc)
        recipefile = os.path.join(self.tempdir, 'logrotate_3.12.3.bb')
        srcuri = 'https://github.com/logrotate/logrotate/releases/download/3.12.3/logrotate-3.12.3.tar.xz'
        result = runCmd('recipetool create -o %s %s -x %s' % (recipefile, srcuri, tempsrc))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = 'GPL-2.0-only'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'
        checkvars['SRC_URI'] = 'https://github.com/logrotate/logrotate/releases/download/${PV}/logrotate-${PV}.tar.xz'
        checkvars['SRC_URI[sha256sum]'] = '2e6a401cac9024db2288297e3be1a8ab60e7401ba8e91225218aaf4a27e82a07'
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_recipetool_create_autotools(self):
        if 'x11' not in get_bb_var('DISTRO_FEATURES'):
            self.skipTest('Test requires x11 as distro feature')
        # Ensure we have the right data in shlibs/pkgdata
        bitbake('libpng pango libx11 libxext jpeg libcheck')
        # Try adding a recipe
        tempsrc = os.path.join(self.tempdir, 'srctree')
        os.makedirs(tempsrc)
        recipefile = os.path.join(self.tempdir, 'libmatchbox.bb')
        srcuri = 'git://git.yoctoproject.org/libmatchbox;protocol=https'
        result = runCmd(['recipetool', 'create', '-o', recipefile, srcuri + ";rev=9f7cf8895ae2d39c465c04cc78e918c157420269", '-x', tempsrc])
        self.assertTrue(os.path.isfile(recipefile), 'recipetool did not create recipe file; output:\n%s' % result.output)
        checkvars = {}
        checkvars['LICENSE'] = 'LGPL-2.1-only'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34'
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '1.11+git'
        checkvars['SRC_URI'] = srcuri + ';branch=master'
        checkvars['DEPENDS'] = set(['libcheck', 'libjpeg-turbo', 'libpng', 'libx11', 'libxext', 'pango'])
        inherits = ['autotools', 'pkgconfig']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_simple(self):
        # Try adding a recipe
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pv = '1.7.4.1'
        srcuri = 'http://www.dest-unreach.org/socat/download/Archive/socat-%s.tar.bz2' % pv
        result = runCmd('recipetool create %s -o %s' % (srcuri, temprecipe))
        dirlist = os.listdir(temprecipe)
        if len(dirlist) > 1:
            self.fail('recipetool created more than just one file; output:\n%s\ndirlist:\n%s' % (result.output, str(dirlist)))
        if len(dirlist) < 1 or not os.path.isfile(os.path.join(temprecipe, dirlist[0])):
            self.fail('recipetool did not create recipe file; output:\n%s\ndirlist:\n%s' % (result.output, str(dirlist)))
        self.assertEqual(dirlist[0], 'socat_%s.bb' % pv, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['LICENSE'] = set(['Unknown', 'GPL-2.0-only'])
        checkvars['LIC_FILES_CHKSUM'] = set(['file://COPYING.OpenSSL;md5=5c9bccc77f67a8328ef4ebaf468116f4', 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'])
        # We don't check DEPENDS since they are variable for this recipe depending on what's in the sysroot
        checkvars['S'] = None
        checkvars['SRC_URI'] = srcuri.replace(pv, '${PV}')
        inherits = ['autotools']
        self._test_recipe_contents(os.path.join(temprecipe, dirlist[0]), checkvars, inherits)

    def test_recipetool_create_cmake(self):
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        recipefile = os.path.join(temprecipe, 'taglib_1.11.1.bb')
        srcuri = 'http://taglib.github.io/releases/taglib-1.11.1.tar.gz'
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['LGPL-2.1-only', 'MPL-1.1-only'])
        checkvars['SRC_URI'] = 'http://taglib.github.io/releases/taglib-${PV}.tar.gz'
        checkvars['SRC_URI[sha256sum]'] = 'b6d1a5a610aae6ff39d93de5efd0fdc787aa9e9dc1e7026fa4c961b26563526b'
        checkvars['DEPENDS'] = set(['boost', 'zlib'])
        inherits = ['cmake']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_npm(self):
        collections = get_bb_var('BBFILE_COLLECTIONS').split()
        if "openembedded-layer" not in collections:
            self.skipTest("Test needs meta-oe for nodejs")

        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        recipefile = os.path.join(temprecipe, 'savoirfairelinux-node-server-example_1.0.0.bb')
        shrinkwrap = os.path.join(temprecipe, 'savoirfairelinux-node-server-example', 'npm-shrinkwrap.json')
        srcuri = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=1.0.0'
        result = runCmd('recipetool create -o %s \'%s\'' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        self.assertTrue(os.path.isfile(shrinkwrap))
        checkvars = {}
        checkvars['SUMMARY'] = 'Node Server Example'
        checkvars['HOMEPAGE'] = 'https://github.com/savoirfairelinux/node-server-example#readme'
        checkvars['LICENSE'] = 'BSD-3-Clause & ISC & MIT & Unknown'
        urls = []
        urls.append('npm://registry.npmjs.org/;package=@savoirfairelinux/node-server-example;version=${PV}')
        urls.append('npmsw://${THISDIR}/${BPN}/npm-shrinkwrap.json')
        checkvars['SRC_URI'] = set(urls)
        checkvars['S'] = '${WORKDIR}/npm'
        checkvars['LICENSE:${PN}'] = 'MIT'
        checkvars['LICENSE:${PN}-base64'] = 'Unknown'
        checkvars['LICENSE:${PN}-accepts'] = 'MIT'
        checkvars['LICENSE:${PN}-inherits'] = 'ISC'
        inherits = ['npm']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_github(self):
        # Basic test to see if github URL mangling works. Deliberately use an
        # older release of Meson at present so we don't need a toml parser.
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        recipefile = os.path.join(temprecipe, 'python3-meson_git.bb')
        srcuri = 'https://github.com/mesonbuild/meson;rev=0.52.1'
        cmd = ['recipetool', 'create', '-o', temprecipe, srcuri]
        result = runCmd(cmd)
        self.assertTrue(os.path.isfile(recipefile), msg="recipe %s not created for command %s, output %s" % (recipefile, " ".join(cmd), result.output))
        checkvars = {}
        checkvars['LICENSE'] = set(['Apache-2.0', "Unknown"])
        checkvars['SRC_URI'] = 'git://github.com/mesonbuild/meson;protocol=https;branch=0.52'
        inherits = ['setuptools3']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_setuptools(self):
        # Test creating python3 package from tarball (using setuptools3 class)
        # Use the --no-pypi switch to avoid creating a pypi enabled recipe and
        # and check the created recipe as if it was a more general tarball
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'python-magic'
        pv = '0.4.15'
        recipefile = os.path.join(temprecipe, '%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/84/30/80932401906eaf787f2e9bd86dc458f1d2e75b064b4c187341f29516945c/python-magic-%s.tar.gz' % pv
        result = runCmd('recipetool create --no-pypi -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=16a934f165e8c3245f241e77d401bb88'
        checkvars['SRC_URI'] = 'https://files.pythonhosted.org/packages/84/30/80932401906eaf787f2e9bd86dc458f1d2e75b064b4c187341f29516945c/python-magic-${PV}.tar.gz'
        checkvars['SRC_URI[sha256sum]'] = 'f3765c0f582d2dfc72c15f3b5a82aecfae9498bd29ca840d72f37d7bd38bfcd5'
        inherits = ['setuptools3']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_setuptools_pypi_tarball(self):
        # Test creating python3 package from tarball (using setuptools3 and pypi classes)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'python-magic'
        pv = '0.4.15'
        recipefile = os.path.join(temprecipe, '%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/84/30/80932401906eaf787f2e9bd86dc458f1d2e75b064b4c187341f29516945c/python-magic-%s.tar.gz' % pv
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=16a934f165e8c3245f241e77d401bb88'
        checkvars['SRC_URI[sha256sum]'] = 'f3765c0f582d2dfc72c15f3b5a82aecfae9498bd29ca840d72f37d7bd38bfcd5'
        checkvars['PYPI_PACKAGE'] = pn
        inherits = ['setuptools3', 'pypi']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_setuptools_pypi(self):
        # Test creating python3 package from pypi url (using setuptools3 and pypi classes)
        # Intentionnaly using setuptools3 class here instead of any of the pep517 class
        # to avoid the toml dependency and allows this test to run on host autobuilders
        # with older version of python
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'python-magic'
        pv = '0.4.15'
        recipefile = os.path.join(temprecipe, '%s_%s.bb' % (pn, pv))
        # First specify the required version in the url
        srcuri = 'https://pypi.org/project/%s/%s' % (pn, pv)
        runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=16a934f165e8c3245f241e77d401bb88'
        checkvars['SRC_URI[sha256sum]'] = 'f3765c0f582d2dfc72c15f3b5a82aecfae9498bd29ca840d72f37d7bd38bfcd5'
        checkvars['PYPI_PACKAGE'] = pn
        inherits = ['setuptools3', "pypi"]
        self._test_recipe_contents(recipefile, checkvars, inherits)

        # Now specify the version as a recipetool parameter
        runCmd('rm -rf %s' % recipefile)
        self.assertFalse(os.path.isfile(recipefile))
        srcuri = 'https://pypi.org/project/%s' % pn
        runCmd('recipetool create -o %s %s --version %s' % (temprecipe, srcuri, pv))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=16a934f165e8c3245f241e77d401bb88'
        checkvars['SRC_URI[sha256sum]'] = 'f3765c0f582d2dfc72c15f3b5a82aecfae9498bd29ca840d72f37d7bd38bfcd5'
        checkvars['PYPI_PACKAGE'] = pn
        inherits = ['setuptools3', "pypi"]
        self._test_recipe_contents(recipefile, checkvars, inherits)

        # Now, try to grab latest version of the package, so we cannot guess the name of the recipe,
        # unless hardcoding the latest version but it means we will need to update the test for each release,
        # so use a regexp
        runCmd('rm -rf %s' % recipefile)
        self.assertFalse(os.path.isfile(recipefile))
        recipefile_re = r'%s_(.*)\.bb' % pn
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        dirlist = os.listdir(temprecipe)
        if len(dirlist) > 1:
            self.fail('recipetool created more than just one file; output:\n%s\ndirlist:\n%s' % (result.output, str(dirlist)))
        if len(dirlist) < 1 or not os.path.isfile(os.path.join(temprecipe, dirlist[0])):
            self.fail('recipetool did not create recipe file; output:\n%s\ndirlist:\n%s' % (result.output, str(dirlist)))
        import re
        match = re.match(recipefile_re, dirlist[0])
        self.assertTrue(match)
        latest_pv = match.group(1)
        self.assertTrue(latest_pv != pv)
        recipefile = os.path.join(temprecipe, '%s_%s.bb' % (pn, latest_pv))
        # Do not check LIC_FILES_CHKSUM and SRC_URI checksum here to avoid having updating the test on each release
        checkvars = {}
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['PYPI_PACKAGE'] = pn
        inherits = ['setuptools3', "pypi"]
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_pep517_setuptools_build_meta(self):
        # This test require python 3.11 or above for the tomllib module or tomli module to be installed
        needTomllib(self)

        # Test creating python3 package from tarball (using setuptools.build_meta class)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'webcolors'
        pv = '1.13'
        recipefile = os.path.join(temprecipe, 'python3-%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/a1/fb/f95560c6a5d4469d9c49e24cf1b5d4d21ffab5608251c6020a965fb7791c/%s-%s.tar.gz' % (pn, pv)
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['SUMMARY'] = 'A library for working with the color formats defined by HTML and CSS.'
        checkvars['LICENSE'] = set(['BSD-3-Clause'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=702b1ef12cf66832a88f24c8f2ee9c19'
        checkvars['SRC_URI[sha256sum]'] = 'c225b674c83fa923be93d235330ce0300373d02885cef23238813b0d5668304a'
        inherits = ['python_setuptools_build_meta', 'pypi']

        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_pep517_poetry_core_masonry_api(self):
        # This test require python 3.11 or above for the tomllib module or tomli module to be installed
        needTomllib(self)

        # Test creating python3 package from tarball (using poetry.core.masonry.api class)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'iso8601'
        pv = '2.1.0'
        recipefile = os.path.join(temprecipe, 'python3-%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/b9/f3/ef59cee614d5e0accf6fd0cbba025b93b272e626ca89fb70a3e9187c5d15/%s-%s.tar.gz' % (pn, pv)
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['SUMMARY'] = 'Simple module to parse ISO 8601 dates'
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=aab31f2ef7ba214a5a341eaa47a7f367'
        checkvars['SRC_URI[sha256sum]'] = '6b1d3829ee8921c4301998c909f7829fa9ed3cbdac0d3b16af2d743aed1ba8df'
        inherits = ['python_poetry_core', 'pypi']

        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_pep517_flit_core_buildapi(self):
        # This test require python 3.11 or above for the tomllib module or tomli module to be installed
        needTomllib(self)

        # Test creating python3 package from tarball (using flit_core.buildapi class)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'typing-extensions'
        pv = '4.8.0'
        recipefile = os.path.join(temprecipe, 'python3-%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/1f/7a/8b94bb016069caa12fc9f587b28080ac33b4fbb8ca369b98bc0a4828543e/typing_extensions-%s.tar.gz' % pv
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['SUMMARY'] = 'Backported and Experimental Type Hints for Python 3.8+'
        checkvars['LICENSE'] = set(['PSF-2.0'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=fcf6b249c2641540219a727f35d8d2c2'
        checkvars['SRC_URI[sha256sum]'] = 'df8e4339e9cb77357558cbdbceca33c303714cf861d1eef15e1070055ae8b7ef'
        inherits = ['python_flit_core', 'pypi']

        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_pep517_hatchling(self):
        # This test require python 3.11 or above for the tomllib module or tomli module to be installed
        needTomllib(self)

        # Test creating python3 package from tarball (using hatchling class)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'jsonschema'
        pv = '4.19.1'
        recipefile = os.path.join(temprecipe, 'python3-%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/e4/43/087b24516db11722c8687e0caf0f66c7785c0b1c51b0ab951dfde924e3f5/jsonschema-%s.tar.gz' % pv
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['SUMMARY'] = 'An implementation of JSON Schema validation for Python'
        checkvars['HOMEPAGE'] = 'https://github.com/python-jsonschema/jsonschema'
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=7a60a81c146ec25599a3e1dabb8610a8 file://json/LICENSE;md5=9d4de43111d33570c8fe49b4cb0e01af'
        checkvars['SRC_URI[sha256sum]'] = 'ec84cc37cfa703ef7cd4928db24f9cb31428a5d0fa77747b8b51a847458e0bbf'
        inherits = ['python_hatchling', 'pypi']

        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_pep517_maturin(self):
        # This test require python 3.11 or above for the tomllib module or tomli module to be installed
        needTomllib(self)

        # Test creating python3 package from tarball (using maturin class)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'pydantic-core'
        pv = '2.14.5'
        recipefile = os.path.join(temprecipe, 'python3-%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/64/26/cffb93fe9c6b5a91c497f37fae14a4b073ecbc47fc36a9979c7aa888b245/pydantic_core-%s.tar.gz' % pv
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['HOMEPAGE'] = 'https://github.com/pydantic/pydantic-core'
        checkvars['LICENSE'] = set(['MIT'])
        checkvars['LIC_FILES_CHKSUM'] = 'file://LICENSE;md5=ab599c188b4a314d2856b3a55030c75c'
        checkvars['SRC_URI[sha256sum]'] = '6d30226dfc816dd0fdf120cae611dd2215117e4f9b124af8c60ab9093b6e8e71'
        inherits = ['python_maturin', 'pypi']

        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_python3_pep517_mesonpy(self):
        # This test require python 3.11 or above for the tomllib module or tomli module to be installed
        needTomllib(self)

        # Test creating python3 package from tarball (using mesonpy class)
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pn = 'siphash24'
        pv = '1.4'
        recipefile = os.path.join(temprecipe, 'python3-%s_%s.bb' % (pn, pv))
        srcuri = 'https://files.pythonhosted.org/packages/c2/32/b934a70592f314afcfa86c7f7e388804a8061be65b822e2aa07e573b6477/%s-%s.tar.gz' % (pn, pv)
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['SRC_URI[sha256sum]'] = '7fd65e39b2a7c8c4ddc3a168a687f4610751b0ac2ebb518783c0cdfc30bec4a0'
        inherits = ['python_mesonpy', 'pypi']

        self._test_recipe_contents(recipefile, checkvars, inherits)

    def test_recipetool_create_github_tarball(self):
        # Basic test to ensure github URL mangling doesn't apply to release tarballs.
        # Deliberately use an older release of Meson at present so we don't need a toml parser.
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pv = '0.52.1'
        recipefile = os.path.join(temprecipe, 'python3-meson_%s.bb' % pv)
        srcuri = 'https://github.com/mesonbuild/meson/releases/download/%s/meson-%s.tar.gz' % (pv, pv)
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['Apache-2.0'])
        checkvars['SRC_URI'] = 'https://github.com/mesonbuild/meson/releases/download/${PV}/meson-${PV}.tar.gz'
        inherits = ['setuptools3']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def _test_recipetool_create_git(self, srcuri, branch=None):
        # Basic test to check http git URL mangling works
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        name = srcuri.split(';')[0].split('/')[-1]
        recipefile = os.path.join(temprecipe, name + '_git.bb')
        options = ' -B %s' % branch if branch else ''
        result = runCmd('recipetool create -o %s%s "%s"' % (temprecipe, options, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['SRC_URI'] = srcuri
        for scheme in ['http', 'https']:
            if srcuri.startswith(scheme + ":"):
                checkvars['SRC_URI'] = 'git%s;protocol=%s' % (srcuri[len(scheme):], scheme)
        if ';branch=' not in srcuri:
            checkvars['SRC_URI'] += ';branch=' + (branch or 'master')
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_recipetool_create_git_http(self):
        self._test_recipetool_create_git('http://git.yoctoproject.org/git/matchbox-keyboard')

    def test_recipetool_create_git_srcuri_master(self):
        self._test_recipetool_create_git('git://git.yoctoproject.org/matchbox-keyboard;branch=master;protocol=https')

    def test_recipetool_create_git_srcuri_branch(self):
        self._test_recipetool_create_git('git://git.yoctoproject.org/matchbox-keyboard;branch=matchbox-keyboard-0-1;protocol=https')

    def test_recipetool_create_git_srcbranch(self):
        self._test_recipetool_create_git('git://git.yoctoproject.org/matchbox-keyboard;protocol=https', 'matchbox-keyboard-0-1')

    def _go_urifiy(self, url, version, modulepath = None, pathmajor = None, subdir = None):
        modulepath = ",path='%s'" % modulepath if len(modulepath) else ''
        pathmajor = ",pathmajor='%s'" % pathmajor if len(pathmajor) else ''
        subdir = ",subdir='%s'" % subdir if len(subdir) else ''
        return "${@go_src_uri('%s','%s'%s%s%s)}" % (url, version, modulepath, pathmajor, subdir)

    def test_recipetool_create_go(self):
        # Basic test to check go recipe generation
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)

        recipefile = os.path.join(temprecipe, 'edgex-go_git.bb')
        deps_require_file = os.path.join(temprecipe, 'edgex-go', 'edgex-go-modules.inc')
        lics_require_file = os.path.join(temprecipe, 'edgex-go', 'edgex-go-licenses.inc')
        modules_txt_file = os.path.join(temprecipe, 'edgex-go', 'modules.txt')

        srcuri = 'https://github.com/edgexfoundry/edgex-go.git'
        srcrev = "v3.0.0"
        srcbranch = "main"

        result = runCmd('recipetool create -o %s %s -S %s -B %s' % (temprecipe, srcuri, srcrev, srcbranch))

        self.maxDiff = None
        inherits = ['go-vendor']

        checkvars = {}
        checkvars['GO_IMPORT'] = "github.com/edgexfoundry/edgex-go"
        checkvars['SRC_URI'] = {'git://${GO_IMPORT};destsuffix=git/src/${GO_IMPORT};nobranch=1;name=${BPN};protocol=https',
                                'file://modules.txt'}
        checkvars['LIC_FILES_CHKSUM'] = {'file://src/${GO_IMPORT}/LICENSE;md5=8f8bc924cf73f6a32381e5fd4c58d603'}

        self.assertTrue(os.path.isfile(recipefile))
        self._test_recipe_contents(recipefile, checkvars, inherits)

        checkvars = {}
        checkvars['VENDORED_LIC_FILES_CHKSUM'] = set(
                 ['file://src/${GO_IMPORT}/vendor/github.com/Microsoft/go-winio/LICENSE;md5=69205ff73858f2c22b2ca135b557e8ef',
                 'file://src/${GO_IMPORT}/vendor/github.com/armon/go-metrics/LICENSE;md5=d2d77030c0183e3d1e66d26dc1f243be',
                 'file://src/${GO_IMPORT}/vendor/github.com/cenkalti/backoff/LICENSE;md5=1571d94433e3f3aa05267efd4dbea68b',
                 'file://src/${GO_IMPORT}/vendor/github.com/davecgh/go-spew/LICENSE;md5=c06795ed54b2a35ebeeb543cd3a73e56',
                 'file://src/${GO_IMPORT}/vendor/github.com/eclipse/paho.mqtt.golang/LICENSE;md5=dcdb33474b60c38efd27356d8f2edec7',
                 'file://src/${GO_IMPORT}/vendor/github.com/eclipse/paho.mqtt.golang/edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3',
                 'file://src/${GO_IMPORT}/vendor/github.com/edgexfoundry/go-mod-bootstrap/v3/LICENSE;md5=0d6dae39976133b2851fba4c1e1275ff',
                 'file://src/${GO_IMPORT}/vendor/github.com/edgexfoundry/go-mod-configuration/v3/LICENSE;md5=0d6dae39976133b2851fba4c1e1275ff',
                 'file://src/${GO_IMPORT}/vendor/github.com/edgexfoundry/go-mod-core-contracts/v3/LICENSE;md5=0d6dae39976133b2851fba4c1e1275ff',
                 'file://src/${GO_IMPORT}/vendor/github.com/edgexfoundry/go-mod-messaging/v3/LICENSE;md5=0d6dae39976133b2851fba4c1e1275ff',
                 'file://src/${GO_IMPORT}/vendor/github.com/edgexfoundry/go-mod-registry/v3/LICENSE;md5=0d6dae39976133b2851fba4c1e1275ff',
                 'file://src/${GO_IMPORT}/vendor/github.com/edgexfoundry/go-mod-secrets/v3/LICENSE;md5=f9fa2f4f8e0ef8cc7b5dd150963eb457',
                 'file://src/${GO_IMPORT}/vendor/github.com/fatih/color/LICENSE.md;md5=316e6d590bdcde7993fb175662c0dd5a',
                 'file://src/${GO_IMPORT}/vendor/github.com/fxamacker/cbor/v2/LICENSE;md5=827f5a2fa861382d35a3943adf9ebb86',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-jose/go-jose/v3/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-jose/go-jose/v3/json/LICENSE;md5=591778525c869cdde0ab5a1bf283cd81',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-kit/log/LICENSE;md5=5b7c15ad5fffe2ff6e9d58a6c161f082',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-logfmt/logfmt/LICENSE;md5=98e39517c38127f969de33057067091e',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-playground/locales/LICENSE;md5=3ccbda375ee345400ad1da85ba522301',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-playground/universal-translator/LICENSE;md5=2e2b21ef8f61057977d27c727c84bef1',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-playground/validator/v10/LICENSE;md5=a718a0f318d76f7c5d510cbae84f0b60',
                 'file://src/${GO_IMPORT}/vendor/github.com/go-redis/redis/v7/LICENSE;md5=58103aa5ea1ee9b7a369c9c4a95ef9b5',
                 'file://src/${GO_IMPORT}/vendor/github.com/golang/protobuf/LICENSE;md5=939cce1ec101726fa754e698ac871622',
                 'file://src/${GO_IMPORT}/vendor/github.com/gomodule/redigo/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93',
                 'file://src/${GO_IMPORT}/vendor/github.com/google/uuid/LICENSE;md5=88073b6dd8ec00fe09da59e0b6dfded1',
                 'file://src/${GO_IMPORT}/vendor/github.com/gorilla/mux/LICENSE;md5=33fa1116c45f9e8de714033f99edde13',
                 'file://src/${GO_IMPORT}/vendor/github.com/gorilla/websocket/LICENSE;md5=c007b54a1743d596f46b2748d9f8c044',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/consul/api/LICENSE;md5=b8a277a612171b7526e9be072f405ef4',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/errwrap/LICENSE;md5=b278a92d2c1509760384428817710378',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-cleanhttp/LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-hclog/LICENSE;md5=ec7f605b74b9ad03347d0a93a5cc7eb8',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-immutable-radix/LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-multierror/LICENSE;md5=d44fdeb607e2d2614db9464dbedd4094',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/go-rootcerts/LICENSE;md5=65d26fcc2f35ea6a181ac777e42db1ea',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/golang-lru/LICENSE;md5=f27a50d2e878867827842f2c60e30bfc',
                 'file://src/${GO_IMPORT}/vendor/github.com/hashicorp/serf/LICENSE;md5=b278a92d2c1509760384428817710378',
                 'file://src/${GO_IMPORT}/vendor/github.com/leodido/go-urn/LICENSE;md5=8f50db5538ec1148a9b3d14ed96c3418',
                 'file://src/${GO_IMPORT}/vendor/github.com/mattn/go-colorable/LICENSE;md5=24ce168f90aec2456a73de1839037245',
                 'file://src/${GO_IMPORT}/vendor/github.com/mattn/go-isatty/LICENSE;md5=f509beadd5a11227c27b5d2ad6c9f2c6',
                 'file://src/${GO_IMPORT}/vendor/github.com/mitchellh/consulstructure/LICENSE;md5=96ada10a9e51c98c4656f2cede08c673',
                 'file://src/${GO_IMPORT}/vendor/github.com/mitchellh/copystructure/LICENSE;md5=56da355a12d4821cda57b8f23ec34bc4',
                 'file://src/${GO_IMPORT}/vendor/github.com/mitchellh/go-homedir/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd',
                 'file://src/${GO_IMPORT}/vendor/github.com/mitchellh/mapstructure/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd',
                 'file://src/${GO_IMPORT}/vendor/github.com/mitchellh/reflectwalk/LICENSE;md5=3f7765c3d4f58e1f84c4313cecf0f5bd',
                 'file://src/${GO_IMPORT}/vendor/github.com/nats-io/nats.go/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327',
                 'file://src/${GO_IMPORT}/vendor/github.com/nats-io/nkeys/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327',
                 'file://src/${GO_IMPORT}/vendor/github.com/nats-io/nuid/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327',
                 'file://src/${GO_IMPORT}/vendor/github.com/pmezard/go-difflib/LICENSE;md5=e9a2ebb8de779a07500ddecca806145e',
                 'file://src/${GO_IMPORT}/vendor/github.com/rcrowley/go-metrics/LICENSE;md5=1bdf5d819f50f141366dabce3be1460f',
                 'file://src/${GO_IMPORT}/vendor/github.com/spiffe/go-spiffe/v2/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327',
                 'file://src/${GO_IMPORT}/vendor/github.com/stretchr/objx/LICENSE;md5=d023fd31d3ca39ec61eec65a91732735',
                 'file://src/${GO_IMPORT}/vendor/github.com/stretchr/testify/LICENSE;md5=188f01994659f3c0d310612333d2a26f',
                 'file://src/${GO_IMPORT}/vendor/github.com/x448/float16/LICENSE;md5=de8f8e025d57fe7ee0b67f30d571323b',
                 'file://src/${GO_IMPORT}/vendor/github.com/zeebo/errs/LICENSE;md5=84914ab36fc0eb48edbaa53e66e8d326',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/crypto/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/mod/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/net/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/sync/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/sys/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/text/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/golang.org/x/tools/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707',
                 'file://src/${GO_IMPORT}/vendor/google.golang.org/genproto/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57',
                 'file://src/${GO_IMPORT}/vendor/google.golang.org/grpc/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57',
                 'file://src/${GO_IMPORT}/vendor/google.golang.org/protobuf/LICENSE;md5=02d4002e9171d41a8fad93aa7faf3956',
                 'file://src/${GO_IMPORT}/vendor/gopkg.in/eapache/queue.v1/LICENSE;md5=1bfd4408d3de090ef6b908b0cc45a316',
                 'file://src/${GO_IMPORT}/vendor/gopkg.in/yaml.v3/LICENSE;md5=3c91c17266710e16afdbb2b6d15c761c'])

        self.assertTrue(os.path.isfile(lics_require_file))
        self._test_recipe_contents(lics_require_file, checkvars, [])

        dependencies = \
            [   ('github.com/eclipse/paho.mqtt.golang','v1.4.2', '', '', ''),
                ('github.com/edgexfoundry/go-mod-bootstrap','v3.0.1','github.com/edgexfoundry/go-mod-bootstrap/v3','/v3', ''),
                ('github.com/edgexfoundry/go-mod-configuration','v3.0.0','github.com/edgexfoundry/go-mod-configuration/v3','/v3', ''),
                ('github.com/edgexfoundry/go-mod-core-contracts','v3.0.0','github.com/edgexfoundry/go-mod-core-contracts/v3','/v3', ''),
                ('github.com/edgexfoundry/go-mod-messaging','v3.0.0','github.com/edgexfoundry/go-mod-messaging/v3','/v3', ''),
                ('github.com/edgexfoundry/go-mod-secrets','v3.0.1','github.com/edgexfoundry/go-mod-secrets/v3','/v3', ''),
                ('github.com/fxamacker/cbor','v2.4.0','github.com/fxamacker/cbor/v2','/v2', ''),
                ('github.com/gomodule/redigo','v1.8.9', '', '', ''),
                ('github.com/google/uuid','v1.3.0', '', '', ''),
                ('github.com/gorilla/mux','v1.8.0', '', '', ''),
                ('github.com/rcrowley/go-metrics','v0.0.0-20201227073835-cf1acfcdf475', '', '', ''),
                ('github.com/spiffe/go-spiffe','v2.1.4','github.com/spiffe/go-spiffe/v2','/v2', ''),
                ('github.com/stretchr/testify','v1.8.2', '', '', ''),
                ('go.googlesource.com/crypto','v0.8.0','golang.org/x/crypto', '', ''),
                ('gopkg.in/eapache/queue.v1','v1.1.0', '', '', ''),
                ('gopkg.in/yaml.v3','v3.0.1', '', '', ''),
                ('github.com/microsoft/go-winio','v0.6.0','github.com/Microsoft/go-winio', '', ''),
                ('github.com/hashicorp/go-metrics','v0.3.10','github.com/armon/go-metrics', '', ''),
                ('github.com/cenkalti/backoff','v2.2.1+incompatible', '', '', ''),
                ('github.com/davecgh/go-spew','v1.1.1', '', '', ''),
                ('github.com/edgexfoundry/go-mod-registry','v3.0.0','github.com/edgexfoundry/go-mod-registry/v3','/v3', ''),
                ('github.com/fatih/color','v1.9.0', '', '', ''),
                ('github.com/go-jose/go-jose','v3.0.0','github.com/go-jose/go-jose/v3','/v3', ''),
                ('github.com/go-kit/log','v0.2.1', '', '', ''),
                ('github.com/go-logfmt/logfmt','v0.5.1', '', '', ''),
                ('github.com/go-playground/locales','v0.14.1', '', '', ''),
                ('github.com/go-playground/universal-translator','v0.18.1', '', '', ''),
                ('github.com/go-playground/validator','v10.13.0','github.com/go-playground/validator/v10','/v10', ''),
                ('github.com/go-redis/redis','v7.3.0','github.com/go-redis/redis/v7','/v7', ''),
                ('github.com/golang/protobuf','v1.5.2', '', '', ''),
                ('github.com/gorilla/websocket','v1.4.2', '', '', ''),
                ('github.com/hashicorp/consul','v1.20.0','github.com/hashicorp/consul/api', '', 'api'),
                ('github.com/hashicorp/errwrap','v1.0.0', '', '', ''),
                ('github.com/hashicorp/go-cleanhttp','v0.5.1', '', '', ''),
                ('github.com/hashicorp/go-hclog','v0.14.1', '', '', ''),
                ('github.com/hashicorp/go-immutable-radix','v1.3.0', '', '', ''),
                ('github.com/hashicorp/go-multierror','v1.1.1', '', '', ''),
                ('github.com/hashicorp/go-rootcerts','v1.0.2', '', '', ''),
                ('github.com/hashicorp/golang-lru','v0.5.4', '', '', ''),
                ('github.com/hashicorp/serf','v0.10.1', '', '', ''),
                ('github.com/leodido/go-urn','v1.2.3', '', '', ''),
                ('github.com/mattn/go-colorable','v0.1.12', '', '', ''),
                ('github.com/mattn/go-isatty','v0.0.14', '', '', ''),
                ('github.com/mitchellh/consulstructure','v0.0.0-20190329231841-56fdc4d2da54', '', '', ''),
                ('github.com/mitchellh/copystructure','v1.2.0', '', '', ''),
                ('github.com/mitchellh/go-homedir','v1.1.0', '', '', ''),
                ('github.com/mitchellh/mapstructure','v1.5.0', '', '', ''),
                ('github.com/mitchellh/reflectwalk','v1.0.2', '', '', ''),
                ('github.com/nats-io/nats.go','v1.25.0', '', '', ''),
                ('github.com/nats-io/nkeys','v0.4.4', '', '', ''),
                ('github.com/nats-io/nuid','v1.0.1', '', '', ''),
                ('github.com/pmezard/go-difflib','v1.0.0', '', '', ''),
                ('github.com/stretchr/objx','v0.5.0', '', '', ''),
                ('github.com/x448/float16','v0.8.4', '', '', ''),
                ('github.com/zeebo/errs','v1.3.0', '', '', ''),
                ('go.googlesource.com/mod','v0.8.0','golang.org/x/mod', '', ''),
                ('go.googlesource.com/net','v0.9.0','golang.org/x/net', '', ''),
                ('go.googlesource.com/sync','v0.1.0','golang.org/x/sync', '', ''),
                ('go.googlesource.com/sys','v0.7.0','golang.org/x/sys', '', ''),
                ('go.googlesource.com/text','v0.9.0','golang.org/x/text', '', ''),
                ('go.googlesource.com/tools','v0.6.0','golang.org/x/tools', '', ''),
                ('github.com/googleapis/go-genproto','v0.0.0-20230223222841-637eb2293923','google.golang.org/genproto', '', ''),
                ('github.com/grpc/grpc-go','v1.53.0','google.golang.org/grpc', '', ''),
                ('go.googlesource.com/protobuf','v1.28.1','google.golang.org/protobuf', '', ''),
            ]

        src_uri = set()
        for d in dependencies:
            src_uri.add(self._go_urifiy(*d))

        checkvars = {}
        checkvars['GO_DEPENDENCIES_SRC_URI'] = src_uri

        self.assertTrue(os.path.isfile(deps_require_file))
        self._test_recipe_contents(deps_require_file, checkvars, [])

    def test_recipetool_create_go_replace_modules(self):
        # Check handling of replaced modules
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)

        recipefile = os.path.join(temprecipe, 'openapi-generator_git.bb')
        deps_require_file = os.path.join(temprecipe, 'openapi-generator', 'go-modules.inc')
        lics_require_file = os.path.join(temprecipe, 'openapi-generator', 'go-licenses.inc')
        modules_txt_file = os.path.join(temprecipe, 'openapi-generator', 'modules.txt')

        srcuri = 'https://github.com/OpenAPITools/openapi-generator.git'
        srcrev = "v7.2.0"
        srcbranch = "master"
        srcsubdir = "samples/openapi3/client/petstore/go"

        result = runCmd('recipetool create -o %s %s -S %s -B %s --src-subdir %s' % (temprecipe, srcuri, srcrev, srcbranch, srcsubdir))

        self.maxDiff = None
        inherits = ['go-vendor']

        checkvars = {}
        checkvars['GO_IMPORT'] = "github.com/OpenAPITools/openapi-generator/samples/openapi3/client/petstore/go"
        checkvars['SRC_URI'] = {'git://${GO_IMPORT};destsuffix=git/src/${GO_IMPORT};nobranch=1;name=${BPN};protocol=https',
                                'file://modules.txt'}

        self.assertNotIn('Traceback', result.output)
        self.assertIn('No license file was detected for the main module', result.output)
        self.assertTrue(os.path.isfile(recipefile))
        self._test_recipe_contents(recipefile, checkvars, inherits)

        # make sure that dependencies don't mention local directory ./go-petstore
        dependencies = \
            [   ('github.com/stretchr/testify','v1.8.4', '', '', ''),
                ('go.googlesource.com/oauth2','v0.10.0','golang.org/x/oauth2', '', ''),
                ('github.com/davecgh/go-spew','v1.1.1', '', '', ''),
                ('github.com/golang/protobuf','v1.5.3', '', '', ''),
                ('github.com/kr/pretty','v0.3.0', '', '', ''),
                ('github.com/pmezard/go-difflib','v1.0.0', '', '', ''),
                ('github.com/rogpeppe/go-internal','v1.9.0', '', '', ''),
                ('go.googlesource.com/net','v0.12.0','golang.org/x/net', '', ''),
                ('github.com/golang/appengine','v1.6.7','google.golang.org/appengine', '', ''),
                ('go.googlesource.com/protobuf','v1.31.0','google.golang.org/protobuf', '', ''),
                ('gopkg.in/check.v1','v1.0.0-20201130134442-10cb98267c6c', '', '', ''),
                ('gopkg.in/yaml.v3','v3.0.1', '', '', ''),
            ]

        src_uri = set()
        for d in dependencies:
            src_uri.add(self._go_urifiy(*d))

        checkvars = {}
        checkvars['GO_DEPENDENCIES_SRC_URI'] = src_uri

        self.assertTrue(os.path.isfile(deps_require_file))
        self._test_recipe_contents(deps_require_file, checkvars, [])

class RecipetoolTests(RecipetoolBase):

    @classmethod
    def setUpClass(cls):
        import sys

        super(RecipetoolTests, cls).setUpClass()
        bb_vars = get_bb_vars(['BBPATH'])
        cls.bbpath = bb_vars['BBPATH']
        libpath = os.path.join(get_bb_var('COREBASE'), 'scripts', 'lib', 'recipetool')
        sys.path.insert(0, libpath)

    def _copy_file_with_cleanup(self, srcfile, basedstdir, *paths):
        dstdir = basedstdir
        self.assertTrue(os.path.exists(dstdir))
        for p in paths:
            dstdir = os.path.join(dstdir, p)
            if not os.path.exists(dstdir):
                try:
                    os.makedirs(dstdir)
                except PermissionError:
                    return False
                except OSError as e:
                    if e.errno == errno.EROFS:
                        return False
                    else:
                        raise e
                if p == "lib":
                    # Can race with other tests
                    self.add_command_to_tearDown('rmdir --ignore-fail-on-non-empty %s' % dstdir)
                else:
                    self.track_for_cleanup(dstdir)
        dstfile = os.path.join(dstdir, os.path.basename(srcfile))
        if srcfile != dstfile:
            try:
                shutil.copy(srcfile, dstfile)
            except PermissionError:
                return False
            self.track_for_cleanup(dstfile)
        return True

    def test_recipetool_load_plugin(self):
        """Test that recipetool loads only the first found plugin in BBPATH."""

        recipetool = runCmd("which recipetool")
        fromname = runCmd("recipetool --quiet pluginfile")
        srcfile = fromname.output
        searchpath = self.bbpath.split(':') + [os.path.dirname(recipetool.output)]
        plugincontent = []
        with open(srcfile) as fh:
            plugincontent = fh.readlines()
        try:
            self.assertIn('meta-selftest', srcfile, 'wrong bbpath plugin found')
            searchpath = [
                path for path in searchpath
                if self._copy_file_with_cleanup(srcfile, path, 'lib', 'recipetool')
            ]
            result = runCmd("recipetool --quiet count")
            self.assertEqual(result.output, '1')
            result = runCmd("recipetool --quiet multiloaded")
            self.assertEqual(result.output, "no")
            for path in searchpath:
                result = runCmd("recipetool --quiet bbdir")
                self.assertEqual(os.path.realpath(result.output), os.path.realpath(path))
                os.unlink(os.path.join(result.output, 'lib', 'recipetool', 'bbpath.py'))
        finally:
            with open(srcfile, 'w') as fh:
                fh.writelines(plugincontent)

    def test_recipetool_handle_license_vars(self):
        from create import handle_license_vars
        from unittest.mock import Mock

        commonlicdir = get_bb_var('COMMON_LICENSE_DIR')

        class DataConnectorCopy(bb.tinfoil.TinfoilDataStoreConnector):
            pass

        d = DataConnectorCopy
        d.getVar = Mock(return_value=commonlicdir)
        d.expand = Mock(side_effect=lambda x: x)

        srctree = tempfile.mkdtemp(prefix='recipetoolqa')
        self.track_for_cleanup(srctree)

        # Multiple licenses
        licenses = ['MIT', 'ISC', 'BSD-3-Clause', 'Apache-2.0']
        for licence in licenses:
            shutil.copy(os.path.join(commonlicdir, licence), os.path.join(srctree, 'LICENSE.' + licence))
        # Duplicate license
        shutil.copy(os.path.join(commonlicdir, 'MIT'), os.path.join(srctree, 'LICENSE'))

        extravalues = {
            # Duplicate and missing licenses
            'LICENSE': 'Zlib & BSD-2-Clause & Zlib',
            'LIC_FILES_CHKSUM': [
                'file://README.md;md5=0123456789abcdef0123456789abcd'
            ]
        }
        lines_before = []
        handled = []
        licvalues = handle_license_vars(srctree, lines_before, handled, extravalues, d)
        expected_lines_before = [
            '# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is',
            '# your responsibility to verify that the values are complete and correct.',
            '# NOTE: Original package / source metadata indicates license is: BSD-2-Clause & Zlib',
            '#',
            '# NOTE: multiple licenses have been detected; they have been separated with &',
            '# in the LICENSE value for now since it is a reasonable assumption that all',
            '# of the licenses apply. If instead there is a choice between the multiple',
            '# licenses then you should change the value to separate the licenses with |',
            '# instead of &. If there is any doubt, check the accompanying documentation',
            '# to determine which situation is applicable.',
            'LICENSE = "Apache-2.0 & BSD-2-Clause & BSD-3-Clause & ISC & MIT & Zlib"',
            'LIC_FILES_CHKSUM = "file://LICENSE;md5=0835ade698e0bcf8506ecda2f7b4f302 \\\n'
            '                    file://LICENSE.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10 \\\n'
            '                    file://LICENSE.BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9 \\\n'
            '                    file://LICENSE.ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d \\\n'
            '                    file://LICENSE.MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \\\n'
            '                    file://README.md;md5=0123456789abcdef0123456789abcd"',
            ''
        ]
        self.assertEqual(lines_before, expected_lines_before)
        expected_licvalues = [
            ('MIT', 'LICENSE', '0835ade698e0bcf8506ecda2f7b4f302'),
            ('Apache-2.0', 'LICENSE.Apache-2.0', '89aea4e17d99a7cacdbeed46a0096b10'),
            ('BSD-3-Clause', 'LICENSE.BSD-3-Clause', '550794465ba0ec5312d6919e203a55f9'),
            ('ISC', 'LICENSE.ISC', 'f3b90e78ea0cffb20bf5cca7947a896d'),
            ('MIT', 'LICENSE.MIT', '0835ade698e0bcf8506ecda2f7b4f302')
        ]
        self.assertEqual(handled, [('license', expected_licvalues)])
        self.assertEqual(extravalues, {})
        self.assertEqual(licvalues, expected_licvalues)


    def test_recipetool_split_pkg_licenses(self):
        from create import split_pkg_licenses
        licvalues = [
            # Duplicate licenses
            ('BSD-2-Clause', 'x/COPYING', None),
            ('BSD-2-Clause', 'x/LICENSE', None),
            # Multiple licenses
            ('MIT', 'x/a/LICENSE.MIT', None),
            ('ISC', 'x/a/LICENSE.ISC', None),
            # Alternative licenses
            ('(MIT | ISC)', 'x/b/LICENSE', None),
            # Alternative licenses without brackets
            ('MIT | BSD-2-Clause', 'x/c/LICENSE', None),
            # Multi licenses with alternatives
            ('MIT', 'x/d/COPYING', None),
            ('MIT | BSD-2-Clause', 'x/d/LICENSE', None),
            # Multi licenses with alternatives and brackets
            ('Apache-2.0 & ((MIT | ISC) & BSD-3-Clause)', 'x/e/LICENSE', None)
        ]
        packages = {
            '${PN}': '',
            'a': 'x/a',
            'b': 'x/b',
            'c': 'x/c',
            'd': 'x/d',
            'e': 'x/e',
            'f': 'x/f',
            'g': 'x/g',
        }
        fallback_licenses = {
            # Ignored
            'a': 'BSD-3-Clause',
            # Used
            'f': 'BSD-3-Clause'
        }
        outlines = []
        outlicenses = split_pkg_licenses(licvalues, packages, outlines, fallback_licenses)
        expected_outlicenses = {
            '${PN}': ['BSD-2-Clause'],
            'a': ['ISC', 'MIT'],
            'b': ['(ISC | MIT)'],
            'c': ['(BSD-2-Clause | MIT)'],
            'd': ['(BSD-2-Clause | MIT)', 'MIT'],
            'e': ['(ISC | MIT)', 'Apache-2.0', 'BSD-3-Clause'],
            'f': ['BSD-3-Clause'],
            'g': ['Unknown']
        }
        self.assertEqual(outlicenses, expected_outlicenses)
        expected_outlines = [
            'LICENSE:${PN} = "BSD-2-Clause"',
            'LICENSE:a = "ISC & MIT"',
            'LICENSE:b = "(ISC | MIT)"',
            'LICENSE:c = "(BSD-2-Clause | MIT)"',
            'LICENSE:d = "(BSD-2-Clause | MIT) & MIT"',
            'LICENSE:e = "(ISC | MIT) & Apache-2.0 & BSD-3-Clause"',
            'LICENSE:f = "BSD-3-Clause"',
            'LICENSE:g = "Unknown"'
        ]
        self.assertEqual(outlines, expected_outlines)


class RecipetoolAppendsrcBase(RecipetoolBase):
    def _try_recipetool_appendsrcfile(self, testrecipe, newfile, destfile, options, expectedlines, expectedfiles):
        cmd = 'recipetool appendsrcfile %s %s %s %s %s' % (options, self.templayerdir, testrecipe, newfile, destfile)
        return self._try_recipetool_appendcmd(cmd, testrecipe, expectedfiles, expectedlines)

    def _try_recipetool_appendsrcfiles(self, testrecipe, newfiles, expectedlines=None, expectedfiles=None, destdir=None, options=''):

        if destdir:
            options += ' -D %s' % destdir

        if expectedfiles is None:
            expectedfiles = [os.path.basename(f) for f in newfiles]

        cmd = 'recipetool appendsrcfiles %s %s %s %s' % (options, self.templayerdir, testrecipe, ' '.join(newfiles))
        return self._try_recipetool_appendcmd(cmd, testrecipe, expectedfiles, expectedlines)

    def _try_recipetool_appendsrcfile_fail(self, testrecipe, newfile, destfile, checkerror):
        cmd = 'recipetool appendsrcfile %s %s %s %s' % (self.templayerdir, testrecipe, newfile, destfile or '')
        result = runCmd(cmd, ignore_status=True)
        self.assertNotEqual(result.status, 0, 'Command "%s" should have failed but didn\'t' % cmd)
        self.assertNotIn('Traceback', result.output)
        for errorstr in checkerror:
            self.assertIn(errorstr, result.output)

    @staticmethod
    def _get_first_file_uri(recipe):
        '''Return the first file:// in SRC_URI for the specified recipe.'''
        src_uri = get_bb_var('SRC_URI', recipe).split()
        for uri in src_uri:
            p = urllib.parse.urlparse(uri)
            if p.scheme == 'file':
                return p.netloc + p.path, uri

    def _test_appendsrcfile(self, testrecipe, filename=None, destdir=None, has_src_uri=True, srcdir=None, newfile=None, remove=None, machine=None , options=''):
        if newfile is None:
            newfile = self.testfile

        if srcdir:
            if destdir:
                expected_subdir = os.path.join(srcdir, destdir)
            else:
                expected_subdir = srcdir
        else:
            options += " -W"
            expected_subdir = destdir

        if filename:
            if destdir:
                destpath = os.path.join(destdir, filename)
            else:
                destpath = filename
        else:
            filename = os.path.basename(newfile)
            if destdir:
                destpath = destdir + os.sep
            else:
                destpath = '.' + os.sep

        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']

        override = ""
        if machine:
            options += ' -m %s' % machine
            override = ':append:%s' % machine
            expectedlines.extend(['PACKAGE_ARCH = "${MACHINE_ARCH}"\n',
                                  '\n'])

        if remove:
            for entry in remove:
                if machine:
                    entry_remove_line = 'SRC_URI:remove:%s = " %s"\n' % (machine, entry)
                else:
                    entry_remove_line = 'SRC_URI:remove = "%s"\n' % entry

                expectedlines.extend([entry_remove_line,
                                       '\n'])

        if has_src_uri:
            uri = 'file://%s' % filename
            if expected_subdir:
                uri += ';subdir=%s' % expected_subdir
            if machine:
                src_uri_line = 'SRC_URI%s = " %s"\n' % (override, uri)
            else:
                src_uri_line = 'SRC_URI += "%s"\n' % uri

            expectedlines.extend([src_uri_line, '\n'])

        with open("/tmp/tmp.txt", "w") as file:
            print(expectedlines, file=file)

        if machine:
            filename = '%s/%s' % (machine, filename)

        return self._try_recipetool_appendsrcfile(testrecipe, newfile, destpath, options, expectedlines, [filename])

    def _test_appendsrcfiles(self, testrecipe, newfiles, expectedfiles=None, destdir=None, options=''):
        if expectedfiles is None:
            expectedfiles = [os.path.basename(n) for n in newfiles]

        self._try_recipetool_appendsrcfiles(testrecipe, newfiles, expectedfiles=expectedfiles, destdir=destdir, options=options)

        bb_vars = get_bb_vars(['SRC_URI', 'FILE', 'FILESEXTRAPATHS'], testrecipe)
        src_uri = bb_vars['SRC_URI'].split()
        for f in expectedfiles:
            if destdir:
                self.assertIn('file://%s;subdir=%s' % (f, destdir), src_uri)
            else:
                self.assertIn('file://%s' % f, src_uri)

        recipefile = bb_vars['FILE']
        bbappendfile = self._check_bbappend(testrecipe, recipefile, self.templayerdir)
        filesdir = os.path.join(os.path.dirname(bbappendfile), testrecipe)
        filesextrapaths = bb_vars['FILESEXTRAPATHS'].split(':')
        self.assertIn(filesdir, filesextrapaths)




class RecipetoolAppendsrcTests(RecipetoolAppendsrcBase):

    def test_recipetool_appendsrcfile_basic(self):
        self._test_appendsrcfile('base-files', 'a-file')

    def test_recipetool_appendsrcfile_basic_wildcard(self):
        testrecipe = 'base-files'
        self._test_appendsrcfile(testrecipe, 'a-file', options='-w')
        recipefile = get_bb_var('FILE', testrecipe)
        bbappendfile = self._check_bbappend(testrecipe, recipefile, self.templayerdir)
        self.assertEqual(os.path.basename(bbappendfile), '%s_%%.bbappend' % testrecipe)

    def test_recipetool_appendsrcfile_subdir_basic(self):
        self._test_appendsrcfile('base-files', 'a-file', 'tmp')

    def test_recipetool_appendsrcfile_subdir_basic_dirdest(self):
        self._test_appendsrcfile('base-files', destdir='tmp')

    def test_recipetool_appendsrcfile_srcdir_basic(self):
        testrecipe = 'bash'
        bb_vars = get_bb_vars(['S', 'WORKDIR'], testrecipe)
        srcdir = bb_vars['S']
        workdir = bb_vars['WORKDIR']
        subdir = os.path.relpath(srcdir, workdir)
        self._test_appendsrcfile(testrecipe, 'a-file', srcdir=subdir)

    def test_recipetool_appendsrcfile_existing_in_src_uri(self):
        testrecipe = 'base-files'
        filepath,_  = self._get_first_file_uri(testrecipe)
        self.assertTrue(filepath, 'Unable to test, no file:// uri found in SRC_URI for %s' % testrecipe)
        self._test_appendsrcfile(testrecipe, filepath, has_src_uri=False)

    def test_recipetool_appendsrcfile_existing_in_src_uri_diff_params(self, machine=None):
        testrecipe = 'base-files'
        subdir = 'tmp'
        filepath, srcuri_entry = self._get_first_file_uri(testrecipe)
        self.assertTrue(filepath, 'Unable to test, no file:// uri found in SRC_URI for %s' % testrecipe)

        self._test_appendsrcfile(testrecipe, filepath, subdir, machine=machine, remove=[srcuri_entry])

    def test_recipetool_appendsrcfile_machine(self):
        # A very basic test
        self._test_appendsrcfile('base-files', 'a-file', machine='mymachine')

        # Force cleaning the output of previous test
        self.tearDownLocal()

        # A more complex test: existing entry in src_uri with different param
        self.test_recipetool_appendsrcfile_existing_in_src_uri_diff_params(machine='mymachine')

    def test_recipetool_appendsrcfile_update_recipe_basic(self):
        testrecipe = "mtd-utils-selftest"
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('meta-selftest', recipefile, 'This test expect %s recipe to be in meta-selftest')
        cmd = 'recipetool appendsrcfile -W -u meta-selftest %s %s' % (testrecipe, self.testfile)
        result = runCmd(cmd)
        self.assertNotIn('Traceback', result.output)
        self.add_command_to_tearDown('cd %s; rm -f %s/%s; git checkout .' % (os.path.dirname(recipefile), testrecipe, os.path.basename(self.testfile)))

        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('??', '.*/%s/%s$' % (testrecipe, os.path.basename(self.testfile)))]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        result = runCmd('git diff %s' % os.path.basename(recipefile), cwd=os.path.dirname(recipefile))
        removelines = []
        addlines = [
            'file://%s \\\\' % os.path.basename(self.testfile),
        ]
        self._check_diff(result.output, addlines, removelines)

    def test_recipetool_appendsrcfile_replace_file_srcdir(self):
        testrecipe = 'bash'
        filepath = 'Makefile.in'
        bb_vars = get_bb_vars(['S', 'WORKDIR'], testrecipe)
        srcdir = bb_vars['S']
        workdir = bb_vars['WORKDIR']
        subdir = os.path.relpath(srcdir, workdir)

        self._test_appendsrcfile(testrecipe, filepath, srcdir=subdir)
        bitbake('%s:do_unpack' % testrecipe)
        with open(self.testfile, 'r') as testfile:
            with open(os.path.join(srcdir, filepath), 'r') as makefilein:
                self.assertEqual(testfile.read(), makefilein.read())

    def test_recipetool_appendsrcfiles_basic(self, destdir=None):
        newfiles = [self.testfile]
        for i in range(1, 5):
            testfile = os.path.join(self.tempdir, 'testfile%d' % i)
            with open(testfile, 'w') as f:
                f.write('Test file %d\n' % i)
            newfiles.append(testfile)
        self._test_appendsrcfiles('gcc', newfiles, destdir=destdir, options='-W')

    def test_recipetool_appendsrcfiles_basic_subdir(self):
        self.test_recipetool_appendsrcfiles_basic(destdir='testdir')
