#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import errno
import os
import re
import shutil
import tempfile
import glob
import fnmatch
import unittest
import json

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, create_temp_layer
from oeqa.utils.commands import get_bb_vars, runqemu, get_test_layer
from oeqa.core.decorator import OETestTag

oldmetapath = None

def setUpModule():
    import bb.utils

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
        cls.sstate_conf += ('BB_HASHSERVE_UPSTREAM = "hashserv.yocto.io:8687"\n')

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
        bb.utils.mkdirhier(test_file_dir_full)
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
        checkvars['LICENSE'] = 'GPL-2.0-only'
        checkvars['LIC_FILES_CHKSUM'] = 'file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263'
        checkvars['S'] = '${WORKDIR}/git'
        checkvars['PV'] = '0.1+git'
        checkvars['SRC_URI'] = 'git://git.yoctoproject.org/git/dbus-wait;protocol=https;branch=master'
        checkvars['SRCREV'] = srcrev
        checkvars['DEPENDS'] = set(['dbus'])
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_git_style1(self):
        version = 'v3.1.0'
        pn = 'mbedtls'
        # this will trigger reformat_git_uri with branch parameter in url
        git_url = "'git://git@github.com/ARMmbed/mbedtls.git;branch=mbedtls-2.28;protocol=https'"
        resulting_src_uri = "git://git@github.com/ARMmbed/mbedtls.git;branch=mbedtls-2.28;protocol=https"
        self._test_devtool_add_git_url(git_url, version, pn, resulting_src_uri)

    def test_devtool_add_git_style2(self):
        version = 'v3.1.0'
        srcrev = 'v3.1.0'
        pn = 'mbedtls'
        # this will trigger reformat_git_uri with branch parameter in url
        git_url = "'git://git@github.com/ARMmbed/mbedtls.git;protocol=https'"
        resulting_src_uri = "git://git@github.com/ARMmbed/mbedtls.git;protocol=https;branch=master"
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
        result = runCmd('recipetool setvar %s EXTRA_OECMAKE -- \'-DPYTHON_BINDINGS=OFF -DLIBFTDI_CMAKE_CONFIG_DIR=${datadir}/cmake/Modules\'' % recipefile)
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
        checkvars['S'] = '${WORKDIR}/MarkupSafe-${PV}'
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
        checkvars['S'] = '${WORKDIR}/MarkupSafe-%s' % testver
        checkvars['SRC_URI'] = url
        self._test_recipe_contents(recipefile, checkvars, [])

    def test_devtool_add_fetch_git(self):
        tempdir = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir)
        url = 'gitsm://git.yoctoproject.org/mraa'
        url_branch = '%s;branch=master' % url
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
        checkvars['PV'] = '1.0+git'
        checkvars['SRC_URI'] = url_branch
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

        cargo_config_path = os.path.join(cargo_home, 'config')
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
        result = runCmd('git add minicom', cwd=os.path.dirname(recipefile))
        expected_status = [(' M', '.*/%s$' % os.path.basename(recipefile)),
                           ('A ', '.*/0001-Change-the-README.patch$'),
                           ('A ', '.*/0002-Add-a-new-file.patch$')]
        self._check_repo_status(os.path.dirname(recipefile), expected_status)

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
        result = runCmd("sed 's!\\(#define VERSION\\W*\"[^\"]*\\)\"!\\1-custom\"!' -i ReadMe.c", cwd=tempsrcdir)
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
        bb_vars = get_bb_vars(['SRC_URI', 'S', 'WORKDIR', 'FILE'], testrecipe)
        self.assertIn('git://', bb_vars['SRC_URI'], 'This test expects the %s recipe to be a git recipe' % testrecipe)
        workdir_git = '%s/git/' % bb_vars['WORKDIR']
        if not bb_vars['S'].startswith(workdir_git):
            self.fail('This test expects the %s recipe to be building from a subdirectory of the git repo' % testrecipe)
        subdir = bb_vars['S'].split(workdir_git, 1)[1]
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
                           ('??', '.*/.*-Adding-a-new-file.patch$')]
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

    @OETestTag("runqemu")
    def test_devtool_deploy_target(self):
        self._check_runqemu_prerequisites()
        self.assertTrue(not os.path.exists(self.workspacedir), 'This test cannot be run with a workspace directory under the build directory')
        # Definitions
        testrecipe = 'mdadm'
        testfile = '/sbin/mdadm'
        if "usrmerge" in get_bb_var('DISTRO_FEATURES'):
            testfile = '/usr/sbin/mdadm'
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

    def setUp(self):
        super().setUp()
        try:
            runCmd("git config --global user.name")
            runCmd("git config --global user.email")
        except:
            self.skip("Git user.name and user.email must be set")

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

    def test_devtool_upgrade_recipe_update_extra_tasks(self):
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
        checkvars['S'] = '${WORKDIR}/%s-%s' % (recipename, recipever)
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
        checkvars['S'] = '${WORKDIR}/%s-${PV}' % recipename
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


class DevtoolIdeSdkTests(DevtoolBase):
    def _write_bb_config(self, recipe_names):
        """Helper to write the bitbake local.conf file"""
        conf_lines = [
            'IMAGE_CLASSES += "image-combined-dbg"',
            'IMAGE_GEN_DEBUGFS = "1"',
            'IMAGE_INSTALL:append = " gdbserver %s"' % ' '.join(
                [r + '-ptest' for r in recipe_names])
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

        result = runCmd('devtool modify %s -x %s' % (recipe_name, tempdir))
        self.assertExists(os.path.join(tempdir, build_file),
                          'Extracted source could not be found')
        self.assertExists(os.path.join(self.workspacedir, 'conf',
                                       'layer.conf'), 'Workspace directory not created')
        matches = glob.glob(os.path.join(self.workspacedir,
                            'appends', recipe_name + '.bbappend'))
        self.assertTrue(matches, 'bbappend not created %s' % result.output)

        # Test devtool status
        result = runCmd('devtool status')
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

    def _verify_install_script_code(self, tempdir, recipe_name):
        """Verify the scripts referred by the tasks.json file are fine.

        This function does not depend on Qemu. Therefore it verifies the scripts
        exists and the delete step works as expected. But it does not try to
        deploy to Qemu.
        """
        recipe_id, recipe_id_pretty = self._get_recipe_ids(recipe_name)
        with open(os.path.join(tempdir, '.vscode', 'tasks.json')) as tasks_j:
            tasks_d = json.load(tasks_j)
        tasks = tasks_d["tasks"]
        task_install = next(
            (task for task in tasks if task["label"] == "install && deploy-target %s" % recipe_id_pretty), None)
        self.assertIsNot(task_install, None)
        # execute only the bb_run_do_install script since the deploy would require e.g. Qemu running.
        i_and_d_script = "install_and_deploy_" + recipe_id
        i_and_d_script_path = os.path.join(
            self._workspace_scripts_dir(recipe_name), i_and_d_script)
        self.assertExists(i_and_d_script_path)
        del_script = "delete_package_dirs_" + recipe_id
        del_script_path = os.path.join(
            self._workspace_scripts_dir(recipe_name), del_script)
        self.assertExists(del_script_path)
        runCmd(del_script_path, cwd=tempdir)

    def _devtool_ide_sdk_qemu(self, tempdir, qemu, recipe_name, example_exe):
        """Verify deployment and execution in Qemu system work for one recipe.

        This function checks the entire SDK workflow: changing the code, recompiling
        it and deploying it back to Qemu, and checking that the changes have been
        incorporated into the provided binaries. It also runs the tests of the recipe.
        """
        recipe_id, _ = self._get_recipe_ids(recipe_name)
        i_and_d_script = "install_and_deploy_" + recipe_id
        install_deploy_cmd = os.path.join(
            self._workspace_scripts_dir(recipe_name), i_and_d_script)
        self.assertExists(install_deploy_cmd,
                          '%s script not found' % install_deploy_cmd)
        runCmd(install_deploy_cmd)

        MAGIC_STRING_ORIG = "Magic: 123456789"
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
        self.assertIn(MAGIC_STRING_ORIG, output)

        # Verify the unmodified ptests work
        status, output = qemu.run(ptest_cmd)
        self.assertEqual(status, 0, msg="%s failed: %s" % (ptest_cmd, output))
        self.assertIn("PASS: cpp-example-lib", output)

        # Verify remote debugging works
        self._gdb_cross_debugging(
            qemu, recipe_name, example_exe, MAGIC_STRING_ORIG)

        # Replace the Magic String in the code, compile and deploy to Qemu
        cpp_example_lib_hpp = os.path.join(tempdir, 'cpp-example-lib.hpp')
        with open(cpp_example_lib_hpp, 'r') as file:
            cpp_code = file.read()
            cpp_code = cpp_code.replace(MAGIC_STRING_ORIG, MAGIC_STRING_NEW)
        with open(cpp_example_lib_hpp, 'w') as file:
            file.write(cpp_code)
        runCmd(install_deploy_cmd, cwd=tempdir)

        # Verify the modified example prints the modified magic string
        status, output = qemu.run(example_exe)
        self.assertEqual(status, 0, msg="%s failed: %s" %
                         (example_exe, output))
        self.assertNotIn(MAGIC_STRING_ORIG, output)
        self.assertIn(MAGIC_STRING_NEW, output)

        # Verify the modified example ptests work
        status, output = qemu.run(ptest_cmd)
        self.assertEqual(status, 0, msg="%s failed: %s" % (ptest_cmd, output))
        self.assertIn("PASS: cpp-example-lib", output)

        # Verify remote debugging works wit the modified magic string
        self._gdb_cross_debugging(
            qemu, recipe_name, example_exe, MAGIC_STRING_NEW)

    def _gdb_cross(self):
        """Verify gdb-cross is provided by devtool ide-sdk"""
        target_arch = self.td["TARGET_ARCH"]
        target_sys = self.td["TARGET_SYS"]
        gdb_recipe = "gdb-cross-" + target_arch
        gdb_binary = target_sys + "-gdb"

        native_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", gdb_recipe)
        r = runCmd("%s --version" % gdb_binary,
                   native_sysroot=native_sysroot, target_sys=target_sys)
        self.assertEqual(r.status, 0)
        self.assertIn("GNU gdb", r.output)

    def _gdb_cross_debugging(self, qemu, recipe_name, example_exe, magic_string):
        """Verify gdb-cross is working

        Test remote debugging:
        break main
        run
        continue
        break CppExample::print_json()
        continue
        print CppExample::test_string.compare("cpp-example-lib Magic: 123456789")
        $1 = 0
        print CppExample::test_string.compare("cpp-example-lib Magic: 123456789aaa")
        $2 = -3
        list cpp-example-lib.hpp:13,13
        13	    inline static const std::string test_string = "cpp-example-lib Magic: 123456789";
        continue
        """
        sshargs = '-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
        gdbserver_script = os.path.join(self._workspace_scripts_dir(
            recipe_name), 'gdbserver_1234_usr-bin-' + example_exe + '_m')
        gdb_script = os.path.join(self._workspace_scripts_dir(
            recipe_name), 'gdb_1234_usr-bin-' + example_exe)

        # Start a gdbserver
        r = runCmd(gdbserver_script)
        self.assertEqual(r.status, 0)

        # Check there is a gdbserver running
        r = runCmd('ssh %s root@%s %s' % (sshargs, qemu.ip, 'ps'))
        self.assertEqual(r.status, 0)
        self.assertIn("gdbserver ", r.output)

        # Check the pid file is correct
        test_cmd = "cat /proc/$(cat /tmp/gdbserver_1234_usr-bin-" + \
            example_exe + "/pid)/cmdline"
        r = runCmd('ssh %s root@%s %s' % (sshargs, qemu.ip, test_cmd))
        self.assertEqual(r.status, 0)
        self.assertIn("gdbserver", r.output)

        # Test remote debugging works
        gdb_batch_cmd = " --batch -ex 'break main' -ex 'run'"
        gdb_batch_cmd += " -ex 'break CppExample::print_json()' -ex 'continue'"
        gdb_batch_cmd += " -ex 'print CppExample::test_string.compare(\"cpp-example-lib %s\")'" % magic_string
        gdb_batch_cmd += " -ex 'print CppExample::test_string.compare(\"cpp-example-lib %saaa\")'" % magic_string
        gdb_batch_cmd += " -ex 'list cpp-example-lib.hpp:13,13'"
        gdb_batch_cmd += " -ex 'continue'"
        r = runCmd(gdb_script + gdb_batch_cmd)
        self.logger.debug("%s %s returned: %s", gdb_script,
                          gdb_batch_cmd, r.output)
        self.assertEqual(r.status, 0)
        self.assertIn("Breakpoint 1, main", r.output)
        self.assertIn("$1 = 0", r.output)  # test.string.compare equal
        self.assertIn("$2 = -3", r.output)  # test.string.compare longer
        self.assertIn(
            'inline static const std::string test_string = "cpp-example-lib %s";' % magic_string, r.output)
        self.assertIn("exited normally", r.output)

        # Stop the gdbserver
        r = runCmd(gdbserver_script + ' stop')
        self.assertEqual(r.status, 0)

        # Check there is no gdbserver running
        r = runCmd('ssh %s root@%s %s' % (sshargs, qemu.ip, 'ps'))
        self.assertEqual(r.status, 0)
        self.assertNotIn("gdbserver ", r.output)

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

        # Verify the wrapper for cmake native is available
        self.assertExists(cmake_exe)

        # Verify the cmake preset generated by devtool ide-sdk is available
        result = runCmd('%s --list-presets' % cmake_exe, cwd=tempdir)
        self.assertIn(preset_name, result.output)

        # Verify cmake re-uses the o files compiled by bitbake
        result = runCmd('%s --build --preset %s' %
                        (cmake_exe, preset_name), cwd=tempdir)
        self.assertIn("ninja: no work to do.", result.output)

        # Verify the unit tests work (in Qemu user mode)
        result = runCmd('%s --build --preset %s --target test' %
                        (cmake_exe, preset_name), cwd=tempdir)
        self.assertIn("100% tests passed", result.output)

        # Verify re-building and testing works again
        result = runCmd('%s --build --preset %s --target clean' %
                        (cmake_exe, preset_name), cwd=tempdir)
        self.assertIn("Cleaning", result.output)
        result = runCmd('%s --build --preset %s' %
                        (cmake_exe, preset_name), cwd=tempdir)
        self.assertIn("Building", result.output)
        self.assertIn("Linking", result.output)
        result = runCmd('%s --build --preset %s --target test' %
                        (cmake_exe, preset_name), cwd=tempdir)
        self.assertIn("Running tests...", result.output)
        self.assertIn("100% tests passed", result.output)

    @OETestTag("runqemu")
    def test_devtool_ide_sdk_none_qemu(self):
        """Start qemu-system and run tests for multiple recipes. ide=none is used."""
        recipe_names = ["cmake-example", "meson-example"]
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config(recipe_names)
        self._check_runqemu_prerequisites()

        # Verify deployment to Qemu (system mode) works
        bitbake(testimage)
        with runqemu(testimage, runqemuparams="nographic") as qemu:
            # cmake-example recipe
            recipe_name = "cmake-example"
            example_exe = "cmake-example"
            build_file = "CMakeLists.txt"
            tempdir = self._devtool_ide_sdk_recipe(
                recipe_name, build_file, testimage)
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --ide=none' % (
                recipe_name, testimage, qemu.ip)
            runCmd(bitbake_sdk_cmd)
            self._gdb_cross()
            self._verify_cmake_preset(tempdir)
            self._devtool_ide_sdk_qemu(tempdir, qemu, recipe_name, example_exe)
            # Verify the oe-scripts sym-link is valid
            self.assertEqual(self._workspace_scripts_dir(
                recipe_name), self._sources_scripts_dir(tempdir))

            # meson-example recipe
            recipe_name = "meson-example"
            example_exe = "mesonex"
            build_file = "meson.build"
            tempdir = self._devtool_ide_sdk_recipe(
                recipe_name, build_file, testimage)
            bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@%s -c --ide=none' % (
                recipe_name, testimage, qemu.ip)
            runCmd(bitbake_sdk_cmd)
            self._gdb_cross()
            self._devtool_ide_sdk_qemu(tempdir, qemu, recipe_name, example_exe)
            # Verify the oe-scripts sym-link is valid
            self.assertEqual(self._workspace_scripts_dir(
                recipe_name), self._sources_scripts_dir(tempdir))

    def test_devtool_ide_sdk_code_cmake(self):
        """Verify a cmake recipe works with ide=code mode"""
        recipe_name = "cmake-example"
        build_file = "CMakeLists.txt"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config([recipe_name])
        tempdir = self._devtool_ide_sdk_recipe(
            recipe_name, build_file, testimage)
        bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@192.168.17.17 -c --ide=code' % (
            recipe_name, testimage)
        runCmd(bitbake_sdk_cmd)
        self._verify_cmake_preset(tempdir)
        self._verify_install_script_code(tempdir,  recipe_name)
        self._gdb_cross()

    def test_devtool_ide_sdk_code_meson(self):
        """Verify a meson recipe works with ide=code mode"""
        recipe_name = "meson-example"
        build_file = "meson.build"
        testimage = "oe-selftest-image"

        self._check_workspace()
        self._write_bb_config([recipe_name])
        tempdir = self._devtool_ide_sdk_recipe(
            recipe_name, build_file, testimage)
        bitbake_sdk_cmd = 'devtool ide-sdk %s %s -t root@192.168.17.17 -c --ide=code' % (
            recipe_name, testimage)
        runCmd(bitbake_sdk_cmd)

        with open(os.path.join(tempdir, '.vscode', 'settings.json')) as settings_j:
            settings_d = json.load(settings_j)
        meson_exe = settings_d["mesonbuild.mesonPath"]
        meson_build_folder = settings_d["mesonbuild.buildFolder"]

        # Verify the wrapper for meson native is available
        self.assertExists(meson_exe)

        # Verify meson re-uses the o files compiled by bitbake
        result = runCmd('%s compile -C  %s' %
                        (meson_exe, meson_build_folder), cwd=tempdir)
        self.assertIn("ninja: no work to do.", result.output)

        # Verify the unit tests work (in Qemu)
        runCmd('%s test -C %s' % (meson_exe, meson_build_folder), cwd=tempdir)

        # Verify re-building and testing works again
        result = runCmd('%s compile -C  %s --clean' %
                        (meson_exe, meson_build_folder), cwd=tempdir)
        self.assertIn("Cleaning...", result.output)
        result = runCmd('%s compile -C  %s' %
                        (meson_exe, meson_build_folder), cwd=tempdir)
        self.assertIn("Linking target", result.output)
        runCmd('%s test -C %s' % (meson_exe, meson_build_folder), cwd=tempdir)

        self._verify_install_script_code(tempdir,  recipe_name)
        self._gdb_cross()

    def test_devtool_ide_sdk_shared_sysroots(self):
        """Verify the shared sysroot SDK"""

        # Handle the workspace (which is not needed by this test case)
        self._check_workspace()

        result_init = runCmd(
            'devtool ide-sdk -m shared oe-selftest-image cmake-example meson-example --ide=code')
        bb_vars = get_bb_vars(
            ['REAL_MULTIMACH_TARGET_SYS', 'DEPLOY_DIR_IMAGE', 'COREBASE'], "meta-ide-support")
        environment_script = 'environment-setup-%s' % bb_vars['REAL_MULTIMACH_TARGET_SYS']
        deploydir = bb_vars['DEPLOY_DIR_IMAGE']
        environment_script_path = os.path.join(deploydir, environment_script)
        cpp_example_src = os.path.join(
            bb_vars['COREBASE'], 'meta-selftest', 'recipes-test', 'cpp', 'files')

        # Verify the cross environment script is available
        self.assertExists(environment_script_path)

        def runCmdEnv(cmd, cwd):
            cmd = '/bin/sh -c ". %s > /dev/null && %s"' % (
                environment_script_path, cmd)
            return runCmd(cmd, cwd)

        # Verify building the C++ example works with CMake
        tempdir_cmake = tempfile.mkdtemp(prefix='devtoolqa')
        self.track_for_cleanup(tempdir_cmake)

        result_cmake = runCmdEnv("which cmake", cwd=tempdir_cmake)
        cmake_native = os.path.normpath(result_cmake.output.strip())
        self.assertExists(cmake_native)

        runCmdEnv('cmake %s' % cpp_example_src, cwd=tempdir_cmake)
        runCmdEnv('cmake --build %s' % tempdir_cmake, cwd=tempdir_cmake)

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

        result_cmake = runCmdEnv("which meson", cwd=tempdir_meson)
        meson_native = os.path.normpath(result_cmake.output.strip())
        self.assertExists(meson_native)

        runCmdEnv('meson setup %s' % tempdir_meson, cwd=cpp_example_src)
        runCmdEnv('meson compile', cwd=tempdir_meson)

    def test_devtool_ide_sdk_plugins(self):
        """Test that devtool ide-sdk can use plugins from other layers."""

        # We need a workspace layer and a modified recipe (but no image)
        modified_recipe_name = "meson-example"
        modified_build_file = "meson.build"
        testimage = "oe-selftest-image"
        shared_recipe_name = "cmake-example"

        self._check_workspace()
        self._write_bb_config([modified_recipe_name])
        tempdir = self._devtool_ide_sdk_recipe(
            modified_recipe_name, modified_build_file, None)

        IDE_RE = re.compile(r'.*--ide \{(.*)\}.*')

        def get_ides_from_help(help_str):
            m = IDE_RE.search(help_str)
            return m.group(1).split(',')

        # verify the default plugins are available but the foo plugin is not
        result = runCmd('devtool ide-sdk -h')
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
        result = runCmd('devtool ide-sdk -h')
        found_ides = get_ides_from_help(result.output)
        self.assertIn('code', found_ides)
        self.assertIn('none', found_ides)
        self.assertIn('foo', found_ides)

        # Verify the foo plugin generates a shared config
        result = runCmd(
            'devtool ide-sdk -m shared --skip-bitbake --ide foo %s' % shared_recipe_name)
        with open(shared_config_file) as shared_config:
            shared_config_new = shared_config.read()
        self.assertEqual(shared_config_str, shared_config_new)

        # Verify the foo plugin generates a modified config
        result = runCmd('devtool ide-sdk --skip-bitbake --ide foo %s %s' %
                        (modified_recipe_name, testimage))
        with open(modified_config_file) as modified_config:
            modified_config_new = modified_config.read()
        self.assertEqual(modified_config_str, modified_config_new)
