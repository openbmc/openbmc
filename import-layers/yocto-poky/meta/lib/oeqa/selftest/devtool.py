import unittest
import os
import logging
import re
import shutil
import tempfile
import glob
import fnmatch

import oeqa.utils.ftools as ftools
from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, create_temp_layer, runqemu, get_test_layer
from oeqa.utils.decorators import testcase

class DevtoolBase(oeSelfTest):

    def _test_recipe_contents(self, recipefile, checkvars, checkinherits):
        with open(recipefile, 'r') as f:
            invar = None
            invalue = None
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
                    inherits = line.split()[1:]

                if var and var in checkvars:
                    needvalue = checkvars.pop(var)
                    if needvalue is None:
                        self.fail('Variable %s should not appear in recipe')
                    if isinstance(needvalue, set):
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

    def setUp(self):
        """Test case setup function"""
        super(DevtoolTests, self).setUp()
        self.workspacedir = os.path.join(self.builddir, 'workspace')
        self.assertTrue(not os.path.exists(self.workspacedir),
                        'This test cannot be run with a workspace directory '
                        'under the build directory')

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

    @testcase(1158)
    def test_create_workspace(self):
        # Check preconditions
        result = runCmd('bitbake-layers show-layers')
        self.assertTrue('/workspace' not in result.output, 'This test cannot be run with a workspace layer in bblayers.conf')
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

    @testcase(1159)
    def test_devtool_add(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        url = 'http://www.ivarch.com/programs/sources/pv-1.5.3.tar.bz2'
        result = runCmd('wget %s' % url, cwd=tempdir)
        result = runCmd('tar xfv pv-1.5.3.tar.bz2', cwd=tempdir)
        srcdir = os.path.join(tempdir, 'pv-1.5.3')
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure')), 'Unable to find configure script in source directory')
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate pv')
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add pv %s' % srcdir)
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn('pv', result.output)
        self.assertIn(srcdir, result.output)
        # Clean up anything in the workdir/sysroot/sstate cache (have to do this *after* devtool add since the recipe only exists then)
        bitbake('pv -c cleansstate')
        # Test devtool build
        result = runCmd('devtool build pv')
        installdir = get_bb_var('D', 'pv')
        self.assertTrue(installdir, 'Could not query installdir variable')
        bindir = get_bb_var('bindir', 'pv')
        self.assertTrue(bindir, 'Could not query bindir variable')
        if bindir[0] == '/':
            bindir = bindir[1:]
        self.assertTrue(os.path.isfile(os.path.join(installdir, bindir, 'pv')), 'pv binary not found in D')

    @testcase(1423)
    def test_devtool_add_git_local(self):
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
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created')
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

    @testcase(1162)
    def test_devtool_add_library(self):
        # We don't have the ability to pick up this dependency automatically yet...
        bitbake('libusb1')
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
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created')
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
        # Test devtool build
        result = runCmd('devtool build libftdi')
        staging_libdir = get_bb_var('STAGING_LIBDIR', 'libftdi')
        self.assertTrue(staging_libdir, 'Could not query STAGING_LIBDIR variable')
        self.assertTrue(os.path.isfile(os.path.join(staging_libdir, 'libftdi1.so.2.1.0')), "libftdi binary not found in STAGING_LIBDIR. Output of devtool build libftdi %s" % result.output)
        # Test devtool reset
        stampprefix = get_bb_var('STAMP', 'libftdi')
        result = runCmd('devtool reset libftdi')
        result = runCmd('devtool status')
        self.assertNotIn('libftdi', result.output)
        self.assertTrue(stampprefix, 'Unable to get STAMP value for recipe libftdi')
        matches = glob.glob(stampprefix + '*')
        self.assertFalse(matches, 'Stamp files exist for recipe libftdi that should have been cleaned')
        self.assertFalse(os.path.isfile(os.path.join(staging_libdir, 'libftdi1.so.2.1.0')), 'libftdi binary still found in STAGING_LIBDIR after cleaning')

    @testcase(1160)
    def test_devtool_add_fetch(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        testver = '0.23'
        url = 'https://pypi.python.org/packages/source/M/MarkupSafe/MarkupSafe-%s.tar.gz' % testver
        testrecipe = 'python-markupsafe'
        srcdir = os.path.join(tempdir, testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s -f %s' % (testrecipe, srcdir, url))
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created. %s' % result.output)
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

    @testcase(1161)
    def test_devtool_add_fetch_git(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        url = 'git://git.yoctoproject.org/libmatchbox'
        checkrev = '462f0652055d89c648ddd54fd7b03f175c2c6973'
        testrecipe = 'libmatchbox2'
        srcdir = os.path.join(tempdir, testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s -a -f %s' % (testrecipe, srcdir, url))
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created: %s' % result.output)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure.ac')), 'Unable to find configure.ac in source directory')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('_git.bb', recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '1.12+git${SRCPV}'
        checkvars['SRC_URI'] = url
        checkvars['SRCREV'] = '${AUTOREV}'
        self._test_recipe_contents(recipefile, checkvars, [])
        # Try with revision and version specified
        result = runCmd('devtool reset -n %s' % testrecipe)
        shutil.rmtree(srcdir)
        url_rev = '%s;rev=%s' % (url, checkrev)
        result = runCmd('devtool add %s %s -f "%s" -V 1.5' % (testrecipe, srcdir, url_rev))
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure.ac')), 'Unable to find configure.ac in source directory')
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

    @testcase(1391)
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
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created. %s' % result.output)
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

    @testcase(1164)
    def test_devtool_modify(self):
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('mdadm -c cleansstate')
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.add_command_to_tearDown('bitbake -c clean mdadm')
        result = runCmd('devtool modify mdadm -x %s' % tempdir)
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'Makefile')), 'Extracted source could not be found')
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', 'mdadm_*.bbappend'))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn('mdadm', result.output)
        self.assertIn(tempdir, result.output)
        # Check git repo
        self._check_src_repo(tempdir)
        # Try building
        bitbake('mdadm')
        # Try making (minor) modifications to the source
        result = runCmd("sed -i 's!^\.TH.*!.TH MDADM 8 \"\" v9.999-custom!' %s" % os.path.join(tempdir, 'mdadm.8.in'))
        bitbake('mdadm -c package')
        pkgd = get_bb_var('PKGD', 'mdadm')
        self.assertTrue(pkgd, 'Could not query PKGD variable')
        mandir = get_bb_var('mandir', 'mdadm')
        self.assertTrue(mandir, 'Could not query mandir variable')
        if mandir[0] == '/':
            mandir = mandir[1:]
        with open(os.path.join(pkgd, mandir, 'man8', 'mdadm.8'), 'r') as f:
            for line in f:
                if line.startswith('.TH'):
                    self.assertEqual(line.rstrip(), '.TH MDADM 8 "" v9.999-custom', 'man file not modified. man searched file path: %s' % os.path.join(pkgd, mandir, 'man8', 'mdadm.8'))
        # Test devtool reset
        stampprefix = get_bb_var('STAMP', 'mdadm')
        result = runCmd('devtool reset mdadm')
        result = runCmd('devtool status')
        self.assertNotIn('mdadm', result.output)
        self.assertTrue(stampprefix, 'Unable to get STAMP value for recipe mdadm')
        matches = glob.glob(stampprefix + '*')
        self.assertFalse(matches, 'Stamp files exist for recipe mdadm that should have been cleaned')

    @testcase(1166)
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

    @testcase(1365)
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


    @testcase(1165)
    def test_devtool_modify_git(self):
        # Check preconditions
        testrecipe = 'mkelfimage'
        src_uri = get_bb_var('SRC_URI', testrecipe)
        self.assertIn('git://', src_uri, 'This test expects the %s recipe to be a git recipe' % testrecipe)
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'Makefile')), 'Extracted source could not be found')
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created. devtool output: %s' % result.output)
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', 'mkelfimage_*.bbappend'))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Check git repo
        self._check_src_repo(tempdir)
        # Try building
        bitbake(testrecipe)

    @testcase(1167)
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
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'configure.ac')), 'Extracted source could not be found')
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', '%s_*.bbappend' % testrecipe))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Try building
        bitbake(testrecipe)

    @testcase(1378)
    def test_devtool_modify_virtual(self):
        # Try modifying a virtual recipe
        virtrecipe = 'virtual/libx11'
        realrecipe = 'libx11'
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (virtrecipe, tempdir))
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'Makefile.am')), 'Extracted source could not be found')
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'conf', 'layer.conf')), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', '%s_*.bbappend' % realrecipe))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)
        # Test devtool status
        result = runCmd('devtool status')
        self.assertNotIn(virtrecipe, result.output)
        self.assertIn(realrecipe, result.output)
        # Check git repo
        self._check_src_repo(tempdir)
        # This is probably sufficient


    @testcase(1169)
    def test_devtool_update_recipe(self):
        # Check preconditions
        testrecipe = 'minicom'
        recipefile = get_bb_var('FILE', testrecipe)
        src_uri = get_bb_var('SRC_URI', testrecipe)
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

    @testcase(1172)
    def test_devtool_update_recipe_git(self):
        # Check preconditions
        testrecipe = 'mtd-utils'
        recipefile = get_bb_var('FILE', testrecipe)
        src_uri = get_bb_var('SRC_URI', testrecipe)
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
        result = runCmd('echo "# Additional line" >> Makefile', cwd=tempdir)
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

    @testcase(1170)
    def test_devtool_update_recipe_append(self):
        # Check preconditions
        testrecipe = 'mdadm'
        recipefile = get_bb_var('FILE', testrecipe)
        src_uri = get_bb_var('SRC_URI', testrecipe)
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
        self.assertTrue(os.path.exists(patchfile), 'Patch file not created')

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
        self.assertFalse(os.path.exists(patchfile), 'Patch file not deleted')
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
        self.assertTrue(os.path.exists(patchfile), 'Patch file not created (with disabled layer)')
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, f.readlines())
        # Deleting isn't expected to work under these circumstances

    @testcase(1171)
    def test_devtool_update_recipe_append_git(self):
        # Check preconditions
        testrecipe = 'mtd-utils'
        recipefile = get_bb_var('FILE', testrecipe)
        src_uri = get_bb_var('SRC_URI', testrecipe)
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
        result = runCmd('echo "# Additional line" >> Makefile', cwd=tempsrcdir)
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
        self.assertFalse(os.path.exists(os.path.join(appenddir, testrecipe)), 'Patch directory should not be created')

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
        self.assertFalse(os.path.exists(os.path.join(appenddir, testrecipe)), 'Patch directory should not be created')
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
        self.assertFalse(os.path.exists(os.path.join(appenddir, testrecipe)), 'Patch directory should not be created')
        result = runCmd('git rev-parse HEAD', cwd=tempsrcdir)
        expectedlines = set(['SRCREV = "%s"\n' % result.output,
                             '\n',
                             'SRC_URI = "%s"\n' % git_uri,
                             '\n'])
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines, set(f.readlines()))
        # Deleting isn't expected to work under these circumstances

    @testcase(1370)
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

    @testcase(1371)
    def test_devtool_update_recipe_local_files_2(self):
        """Check local source files support when oe-local-files is in Git"""
        testrecipe = 'lzo'
        recipefile = get_bb_var('FILE', testrecipe)
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
        runCmd('echo "# Foobar" >> oe-local-files/acinclude.m4', cwd=tempdir)
        runCmd('git commit -am "Edit existing file"', cwd=tempdir)
        runCmd('git rm oe-local-files/run-ptest', cwd=tempdir)
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
        runCmd('git checkout HEAD^ -- oe-local-files/acinclude.m4', cwd=tempdir)
        runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           (' M', '.*/acinclude.m4$'),
                           (' D', '.*/run-ptest$'),
                           ('??', '.*/new-local$'),
                           ('??', '.*/0001-Add-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    @testcase(1163)
    def test_devtool_extract(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        # Try devtool extract
        self.track_for_cleanup(tempdir)
        self.append_config('PREFERRED_PROVIDER_virtual/make = "remake"')
        result = runCmd('devtool extract remake %s' % tempdir)
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'Makefile.am')), 'Extracted source could not be found')
        # devtool extract shouldn't create the workspace
        self.assertFalse(os.path.exists(self.workspacedir))
        self._check_src_repo(tempdir)

    @testcase(1379)
    def test_devtool_extract_virtual(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        # Try devtool extract
        self.track_for_cleanup(tempdir)
        result = runCmd('devtool extract virtual/libx11 %s' % tempdir)
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'Makefile.am')), 'Extracted source could not be found')
        # devtool extract shouldn't create the workspace
        self.assertFalse(os.path.exists(self.workspacedir))
        self._check_src_repo(tempdir)

    @testcase(1168)
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

    @testcase(1272)
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
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
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
            installdir = get_bb_var('D', testrecipe)
            fakerootenv = get_bb_var('FAKEROOTENV', testrecipe)
            fakerootcmd = get_bb_var('FAKEROOTCMD', testrecipe)
            result = runCmd('%s %s find . -type f -exec ls -l {} \;' % (fakerootenv, fakerootcmd), cwd=installdir)
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

    @testcase(1366)
    def test_devtool_build_image(self):
        """Test devtool build-image plugin"""
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        image = 'core-image-minimal'
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.add_command_to_tearDown('bitbake -c clean %s' % image)
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

    @testcase(1367)
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
        self.assertTrue(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe, '%s-%s' % (recipe, version))), 'Recipe folder should exist')
        # Check new recipe file is present
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipe, '%s_%s.bb' % (recipe, version))
        self.assertTrue(os.path.exists(newrecipefile), 'Recipe file should exist after upgrade')
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
        self.assertFalse(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe)), 'Recipe directory should not exist after resetting')

    @testcase(1433)
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
        self.assertTrue(os.path.exists(newrecipefile), 'Recipe file should exist after upgrade')
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
        self.assertFalse(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe)), 'Recipe directory should not exist after resetting')

    @testcase(1352)
    def test_devtool_layer_plugins(self):
        """Test that devtool can use plugins from other layers.

        This test executes the selftest-reverse command from meta-selftest."""

        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

        s = "Microsoft Made No Profit From Anyone's Zunes Yo"
        result = runCmd("devtool --quiet selftest-reverse \"%s\"" % s)
        self.assertEqual(result.output, s[::-1])

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
        self.assertTrue(os.path.exists(os.path.join(olddir, patchfn)), 'Original patch file does not exist')
        return recipe, oldrecipefile, recipedir, olddir, newversion, patchfn

    def test_devtool_finish_upgrade_origlayer(self):
        recipe, oldrecipefile, recipedir, olddir, newversion, patchfn = self._setup_test_devtool_finish_upgrade()
        # Ensure the recipe is where we think it should be (so that cleanup doesn't trash things)
        self.assertIn('/meta-selftest/', recipedir)
        # Try finish to the original layer
        self.add_command_to_tearDown('rm -rf %s ; cd %s ; git checkout %s' % (recipedir, os.path.dirname(recipedir), recipedir))
        result = runCmd('devtool finish %s meta-selftest' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertFalse(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe)), 'Recipe directory should not exist after finish')
        self.assertFalse(os.path.exists(oldrecipefile), 'Old recipe file should have been deleted but wasn\'t')
        self.assertFalse(os.path.exists(os.path.join(olddir, patchfn)), 'Old patch file should have been deleted but wasn\'t')
        newrecipefile = os.path.join(recipedir, '%s_%s.bb' % (recipe, newversion))
        newdir = os.path.join(recipedir, recipe + '-' + newversion)
        self.assertTrue(os.path.exists(newrecipefile), 'New recipe file should have been copied into existing layer but wasn\'t')
        self.assertTrue(os.path.exists(os.path.join(newdir, patchfn)), 'Patch file should have been copied into new directory but wasn\'t')
        self.assertTrue(os.path.exists(os.path.join(newdir, '0002-Add-a-comment-to-the-code.patch')), 'New patch file should have been created but wasn\'t')

    def test_devtool_finish_upgrade_otherlayer(self):
        recipe, oldrecipefile, recipedir, olddir, newversion, patchfn = self._setup_test_devtool_finish_upgrade()
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
        self.assertFalse(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe)), 'Recipe directory should not exist after finish')
        self.assertTrue(os.path.exists(oldrecipefile), 'Old recipe file should not have been deleted')
        self.assertTrue(os.path.exists(os.path.join(olddir, patchfn)), 'Old patch file should not have been deleted')
        newdir = os.path.join(newrecipedir, recipe + '-' + newversion)
        self.assertTrue(os.path.exists(newrecipefile), 'New recipe file should have been copied into existing layer but wasn\'t')
        self.assertTrue(os.path.exists(os.path.join(newdir, patchfn)), 'Patch file should have been copied into new directory but wasn\'t')
        self.assertTrue(os.path.exists(os.path.join(newdir, '0002-Add-a-comment-to-the-code.patch')), 'New patch file should have been created but wasn\'t')

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
        self.assertTrue(os.path.exists(os.path.join(tempdir, 'Makefile')), 'Extracted source could not be found')
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
        self.assertFalse(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe)), 'Recipe directory should not exist after finish')
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
        self.assertFalse(os.path.exists(os.path.join(self.workspacedir, 'recipes', recipe)), 'Recipe directory should not exist after finish')
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains the following unexpected changes after finish:\n%s' % (recipe, result.output.strip()))
        recipefn = os.path.splitext(os.path.basename(oldrecipefile))[0]
        recipefn = recipefn.split('_')[0] + '_%'
        appendfile = os.path.join(appenddir, recipefn + '.bbappend')
        self.assertTrue(os.path.exists(appendfile), 'bbappend %s should have been created but wasn\'t' % appendfile)
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
