#
# SPDX-License-Identifier: MIT
#

import os
import re
import shutil
import tempfile
import glob
import fnmatch

import oeqa.utils.ftools as ftools
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, create_temp_layer
from oeqa.utils.commands import get_bb_vars, runqemu, get_test_layer

oldmetapath = None

def setUpModule():
    import bb.utils

    global templayerdir
    templayerdir = tempfile.mkdtemp(prefix='devtoolqa')
    corecopydir = os.path.join(templayerdir, 'core-copy')
    bblayers_conf = os.path.join(os.environ['BUILDDIR'], 'conf', 'bblayers.conf')
    edited_layers = []

    # We need to take a copy of the meta layer so we can modify it and not
    # have any races against other tests that might be running in parallel
    # however things like COREBASE mean that you can't just copy meta, you
    # need the whole repository.
    def bblayers_edit_cb(layerpath, canonical_layerpath):
        global oldmetapath
        if not canonical_layerpath.endswith('/'):
            # This helps us match exactly when we're using this path later
            canonical_layerpath += '/'
        if not edited_layers and canonical_layerpath.endswith('/meta/'):
            canonical_layerpath = os.path.realpath(canonical_layerpath) + '/'
            edited_layers.append(layerpath)
            oldmetapath = os.path.realpath(layerpath)
            result = runCmd('git rev-parse --show-toplevel', cwd=canonical_layerpath)
            oldreporoot = result.output.rstrip()
            newmetapath = os.path.join(corecopydir, os.path.relpath(oldmetapath, oldreporoot))
            runCmd('git clone %s %s' % (oldreporoot, corecopydir), cwd=templayerdir)
            # Now we need to copy any modified files
            # You might ask "why not just copy the entire tree instead of
            # cloning and doing this?" - well, the problem with that is
            # TMPDIR or an equally large subdirectory might exist
            # under COREBASE and we don't want to copy that, so we have
            # to be selective.
            result = runCmd('git status --porcelain', cwd=oldreporoot)
            for line in result.output.splitlines():
                if line.startswith(' M ') or line.startswith('?? '):
                    relpth = line.split()[1]
                    pth = os.path.join(oldreporoot, relpth)
                    if pth.startswith(canonical_layerpath):
                        if relpth.endswith('/'):
                            destdir = os.path.join(corecopydir, relpth)
                            # avoid race condition by not copying .pyc files YPBZ#13421,13803
                            shutil.copytree(pth, destdir, ignore=shutil.ignore_patterns('*.pyc', '__pycache__'))
                        else:
                            destdir = os.path.join(corecopydir, os.path.dirname(relpth))
                            bb.utils.mkdirhier(destdir)
                            shutil.copy2(pth, destdir)
            return newmetapath
        else:
            return layerpath
    bb.utils.edit_bblayers_conf(bblayers_conf, None, None, bblayers_edit_cb)

def tearDownModule():
    if oldmetapath:
        edited_layers = []
        def bblayers_edit_cb(layerpath, canonical_layerpath):
            if not edited_layers and canonical_layerpath.endswith('/meta'):
                edited_layers.append(layerpath)
                return oldmetapath
            else:
                return layerpath
        bblayers_conf = os.path.join(os.environ['BUILDDIR'], 'conf', 'bblayers.conf')
        bb.utils.edit_bblayers_conf(bblayers_conf, None, None, bblayers_edit_cb)
    shutil.rmtree(templayerdir)

class DevtoolBase(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(DevtoolBase, cls).setUpClass()
        bb_vars = get_bb_vars(['TOPDIR', 'SSTATE_DIR'])
        cls.original_sstate = bb_vars['SSTATE_DIR']
        cls.devtool_sstate = os.path.join(bb_vars['TOPDIR'], 'sstate_devtool')
        cls.sstate_conf  = 'SSTATE_DIR = "%s"\n' % cls.devtool_sstate
        cls.sstate_conf += ('SSTATE_MIRRORS += "file://.* file:///%s/PATH"\n'
                            % cls.original_sstate)

    @classmethod
    def tearDownClass(cls):
        cls.logger.debug('Deleting devtool sstate cache on %s' % cls.devtool_sstate)
        runCmd('rm -rf %s' % cls.devtool_sstate)
        super(DevtoolBase, cls).tearDownClass()

    def setUp(self):
        """Test case setup function"""
        super(DevtoolBase, self).setUp()
        self.workspacedir = os.path.join(self.builddir, 'workspace')
        self.assertTrue(not os.path.exists(self.workspacedir),
                        'This test cannot be run with a workspace directory '
                        'under the build directory')
        self.append_config(self.sstate_conf)

    def _check_src_repo(self, repo_dir):
        """Check srctree git repository"""
        self.assertTrue(os.path.isdir(os.path.join(repo_dir, '.git')),
                        'git repository for external source tree not found')
        result = runCmd('git status --porcelain', cwd=repo_dir)
        self.assertEqual(result.output.strip(), "",
                         'Created git repo is not clean')
        result = runCmd('git symbolic-ref HEAD', cwd=repo_dir)
        self.assertEqual(result.output.strip(), "refs/heads/devtool",
                         'Wrong branch in git repo')

    def _check_repo_status(self, repo_dir, expected_status):
        """Check the worktree status of a repository"""
        result = runCmd('git status . --porcelain',
                        cwd=repo_dir)
        for line in result.output.splitlines():
            for ind, (f_status, fn_re) in enumerate(expected_status):
                if re.match(fn_re, line[3:]):
                    if f_status != line[:2]:
                        self.fail('Unexpected status in line: %s' % line)
                    expected_status.pop(ind)
                    break
            else:
                self.fail('Unexpected modified file in line: %s' % line)
        if expected_status:
            self.fail('Missing file changes: %s' % expected_status)

    def _test_recipe_contents(self, recipefile, checkvars, checkinherits):
        with open(recipefile, 'r') as f:
            invar = None
            invalue = None
            inherits = set()
            for line in f:
                var = None
                if invar:
                    value = line.strip().strip('"')
                    if value.endswith('\\'):
                        invalue += ' ' + value[:-1].strip()
                        continue
                    else:
                        invalue += ' ' + value.strip()
                        var = invar
                        value = invalue
                        invar = None
                elif '=' in line:
                    splitline = line.split('=', 1)
                    var = splitline[0].rstrip()
                    value = splitline[1].strip().strip('"')
                    if value.endswith('\\'):
                        invalue = value[:-1].strip()
                        invar = var
                        continue
                elif line.startswith('inherit '):
                    inherits.update(line.split()[1:])

                if var and var in checkvars:
                    needvalue = checkvars.pop(var)
                    if needvalue is None:
                        self.fail('Variable %s should not appear in recipe, but value is being set to "%s"' % (var, value))
                    if isinstance(needvalue, set):
                        if var == 'LICENSE':
                            value = set(value.split(' & '))
                        else:
                            value = set(value.split())
                    self.assertEqual(value, needvalue, 'values for %s do not match' % var)


        missingvars = {}
        for var, value in checkvars.items():
            if value is not None:
                missingvars[var] = value
        self.assertEqual(missingvars, {}, 'Some expected variables not found in recipe: %s' % checkvars)

        for inherit in checkinherits:
            self.assertIn(inherit, inherits, 'Missing inherit of %s' % inherit)

    def _check_bbappend(self, testrecipe, recipefile, appenddir):
        result = runCmd('bitbake-layers show-appends', cwd=self.builddir)
        resultlines = result.output.splitlines()
        inrecipe = False
        bbappends = []
        bbappendfile = None
        for line in resultlines:
            if inrecipe:
                if line.startswith(' '):
                    bbappends.append(line.strip())
                else:
                    break
            elif line == '%s:' % os.path.basename(recipefile):
                inrecipe = True
        self.assertLessEqual(len(bbappends), 2, '%s recipe is being bbappended by another layer - bbappends found:\n  %s' % (testrecipe, '\n  '.join(bbappends)))
        for bbappend in bbappends:
            if bbappend.startswith(appenddir):
                bbappendfile = bbappend
                break
        else:
            self.fail('bbappend for recipe %s does not seem to be created in test layer' % testrecipe)
        return bbappendfile

    def _create_temp_layer(self, templayerdir, addlayer, templayername, priority=999, recipepathspec='recipes-*/*'):
        create_temp_layer(templayerdir, templayername, priority, recipepathspec)
        if addlayer:
            self.add_command_to_tearDown('bitbake-layers remove-layer %s || true' % templayerdir)
            result = runCmd('bitbake-layers add-layer %s' % templayerdir, cwd=self.builddir)

    def _process_ls_output(self, output):
        """
        Convert ls -l output to a format we can reasonably compare from one context
        to another (e.g. from host to target)
        """
        filelist = []
        for line in output.splitlines():
            splitline = line.split()
            if len(splitline) < 8:
                self.fail('_process_ls_output: invalid output line: %s' % line)
            # Remove trailing . on perms
            splitline[0] = splitline[0].rstrip('.')
            # Remove leading . on paths
            splitline[-1] = splitline[-1].lstrip('.')
            # Drop fields we don't want to compare
            del splitline[7]
            del splitline[6]
            del splitline[5]
            del splitline[4]
            del splitline[1]
            filelist.append(' '.join(splitline))
        return filelist


class DevtoolTests(DevtoolBase):

    def test_create_workspace(self):
        # Check preconditions
        result = runCmd('bitbake-layers show-layers')
        self.assertTrue('\nworkspace' not in result.output, 'This test cannot be run with a workspace layer in bblayers.conf')
        # remove conf/devtool.conf to avoid it corrupting tests
        devtoolconf = os.path.join(self.builddir, 'conf', 'devtool.conf')
        self.track_for_cleanup(devtoolconf)
        # Try creating a workspace layer with a specific path
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        result = runCmd('devtool create-workspace %s' % tempdir)
        self.assertTrue(os.path.isfile(os.path.join(tempdir, 'conf', 'layer.conf')), msg = "No workspace created. devtool output: %s " % result.output)
        result = runCmd('bitbake-layers show-layers')
        self.assertIn(tempdir, result.output)
        # Try creating a workspace layer with the default path
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool create-workspace')
        self.assertTrue(os.path.isfile(os.path.join(self.workspacedir, 'conf', 'layer.conf')), msg = "No workspace created. devtool output: %s " % result.output)
        result = runCmd('bitbake-layers show-layers')
        self.assertNotIn(tempdir, result.output)
        self.assertIn(self.workspacedir, result.output)

class DevtoolAddTests(DevtoolBase):

    def test_devtool_add(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        pn = 'pv'
        pv = '1.5.3'
        url = 'http://downloads.yoctoproject.org/mirror/sources/pv-1.5.3.tar.bz2'
        result = runCmd('wget %s' % url, cwd=tempdir)
        result = runCmd('tar xfv %s' % os.path.basename(url), cwd=tempdir)
        srcdir = os.path.join(tempdir, '%s-%s' % (pn, pv))
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure')), 'Unable to find configure script in source directory')
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % pn)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s' % (pn, srcdir))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        # Test devtool status
        result = runCmd('devtool status')
        recipepath = '%s/recipes/%s/%s_%s.bb' % (self.workspacedir, pn, pn, pv)
        self.assertIn(recipepath, result.output)
        self.assertIn(srcdir, result.output)
        # Test devtool find-recipe
        result = runCmd('devtool -q find-recipe %s' % pn)
        self.assertEqual(recipepath, result.output.strip())
        # Test devtool edit-recipe
        result = runCmd('VISUAL="echo 123" devtool -q edit-recipe %s' % pn)
        self.assertEqual('123 %s' % recipepath, result.output.strip())
        # Clean up anything in the workdir/sysroot/sstate cache (have to do this *after* devtool add since the recipe only exists then)
        bitbake('%s -c cleansstate' % pn)
        # Test devtool build
        result = runCmd('devtool build %s' % pn)
        bb_vars = get_bb_vars(['D', 'bindir'], pn)
        installdir = bb_vars['D']
        self.assertTrue(installdir, 'Could not query installdir variable')
        bindir = bb_vars['bindir']
        self.assertTrue(bindir, 'Could not query bindir variable')
        if bindir[0] == '/':
            bindir = bindir[1:]
        self.assertTrue(os.path.isfile(os.path.join(installdir, bindir, 'pv')), 'pv binary not found in D')

    def test_devtool_add_git_local(self):
        # We need dbus built so that DEPENDS recognition works
        bitbake('dbus')
        # Fetch source from a remote URL, but do it outside of devtool
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        pn = 'dbus-wait'
        srcrev = '6cc6077a36fe2648a5f993fe7c16c9632f946517'
        # We choose an https:// git URL here to check rewriting the URL works
        url = 'https://git.yoctoproject.org/git/dbus-wait'
        # Force fetching to "noname" subdir so we verify we're picking up the name from autoconf
        # instead of the directory name
        result = runCmd('git clone %s noname' % url, cwd=tempdir)
        srcdir = os.path.join(tempdir, 'noname')
        result = runCmd('git reset --hard %s' % srcrev, cwd=srcdir)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure.ac')), 'Unable to find configure script in source directory')
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # Don't specify a name since we should be able to auto-detect it
        result = runCmd('devtool add %s' % srcdir)
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        # Check the recipe name is correct
        recipefile = get_bb_var('FILE', pn)
        self.assertIn('%s_git.bb' % pn, recipefile, 'Recipe file incorrectly named')
        self.assertIn(recipefile, result.output)
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(pn, result.output)
        self.assertIn(srcdir, result.output)
        self.assertIn(recipefile, result.output)
        checkvars = {}
        checkvars['LICENSE'] = 'GPLv2'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '0.1+git${SRCPV}'
        checkvars['SRC_URI'] = 'git://git.yoctoproject.org/git/dbus-wait;protocol=https'
        checkvars['SRCREV'] = srcrev
        checkvars['DEPENDS'] = set(['dbus'])
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_library(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        version = '1.1'
        url = 'https://www.intra2net.com/en/developer/libftdi/download/libftdi1-%s.tar.bz2' % version
        result = runCmd('wget %s' % url, cwd=tempdir)
        result = runCmd('tar xfv libftdi1-%s.tar.bz2' % version, cwd=tempdir)
        srcdir = os.path.join(tempdir, 'libftdi1-%s' % version)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'CMakeLists.txt')), 'Unable to find CMakeLists.txt in source directory')
        # Test devtool add (and use -V so we test that too)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add libftdi %s -V %s' % (srcdir, version))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn('libftdi', result.output)
        self.assertIn(srcdir, result.output)
        # Clean up anything in the workdir/sysroot/sstate cache (have to do this *after* devtool add since the recipe only exists then)
        bitbake('libftdi -c cleansstate')
        # libftdi's python/CMakeLists.txt is a bit broken, so let's just disable it
        # There's also the matter of it installing cmake files to a path we don't
        # normally cover, which triggers the installed-vs-shipped QA test we have
        # within do_package
        recipefile = '%s/recipes/libftdi/libftdi_%s.bb' % (self.workspacedir, version)
        result = runCmd('recipetool setvar %s EXTRA_OECMAKE -- \'-DPYTHON_BINDINGS=OFF -DLIBFTDI_CMAKE_CONFIG_DIR=${datadir}/cmake/Modules\'' % recipefile)
        with open(recipefile, 'a') as f:
            f.write('\nFILES_${PN}-dev += "${datadir}/cmake/Modules"\n')
            # We don't have the ability to pick up this dependency automatically yet...
            f.write('\nDEPENDS += "libusb1"\n')
            f.write('\nTESTLIBOUTPUT = "${COMPONENTS_DIR}/${TUNE_PKGARCH}/${PN}/${libdir}"\n')
        # Test devtool build
        result = runCmd('devtool build libftdi')
        bb_vars = get_bb_vars(['TESTLIBOUTPUT', 'STAMP'], 'libftdi')
        staging_libdir = bb_vars['TESTLIBOUTPUT']
        self.assertTrue(staging_libdir, 'Could not query TESTLIBOUTPUT variable')
        self.assertTrue(os.path.isfile(os.path.join(staging_libdir, 'libftdi1.so.2.1.0')), "libftdi binary not found in STAGING_LIBDIR. Output of devtool build libftdi %s" % result.output)
        # Test devtool reset
        stampprefix = bb_vars['STAMP']
        result = runCmd('devtool reset libftdi')
        result = runCmd('devtool status')
        self.assertNotIn('libftdi', result.output)
        self.assertTrue(stampprefix, 'Unable to get STAMP value for recipe libftdi')
        matches = glob.glob(stampprefix + '*')
        self.assertFalse(matches, 'Stamp files exist for recipe libftdi that should have been cleaned')
        self.assertFalse(os.path.isfile(os.path.join(staging_libdir, 'libftdi1.so.2.1.0')), 'libftdi binary still found in STAGING_LIBDIR after cleaning')

    def test_devtool_add_fetch(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        testver = '0.23'
        url = 'https://files.pythonhosted.org/packages/c0/41/bae1254e0396c0cc8cf1751cb7d9afc90a602353695af5952530482c963f/MarkupSafe-%s.tar.gz' % testver
        testrecipe = 'python-markupsafe'
        srcdir = os.path.join(tempdir, testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s -f %s' % (testrecipe, srcdir, url))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created. %s' % result.output)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'setup.py')), 'Unable to find setup.py in source directory')
        self.assertTrue(os.path.isdir(os.path.join(srcdir, '.git')), 'git repository for external source tree was not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('%s_%s.bb' % (testrecipe, testver), recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/MarkupSafe-${PV}'
        checkvars['SRC_URI'] = url.replace(testver, '${PV}')
        self._test_recipe_contents(recipefile, checkvars, [])
        # Try with version specified
        result = runCmd('devtool reset -n %s' % testrecipe)
        shutil.rmtree(srcdir)
        fakever = '1.9'
        result = runCmd('devtool add %s %s -f %s -V %s' % (testrecipe, srcdir, url, fakever))
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'setup.py')), 'Unable to find setup.py in source directory')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('%s_%s.bb' % (testrecipe, fakever), recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/MarkupSafe-%s' % testver
        checkvars['SRC_URI'] = url
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_fetch_git(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        url = 'gitsm://git.yoctoproject.org/mraa'
        checkrev = 'ae127b19a50aa54255e4330ccfdd9a5d058e581d'
        testrecipe = 'mraa'
        srcdir = os.path.join(tempdir, testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s -a -f %s' % (testrecipe, srcdir, url))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created: %s' % result.output)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'imraa', 'imraa.c')), 'Unable to find imraa/imraa.c in source directory')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('_git.bb', recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '1.0+git${SRCPV}'
        checkvars['SRC_URI'] = url
        checkvars['SRCREV'] = '${AUTOREV}'
        self._test_recipe_contents(recipefile, checkvars, [])
        # Try with revision and version specified
        result = runCmd('devtool reset -n %s' % testrecipe)
        shutil.rmtree(srcdir)
        url_rev = '%s;rev=%s' % (url, checkrev)
        result = runCmd('devtool add %s %s -f "%s" -V 1.5' % (testrecipe, srcdir, url_rev))
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'imraa', 'imraa.c')), 'Unable to find imraa/imraa.c in source directory')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('_git.bb', recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '1.5+git${SRCPV}'
        checkvars['SRC_URI'] = url
        checkvars['SRCREV'] = checkrev
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_fetch_simple(self):
        # Fetch source from a remote URL, auto-detecting name
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        testver = '1.6.0'
        url = 'http://www.ivarch.com/programs/sources/pv-%s.tar.bz2' % testver
        testrecipe = 'pv'
        srcdir = os.path.join(self.workspacedir, 'sources', testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s' % url)
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created. %s' % result.output)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure')), 'Unable to find configure script in source directory')
        self.assertTrue(os.path.isdir(os.path.join(srcdir, '.git')), 'git repository for external source tree was not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('%s_%s.bb' % (testrecipe, testver), recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = None
        checkvars['SRC_URI'] = url.replace(testver, '${PV}')
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_npm(self):
        pn = 'savoirfairelinux-node-server-example'
        pv = '1.0.0'
        url = 'npm://registry.npmjs.org;package=@savoirfairelinux/node-server-example;version=' + pv
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % pn)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add \'%s\'' % url)
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        self.assertExists(os.path.join(self.workspacedir, 'recipes', pn, '%s_%s.bb' % (pn, pv)), 'Recipe not created')
        self.assertExists(os.path.join(self.workspacedir, 'recipes', pn, pn, 'npm-shrinkwrap.json'), 'Shrinkwrap not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(pn, result.output)
        # Clean up anything in the workdir/sysroot/sstate cache (have to do this *after* devtool add since the recipe only exists then)
        bitbake('%s -c cleansstate' % pn)
        # Test devtool build
        result = runCmd('devtool build %s' % pn)

class DevtoolModifyTests(DevtoolBase):

    def test_devtool_modify(self):
        import oe.path

        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean mdadm')
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify mdadm -x %s' % tempdir)
        self.assertExists(os.path.join(tempdir, 'Makefile'), 'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', 'mdadm_*.bbappend'))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)

        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn('mdadm', result.output)
        self.assertIn(tempdir, result.output)
        self._check_src_repo(tempdir)

        bitbake('mdadm -C unpack')

        def check_line(checkfile, expected, message, present=True):
            # Check for $expected, on a line on its own, in checkfile.
            with open(checkfile, 'r') as f:
                if present:
                    self.assertIn(expected + '\n', f, message)
                else:
                    self.assertNotIn(expected + '\n', f, message)

        modfile = os.path.join(tempdir, 'mdadm.8.in')
        bb_vars = get_bb_vars(['PKGD', 'mandir'], 'mdadm')
        pkgd = bb_vars['PKGD']
        self.assertTrue(pkgd, 'Could not query PKGD variable')
        mandir = bb_vars['mandir']
        self.assertTrue(mandir, 'Could not query mandir variable')
        manfile = oe.path.join(pkgd, mandir, 'man8', 'mdadm.8')

        check_line(modfile, 'Linux Software RAID', 'Could not find initial string')
        check_line(modfile, 'antique pin sardine', 'Unexpectedly found replacement string', present=False)

        result = runCmd("sed -i 's!^Linux Software RAID$!antique pin sardine!' %s" % modfile)
        check_line(modfile, 'antique pin sardine', 'mdadm.8.in file not modified (sed failed)')

        bitbake('mdadm -c package')
        check_line(manfile, 'antique pin sardine', 'man file not modified. man searched file path: %s' % manfile)

        result = runCmd('git checkout -- %s' % modfile, cwd=tempdir)
        check_line(modfile, 'Linux Software RAID', 'man .in file not restored (git failed)')

        bitbake('mdadm -c package')
        check_line(manfile, 'Linux Software RAID', 'man file not updated. man searched file path: %s' % manfile)

        result = runCmd('devtool reset mdadm')
        result = runCmd('devtool status')
        self.assertNotIn('mdadm', result.output)

    def test_devtool_buildclean(self):
        def assertFile(path, *paths):
            f = os.path.join(path, *paths)
            self.assertExists(f)
        def assertNoFile(path, *paths):
            f = os.path.join(path, *paths)
            self.assertNotExists(f)

        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('mdadm m4 -c cleansstate')
        # Try modifying a recipe
        tempdir_mdadm = tempfile.mkdtemp(prefix='devtoolqa')
        tempdir_m4 = tempfile.mkdtemp(prefix='devtoolqa')
        builddir_m4 = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir_mdadm)
        self.track_for_cleanup(tempdir_m4)
        self.track_for_cleanup(builddir_m4)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean mdadm m4')
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.write_recipeinc('m4', 'EXTERNALSRC_BUILD = "%s"\ndo_clean() {\n\t:\n}\n' % builddir_m4)
        try:
            runCmd('devtool modify mdadm -x %s' % tempdir_mdadm)
            runCmd('devtool modify m4 -x %s' % tempdir_m4)
            assertNoFile(tempdir_mdadm, 'mdadm')
            assertNoFile(builddir_m4, 'src/m4')
            result = bitbake('m4 -e')
            result = bitbake('mdadm m4 -c compile')
            self.assertEqual(result.status, 0)
            assertFile(tempdir_mdadm, 'mdadm')
            assertFile(builddir_m4, 'src/m4')
            # Check that buildclean task exists and does call make clean
            bitbake('mdadm m4 -c buildclean')
            assertNoFile(tempdir_mdadm, 'mdadm')
            assertNoFile(builddir_m4, 'src/m4')
            runCmd('echo "#Trigger rebuild" >> %s/Makefile' % tempdir_mdadm)
            bitbake('mdadm m4 -c compile')
            assertFile(tempdir_mdadm, 'mdadm')
            assertFile(builddir_m4, 'src/m4')
            bitbake('mdadm m4 -c clean')
            # Check that buildclean task is run before clean for B == S
            assertNoFile(tempdir_mdadm, 'mdadm')
            # Check that buildclean task is not run before clean for B != S
            assertFile(builddir_m4, 'src/m4')
        finally:
            self.delete_recipeinc('m4')

    def test_devtool_modify_invalid(self):
        # Try modifying some recipes
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

        testrecipes = 'perf kernel-devsrc package-index core-image-minimal meta-toolchain packagegroup-core-sdk meta-ide-support'.split()
        # Find actual name of gcc-source since it now includes the version - crude, but good enough for this purpose
        result = runCmd('bitbake-layers show-recipes gcc-source*')
        for line in result.output.splitlines():
            # just match those lines that contain a real target
            m = re.match('(?P<recipe>^[a-zA-Z0-9.-]+)(?P<colon>:$)', line)
            if m:
                testrecipes.append(m.group('recipe'))
        for testrecipe in testrecipes:
            # Check it's a valid recipe
            bitbake('%s -e' % testrecipe)
            # devtool extract should fail
            result = runCmd('devtool extract %s %s' % (testrecipe, os.path.join(tempdir, testrecipe)), ignore_status=True)
            self.assertNotEqual(result.status, 0, 'devtool extract on %s should have failed. devtool output: %s' % (testrecipe, result.output))
            self.assertNotIn('Fetching ', result.output, 'devtool extract on %s should have errored out before trying to fetch' % testrecipe)
            self.assertIn('ERROR: ', result.output, 'devtool extract on %s should have given an ERROR' % testrecipe)
            # devtool modify should fail
            result = runCmd('devtool modify %s -x %s' % (testrecipe, os.path.join(tempdir, testrecipe)), ignore_status=True)
            self.assertNotEqual(result.status, 0, 'devtool modify on %s should have failed. devtool output: %s' %  (testrecipe, result.output))
            self.assertIn('ERROR: ', result.output, 'devtool modify on %s should have given an ERROR' % testrecipe)

    def test_devtool_modify_native(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        # Try modifying some recipes
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

        bbclassextended = False
        inheritnative = False
        testrecipes = 'mtools-native apt-native desktop-file-utils-native'.split()
        for testrecipe in testrecipes:
            checkextend = 'native' in (get_bb_var('BBCLASSEXTEND', testrecipe) or '').split()
            if not bbclassextended:
                bbclassextended = checkextend
            if not inheritnative:
                inheritnative = not checkextend
            result = runCmd('devtool modify %s -x %s' % (testrecipe, os.path.join(tempdir, testrecipe)))
            self.assertNotIn('ERROR: ', result.output, 'ERROR in devtool modify output: %s' % result.output)
            result = runCmd('devtool build %s' % testrecipe)
            self.assertNotIn('ERROR: ', result.output, 'ERROR in devtool build output: %s' % result.output)
            result = runCmd('devtool reset %s' % testrecipe)
            self.assertNotIn('ERROR: ', result.output, 'ERROR in devtool reset output: %s' % result.output)

        self.assertTrue(bbclassextended, 'None of these recipes are BBCLASSEXTENDed to native - need to adjust testrecipes list: %s' % ', '.join(testrecipes))
        self.assertTrue(inheritnative, 'None of these recipes do "inherit native" - need to adjust testrecipes list: %s' % ', '.join(testrecipes))


    def test_devtool_modify_git(self):
        # Check preconditions
        testrecipe = 'psplash'
        src_uri = get_bb_var('SRC_URI', testrecipe)
        self.assertIn('git://', src_uri, 'This test expects the %s recipe to be a git recipe' % testrecipe)
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertExists(os.path.join(tempdir, 'Makefile.am'), 'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created. devtool output: %s' % result.output)
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', 'psplash_*.bbappend'))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Check git repo
        self._check_src_repo(tempdir)
        # Try building
        bitbake(testrecipe)

    def test_devtool_modify_localfiles(self):
        # Check preconditions
        testrecipe = 'lighttpd'
        src_uri = (get_bb_var('SRC_URI', testrecipe) or '').split()
        foundlocal = False
        for item in src_uri:
            if item.startswith('file://') and '.patch' not in item:
                foundlocal = True
                break
        self.assertTrue(foundlocal, 'This test expects the %s recipe to fetch local files and it seems that it no longer does' % testrecipe)
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertExists(os.path.join(tempdir, 'configure.ac'), 'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', '%s_*.bbappend' % testrecipe))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Try building
        bitbake(testrecipe)

    def test_devtool_modify_virtual(self):
        # Try modifying a virtual recipe
        virtrecipe = 'virtual/make'
        realrecipe = 'make'
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (virtrecipe, tempdir))
        self.assertExists(os.path.join(tempdir, 'Makefile.am'), 'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', '%s_*.bbappend' % realrecipe))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)
        # Test devtool status
        result = runCmd('devtool status')
        self.assertNotIn(virtrecipe, result.output)
        self.assertIn(realrecipe, result.output)
        # Check git repo
        self._check_src_repo(tempdir)
        # This is probably sufficient

class DevtoolUpdateTests(DevtoolBase):

    def test_devtool_update_recipe(self):
        # Check preconditions
        testrecipe = 'minicom'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        self.assertNotIn('git://', src_uri, 'This test expects the %s recipe to NOT be a git recipe' % testrecipe)
        self._check_repo_status(os.path.dirname(recipefile), [])
        # First, modify a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        # We don't use -x here so that we test the behaviour of devtool modify without it
        result = runCmd('devtool modify %s %s' % (testrecipe, tempdir))
        # Check git repo
        self._check_src_repo(tempdir)
        # Add a couple of commits
        # FIXME: this only tests adding, need to also test update and remove
        result = runCmd('echo "Additional line" >> README', cwd=tempdir)
        result = runCmd('git commit -a -m "Change the README"', cwd=tempdir)
        result = runCmd('echo "A new file" > devtool-new-file', cwd=tempdir)
        result = runCmd('git add devtool-new-file', cwd=tempdir)
        result = runCmd('git commit -m "Add a new file"', cwd=tempdir)
        self.add_command_to_tearDown('cd %s; rm %s/*.patch; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('??', '.*/0001-Change-the-README.patch$'),
                           ('??', '.*/0002-Add-a-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    def test_devtool_update_recipe_git(self):
        # Check preconditions
        testrecipe = 'mtd-utils'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        self.assertIn('git://', src_uri, 'This test expects the %s recipe to be a git recipe' % testrecipe)
        patches = []
        for entry in src_uri.split():
            if entry.startswith('file://') and entry.endswith('.patch'):
                patches.append(entry[7:].split(';')[0])
        self.assertGreater(len(patches), 0, 'The %s recipe does not appear to contain any patches, so this test will not be effective' % testrecipe)
        self._check_repo_status(os.path.dirname(recipefile), [])
        # First, modify a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        # Check git repo
        self._check_src_repo(tempdir)
        # Add a couple of commits
        # FIXME: this only tests adding, need to also test update and remove
        result = runCmd('echo "# Additional line" >> Makefile.am', cwd=tempdir)
        result = runCmd('git commit -a -m "Change the Makefile"', cwd=tempdir)
        result = runCmd('echo "A new file" > devtool-new-file', cwd=tempdir)
        result = runCmd('git add devtool-new-file', cwd=tempdir)
        result = runCmd('git commit -m "Add a new file"', cwd=tempdir)
        self.add_command_to_tearDown('cd %s; rm -rf %s; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe -m srcrev %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile))] + \
                          [(' D', '.*/%s$' % patch) for patch in patches]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

        result = runCmd('git diff %s' % os.path.basename(recipefile), cwd=os.path.dirname(recipefile))
        addlines = ['SRCREV = ".*"', 'SRC_URI = "git://git.infradead.org/mtd-utils.git"']
        srcurilines = src_uri.split()
        srcurilines[0] = 'SRC_URI = "' + srcurilines[0]
        srcurilines.append('"')
        removelines = ['SRCREV = ".*"'] + srcurilines
        for line in result.output.splitlines():
            if line.startswith('+++') or line.startswith('---'):
                continue
            elif line.startswith('+'):
                matched = False
                for item in addlines:
                    if re.match(item, line[1:].strip()):
                        matched = True
                        break
                self.assertTrue(matched, 'Unexpected diff add line: %s' % line)
            elif line.startswith('-'):
                matched = False
                for item in removelines:
                    if re.match(item, line[1:].strip()):
                        matched = True
                        break
                self.assertTrue(matched, 'Unexpected diff remove line: %s' % line)
        # Now try with auto mode
        runCmd('cd %s; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        result = runCmd('git rev-parse --show-toplevel', cwd=os.path.dirname(recipefile))
        topleveldir = result.output.strip()
        relpatchpath = os.path.join(os.path.relpath(os.path.dirname(recipefile), topleveldir), testrecipe)
        expected_status = [(' M', os.path.relpath(recipefile, topleveldir)),
                           ('??', '%s/0001-Change-the-Makefile.patch' % relpatchpath),
                           ('??', '%s/0002-Add-a-new-file.patch' % relpatchpath)]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    def test_devtool_update_recipe_append(self):
        # Check preconditions
        testrecipe = 'mdadm'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        self.assertNotIn('git://', src_uri, 'This test expects the %s recipe to NOT be a git recipe' % testrecipe)
        self._check_repo_status(os.path.dirname(recipefile), [])
        # First, modify a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        tempsrcdir = os.path.join(tempdir, 'source')
        templayerdir = os.path.join(tempdir, 'layer')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempsrcdir))
        # Check git repo
        self._check_src_repo(tempsrcdir)
        # Add a commit
        result = runCmd("sed 's!\\(#define VERSION\\W*\"[^\"]*\\)\"!\\1-custom\"!' -i ReadMe.c", cwd=tempsrcdir)
        result = runCmd('git commit -a -m "Add our custom version"', cwd=tempsrcdir)
        self.add_command_to_tearDown('cd %s; rm -f %s/*.patch; git checkout .' % (os.path.dirname(recipefile), testrecipe))
        # Create a temporary layer and add it to bblayers.conf
        self._create_temp_layer(templayerdir, True, 'selftestupdaterecipe')
        # Create the bbappend
        result = runCmd('devtool update-recipe %s -a %s' % (testrecipe, templayerdir))
        self.assertNotIn('WARNING:', result.output)
        # Check recipe is still clean
        self._check_repo_status(os.path.dirname(recipefile), [])
        # Check bbappend was created
        splitpath = os.path.dirname(recipefile).split(os.sep)
        appenddir = os.path.join(templayerdir, splitpath[-2], splitpath[-1])
        bbappendfile = self._check_bbappend(testrecipe, recipefile, appenddir)
        patchfile = os.path.join(appenddir, testrecipe, '0001-Add-our-custom-version.patch')
        self.assertExists(patchfile, 'Patch file not created')

        # Check bbappend contents
        expectedlines = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n',
                         'SRC_URI += "file://0001-Add-our-custom-version.patch"\n',
                         '\n']
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, f.readlines())

        # Check we can run it again and bbappend isn't modified
        result = runCmd('devtool update-recipe %s -a %s' % (testrecipe, templayerdir))
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, f.readlines())
        # Drop new commit and check patch gets deleted
        result = runCmd('git reset HEAD^', cwd=tempsrcdir)
        result = runCmd('devtool update-recipe %s -a %s' % (testrecipe, templayerdir))
        self.assertNotExists(patchfile, 'Patch file not deleted')
        expectedlines2 = ['FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines2, f.readlines())
        # Put commit back and check we can run it if layer isn't in bblayers.conf
        os.remove(bbappendfile)
        result = runCmd('git commit -a -m "Add our custom version"', cwd=tempsrcdir)
        result = runCmd('bitbake-layers remove-layer %s' % templayerdir, cwd=self.builddir)
        result = runCmd('devtool update-recipe %s -a %s' % (testrecipe, templayerdir))
        self.assertIn('WARNING: Specified layer is not currently enabled in bblayers.conf', result.output)
        self.assertExists(patchfile, 'Patch file not created (with disabled layer)')
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, f.readlines())
        # Deleting isn't expected to work under these circumstances

    def test_devtool_update_recipe_append_git(self):
        # Check preconditions
        testrecipe = 'mtd-utils'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        self.assertIn('git://', src_uri, 'This test expects the %s recipe to be a git recipe' % testrecipe)
        for entry in src_uri.split():
            if entry.startswith('git://'):
                git_uri = entry
                break
        self._check_repo_status(os.path.dirname(recipefile), [])
        # First, modify a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        tempsrcdir = os.path.join(tempdir, 'source')
        templayerdir = os.path.join(tempdir, 'layer')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempsrcdir))
        # Check git repo
        self._check_src_repo(tempsrcdir)
        # Add a commit
        result = runCmd('echo "# Additional line" >> Makefile.am', cwd=tempsrcdir)
        result = runCmd('git commit -a -m "Change the Makefile"', cwd=tempsrcdir)
        self.add_command_to_tearDown('cd %s; rm -f %s/*.patch; git checkout .' % (os.path.dirname(recipefile), testrecipe))
        # Create a temporary layer
        os.makedirs(os.path.join(templayerdir, 'conf'))
        with open(os.path.join(templayerdir, 'conf', 'layer.conf'), 'w') as f:
            f.write('BBPATH .= ":${LAYERDIR}"\n')
            f.write('BBFILES += "${LAYERDIR}/recipes-*/*/*.bbappend"\n')
            f.write('BBFILE_COLLECTIONS += "oeselftesttemplayer"\n')
            f.write('BBFILE_PATTERN_oeselftesttemplayer = "^${LAYERDIR}/"\n')
            f.write('BBFILE_PRIORITY_oeselftesttemplayer = "999"\n')
            f.write('BBFILE_PATTERN_IGNORE_EMPTY_oeselftesttemplayer = "1"\n')
            f.write('LAYERSERIES_COMPAT_oeselftesttemplayer = "${LAYERSERIES_COMPAT_core}"\n')
        self.add_command_to_tearDown('bitbake-layers remove-layer %s || true' % templayerdir)
        result = runCmd('bitbake-layers add-layer %s' % templayerdir, cwd=self.builddir)
        # Create the bbappend
        result = runCmd('devtool update-recipe -m srcrev %s -a %s' % (testrecipe, templayerdir))
        self.assertNotIn('WARNING:', result.output)
        # Check recipe is still clean
        self._check_repo_status(os.path.dirname(recipefile), [])
        # Check bbappend was created
        splitpath = os.path.dirname(recipefile).split(os.sep)
        appenddir = os.path.join(templayerdir, splitpath[-2], splitpath[-1])
        bbappendfile = self._check_bbappend(testrecipe, recipefile, appenddir)
        self.assertNotExists(os.path.join(appenddir, testrecipe), 'Patch directory should not be created')

        # Check bbappend contents
        result = runCmd('git rev-parse HEAD', cwd=tempsrcdir)
        expectedlines = set(['SRCREV = "%s"\n' % result.output,
                             '\n',
                             'SRC_URI = "%s"\n' % git_uri,
                             '\n'])
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, set(f.readlines()))

        # Check we can run it again and bbappend isn't modified
        result = runCmd('devtool update-recipe -m srcrev %s -a %s' % (testrecipe, templayerdir))
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, set(f.readlines()))
        # Drop new commit and check SRCREV changes
        result = runCmd('git reset HEAD^', cwd=tempsrcdir)
        result = runCmd('devtool update-recipe -m srcrev %s -a %s' % (testrecipe, templayerdir))
        self.assertNotExists(os.path.join(appenddir, testrecipe), 'Patch directory should not be created')
        result = runCmd('git rev-parse HEAD', cwd=tempsrcdir)
        expectedlines = set(['SRCREV = "%s"\n' % result.output,
                             '\n',
                             'SRC_URI = "%s"\n' % git_uri,
                             '\n'])
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, set(f.readlines()))
        # Put commit back and check we can run it if layer isn't in bblayers.conf
        os.remove(bbappendfile)
        result = runCmd('git commit -a -m "Change the Makefile"', cwd=tempsrcdir)
        result = runCmd('bitbake-layers remove-layer %s' % templayerdir, cwd=self.builddir)
        result = runCmd('devtool update-recipe -m srcrev %s -a %s' % (testrecipe, templayerdir))
        self.assertIn('WARNING: Specified layer is not currently enabled in bblayers.conf', result.output)
        self.assertNotExists(os.path.join(appenddir, testrecipe), 'Patch directory should not be created')
        result = runCmd('git rev-parse HEAD', cwd=tempsrcdir)
        expectedlines = set(['SRCREV = "%s"\n' % result.output,
                             '\n',
                             'SRC_URI = "%s"\n' % git_uri,
                             '\n'])
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, set(f.readlines()))
        # Deleting isn't expected to work under these circumstances

    def test_devtool_update_recipe_local_files(self):
        """Check that local source files are copied over instead of patched"""
        testrecipe = 'makedevs'
        recipefile = get_bb_var('FILE', testrecipe)
        # Setup srctree for modifying the recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be
        # building it)
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        # Check git repo
        self._check_src_repo(tempdir)
        # Try building just to ensure we haven't broken that
        bitbake("%s" % testrecipe)
        # Edit / commit local source
        runCmd('echo "/* Foobar */" >> oe-local-files/makedevs.c', cwd=tempdir)
        runCmd('echo "Foo" > oe-local-files/new-local', cwd=tempdir)
        runCmd('echo "Bar" > new-file', cwd=tempdir)
        runCmd('git add new-file', cwd=tempdir)
        runCmd('git commit -m "Add new file"', cwd=tempdir)
        self.add_command_to_tearDown('cd %s; git clean -fd .; git checkout .' %
                                     os.path.dirname(recipefile))
        runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           (' M', '.*/makedevs/makedevs.c$'),
                           ('??', '.*/makedevs/new-local$'),
                           ('??', '.*/makedevs/0001-Add-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    def test_devtool_update_recipe_local_files_2(self):
        """Check local source files support when oe-local-files is in Git"""
        testrecipe = 'devtool-test-local'
        recipefile = get_bb_var('FILE', testrecipe)
        recipedir = os.path.dirname(recipefile)
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains uncommitted changes' % testrecipe)
        # Setup srctree for modifying the recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        # Check git repo
        self._check_src_repo(tempdir)
        # Add oe-local-files to Git
        runCmd('rm oe-local-files/.gitignore', cwd=tempdir)
        runCmd('git add oe-local-files', cwd=tempdir)
        runCmd('git commit -m "Add local sources"', cwd=tempdir)
        # Edit / commit local sources
        runCmd('echo "# Foobar" >> oe-local-files/file1', cwd=tempdir)
        runCmd('git commit -am "Edit existing file"', cwd=tempdir)
        runCmd('git rm oe-local-files/file2', cwd=tempdir)
        runCmd('git commit -m"Remove file"', cwd=tempdir)
        runCmd('echo "Foo" > oe-local-files/new-local', cwd=tempdir)
        runCmd('git add oe-local-files/new-local', cwd=tempdir)
        runCmd('git commit -m "Add new local file"', cwd=tempdir)
        runCmd('echo "Gar" > new-file', cwd=tempdir)
        runCmd('git add new-file', cwd=tempdir)
        runCmd('git commit -m "Add new file"', cwd=tempdir)
        self.add_command_to_tearDown('cd %s; git clean -fd .; git checkout .' %
                                     os.path.dirname(recipefile))
        # Checkout unmodified file to working copy -> devtool should still pick
        # the modified version from HEAD
        runCmd('git checkout HEAD^ -- oe-local-files/file1', cwd=tempdir)
        runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           (' M', '.*/file1$'),
                           (' D', '.*/file2$'),
                           ('??', '.*/new-local$'),
                           ('??', '.*/0001-Add-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    def test_devtool_update_recipe_local_files_3(self):
        # First, modify the recipe
        testrecipe = 'devtool-test-localonly'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s' % testrecipe)
        # Modify one file
        runCmd('echo "Another line" >> file2', cwd=os.path.join(self.workspacedir, 'sources', testrecipe, 'oe-local-files'))
        self.add_command_to_tearDown('cd %s; rm %s/*; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s/file2$' % testrecipe)]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    def test_devtool_update_recipe_local_patch_gz(self):
        # First, modify the recipe
        testrecipe = 'devtool-test-patch-gz'
        if get_bb_var('DISTRO') == 'poky-tiny':
            self.skipTest("The DISTRO 'poky-tiny' does not provide the dependencies needed by %s" % testrecipe)
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s' % testrecipe)
        # Modify one file
        srctree = os.path.join(self.workspacedir, 'sources', testrecipe)
        runCmd('echo "Another line" >> README', cwd=srctree)
        runCmd('git commit -a --amend --no-edit', cwd=srctree)
        self.add_command_to_tearDown('cd %s; rm %s/*; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s/readme.patch.gz$' % testrecipe)]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        patch_gz = os.path.join(os.path.dirname(recipefile), testrecipe, 'readme.patch.gz')
        result = runCmd('file %s' % patch_gz)
        if 'gzip compressed data' not in result.output:
            self.fail('New patch file is not gzipped - file reports:\n%s' % result.output)

    def test_devtool_update_recipe_local_files_subdir(self):
        # Try devtool update-recipe on a recipe that has a file with subdir= set in
        # SRC_URI such that it overwrites a file that was in an archive that
        # was also in SRC_URI
        # First, modify the recipe
        testrecipe = 'devtool-test-subdir'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s' % testrecipe)
        testfile = os.path.join(self.workspacedir, 'sources', testrecipe, 'testfile')
        self.assertExists(testfile, 'Extracted source could not be found')
        with open(testfile, 'r') as f:
            contents = f.read().rstrip()
        self.assertEqual(contents, 'Modified version', 'File has apparently not been overwritten as it should have been')
        # Test devtool update-recipe without modifying any files
        self.add_command_to_tearDown('cd %s; rm %s/*; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = []
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

class DevtoolExtractTests(DevtoolBase):

    def test_devtool_extract(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        # Try devtool extract
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool extract matchbox-terminal %s' % tempdir)
        self.assertExists(os.path.join(tempdir, 'Makefile.am'), 'Extracted source could not be found')
        self._check_src_repo(tempdir)

    def test_devtool_extract_virtual(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        # Try devtool extract
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool extract virtual/make %s' % tempdir)
        self.assertExists(os.path.join(tempdir, 'Makefile.am'), 'Extracted source could not be found')
        self._check_src_repo(tempdir)

    def test_devtool_reset_all(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        testrecipe1 = 'mdadm'
        testrecipe2 = 'cronie'
        result = runCmd('devtool modify -x %s %s' % (testrecipe1, os.path.join(tempdir, testrecipe1)))
        result = runCmd('devtool modify -x %s %s' % (testrecipe2, os.path.join(tempdir, testrecipe2)))
        result = runCmd('devtool build %s' % testrecipe1)
        result = runCmd('devtool build %s' % testrecipe2)
        stampprefix1 = get_bb_var('STAMP', testrecipe1)
        self.assertTrue(stampprefix1, 'Unable to get STAMP value for recipe %s' % testrecipe1)
        stampprefix2 = get_bb_var('STAMP', testrecipe2)
        self.assertTrue(stampprefix2, 'Unable to get STAMP value for recipe %s' % testrecipe2)
        result = runCmd('devtool reset -a')
        self.assertIn(testrecipe1, result.output)
        self.assertIn(testrecipe2, result.output)
        result = runCmd('devtool status')
        self.assertNotIn(testrecipe1, result.output)
        self.assertNotIn(testrecipe2, result.output)
        matches1 = glob.glob(stampprefix1 + '*')
        self.assertFalse(matches1, 'Stamp files exist for recipe %s that should have been cleaned' % testrecipe1)
        matches2 = glob.glob(stampprefix2 + '*')
        self.assertFalse(matches2, 'Stamp files exist for recipe %s that should have been cleaned' % testrecipe2)

    def test_devtool_deploy_target(self):
        # NOTE: Whilst this test would seemingly be better placed as a runtime test,
        # unfortunately the runtime tests run under bitbake and you can't run
        # devtool within bitbake (since devtool needs to run bitbake itself).
        # Additionally we are testing build-time functionality as well, so
        # really this has to be done as an oe-selftest test.
        #
        # Check preconditions
        machine = get_bb_var('MACHINE')
        if not machine.startswith('qemu'):
            self.skipTest('This test only works with qemu machines')
        if not os.path.exists('/etc/runqemu-nosudo'):
            self.skipTest('You must set up tap devices with scripts/runqemu-gen-tapdevs before running this test')
        result = runCmd('PATH="$PATH:/sbin:/usr/sbin" ip tuntap show', ignore_status=True)
        if result.status != 0:
            result = runCmd('PATH="$PATH:/sbin:/usr/sbin" ifconfig -a', ignore_status=True)
            if result.status != 0:
                self.skipTest('Failed to determine if tap devices exist with ifconfig or ip: %s' % result.output)
        for line in result.output.splitlines():
            if line.startswith('tap'):
                break
        else:
            self.skipTest('No tap devices found - you must set up tap devices with scripts/runqemu-gen-tapdevs before running this test')
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        # Definitions
        testrecipe = 'mdadm'
        testfile = '/sbin/mdadm'
        testimage = 'oe-selftest-image'
        testcommand = '/sbin/mdadm --help'
        # Build an image to run
        bitbake("%s qemu-native qemu-helper-native" % testimage)
        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        self.add_command_to_tearDown('bitbake -c clean %s' % testimage)
        self.add_command_to_tearDown('rm -f %s/%s*' % (deploy_dir_image, testimage))
        # Clean recipe so the first deploy will fail
        bitbake("%s -c clean" % testrecipe)
        # Try devtool modify
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        # Test that deploy-target at this point fails (properly)
        result = runCmd('devtool deploy-target -n %s root@localhost' % testrecipe, ignore_status=True)
        self.assertNotEqual(result.output, 0, 'devtool deploy-target should have failed, output: %s' % result.output)
        self.assertNotIn(result.output, 'Traceback', 'devtool deploy-target should have failed with a proper error not a traceback, output: %s' % result.output)
        result = runCmd('devtool build %s' % testrecipe)
        # First try a dry-run of deploy-target
        result = runCmd('devtool deploy-target -n %s root@localhost' % testrecipe)
        self.assertIn('  %s' % testfile, result.output)
        # Boot the image
        with runqemu(testimage) as qemu:
            # Now really test deploy-target
            result = runCmd('devtool deploy-target -c %s root@%s' % (testrecipe, qemu.ip))
            # Run a test command to see if it was installed properly
            sshargs = '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
            result = runCmd('ssh %s root@%s %s' % (sshargs, qemu.ip, testcommand))
            # Check if it deployed all of the files with the right ownership/perms
            # First look on the host - need to do this under pseudo to get the correct ownership/perms
            bb_vars = get_bb_vars(['D', 'FAKEROOTENV', 'FAKEROOTCMD'], testrecipe)
            installdir = bb_vars['D']
            fakerootenv = bb_vars['FAKEROOTENV']
            fakerootcmd = bb_vars['FAKEROOTCMD']
            result = runCmd('%s %s find . -type f -exec ls -l {} \\;' % (fakerootenv, fakerootcmd), cwd=installdir)
            filelist1 = self._process_ls_output(result.output)

            # Now look on the target
            tempdir2 = tempfile.mkdtemp(prefix='devtoolqa')
            self.track_for_cleanup(tempdir2)
            tmpfilelist = os.path.join(tempdir2, 'files.txt')
            with open(tmpfilelist, 'w') as f:
                for line in filelist1:
                    splitline = line.split()
                    f.write(splitline[-1] + '\n')
            result = runCmd('cat %s | ssh -q %s root@%s \'xargs ls -l\'' % (tmpfilelist, sshargs, qemu.ip))
            filelist2 = self._process_ls_output(result.output)
            filelist1.sort(key=lambda item: item.split()[-1])
            filelist2.sort(key=lambda item: item.split()[-1])
            self.assertEqual(filelist1, filelist2)
            # Test undeploy-target
            result = runCmd('devtool undeploy-target -c %s root@%s' % (testrecipe, qemu.ip))
            result = runCmd('ssh %s root@%s %s' % (sshargs, qemu.ip, testcommand), ignore_status=True)
            self.assertNotEqual(result, 0, 'undeploy-target did not remove command as it should have')

    def test_devtool_build_image(self):
        """Test devtool build-image plugin"""
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        image = 'core-image-minimal'
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % image)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        bitbake('%s -c clean' % image)
        # Add target and native recipes to workspace
        recipes = ['mdadm', 'parted-native']
        for recipe in recipes:
            tempdir = tempfile.mkdtemp(prefix='devtoolqa')
            self.track_for_cleanup(tempdir)
            self.add_command_to_tearDown('bitbake -c clean %s' % recipe)
            runCmd('devtool modify %s -x %s' % (recipe, tempdir))
        # Try to build image
        result = runCmd('devtool build-image %s' % image)
        self.assertNotEqual(result, 0, 'devtool build-image failed')
        # Check if image contains expected packages
        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        image_link_name = get_bb_var('IMAGE_LINK_NAME', image)
        reqpkgs = [item for item in recipes if not item.endswith('-native')]
        with open(os.path.join(deploy_dir_image, image_link_name + '.manifest'), 'r') as f:
            for line in f:
                splitval = line.split()
                if splitval:
                    pkg = splitval[0]
                    if pkg in reqpkgs:
                        reqpkgs.remove(pkg)
        if reqpkgs:
            self.fail('The following packages were not present in the image as expected: %s' % ', '.join(reqpkgs))

class DevtoolUpgradeTests(DevtoolBase):

    def test_devtool_upgrade(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # Check parameters
        result = runCmd('devtool upgrade -h')
        for param in 'recipename srctree --version -V --branch -b --keep-temp --no-patch'.split():
            self.assertIn(param, result.output)
        # For the moment, we are using a real recipe.
        recipe = 'devtool-upgrade-test1'
        version = '1.6.0'
        oldrecipefile = get_bb_var('FILE', recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        # Check that recipe is not already under devtool control
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        # Check upgrade. Code does not check if new PV is older or newer that current PV, so, it may be that
        # we are downgrading instead of upgrading.
        result = runCmd('devtool upgrade %s %s -V %s' % (recipe, tempdir, version))
        # Check if srctree at least is populated
        self.assertTrue(len(os.listdir(tempdir)) > 0, 'srctree (%s) should be populated with new (%s) source code' % (tempdir, version))
        # Check new recipe subdirectory is present
        self.assertExists(os.path.join(self.workspacedir, 'recipes', recipe, '%s-%s' % (recipe, version)), 'Recipe folder should exist')
        # Check new recipe file is present
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipe, '%s_%s.bb' % (recipe, version))
        self.assertExists(newrecipefile, 'Recipe file should exist after upgrade')
        # Check devtool status and make sure recipe is present
        result = runCmd('devtool status')
        self.assertIn(recipe, result.output)
        self.assertIn(tempdir, result.output)
        # Check recipe got changed as expected
        with open(oldrecipefile + '.upgraded', 'r') as f:
            desiredlines = f.readlines()
        with open(newrecipefile, 'r') as f:
            newlines = f.readlines()
        self.assertEqual(desiredlines, newlines)
        # Check devtool reset recipe
        result = runCmd('devtool reset %s -n' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after resetting')

    def test_devtool_upgrade_git(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        recipe = 'devtool-upgrade-test2'
        commit = '6cc6077a36fe2648a5f993fe7c16c9632f946517'
        oldrecipefile = get_bb_var('FILE', recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        # Check that recipe is not already under devtool control
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        # Check upgrade
        result = runCmd('devtool upgrade %s %s -S %s' % (recipe, tempdir, commit))
        # Check if srctree at least is populated
        self.assertTrue(len(os.listdir(tempdir)) > 0, 'srctree (%s) should be populated with new (%s) source code' % (tempdir, commit))
        # Check new recipe file is present
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipe, os.path.basename(oldrecipefile))
        self.assertExists(newrecipefile, 'Recipe file should exist after upgrade')
        # Check devtool status and make sure recipe is present
        result = runCmd('devtool status')
        self.assertIn(recipe, result.output)
        self.assertIn(tempdir, result.output)
        # Check recipe got changed as expected
        with open(oldrecipefile + '.upgraded', 'r') as f:
            desiredlines = f.readlines()
        with open(newrecipefile, 'r') as f:
            newlines = f.readlines()
        self.assertEqual(desiredlines, newlines)
        # Check devtool reset recipe
        result = runCmd('devtool reset %s -n' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after resetting')

    def test_devtool_layer_plugins(self):
        """Test that devtool can use plugins from other layers.

        This test executes the selftest-reverse command from meta-selftest."""

        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

        s = "Microsoft Made No Profit From Anyone's Zunes Yo"
        result = runCmd("devtool --quiet selftest-reverse \"%s\"" % s)
        self.assertEqual(result.output, s[::-1])

    def _copy_file_with_cleanup(self, srcfile, basedstdir, *paths):
        dstdir = basedstdir
        self.assertExists(dstdir)
        for p in paths:
            dstdir = os.path.join(dstdir, p)
            if not os.path.exists(dstdir):
                os.makedirs(dstdir)
                if p == "lib":
                    # Can race with other tests
                    self.add_command_to_tearDown('rmdir --ignore-fail-on-non-empty %s' % dstdir)
                else:
                    self.track_for_cleanup(dstdir)
        dstfile = os.path.join(dstdir, os.path.basename(srcfile))
        if srcfile != dstfile:
            shutil.copy(srcfile, dstfile)
            self.track_for_cleanup(dstfile)

    def test_devtool_load_plugin(self):
        """Test that devtool loads only the first found plugin in BBPATH."""

        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

        devtool = runCmd("which devtool")
        fromname = runCmd("devtool --quiet pluginfile")
        srcfile = fromname.output
        bbpath = get_bb_var('BBPATH')
        searchpath = bbpath.split(':') + [os.path.dirname(devtool.output)]
        plugincontent = []
        with open(srcfile) as fh:
            plugincontent = fh.readlines()
        try:
            self.assertIn('meta-selftest', srcfile, 'wrong bbpath plugin found')
            for path in searchpath:
                self._copy_file_with_cleanup(srcfile, path, 'lib', 'devtool')
            result = runCmd("devtool --quiet count")
            self.assertEqual(result.output, '1')
            result = runCmd("devtool --quiet multiloaded")
            self.assertEqual(result.output, "no")
            for path in searchpath:
                result = runCmd("devtool --quiet bbdir")
                self.assertEqual(result.output, path)
                os.unlink(os.path.join(result.output, 'lib', 'devtool', 'bbpath.py'))
        finally:
            with open(srcfile, 'w') as fh:
                fh.writelines(plugincontent)

    def _setup_test_devtool_finish_upgrade(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # Use a "real" recipe from meta-selftest
        recipe = 'devtool-upgrade-test1'
        oldversion = '1.5.3'
        newversion = '1.6.0'
        oldrecipefile = get_bb_var('FILE', recipe)
        recipedir = os.path.dirname(oldrecipefile)
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains uncommitted changes' % recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        # Check that recipe is not already under devtool control
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        # Do the upgrade
        result = runCmd('devtool upgrade %s %s -V %s' % (recipe, tempdir, newversion))
        # Check devtool status and make sure recipe is present
        result = runCmd('devtool status')
        self.assertIn(recipe, result.output)
        self.assertIn(tempdir, result.output)
        # Make a change to the source
        result = runCmd('sed -i \'/^#include "pv.h"/a \\/* Here is a new comment *\\/\' src/pv/number.c', cwd=tempdir)
        result = runCmd('git status --porcelain', cwd=tempdir)
        self.assertIn('M src/pv/number.c', result.output)
        result = runCmd('git commit src/pv/number.c -m "Add a comment to the code"', cwd=tempdir)
        # Check if patch is there
        recipedir = os.path.dirname(oldrecipefile)
        olddir = os.path.join(recipedir, recipe + '-' + oldversion)
        patchfn = '0001-Add-a-note-line-to-the-quick-reference.patch'
        backportedpatchfn = 'backported.patch'
        self.assertExists(os.path.join(olddir, patchfn), 'Original patch file does not exist')
        self.assertExists(os.path.join(olddir, backportedpatchfn), 'Backported patch file does not exist')
        return recipe, oldrecipefile, recipedir, olddir, newversion, patchfn, backportedpatchfn

    def test_devtool_finish_upgrade_origlayer(self):
        recipe, oldrecipefile, recipedir, olddir, newversion, patchfn, backportedpatchfn = self._setup_test_devtool_finish_upgrade()
        # Ensure the recipe is where we think it should be (so that cleanup doesn't trash things)
        self.assertIn('/meta-selftest/', recipedir)
        # Try finish to the original layer
        self.add_command_to_tearDown('rm -rf %s ; cd %s ; git checkout %s' % (recipedir, os.path.dirname(recipedir), recipedir))
        result = runCmd('devtool finish %s meta-selftest' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after finish')
        self.assertNotExists(oldrecipefile, 'Old recipe file should have been deleted but wasn\'t')
        self.assertNotExists(os.path.join(olddir, patchfn), 'Old patch file should have been deleted but wasn\'t')
        self.assertNotExists(os.path.join(olddir, backportedpatchfn), 'Old backported patch file should have been deleted but wasn\'t')
        newrecipefile = os.path.join(recipedir, '%s_%s.bb' % (recipe, newversion))
        newdir = os.path.join(recipedir, recipe + '-' + newversion)
        self.assertExists(newrecipefile, 'New recipe file should have been copied into existing layer but wasn\'t')
        self.assertExists(os.path.join(newdir, patchfn), 'Patch file should have been copied into new directory but wasn\'t')
        self.assertNotExists(os.path.join(newdir, backportedpatchfn), 'Backported patch file should not have been copied into new directory but was')
        self.assertExists(os.path.join(newdir, '0002-Add-a-comment-to-the-code.patch'), 'New patch file should have been created but wasn\'t')
        with open(newrecipefile, 'r') as f:
            newcontent = f.read()
        self.assertNotIn(backportedpatchfn, newcontent, "Backported patch should have been removed from the recipe but wasn't")
        self.assertIn(patchfn, newcontent, "Old patch should have not been removed from the recipe but was")
        self.assertIn("0002-Add-a-comment-to-the-code.patch", newcontent, "New patch should have been added to the recipe but wasn't")
        self.assertIn("http://www.ivarch.com/programs/sources/pv-${PV}.tar.gz", newcontent, "New recipe no longer has upstream source in SRC_URI")


    def test_devtool_finish_upgrade_otherlayer(self):
        recipe, oldrecipefile, recipedir, olddir, newversion, patchfn, backportedpatchfn = self._setup_test_devtool_finish_upgrade()
        # Ensure the recipe is where we think it should be (so that cleanup doesn't trash things)
        self.assertIn('/meta-selftest/', recipedir)
        # Try finish to a different layer - should create a bbappend
        # This cleanup isn't strictly necessary but do it anyway just in case it goes wrong and writes to here
        self.add_command_to_tearDown('rm -rf %s ; cd %s ; git checkout %s' % (recipedir, os.path.dirname(recipedir), recipedir))
        oe_core_dir = os.path.join(get_bb_var('COREBASE'), 'meta')
        newrecipedir = os.path.join(oe_core_dir, 'recipes-test', 'devtool')
        newrecipefile = os.path.join(newrecipedir, '%s_%s.bb' % (recipe, newversion))
        self.track_for_cleanup(newrecipedir)
        result = runCmd('devtool finish %s oe-core' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after finish')
        self.assertExists(oldrecipefile, 'Old recipe file should not have been deleted')
        self.assertExists(os.path.join(olddir, patchfn), 'Old patch file should not have been deleted')
        self.assertExists(os.path.join(olddir, backportedpatchfn), 'Old backported patch file should not have been deleted')
        newdir = os.path.join(newrecipedir, recipe + '-' + newversion)
        self.assertExists(newrecipefile, 'New recipe file should have been copied into existing layer but wasn\'t')
        self.assertExists(os.path.join(newdir, patchfn), 'Patch file should have been copied into new directory but wasn\'t')
        self.assertNotExists(os.path.join(newdir, backportedpatchfn), 'Backported patch file should not have been copied into new directory but was')
        self.assertExists(os.path.join(newdir, '0002-Add-a-comment-to-the-code.patch'), 'New patch file should have been created but wasn\'t')
        with open(newrecipefile, 'r') as f:
            newcontent = f.read()
        self.assertNotIn(backportedpatchfn, newcontent, "Backported patch should have been removed from the recipe but wasn't")
        self.assertIn(patchfn, newcontent, "Old patch should have not been removed from the recipe but was")
        self.assertIn("0002-Add-a-comment-to-the-code.patch", newcontent, "New patch should have been added to the recipe but wasn't")
        self.assertIn("http://www.ivarch.com/programs/sources/pv-${PV}.tar.gz", newcontent, "New recipe no longer has upstream source in SRC_URI")

    def _setup_test_devtool_finish_modify(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        # Try modifying a recipe
        self.track_for_cleanup(self.workspacedir)
        recipe = 'mdadm'
        oldrecipefile = get_bb_var('FILE', recipe)
        recipedir = os.path.dirname(oldrecipefile)
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains uncommitted changes' % recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s %s' % (recipe, tempdir))
        self.assertExists(os.path.join(tempdir, 'Makefile'), 'Extracted source could not be found')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(recipe, result.output)
        self.assertIn(tempdir, result.output)
        # Make a change to the source
        result = runCmd('sed -i \'/^#include "mdadm.h"/a \\/* Here is a new comment *\\/\' maps.c', cwd=tempdir)
        result = runCmd('git status --porcelain', cwd=tempdir)
        self.assertIn('M maps.c', result.output)
        result = runCmd('git commit maps.c -m "Add a comment to the code"', cwd=tempdir)
        for entry in os.listdir(recipedir):
            filesdir = os.path.join(recipedir, entry)
            if os.path.isdir(filesdir):
                break
        else:
            self.fail('Unable to find recipe files directory for %s' % recipe)
        return recipe, oldrecipefile, recipedir, filesdir

    def test_devtool_finish_modify_origlayer(self):
        recipe, oldrecipefile, recipedir, filesdir = self._setup_test_devtool_finish_modify()
        # Ensure the recipe is where we think it should be (so that cleanup doesn't trash things)
        self.assertIn('/meta/', recipedir)
        # Try finish to the original layer
        self.add_command_to_tearDown('rm -rf %s ; cd %s ; git checkout %s' % (recipedir, os.path.dirname(recipedir), recipedir))
        result = runCmd('devtool finish %s meta' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after finish')
        expected_status = [(' M', '.*/%s$' % os.path.basename(oldrecipefile)),
                           ('??', '.*/.*-Add-a-comment-to-the-code.patch$')]
        self._check_repo_status(recipedir, expected_status)

    def test_devtool_finish_modify_otherlayer(self):
        recipe, oldrecipefile, recipedir, filesdir = self._setup_test_devtool_finish_modify()
        # Ensure the recipe is where we think it should be (so that cleanup doesn't trash things)
        self.assertIn('/meta/', recipedir)
        relpth = os.path.relpath(recipedir, os.path.join(get_bb_var('COREBASE'), 'meta'))
        appenddir = os.path.join(get_test_layer(), relpth)
        self.track_for_cleanup(appenddir)
        # Try finish to the original layer
        self.add_command_to_tearDown('rm -rf %s ; cd %s ; git checkout %s' % (recipedir, os.path.dirname(recipedir), recipedir))
        result = runCmd('devtool finish %s meta-selftest' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after finish')
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains the following unexpected changes after finish:\n%s' % (recipe, result.output.strip()))
        recipefn = os.path.splitext(os.path.basename(oldrecipefile))[0]
        recipefn = recipefn.split('_')[0] + '_%'
        appendfile = os.path.join(appenddir, recipefn + '.bbappend')
        self.assertExists(appendfile, 'bbappend %s should have been created but wasn\'t' % appendfile)
        newdir = os.path.join(appenddir, recipe)
        files = os.listdir(newdir)
        foundpatch = None
        for fn in files:
            if fnmatch.fnmatch(fn, '*-Add-a-comment-to-the-code.patch'):
                foundpatch = fn
        if not foundpatch:
            self.fail('No patch file created next to bbappend')
        files.remove(foundpatch)
        if files:
            self.fail('Unexpected file(s) copied next to bbappend: %s' % ', '.join(files))

    def test_devtool_rename(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

        # First run devtool add
        # We already have this recipe in OE-Core, but that doesn't matter
        recipename = 'i2c-tools'
        recipever = '3.1.2'
        recipefile = os.path.join(self.workspacedir, 'recipes', recipename, '%s_%s.bb' % (recipename, recipever))
        url = 'http://downloads.yoctoproject.org/mirror/sources/i2c-tools-%s.tar.bz2' % recipever
        def add_recipe():
            result = runCmd('devtool add %s' % url)
            self.assertExists(recipefile, 'Expected recipe file not created')
            self.assertExists(os.path.join(self.workspacedir, 'sources', recipename), 'Source directory not created')
            checkvars = {}
            checkvars['S'] = None
            checkvars['SRC_URI'] = url.replace(recipever, '${PV}')
            self._test_recipe_contents(recipefile, checkvars, [])
        add_recipe()
        # Now rename it - change both name and version
        newrecipename = 'mynewrecipe'
        newrecipever = '456'
        newrecipefile = os.path.join(self.workspacedir, 'recipes', newrecipename, '%s_%s.bb' % (newrecipename, newrecipever))
        result = runCmd('devtool rename %s %s -V %s' % (recipename, newrecipename, newrecipever))
        self.assertExists(newrecipefile, 'Recipe file not renamed')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipename), 'Old recipe directory still exists')
        newsrctree = os.path.join(self.workspacedir, 'sources', newrecipename)
        self.assertExists(newsrctree, 'Source directory not renamed')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/%s-%s' % (recipename, recipever)
        checkvars['SRC_URI'] = url
        self._test_recipe_contents(newrecipefile, checkvars, [])
        # Try again - change just name this time
        result = runCmd('devtool reset -n %s' % newrecipename)
        shutil.rmtree(newsrctree)
        add_recipe()
        newrecipefile = os.path.join(self.workspacedir, 'recipes', newrecipename, '%s_%s.bb' % (newrecipename, recipever))
        result = runCmd('devtool rename %s %s' % (recipename, newrecipename))
        self.assertExists(newrecipefile, 'Recipe file not renamed')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipename), 'Old recipe directory still exists')
        self.assertExists(os.path.join(self.workspacedir, 'sources', newrecipename), 'Source directory not renamed')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/%s-${PV}' % recipename
        checkvars['SRC_URI'] = url.replace(recipever, '${PV}')
        self._test_recipe_contents(newrecipefile, checkvars, [])
        # Try again - change just version this time
        result = runCmd('devtool reset -n %s' % newrecipename)
        shutil.rmtree(newsrctree)
        add_recipe()
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipename, '%s_%s.bb' % (recipename, newrecipever))
        result = runCmd('devtool rename %s -V %s' % (recipename, newrecipever))
        self.assertExists(newrecipefile, 'Recipe file not renamed')
        self.assertExists(os.path.join(self.workspacedir, 'sources', recipename), 'Source directory no longer exists')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/${BPN}-%s' % recipever
        checkvars['SRC_URI'] = url
        self._test_recipe_contents(newrecipefile, checkvars, [])

    def test_devtool_virtual_kernel_modify(self):
        """
        Summary:        The purpose of this test case is to verify that
                        devtool modify works correctly when building
                        the kernel.
        Dependencies:   NA
        Steps:          1. Build kernel with bitbake.
                        2. Save the config file generated.
                        3. Clean the environment.
                        4. Use `devtool modify virtual/kernel` to validate following:
                           4.1 The source is checked out correctly.
                           4.2 The resulting configuration is the same as
                               what was get on step 2.
                           4.3 The Kernel can be build correctly.
                           4.4 Changes made on the source are reflected on the
                               subsequent builds.
                           4.5 Changes on the configuration are reflected on the
                               subsequent builds
         Expected:       devtool modify is able to checkout the source of the kernel
                         and modification to the source and configurations are reflected
                         when building the kernel.
         """
        kernel_provider = get_bb_var('PREFERRED_PROVIDER_virtual/kernel')
        # Clean up the environment
        bitbake('%s -c clean' % kernel_provider)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        tempdir_cfg = tempfile.mkdtemp(prefix='config_qa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(tempdir_cfg)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % kernel_provider)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        #Step 1
        #Here is just generated the config file instead of all the kernel to optimize the
        #time of executing this test case.
        bitbake('%s -c configure' % kernel_provider)
        bbconfig = os.path.join(get_bb_var('B', kernel_provider),'.config')
        #Step 2
        runCmd('cp %s %s' % (bbconfig, tempdir_cfg))
        self.assertExists(os.path.join(tempdir_cfg, '.config'), 'Could not copy .config file from kernel')

        tmpconfig = os.path.join(tempdir_cfg, '.config')
        #Step 3
        bitbake('%s -c clean' % kernel_provider)
        #Step 4.1
        runCmd('devtool modify virtual/kernel -x %s' % tempdir)
        self.assertExists(os.path.join(tempdir, 'Makefile'), 'Extracted source could not be found')
        #Step 4.2
        configfile = os.path.join(tempdir,'.config')
        diff = runCmd('diff %s %s' % (tmpconfig, configfile))
        self.assertEqual(0,diff.status,'Kernel .config file is not the same using bitbake and devtool')
        #Step 4.3
        #NOTE: virtual/kernel is mapped to kernel_provider
        result = runCmd('devtool build %s' % kernel_provider)
        self.assertEqual(0,result.status,'Cannot build kernel using `devtool build`')
        kernelfile = os.path.join(get_bb_var('KBUILD_OUTPUT', kernel_provider), 'vmlinux')
        self.assertExists(kernelfile, 'Kernel was not build correctly')

        #Modify the kernel source
        modfile = os.path.join(tempdir,'arch/x86/boot/header.S')
        modstring = "Use a boot loader. Devtool testing."
        modapplied = runCmd("sed -i 's/Use a boot loader./%s/' %s" % (modstring, modfile))
        self.assertEqual(0,modapplied.status,'Modification to %s on kernel source failed' % modfile)
        #Modify the configuration
        codeconfigfile = os.path.join(tempdir,'.config.new')
        modconfopt = "CONFIG_SG_POOL=n"
        modconf = runCmd("sed -i 's/CONFIG_SG_POOL=y/%s/' %s" % (modconfopt, codeconfigfile))
        self.assertEqual(0,modconf.status,'Modification to %s failed' % codeconfigfile)
        #Build again kernel with devtool
        rebuild = runCmd('devtool build %s' % kernel_provider)
        self.assertEqual(0,rebuild.status,'Fail to build kernel after modification of source and config')
        #Step 4.4
        bzimagename = 'bzImage-' + get_bb_var('KERNEL_VERSION_NAME', kernel_provider)
        bzimagefile = os.path.join(get_bb_var('D', kernel_provider),'boot', bzimagename)
        checkmodcode = runCmd("grep '%s' %s" % (modstring, bzimagefile))
        self.assertEqual(0,checkmodcode.status,'Modification on kernel source failed')
        #Step 4.5
        checkmodconfg = runCmd("grep %s %s" % (modconfopt, codeconfigfile))
        self.assertEqual(0,checkmodconfg.status,'Modification to configuration file failed')
