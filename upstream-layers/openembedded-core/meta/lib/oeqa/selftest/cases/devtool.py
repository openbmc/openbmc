#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import errno
import os
import re
import shutil
import subprocess
import tempfile
import threading
import time
import glob
import fnmatch
import unittest
import json
import logging
import shlex

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, create_temp_layer
from oeqa.utils.commands import get_bb_vars, runqemu, runqemu_check_taps, get_test_layer
from oeqa.core.decorator import OETestTag
from oeqa.core.decorator.data import skipIfNotFeature
from bb.utils import mkdirhier, edit_bblayers_conf

oldmetapath = None

def setUpModule():
    global templayerdir
    templayerdir = tempfile.mkdtemp(prefix='devtoolqa')
    corecopydir = os.path.join(templayerdir, 'core-copy')
    bblayers_conf = os.path.join(os.environ['BUILDDIR'], 'conf', 'bblayers.conf')
    edited_layers = []
    # make sure user doesn't have a local workspace
    result = runCmd('bitbake-layers show-layers')
    assert "workspacelayer" not in result.output, "Devtool test suite cannot be run with a local workspace directory"

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

            # when downloading poky from tar.gz some tests will be skipped (BUG 12389)
            try:
                runCmd('git rev-parse --is-inside-work-tree', cwd=canonical_layerpath)
            except:
                raise unittest.SkipTest("devtool tests require folder to be a git repo")

            result = runCmd('git rev-parse --show-toplevel', cwd=canonical_layerpath)
            oldreporoot = result.output.rstrip()
            newmetapath = os.path.join(corecopydir, os.path.relpath(oldmetapath, oldreporoot))
            runCmd('git clone file://%s %s' % (oldreporoot, corecopydir), cwd=templayerdir)
            # Now we need to copy any modified files
            # You might ask "why not just copy the entire tree instead of
            # cloning and doing this?" - well, the problem with that is
            # TMPDIR or an equally large subdirectory might exist
            # under COREBASE and we don't want to copy that, so we have
            # to be selective.
            result = runCmd('git status --porcelain', cwd=oldreporoot)

            # Also copy modifications to the 'scripts/' directory
            canonical_layerpath_scripts = os.path.normpath(canonical_layerpath + "../scripts")

            for line in result.output.splitlines():
                if line.startswith(' M ') or line.startswith('?? '):
                    relpth = line.split()[1]
                    pth = os.path.join(oldreporoot, relpth)
                    if pth.startswith(canonical_layerpath) or pth.startswith(canonical_layerpath_scripts):
                        if relpth.endswith('/'):
                            destdir = os.path.join(corecopydir, relpth)
                            # avoid race condition by not copying .pyc files YPBZ#13421,13803
                            shutil.copytree(pth, destdir, ignore=shutil.ignore_patterns('*.pyc', '__pycache__'))
                        else:
                            destdir = os.path.join(corecopydir, os.path.dirname(relpth))
                            mkdirhier(destdir)
                            shutil.copy2(pth, destdir)
            return newmetapath
        else:
            return layerpath
    edit_bblayers_conf(bblayers_conf, None, None, bblayers_edit_cb)

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
        edit_bblayers_conf(bblayers_conf, None, None, bblayers_edit_cb)
    shutil.rmtree(templayerdir)

class DevtoolTestCase(OESelftestTestCase):

    def setUp(self):
        """Test case setup function"""
        super(DevtoolTestCase, self).setUp()
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
                    splitline = re.split(r"[?+:]*=[+]?", line, 1)
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

    def _check_diff(self, diffoutput, addlines, removelines):
        """Check output from 'git diff' matches expectation"""
        remaining_addlines = addlines[:]
        remaining_removelines = removelines[:]
        for line in diffoutput.splitlines():
            if line.startswith('+++') or line.startswith('---'):
                continue
            elif line.startswith('+'):
                matched = False
                for item in addlines:
                    if re.match(item, line[1:].strip()):
                        matched = True
                        remaining_addlines.remove(item)
                        break
                self.assertTrue(matched, 'Unexpected diff add line: %s' % line)
            elif line.startswith('-'):
                matched = False
                for item in removelines:
                    if re.match(item, line[1:].strip()):
                        matched = True
                        remaining_removelines.remove(item)
                        break
                self.assertTrue(matched, 'Unexpected diff remove line: %s' % line)
        if remaining_addlines:
            self.fail('Expected added lines not found: %s' % remaining_addlines)
        if remaining_removelines:
            self.fail('Expected removed lines not found: %s' % remaining_removelines)

    def _check_runqemu_prerequisites(self):
        """Check runqemu is available

        Whilst some tests would seemingly be better placed as a runtime test,
        unfortunately the runtime tests run under bitbake and you can't run
        devtool within bitbake (since devtool needs to run bitbake itself).
        Additionally we are testing build-time functionality as well, so
        really this has to be done as an oe-selftest test.
        """
        machine = get_bb_var('MACHINE')
        if not machine.startswith('qemu'):
            self.skipTest('This test only works with qemu machines')
        if not runqemu_check_taps():
            self.skipTest('You must set up tap devices with scripts/runqemu-gen-tapdevs before running this test')

    def _test_devtool_add_git_url(self, git_url, version, pn, resulting_src_uri, srcrev=None):
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        command = 'devtool add --version %s %s %s' % (version, pn, git_url)
        if srcrev :
            command += ' --srcrev %s' %srcrev
        result = runCmd(command)
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')
        # Check the recipe name is correct
        recipefile = get_bb_var('FILE', pn)
        self.assertIn('%s_git.bb' % pn, recipefile, 'Recipe file incorrectly named')
        self.assertIn(recipefile, result.output)
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(pn, result.output)
        self.assertIn(recipefile, result.output)
        checkvars = {}
        checkvars['SRC_URI'] = resulting_src_uri
        self._test_recipe_contents(recipefile, checkvars, [])

class DevtoolBase(DevtoolTestCase):

    @classmethod
    def setUpClass(cls):
        super(DevtoolBase, cls).setUpClass()
        bb_vars = get_bb_vars(['TOPDIR', 'SSTATE_DIR'])
        cls.original_sstate = bb_vars['SSTATE_DIR']
        cls.devtool_sstate = os.path.join(bb_vars['TOPDIR'], 'sstate_devtool')
        cls.sstate_conf  = 'SSTATE_DIR = "%s"\n' % cls.devtool_sstate
        cls.sstate_conf += ('SSTATE_MIRRORS += "file://.* file:///%s/PATH"\n'
                            % cls.original_sstate)
        cls.sstate_conf += ('BB_HASHSERVE_UPSTREAM = "hashserv.yoctoproject.org:8686"\n')

    @classmethod
    def tearDownClass(cls):
        cls.logger.debug('Deleting devtool sstate cache on %s' % cls.devtool_sstate)
        runCmd('rm -rf %s' % cls.devtool_sstate)
        super(DevtoolBase, cls).tearDownClass()

    def setUp(self):
        """Test case setup function"""
        super(DevtoolBase, self).setUp()
        self.append_config(self.sstate_conf)


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

    def test_devtool_add_binary(self):
        # Create a binary package containing a known test file
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        pn = 'tst-bin'
        pv = '1.0'
        test_file_dir     = "var/lib/%s/" % pn
        test_file_name    = "test_file"
        test_file_content = "TEST CONTENT"
        test_file_package_root = os.path.join(tempdir, pn)
        test_file_dir_full = os.path.join(test_file_package_root, test_file_dir)
        mkdirhier(test_file_dir_full)
        with open(os.path.join(test_file_dir_full, test_file_name), "w") as f:
            f.write(test_file_content)
        bin_package_path = os.path.join(tempdir, "%s.tar.gz" % pn)
        runCmd("tar czf %s -C %s ." % (bin_package_path, test_file_package_root))

        # Test devtool add -b on the binary package
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % pn)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add  -b %s %s' % (pn, bin_package_path))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created')

        # Build the resulting recipe
        result = runCmd('devtool build %s' % pn)
        installdir = get_bb_var('D', pn)
        self.assertTrue(installdir, 'Could not query installdir variable')

        # Check that a known file from the binary package has indeed been installed
        self.assertTrue(os.path.isfile(os.path.join(installdir, test_file_dir, test_file_name)), '%s not found in D' % test_file_name)

    def test_devtool_add_git_local(self):
        # We need dbus built so that DEPENDS recognition works
        bitbake('dbus')
        # Fetch source from a remote URL, but do it outside of devtool
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        pn = 'dbus-wait'
        srcrev = '6cc6077a36fe2648a5f993fe7c16c9632f946517'
        # We choose an https:// git URL here to check rewriting the URL works
        url = 'https://git.yoctoproject.org/dbus-wait'
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
        checkvars['LICENSE'] = 'GPL-2.0-only'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'
        checkvars['S'] = None
        checkvars['PV'] = '0.1+git'
        checkvars['SRC_URI'] = 'git://git.yoctoproject.org/dbus-wait;protocol=https;branch=master'
        checkvars['SRCREV'] = srcrev
        checkvars['DEPENDS'] = set(['dbus'])
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_git_style1(self):
        version = 'v3.1.0'
        pn = 'mbedtls'
        # this will trigger reformat_git_uri with branch parameter in url
        git_url = "'git://git@github.com/Mbed-TLS/mbedtls.git;branch=archive/mbedtls-2.28;protocol=https'"
        resulting_src_uri = "git://git@github.com/Mbed-TLS/mbedtls.git;branch=archive/mbedtls-2.28;protocol=https"
        self._test_devtool_add_git_url(git_url, version, pn, resulting_src_uri)

    def test_devtool_add_git_style2(self):
        version = 'v3.1.0'
        srcrev = 'v3.1.0'
        pn = 'mbedtls'
        # this will trigger reformat_git_uri with branch parameter in url
        git_url = "'git://git@github.com/Mbed-TLS/mbedtls.git;protocol=https'"
        resulting_src_uri = "git://git@github.com/Mbed-TLS/mbedtls.git;protocol=https;branch=master"
        self._test_devtool_add_git_url(git_url, version, pn, resulting_src_uri, srcrev)

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
        # There is no upstream release that supports building with CMake 4+ yet, so we explicitly
        # set the policy minimum version via EXTRA_OECMAKE. That's easier than applying backported
        # patches.
        result = runCmd(
            "recipetool setvar %s EXTRA_OECMAKE -- '-DCMAKE_POLICY_VERSION_MINIMUM=3.5 -DPYTHON_BINDINGS=OFF -DLIBFTDI_CMAKE_CONFIG_DIR=${datadir}/cmake/Modules'"
            % recipefile
        )
        with open(recipefile, 'a') as f:
            f.write('\nFILES:${PN}-dev += "${datadir}/cmake/Modules"\n')
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
        result = runCmd('devtool add --no-pypi %s %s -f %s' % (testrecipe, srcdir, url))
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
        checkvars['S'] = '${UNPACKDIR}/MarkupSafe-${PV}'
        checkvars['SRC_URI'] = url.replace(testver, '${PV}')
        self._test_recipe_contents(recipefile, checkvars, [])
        # Try with version specified
        result = runCmd('devtool reset -n %s' % testrecipe)
        shutil.rmtree(srcdir)
        fakever = '1.9'
        result = runCmd('devtool add --no-pypi %s %s -f %s -V %s' % (testrecipe, srcdir, url, fakever))
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'setup.py')), 'Unable to find setup.py in source directory')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(srcdir, result.output)
        # Check recipe
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('%s_%s.bb' % (testrecipe, fakever), recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = '${UNPACKDIR}/MarkupSafe-%s' % testver
        checkvars['SRC_URI'] = url
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_fetch_git(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        url = 'gitsm://git.yoctoproject.org/mraa;protocol=https'
        url_branch = '%s;branch=master' % url
        checkrev = 'ae127b19a50aa54255e4330ccfdd9a5d058e581d'
        testrecipe = 'mraa'
        srcdir = os.path.join(tempdir, testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s -a "%s"' % (testrecipe, srcdir, url))
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
        checkvars['S'] = None
        checkvars['PV'] = '1.0+git'
        checkvars['SRC_URI'] = url_branch
        checkvars['SRCREV'] = '${AUTOREV}'
        self._test_recipe_contents(recipefile, checkvars, [])
        # Finish into a temporary layer instead of resetting; srcrev mode
        # used to fail with "fatal: bad revision 'AUTOINC'" because the
        # unresolved AUTOREV placeholder was used as the git revision to
        # export patches from [YOCTO #16354]. srcrev mode has to be forced
        # because update-mode guessing never selects it for gitsm:// URLs.
        templayerdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(templayerdir)
        self._create_temp_layer(templayerdir, False, 'selftestautorev')
        result = runCmd('devtool finish --mode srcrev %s %s' % (testrecipe, templayerdir))
        result = runCmd('devtool status')
        self.assertNotIn(testrecipe, result.output, 'Recipe should have been removed from workspace by devtool finish')
        newrecipefiles = glob.glob(os.path.join(templayerdir, 'recipes-*', testrecipe, '%s_git.bb' % testrecipe))
        self.assertEqual(len(newrecipefiles), 1, 'Recipe not moved into destination layer by devtool finish')
        # The finished recipe should still use a floating revision and no
        # patch files should have been created
        checkvars = {}
        checkvars['SRCREV'] = '${AUTOREV}'
        self._test_recipe_contents(newrecipefiles[0], checkvars, [])
        patchfiles = glob.glob(os.path.join(templayerdir, 'recipes-*', testrecipe, '*', '*.patch'))
        self.assertEqual(patchfiles, [], 'Unexpected patch files generated by devtool finish')
        # Try with revision and version specified
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
        checkvars['S'] = None
        checkvars['PV'] = '1.5+git'
        checkvars['SRC_URI'] = url_branch
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
        # Check recipedevtool add
        recipefile = get_bb_var('FILE', testrecipe)
        self.assertIn('%s_%s.bb' % (testrecipe, testver), recipefile, 'Recipe file incorrectly named')
        checkvars = {}
        checkvars['S'] = None
        checkvars['SRC_URI'] = url.replace(testver, '${PV}')
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_npm(self):
        collections = get_bb_var('BBFILE_COLLECTIONS').split()
        if "openembedded-layer" not in collections:
            self.skipTest("Test needs meta-oe for nodejs")

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

    def test_devtool_add_python_egg_requires(self):
        # Fetch source
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        testver = '0.14.0'
        url = 'https://files.pythonhosted.org/packages/e9/9e/25d59f5043cf763833b2581c8027fa92342c4cf8ee523b498ecdf460c16d/uvicorn-%s.tar.gz' % testver
        testrecipe = 'python3-uvicorn'
        srcdir = os.path.join(tempdir, testrecipe)
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s %s -f %s' % (testrecipe, srcdir, url))

    def test_devtool_add_build_finish(self):
        url = 'https://ftp.gnu.org/gnu/hello/hello-2.12.3.tar.gz'
        pn = 'hello'
        recipe = 'hello_2.12.3.bb'
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % pn)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s' % (url, ))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'),
                          'Workspace directory not created')
        # Test devtool build
        result = runCmd('devtool build %s' % pn)
        bb_vars = get_bb_vars(['D', 'bindir'], pn)
        installdir = bb_vars['D']
        self.assertTrue(installdir, 'Could not query installdir variable')
        # devtool finish
        result = runCmd('devtool finish %s meta-selftest' % pn,
                        ignore_status=True)
        self.assertNotEqual(result.status, 0,
                            'devtool finish should have failed on a dirty directory. devtool output: %s' %
                            (result.output))
        # clean directory
        sourcedir = os.path.join(self.workspacedir, 'sources', 'hello')
        runCmd('git reset --hard; git clean -dxf', cwd=sourcedir)
        # devtool finish (again)
        self.assertExists(os.path.join(self.workspacedir, 'recipes', pn),
                          'Recipe directory should exist before finish')
        result = runCmd('devtool finish %s meta-selftest' % pn)
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', pn),
                             'Recipe directory should not exist after finish')
        # Check recipe got created as expected
        self.assertExists(os.path.join(get_test_layer(), f'recipes-{pn}', pn, recipe),
                          'Recipe should exist in the layer after finish')

    def test_devtool_add_build_finish_force(self):
        url = 'https://ftp.gnu.org/gnu/hello/hello-2.12.3.tar.gz'
        pn = 'hello'
        recipe = 'hello_2.12.3.bb'
        # Test devtool add
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % pn)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool add %s' % (url, ))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'),
                          'Workspace directory not created')
        # Test devtool build
        result = runCmd('devtool build %s' % pn)
        bb_vars = get_bb_vars(['D', 'bindir'], pn)
        installdir = bb_vars['D']
        self.assertTrue(installdir, 'Could not query installdir variable')
        # devtool finish
        result = runCmd('devtool finish %s meta-selftest --force' % pn)
        # Check recipe got created as expected
        self.assertExists(os.path.join(get_test_layer(), f'recipes-{pn}', pn, recipe),
                          'Recipe should exist in the layer after finish')


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

    def test_devtool_modify_go(self):
        import oe.path
        from tempfile import TemporaryDirectory
        with TemporaryDirectory(prefix='devtoolqa') as tempdir:
            self.track_for_cleanup(self.workspacedir)
            self.add_command_to_tearDown('bitbake -c clean go-helloworld')
            self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
            result = runCmd('devtool modify go-helloworld -x %s' % tempdir)
            self.assertExists(
                oe.path.join(tempdir, 'src', 'golang.org', 'x', 'example', 'go.mod'),
                             'Extracted source could not be found'
            )
            self.assertExists(
                oe.path.join(self.workspacedir, 'conf', 'layer.conf'),
                'Workspace directory not created'
            )
            matches = glob.glob(oe.path.join(self.workspacedir, 'appends', 'go-helloworld_*.bbappend'))
            self.assertTrue(matches, 'bbappend not created %s' % result.output)

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

        testrecipes = 'perf kernel-devsrc package-index core-image-minimal meta-toolchain packagegroup-core-sdk'.split()
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
        testrecipes = 'cdrtools-native mtools-native apt-native desktop-file-utils-native'.split()
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

    def test_devtool_modify_localfiles_only(self):
        # Check preconditions
        testrecipe = 'base-files'
        src_uri = (get_bb_var('SRC_URI', testrecipe) or '').split()
        foundlocalonly = False
        correct_symlink = False
        for item in src_uri:
            if item.startswith('file://'):
                if '.patch' not in item:
                    foundlocalonly = True
            else:
                foundlocalonly = False
                break
        self.assertTrue(foundlocalonly, 'This test expects the %s recipe to fetch local files only and it seems that it no longer does' % testrecipe)
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        srcfile = os.path.join(tempdir, 'share/dot.bashrc')
        self.assertExists(srcfile, 'Extracted source could not be found')
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', '%s_*.bbappend' % testrecipe))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Try building
        bitbake(testrecipe)

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

    def test_devtool_modify_git_no_extract(self):
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
        result = runCmd('git clone https://git.yoctoproject.org/psplash %s && devtool modify -n %s %s' % (tempdir, testrecipe, tempdir))
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created. devtool output: %s' % result.output)
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', 'psplash_*.bbappend'))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)

    def test_devtool_modify_git_crates_subpath(self):
        # This tests two things in devtool context:
        #   - that we support local git dependencies for cargo based recipe
        #   - that we support patches in SRC_URI when git url contains subpath parameter

        # Check preconditions:
        #    recipe inherits cargo
        #    git:// uri with a subpath as the main package
        #    some crate:// in SRC_URI
        #    others git:// in SRC_URI
        #    cointains a patch
        testrecipe = 'hello-rs'
        bb_vars = get_bb_vars(['SRC_URI', 'FILE', 'UNPACKDIR', 'CARGO_HOME'], testrecipe)
        recipefile = bb_vars['FILE']
        unpackdir = bb_vars['UNPACKDIR']
        cargo_home = bb_vars['CARGO_HOME']
        src_uri = bb_vars['SRC_URI'].split()
        self.assertTrue(src_uri[0].startswith('git://'),
                        'This test expects the %s recipe to have a git repo has its main uri' % testrecipe)
        self.assertIn(';subpath=', src_uri[0],
                      'This test expects the %s recipe to have a git uri with subpath' % testrecipe)
        self.assertTrue(any([uri.startswith('crate://') for uri in src_uri]),
                        'This test expects the %s recipe to have some crates in its src uris' % testrecipe)
        self.assertGreaterEqual(sum(map(lambda x:x.startswith('git://'), src_uri)), 2,
                           'This test expects the %s recipe to have several git:// uris' % testrecipe)
        self.assertTrue(any([uri.startswith('file://') and '.patch' in uri for uri in src_uri]),
                        'This test expects the %s recipe to have a patch in its src uris' % testrecipe)

        self._test_recipe_contents(recipefile, {}, ['ptest-cargo'])

        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertExists(os.path.join(tempdir, 'Cargo.toml'), 'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created. devtool output: %s' % result.output)
        matches = glob.glob(os.path.join(self.workspacedir, 'appends', '%s_*.bbappend' % testrecipe))
        self.assertTrue(matches, 'bbappend not created')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Check git repo
        self._check_src_repo(tempdir)
        # Check that the patch is correctly applied.
        # The last commit message in the tree must contain the following note:
        # Notes (devtool):
        #     original patch: <patchname>
        # ..
        patchname = None
        for uri in src_uri:
            if uri.startswith('file://') and '.patch' in uri:
                patchname = uri.replace("file://", "").partition('.patch')[0] + '.patch'
        self.assertIsNotNone(patchname)
        result = runCmd('git -C %s log -1' % tempdir)
        self.assertIn("Notes (devtool):\n    original patch: %s" % patchname, result.output)

        # Configure the recipe to check that the git dependencies are correctly patched in cargo config
        bitbake('-c configure %s' % testrecipe)

        cargo_config_path = os.path.join(cargo_home, 'config.toml')
        with open(cargo_config_path, "r") as f:
            cargo_config_contents = [line.strip('\n') for line in f.readlines()]

        # Get back git dependencies of the recipe (ignoring the main one)
        # and check that they are all correctly patched to be fetched locally
        git_deps = [uri for uri in src_uri if uri.startswith("git://")][1:]
        for git_dep in git_deps:
            raw_url, _, raw_parms = git_dep.partition(";")
            parms = {}
            for parm in raw_parms.split(";"):
                name_parm, _, value_parm = parm.partition('=')
                parms[name_parm]=value_parm
            self.assertIn('protocol', parms, 'git dependencies uri should contain the "protocol" parameter')
            self.assertIn('name', parms, 'git dependencies uri should contain the "name" parameter')
            self.assertIn('destsuffix', parms, 'git dependencies uri should contain the "destsuffix" parameter')
            self.assertIn('type', parms, 'git dependencies uri should contain the "type" parameter')
            self.assertEqual(parms['type'], 'git-dependency', 'git dependencies uri should have "type=git-dependency"')
            raw_url = raw_url.replace("git://", '%s://' % parms['protocol'])
            patch_line = '[patch."%s"]' % raw_url
            path_patched = os.path.join(unpackdir, parms['destsuffix'])
            path_override_line = '%s = { path = "%s" }' % (parms['name'], path_patched)
            # Would have been better to use tomllib to read this file :/
            self.assertIn(patch_line, cargo_config_contents)
            self.assertIn(path_override_line, cargo_config_contents)

        # Try to package the recipe
        bitbake('-c package_qa %s' % testrecipe)

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

    def test_devtool_modify_kernel_cfg_subdirs(self):
        """
        Regression test for YOCTO #15169: devtool modify of a kernel-yocto
        recipe must not fail when an .scc file and the .cfg fragments it
        references (via "kconf"/"patch" lines) live in different
        FILESEXTRAPATHS subdirectories. Pre-fix, devtool computed each .cfg
        file's path as dirname(<.scc file>) + <cfg filename>, which broke
        whenever the .cfg file was not in the same directory as the .scc
        file that references it.
        """
        testrecipe = 'virtual/kernel'
        bb_vars = get_bb_vars(['PN', 'FILE'], testrecipe)
        realrecipe = bb_vars['PN']

        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        self.add_command_to_tearDown('bitbake -c clean %s' % realrecipe)

        # The created layer must outlive the 'bitbake-layers remove-layer'
        # teardown command below: tearDown() runs the queued commands first
        # and only then deletes track_for_cleanup() paths, so mkdtemp() +
        # track_for_cleanup() removes the directory after remove-layer has
        # run. A 'with tempfile.TemporaryDirectory()' here would instead
        # delete it when the test body returns, i.e. before tearDown, leaving
        # bblayers.conf pointing at a missing layer.
        tempparentdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempparentdir)
        # bitbake-layers create-layer refuses to run if the target directory
        # already exists, so use a not-yet-created subdirectory of tempparentdir
        layerdir = os.path.join(tempparentdir, 'meta-selftest-kernelcfgsubdirs')
        runCmd('bitbake-layers create-layer %s' % layerdir)
        runCmd('bitbake-layers add-layer %s' % layerdir)
        self.add_command_to_tearDown('bitbake-layers remove-layer %s || true' % layerdir)

        recipedir = os.path.join(layerdir, 'recipes-kernel', 'linux', realrecipe)
        bspdir = os.path.join(recipedir, 'bsp')
        cfgdir = os.path.join(recipedir, 'cfg')
        os.makedirs(bspdir)
        os.makedirs(cfgdir)

        # .scc lives in bsp/, references .cfg files that live in cfg/ -
        # this is the exact split reported in the bug
        with open(os.path.join(bspdir, 'selftest-cfgsubdirs.scc'), 'w') as f:
            f.write('kconf hardware selftest-spi.cfg\n')
            f.write('kconf hardware selftest-clock.cfg\n')
        with open(os.path.join(cfgdir, 'selftest-spi.cfg'), 'w') as f:
            f.write('# CONFIG_SPI_SELFTEST is not set\n')
        with open(os.path.join(cfgdir, 'selftest-clock.cfg'), 'w') as f:
            f.write('# CONFIG_COMMON_CLK_SELFTEST is not set\n')

        appendfile = os.path.join(layerdir, 'recipes-kernel', 'linux', realrecipe + '_%.bbappend')
        with open(appendfile, 'w') as f:
            f.write('FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:${THISDIR}/${PN}/cfg:${THISDIR}/${PN}/bsp:"\n')
            f.write('SRC_URI:append = " file://selftest-cfgsubdirs.scc"\n')
            f.write('SRC_URI:append = " file://selftest-spi.cfg"\n')
            f.write('SRC_URI:append = " file://selftest-clock.cfg"\n')

        with tempfile.TemporaryDirectory(prefix='devtoolqa') as tempdir:
            # Pre-fix (kirkstone-era), this raised FileNotFoundError while
            # trying to shutil.copy2() selftest-spi.cfg/selftest-clock.cfg
            # from inside bsp/ (the .scc's directory) instead of cfg/ (where
            # they actually live).
            result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
            self.assertNotIn('FileNotFoundError', result.output)
            self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'),
                               'Workspace directory not created')

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

    def test_devtool_modify_overrides(self):
        # Try modifying a recipe with patches in overrides
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify devtool-patch-overrides -x %s' % (tempdir))

        self._check_src_repo(tempdir)
        source = os.path.join(tempdir, "source")
        def check(branch, expected):
            runCmd('git -C %s checkout %s' % (tempdir, branch))
            with open(source, "rt") as f:
                content = f.read()
            self.assertEqual(content, expected)
        if self.td["MACHINE"] == "qemux86":
            check('devtool', 'This is a test for qemux86\n')
        elif self.td["MACHINE"] == "qemuarm":
            check('devtool', 'This is a test for qemuarm\n')
        else:
            check('devtool', 'This is a test for something\n')
        check('devtool-no-overrides', 'This is a test for something\n')
        check('devtool-override-qemuarm', 'This is a test for qemuarm\n')
        check('devtool-override-qemux86', 'This is a test for qemux86\n')

    def test_devtool_modify_multiple_sources(self):
        # This test check that recipes fetching several sources can be used with devtool modify/build
        # Check preconditions
        testrecipe = 'bzip2'
        src_uri = get_bb_var('SRC_URI', testrecipe)
        src1 = 'https://' in src_uri
        src2 = 'git://' in src_uri
        self.assertTrue(src1 and src2, 'This test expects the %s recipe to fetch both a git source and a tarball and it seems that it no longer does' % testrecipe)
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertEqual(result.status, 0, "Could not modify recipe %s. Output: %s" % (testrecipe, result.output))
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Try building
        result = bitbake(testrecipe)
        self.assertEqual(result.status, 0, "Bitbake failed, exit code %s, output %s" % (result.status, result.output))

    def test_devtool_modify_nested_gitsm(self):
        """Checks that a recipe with multiple sources including a git repo with a nested git repo with
        submodules can be used with devtool modify
        """
        testrecipe = 'devtool-test-git-gitsm'
        src_uri = get_bb_var('SRC_URI', testrecipe)
        self.assertIn('git://', src_uri, 'This test expects the %s recipe to fetch a git source' % testrecipe)
        self.assertIn('gitsm://', src_uri, 'This test expects the %s recipe to fetch a gitsm source' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        self.assertEqual(result.status, 0, "Could not modify recipe %s. Output: %s" % (testrecipe, result.output))
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(testrecipe, result.output)
        self.assertIn(tempdir, result.output)
        # Submodules in repo-gitsm should be extracted
        source_repo_gitsm_gitmodules = os.path.join(tempdir, 'nested/repo-gitsm')
        self.assertExists(source_repo_gitsm_gitmodules, 'Nested repo repo-gitsm not found')
        self.assertExists(os.path.join(source_repo_gitsm_gitmodules, 'bitbake'), 'Submodule not found')
        self.assertExists(os.path.join(source_repo_gitsm_gitmodules, 'bitbake-gitsm-test1'), 'Submodule not found')

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
        cleanup_cmd = 'cd %s; rm %s/*.patch; git add %s; git checkout %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile))
        self.add_command_to_tearDown(cleanup_cmd)
        result = runCmd('devtool update-recipe %s' % testrecipe)
        result = runCmd('git add minicom', cwd=os.path.dirname(recipefile))
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('A ', '.*/0001-Change-the-README.patch$'),
                           ('A ', '.*/0002-Add-a-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        result = runCmd(cleanup_cmd)
        self._check_repo_status(os.path.dirname(recipefile), [])
        # Now try the same passing --initial-rev: the recorded initial
        # revision must give the same result as not passing the option, and a
        # later revision must export only the commits after it
        result = runCmd('git rev-parse devtool-base', cwd=tempdir)
        initial_rev = result.output.strip()
        result = runCmd('devtool update-recipe --initial-rev %s %s' % (initial_rev, testrecipe))
        result = runCmd('git add minicom', cwd=os.path.dirname(recipefile))
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('A ', '.*/0001-Change-the-README.patch$'),
                           ('A ', '.*/0002-Add-a-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        result = runCmd(cleanup_cmd)
        self._check_repo_status(os.path.dirname(recipefile), [])
        result = runCmd('git rev-parse HEAD~1', cwd=tempdir)
        midpoint_rev = result.output.strip()
        result = runCmd('devtool update-recipe --initial-rev %s %s' % (midpoint_rev, testrecipe))
        result = runCmd('git add minicom', cwd=os.path.dirname(recipefile))
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('A ', '.*/0001-Add-a-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        result = runCmd(cleanup_cmd)
        self._check_repo_status(os.path.dirname(recipefile), [])

    def test_devtool_update_recipe_git(self):
        # Check preconditions
        testrecipe = 'mtd-utils-selftest'
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
        addlines = ['SRCREV = ".*"', 'SRC_URI = "git://git.infradead.org/mtd-utils.git;branch=master"']
        srcurilines = src_uri.split()
        srcurilines[0] = 'SRC_URI = "' + srcurilines[0]
        srcurilines.append('"')
        removelines = ['SRCREV = ".*"'] + srcurilines
        self._check_diff(result.output, addlines, removelines)
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

    def test_devtool_update_recipe_gitsm(self):
        # Check that auto mode guesses srcrev update mode for a gitsm:// recipe
        # when HEAD is on the upstream branch, same as it does for git://
        testrecipe = 'git-submodule-test'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI', 'SRCREV'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        self.assertIn('gitsm://', src_uri, 'This test expects the %s recipe to be a gitsm recipe' % testrecipe)
        self._check_repo_status(os.path.dirname(recipefile), [])
        # First, modify a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        # Check git repo
        self._check_src_repo(tempdir)
        # Move the source tree to the parent of the pinned revision, staying
        # on the devtool branch: a revision reachable from the upstream branch
        # with no local commits, i.e. the "check out another upstream revision
        # to move the recipe" workflow the srcrev guess exists to detect
        result = runCmd('git rev-parse HEAD~1', cwd=tempdir)
        prevrev = result.output.strip()
        runCmd('git reset --hard %s' % prevrev, cwd=tempdir)
        # Sync the submodules to the newly checked out revision: with any
        # submodule checkout not matching the revision the parent records,
        # there is potential submodule content to export and the guesser
        # legitimately stays in patch mode
        runCmd('git submodule update --recursive', cwd=tempdir)
        self.add_command_to_tearDown('cd %s; git checkout %s' % (os.path.dirname(recipefile), os.path.basename(recipefile)))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile))]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        result = runCmd('git diff %s' % os.path.basename(recipefile), cwd=os.path.dirname(recipefile))
        addlines = ['SRCREV = "%s"' % prevrev]
        removelines = ['SRCREV = "%s"' % bb_vars['SRCREV']]
        self._check_diff(result.output, addlines, removelines)

    def test_devtool_update_recipe_append(self):
        # Check preconditions
        testrecipe = 'minicom'
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
        result = runCmd('echo "Additional line" >> README', cwd=tempsrcdir)
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
        expectedlines = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
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
        result = runCmd('git reset HEAD^ --hard', cwd=tempsrcdir)
        result = runCmd('devtool update-recipe %s -a %s' % (testrecipe, templayerdir))
        self.assertNotExists(patchfile, 'Patch file not deleted')
        expectedlines2 = ['FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
                         '\n']
        with open(bbappendfile, 'r') as f:
            self.assertEqual(expectedlines2, f.readlines())
        # Put commit back and check we can run it if layer isn't in bblayers.conf
        os.remove(bbappendfile)
        result = runCmd('echo "Additional line" >> README', cwd=tempsrcdir)
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
        testrecipe = 'mtd-utils-selftest'
        bb_vars = get_bb_vars(['FILE', 'SRC_URI', 'LAYERSERIES_CORENAMES'], testrecipe)
        recipefile = bb_vars['FILE']
        src_uri = bb_vars['SRC_URI']
        corenames = bb_vars['LAYERSERIES_CORENAMES']
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
            f.write('LAYERSERIES_COMPAT_oeselftesttemplayer = "%s"\n' % corenames)
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
        result = runCmd('git reset HEAD^ --hard', cwd=tempsrcdir)
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
        result = runCmd('echo "# Additional line" >> Makefile.am', cwd=tempsrcdir)
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
        runCmd('echo "/* Foobar */" >> makedevs.c', cwd=tempdir)
        runCmd('echo "Foo" > new-local', cwd=tempdir)
        runCmd('echo "Bar" > new-file', cwd=tempdir)
        runCmd('git add new-file', cwd=tempdir)
        runCmd('git commit -m "Add new file"', cwd=tempdir)
        runCmd('git add new-local', cwd=tempdir)
        runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           (' M', '.*/makedevs/makedevs.c$'),
                           ('??', '.*/makedevs/new-local$'),
                           ('??', '.*/makedevs/0001-Add-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        # Now try to update recipe in another layer, so first, clean it
        runCmd('cd %s; git clean -fd .; git checkout .' % os.path.dirname(recipefile))
        # Create a temporary layer and add it to bblayers.conf
        self._create_temp_layer(templayerdir, True, 'templayer')
        # Update recipe in templayer
        result = runCmd('devtool update-recipe %s -a %s' % (testrecipe, templayerdir))
        self.assertNotIn('WARNING:', result.output)
        # Check recipe is still clean
        self._check_repo_status(os.path.dirname(recipefile), [])
        splitpath = os.path.dirname(recipefile).split(os.sep)
        appenddir = os.path.join(templayerdir, splitpath[-2], splitpath[-1])
        bbappendfile = self._check_bbappend(testrecipe, recipefile, appenddir)
        patchfile = os.path.join(appenddir, testrecipe, '0001-Add-new-file.patch')
        new_local_file = os.path.join(appenddir, testrecipe, 'new_local')
        local_file = os.path.join(appenddir, testrecipe, 'makedevs.c')
        self.assertExists(patchfile, 'Patch file 0001-Add-new-file.patch not created')
        self.assertExists(local_file, 'File makedevs.c not created')
        self.assertExists(patchfile, 'File new_local not created')

    def _test_devtool_update_recipe_local_files_2(self):
        """Check local source files support when editing local files in Git"""
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
        # Edit / commit local sources
        runCmd('echo "# Foobar" >> file1', cwd=tempdir)
        runCmd('git commit -am "Edit existing file"', cwd=tempdir)
        runCmd('git rm file2', cwd=tempdir)
        runCmd('git commit -m"Remove file"', cwd=tempdir)
        runCmd('echo "Foo" > new-local', cwd=tempdir)
        runCmd('git add new-local', cwd=tempdir)
        runCmd('git commit -m "Add new local file"', cwd=tempdir)
        runCmd('echo "Gar" > new-file', cwd=tempdir)
        runCmd('git add new-file', cwd=tempdir)
        runCmd('git commit -m "Add new file"', cwd=tempdir)
        self.add_command_to_tearDown('cd %s; git clean -fd .; git checkout .' %
                                     os.path.dirname(recipefile))
        # Checkout unmodified file to working copy -> devtool should still pick
        # the modified version from HEAD
        runCmd('git checkout HEAD^ -- file1', cwd=tempdir)
        runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           (' M', '.*/file1$'),
                           (' D', '.*/file2$'),
                           ('??', '.*/new-local$'),
                           ('??', '.*/0001-Add-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

    def test_devtool_update_recipe_with_gitignore(self):
        # First, modify the recipe
        testrecipe = 'devtool-test-ignored'
        bb_vars = get_bb_vars(['FILE'], testrecipe)
        recipefile = bb_vars['FILE']
        patchfile = os.path.join(os.path.dirname(recipefile), testrecipe, testrecipe + '.patch')
        newpatchfile = os.path.join(os.path.dirname(recipefile), testrecipe, testrecipe + '.patch.expected')
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s' % testrecipe)
        self.add_command_to_tearDown('cd %s; rm %s/*; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool finish --force-patch-refresh %s meta-selftest' % testrecipe)
        # Check recipe got changed as expected
        with open(newpatchfile, 'r') as f:
            desiredlines = f.readlines()
        with open(patchfile, 'r') as f:
            newlines = f.readlines()
        # Ignore the initial lines, because oe-selftest creates own meta-selftest repo
        # which changes the metadata subject which is added into the patch, but keep
        # .patch.expected as it is in case someone runs devtool finish --force-patch-refresh
        # devtool-test-ignored manually, then it should generate exactly the same .patch file
        self.assertEqual(desiredlines[5:], newlines[5:])

    def test_devtool_update_recipe_long_filename(self):
        # First, modify the recipe
        testrecipe = 'devtool-test-long-filename'
        bb_vars = get_bb_vars(['FILE'], testrecipe)
        recipefile = bb_vars['FILE']
        patchfilename = '0001-I-ll-patch-you-only-if-devtool-lets-me-to-do-it-corr.patch'
        patchfile = os.path.join(os.path.dirname(recipefile), testrecipe, patchfilename)
        newpatchfile = os.path.join(os.path.dirname(recipefile), testrecipe, patchfilename + '.expected')
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # (don't bother with cleaning the recipe on teardown, we won't be building it)
        result = runCmd('devtool modify %s' % testrecipe)
        self.add_command_to_tearDown('cd %s; rm %s/*; git checkout %s %s' % (os.path.dirname(recipefile), testrecipe, testrecipe, os.path.basename(recipefile)))
        result = runCmd('devtool finish --force-patch-refresh %s meta-selftest' % testrecipe)
        # Check recipe got changed as expected
        with open(newpatchfile, 'r') as f:
            desiredlines = f.readlines()
        with open(patchfile, 'r') as f:
            newlines = f.readlines()
        # Ignore the initial lines, because oe-selftest creates own meta-selftest repo
        # which changes the metadata subject which is added into the patch, but keep
        # .patch.expected as it is in case someone runs devtool finish --force-patch-refresh
        # devtool-test-ignored manually, then it should generate exactly the same .patch file
        self.assertEqual(desiredlines[5:], newlines[5:])

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
        runCmd('echo "Another line" >> file2', cwd=os.path.join(self.workspacedir, 'sources', testrecipe))
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
        runCmd('git commit -a --amend --no-edit --no-verify', cwd=srctree)
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

    def test_devtool_finish_modify_git_subdir(self):
        # Check preconditions
        testrecipe = 'dos2unix'
        self.append_config('ERROR_QA:remove:pn-dos2unix = "patch-status"\n')
        bb_vars = get_bb_vars(['SRC_URI', 'S', 'UNPACKDIR', 'FILE', 'BB_GIT_DEFAULT_DESTSUFFIX'], testrecipe)
        self.assertIn('git://', bb_vars['SRC_URI'], 'This test expects the %s recipe to be a git recipe' % testrecipe)
        unpackdir_git = '%s/%s/' % (bb_vars['UNPACKDIR'], bb_vars['BB_GIT_DEFAULT_DESTSUFFIX'])
        if not bb_vars['S'].startswith(unpackdir_git):
            self.fail('This test expects the %s recipe to be building from a subdirectory of the git repo' % testrecipe)
        subdir = bb_vars['S'].split(unpackdir_git, 1)[1]
        # Clean up anything in the workdir/sysroot/sstate cache
        bitbake('%s -c cleansstate' % testrecipe)
        # Try modifying a recipe
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        testsrcfile = os.path.join(tempdir, subdir, 'dos2unix.c')
        self.assertExists(testsrcfile, 'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'), 'Workspace directory not created. devtool output: %s' % result.output)
        self.assertNotExists(os.path.join(tempdir, subdir, '.git'), 'Subdirectory has been initialised as a git repo')
        # Check git repo
        self._check_src_repo(tempdir)
        # Modify file
        runCmd("sed -i '1s:^:/* Add a comment */\\n:' %s" % testsrcfile)
        result = runCmd('git commit -a -m "Add a comment"', cwd=tempdir)
        # Now try updating original recipe
        recipefile = bb_vars['FILE']
        recipedir = os.path.dirname(recipefile)
        self.add_command_to_tearDown('cd %s; rm -f %s/*.patch; git checkout .' % (recipedir, testrecipe))
        result = runCmd('devtool update-recipe %s' % testrecipe)
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('??', '.*/%s/%s/$' % (testrecipe, testrecipe))]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)
        result = runCmd('git diff %s' % os.path.basename(recipefile), cwd=os.path.dirname(recipefile))
        removelines = ['SRC_URI = "git://.*"']
        addlines = [
            'SRC_URI = "git://.* \\\\',
            'file://0001-Add-a-comment.patch;patchdir=.. \\\\',
            '"'
        ]
        self._check_diff(result.output, addlines, removelines)
        # Put things back so we can run devtool finish on a different layer
        runCmd('cd %s; rm -f %s/*.patch; git checkout .' % (recipedir, testrecipe))
        # Run devtool finish
        res = re.search('recipes-.*', recipedir)
        self.assertTrue(res, 'Unable to find recipe subdirectory')
        recipesubdir = res[0]
        self.add_command_to_tearDown('rm -rf %s' % os.path.join(self.testlayer_path, recipesubdir))
        result = runCmd('devtool finish %s meta-selftest' % testrecipe)
        # Check bbappend file contents
        appendfn = os.path.join(self.testlayer_path, recipesubdir, '%s_%%.bbappend' % testrecipe)
        with open(appendfn, 'r') as f:
            appendlines = f.readlines()
        expected_appendlines = [
            'FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"\n',
            '\n',
            'SRC_URI += "file://0001-Add-a-comment.patch;patchdir=.."\n',
            '\n'
        ]
        self.assertEqual(appendlines, expected_appendlines)
        self.assertExists(os.path.join(os.path.dirname(appendfn), testrecipe, '0001-Add-a-comment.patch'))
        # Try building
        bitbake('%s -c patch' % testrecipe)

    @skipIfNotFeature('vulkan', 'Test requires vulkan to be in DISTRO_FEATURES (operates on vulkan-samples)')
    def test_devtool_git_submodules(self):
        # This tests if we can add a patch in a git submodule and extract it properly using devtool finish
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        recipe = 'vulkan-samples'
        src_uri = get_bb_var('SRC_URI', recipe)
        self.assertIn('gitsm://', src_uri, 'This test expects the %s recipe to be a git recipe with submodules' % recipe)
        oldrecipefile = get_bb_var('FILE', recipe)
        recipedir = os.path.dirname(oldrecipefile)
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains uncommitted changes' % recipe)
        self.assertIn('/meta/', recipedir)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s %s' % (recipe, tempdir))
        self.assertExists(os.path.join(tempdir, 'CMakeLists.txt'), 'Extracted source could not be found')
        # Test devtool status
        result = runCmd('devtool status')
        self.assertIn(recipe, result.output)
        self.assertIn(tempdir, result.output)
        # Modify a source file in a submodule, (grab the first one)
        result = runCmd('git submodule --quiet foreach \'echo $sm_path\'', cwd=tempdir)
        submodule = result.output.splitlines()[0]
        submodule_path = os.path.join(tempdir, submodule)
        runCmd('echo "#This is a first comment" >> testfile', cwd=submodule_path)
        result = runCmd('git status --porcelain . ', cwd=submodule_path)
        self.assertIn("testfile", result.output)
        runCmd('git add testfile; git commit -m "Adding a new file"', cwd=submodule_path)

        # Try finish to the original layer
        self.add_command_to_tearDown('rm -rf %s ; cd %s ; git checkout %s' % (recipedir, os.path.dirname(recipedir), recipedir))
        runCmd('devtool finish -f %s meta' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after finish')
        expected_status = [(' M', '.*/%s$' % os.path.basename(oldrecipefile)),
                           ('??', '.*/vulkan/vulkan-samples/$')]
        self._check_repo_status(recipedir, expected_status)
        # Make sure the patch is added to the recipe with the correct "patchdir" option
        result = runCmd('git diff .', cwd=recipedir)
        addlines = [
           'file://0001-Adding-a-new-file.patch;patchdir=%s \\\\' % submodule
        ]
        self._check_diff(result.output, addlines, [])

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

class DevtoolResetTests(DevtoolBase):

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

    def test_devtool_reset_re_plus_plus(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        testrecipe = 'devtool-test-reset-re++'
        result = runCmd('devtool modify %s' % testrecipe)
        result = runCmd('devtool reset -n %s' % testrecipe)
        self.assertIn(testrecipe, result.output)
        result = runCmd('devtool status')
        self.assertNotIn(testrecipe, result.output)
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', testrecipe), 'Recipe directory should not exist after resetting')

class DevtoolDeployTargetTests(DevtoolBase):

    @OETestTag("runqemu")
    def test_devtool_deploy_target(self):
        self._check_runqemu_prerequisites()
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        # Definitions
        testrecipe = 'mdadm'
        testfile = '/sbin/mdadm'
        # mdmon is installed by the same do_install, used to check --package/--file-glob filtering
        otherfile = '/sbin/mdmon'
        if "usrmerge" in get_bb_var('DISTRO_FEATURES'):
            testfile = '/usr/sbin/mdadm'
            otherfile = '/usr/sbin/mdmon'
        # Use the mdadm-doc package for testing --package and --file-glob filtering
        mandir = get_bb_var('mandir', testrecipe)
        docfile = os.path.join(mandir, 'man8', '%s.8' % testrecipe)
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
            def _deploy_and_check(extra_args, check_full_filelist, expected_files):
                """Deploy with extra_args, verify the result, then undeploy and verify removal.

                check_full_filelist is only meaningful for an unfiltered deploy: it
                compares every file installed by do_install against what actually
                landed on the target. For a --package/--file-glob filtered deploy,
                expected_files instead maps a handful of representative paths
                (testfile, otherfile, docfile) to whether they should have been
                deployed by this particular filter.
                """
                deploy_cmd = 'devtool deploy-target -c %s root@%s %s' % (testrecipe, qemu.ip, extra_args)
                if self.logger.isEnabledFor(logging.DEBUG):
                    deploy_cmd += ' -s'
                result = runCmd(deploy_cmd)
                self.logger.debug('%s\n%s' % (deploy_cmd, result.output))

                if check_full_filelist:
                    # Run a test command to see if it was installed properly
                    status, _ = qemu.run(testcommand)
                    self.assertEqual(status, 0, '%s: %s was not deployed' % (extra_args, testfile))

                    # Check if it deployed all of the files with the right ownership/perms
                    # First look on the host - need to do this under pseudo to get the correct ownership/perms
                    bb_vars = get_bb_vars(['D', 'FAKEROOTENV', 'FAKEROOTCMD', 'PATH'], testrecipe)
                    installdir = bb_vars['D']
                    fakerootenv = bb_vars['FAKEROOTENV']
                    fakerootcmd = bb_vars['FAKEROOTCMD']
                    path = bb_vars['PATH']
                    result = runCmd('PATH="%s" %s %s find . -type f -exec ls -l {} \\;' % (path, fakerootenv, fakerootcmd), cwd=installdir)
                    filelist1 = self._process_ls_output(result.output)

                    # Now look on the target
                    tempdir2 = tempfile.mkdtemp(prefix='devtoolqa')
                    self.track_for_cleanup(tempdir2)
                    tmpfilelist = os.path.join(tempdir2, 'files.txt')
                    with open(tmpfilelist, 'w') as f:
                        for line in filelist1:
                            splitline = line.split()
                            f.write(splitline[-1] + '\n')
                    remotefilelist = '/tmp/%s' % os.path.basename(tmpfilelist)
                    status, _ = qemu.copy_to(tmpfilelist, remotefilelist)
                    self.assertEqual(status, 0)
                    status, output = qemu.run(
                        'xargs ls -l < %s; status=$?; rm -f %s; exit $status' % (
                            remotefilelist, remotefilelist))
                    self.assertEqual(status, 0)
                    filelist2 = self._process_ls_output(output)
                    filelist1.sort(key=lambda item: item.split()[-1])
                    filelist2.sort(key=lambda item: item.split()[-1])
                    self.assertEqual(filelist1, filelist2)
                else:
                    for path, expected in expected_files.items():
                        status, _ = qemu.run('test -e %s' % path)
                        if expected:
                            self.assertEqual(status, 0, '%s: %s should have been deployed' % (extra_args, path))
                        else:
                            self.assertNotEqual(status, 0, '%s: %s should not have been deployed' % (extra_args, path))

                # Test undeploy-target
                runCmd('devtool undeploy-target -c %s root@%s' % (testrecipe, qemu.ip))
                status, _ = qemu.run(testcommand)
                self.assertNotEqual(status, 0, 'undeploy-target did not remove command as it should have')

            filter_cases = [
                ('', {}),
                ('--package %s' % testrecipe,
                 {testfile: True, otherfile: True, docfile: False}),
                ('--package %s-doc' % testrecipe,
                 {testfile: False, otherfile: False, docfile: True}),
                ('--package %s,%s-doc' % (testrecipe, testrecipe),
                 {testfile: True, otherfile: True, docfile: True}),
                ('--file-glob %s' % testfile,
                 {testfile: True, otherfile: False, docfile: False}),
                ('--package %s --file-glob %s' % (testrecipe, testfile),
                 {testfile: True, otherfile: True, docfile: False}),
                # "RECIPE:" prefix (needed so a single 'devtool ide-sdk' invocation
                # can scope --package/--file-glob entries to one of several recipes)
                ('--package %s:%s' % (testrecipe, testrecipe),
                 {testfile: True, otherfile: True, docfile: False}),
                # shorthand: "RECIPE:-suffix" expands to "RECIPE-suffix"
                ('--package %s:-doc' % testrecipe,
                 {testfile: False, otherfile: False, docfile: True}),
                # shorthand: empty item + "-suffix" combined in one entry
                ('--package %s:,-doc' % testrecipe,
                 {testfile: True, otherfile: True, docfile: True}),
                # a "RECIPE:" prefix that doesn't match this recipe is skipped,
                # leaving no package filter applied (same as passing none at all)
                ('--package other-recipe:%s' % testrecipe,
                 {testfile: True, otherfile: True, docfile: True}),
                ('--file-glob %s:%s' % (testrecipe, testfile),
                 {testfile: True, otherfile: False, docfile: False}),
                ('--file-glob other-recipe:%s' % testfile,
                 {testfile: True, otherfile: True, docfile: True}),
            ]
            for strip_opt in ['', '--strip']:
                for filter_args, expected_files in filter_cases:
                    extra_args = ' '.join(a for a in (strip_opt, filter_args) if a)
                    _deploy_and_check(extra_args, check_full_filelist=not filter_args, expected_files=expected_files)

    @OETestTag("runqemu")
    def test_devtool_deploy_target_path(self):
        """Verify 'devtool deploy-target/undeploy-target <recipe> <path>' deploy/remove straight into
        a local pseudo-managed rootfs directory (no ssh), and that a target
        booting that same directory via NFS immediately sees the change.
        """
        self._check_runqemu_prerequisites()
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        testrecipe = 'mdadm'
        testfile = '/sbin/mdadm'
        # mdmon is installed by the same do_install, used to check --file-glob filtering
        otherfile = '/sbin/mdmon'
        if "usrmerge" in get_bb_var('DISTRO_FEATURES'):
            testfile = '/usr/sbin/mdadm'
            otherfile = '/usr/sbin/mdmon'
        testcommand = '/sbin/mdadm --help'
        testimage = 'oe-selftest-image'
        # Use the mdadm-doc package to check --package filtering excludes it
        mandir = get_bb_var('mandir', testrecipe)
        docfile = os.path.join(mandir, 'man8', '%s.8' % testrecipe)

        # A tar rootfs is needed both to extract a local copy of it (below)
        # and for runqemu to NFS-boot straight from that extracted directory.
        self.append_config('IMAGE_FSTYPES:append = " tar"\n')
        bitbake("%s qemu-native qemu-helper-native" % testimage)
        bb_vars = get_bb_vars(['DEPLOY_DIR_IMAGE', 'IMAGE_LINK_NAME'], testimage)
        deploy_dir_image = bb_vars['DEPLOY_DIR_IMAGE']
        image_link_name = bb_vars['IMAGE_LINK_NAME']
        self.add_command_to_tearDown('bitbake -c clean %s' % testimage)
        self.add_command_to_tearDown('rm -f %s/%s*' % (deploy_dir_image, testimage))

        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake -c clean %s' % testrecipe)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        runCmd('devtool modify %s -x %s' % (testrecipe, tempdir))
        runCmd('devtool build %s' % testrecipe)

        # Extract a local pseudo-managed rootfs the same way
        rootfs_tarball = os.path.join(deploy_dir_image, image_link_name + '.tar')
        self.assertExists(rootfs_tarball)
        extractdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(extractdir)
        nfs_rootfs = os.path.join(extractdir, 'rootfs')
        runCmd('runqemu-extract-sdk %s %s' % (rootfs_tarball, nfs_rootfs))
        self.assertExists(nfs_rootfs)
        self.assertExists(nfs_rootfs + '.pseudo_state')

        # oe-selftest-image does not install mdadm by default, so the target must not see it yet.
        self.assertNotExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))

        qemuboot = os.path.join(deploy_dir_image, image_link_name + '.qemuboot.conf')
        self.assertExists(qemuboot)
        launch_cmd = 'runqemu %s %s nographic' % (shlex.quote(qemuboot), shlex.quote(nfs_rootfs))
        with runqemu(testimage, launch_cmd=launch_cmd) as qemu:
            status, output = qemu.run("awk '$2 == \"/\" {print $3}' /proc/mounts")
            self.assertEqual(status, 0)
            self.assertEqual(output.strip(), 'nfs')

            status, _ = qemu.run(testcommand)
            self.assertNotEqual(status, 0, '%s should not be deployed yet' % testfile)

            # Deploy directly into the local rootfs path (no ssh) while the target has it NFS-mounted live
            deploy_cmd = 'devtool deploy-target %s %s' % (testrecipe, nfs_rootfs)
            if self.logger.isEnabledFor(logging.DEBUG):
                deploy_cmd += ' -s'
            result = runCmd(deploy_cmd)
            self.assertEqual(result.status, 0)
            self.assertExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))
            self.assertExists(os.path.join(nfs_rootfs, otherfile.lstrip('/')))

            status, _ = qemu.run(testcommand)
            self.assertEqual(status, 0, '%s was not deployed' % testfile)

            # Deploying again while the target still has this directory NFS-mounted live must still succeed.
            result = runCmd(deploy_cmd)
            self.assertEqual(result.status, 0)

            # Undeploy directly from the local rootfs path (no ssh) while the target still has it NFS-mounted live
            undeploy_cmd = 'devtool undeploy-target %s %s' % (testrecipe, nfs_rootfs)
            if self.logger.isEnabledFor(logging.DEBUG):
                undeploy_cmd += ' -s'
            result = runCmd(undeploy_cmd)
            self.assertEqual(result.status, 0)
            self.assertNotExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))

            status, _ = qemu.run(testcommand)
            self.assertNotEqual(status, 0, 'undeploy-target did not remove %s as it should have' % testfile)

            # Confirm the local-path deploy (dual-pseudo pipe) and the ssh deploy
            # (tar over ssh) land identical files with identical ownership/perms
            # into the very same live NFS-exported directory, for both an
            # unfiltered deploy and a --package filtered one.
            bb_vars = get_bb_vars(['FAKEROOTENV', 'FAKEROOTCMD', 'PATH'], testrecipe)
            fakerootenv = bb_vars['FAKEROOTENV']
            fakerootcmd = bb_vars['FAKEROOTCMD']
            path = bb_vars['PATH']
            state_dir = nfs_rootfs + '.pseudo_state'

            def _get_rootfs_filelist():
                # nfs_rootfs has its own pseudo database, distinct from the one
                # FAKEROOTENV points at, so override it for this inspection.
                cmd = 'PATH="%s" %s PSEUDO_LOCALSTATEDIR=%s PSEUDO_INCLUDE_PATHS=%s %s find . -type f -exec ls -l {} \\;' % (
                    path, fakerootenv, shlex.quote(state_dir), shlex.quote(nfs_rootfs), fakerootcmd)
                result = runCmd(cmd, cwd=nfs_rootfs)
                filelist = self._process_ls_output(result.output)
                filelist.sort(key=lambda item: item.split()[-1])
                return filelist

            def _compare_local_and_ssh_deploy(extra_args, expected_files):
                """Deploy extra_args once locally and once over ssh into the same
                live directory, and assert both leave identical files with
                identical ownership/permissions (expected_files checks each path
                was/wasn't deployed, on both sides)."""
                local_cmd = ('%s %s' % (deploy_cmd, extra_args)).strip()
                result = runCmd(local_cmd)
                self.assertEqual(result.status, 0)
                for filepath, expected in expected_files.items():
                    exists = os.path.exists(os.path.join(nfs_rootfs, filepath.lstrip('/')))
                    self.assertEqual(exists, expected, '%s: %s exists=%s after local deploy' % (extra_args, filepath, exists))
                local_filelist = _get_rootfs_filelist()

                result = runCmd(undeploy_cmd)
                self.assertEqual(result.status, 0)
                self.assertNotExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))

                ssh_cmd = ('devtool deploy-target -c %s root@%s %s' % (testrecipe, qemu.ip, extra_args)).strip()
                result = runCmd(ssh_cmd)
                self.assertEqual(result.status, 0)
                for filepath, expected in expected_files.items():
                    exists = os.path.exists(os.path.join(nfs_rootfs, filepath.lstrip('/')))
                    self.assertEqual(exists, expected, '%s: %s exists=%s after ssh deploy' % (extra_args, filepath, exists))
                ssh_filelist = _get_rootfs_filelist()

                self.assertEqual(local_filelist, ssh_filelist,
                                '%s: local-path deploy and ssh deploy produced different file ownership/permissions' % extra_args)

                result = runCmd('devtool undeploy-target -c %s root@%s' % (testrecipe, qemu.ip))
                self.assertEqual(result.status, 0)
                self.assertNotExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))

            _compare_local_and_ssh_deploy('', {testfile: True, otherfile: True, docfile: True})
            _compare_local_and_ssh_deploy('--package %s' % testrecipe, {testfile: True, otherfile: True, docfile: False})

            # A --package/--file-glob filtered deploy hands tar an explicit file
            # list instead of packing the whole tree, so cover that path as well.
            filtered_deploy_cmd = deploy_cmd + ' --file-glob %s' % testfile
            result = runCmd(filtered_deploy_cmd)
            self.assertEqual(result.status, 0)
            self.assertExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))
            self.assertNotExists(os.path.join(nfs_rootfs, otherfile.lstrip('/')))

            status, _ = qemu.run(testcommand)
            self.assertEqual(status, 0, '%s was not deployed' % testfile)

            result = runCmd(undeploy_cmd)
            self.assertEqual(result.status, 0)
            self.assertNotExists(os.path.join(nfs_rootfs, testfile.lstrip('/')))


class DevtoolBuildImageTests(DevtoolBase):

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

    def setUp(self):
        super().setUp()
        try:
            runCmd("git config --global user.name")
            runCmd("git config --global user.email")
        except:
            self.skip("Git user.name and user.email must be set")

    def _check_changelog(self, recipe, oldrecipefile):
        """Compare extracted changelog against reference data."""
        changelog_ref = oldrecipefile + '.changelog'
        self.assertExists(changelog_ref, 'Changelog reference file must exist for %s' % recipe)
        changelog_file = os.path.join(self.workspacedir, 'changelogs', '%s.txt' % recipe)
        with open(changelog_ref, 'r') as f:
            expected = f.read()
        if not expected:
            self.assertNotExists(changelog_file,
                'Changelog file should not exist when reference is empty')
        else:
            self.assertExists(changelog_file, 'Changelog file should exist after upgrade')
            with open(changelog_file, 'r') as f:
                actual = f.read()
            self.assertEqual(expected, actual)

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
        # Check changelog
        self._check_changelog(recipe, oldrecipefile)
        # Check devtool reset recipe
        result = runCmd('devtool reset %s -n' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after resetting')

    def _test_devtool_upgrade_git_by_recipe(self, recipe, commit):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
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
        # Check changelog
        self._check_changelog(recipe, oldrecipefile)
        # Check devtool reset recipe
        result = runCmd('devtool reset %s -n' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after resetting')
        self.assertNotExists(os.path.join(self.workspacedir, 'changelogs', '%s.txt' % recipe), 'Changelog file should be removed after reset')

    def test_devtool_upgrade_git(self):
        self._test_devtool_upgrade_git_by_recipe('devtool-upgrade-test2', '6cc6077a36fe2648a5f993fe7c16c9632f946517')

    def test_devtool_upgrade_gitsm(self):
        self._test_devtool_upgrade_git_by_recipe('devtool-upgrade-test5', '0a60d6af95d22b4c50446559cd41942a8acd2d57')

    def test_devtool_upgrade_drop_md5sum(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # For the moment, we are using a real recipe.
        recipe = 'devtool-upgrade-test3'
        version = '1.6.0'
        oldrecipefile = get_bb_var('FILE', recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        # Check upgrade. Code does not check if new PV is older or newer that current PV, so, it may be that
        # we are downgrading instead of upgrading.
        result = runCmd('devtool upgrade %s %s -V %s' % (recipe, tempdir, version))
        # Check new recipe file is present
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipe, '%s_%s.bb' % (recipe, version))
        self.assertExists(newrecipefile, 'Recipe file should exist after upgrade')
        # Check recipe got changed as expected
        with open(oldrecipefile + '.upgraded', 'r') as f:
            desiredlines = f.readlines()
        with open(newrecipefile, 'r') as f:
            newlines = f.readlines()
        self.assertEqual(desiredlines, newlines)
        # Check changelog
        self._check_changelog(recipe, oldrecipefile)

    def test_devtool_upgrade_all_checksums(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        # For the moment, we are using a real recipe.
        recipe = 'devtool-upgrade-test4'
        version = '1.6.0'
        oldrecipefile = get_bb_var('FILE', recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        # Check upgrade. Code does not check if new PV is older or newer that current PV, so, it may be that
        # we are downgrading instead of upgrading.
        result = runCmd('devtool upgrade %s %s -V %s' % (recipe, tempdir, version))
        # Check new recipe file is present
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipe, '%s_%s.bb' % (recipe, version))
        self.assertExists(newrecipefile, 'Recipe file should exist after upgrade')
        # Check recipe got changed as expected
        with open(oldrecipefile + '.upgraded', 'r') as f:
            desiredlines = f.readlines()
        with open(newrecipefile, 'r') as f:
            newlines = f.readlines()
        self.assertEqual(desiredlines, newlines)
        # Check changelog
        self._check_changelog(recipe, oldrecipefile)

    def test_devtool_upgrade_recipe_upgrade_extra_tasks(self):
        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        recipe = 'python3-guessing-game'
        version = '0.2.0'
        commit = '40cf004c2772ffa20ea803fa3be1528a75be3e98'
        oldrecipefile = get_bb_var('FILE', recipe)
        oldcratesincfile = os.path.join(os.path.dirname(oldrecipefile), os.path.basename(oldrecipefile).strip('_git.bb') + '-crates.inc')
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        # Check that recipe is not already under devtool control
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output)
        # Check upgrade
        result = runCmd('devtool upgrade %s %s --version %s --srcrev %s' % (recipe, tempdir, version, commit))
        # Check if srctree at least is populated
        self.assertTrue(len(os.listdir(tempdir)) > 0, 'srctree (%s) should be populated with new (%s) source code' % (tempdir, commit))
        # Check new recipe file and new -crates.inc files are present
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipe, os.path.basename(oldrecipefile))
        newcratesincfile = os.path.join(self.workspacedir, 'recipes', recipe, os.path.basename(oldcratesincfile))
        self.assertExists(newrecipefile, 'Recipe file should exist after upgrade')
        self.assertExists(newcratesincfile, 'Recipe crates.inc file should exist after upgrade')
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
        # Check crates.inc got changed as expected
        with open(oldcratesincfile + '.upgraded', 'r') as f:
            desiredlines = f.readlines()
        with open(newcratesincfile, 'r') as f:
            newlines = f.readlines()
        self.assertEqual(desiredlines, newlines)
        # Check changelog
        self._check_changelog(recipe, oldrecipefile)
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
            searchpath = [
                path for path in searchpath
                if self._copy_file_with_cleanup(srcfile, path, 'lib', 'devtool')
            ]
            result = runCmd("devtool --quiet count")
            self.assertEqual(result.output, '1')
            result = runCmd("devtool --quiet multiloaded")
            self.assertEqual(result.output, "no")
            for path in searchpath:
                result = runCmd("devtool --quiet bbdir")
                self.assertEqual(os.path.realpath(result.output), os.path.realpath(path))
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
        # Make another change to the source
        result = runCmd('sed -i \'/^#include "mdadm.h"/a \\/* Here is another comment *\\/\' maps.c', cwd=tempdir)
        result = runCmd('git status --porcelain', cwd=tempdir)
        self.assertIn('M maps.c', result.output)
        result = runCmd('git commit maps.c -m "Add another comment to the code"', cwd=tempdir)
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
                           ('??', '.*/.*-Add-a-comment-to-the-code.patch$'),
                           ('??', '.*/.*-Add-another-comment-to-the-code.patch$')]
        self._check_repo_status(recipedir, expected_status)
        result = runCmd('git diff %s' % os.path.basename(oldrecipefile), cwd=os.path.dirname(oldrecipefile))
        # Check that the recipe got updated as expected
        # Can't use self._check_diff() as the order of the added files matter.
        result = result.output.splitlines()
        self.assertEqual('+           file://0001-Add-a-comment-to-the-code.patch \\', result[8])
        self.assertEqual('+           file://0002-Add-another-comment-to-the-code.patch \\', result[9])

    def test_devtool_finish_modify_otherlayer(self):
        recipe, oldrecipefile, recipedir, filesdir = self._setup_test_devtool_finish_modify()
        # Ensure the recipe is where we think it should be (so that cleanup doesn't trash things)
        self.assertIn('/meta/', recipedir)
        relpth = os.path.relpath(recipedir, os.path.join(get_bb_var('COREBASE'), 'meta'))
        appenddir = os.path.join(get_test_layer(), relpth)
        self.track_for_cleanup(appenddir)
        # Try finish to another layer than the original layer
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
        # Check that the bbappend got created as expected
        with open(appendfile, 'r') as f:
            newlines = f.readlines()
        self.assertEqual('SRC_URI += "file://0001-Add-a-comment-to-the-code.patch file://0002-Add-another-comment-to-the-code.patch"\n', newlines[2])
        newdir = os.path.join(appenddir, recipe)
        files = os.listdir(newdir)
        foundpatch = False
        for fn in files[:]:
            if fnmatch.fnmatch(fn, '*-Add-a*-comment-to-the-code.patch'):
                files.remove(fn)
                foundpatch = True
        if not foundpatch:
            self.fail('No patch file created next to bbappend')
        if files:
            self.fail('Unexpected file(s) copied next to bbappend: %s' % ', '.join(files))

    def test_devtool_finish_update_patch(self):
        # This test uses a modified version of the sysdig recipe from meta-oe.
        # - The patches have been renamed.
        # - The dependencies are commented out since the recipe is not being
        #   built.
        #
        # The sysdig recipe is interesting in that it fetches two different Git
        # repositories, and there are patches for both. This leads to that
        # devtool will create ignore commits as it uses Git submodules to keep
        # track of the second repository.
        #
        # This test will verify that the ignored commits actually are ignored
        # when a commit in between is modified. It will also verify that the
        # updated patch keeps its original name.

        # Check preconditions
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        # Try modifying a recipe
        self.track_for_cleanup(self.workspacedir)
        recipe = 'sysdig-selftest'
        recipefile = get_bb_var('FILE', recipe)
        recipedir = os.path.dirname(recipefile)
        result = runCmd('git status --porcelain .', cwd=recipedir)
        if result.output.strip():
            self.fail('Recipe directory for %s contains uncommitted changes' % recipe)
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')
        result = runCmd('devtool modify %s %s' % (recipe, tempdir))
        self.add_command_to_tearDown('cd %s; rm %s/*; git checkout %s %s' % (recipedir, recipe, recipe, os.path.basename(recipefile)))
        self.assertExists(os.path.join(tempdir, 'CMakeLists.txt'), 'Extracted source could not be found')
        # Make a change to one of the existing commits
        result = runCmd('echo "# A comment " >> CMakeLists.txt', cwd=tempdir)
        result = runCmd('git status --porcelain', cwd=tempdir)
        self.assertIn('M CMakeLists.txt', result.output)
        result = runCmd('git commit --fixup HEAD^ CMakeLists.txt', cwd=tempdir)
        result = runCmd('git show -s --format=%s', cwd=tempdir)
        self.assertIn('fixup! cmake: Pass PROBE_NAME via CFLAGS', result.output)
        result = runCmd('GIT_SEQUENCE_EDITOR=true git rebase -i --autosquash devtool-base', cwd=tempdir)
        result = runCmd('devtool finish %s meta-selftest' % recipe)
        result = runCmd('devtool status')
        self.assertNotIn(recipe, result.output, 'Recipe should have been reset by finish but wasn\'t')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipe), 'Recipe directory should not exist after finish')
        expected_status = [(' M', '.*/0099-cmake-Pass-PROBE_NAME-via-CFLAGS.patch$')]
        self._check_repo_status(recipedir, expected_status)

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
        checkvars['S'] = '${UNPACKDIR}/%s-%s' % (recipename, recipever)
        checkvars['SRC_URI'] = url
        self._test_recipe_contents(newrecipefile, checkvars, [])
        # Try again - change just name this time
        result = runCmd('devtool reset -n %s' % newrecipename)
        add_recipe()
        newrecipefile = os.path.join(self.workspacedir, 'recipes', newrecipename, '%s_%s.bb' % (newrecipename, recipever))
        result = runCmd('devtool rename %s %s' % (recipename, newrecipename))
        self.assertExists(newrecipefile, 'Recipe file not renamed')
        self.assertNotExists(os.path.join(self.workspacedir, 'recipes', recipename), 'Old recipe directory still exists')
        self.assertExists(os.path.join(self.workspacedir, 'sources', newrecipename), 'Source directory not renamed')
        checkvars = {}
        checkvars['S'] = '${UNPACKDIR}/%s-${PV}' % recipename
        checkvars['SRC_URI'] = url.replace(recipever, '${PV}')
        self._test_recipe_contents(newrecipefile, checkvars, [])
        # Try again - change just version this time
        result = runCmd('devtool reset -n %s' % newrecipename)
        add_recipe()
        newrecipefile = os.path.join(self.workspacedir, 'recipes', recipename, '%s_%s.bb' % (recipename, newrecipever))
        result = runCmd('devtool rename %s -V %s' % (recipename, newrecipever))
        self.assertExists(newrecipefile, 'Recipe file not renamed')
        self.assertExists(os.path.join(self.workspacedir, 'sources', recipename), 'Source directory no longer exists')
        checkvars = {}
        checkvars['S'] = '${UNPACKDIR}/${BPN}-%s' % recipever
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
        kernel_provider = self.td['PREFERRED_PROVIDER_virtual/kernel']

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
        runCmd('diff %s %s' % (tmpconfig, configfile))

        #Step 4.3
        #NOTE: virtual/kernel is mapped to kernel_provider
        runCmd('devtool build %s' % kernel_provider)
        kernelfile = os.path.join(get_bb_var('KBUILD_OUTPUT', kernel_provider), 'vmlinux')
        self.assertExists(kernelfile, 'Kernel was not build correctly')

        #Modify the kernel source
        modfile = os.path.join(tempdir, 'init/version.c')
        # Moved to uts.h in 6.1 onwards
        modfile2 = os.path.join(tempdir, 'include/linux/uts.h')
        runCmd("sed -i 's/Linux/LiNuX/g' %s %s" % (modfile, modfile2))

        #Modify the configuration
        codeconfigfile = os.path.join(tempdir, '.config.new')
        modconfopt = "CONFIG_SG_POOL=n"
        runCmd("sed -i 's/CONFIG_SG_POOL=y/%s/' %s" % (modconfopt, codeconfigfile))

        #Build again kernel with devtool
        runCmd('devtool build %s' % kernel_provider)

        #Step 4.4
        runCmd("grep '%s' %s" % ('LiNuX', kernelfile))

        #Step 4.5
        runCmd("grep %s %s" % (modconfopt, codeconfigfile))



class RunCmdBackground:
    """Context manager running a command in the background

    This mirrors what VS Code itself does with a task's "isBackground" +
    "problemMatcher": ["background"]["endsPattern"] (see the generated
    tasks.json and how _verify_launch_config() reads
    prelaunch_task["problemMatcher"][0]["background"]["endsPattern"]): VS
    Code also watches the task's output incrementally and considers the
    background task "ready" as soon as a line matches endsPattern, rather
    than waiting for the task to exit or polling on an interval.
    """
    def __init__(self, command, output_log=None, **options):
        self.command = command
        self.output_log = output_log
        self.options = options
        self.process = None
        self._reader_thread = None
        self._cond = threading.Condition()
        self._chunks = []

    def __enter__(self):
        popen_options = dict(self.options)
        popen_options.setdefault("stdout", subprocess.PIPE)
        popen_options.setdefault("stderr", subprocess.STDOUT)
        popen_options.setdefault("shell", isinstance(self.command, str))
        self.process = subprocess.Popen(self.command, **popen_options)
        self._reader_thread = threading.Thread(target=self._read_output, daemon=True)
        self._reader_thread.start()
        return self

    def _read_output(self):
        for line in self.process.stdout:
            text = line.decode("utf-8", errors="replace")
            if self.output_log:
                self.output_log.info(text.rstrip())
            with self._cond:
                self._chunks.append(text)
                self._cond.notify_all()
        # Wake up a waiter still blocked once stdout closes, in case the
        # process exited without ever producing the awaited pattern.
        with self._cond:
            self._cond.notify_all()

    def output(self):
        with self._cond:
            return "".join(self._chunks)

    def wait_for_output(self, pattern, timeout):
        """Block until pattern appears in the output, the process exits, or timeout elapses."""
        pattern = re.compile(pattern, re.MULTILINE)
        deadline = time.monotonic() + timeout
        with self._cond:
            while True:
                if pattern.search("".join(self._chunks)):
                    return True
                if self.process.poll() is not None:
                    return False
                remaining = deadline - time.monotonic()
                if remaining <= 0:
                    return False
                self._cond.wait(remaining)

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.process.poll() is None:
            self.process.terminate()
            try:
                self.process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                self.process.kill()
                self.process.wait()
        self._reader_thread.join(timeout=5)


class DevtoolIdeSdkTests(DevtoolBase):

    MAGIC_STRING_ORIG = "Magic: 123456789"

    def setUp(self):
        super().setUp()
        self._cmd_logger = None
        if self.logger.isEnabledFor(logging.DEBUG):
            self._cmd_logger = self.logger

    def _write_bb_config(self):
        """Helper to write the bitbake local.conf file.

        Image debug settings (IMAGE_GEN_DEBUGFS, IMAGE_CLASSES += image-combined-dbg,
        IMAGE_INSTALL for gdbserver/lldb-server and the recipe under test) are written
        automatically by devtool ide-sdk to the image workspace bbappend.
        Only settings not managed by ide_sdk are configured here.
        """
        conf_lines = [
            'DISTRO_FEATURES:append = " ptest"',
            # Static UIDs/GIDs are required so that files installed via
            # "install -o ${BPN}" in do_install embed the same UID that gets
            # assigned in the final image. Without this, each recipe's isolated
            # sysroot allocates UIDs independently (both start at the first free
            # system UID), so files end up with colliding UIDs in the image.
            # devtool deploy-target is a raw file copy and does not run
            # pkg_postinst, so ownership must be correct already in ${D}.
            'USERADDEXTENSION = "useradd-staticids"',
            'USERADD_UID_TABLES += "files/static-passwd"',
            'USERADD_GID_TABLES += "files/static-group"',
        ]
        self.write_config("\n".join(conf_lines))

    def _check_workspace(self):
        """Check if a workspace directory is available and setup the cleanup"""
        self.assertTrue(not os.path.exists(self.workspacedir),
                        'This test cannot be run with a workspace directory under the build directory')
        self.track_for_cleanup(self.workspacedir)
        self.add_command_to_tearDown('bitbake-layers remove-layer */workspace')

    def _workspace_scripts_dir(self, recipe_name):
        return os.path.realpath(os.path.join(self.builddir, 'workspace', 'ide-sdk', recipe_name, 'scripts'))

    def _sources_scripts_dir(self, src_dir):
        return os.path.realpath(os.path.join(src_dir, 'oe-scripts'))

    def _sources_workdir_dir(self, src_dir):
        return os.path.realpath(os.path.join(src_dir, 'oe-workdir'))

    def _workspace_gdbinit_dir(self, recipe_name):
        return os.path.realpath(os.path.join(self.builddir, 'workspace', 'ide-sdk', recipe_name, 'scripts', 'gdbinit'))

    def _sources_gdbinit_dir(self, src_dir):
        return os.path.realpath(os.path.join(src_dir, 'oe-gdbinit'))

    def _devtool_ide_sdk_recipe(self, recipe_name, build_file, testimage):
        """Setup a recipe for working with devtool ide-sdk

        Basically devtool modify -x followed by some tests
        """
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.add_command_to_tearDown('bitbake -c clean %s' % recipe_name)

        result = runCmd('devtool modify %s -x %s --debug-build' % (recipe_name, tempdir), output_log=self._cmd_logger)
        self.assertExists(os.path.join(tempdir, build_file),
                          'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf',
                                       'layer.conf'), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir,
                            'appends', recipe_name + '.bbappend'))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)

        # Test devtool status
        result = runCmd('devtool status', output_log=self._cmd_logger)
        self.assertIn(recipe_name, result.output)
        self.assertIn(tempdir, result.output)
        self._check_src_repo(tempdir)

        # Usually devtool ide-sdk would initiate the build of the SDK.
        # But there is a circular dependency with starting Qemu and passing the IP of runqemu to devtool ide-sdk.
        if testimage:
            bitbake("%s qemu-native qemu-helper-native" % testimage)
            deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
            self.add_command_to_tearDown('bitbake -c clean %s' % testimage)
            self.add_command_to_tearDown(
                'rm -f %s/%s*' % (deploy_dir_image, testimage))

        return tempdir

    def _get_recipe_ids(self, recipe_name):
        """IDs needed to write recipe specific config entries into IDE config files"""
        package_arch = get_bb_var('PACKAGE_ARCH', recipe_name)
        recipe_id = recipe_name + "-" + package_arch
        recipe_id_pretty = recipe_name + ": " + package_arch
        return (recipe_id, recipe_id_pretty)

    def _verify_install_script_code(self, tempdir, recipe_name, package_args=None):
        """Verify the scripts referred by the tasks.json file are fine.

        This function does not depend on Qemu. Therefore it verifies the scripts
        exists and the install step works as expected. But it does not try to
        deploy to Qemu.
        """
        recipe_id, recipe_id_pretty = self._get_recipe_ids(recipe_name)
        with open(os.path.join(tempdir, '.vscode', 'tasks.json')) as tasks_j:
            tasks_d = json.load(tasks_j)
        tasks = tasks_d["tasks"]
        task_install = next(
            (task for task in tasks if task["label"] == "install && deploy-target %s" % recipe_id_pretty), None)
        self.assertIsNot(task_install, None)
        for package_arg in package_args or []:
            self.assertIn("--package", task_install["args"])
            self.assertIn(package_arg, task_install["args"])
        # execute only the bb_run_do_install script since the deploy would require e.g. Qemu running.
        i_and_d_script = "install_and_deploy_" + recipe_id
        i_and_d_script_path = os.path.join(
            self._workspace_scripts_dir(recipe_name), i_and_d_script)
        self.assertExists(i_and_d_script_path)
        install_script = 'bb_run_do_install_' + recipe_id
        install_script_path = os.path.join(
            self._workspace_scripts_dir(recipe_name), install_script)
        self.assertExists(install_script_path)
        runCmd(install_script_path, cwd=tempdir, output_log=self._cmd_logger)

        deploy_script_path = os.path.join(
            self._workspace_scripts_dir(recipe_name), 'deploy_target_' + recipe_id)
        with open(deploy_script_path) as deploy_script:
            deploy_script_content = deploy_script.read()
        self.assertIn('packages_files = ', deploy_script_content)
        self.assertNotIn("'package':", deploy_script_content)

    def _devtool_ide_sdk_qemu(self, tempdir, qemu, recipe_name, example_exe, compile_cmd):
        """Verify deployment, execution and remote debugging in Qemu system work for one recipe.

        This function checks the entire SDK workflow twice, before and after a code
        change:
        - Deploying the example and its ptests to Qemu and running them.
        - Remote debugging with gdb-cross, hitting breakpoints in the executable
          (by file:line), the library (by symbol and by file:line) and a
          header-only function (by file:line), see _gdb_cross_debugging_multi().

        Between the two passes, it changes the magic string printed by the example
        and inserts LINE_SHIFT extra lines right before the statements the file:line
        breakpoints target, then recompiles and redeploys. This proves the second
        pass's breakpoints resolve against the freshly rebuilt debug info (now at
        shifted line numbers), rather than a stale/cached line-to-address mapping
        left over from the first pass.

        The library's own cpp-example-lib.cpp is shifted too, and a breakpoint is
        set there by file:line (in addition to the existing symbol breakpoint on
        print_json()). Only install_deploy_cmd (do_install + deploy-target) runs
        between the two passes, not a full image rebuild, so rootfs/rootfs-dbg
        keep whatever debug symbols the initial bootstrap image build produced.
        A file:line breakpoint at a line that only exists after the shift can
        therefore only resolve correctly if solib-search-path prefers the
        library's freshly rebuilt debug info in the image folder (D) over a
        stale rootfs-dbg copy.
        """
        recipe_id, _ = self._get_recipe_ids(recipe_name)
        i_and_d_script = "install_and_deploy_" + recipe_id
        install_deploy_cmd = os.path.join(
            self._workspace_scripts_dir(recipe_name), i_and_d_script)
        self.assertExists(install_deploy_cmd,
                          '%s script not found' % install_deploy_cmd)
        runCmd(install_deploy_cmd, output_log=self._cmd_logger)

        MAGIC_STRING_NEW = "Magic: 987654321"
        ptest_cmd = "ptest-runner " + recipe_name

        # validate that SSH is working
        status, _ = qemu.run("uname")
        self.assertEqual(
            status, 0, msg="Failed to connect to the SSH server on Qemu")

        # Verify the unmodified example prints the magic string
        status, output = qemu.run(example_exe)
        self.assertEqual(status, 0, msg="%s failed: %s" %
                         (example_exe, output))
        self.assertIn(DevtoolIdeSdkTests.MAGIC_STRING_ORIG, output)

        # Verify the unmodified ptests work
        status, output = qemu.run(ptest_cmd)
        self.assertEqual(status, 0, msg="%s failed: %s" % (ptest_cmd, output))
        self.assertIn("PASS: cpp-example-lib", output)

        # Verify remote debugging works
        self._gdb_cross_debugging_multi(
            qemu, recipe_name, example_exe, DevtoolIdeSdkTests.MAGIC_STRING_ORIG)

        # Replace the Magic String in the code, compile and deploy to Qemu.
        # Also insert LINE_SHIFT extra lines right before the statements the
        # gdb file:line breakpoints target in the executable (cpp-example.cpp),
        # the library (cpp-example-lib.cpp) and the header (cpp-example-lib.hpp),
        # so those breakpoints land on different line numbers after the
        # recompile/redeploy below. This proves the breakpoints are resolved
        # against the freshly rebuilt debug info, rather than happening to
        # still work against a stale, cached line-to-address mapping from the
        # previous build.
        LINE_SHIFT = 3
        extra_lines = "".join(
            "    // extra line %d inserted by the test to shift subsequent line numbers\n" % i
            for i in range(LINE_SHIFT))

        cpp_example_lib_hpp = os.path.join(tempdir, 'cpp-example-lib.hpp')
        with open(cpp_example_lib_hpp, 'r') as file:
            cpp_code = file.read()
            cpp_code = cpp_code.replace(DevtoolIdeSdkTests.MAGIC_STRING_ORIG, MAGIC_STRING_NEW)
            cpp_code = cpp_code.replace(
                "    static int scale_number(int n)",
                extra_lines + "    static int scale_number(int n)")
        with open(cpp_example_lib_hpp, 'w') as file:
            file.write(cpp_code)

        cpp_example_cpp = os.path.join(tempdir, 'cpp-example.cpp')
        with open(cpp_example_cpp, 'r') as file:
            cpp_code = file.read()
            cpp_code = cpp_code.replace(
                "    volatile int n1 = 1, n2 = 2, n3 = 3;",
                extra_lines + "    volatile int n1 = 1, n2 = 2, n3 = 3;")
        with open(cpp_example_cpp, 'w') as file:
            file.write(cpp_code)

        # Shift a line inside the library's own .cpp file (not the header, not
        # the executable). This is the only file:line breakpoint target that
        # actually resolves through solib-search-path for the rebuilt shared
        # library, so it is the one that would fail to resolve (or resolve to
        # the wrong/stale line) if solib-search-path preferred a stale
        # rootfs-dbg copy of the library's debug info over the freshly
        # rebuilt one in the image folder (D).
        cpp_example_lib_cpp = os.path.join(tempdir, 'cpp-example-lib.cpp')
        with open(cpp_example_lib_cpp, 'r') as file:
            cpp_code = file.read()
            cpp_code = cpp_code.replace(
                "    std::cout << json_object_to_json_string_ext(jobj, flag) << std::endl;",
                extra_lines + "    std::cout << json_object_to_json_string_ext(jobj, flag) << std::endl;")
        with open(cpp_example_lib_cpp, 'w') as file:
            file.write(cpp_code)

        runCmd(compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        runCmd(install_deploy_cmd, cwd=tempdir, output_log=self._cmd_logger)

        # Verify the modified example prints the modified magic string
        status, output = qemu.run(example_exe)
        self.assertEqual(status, 0, msg="%s failed: %s" %
                         (example_exe, output))
        self.assertNotIn(DevtoolIdeSdkTests.MAGIC_STRING_ORIG, output)
        self.assertIn(MAGIC_STRING_NEW, output)

        # Verify the modified example ptests work
        status, output = qemu.run(ptest_cmd)
        self.assertEqual(status, 0, msg="%s failed: %s" % (ptest_cmd, output))
        self.assertIn("PASS: cpp-example-lib", output)

        # Verify remote debugging works with the modified magic string, with
        # the file:line breakpoints shifted by LINE_SHIFT lines compared to
        # the first _gdb_cross_debugging_multi call above.
        self._gdb_cross_debugging_multi(
            qemu, recipe_name, example_exe, MAGIC_STRING_NEW,
            exe_break_line=136 + LINE_SHIFT, exe_list_line=129 + LINE_SHIFT,
            hpp_break_line=24 + LINE_SHIFT, lib_break_line=31 + LINE_SHIFT)

    def _verify_cmake_preset(self, tempdir):
        """Verify the generated cmake preset works as expected

        Check if compiling works
        Check if unit tests can be executed in qemu (not qemu-system)
        """
        with open(os.path.join(tempdir, 'CMakeUserPresets.json')) as cmake_preset_j:
            cmake_preset_d = json.load(cmake_preset_j)
        config_presets = cmake_preset_d["configurePresets"]
        self.assertEqual(len(config_presets), 1)
        cmake_exe = config_presets[0]["cmakeExecutable"]
        preset_name = config_presets[0]["name"]
        compile_cmd = '%s --build --preset %s' % (cmake_exe, preset_name)

        # Verify the wrapper for cmake native is available
        self.assertExists(cmake_exe)

        # Verify the cmake preset generated by devtool ide-sdk is available
        result = runCmd('%s --list-presets' % cmake_exe, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn(preset_name, result.output)

        # Verify cmake re-uses the o files compiled by bitbake
        result = runCmd(compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("ninja: no work to do.", result.output)

        # Verify the unit tests work (in Qemu user mode)
        result = runCmd('%s --target test' % compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("100% tests passed", result.output)

        # Verify re-building and testing works again
        result = runCmd('%s --target clean' % compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("Cleaning", result.output)
        result = runCmd(compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("Building", result.output)
        self.assertIn("Linking", result.output)
        result = runCmd('%s --target test' % compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("Running tests...", result.output)
        self.assertIn("100% tests passed", result.output)

        return compile_cmd

    def _verify_meson_build(self, tempdir, recipe_name):
        """Verify meson works as expected

        Check if compiling works
        Check if unit tests can be executed in qemu (not qemu-system)
        """
        meson_exe = os.path.join(self._workspace_scripts_dir(recipe_name), "meson")
        self.assertExists(meson_exe)
        build_dir = os.path.join(self._sources_workdir_dir(tempdir), recipe_name + "-1.0")
        compile_cmd = '%s compile -C %s' % (meson_exe, build_dir)

        # Verify meson re-uses the o files compiled by bitbake
        result = runCmd(compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("ninja: no work to do.", result.output)

        # Verify the unit tests work (in Qemu user mode)
        result = runCmd('%s test -C %s' % (meson_exe, build_dir),
                        cwd=tempdir, output_log=self._cmd_logger)
        self.assertEqual(result.status, 0)
        self.assertRegex(result.output, r"Fail:\s+0")

        # Verify re-building and testing works again
        result = runCmd('%s compile -C %s --clean' % (meson_exe, build_dir),
                        cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("Cleaning...", result.output)
        result = runCmd(compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        self.assertIn("Linking target", result.output)
        result = runCmd('%s test -C %s' % (meson_exe, build_dir),
                        cwd=tempdir, output_log=self._cmd_logger)
        self.assertEqual(result.status, 0)
        self.assertRegex(result.output, r"Fail:\s+0")

        return compile_cmd

    def _verify_service_running(self, qemu, service_name):
        """Helper to verify a service is running in Qemu"""
        # Use anchored regex (^name$) instead of pgrep -x because the target
        # may have busybox pgrep which does not support the -x flag.
        status, output = qemu.run("pgrep '^%s$'" % service_name)
        self.assertEqual(status, 0, msg="%s service not running: %s" %
                         (service_name, output))
        self.assertTrue(output.strip().isdigit(),
                        f"pgrep output should be a PID integer, got: {output.strip()}")

    def _verify_conf_file(self, qemu, conf_file, owner, group):
        """Helper to verify a configuration file is owned by the proper user and group"""
        stat_cmd = "stat -c '%%U %%G' %s" % conf_file
        status, output = qemu.run(stat_cmd)
        self.assertEqual(status, 0, msg="Failed to stat %s: %s" % (conf_file, output))
        actual_owner, actual_group = output.strip().split()
        self.assertEqual(actual_owner, owner,
                         msg="%s not owned by user %s: got %s" % (conf_file, owner, actual_owner))
        self.assertEqual(actual_group, group,
                         msg="%s not owned by group %s: got %s" % (conf_file, group, actual_group))

    def _run_vscode_task_with_dependencies(self, tempdir, task_label, run_main_task=True):
        """Run a VS Code shell task and its dependsOn chain.

        The helper mirrors VS Code task dependency ordering for selftests, so
        preLaunchTask prerequisites (e.g. compile and do_install/deploy tasks)
        run before the debug server task.
        """
        tasks_path = os.path.join(tempdir, '.vscode', 'tasks.json')
        self.assertExists(tasks_path, 'tasks.json not found at %s' % tasks_path)

        with open(tasks_path) as tasks_j:
            tasks_d = json.load(tasks_j)

        task_by_label = {
            task.get('label'): task
            for task in tasks_d.get('tasks', [])
            if task.get('label')
        }
        self.assertIn(task_label, task_by_label,
                      "Task '%s' not found in tasks.json" % task_label)

        ran = set()

        def run_task(label, execute_task):
            if label in ran:
                return

            task = task_by_label[label]
            deps = task.get('dependsOn', [])
            if isinstance(deps, str):
                deps = [deps]

            for dep in deps:
                self.assertIn(dep, task_by_label,
                              "DependsOn task '%s' not found in tasks.json" % dep)
                run_task(dep, True)

            if execute_task:
                command = task.get('command')
                if command:
                    args = task.get('args', [])
                    if not isinstance(args, list):
                        args = [args]
                    task_cmd = [command] + [str(arg) for arg in args]

                    task_cwd = task.get('options', {}).get('cwd', tempdir)
                    if isinstance(task_cwd, str):
                        task_cwd = task_cwd.replace('${workspaceFolder}', tempdir)
                    runCmd(task_cmd, cwd=task_cwd, output_log=self._cmd_logger)

            ran.add(label)

        run_task(task_label, run_main_task)


class DevtoolIdeSdkGccTests(DevtoolIdeSdkTests):

    def _gdb_cross(self):
        """Verify gdb-cross is provided by devtool ide-sdk"""
        target_arch = self.td["TARGET_ARCH"]
        target_sys = self.td["TARGET_SYS"]
        gdb_recipe = "gdb-cross-" + target_arch
        gdb_binary = target_sys + "-gdb"

        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", gdb_recipe)
        r = runCmd("%s --version" % gdb_binary,
                   native_sysroot=native_sysroot, target_sys=target_sys, output_log=self._cmd_logger)
        self.assertEqual(r.status, 0)
        self.assertIn("GNU gdb", r.output)

    def _gdb_debug_cpp_example(self, magic_string, gdb_start_cmd="run",
                              exe_break_line=136, exe_list_line=129, hpp_break_line=24,
                              lib_break_line=31):
        """Get a series of gdb commands to debug the cpp-example-lib example"""
        gdb_batch_cmd = " -ex 'break main' -ex '%s'" % gdb_start_cmd
        gdb_batch_cmd += " -ex 'break CppExample::print_json()' -ex 'continue'"
        gdb_batch_cmd += " -ex 'print CppExample::test_string.compare(\"cpp-example-lib %s\")'" % magic_string
        gdb_batch_cmd += " -ex 'print CppExample::test_string.compare(\"cpp-example-lib %saaa\")'" % magic_string
        gdb_batch_cmd += " -ex 'list cpp-example-lib.hpp:16,16'"

        # Break inside the library's own .cpp file by file:line (not by
        # symbol), while still inside the print_json() call reached above.
        # Unlike the symbol breakpoint above, resolving a file:line breakpoint
        # requires the line-to-address mapping from the library's debug info
        # that matches the currently deployed build. lib_break_line shifts
        # after the test edits and recompiles cpp-example-lib.cpp, to prove
        # this breakpoint resolves via the freshly rebuilt library debug info
        # found through solib-search-path, rather than a stale rootfs-dbg
        # copy left over from the last full image build.
        gdb_batch_cmd += " -ex 'break cpp-example-lib.cpp:%d'" % lib_break_line
        gdb_batch_cmd += " -ex 'continue'"
        gdb_batch_cmd += " -ex 'list cpp-example-lib.cpp:%d,%d'" % (lib_break_line, lib_break_line)

        # check if resolving std::vector works with python scripts
        gdb_batch_cmd += " -ex 'list cpp-example.cpp:%d,%d'" % (exe_list_line, exe_list_line)
        # Break on exe_break_line (the scale_number call) so the vector on
        # exe_list_line is both constructed and referenced; the compiler cannot
        # eliminate the vector because its elements are passed as the argument.
        # These line numbers shift after the test inserts extra lines and
        # recompiles, proving the breakpoint resolves via the freshly rebuilt
        # debug info rather than a stale, cached line-to-address mapping.
        gdb_batch_cmd += " -ex 'break cpp-example.cpp:%d'" % exe_break_line
        gdb_batch_cmd += " -ex 'continue'"
        gdb_batch_cmd += " -ex 'print numbers'"

        # Break on scale_number(), an inline function defined directly in the
        # header (cpp-example-lib.hpp), to exercise breakpoint resolution for
        # header-only debug info, separately from the executable's own
        # cpp-example.cpp (file:line breakpoint above) and the library's
        # cpp-example-lib.cpp (CppExample::print_json() breakpoint above).
        # hpp_break_line shifts for the same reason as exe_break_line above.
        gdb_batch_cmd += " -ex 'break cpp-example-lib.hpp:%d'" % hpp_break_line
        gdb_batch_cmd += " -ex 'continue'"
        gdb_batch_cmd += " -ex 'print n'"
        gdb_batch_cmd += " -ex 'continue'"
        return gdb_batch_cmd

    def _gdb_debug_cpp_example_check(self, gdb_output, magic_string, exe_list_line=129, lib_break_line=31):
        self.assertIn("Breakpoint 1, main", gdb_output)
        self.assertIn("$1 = 0", gdb_output)  # test.string.compare equal
        self.assertIn("$2 = -3", gdb_output)  # test.string.compare longer
        self.assertIn(
            'inline static const std::string test_string = "cpp-example-lib %s";' % magic_string, gdb_output)

        # check that the file:line breakpoint set directly in the library's
        # own .cpp file actually resolved and was hit at the expected
        # (possibly shifted) line, i.e. against the freshly rebuilt library
        # debug info rather than a stale rootfs-dbg copy
        self.assertIn("cpp-example-lib.cpp:%d" % lib_break_line, gdb_output)
        self.assertRegex(
            gdb_output, r"%d\s+std::cout << json_object_to_json_string_ext\(jobj, flag\) << std::endl;" % lib_break_line)

        # check if resolving std::vector works with python scripts
        self.assertRegex(
            gdb_output, r"%d\s+std::vector<int> numbers = \{n1, n2, n3\};" % exe_list_line)
        self.assertIn("$3 = std::vector of length 3, capacity 3 = {1, 2, 3}", gdb_output)

        # check that a breakpoint in an inline function defined directly in
        # the header (cpp-example-lib.hpp) is resolved and hit
        self.assertIn("scale_number", gdb_output)
        self.assertIn("$4 = 6", gdb_output)  # n == 6, the call argument

        self.assertIn("exited normally", gdb_output)

    def _gdb_cross_debugging_multi(self, qemu, recipe_name, example_exe, magic_string,
                                   exe_break_line=136, exe_list_line=129, hpp_break_line=24,
                                   lib_break_line=31):
        """Verify gdb-cross is working

        Test remote debugging with breakpoints in the executable
        (cpp-example.cpp), the library (cpp-example-lib.cpp, by symbol and by
        file:line) and a header defined directly in cpp-example-lib.hpp.
        exe_break_line, exe_list_line, hpp_break_line and lib_break_line are
        parameterized because the caller shifts them to different line
        numbers after recompiling, to prove the breakpoints resolve via the
        freshly rebuilt debug info.
        """
        gdbserver_script = os.path.join(self._workspace_scripts_dir(
            recipe_name), 'gdbserver_1234_usr-bin-' + example_exe + '_multi')
        gdb_script = os.path.join(self._workspace_scripts_dir(
            recipe_name), 'gdb_1234_usr-bin-' + example_exe)

        # Start a gdbserver
        r = runCmd(gdbserver_script, output_log=self._cmd_logger)
        self.assertEqual(r.status, 0)

        # The start script waits for gdbserver's port before returning, but on
        # an NFS-root target (--nfs=rootfs-dbg) the pid file it wrote has been
        # observed to become readable over the next SSH connection only a
        # moment later, so poll for it.
        pid_file = '/tmp/gdbserver_1234_usr-bin-%s_multi/gdbserver.pid' % example_exe
        status, output = qemu.run('cat %s' % pid_file)
        for _ in range(10):
            if status == 0:
                break
            time.sleep(1)
            status, output = qemu.run('cat %s' % pid_file)
        self.assertEqual(status, 0)
        gdbserver_pid = output.strip()
        self.assertRegex(gdbserver_pid, r'^\d+$')

        # Check the pid file identifies the expected gdbserver process
        status, output = qemu.run('cat /proc/%s/cmdline' % gdbserver_pid)
        self.assertEqual(status, 0)
        self.assertIn("gdbserver", output)
        self.assertIn("--multi", output)
        self.assertIn("1234", output)

        # Test remote debugging works
        gdb_batch_cmd = " --batch " + self._gdb_debug_cpp_example(
            magic_string, exe_break_line=exe_break_line, exe_list_line=exe_list_line,
            hpp_break_line=hpp_break_line, lib_break_line=lib_break_line)
        r = runCmd(gdb_script + gdb_batch_cmd, output_log=self._cmd_logger)
        self.logger.debug("%s %s returned: %s", gdb_script,
                          gdb_batch_cmd, r.output)
        self.assertEqual(r.status, 0)
        self._gdb_debug_cpp_example_check(
            r.output, magic_string=magic_string, exe_list_line=exe_list_line, lib_break_line=lib_break_line)

        # Stop the gdbserver
        r = runCmd(gdbserver_script + ' stop', output_log=self._cmd_logger)
        self.assertEqual(r.status, 0)

        # The stop script waits for its recorded PID before it succeeds.
        status, _ = qemu.run('test ! -d /proc/%s && test ! -e %s' % (
            gdbserver_pid, pid_file))
        self.assertEqual(status, 0)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_qemu(self):
        """Start qemu-system and run tests for multiple recipes. ide=none is used."""
        recipe_names = ["cmake-example", "meson-example"]
        package_filters = [
            # Exercise multi-recipe package scoping via repeated --package:
            # RECIPE:,-ptest expands to RECIPE and RECIPE-ptest.
            "cmake-example:,-ptest",
            "meson-example:,-ptest",
        ]
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()
        self._check_runqemu_prerequisites()

        # Verify deployment to Qemu (system mode) works
        tempdir_cmake = self._devtool_ide_sdk_recipe("cmake-example", "CMakeLists.txt", None)
        tempdir_meson = self._devtool_ide_sdk_recipe("meson-example", "meson.build", testimage)
        package_opts = ' '.join('--package %s' % p for p in package_filters)
        runCmd('devtool ide-sdk cmake-example meson-example %s -c --ide=none %s' % (testimage, package_opts),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # cmake-example recipe
            recipe_name = "cmake-example"
            example_exe = "cmake-example"
            example_user_group = "cmake-example"
            conf_file = "/etc/cmake-example.conf"

            # Verify the cmake-example service is running on the target
            self._verify_service_running(qemu, example_exe)
            # Verify /etc/cmake-example.conf is owned by the cmake-example user
            self._verify_conf_file(qemu, conf_file, example_user_group, example_user_group)

            # Re-run ide-sdk with only the image name. Modified recipes must
            # be discovered from workspace appends; the image bbappend itself
            # must not be mistaken for a modified recipe.
            bitbake_sdk_cmd = 'devtool ide-sdk %s -t root@%s -c --skip-bitbake --ide=none %s' % (
                testimage, qemu.ip, package_opts)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            self._gdb_cross()
            compile_cmd = self._verify_cmake_preset(tempdir_cmake)
            self._devtool_ide_sdk_qemu(tempdir_cmake, qemu, recipe_name, example_exe, compile_cmd)

            # Verify the oe-scripts sym-link is valid
            self.assertEqual(self._workspace_scripts_dir(
                recipe_name), self._sources_scripts_dir(tempdir_cmake))

            # Verify /etc/cmake-example.conf is still owned by the cmake-example user
            # after the install and deploy scripts updated the file
            self._verify_conf_file(qemu, conf_file, example_exe, example_exe)

            # meson-example recipe
            recipe_name = "meson-example"
            example_exe = "mesonex"
            example_user_group = "meson-example"
            conf_file = "/etc/meson-example.conf"

            # Verify the meson-example service is running on the target
            self._verify_service_running(qemu, example_exe)
            # Verify /etc/meson-example.conf is owned by the meson-example user
            self._verify_conf_file(qemu, conf_file, example_user_group, example_user_group)

            # Re-run ide-sdk with the actual QEMU IP for this recipe
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=none %s' % (
                recipe_name, testimage, qemu.ip, package_opts)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            self._gdb_cross()
            compile_cmd = self._verify_meson_build(tempdir_meson, recipe_name)
            self._devtool_ide_sdk_qemu(tempdir_meson, qemu, recipe_name, example_exe, compile_cmd)

            # Verify the oe-scripts sym-link is valid
            self.assertEqual(self._workspace_scripts_dir(
                recipe_name), self._sources_scripts_dir(tempdir_meson))

            # Verify /etc/meson-example.conf is still owned by the meson-example user
            # after the install and deploy scripts updated the file
            self._verify_conf_file(qemu, conf_file, example_user_group, example_user_group)

    def _verify_launch_json(self, tempdir):
        """Verify the launch.json file created is valid and contains proper debug configurations"""
        launch_json_path = os.path.join(tempdir, '.vscode', 'launch.json')
        tasks_json_path = os.path.join(tempdir, '.vscode', 'tasks.json')
        self.assertTrue(os.path.exists(launch_json_path), "launch.json file should exist")
        self.assertTrue(os.path.exists(tasks_json_path), "tasks.json file should exist")

        with open(launch_json_path) as launch_j:
            launch_d = json.load(launch_j)
        with open(tasks_json_path) as tasks_j:
            tasks_d = json.load(tasks_j)
        task_labels = {
            task.get("label")
            for task in tasks_d.get("tasks", [])
            if task.get("label")
        }

        self.assertIn("configurations", launch_d)
        configurations = launch_d["configurations"]
        self.assertEqual(len(configurations), 3, "Should have exactly three debug configurations")

        # Track configurations found
        once_configs = []
        attach_configs = []
        server_addresses = []

        for config in configurations:
            # Verify required fields exist
            required_fields = ["name", "type", "request", "program", "MIMode",
                             "miDebuggerPath", "miDebuggerServerAddress"]
            for field in required_fields:
                self.assertIn(field, config, f"Configuration '{config.get('name', 'Unknown')}' missing required field: {field}")

            # Verify common configuration values
            self.assertEqual(config["type"], "cppdbg", f"Configuration '{config['name']}' should use cppdbg type")
            self.assertEqual(config["MIMode"], "gdb", f"Configuration '{config['name']}' should use gdb MIMode")

            if config["request"] == "launch":
                self.assertEqual(config["cwd"], "${workspaceFolder}", f"Configuration '{config['name']}' should use workspaceFolder as cwd")
                self.assertEqual(config.get("externalConsole", False), False, f"Configuration '{config['name']}' should not use external console")
                self.assertEqual(config.get("stopAtEntry", True), True, f"Configuration '{config['name']}' should stop at entry")
            elif config["request"] == "attach":
                # Attaching to a process running on the target requires the
                # extended-remote protocol. Stopping the session then detaches
                # from the process instead of killing it.
                self.assertTrue(config.get("useExtendedRemote"), f"Configuration '{config['name']}' should use useExtendedRemote")
                self.assertNotIn("cwd", config, f"Configuration '{config['name']}' should not set cwd in attach mode")
                self.assertTrue(config.get("processFilter"), f"Configuration '{config['name']}' should use processFilter")
            else:
                self.fail(f"Configuration '{config['name']}' has unexpected request type: {config['request']}")

            # Verify program path is absolute and exists conceptually
            program = config["program"]
            self.assertTrue(program.startswith("/"), f"Configuration '{config['name']}' program path should be absolute: {program}")
            self.assertIn("/image/usr/bin/", program, f"Configuration '{config['name']}' program should be in image/usr/bin")

            # Verify debugger path
            debugger_path = config["miDebuggerPath"]
            self.assertTrue(debugger_path.endswith("-gdb"), f"Configuration '{config['name']}' debugger should end with -gdb: {debugger_path}")
            self.assertIn("/recipe-sysroot-native/usr/bin/", debugger_path, f"Configuration '{config['name']}' debugger should be in sysroot-native")

            # Verify server address format
            server_addr = config["miDebuggerServerAddress"]
            self.assertRegex(server_addr, r"^\d+\.\d+\.\d+\.\d+:\d+$", f"Configuration '{config['name']}' server address should be IP:PORT format: {server_addr}")
            server_addresses.append(server_addr)

            # Verify additional SO lib search path exists and contains debug paths
            so_paths = config.get("additionalSOLibSearchPath", [])
            self.assertIn("/.debug", so_paths, f"Configuration '{config['name']}' should include debug symbol paths")
            self.assertIn("/rootfs-dbg/", so_paths, f"Configuration '{config['name']}' should include rootfs-dbg paths")
            # The recipe's own D/usr/lib must come first, so GDB's solib-search-path basename fallback
            # prefers freshly rebuilt libraries over a stale rootfs-dbg/rootfs copy from the last full image build.
            first_so_path = so_paths.split(":", 1)[0]
            self.assertIn("/image/", first_so_path, f"Configuration '{config['name']}' first SO lib search path should be the recipe's own image dir: {first_so_path}")
            self.assertNotIn("rootfs-dbg", first_so_path, f"Configuration '{config['name']}' first SO lib search path should not be rootfs-dbg: {first_so_path}")

            # Verify source file mappings
            source_map = config.get("sourceFileMap", {})
            self.assertIsInstance(source_map, dict, f"Configuration '{config['name']}' sourceFileMap should be a dictionary")
            self.assertIn("/usr/src/debug", source_map, f"Configuration '{config['name']}' should map /usr/src/debug")
            self.assertIn("${workspaceFolder}", str(source_map), f"Configuration '{config['name']}' should map to workspaceFolder")

            # Verify setup commands for sysroot
            setup_commands = config.get("setupCommands", [])
            self.assertTrue(len(setup_commands) >= 1, f"Configuration '{config['name']}' should have setup commands")
            sysroot_cmd = setup_commands[0]
            self.assertIn("sysroot", sysroot_cmd.get("text", ""), f"Configuration '{config['name']}' should set sysroot in setup commands")

            # Verify preLaunchTask exists and matches name pattern
            task = config.get("preLaunchTask", "")
            self.assertTrue(task, f"Configuration '{config['name']}' preLaunchTask should not be empty")
            # Task should contain port number and executable name from config name
            config_name = config["name"]
            if "_once" in config_name:
                self.assertIn("_once", task, f"once configuration '{config_name}' should have once preLaunchTask")
            elif "_attach" in config_name:
                self.assertIn("_attach", task, f"attach configuration '{config_name}' should have attach preLaunchTask")
                # Verify postDebugTask exists for attach configurations (kill gdbserver)
                self.assertIn("postDebugTask", config, f"attach configuration '{config_name}' should have postDebugTask")
                self.assertIn(config["postDebugTask"], task_labels,
                              f"attach configuration '{config_name}' postDebugTask should exist in tasks.json")

            # Categorize configurations
            config_name = config["name"]
            if "_once" in config_name:
                once_configs.append(config_name)
            elif "_attach" in config_name:
                attach_configs.append(config_name)

        # Verify we have expected configuration types
        self.assertEqual(len(once_configs), 2, f"Should have two once configuration, found: {once_configs}")
        self.assertEqual(len(attach_configs), 1, f"Should have one attach configuration, found: {attach_configs}")
        self.assertEqual(len(server_addresses), len(set(server_addresses)),
                 "Each debug configuration should use a distinct server address")

    def _verify_launch_json_debugging(self, tempdir, qemu, example_exe):
        """Verify launch.json debugging configs for one target executable.

        Runs attach-mode first against the already-running service process,
        then validates the once configuration. This ordering avoids Build-ID
        mismatch from once/deploy side effects before attach is tested.
        """
        with open(os.path.join(tempdir, '.vscode', 'launch.json')) as launch_j:
            launch_d = json.load(launch_j)
        with open(os.path.join(tempdir, '.vscode', 'tasks.json')) as tasks_j:
            tasks_d = json.load(tasks_j)

        configurations = launch_d["configurations"]
        tasks = tasks_d["tasks"]

        # Test one configuration for remote debugging
        # The config name is derived from the binary's install path
        # (e.g. "usr-bin-<example_exe>"), which is not necessarily the same
        # as the recipe name (e.g. meson-example installs a binary named
        # "mesonex").
        once_config_count = 0
        attach_config_count = 0

        # Run attach first. The attach test targets the already-running
        # service process in the image; running a once configuration first can
        # deploy/update binaries and trigger Build-ID mismatch warnings against
        # that still-running process.
        for config in configurations:
            if f"usr-bin-{example_exe}_attach" in config["name"]:
                attach_config_count += 1
                self._verify_launch_config_attach(tempdir, config, qemu, example_exe)

        for config in configurations:
            if f"usr-bin-{example_exe}_once" in config["name"]:
                once_config_count += 1
                self._verify_launch_config(tempdir, config, tasks, qemu, example_exe,
                                           self._gdb_debug_cpp_example, self._gdb_debug_cpp_example_check)
        self.assertEqual(once_config_count, 1, f"Should have one once configuration, found: {once_config_count}")
        self.assertEqual(attach_config_count, 1, f"Should have one attach configuration, found: {attach_config_count}")

    def _verify_launch_config_attach(self, tempdir, launch_config, qemu, example_exe):
        """Verify attach-mode debugging by exercising debugger control flow.

        The attached service runs an endless loop, and each loop iteration
        executes sleep(). We break on sleep() to prove that attach-mode gdb is
        controlling the already-running process at a point that is expected to
        be reached repeatedly during normal service execution, then detach.
        """
        self.assertEqual(launch_config.get("request"), "attach")

        tasks_path = os.path.join(tempdir, '.vscode', 'tasks.json')
        self.assertExists(tasks_path, 'tasks.json not found at %s' % tasks_path)
        with open(tasks_path) as tasks_j:
            tasks_d = json.load(tasks_j)
        task_by_label = {
            task.get('label'): task
            for task in tasks_d.get('tasks', [])
            if task.get('label')
        }

        debugger_path = launch_config["miDebuggerPath"]
        server_addr = launch_config["miDebuggerServerAddress"]
        prelaunch_task_name = launch_config["preLaunchTask"]
        post_debug_task_name = launch_config.get("postDebugTask")
        program = launch_config["program"]
        additional_so_lib_search_path = launch_config["additionalSOLibSearchPath"]
        source_file_map = launch_config["sourceFileMap"]
        setup_commands = launch_config["setupCommands"]

        self.assertTrue(post_debug_task_name,
                        "attach configuration should define postDebugTask")
        self.assertIn(prelaunch_task_name, task_by_label)
        self.assertIn(post_debug_task_name, task_by_label)

        status, _ = qemu.run('test -x /usr/bin/gdbserver')
        self.assertEqual(status, 0, "gdbserver should be installed on target")
        status, _ = qemu.run('test -x ' + os.path.join('/usr/bin', example_exe))
        self.assertEqual(status, 0, "Example binary should be installed on target")

        # The attach preLaunchTask has no dependsOn (it attaches to an already
        # running service instead of building/deploying), so it can be run
        # directly without a dependency chain runner.
        prelaunch_task = task_by_label[prelaunch_task_name]
        prelaunch_cmd = [prelaunch_task["command"]] + [str(arg)
                                                         for arg in prelaunch_task.get("args", [])]
        # The tasks.json command is shell-quoted and escapes '$' for an
        # intermediate shell, so undo that before passing argv to subprocess.
        if prelaunch_cmd[-1].startswith('"') and prelaunch_cmd[-1].endswith('"'):
            prelaunch_cmd[-1] = prelaunch_cmd[-1][1:-1].replace('\\$', '$')
        runCmd(prelaunch_cmd, output_log=self._cmd_logger)

        # Attach to the service process currently running on the target.
        status, output = qemu.run("pgrep '^%s$'" % example_exe)
        self.assertEqual(status, 0, msg="%s service not running: %s" %
                         (example_exe, output))
        service_pid = output.strip()
        self.assertRegex(service_pid, r'^\d+$')

        gdb_batch_cmd = debugger_path + " --batch"
        for setup_command in setup_commands:
            setup_cmd = setup_command["text"].strip()
            if setup_cmd.startswith("-"):
                continue
            gdb_batch_cmd += ' -ex ' + shlex.quote(setup_cmd)
        for k, v in source_file_map.items():
            gdb_batch_cmd += " -ex 'set substitute-path %s %s'" % (k, v.replace("${workspaceFolder}", tempdir))
        gdb_batch_cmd += " -ex 'set solib-search-path %s'" % additional_so_lib_search_path
        gdb_batch_cmd += " -ex 'file %s'" % program
        gdb_batch_cmd += " -ex 'target extended-remote %s'" % server_addr
        gdb_batch_cmd += " -ex 'attach %s'" % service_pid
        gdb_batch_cmd += " -ex 'set breakpoint pending on'"
        gdb_batch_cmd += " -ex 'tbreak sleep'"
        gdb_batch_cmd += " -ex 'continue'"
        gdb_batch_cmd += " -ex 'detach'"
        self.logger.debug(f"Starting attach gdb session with command: {gdb_batch_cmd}")

        r = runCmd(gdb_batch_cmd, output_log=self._cmd_logger)
        self.assertEqual(r.status, 0)
        self.assertIn("sleep", r.output)

        # Stop the persistent attach server and verify the service survives.
        post_debug_task = task_by_label[post_debug_task_name]
        post_debug_cmd = [post_debug_task["command"]] + [str(arg)
                                                          for arg in post_debug_task.get("args", [])]
        if post_debug_cmd[-1].startswith('"') and post_debug_cmd[-1].endswith('"'):
            post_debug_cmd[-1] = post_debug_cmd[-1][1:-1].replace('\\$', '$')
        runCmd(post_debug_cmd, output_log=self._cmd_logger)

        status, output = qemu.run("pgrep '^%s$'" % example_exe)
        self.assertEqual(status, 0, msg="%s service should still be running after attach stop: %s" %
                         (example_exe, output))

    def _verify_launch_config(self, tempdir, launch_config, tasks, qemu, example_exe, debug_func=None, debug_check_func=None):
        self.assertIsNotNone(launch_config, "Should have at least one launch debug configuration")

        # Extract configuration values for launch.json
        debugger_path = launch_config["miDebuggerPath"]
        server_addr = launch_config["miDebuggerServerAddress"]
        prelaunch_task_name = launch_config["preLaunchTask"]
        program = launch_config["program"]
        additional_so_lib_search_path = launch_config["additionalSOLibSearchPath"]
        source_file_map = launch_config["sourceFileMap"]
        setup_commands = launch_config["setupCommands"]

        # Find the preLaunchTask in tasks.json
        prelaunch_task = next(
            (task for task in tasks if task["label"] == prelaunch_task_name), None)
        self.assertIsNotNone(prelaunch_task, f"PreLaunchTask '{prelaunch_task_name}' not found in tasks.json")

        # Execute preLaunchTask prerequisites exactly like VS Code dependsOn
        # handling, but keep starting the debug server in this method.
        self._run_vscode_task_with_dependencies(
            tempdir, prelaunch_task_name, run_main_task=False)

        # Verify task structure and extract command details
        self.assertEqual(prelaunch_task["type"], "shell", f"Task '{prelaunch_task_name}' should be shell type")
        task_command = prelaunch_task["command"]
        task_args = prelaunch_task["args"]

        # The command should be ssh for remote execution
        self.assertEqual(task_command, "ssh", f"Task '{prelaunch_task_name}' should use ssh command")
        self.assertTrue(len(task_args) >= 2, f"Task '{prelaunch_task_name}' should have at least 2 args (ssh options and remote command)")

        status, _ = qemu.run('test -x /usr/bin/gdbserver')
        self.assertEqual(status, 0, "gdbserver should be installed on target")
        status, _ = qemu.run('test -x ' + os.path.join('/usr/bin', example_exe))
        self.assertEqual(status, 0, "Example binary should be installed on target")

        # Start gdbserver on target using the task command (keep the ssh connection open while debugging)
        ssh_gdbserver_cmd = [task_command] + task_args
        # The tasks.json argument is formatted for an intermediate shell. Strip
        # its quotes and restore dollar expansions before passing it directly to
        # SSH via subprocess.
        if len(ssh_gdbserver_cmd) > 0 and ssh_gdbserver_cmd[-1].startswith('"') and ssh_gdbserver_cmd[-1].endswith('"'):
            ssh_gdbserver_cmd[-1] = ssh_gdbserver_cmd[-1][1:-1].replace('\\$', '$')  # Remove surrounding quotes
        self.logger.debug(f"Starting gdbserver with command: {' '.join(ssh_gdbserver_cmd)}")
        _, server_port = server_addr.split(':')
        with RunCmdBackground(ssh_gdbserver_cmd, output_log=self._cmd_logger) as gdbserver:
            ready_pattern = prelaunch_task["problemMatcher"][0]["background"]["endsPattern"]
            # Must exceed the target side budget (TARGET_START_RETRIES * 0.1s),
            # otherwise this gives up while the target is still waiting and its
            # diagnostics never make it into the failure.
            self.assertTrue(
                gdbserver.wait_for_output(ready_pattern, timeout=60),
                "gdbserver did not report readiness on port %s:\n%s" %
                (server_port, gdbserver.output()))

            if debug_func and debug_check_func:
                # Do a gdb remote session using the once configuration
                gdb_batch_cmd = debugger_path + " --batch"
                for setup_command in setup_commands:
                    # What VSCode does, for tracing add "logging": {"engineLogging": true } to launch.json
                    setup_cmd = setup_command["text"].strip()
                    if setup_cmd.startswith("-"):
                        # Ignore commands starting with '-' as they are VSCode internal commands?
                        continue
                    else:
                        gdb_batch_cmd += ' -ex ' + shlex.quote(setup_cmd)
                for k, v in source_file_map.items():
                    gdb_batch_cmd += " -ex 'set substitute-path %s %s'" % (k, v.replace("${workspaceFolder}", tempdir))
                gdb_batch_cmd += " -ex 'set solib-search-path %s'" % additional_so_lib_search_path
                gdb_batch_cmd += " -ex 'file %s'" % program
                gdb_batch_cmd += " -ex 'target remote %s'" % server_addr
                # Add a basic set of command performing a simple debugging session
                gdb_batch_cmd += debug_func(DevtoolIdeSdkTests.MAGIC_STRING_ORIG, "continue")
                self.logger.debug(f"Starting gdb session with command: {gdb_batch_cmd}")
                r = runCmd(gdb_batch_cmd, output_log=self._cmd_logger)
                self.logger.debug("%s %s returned: %s", debugger_path, gdb_batch_cmd, r.output)
                self.assertEqual(r.status, 0)
                debug_check_func(r.output, DevtoolIdeSdkTests.MAGIC_STRING_ORIG)

    def test_devtool_ide_sdk_code_nfs_debug_rootfs(self):
        """Verify ide-sdk extracts an NFS debug rootfs for VS Code debugging."""
        recipe_name = "cmake-example"
        build_file = "CMakeLists.txt"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()
        tempdir = self._devtool_ide_sdk_recipe(
            recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=code --nfs=rootfs-dbg' %
               (recipe_name, testimage), output_log=self._cmd_logger)

        nfs_rootfs = os.path.join(
            self.workspacedir, 'nfs-exports', testimage, 'rootfs-dbg')
        self.assertExists(nfs_rootfs)
        self.assertExists(nfs_rootfs + '.pseudo_state')
        self.assertExists(os.path.join(nfs_rootfs, 'usr', 'bin', recipe_name))
        runqemu_helper = os.path.join(
            self.workspacedir, 'nfs-exports', testimage,
            'runqemu-rootfs-dbg')
        self.assertExists(runqemu_helper)
        self.assertTrue(os.access(runqemu_helper, os.X_OK))
        with open(runqemu_helper) as helper_file:
            helper = helper_file.read()
        self.assertIn('exec runqemu ', helper)
        self.assertIn(nfs_rootfs, helper)

        bbappend = os.path.join(
            self.workspacedir, 'appends', testimage + '.bbappend')
        with open(bbappend) as append_file:
            self.assertRegex(
                append_file.read(),
                r'IMAGE_FSTYPES_DEBUGFS(?::append)? = " ?tar"')

        with open(os.path.join(tempdir, '.vscode', 'launch.json')) as launch_file:
            launch_configurations = json.load(launch_file)['configurations']
        for configuration in launch_configurations:
            self.assertIn(
                nfs_rootfs, configuration['additionalSOLibSearchPath'])
            self.assertEqual(
                os.path.join(nfs_rootfs, 'usr', 'src', 'debug'),
                configuration['sourceFileMap']['/usr/src/debug'])

    def _test_devtool_ide_sdk_nfs_debug_rootfs_qemu(self, slirp=False):
        """Boot the extracted NFS debug rootfs and exercise remote GDB."""
        recipe_name = "cmake-example"
        build_file = "CMakeLists.txt"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()
        if not slirp:
            self._check_runqemu_prerequisites()

        tempdir = self._devtool_ide_sdk_recipe(
            recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=none --nfs=rootfs-dbg' %
               (recipe_name, testimage),
               output_log=self._cmd_logger)

        nfs_rootfs = os.path.join(
            self.workspacedir, 'nfs-exports', testimage, 'rootfs-dbg')
        runqemu_helper = os.path.join(
            self.workspacedir, 'nfs-exports', testimage,
            'runqemu-rootfs-dbg')
        self.assertExists(nfs_rootfs)
        self.assertExists(runqemu_helper)

        launch_cmd = '%s nographic' % shlex.quote(runqemu_helper)
        runqemuparams = ''
        if slirp:
            launch_cmd += ' slirp'
            # QemuTarget uses this only to select its localhost SSH endpoint;
            # launch_cmd supplies the actual runqemu option.
            runqemuparams = 'slirp'

        with runqemu(testimage, runqemuparams=runqemuparams,
                     launch_cmd=launch_cmd) as qemu:
            status, output = qemu.run(
                "awk '$2 == \"/\" {print $3}' /proc/mounts")
            self.assertEqual(status, 0)
            self.assertEqual(output.strip(), 'nfs')

            # Re-run with the real QEMU target address, instead of 192.168.7.2 IP.
            # --skip-bitbake also skips the rootfs extraction, which would
            # otherwise wipe the directory the target has mounted right now.
            if slirp:
                self.assertIsNotNone(qemu.port, 'No SSH port for the slirp target')
                target_options = '-t root@%s -P %s ' % (qemu.ip, qemu.port)
            else:
                target_options = '-t root@%s -c ' % qemu.ip
            runCmd('devtool ide-sdk %s %s %s--skip-bitbake --ide=none --nfs=rootfs-dbg' %
                   (recipe_name, testimage, target_options),
                   output_log=self._cmd_logger)

            self._gdb_cross()
            compile_cmd = self._verify_cmake_preset(tempdir)
            self._devtool_ide_sdk_qemu(
                tempdir, qemu, recipe_name, recipe_name, compile_cmd)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_nfs_qemu(self):
        """Verify remote GDB debugging through an NFS-root QEMU target."""
        self._test_devtool_ide_sdk_nfs_debug_rootfs_qemu()

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_nfs_qemu_slirp(self):
        """Verify remote GDB debugging through an NFS-root slirp target."""
        self._test_devtool_ide_sdk_nfs_debug_rootfs_qemu(slirp=True)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_code_cmake(self):
        """Verify a cmake recipe works with ide=code mode"""
        recipe_name = "cmake-example"
        example_exe = "cmake-example"
        package_opt = '--package %s:,-ptest' % recipe_name
        build_file = "CMakeLists.txt"
        testimage = "oe-selftest-image"
        build_file = "CMakeLists.txt"

        self._check_workspace()
        self._write_bb_config()

        # Build image with debug settings before starting QEMU
        self._check_runqemu_prerequisites()
        tempdir = self._devtool_ide_sdk_recipe(recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=code %s' % (recipe_name, testimage, package_opt),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # Re-run with actual QEMU IP; image is already built
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=code %s' % (
                recipe_name, testimage, qemu.ip, package_opt)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)
            self._verify_cmake_preset(tempdir)
            self._verify_install_script_code(
                tempdir, recipe_name, ['%s:,-ptest' % recipe_name])
            self._gdb_cross()

            # Verify the launch.json file created is valid
            self._verify_launch_json(tempdir)

            # Verify deployment and remote debugging works
            self._verify_launch_json_debugging(tempdir, qemu, example_exe)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_code_meson(self):
        """Verify a meson recipe works with ide=code mode"""
        recipe_name = "meson-example"
        example_exe = "mesonex"
        package_opt = '--package %s:,-ptest' % recipe_name
        build_file = "meson.build"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()

        # Build image with debug settings before starting QEMU
        self._check_runqemu_prerequisites()
        tempdir = self._devtool_ide_sdk_recipe(
            recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=code %s' % (recipe_name, testimage, package_opt),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # Re-run with actual QEMU IP; image is already built
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=code %s' % (
                recipe_name, testimage, qemu.ip, package_opt)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            with open(os.path.join(tempdir, '.vscode', 'settings.json')) as settings_j:
                settings_d = json.load(settings_j)
            meson_exe = settings_d["mesonbuild.mesonPath"]
            meson_build_folder = settings_d["mesonbuild.buildFolder"]

            # Verify the wrapper for meson native is available
            self.assertExists(meson_exe)

            # Verify meson re-uses the o files compiled by bitbake
            result = runCmd('%s compile -C  %s' %
                            (meson_exe, meson_build_folder), cwd=tempdir, output_log=self._cmd_logger)
            self.assertIn("ninja: no work to do.", result.output)

            # Verify the unit tests work (in Qemu user mode)
            runCmd('%s test -C %s' % (meson_exe, meson_build_folder), cwd=tempdir,
                   output_log=self._cmd_logger)

            # Verify re-building and testing works again
            result = runCmd('%s compile -C  %s --clean' %
                            (meson_exe, meson_build_folder), cwd=tempdir, output_log=self._cmd_logger)
            self.assertIn("Cleaning...", result.output)
            result = runCmd('%s compile -C  %s' %
                            (meson_exe, meson_build_folder), cwd=tempdir, output_log=self._cmd_logger)
            self.assertIn("Linking target", result.output)
            runCmd('%s test -C %s' % (meson_exe, meson_build_folder), cwd=tempdir,
                   output_log=self._cmd_logger)

            self._verify_install_script_code(
                tempdir, recipe_name, ['%s:,-ptest' % recipe_name])
            self._gdb_cross()

            # Verify the launch.json file created is valid
            self._verify_launch_json(tempdir)

            # Verify deployment and remote debugging works
            self._verify_launch_json_debugging(tempdir, qemu, example_exe)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_qemu_slirp(self):
        """Verify devtool ide-sdk works with runqemu slirp networking.

        Slirp mode uses SSH port forwarding (default: localhost:2222 -> guest:22).
        This test checks that:
          - update_qb_slirp_opt() writes the QB_SLIRP_OPT host-forwarding
            entries to the image workspace bbappend before the build.
          - runqemu boots the image in slirp mode.
          - devtool deploy-target reaches the target via localhost:2222.
        """
        recipe_name = "cmake-example"
        build_file = "CMakeLists.txt"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()

        # devtool modify and build image; QB_SLIRP_OPT is written to the bbappend.
        tempdir = self._devtool_ide_sdk_recipe(recipe_name, build_file, None)
        runCmd('devtool ide-sdk %s %s -c --ide=none' % (recipe_name, testimage),
               output_log=self._cmd_logger)

        # Verify QB_SLIRP_OPT was written to the workspace bbappend.
        appends_dir = os.path.join(self.workspacedir, 'appends')
        bbappend = os.path.join(appends_dir, testimage + '.bbappend')
        self.assertExists(bbappend, 'Image bbappend not created at %s' % bbappend)
        with open(bbappend) as f:
            bbappend_content = f.read()
        self.assertIn('QB_SLIRP_OPT', bbappend_content,
                      'QB_SLIRP_OPT not written to image bbappend')
        self.assertIn('hostfwd=tcp:127.0.0.1:2222-:22', bbappend_content,
                      'SSH slirp port forward missing from QB_SLIRP_OPT')

        with runqemu(testimage, runqemuparams="nographic slirp") as qemu:
            slirp_host = qemu.ip
            self.assertIsNotNone(qemu.port, 'No SSH port for the slirp target')
            slirp_port = qemu.port

            # Re-run ide-sdk with the actual slirp address; image is already built.
            bitbake_sdk_cmd = (
                'devtool ide-sdk %s %s -t root@%s -P %s --skip-bitbake --ide=none'
                % (recipe_name, testimage, slirp_host, slirp_port))
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            self._gdb_cross()
            compile_cmd = self._verify_cmake_preset(tempdir)
            self._devtool_ide_sdk_qemu(tempdir, qemu, recipe_name,
                                        recipe_name, compile_cmd)


class DevtoolIdeSdkKernelTests(DevtoolIdeSdkTests):

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_code_kernel_module(self):
        """Verify a kernel module recipe works with ide=code mode

        Test flow:
        1. devtool modify  — extract sources into a temporary directory
        2. devtool ide-sdk (no -t) — generate VSCode config files with the
           default target; verify settings.json, extensions.json,
           c_cpp_properties.json, and the install && deploy-target task
        3. Boot Qemu, then re-run devtool ide-sdk with -t and --skip-bitbake
           to update the deploy scripts with the real target address
        4. Deploy the .ko and load it with insmod; read the initial magic
           string from the sysfs attribute exposed by the module
        5. Modify the magic string in the source tree, rebuild with the make
           command and environment taken from settings.json (mirroring what
           VSCode's Makefile Tools extension would invoke), and redeploy
        6. Reload the module and verify the updated string appears in sysfs
        """
        recipe_name = "selftest-kmodule"
        build_file = "Makefile"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._check_runqemu_prerequisites()

        # Setup source tree with devtool modify
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        self.add_command_to_tearDown('bitbake -c clean %s' % recipe_name)
        result = runCmd('devtool modify %s -x %s' % (recipe_name, tempdir),
                        output_log=self._cmd_logger)
        self.assertExists(os.path.join(tempdir, build_file),
                          'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf', 'layer.conf'),
                          'Workspace directory not created')
        matches = glob.glob(os.path.join(
            self.workspacedir, 'appends', recipe_name + '.bbappend'))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)

        # Test devtool status
        result = runCmd('devtool status', output_log=self._cmd_logger)
        self.assertIn(recipe_name, result.output)
        self.assertIn(tempdir, result.output)

        # Generate VSCode configuration with the default target address; this step
        # does not require Qemu to be running and produces the settings/tasks files
        # that we verify first before booting the image.
        bitbake_sdk_cmd = 'devtool ide-sdk %s %s -c --ide=code' % (recipe_name, testimage)
        runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

        # Verify the install && deploy-target script and tasks.json entry exist
        self._verify_install_script_code(tempdir, recipe_name)

        # --- Verify settings.json ---
        with open(os.path.join(tempdir, '.vscode', 'settings.json')) as settings_j:
            settings_d = json.load(settings_j)

        # Verify make configurations are generated (build + clean)
        make_configs = settings_d.get('makefile.configurations', [])
        self.assertTrue(len(make_configs) >= 2,
                        'makefile.configurations should have at least two entries (build + clean)')
        build_config = next((c for c in make_configs if c.get('name') != 'clean'), None)
        self.assertIsNotNone(build_config,
                             'A build configuration should be present in makefile.configurations')
        clean_config = next((c for c in make_configs if c.get('name') == 'clean'), None)
        self.assertIsNotNone(clean_config,
                             'A clean configuration should be present in makefile.configurations')

        # Verify make executable is set and exists
        make_exe = build_config.get('makePath', '')
        self.assertTrue(make_exe.endswith('/make'),
                        'makePath should point to a make binary: %s' % make_exe)
        self.assertExists(make_exe)

        # Verify that the Makefile path points inside the source tree
        self.assertEqual(build_config.get('makeDirectory'), tempdir,
                         'makeDirectory should be the source tree')
        self.assertEqual(build_config.get('makefilePath'),
                         os.path.join(tempdir, 'Makefile'),
                         'makefilePath should point to the Makefile in the source tree')

        # Verify kernel sources (under TMPDIR) are set read-only
        tmpdir = get_bb_var('TMPDIR', recipe_name)
        readonly_includes = settings_d.get('files.readonlyInclude', {})
        self.assertIn(os.path.realpath(tmpdir) + '/**', readonly_includes,
                     'TMPDIR should be set read-only in files.readonlyInclude: %s' % readonly_includes)

        # Verify the cross-build environment is exported for the terminal
        self.assertIn('terminal.integrated.env.linux', settings_d,
                      'terminal.integrated.env.linux should be set for kernel modules')
        terminal_env = settings_d['terminal.integrated.env.linux']
        self.assertIn('KERNEL_SRC', terminal_env,
                      'KERNEL_SRC should be in the exported terminal environment')
        self.assertIn('KERNEL_VERSION', terminal_env,
                      'KERNEL_VERSION should be in the exported terminal environment')
        self.assertIn('CC', terminal_env,
                      'CC (kernel compiler) should be in the exported terminal environment')

        # Verify kernel-specific file exclude patterns are present
        files_exclude = settings_d.get('files.exclude', {})
        self.assertIn('**/.*.cmd', files_exclude,
                      'Kernel build artifacts (.*.cmd) should be excluded from view')
        self.assertIn('**/*.o', files_exclude,
                      'Kernel build artifacts (*.o) should be excluded from view')

        # --- Verify extensions.json ---
        with open(os.path.join(tempdir, '.vscode', 'extensions.json')) as ext_j:
            ext_d = json.load(ext_j)
        recommendations = ext_d.get('recommendations', [])
        self.assertIn('ms-vscode.makefile-tools', recommendations,
                      'ms-vscode.makefile-tools should be recommended for kernel modules')
        self.assertIn('ms-vscode.cpptools', recommendations,
                      'ms-vscode.cpptools should be recommended for kernel modules')
        # cmake-tools and mesonbuild should not be recommended for kernel modules
        self.assertNotIn('ms-vscode.cmake-tools', recommendations,
                         'ms-vscode.cmake-tools should not be recommended for kernel modules')
        self.assertNotIn('mesonbuild.mesonbuild', recommendations,
                         'mesonbuild.mesonbuild should not be recommended for kernel modules')

        # --- Verify c_cpp_properties.json ---
        with open(os.path.join(tempdir, '.vscode', 'c_cpp_properties.json')) as props_j:
            props_d = json.load(props_j)
        configurations = props_d.get('configurations', [])
        self.assertTrue(len(configurations) > 0,
                        'c_cpp_properties.json should have at least one configuration')
        # Kernel modules use gnu11 as the C standard
        self.assertEqual(configurations[0].get('cStandard'), 'gnu11',
                         'Kernel modules should use gnu11 C standard in c_cpp_properties.json')
        # Kernel include paths should be present
        include_path = configurations[0].get('includePath', [])
        self.assertTrue(
            any('kernel' in p.lower() for p in include_path),
            'Kernel include path should be present in c_cpp_properties.json: %s' % include_path)

        # Build the make environment and command from settings.json so the
        # rebuild step below uses the exact same invocation that VSCode would
        # use via the Makefile Tools extension.
        make_args = build_config.get('makeArgs', [])
        make_dir = build_config.get('makeDirectory', tempdir)
        make_env = dict(os.environ)
        make_env.update(terminal_env)

        recipe_id, _ = self._get_recipe_ids(recipe_name)
        install_deploy_cmd = os.path.join(
            self._workspace_scripts_dir(recipe_name), 'install_and_deploy_' + recipe_id)

        SYSFS_MAGIC = '/sys/kernel/selftest_kmodule/magic'
        MODULE_NAME = 'selftest_kmodule'
        MAGIC_STRING_ORIG = 'Hello from selftest-kmodule'
        MAGIC_STRING_NEW  = 'Goodbye from selftest-kmodule'

        deploy_dir_image = get_bb_var('DEPLOY_DIR_IMAGE')
        self.add_command_to_tearDown('bitbake -c clean %s' % testimage)
        self.add_command_to_tearDown('rm -f %s/%s*' % (deploy_dir_image, testimage))
        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # Re-run ide-sdk with the Qemu target address to update the
            # install && deploy scripts; --skip-bitbake avoids a rebuild.
            bitbake_sdk_cmd = (
                'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=code' % (
                    recipe_name, testimage, qemu.ip))
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            # Deploy the initial .ko to the target
            runCmd(install_deploy_cmd, output_log=self._cmd_logger)

            # Out-of-tree modules land in updates/ after modules_install;
            # use insmod with the full path to avoid needing depmod -a.
            status, output = qemu.run(
                'find /lib/modules -name "selftest-kmodule.ko" 2>/dev/null')
            self.assertEqual(status, 0)
            ko_path = output.strip()
            self.assertTrue(ko_path.endswith('selftest-kmodule.ko'),
                            'selftest-kmodule.ko not found on target: %s' % output)

            status, output = qemu.run('insmod %s' % ko_path)
            self.assertEqual(status, 0, msg='insmod failed: %s' % output)

            # Verify the sysfs interface exposes the expected magic string
            status, output = qemu.run('cat %s' % SYSFS_MAGIC)
            self.assertEqual(status, 0, msg='reading sysfs magic failed: %s' % output)
            self.assertIn(MAGIC_STRING_ORIG, output,
                          'Initial magic string not found in sysfs: %s' % output)

            # Modify the magic string in the source tree
            kmodule_c = os.path.join(tempdir, 'selftest-kmodule.c')
            with open(kmodule_c) as f:
                src = f.read()
            self.assertIn(MAGIC_STRING_ORIG, src,
                          'SELFTEST_MAGIC_STRING not found in source; cannot modify it')
            with open(kmodule_c, 'w') as f:
                f.write(src.replace(MAGIC_STRING_ORIG, MAGIC_STRING_NEW))

            # Rebuild using the make command and environment from settings.json,
            # mirroring what VSCode would invoke via the Makefile Tools extension.
            runCmd([make_exe] + make_args, cwd=make_dir, env=make_env,
                   output_log=self._cmd_logger)
            runCmd(install_deploy_cmd, output_log=self._cmd_logger)

            # Reload the updated module and verify the sysfs string changed
            status, output = qemu.run('rmmod %s' % MODULE_NAME)
            self.assertEqual(status, 0, msg='rmmod failed: %s' % output)
            status, output = qemu.run(
                'find /lib/modules -name "selftest-kmodule.ko" 2>/dev/null')
            self.assertEqual(status, 0)
            ko_path = output.strip()
            status, output = qemu.run('insmod %s' % ko_path)
            self.assertEqual(status, 0, msg='insmod of modified module failed: %s' % output)

            status, output = qemu.run('cat %s' % SYSFS_MAGIC)
            self.assertEqual(status, 0, msg='reading sysfs magic (modified) failed: %s' % output)
            self.assertNotIn(MAGIC_STRING_ORIG, output,
                             'Old magic string still present in sysfs after rebuild')
            self.assertIn(MAGIC_STRING_NEW, output,
                          'New magic string not found in sysfs after rebuild: %s' % output)

class DevtoolIdeSdkSharedTests(DevtoolIdeSdkTests):

    def test_devtool_ide_sdk_shared_sysroots(self):
        """Verify the shared sysroot SDK"""

        # Handle the workspace (which is not needed by this test case)
        self._check_workspace()

        result_init = runCmd(
            'devtool ide-sdk -m shared oe-selftest-image cmake-example meson-example --ide=code',
            output_log=self._cmd_logger)
        bb_vars = get_bb_vars(
            ['REAL_MULTIMACH_TARGET_SYS', 'DEPLOY_DIR_IMAGE', 'COREBASE'], "meta-ide-support")
        environment_script = 'environment-setup-%s' % bb_vars['REAL_MULTIMACH_TARGET_SYS']
        deploydir = bb_vars['DEPLOY_DIR_IMAGE']
        environment_script_path = os.path.join(deploydir, environment_script)
        cpp_example_src = os.path.join(
            bb_vars['COREBASE'], 'meta-selftest', 'recipes-test', 'cpp', 'files')

        # Verify the cross environment script is available
        self.assertExists(environment_script_path)

        def runCmdEnv(cmd, cwd, output_log=self._cmd_logger):
            cmd = '/bin/sh -c ". %s > /dev/null && %s"' % (
                environment_script_path, cmd)
            return runCmd(cmd, cwd, output_log=output_log)

        # Verify building the C++ example works with CMake
        tempdir_cmake = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir_cmake)

        result_cmake = runCmdEnv("which cmake", cwd=tempdir_cmake, output_log=self._cmd_logger)
        cmake_native = os.path.normpath(result_cmake.output.strip())
        self.assertExists(cmake_native)

        result_cmake = runCmdEnv('cmake -S %s -B %s' % (cpp_example_src, tempdir_cmake),
                                 cwd=tempdir_cmake, output_log=self._cmd_logger)
        self.assertIn("Build files have been written to: %s" % tempdir_cmake, result_cmake.output)
        result_cmake = runCmdEnv('cmake --build %s' % tempdir_cmake,
                                 cwd=tempdir_cmake, output_log=self._cmd_logger)
        self.assertIn("Built target", result_cmake.output)

        # Verify the printed note really referres to a cmake executable
        cmake_native_code = ""
        for line in result_init.output.splitlines():
            m = re.search(r'"cmake.cmakePath": "(.*)"', line)
            if m:
                cmake_native_code = m.group(1)
                break
        self.assertExists(cmake_native_code)
        self.assertEqual(cmake_native, cmake_native_code)

        # Verify building the C++ example works with Meson
        tempdir_meson = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir_meson)

        result_cmake = runCmdEnv("which meson", cwd=tempdir_meson, output_log=self._cmd_logger)
        meson_native = os.path.normpath(result_cmake.output.strip())
        self.assertExists(meson_native)

        runCmdEnv('meson setup %s' % tempdir_meson, cwd=cpp_example_src, output_log=self._cmd_logger)
        runCmdEnv('meson compile', cwd=tempdir_meson, output_log=self._cmd_logger)

class DevtoolIdeSdkClangTests(DevtoolIdeSdkTests):

    def _verify_launch_json_lldb(self, tempdir):
        """Verify the launch.json file contains valid CodeLLDB (type: lldb) configurations."""
        launch_json_path = os.path.join(tempdir, '.vscode', 'launch.json')
        self.assertTrue(os.path.exists(launch_json_path), "launch.json file should exist")

        with open(launch_json_path) as launch_j:
            launch_d = json.load(launch_j)

        self.assertIn("configurations", launch_d)
        configurations = launch_d["configurations"]
        self.assertGreater(len(configurations), 0,
                           "Should have at least one debug configuration")

        for config in configurations:
            config_name = config.get("name", "Unknown")
            # CodeLLDB configs use "type": "lldb", not "type": "cppdbg"
            self.assertEqual(config["type"], "lldb",
                             f"Configuration '{config_name}' should use lldb type (CodeLLDB)")
            self.assertNotIn("MIMode", config,
                             f"Configuration '{config_name}' should not have MIMode (CodeLLDB)")
            self.assertNotIn("miDebuggerPath", config,
                             f"Configuration '{config_name}' should not have miDebuggerPath")

            is_attach = "_attach" in config_name
            if is_attach:
                self.assertEqual(config["request"], "attach",
                                 f"Configuration '{config_name}' should be attach type")
                self.assertNotIn("cwd", config,
                                 f"Configuration '{config_name}' should not set cwd in attach mode")
                self.assertIn("postDebugTask", config,
                              f"attach configuration '{config_name}' should have postDebugTask "
                              "to stop the lldb-server platform instance afterwards")
            else:
                self.assertEqual(config["request"], "launch",
                                 f"Configuration '{config_name}' should be launch type")
                self.assertEqual(config["cwd"], "/tmp",
                                 f"Configuration '{config_name}' cwd should be /tmp (writable on target)")

            # Verify initCommands contain the platform connect sequence
            init_commands = config.get("initCommands", [])
            self.assertTrue(any("platform select remote-linux" in cmd
                                for cmd in init_commands),
                            f"Configuration '{config_name}' should select remote-linux platform")
            self.assertTrue(any("platform connect" in cmd for cmd in init_commands),
                            f"Configuration '{config_name}' should connect to remote platform")

            # Verify targetCreateCommands creates the target with --remote-file so
            # LLDB uses the host debug binary for symbols but executes the pre-deployed
            # binary on the target (avoiding the module-cache upload path).
            target_create_commands = config.get("targetCreateCommands", [])
            self.assertTrue(len(target_create_commands) > 0,
                            f"Configuration '{config_name}' should have targetCreateCommands")
            create_cmd = target_create_commands[0]
            self.assertIn("--remote-file", create_cmd,
                          f"Configuration '{config_name}' targetCreateCommands should use "
                          "--remote-file to specify the remote binary path")
            self.assertIn("/image/", create_cmd,
                          f"Configuration '{config_name}' targetCreateCommands should reference "
                          "the host debug binary in the image directory")

            # Verify preLaunchTask referencing the lldb-server start task
            task = config.get("preLaunchTask", "")
            self.assertTrue(task,
                            f"Configuration '{config_name}' preLaunchTask should not be empty")

    def _find_source_break_line(self, tempdir, source_file, marker):
        """Find the 1-based line number of `marker` in tempdir/source_file

        Used to set a source-level (file:line) breakpoint at a stable,
        self-documenting location instead of hard-coding a line number that
        would silently go stale whenever the example source changes.
        """
        source_path = os.path.join(tempdir, source_file)
        with open(source_path) as f:
            for lineno, line in enumerate(f, start=1):
                if marker in line:
                    return lineno
        self.fail("Could not find marker %r in %s" % (marker, source_path))

    def _lldb_debug_cpp_example_batch_commands(self, tempdir):
        """Get a list of lldb --batch '-o' commands to debug the cpp-example-lib example

        Mirrors _gdb_debug_cpp_example: sets source-level (file:line)
        breakpoints in the executable (cpp-example.cpp), the library's own
        .cpp file (cpp-example-lib.cpp) and a header-only inline function
        (cpp-example-lib.hpp), then inspects a variable at each stop. This
        exercises breakpoint resolution against all three distinct debug-info
        sources (executable, library, header-only), instead of only the
        single call-site breakpoint in the executable used previously.

        The breakpoints are deliberately set by source file:line (rather than
        by function/symbol name, e.g. "b main") because that is what exercises
        CodeLLDB/LLDB's sourceMap reverse-lookup: translating a source file
        path back into debug-info space to resolve the breakpoint location.
        A symbol breakpoint resolves directly from the symbol table and would
        not catch a broken/ambiguous sourceMap (see the "fix source map for
        lldb" change), so it is not a sufficient regression test on its own.

        Line numbers are (re-)discovered via markers rather than hard-coded,
        so the same commands keep working after a caller shifts lines with
        _shift_cpp_example_lines_and_recompile() and recompiles.
        """
        exe_break_line = self._find_source_break_line(
            tempdir, 'cpp-example.cpp', 'cpp_example.print_json();')
        lib_break_line = self._find_source_break_line(
            tempdir, 'cpp-example-lib.cpp',
            'std::cout << json_object_to_json_string_ext(jobj, flag) << std::endl;')
        hpp_break_line = self._find_source_break_line(
            tempdir, 'cpp-example-lib.hpp', 'int scaled = n * 7;')

        return [
            "-o", "breakpoint set --file cpp-example.cpp --line %d" % exe_break_line,
            "-o", "breakpoint set --file cpp-example-lib.cpp --line %d" % lib_break_line,
            "-o", "breakpoint set --file cpp-example-lib.hpp --line %d" % hpp_break_line,
            # Report the resolved location count right away: sourceMap
            # ambiguity/underflow (the meson/ninja relative-path bug) leaves a
            # breakpoint at "locations = 0 (pending)" even though the
            # breakpoint gutter/status can otherwise look "verified".
            "-o", "breakpoint list",
            "-o", "run",
            # Stop 1: the executable's own breakpoint, right before the call
            # into the library.
            "-o", "p cpp_example.get_string()",
            "-o", "continue",
            # Stop 2: inside the library's own .cpp file, by file:line. This
            # is the breakpoint that would fail to resolve (or resolve to a
            # stale line) if the freshly rebuilt library debug info was not
            # preferred over a stale rootfs-dbg copy. Inspect a plain local
            # variable (rather than the CppExample::test_string static class
            # member, as the GDB test does) because LLDB cannot reliably
            # evaluate an expression that needs to trigger the lazy
            # initialization guard of a C++17 inline static std::string over
            # a remote gdb-remote connection ("Couldn't look up symbols").
            "-o", "p flag",
            "-o", "continue",
            # Stop 3: inside an inline function defined directly in the
            # header (cpp-example-lib.hpp).
            "-o", "p n",
            "-o", "continue",
            "-o", "exit",
        ]

    def _lldb_debug_cpp_example_check(self, output, magic_string):
        """Check the output of an lldb --batch session run with the commands
        from _lldb_debug_cpp_example_batch_commands()"""
        self.assertNotIn("(pending)", output,
                         "breakpoints should resolve to a location instead of staying "
                         "pending (sourceMap/prefix-map path mismatch): %s" % output)
        # LLDB emits this when the MD5 checksum embedded in the DWARF line
        # table (recorded by the compiler at compile time) doesn't match the
        # file currently on disk. This should never happen for a freshly
        # (re)compiled and (re)deployed example: it would mean the debugger
        # is displaying/attributing source lines that don't actually
        # correspond to the binary being debugged. Treat it as a hard
        # failure instead of silently tolerating it, so a regression here
        # (e.g. a sourceMap entry getting clobbered, causing LLDB to resolve
        # a source file against the wrong, stale location) doesn't go
        # unnoticed.
        self.assertNotIn("source file checksum mismatch", output,
                         "debug info should match the current source files exactly "
                         "(sourceMap resolving to a stale copy of the file?): %s" % output)
        # exe (by file:line), library (by file:line) and header (by
        # file:line) breakpoints should all have been hit: 1 + 1 + 1 = 3
        self.assertEqual(output.count("stop reason = breakpoint"), 3,
                         "expected 3 breakpoint hits (executable once, library "
                         "once, header inline function once): %s" % output)
        # a local variable should be inspectable at the library breakpoint,
        # proving the library's own debug info is usable (JSON_C_TO_STRING_SPACED
        # | JSON_C_TO_STRING_PRETTY == 3). LLDB doesn't always print a "$N ="
        # value slot (e.g. "(const int) 3" for a compile-time constant vs.
        # "(int) $0 = 3"/"(int) 3" for an ordinary variable), so anchor the
        # check on the "p flag" command itself rather than assuming a "="
        # appears in its output.
        self.assertRegex(output, r"\(lldb\) p flag\r?\n[^\n]*\b3\b",
                        "should be able to inspect flag at the library breakpoint: %s" % output)
        # the magic string should be visible in the program's own output,
        # proving the freshly (re)compiled example actually ran
        self.assertIn(magic_string, output,
                     "should be able to see the magic string printed by the example")
        # the header-only inline function was hit once, called with n == 6
        self.assertRegex(output, r"\(lldb\) p n\r?\n[^\n]*\b6\b",
                        "should be able to inspect n == 6 at the header breakpoint: %s" % output)
        self.assertIn("exited with status = 0", output,
                     "the example should run to completion and exit normally: %s" % output)

    def _shift_cpp_example_lines_and_recompile(self, tempdir, compile_cmd, install_deploy_cmd,
                                               magic_string_new, line_shift=3):
        """Change the magic string and insert line_shift extra lines before the
        statements the LLDB file:line breakpoints target, then recompile and redeploy.

        Mirrors the code-change step of _devtool_ide_sdk_qemu (used by the GDB
        tests): shifts the executable's, the library's and the header's source
        lines so that a subsequent debugging pass' file:line breakpoints only
        resolve correctly if they are based on the freshly rebuilt debug info,
        rather than a stale/cached line-to-address mapping (or, for the
        library, a stale rootfs-dbg copy instead of the freshly redeployed
        image folder).
        """
        extra_lines = "".join(
            "    // extra line %d inserted by the test to shift subsequent line numbers\n" % i
            for i in range(line_shift))

        cpp_example_lib_hpp = os.path.join(tempdir, 'cpp-example-lib.hpp')
        with open(cpp_example_lib_hpp, 'r') as file:
            cpp_code = file.read()
        cpp_code = cpp_code.replace(DevtoolIdeSdkTests.MAGIC_STRING_ORIG, magic_string_new)
        cpp_code = cpp_code.replace(
            "    static int scale_number(int n)",
            extra_lines + "    static int scale_number(int n)")
        with open(cpp_example_lib_hpp, 'w') as file:
            file.write(cpp_code)

        cpp_example_cpp = os.path.join(tempdir, 'cpp-example.cpp')
        with open(cpp_example_cpp, 'r') as file:
            cpp_code = file.read()
        cpp_code = cpp_code.replace(
            "    cpp_example.print_json();",
            extra_lines + "    cpp_example.print_json();")
        with open(cpp_example_cpp, 'w') as file:
            file.write(cpp_code)

        cpp_example_lib_cpp = os.path.join(tempdir, 'cpp-example-lib.cpp')
        with open(cpp_example_lib_cpp, 'r') as file:
            cpp_code = file.read()
        cpp_code = cpp_code.replace(
            "    std::cout << json_object_to_json_string_ext(jobj, flag) << std::endl;",
            extra_lines + "    std::cout << json_object_to_json_string_ext(jobj, flag) << std::endl;")
        with open(cpp_example_lib_cpp, 'w') as file:
            file.write(cpp_code)

        runCmd(compile_cmd, cwd=tempdir, output_log=self._cmd_logger)
        runCmd(install_deploy_cmd, cwd=tempdir, output_log=self._cmd_logger)

    def _lldb_cross_debugging_multi(self, tempdir, recipe_name, compile_cmd, lldb_session_func):
        """Verify LLDB remote debugging in Qemu, before and after a code change.

        Mirrors _devtool_ide_sdk_qemu (the GDB equivalent): debugs the example
        once, then edits the source (magic string + line shift on the
        executable, library and header), recompiles, redeploys, and debugs a
        second time to prove the breakpoints resolve against the freshly
        rebuilt debug info rather than a stale/cached line-to-address mapping,
        closing the coverage gap between the GDB and LLDB test paths.

        lldb_session_func is called once per pass with the magic string
        expected for that pass; it is expected to run an lldb --batch session
        and check its output (e.g. a closure around _lldb_server_debugging_once
        or _lldb_none_debugging_multi).
        """
        recipe_id, _ = self._get_recipe_ids(recipe_name)
        install_deploy_cmd = os.path.join(
            self._workspace_scripts_dir(recipe_name), 'install_and_deploy_' + recipe_id)
        self.assertExists(install_deploy_cmd, '%s script not found' % install_deploy_cmd)
        runCmd(install_deploy_cmd, output_log=self._cmd_logger)

        # First pass: debug the freshly deployed, unmodified example
        lldb_session_func(DevtoolIdeSdkTests.MAGIC_STRING_ORIG)

        # Change the magic string, shift breakpoint lines, recompile and redeploy
        magic_string_new = "Magic: 987654321"
        self._shift_cpp_example_lines_and_recompile(
            tempdir, compile_cmd, install_deploy_cmd, magic_string_new)

        # Second pass: debug again, breakpoints now resolved at shifted line numbers
        lldb_session_func(magic_string_new)

    def _lldb_server_debugging_once(self, tempdir, qemu, magic_string):
        """Verify lldb-server (platform mode) + lldb batch debugging works end-to-end.

        Reads the preLaunchTask SSH command from tasks.json to start lldb-server
        on the target, then runs lldb --batch to perform a debugging session
        covering the executable, the library and a header-only inline function
        (see _lldb_debug_cpp_example_batch_commands), and checks that the
        expected magic string and variable values are visible.
        """
        with open(os.path.join(tempdir, '.vscode', 'launch.json')) as f:
            launch_d = json.load(f)
        with open(os.path.join(tempdir, '.vscode', 'tasks.json')) as f:
            tasks_d = json.load(f)

        # Find the first *_once or *_multi config
        lldb_config = next(
            (c for c in launch_d["configurations"]
             if "_once" in c["name"] or "_multi" in c["name"]), None)
        self.assertIsNotNone(lldb_config, "Should have at least one lldb debug configuration")

        prelaunch_task_name = lldb_config["preLaunchTask"]
        prelaunch_task = next(
            (t for t in tasks_d["tasks"] if t["label"] == prelaunch_task_name), None)
        self.assertIsNotNone(prelaunch_task,
                             "preLaunchTask '%s' not found in tasks.json" % prelaunch_task_name)

        # Run compile/install/deploy prerequisites exactly as VS Code would,
        # but keep starting the debug server itself in this method so we can
        # retain explicit readiness handling and diagnostics.
        self._run_vscode_task_with_dependencies(
            tempdir, prelaunch_task_name, run_main_task=False)

        # Extract the SSH command and start lldb-server on the target
        task_command = prelaunch_task["command"]
        task_args = prelaunch_task["args"]
        self.assertEqual(task_command, "ssh",
                         "preLaunchTask should use ssh to start lldb-server")
        ssh_cmd = [task_command] + task_args
        if ssh_cmd[-1].startswith('"') and ssh_cmd[-1].endswith('"'):
            # The tasks.json arg is formatted for a shell double-quoted context
            # (e.g. bash running "ssh ... \"...\$((...))...\"").  Strip the
            # surrounding quotes and undo the \$ → $ escaping that would
            # normally be done by bash, so the arg works when passed directly
            # to SSH via subprocess without an intermediate shell.
            ssh_cmd[-1] = ssh_cmd[-1][1:-1].replace('\\$', '$')

        # The generated foreground preLaunchTask does not complete until its
        # target-side command has observed lldb-server listening.  Run it
        # synchronously so this selftest follows the same ordering as VS Code.
        init_commands = lldb_config["initCommands"]
        connect_cmd = next((c for c in init_commands if "platform connect" in c), None)
        self.assertIsNotNone(connect_cmd, "initCommands should contain a platform connect command")

        # Find lldb binary from lldb-native sysroot
        lldb_native_sysroot = get_bb_var('RECIPE_SYSROOT_NATIVE', 'lldb-native')
        lldb_binary = os.path.join(lldb_native_sysroot, 'usr', 'bin', 'lldb')
        self.assertExists(lldb_binary, "lldb binary should exist in lldb-native sysroot")

        self.logger.debug("Starting lldb-server via SSH: %s", " ".join(ssh_cmd))
        runCmd(ssh_cmd, output_log=self._cmd_logger)

        # Run lldb --batch: connect to platform, create target with remote-file,
        # set a source-level breakpoint, and run.
        # targetCreateCommands replaces the "program" field; each entry is
        # passed as a separate -o command in batch mode.
        target_create_commands = lldb_config.get("targetCreateCommands", [])
        source_map = lldb_config.get("sourceMap", {})

        lldb_batch = [lldb_binary, "--batch"]
        for cmd in init_commands:
            lldb_batch += ["-o", cmd]
        for cmd in target_create_commands:
            lldb_batch += ["-o", cmd]
        if source_map:
            # "settings set target.source-map" replaces the *entire*
            # mapping list rather than appending to it. Issuing one
            # "-o settings set target.source-map ..." per entry (as done
            # previously) silently discards all but the last mapping, so
            # LLDB ends up resolving source files (and verifying their
            # DWARF MD5 checksum) against the wrong location, e.g. a
            # stale rootfs-dbg copy of a devtool-modified recipe's own
            # sources instead of the freshly edited workspace srctree.
            # All pairs must therefore be set together in a single
            # command, exactly like CodeLLDB itself does.
            source_map_args = []
            for k, v in source_map.items():
                v_resolved = v.replace("${workspaceFolder}", tempdir)
                source_map_args += [k, v_resolved]
            lldb_batch += ["-o", "settings set target.source-map %s" % " ".join(source_map_args)]
        lldb_batch += self._lldb_debug_cpp_example_batch_commands(tempdir)
        r = runCmd(lldb_batch, output_log=self._cmd_logger)
        self.assertEqual(r.status, 0, "lldb batch session failed: %s" % r.output)
        self._lldb_debug_cpp_example_check(r.output, magic_string)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_code_cmake(self):
        """Verify a cmake recipe built with clang works with ide=code (CodeLLDB debugging).

        This test uses the cmake-example-clang recipe which is a cmake-example variant
        built with clang. It installs a separate binary (cmake-example-clang) so all four
        recipe variants (cmake/meson x gcc/clang) can be installed in the same image
        without conflicts. It is configured to use lldb-server for debugging instead of
        gdbserver. The test flow is similar to test_devtool_ide_sdk_code_cmake but with
        additional checks related to lldb:
        - devtool ide-sdk selects lldb-native / lldb-server instead of gdb-cross
        - launch.json uses "type": "lldb" (CodeLLDB) instead of "type": "cppdbg"
        - extensions.json recommends vadimcn.vscode-lldb
        - A basic lldb --batch remote debugging session succeeds against the
          lldb-server platform running on the Qemu target
        """
        recipe_name = "cmake-example-clang"
        build_file = "CMakeLists.txt"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()

        # Build image with debug settings (lldb-server for clang) before starting QEMU
        self._check_runqemu_prerequisites()
        tempdir = self._devtool_ide_sdk_recipe(recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=code' % (recipe_name, testimage),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # Re-run with actual QEMU IP; image is already built
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=code' % (
                recipe_name, testimage, qemu.ip)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            # Verify the cmake preset still works (build system unchanged)
            compile_cmd = self._verify_cmake_preset(tempdir)

            # Verify the install && deploy-target task script exists
            self._verify_install_script_code(tempdir, recipe_name)

            # Verify extensions.json recommends CodeLLDB instead of / alongside cpptools
            with open(os.path.join(tempdir, '.vscode', 'extensions.json')) as ext_j:
                ext_d = json.load(ext_j)
            recommendations = ext_d.get('recommendations', [])
            self.assertIn('vadimcn.vscode-lldb', recommendations,
                          'vadimcn.vscode-lldb should be recommended for clang recipes')

            # Verify launch.json uses CodeLLDB format
            self._verify_launch_json_lldb(tempdir)

            # Verify deployment and lldb batch remote debugging work end-to-end,
            # before and after a code change/recompile/redeploy cycle (see
            # _lldb_cross_debugging_multi)
            self._lldb_cross_debugging_multi(
                tempdir, recipe_name, compile_cmd,
                lambda magic_string: self._lldb_server_debugging_once(
                    tempdir, qemu, magic_string))

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_code_meson(self):
        """Verify a meson recipe built with clang works with ide=code (CodeLLDB debugging).

        This is the meson/ninja counterpart of test_devtool_ide_sdk_code_cmake_clang.
        It matters as its own test (rather than being covered by the cmake/clang
        test alone) because meson/ninja invoke the compiler with source paths
        relative to the build directory, unlike cmake (with the Ninja or
        Makefiles generators used here), which normally passes absolute source
        paths. That relative-path compilation is what previously caused
        -fdebug-prefix-map/-ffile-prefix-map underflow (DW_AT_name climbing
        above DW_AT_comp_dir with excess dot-dot components) for devtool
        workspaces, breaking source-level breakpoint resolution in CodeLLDB.
        The cmake/clang test alone would not catch that regression.

        This test uses the meson-example-clang recipe which is a meson-example
        variant built with clang. It installs a separate binary
        (mesonex-clang) so all four recipe variants (cmake/meson x gcc/clang)
        can be installed in the same image without conflicts.
        """
        recipe_name = "meson-example-clang"
        build_file = "meson.build"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config()

        # Build image with debug settings (lldb-server for clang) before starting QEMU
        self._check_runqemu_prerequisites()
        tempdir = self._devtool_ide_sdk_recipe(recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=code' % (recipe_name, testimage),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # Re-run with actual QEMU IP; image is already built
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=code' % (
                recipe_name, testimage, qemu.ip)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            # Verify the meson build system still works (unchanged by clang/lldb support)
            compile_cmd = self._verify_meson_build(tempdir, recipe_name)

            # Verify the install && deploy-target task script exists
            self._verify_install_script_code(tempdir, recipe_name)

            # Verify extensions.json recommends CodeLLDB instead of / alongside cpptools
            with open(os.path.join(tempdir, '.vscode', 'extensions.json')) as ext_j:
                ext_d = json.load(ext_j)
            recommendations = ext_d.get('recommendations', [])
            self.assertIn('vadimcn.vscode-lldb', recommendations,
                          'vadimcn.vscode-lldb should be recommended for clang recipes')

            # Verify launch.json uses CodeLLDB format
            self._verify_launch_json_lldb(tempdir)

            # Verify deployment and lldb batch remote debugging work end-to-end,
            # before and after a code change/recompile/redeploy cycle (see
            # _lldb_cross_debugging_multi)
            self._lldb_cross_debugging_multi(
                tempdir, recipe_name, compile_cmd,
                lambda magic_string: self._lldb_server_debugging_once(
                    tempdir, qemu, magic_string))

    def _lldb_none_debugging_multi(self, tempdir, qemu, recipe_name, example_exe, magic_string):
        """Verify lldb-server scripts generated by ide=none work end-to-end.

        Mirrors _gdb_cross_debugging_multi but for the LLDB/clang path:
        - uses the generated lldb_server_* start/stop script
        - uses the generated lldb_* wrapper script with lldb --batch

        Covers the executable, the library and a header-only inline function
        (see _lldb_debug_cpp_example_batch_commands), since a single call-site
        breakpoint would not exercise (and thus not catch a regression in) the
        lldbinit source map / debug-file-search-paths setup for the library's
        own debug info specifically.
        """
        scripts_dir = self._workspace_scripts_dir(recipe_name)
        binary_pretty = 'usr-bin-' + example_exe
        lldb_server_script = os.path.join(
            scripts_dir, 'lldb_server_1234_%s_multi' % binary_pretty)
        lldb_script = os.path.join(scripts_dir, 'lldb_1234_%s' % binary_pretty)

        self.assertExists(lldb_server_script)
        self.assertExists(lldb_script)

        # Start lldb-server (script exits once the port is ready)
        r = runCmd(lldb_server_script, output_log=self._cmd_logger)
        self.assertEqual(r.status, 0)

        pid_file = '/tmp/lldb_server_1234_%s_multi/lldb_server.pid' % binary_pretty
        status, output = qemu.run('cat %s' % pid_file)
        self.assertEqual(status, 0)
        lldb_server_pid = output.strip()
        self.assertRegex(lldb_server_pid, r'^\d+$')

        # Verify the pid file points at the running lldb-server process
        status, output = qemu.run('cat /proc/%s/cmdline' % lldb_server_pid)
        self.assertEqual(status, 0)
        self.assertIn('lldb-server', output)

        # Run an lldb batch session covering the executable, library and
        # header breakpoints, then continue to completion
        lldb_batch_args = self._lldb_debug_cpp_example_batch_commands(tempdir)
        r = runCmd([lldb_script, '--batch'] + lldb_batch_args,
                   output_log=self._cmd_logger)
        self.logger.debug("lldb batch output: %s", r.output)
        self.assertEqual(r.status, 0)
        self._lldb_debug_cpp_example_check(r.output, magic_string)

        # Stop lldb-server
        r = runCmd(lldb_server_script + ' stop', output_log=self._cmd_logger)
        self.assertEqual(r.status, 0)

        # The stop script waits for its recorded PID before it succeeds.
        status, _ = qemu.run('test ! -d /proc/%s && test ! -e %s' % (
            lldb_server_pid, pid_file))
        self.assertEqual(status, 0)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_cmake(self):
        """Verify ide=none generates correct LLDB scripts for a clang cmake recipe.

        Uses cmake-example-clang (TOOLCHAIN = "clang") which is built with the
        clang toolchain.  devtool ide-sdk with --ide=none should produce:
          - lldb_server_<port>_<binary>_multi  (start/stop script)
          - lldbinit/lldbinit_<port>_<binary>  (platform connect + source maps)
          - lldb_<port>_<binary>               (lldb wrapper)

        The test verifies that lldb-server can be started via the generated
        script, and that a basic lldb --batch debugging session reaches main.
        """
        recipe_name = 'cmake-example-clang'
        example_exe = 'cmake-example-clang'
        build_file = 'CMakeLists.txt'
        testimage = 'oe-selftest-image'

        self._check_workspace()
        self._write_bb_config()
        self._check_runqemu_prerequisites()

        # Build image with debug settings (lldb-server for clang) before starting QEMU
        tempdir = self._devtool_ide_sdk_recipe(recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=none' % (recipe_name, testimage),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams='nographic') as qemu:
            # Re-run with actual QEMU IP; image is already built
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=none' % (
                recipe_name, testimage, qemu.ip)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            # Verify cmake preset still works (build system unchanged)
            compile_cmd = self._verify_cmake_preset(tempdir)

            # Verify install && deploy script exists
            # (_verify_install_script_code opens .vscode/tasks.json which is not
            # generated for ide=none; check the script path directly instead)
            recipe_id, _ = self._get_recipe_ids(recipe_name)
            scripts_dir = self._workspace_scripts_dir(recipe_name)
            self.assertExists(os.path.join(
                scripts_dir, 'install_and_deploy_' + recipe_id))

            # Verify LLDB scripts were generated (not GDB scripts)
            binary_pretty = 'usr-bin-' + example_exe
            self.assertExists(os.path.join(
                scripts_dir, 'lldb_server_1234_%s_multi' % binary_pretty))
            self.assertExists(os.path.join(
                scripts_dir, 'lldbinit', 'lldbinit_1234_%s' % binary_pretty))
            self.assertExists(os.path.join(
                scripts_dir, 'lldb_1234_%s' % binary_pretty))
            # No GDB scripts should have been generated for a clang recipe
            self.assertFalse(os.path.exists(os.path.join(
                scripts_dir, 'gdbserver_1234_%s_multi' % binary_pretty)),
                'gdbserver script should not be generated for clang recipe')

            # Verify the oe-scripts sym-link is valid
            self.assertEqual(self._workspace_scripts_dir(recipe_name),
                             self._sources_scripts_dir(tempdir))

            # Verify end-to-end lldb debugging, before and after a code
            # change/recompile/redeploy cycle (see _lldb_cross_debugging_multi)
            self._lldb_cross_debugging_multi(
                tempdir, recipe_name, compile_cmd,
                lambda magic_string: self._lldb_none_debugging_multi(
                    tempdir, qemu, recipe_name, example_exe, magic_string))

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_meson(self):
        """Verify ide=none generates correct LLDB scripts for a clang meson recipe.

        This is the meson/ninja counterpart of test_devtool_ide_sdk_none_cmake_clang.
        It is required in addition to the cmake/clang test because meson/ninja
        (unlike cmake here) invoke the compiler with source paths relative to
        the build directory, which is what previously caused source-level
        breakpoints to be left unresolved (pending) for devtool workspaces due
        to -fdebug-prefix-map/-ffile-prefix-map underflow; see
        _lldb_none_debugging_multi.

        Uses meson-example-clang (TOOLCHAIN = "clang").
        """
        recipe_name = 'meson-example-clang'
        example_exe = 'mesonex-clang'
        build_file = 'meson.build'
        testimage = 'oe-selftest-image'

        self._check_workspace()
        self._write_bb_config()
        self._check_runqemu_prerequisites()

        # Build image with debug settings (lldb-server for clang) before starting QEMU
        tempdir = self._devtool_ide_sdk_recipe(recipe_name, build_file, testimage)
        runCmd('devtool ide-sdk %s %s -c --ide=none' % (recipe_name, testimage),
               output_log=self._cmd_logger)

        with runqemu(testimage, runqemuparams='nographic') as qemu:
            # Re-run with actual QEMU IP; image is already built
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --skip-bitbake --ide=none' % (
                recipe_name, testimage, qemu.ip)
            runCmd(bitbake_sdk_cmd, output_log=self._cmd_logger)

            # Verify the meson build system still works (unchanged by clang/lldb support)
            compile_cmd = self._verify_meson_build(tempdir, recipe_name)

            # Verify install && deploy script exists
            # (_verify_install_script_code opens .vscode/tasks.json which is not
            # generated for ide=none; check the script path directly instead)
            recipe_id, _ = self._get_recipe_ids(recipe_name)
            scripts_dir = self._workspace_scripts_dir(recipe_name)
            self.assertExists(os.path.join(
                scripts_dir, 'install_and_deploy_' + recipe_id))

            # Verify LLDB scripts were generated (not GDB scripts)
            binary_pretty = 'usr-bin-' + example_exe
            self.assertExists(os.path.join(
                scripts_dir, 'lldb_server_1234_%s_multi' % binary_pretty))
            self.assertExists(os.path.join(
                scripts_dir, 'lldbinit', 'lldbinit_1234_%s' % binary_pretty))
            self.assertExists(os.path.join(
                scripts_dir, 'lldb_1234_%s' % binary_pretty))
            # No GDB scripts should have been generated for a clang recipe
            self.assertFalse(os.path.exists(os.path.join(
                scripts_dir, 'gdbserver_1234_%s_multi' % binary_pretty)),
                'gdbserver script should not be generated for clang recipe')

            # Verify the oe-scripts sym-link is valid
            self.assertEqual(self._workspace_scripts_dir(recipe_name),
                             self._sources_scripts_dir(tempdir))

            # Verify end-to-end lldb debugging, before and after a code
            # change/recompile/redeploy cycle (see _lldb_cross_debugging_multi)
            self._lldb_cross_debugging_multi(
                tempdir, recipe_name, compile_cmd,
                lambda magic_string: self._lldb_none_debugging_multi(
                    tempdir, qemu, recipe_name, example_exe, magic_string))

class DevtoolIdeSdkMiscTests(DevtoolIdeSdkTests):

    def test_devtool_ide_sdk_plugins(self):
        """Test that devtool ide-sdk can use plugins from other layers."""

        # We need a workspace layer and a modified recipe (but no image)
        modified_recipe_name = "meson-example"
        modified_build_file = "meson.build"
        testimage = "oe-selftest-image"
        shared_recipe_name = "cmake-example"

        self._check_workspace()
        self._write_bb_config()
        tempdir = self._devtool_ide_sdk_recipe(
            modified_recipe_name, modified_build_file, None)

        IDE_RE = re.compile(r'.*--ide \{(.*)\}.*')

        def get_ides_from_help(help_str):
            m = IDE_RE.search(help_str)
            return m.group(1).split(',')

        # verify the default plugins are available but the foo plugin is not
        result = runCmd('devtool ide-sdk -h', output_log=self._cmd_logger)
        found_ides = get_ides_from_help(result.output)
        self.assertIn('code', found_ides)
        self.assertIn('none', found_ides)
        self.assertNotIn('foo', found_ides)

        shared_config_file = os.path.join(tempdir, 'shared-config.txt')
        shared_config_str = 'Dummy shared IDE config'
        modified_config_file = os.path.join(tempdir, 'modified-config.txt')
        modified_config_str = 'Dummy modified IDE config'

        # Generate a foo plugin in the workspace layer
        plugin_dir = os.path.join(
            self.workspacedir, 'lib', 'devtool', 'ide_plugins')
        os.makedirs(plugin_dir)
        plugin_code = 'from devtool.ide_plugins import IdeBase\n\n'
        plugin_code += 'class IdeFoo(IdeBase):\n'
        plugin_code += '    def setup_shared_sysroots(self, shared_env):\n'
        plugin_code += '        with open("%s", "w") as config_file:\n' % shared_config_file
        plugin_code += '            config_file.write("%s")\n\n' % shared_config_str
        plugin_code += '    def setup_modified_recipe(self, args, image_recipe, modified_recipe):\n'
        plugin_code += '        with open("%s", "w") as config_file:\n' % modified_config_file
        plugin_code += '            config_file.write("%s")\n\n' % modified_config_str
        plugin_code += 'def register_ide_plugin(ide_plugins):\n'
        plugin_code += '    ide_plugins["foo"] = IdeFoo\n'

        plugin_py = os.path.join(plugin_dir, 'ide_foo.py')
        with open(plugin_py, 'w') as plugin_file:
            plugin_file.write(plugin_code)

        # Verify the foo plugin is available as well
        result = runCmd('devtool ide-sdk -h', output_log=self._cmd_logger)
        found_ides = get_ides_from_help(result.output)
        self.assertIn('code', found_ides)
        self.assertIn('none', found_ides)
        self.assertIn('foo', found_ides)

        # Verify the foo plugin generates a shared config
        result = runCmd(
            'devtool ide-sdk -m shared --skip-bitbake --ide foo %s' % shared_recipe_name,
            output_log=self._cmd_logger)
        with open(shared_config_file) as shared_config:
            shared_config_new = shared_config.read()
        self.assertEqual(shared_config_str, shared_config_new)

        # Verify the foo plugin generates a modified config
        result = runCmd('devtool ide-sdk --skip-bitbake --ide foo %s %s' %
                        (modified_recipe_name, testimage), output_log=self._cmd_logger)
        with open(modified_config_file) as modified_config:
            modified_config_new = modified_config.read()
        self.assertEqual(modified_config_str, modified_config_new)
