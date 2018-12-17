import os
import shutil
import tempfile
import urllib.parse

from oeqa.utils.commands import runCmd, bitbake, get_bb_var
from oeqa.utils.commands import get_bb_vars, create_temp_layer
from oeqa.core.decorator.oeid import OETestID
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


class RecipetoolBase(devtool.DevtoolBase):

    def setUpLocal(self):
        super(RecipetoolBase, self).setUpLocal()
        self.templayerdir = templayerdir
        self.tempdir = tempfile.mkdtemp(prefix='recipetoolqa')
        self.track_for_cleanup(self.tempdir)
        self.testfile = os.path.join(self.tempdir, 'testfile')
        with open(self.testfile, 'w') as f:
            f.write('Test file\n')

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


class RecipetoolTests(RecipetoolBase):

    @classmethod
    def setUpClass(cls):
        super(RecipetoolTests, cls).setUpClass()
        # Ensure we have the right data in shlibs/pkgdata
        cls.logger.info('Running bitbake to generate pkgdata')
        bitbake('-c packagedata base-files coreutils busybox selftest-recipetool-appendfile')
        bb_vars = get_bb_vars(['COREBASE', 'BBPATH'])
        cls.corebase = bb_vars['COREBASE']
        cls.bbpath = bb_vars['BBPATH']

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

    @OETestID(1177)
    def test_recipetool_appendfile_basic(self):
        # Basic test
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                        '\n']
        _, output = self._try_recipetool_appendfile('base-files', '/etc/motd', self.testfile, '', expectedlines, ['motd'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1183)
    def test_recipetool_appendfile_invalid(self):
        # Test some commands that should error
        self._try_recipetool_appendfile_fail('/etc/passwd', self.testfile, ['ERROR: /etc/passwd cannot be handled by this tool', 'useradd', 'extrausers'])
        self._try_recipetool_appendfile_fail('/etc/timestamp', self.testfile, ['ERROR: /etc/timestamp cannot be handled by this tool'])
        self._try_recipetool_appendfile_fail('/dev/console', self.testfile, ['ERROR: /dev/console cannot be handled by this tool'])

    @OETestID(1176)
    def test_recipetool_appendfile_alternatives(self):
        # Now try with a file we know should be an alternative
        # (this is very much a fake example, but one we know is reliably an alternative)
        self._try_recipetool_appendfile_fail('/bin/ls', self.testfile, ['ERROR: File /bin/ls is an alternative possibly provided by the following recipes:', 'coreutils', 'busybox'])
        # Need a test file - should be executable
        testfile2 = os.path.join(self.corebase, 'oe-init-build-env')
        testfile2name = os.path.basename(testfile2)
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://%s"\n' % testfile2name,
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${base_bindir}\n',
                         '    install -m 0755 ${WORKDIR}/%s ${D}${base_bindir}/ls\n' % testfile2name,
                         '}\n']
        self._try_recipetool_appendfile('coreutils', '/bin/ls', testfile2, '-r coreutils', expectedlines, [testfile2name])
        # Now try bbappending the same file again, contents should not change
        bbappendfile, _ = self._try_recipetool_appendfile('coreutils', '/bin/ls', self.testfile, '-r coreutils', expectedlines, [testfile2name])
        # But file should have
        copiedfile = os.path.join(os.path.dirname(bbappendfile), 'coreutils', testfile2name)
        result = runCmd('diff -q %s %s' % (testfile2, copiedfile), ignore_status=True)
        self.assertNotEqual(result.status, 0, 'New file should have been copied but was not %s' % result.output)

    @OETestID(1178)
    def test_recipetool_appendfile_binary(self):
        # Try appending a binary file
        # /bin/ls can be a symlink to /usr/bin/ls
        ls = os.path.realpath("/bin/ls")
        result = runCmd('recipetool appendfile %s /bin/ls %s -r coreutils' % (self.templayerdir, ls))
        self.assertIn('WARNING: ', result.output)
        self.assertIn('is a binary', result.output)

    @OETestID(1173)
    def test_recipetool_appendfile_add(self):
        # Try arbitrary file add to a recipe
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/something\n',
                         '}\n']
        self._try_recipetool_appendfile('netbase', '/usr/share/something', self.testfile, '-r netbase', expectedlines, ['testfile'])
        # Try adding another file, this time where the source file is executable
        # (so we're testing that, plus modifying an existing bbappend)
        testfile2 = os.path.join(self.corebase, 'oe-init-build-env')
        testfile2name = os.path.basename(testfile2)
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile \\\n',
                         '            file://%s \\\n' % testfile2name,
                         '            "\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/something\n',
                         '    install -m 0755 ${WORKDIR}/%s ${D}${datadir}/scriptname\n' % testfile2name,
                         '}\n']
        self._try_recipetool_appendfile('netbase', '/usr/share/scriptname', testfile2, '-r netbase', expectedlines, ['testfile', testfile2name])

    @OETestID(1174)
    def test_recipetool_appendfile_add_bindir(self):
        # Try arbitrary file add to a recipe, this time to a location such that should be installed as executable
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${bindir}\n',
                         '    install -m 0755 ${WORKDIR}/testfile ${D}${bindir}/selftest-recipetool-testbin\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('netbase', '/usr/bin/selftest-recipetool-testbin', self.testfile, '-r netbase', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1175)
    def test_recipetool_appendfile_add_machine(self):
        # Try arbitrary file add to a recipe, this time to a location such that should be installed as executable
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'PACKAGE_ARCH = "${MACHINE_ARCH}"\n',
                         '\n',
                         'SRC_URI_append_mymachine = " file://testfile"\n',
                         '\n',
                         'do_install_append_mymachine() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/something\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('netbase', '/usr/share/something', self.testfile, '-r netbase -m mymachine', expectedlines, ['mymachine/testfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1184)
    def test_recipetool_appendfile_orig(self):
        # A file that's in SRC_URI and in do_install with the same name
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-orig', self.testfile, '', expectedlines, ['selftest-replaceme-orig'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1191)
    def test_recipetool_appendfile_todir(self):
        # A file that's in SRC_URI and in do_install with destination directory rather than file
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-todir', self.testfile, '', expectedlines, ['selftest-replaceme-todir'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1187)
    def test_recipetool_appendfile_renamed(self):
        # A file that's in SRC_URI with a different name to the destination file
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-renamed', self.testfile, '', expectedlines, ['file1'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1190)
    def test_recipetool_appendfile_subdir(self):
        # A file that's in SRC_URI in a subdir
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/selftest-replaceme-subdir\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-subdir', self.testfile, '', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1189)
    def test_recipetool_appendfile_src_glob(self):
        # A file that's in SRC_URI as a glob
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/selftest-replaceme-src-globfile\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-src-globfile', self.testfile, '', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1181)
    def test_recipetool_appendfile_inst_glob(self):
        # A file that's in do_install as a glob
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-inst-globfile', self.testfile, '', expectedlines, ['selftest-replaceme-inst-globfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1182)
    def test_recipetool_appendfile_inst_todir_glob(self):
        # A file that's in do_install as a glob with destination as a directory
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-inst-todir-globfile', self.testfile, '', expectedlines, ['selftest-replaceme-inst-todir-globfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1185)
    def test_recipetool_appendfile_patch(self):
        # A file that's added by a patch in SRC_URI
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${sysconfdir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${sysconfdir}/selftest-replaceme-patched\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/etc/selftest-replaceme-patched', self.testfile, '', expectedlines, ['testfile'])
        for line in output.splitlines():
            if 'WARNING: ' in line:
                self.assertIn('add-file.patch', line, 'Unexpected warning found in output:\n%s' % line)
                break
        else:
            self.fail('Patch warning not found in output:\n%s' % output)

    @OETestID(1188)
    def test_recipetool_appendfile_script(self):
        # Now, a file that's in SRC_URI but installed by a script (so no mention in do_install)
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/selftest-replaceme-scripted\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-scripted', self.testfile, '', expectedlines, ['testfile'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1180)
    def test_recipetool_appendfile_inst_func(self):
        # A file that's installed from a function called by do_install
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-inst-func', self.testfile, '', expectedlines, ['selftest-replaceme-inst-func'])
        self.assertNotIn('WARNING: ', output)

    @OETestID(1186)
    def test_recipetool_appendfile_postinstall(self):
        # A file that's created by a postinstall script (and explicitly mentioned in it)
        # First try without specifying recipe
        self._try_recipetool_appendfile_fail('/usr/share/selftest-replaceme-postinst', self.testfile, ['File /usr/share/selftest-replaceme-postinst may be written out in a pre/postinstall script of the following recipes:', 'selftest-recipetool-appendfile'])
        # Now specify recipe
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://testfile"\n',
                         '\n',
                         'do_install_append() {\n',
                         '    install -d ${D}${datadir}\n',
                         '    install -m 0644 ${WORKDIR}/testfile ${D}${datadir}/selftest-replaceme-postinst\n',
                         '}\n']
        _, output = self._try_recipetool_appendfile('selftest-recipetool-appendfile', '/usr/share/selftest-replaceme-postinst', self.testfile, '-r selftest-recipetool-appendfile', expectedlines, ['testfile'])

    @OETestID(1179)
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

    @OETestID(1192)
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

    @OETestID(1193)
    def test_recipetool_create(self):
        # Try adding a recipe
        tempsrc = os.path.join(self.tempdir, 'srctree')
        os.makedirs(tempsrc)
        recipefile = os.path.join(self.tempdir, 'logrotate_3.12.3.bb')
        srcuri = 'https://github.com/logrotate/logrotate/releases/download/3.12.3/logrotate-3.12.3.tar.xz'
        result = runCmd('recipetool create -o %s %s -x %s' % (recipefile, srcuri, tempsrc))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = 'GPLv2'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'
        checkvars['SRC_URI'] = 'https://github.com/logrotate/logrotate/releases/download/${PV}/logrotate-${PV}.tar.xz'
        checkvars['SRC_URI[md5sum]'] = 'a560c57fac87c45b2fc17406cdf79288'
        checkvars['SRC_URI[sha256sum]'] = '2e6a401cac9024db2288297e3be1a8ab60e7401ba8e91225218aaf4a27e82a07'
        self._test_recipe_contents(recipefile, checkvars, [])

    @OETestID(1194)
    def test_recipetool_create_git(self):
        if 'x11' not in get_bb_var('DISTRO_FEATURES'):
            self.skipTest('Test requires x11 as distro feature')
        # Ensure we have the right data in shlibs/pkgdata
        bitbake('libpng pango libx11 libxext jpeg libcheck')
        # Try adding a recipe
        tempsrc = os.path.join(self.tempdir, 'srctree')
        os.makedirs(tempsrc)
        recipefile = os.path.join(self.tempdir, 'libmatchbox.bb')
        srcuri = 'git://git.yoctoproject.org/libmatchbox'
        result = runCmd(['recipetool', 'create', '-o', recipefile, srcuri + ";rev=9f7cf8895ae2d39c465c04cc78e918c157420269", '-x', tempsrc])
        self.assertTrue(os.path.isfile(recipefile), 'recipetool did not create recipe file; output:\n%s' % result.output)
        checkvars = {}
        checkvars['LICENSE'] = 'LGPLv2.1'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34'
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '1.11+git${SRCPV}'
        checkvars['SRC_URI'] = srcuri
        checkvars['DEPENDS'] = set(['libcheck', 'libjpeg-turbo', 'libpng', 'libx11', 'libxext', 'pango'])
        inherits = ['autotools', 'pkgconfig']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    @OETestID(1392)
    def test_recipetool_create_simple(self):
        # Try adding a recipe
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pv = '1.7.3.0'
        srcuri = 'http://www.dest-unreach.org/socat/download/socat-%s.tar.bz2' % pv
        result = runCmd('recipetool create %s -o %s' % (srcuri, temprecipe))
        dirlist = os.listdir(temprecipe)
        if len(dirlist) > 1:
            self.fail('recipetool created more than just one file; output:\n%s\ndirlist:\n%s' % (result.output, str(dirlist)))
        if len(dirlist) < 1 or not os.path.isfile(os.path.join(temprecipe, dirlist[0])):
            self.fail('recipetool did not create recipe file; output:\n%s\ndirlist:\n%s' % (result.output, str(dirlist)))
        self.assertEqual(dirlist[0], 'socat_%s.bb' % pv, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['LICENSE'] = set(['Unknown', 'GPLv2'])
        checkvars['LIC_FILES_CHKSUM'] = set(['file://COPYING.OpenSSL;md5=5c9bccc77f67a8328ef4ebaf468116f4', 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'])
        # We don't check DEPENDS since they are variable for this recipe depending on what's in the sysroot
        checkvars['S'] = None
        checkvars['SRC_URI'] = srcuri.replace(pv, '${PV}')
        inherits = ['autotools']
        self._test_recipe_contents(os.path.join(temprecipe, dirlist[0]), checkvars, inherits)

    @OETestID(1418)
    def test_recipetool_create_cmake(self):
        bitbake('-c packagedata gtk+')

        # Try adding a recipe
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        recipefile = os.path.join(temprecipe, 'navit_0.5.0.bb')
        srcuri = 'http://downloads.yoctoproject.org/mirror/sources/navit-0.5.0.tar.gz'
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['Unknown', 'GPLv2', 'LGPLv2'])
        checkvars['SRC_URI'] = 'http://downloads.yoctoproject.org/mirror/sources/navit-${PV}.tar.gz'
        checkvars['SRC_URI[md5sum]'] = '242f398e979a6b8c0f3c802b63435b68'
        checkvars['SRC_URI[sha256sum]'] = '13353481d7fc01a4f64e385dda460b51496366bba0fd2cc85a89a0747910e94d'
        checkvars['DEPENDS'] = set(['freetype', 'zlib', 'openssl', 'glib-2.0', 'virtual/libgl', 'virtual/egl', 'gtk+', 'libpng', 'libsdl', 'freeglut', 'dbus-glib', 'fribidi'])
        inherits = ['cmake', 'python-dir', 'gettext', 'pkgconfig']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    @OETestID(1638)
    def test_recipetool_create_github(self):
        # Basic test to see if github URL mangling works
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        recipefile = os.path.join(temprecipe, 'meson_git.bb')
        srcuri = 'https://github.com/mesonbuild/meson;rev=0.32.0'
        result = runCmd(['recipetool', 'create', '-o', temprecipe, srcuri])
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['Apache-2.0'])
        checkvars['SRC_URI'] = 'git://github.com/mesonbuild/meson;protocol=https'
        inherits = ['setuptools']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    @OETestID(1639)
    def test_recipetool_create_github_tarball(self):
        # Basic test to ensure github URL mangling doesn't apply to release tarballs
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        pv = '0.32.0'
        recipefile = os.path.join(temprecipe, 'meson_%s.bb' % pv)
        srcuri = 'https://github.com/mesonbuild/meson/releases/download/%s/meson-%s.tar.gz' % (pv, pv)
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['Apache-2.0'])
        checkvars['SRC_URI'] = 'https://github.com/mesonbuild/meson/releases/download/${PV}/meson-${PV}.tar.gz'
        inherits = ['setuptools']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    @OETestID(1637)
    def test_recipetool_create_git_http(self):
        # Basic test to check http git URL mangling works
        temprecipe = os.path.join(self.tempdir, 'recipe')
        os.makedirs(temprecipe)
        recipefile = os.path.join(temprecipe, 'matchbox-terminal_git.bb')
        srcuri = 'http://git.yoctoproject.org/git/matchbox-terminal'
        result = runCmd('recipetool create -o %s %s' % (temprecipe, srcuri))
        self.assertTrue(os.path.isfile(recipefile))
        checkvars = {}
        checkvars['LICENSE'] = set(['GPLv2'])
        checkvars['SRC_URI'] = 'git://git.yoctoproject.org/git/matchbox-terminal;protocol=http'
        inherits = ['pkgconfig', 'autotools']
        self._test_recipe_contents(recipefile, checkvars, inherits)

    def _copy_file_with_cleanup(self, srcfile, basedstdir, *paths):
        dstdir = basedstdir
        self.assertTrue(os.path.exists(dstdir))
        for p in paths:
            dstdir = os.path.join(dstdir, p)
            if not os.path.exists(dstdir):
                os.makedirs(dstdir)
                self.track_for_cleanup(dstdir)
        dstfile = os.path.join(dstdir, os.path.basename(srcfile))
        if srcfile != dstfile:
            shutil.copy(srcfile, dstfile)
            self.track_for_cleanup(dstfile)

    @OETestID(1640)
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
            for path in searchpath:
                self._copy_file_with_cleanup(srcfile, path, 'lib', 'recipetool')
            result = runCmd("recipetool --quiet count")
            self.assertEqual(result.output, '1')
            result = runCmd("recipetool --quiet multiloaded")
            self.assertEqual(result.output, "no")
            for path in searchpath:
                result = runCmd("recipetool --quiet bbdir")
                self.assertEqual(result.output, path)
                os.unlink(os.path.join(result.output, 'lib', 'recipetool', 'bbpath.py'))
        finally:
            with open(srcfile, 'w') as fh:
                fh.writelines(plugincontent)


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
                return p.netloc + p.path

    def _test_appendsrcfile(self, testrecipe, filename=None, destdir=None, has_src_uri=True, srcdir=None, newfile=None, options=''):
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

        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        if has_src_uri:
            uri = 'file://%s' % filename
            if expected_subdir:
                uri += ';subdir=%s' % expected_subdir
            expectedlines[0:0] = ['SRC_URI += "%s"\n' % uri,
                                  '\n']

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

    @OETestID(1273)
    def test_recipetool_appendsrcfile_basic(self):
        self._test_appendsrcfile('base-files', 'a-file')

    @OETestID(1274)
    def test_recipetool_appendsrcfile_basic_wildcard(self):
        testrecipe = 'base-files'
        self._test_appendsrcfile(testrecipe, 'a-file', options='-w')
        recipefile = get_bb_var('FILE', testrecipe)
        bbappendfile = self._check_bbappend(testrecipe, recipefile, self.templayerdir)
        self.assertEqual(os.path.basename(bbappendfile), '%s_%%.bbappend' % testrecipe)

    @OETestID(1281)
    def test_recipetool_appendsrcfile_subdir_basic(self):
        self._test_appendsrcfile('base-files', 'a-file', 'tmp')

    @OETestID(1282)
    def test_recipetool_appendsrcfile_subdir_basic_dirdest(self):
        self._test_appendsrcfile('base-files', destdir='tmp')

    @OETestID(1280)
    def test_recipetool_appendsrcfile_srcdir_basic(self):
        testrecipe = 'bash'
        bb_vars = get_bb_vars(['S', 'WORKDIR'], testrecipe)
        srcdir = bb_vars['S']
        workdir = bb_vars['WORKDIR']
        subdir = os.path.relpath(srcdir, workdir)
        self._test_appendsrcfile(testrecipe, 'a-file', srcdir=subdir)

    @OETestID(1275)
    def test_recipetool_appendsrcfile_existing_in_src_uri(self):
        testrecipe = 'base-files'
        filepath = self._get_first_file_uri(testrecipe)
        self.assertTrue(filepath, 'Unable to test, no file:// uri found in SRC_URI for %s' % testrecipe)
        self._test_appendsrcfile(testrecipe, filepath, has_src_uri=False)

    @OETestID(1276)
    def test_recipetool_appendsrcfile_existing_in_src_uri_diff_params(self):
        testrecipe = 'base-files'
        subdir = 'tmp'
        filepath = self._get_first_file_uri(testrecipe)
        self.assertTrue(filepath, 'Unable to test, no file:// uri found in SRC_URI for %s' % testrecipe)

        output = self._test_appendsrcfile(testrecipe, filepath, subdir, has_src_uri=False)
        self.assertTrue(any('with different parameters' in l for l in output))

    @OETestID(1277)
    def test_recipetool_appendsrcfile_replace_file_srcdir(self):
        testrecipe = 'bash'
        filepath = 'Makefile.in'
        bb_vars = get_bb_vars(['S', 'WORKDIR'], testrecipe)
        srcdir = bb_vars['S']
        workdir = bb_vars['WORKDIR']
        subdir = os.path.relpath(srcdir, workdir)

        self._test_appendsrcfile(testrecipe, filepath, srcdir=subdir)
        bitbake('%s:do_unpack' % testrecipe)
        self.assertEqual(open(self.testfile, 'r').read(), open(os.path.join(srcdir, filepath), 'r').read())

    @OETestID(1278)
    def test_recipetool_appendsrcfiles_basic(self, destdir=None):
        newfiles = [self.testfile]
        for i in range(1, 5):
            testfile = os.path.join(self.tempdir, 'testfile%d' % i)
            with open(testfile, 'w') as f:
                f.write('Test file %d\n' % i)
            newfiles.append(testfile)
        self._test_appendsrcfiles('gcc', newfiles, destdir=destdir, options='-W')

    @OETestID(1279)
    def test_recipetool_appendsrcfiles_basic_subdir(self):
        self.test_recipetool_appendsrcfiles_basic(destdir='testdir')
