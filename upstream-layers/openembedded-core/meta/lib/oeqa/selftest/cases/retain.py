# Tests for retain.bbclass
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import glob
import fnmatch
import oe.path
import shutil
import tarfile
from oeqa.utils.commands import bitbake, get_bb_vars
from oeqa.selftest.case import OESelftestTestCase

class Retain(OESelftestTestCase):

    def test_retain_always(self):
        """
        Summary:     Test retain class with RETAIN_DIRS_ALWAYS
        Expected:    Archive written to RETAIN_OUTDIR when build of test recipe completes
        Product:     oe-core
        Author:      Paul Eggleton <paul.eggleton@microsoft.com>
        """

        test_recipe = 'quilt-native'

        features = 'INHERIT += "retain"\n'
        features += 'RETAIN_DIRS_ALWAYS = "${T}"\n'
        self.write_config(features)

        bitbake('-c clean %s' % test_recipe)

        bb_vars = get_bb_vars(['RETAIN_OUTDIR', 'TMPDIR'])
        retain_outdir = bb_vars['RETAIN_OUTDIR'] or ''
        tmpdir = bb_vars['TMPDIR']
        if len(retain_outdir) < 5:
            self.fail('RETAIN_OUTDIR value "%s" is invalid' % retain_outdir)
        if not oe.path.is_path_parent(tmpdir, retain_outdir):
            self.fail('RETAIN_OUTDIR (%s) is not underneath TMPDIR (%s)' % (retain_outdir, tmpdir))
        try:
            shutil.rmtree(retain_outdir)
        except FileNotFoundError:
            pass

        bitbake(test_recipe)
        if not glob.glob(os.path.join(retain_outdir, '%s_temp_*.tar.gz' % test_recipe)):
            self.fail('No output archive for %s created' % test_recipe)


    def test_retain_failure(self):
        """
        Summary:     Test retain class default behaviour
        Expected:    Archive written to RETAIN_OUTDIR only when build of test
                     recipe fails, and archive contents are as expected
        Product:     oe-core
        Author:      Paul Eggleton <paul.eggleton@microsoft.com>
        """

        test_recipe_fail = 'error'

        features = 'INHERIT += "retain"\n'
        self.write_config(features)

        bb_vars = get_bb_vars(['RETAIN_OUTDIR', 'TMPDIR', 'RETAIN_DIRS_ALWAYS', 'RETAIN_DIRS_GLOBAL_ALWAYS'])
        if bb_vars['RETAIN_DIRS_ALWAYS']:
            self.fail('RETAIN_DIRS_ALWAYS is set, this interferes with the test')
        if bb_vars['RETAIN_DIRS_GLOBAL_ALWAYS']:
            self.fail('RETAIN_DIRS_GLOBAL_ALWAYS is set, this interferes with the test')
        retain_outdir = bb_vars['RETAIN_OUTDIR'] or ''
        tmpdir = bb_vars['TMPDIR']
        if len(retain_outdir) < 5:
            self.fail('RETAIN_OUTDIR value "%s" is invalid' % retain_outdir)
        if not oe.path.is_path_parent(tmpdir, retain_outdir):
            self.fail('RETAIN_OUTDIR (%s) is not underneath TMPDIR (%s)' % (retain_outdir, tmpdir))

        try:
            shutil.rmtree(retain_outdir)
        except FileNotFoundError:
            pass

        bitbake('-c clean %s' % test_recipe_fail)

        if os.path.exists(retain_outdir):
            retain_dirlist = os.listdir(retain_outdir)
            if retain_dirlist:
                self.fail('RETAIN_OUTDIR should be empty without failure, contents:\n%s' % '\n'.join(retain_dirlist))

        result = bitbake('-c compile %s' % test_recipe_fail, ignore_status=True)
        if result.status == 0:
            self.fail('Build of %s did not fail as expected' % test_recipe_fail)

        archives = glob.glob(os.path.join(retain_outdir, '%s_*.tar.gz' % test_recipe_fail))
        if not archives:
            self.fail('No output archive for %s created' % test_recipe_fail)
        if len(archives) > 1:
            self.fail('More than one archive for %s created' % test_recipe_fail)
        for archive in archives:
            found = False
            archive_prefix = os.path.basename(archive).split('.tar')[0]
            expected_prefix_start = '%s_workdir' % test_recipe_fail
            if not archive_prefix.startswith(expected_prefix_start):
                self.fail('Archive %s name does not start with expected prefix "%s"' % (os.path.basename(archive), expected_prefix_start))
            with tarfile.open(archive) as tf:
                for ti in tf:
                    if not fnmatch.fnmatch(ti.name, '%s/*' % archive_prefix):
                        self.fail('File without tarball-named subdirectory within tarball %s: %s' % (os.path.basename(archive), ti.name))
                    if ti.name.endswith('/temp/log.do_compile'):
                        found = True
            if not found:
                self.fail('Did not find log.do_compile in output archive %s' % os.path.basename(archive))


    def test_retain_global(self):
        """
        Summary:     Test retain class RETAIN_DIRS_GLOBAL_* behaviour
        Expected:    Ensure RETAIN_DIRS_GLOBAL_ALWAYS always causes an
                     archive to be created, and RETAIN_DIRS_GLOBAL_FAILURE
                     only causes an archive to be created on failure.
                     Also test archive naming (with : character) as an
                     added bonus.
        Product:     oe-core
        Author:      Paul Eggleton <paul.eggleton@microsoft.com>
        """

        test_recipe = 'quilt-native'
        test_recipe_fail = 'error'

        features = 'INHERIT += "retain"\n'
        features += 'RETAIN_DIRS_GLOBAL_ALWAYS = "${LOG_DIR};prefix=buildlogs"\n'
        features += 'RETAIN_DIRS_GLOBAL_FAILURE = "${STAMPS_DIR}"\n'
        self.write_config(features)

        bitbake('-c clean %s' % test_recipe)

        bb_vars = get_bb_vars(['RETAIN_OUTDIR', 'TMPDIR', 'STAMPS_DIR'])
        retain_outdir = bb_vars['RETAIN_OUTDIR'] or ''
        tmpdir = bb_vars['TMPDIR']
        if len(retain_outdir) < 5:
            self.fail('RETAIN_OUTDIR value "%s" is invalid' % retain_outdir)
        if not oe.path.is_path_parent(tmpdir, retain_outdir):
            self.fail('RETAIN_OUTDIR (%s) is not underneath TMPDIR (%s)' % (retain_outdir, tmpdir))
        try:
            shutil.rmtree(retain_outdir)
        except FileNotFoundError:
            pass

        # Test success case
        bitbake(test_recipe)
        if not glob.glob(os.path.join(retain_outdir, 'buildlogs_*.tar.gz')):
            self.fail('No output archive for LOG_DIR created')
        stamps_dir = bb_vars['STAMPS_DIR']
        if glob.glob(os.path.join(retain_outdir, '%s_*.tar.gz' % os.path.basename(stamps_dir))):
            self.fail('Output archive for STAMPS_DIR created when it should not have been')

        # Test failure case
        result = bitbake('-c compile %s' % test_recipe_fail, ignore_status=True)
        if result.status == 0:
            self.fail('Build of %s did not fail as expected' % test_recipe_fail)
        if not glob.glob(os.path.join(retain_outdir, '%s_*.tar.gz' % os.path.basename(stamps_dir))):
            self.fail('Output archive for STAMPS_DIR not created')
        if len(glob.glob(os.path.join(retain_outdir, 'buildlogs_*.tar.gz'))) != 2:
            self.fail('Should be exactly two buildlogs archives in output dir')


    def test_retain_misc(self):
        """
        Summary:     Test retain class with RETAIN_ENABLED and RETAIN_TARBALL_SUFFIX
        Expected:    Archive written to RETAIN_OUTDIR only when RETAIN_ENABLED is set
                     and archive contents are as expected. Also test archive naming
                     (with : character) as an added bonus.
        Product:     oe-core
        Author:      Paul Eggleton <paul.eggleton@microsoft.com>
        """

        test_recipe_fail = 'error'

        features = 'INHERIT += "retain"\n'
        features += 'RETAIN_DIRS_ALWAYS = "${T}"\n'
        features += 'RETAIN_ENABLED = "0"\n'
        self.write_config(features)

        bb_vars = get_bb_vars(['RETAIN_OUTDIR', 'TMPDIR'])
        retain_outdir = bb_vars['RETAIN_OUTDIR'] or ''
        tmpdir = bb_vars['TMPDIR']
        if len(retain_outdir) < 5:
            self.fail('RETAIN_OUTDIR value "%s" is invalid' % retain_outdir)
        if not oe.path.is_path_parent(tmpdir, retain_outdir):
            self.fail('RETAIN_OUTDIR (%s) is not underneath TMPDIR (%s)' % (retain_outdir, tmpdir))

        try:
            shutil.rmtree(retain_outdir)
        except FileNotFoundError:
            pass

        bitbake('-c clean %s' % test_recipe_fail)
        result = bitbake('-c compile %s' % test_recipe_fail, ignore_status=True)
        if result.status == 0:
            self.fail('Build of %s did not fail as expected' % test_recipe_fail)

        if os.path.exists(retain_outdir) and os.listdir(retain_outdir):
            self.fail('RETAIN_OUTDIR should be empty with RETAIN_ENABLED = "0"')

        features = 'INHERIT += "retain"\n'
        features += 'RETAIN_DIRS_ALWAYS = "${T};prefix=recipelogs"\n'
        features += 'RETAIN_TARBALL_SUFFIX = "${DATETIME}-testsuffix.tar.bz2"\n'
        features += 'RETAIN_ENABLED = "1"\n'
        self.write_config(features)

        result = bitbake('-c compile %s' % test_recipe_fail, ignore_status=True)
        if result.status == 0:
            self.fail('Build of %s did not fail as expected' % test_recipe_fail)

        archives = glob.glob(os.path.join(retain_outdir, '%s_*-testsuffix.tar.bz2' % test_recipe_fail))
        if not archives:
            self.fail('No output archive for %s created' % test_recipe_fail)
        if len(archives) != 2:
            self.fail('Two archives for %s expected, but %d exist' % (test_recipe_fail, len(archives)))
        recipelogs_found = False
        workdir_found = False
        for archive in archives:
            contents_found = False
            archive_prefix = os.path.basename(archive).split('.tar')[0]
            if archive_prefix.startswith('%s_recipelogs' % test_recipe_fail):
                recipelogs_found = True
            if archive_prefix.startswith('%s_workdir' % test_recipe_fail):
                workdir_found = True
            with tarfile.open(archive, 'r:bz2') as tf:
                for ti in tf:
                    if not fnmatch.fnmatch(ti.name, '%s/*' % (archive_prefix)):
                        self.fail('File without tarball-named subdirectory within tarball %s: %s' % (os.path.basename(archive), ti.name))
                    if ti.name.endswith('/log.do_compile'):
                        contents_found = True
            if not contents_found:
                # Both archives should contain this file
                self.fail('Did not find log.do_compile in output archive %s' % os.path.basename(archive))
        if not recipelogs_found:
            self.fail('No archive with expected "recipelogs" prefix found')
        if not workdir_found:
            self.fail('No archive with expected "workdir" prefix found')
