#
# SPDX-License-Identifier: MIT
#
# Copyright 2019 by Garmin Ltd. or its subsidiaries

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
import bb.utils
import functools
import multiprocessing
import textwrap
import json
import unittest
import tempfile
import shutil
import stat
import os

MISSING = 'MISSING'
DIFFERENT = 'DIFFERENT'
SAME = 'SAME'

@functools.total_ordering
class CompareResult(object):
    def __init__(self):
        self.reference = None
        self.test = None
        self.status = 'UNKNOWN'

    def __eq__(self, other):
        return (self.status, self.test) == (other.status, other.test)

    def __lt__(self, other):
        return (self.status, self.test) < (other.status, other.test)

class PackageCompareResults(object):
    def __init__(self):
        self.total = []
        self.missing = []
        self.different = []
        self.same = []

    def add_result(self, r):
        self.total.append(r)
        if r.status == MISSING:
            self.missing.append(r)
        elif r.status == DIFFERENT:
            self.different.append(r)
        else:
            self.same.append(r)

    def sort(self):
        self.total.sort()
        self.missing.sort()
        self.different.sort()
        self.same.sort()

    def __str__(self):
        return 'same=%i different=%i missing=%i total=%i' % (len(self.same), len(self.different), len(self.missing), len(self.total))

def compare_file(reference, test, diffutils_sysroot):
    result = CompareResult()
    result.reference = reference
    result.test = test

    if not os.path.exists(reference):
        result.status = MISSING
        return result

    r = runCmd(['cmp', '--quiet', reference, test], native_sysroot=diffutils_sysroot, ignore_status=True)

    if r.status:
        result.status = DIFFERENT
        return result

    result.status = SAME
    return result

class ReproducibleTests(OESelftestTestCase):
    package_classes = ['deb', 'ipk']
    images = ['core-image-minimal']
    save_results = False

    def setUpLocal(self):
        super().setUpLocal()
        needed_vars = ['TOPDIR', 'TARGET_PREFIX', 'BB_NUMBER_THREADS']
        bb_vars = get_bb_vars(needed_vars)
        for v in needed_vars:
            setattr(self, v.lower(), bb_vars[v])

        self.extrasresults = {}
        self.extrasresults.setdefault('reproducible.rawlogs', {})['log'] = ''
        self.extrasresults.setdefault('reproducible', {}).setdefault('files', {})

    def append_to_log(self, msg):
        self.extrasresults['reproducible.rawlogs']['log'] += msg

    def compare_packages(self, reference_dir, test_dir, diffutils_sysroot):
        result = PackageCompareResults()

        old_cwd = os.getcwd()
        try:
            file_result = {}
            os.chdir(test_dir)
            with multiprocessing.Pool(processes=int(self.bb_number_threads or 0)) as p:
                for root, dirs, files in os.walk('.'):
                    async_result = []
                    for f in files:
                        reference_path = os.path.join(reference_dir, root, f)
                        test_path = os.path.join(test_dir, root, f)
                        async_result.append(p.apply_async(compare_file, (reference_path, test_path, diffutils_sysroot)))

                    for a in async_result:
                        result.add_result(a.get())

        finally:
            os.chdir(old_cwd)

        result.sort()
        return result

    def write_package_list(self, package_class, name, packages):
        self.extrasresults['reproducible']['files'].setdefault(package_class, {})[name] = [
                {'reference': p.reference, 'test': p.test} for p in packages]

    def copy_file(self, source, dest):
        bb.utils.mkdirhier(os.path.dirname(dest))
        shutil.copyfile(source, dest)

    def test_reproducible_builds(self):
        capture_vars = ['DEPLOY_DIR_' + c.upper() for c in self.package_classes]

        if self.save_results:
            save_dir = tempfile.mkdtemp(prefix='oe-reproducible-')
            os.chmod(save_dir, stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)
            self.logger.info('Non-reproducible packages will be copied to %s', save_dir)

        # Build native utilities
        self.write_config('')
        bitbake("diffutils-native -c addto_recipe_sysroot")
        diffutils_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "diffutils-native")

        # Reproducible builds should not pull from sstate or mirrors, but
        # sharing DL_DIR is fine
        common_config = textwrap.dedent('''\
            INHERIT += "reproducible_build"
            PACKAGE_CLASSES = "%s"
            SSTATE_DIR = "${TMPDIR}/sstate"
            ''') % (' '.join('package_%s' % c for c in self.package_classes))

        # Perform a build.
        reproducibleA_tmp = os.path.join(self.topdir, 'reproducibleA', 'tmp')
        if os.path.exists(reproducibleA_tmp):
            bb.utils.remove(reproducibleA_tmp, recurse=True)

        self.write_config((textwrap.dedent('''\
            TMPDIR = "%s"
            ''') % reproducibleA_tmp) + common_config)
        vars_A = get_bb_vars(capture_vars)
        bitbake(' '.join(self.images))

        # Perform another build.
        reproducibleB_tmp = os.path.join(self.topdir, 'reproducibleB', 'tmp')
        if os.path.exists(reproducibleB_tmp):
            bb.utils.remove(reproducibleB_tmp, recurse=True)

        self.write_config((textwrap.dedent('''\
            SSTATE_MIRROR = ""
            TMPDIR = "%s"
            ''') % reproducibleB_tmp) + common_config)
        vars_B = get_bb_vars(capture_vars)
        bitbake(' '.join(self.images))

        # NOTE: The temp directories from the reproducible build are purposely
        # kept after the build so it can be diffed for debugging.

        for c in self.package_classes:
            with self.subTest(package_class=c):
                package_class = 'package_' + c

                deploy_A = vars_A['DEPLOY_DIR_' + c.upper()]
                deploy_B = vars_B['DEPLOY_DIR_' + c.upper()]

                result = self.compare_packages(deploy_A, deploy_B, diffutils_sysroot)

                self.logger.info('Reproducibility summary for %s: %s' % (c, result))

                self.append_to_log('\n'.join("%s: %s" % (r.status, r.test) for r in result.total))

                self.write_package_list(package_class, 'missing', result.missing)
                self.write_package_list(package_class, 'different', result.different)
                self.write_package_list(package_class, 'same', result.same)

                if self.save_results:
                    for d in result.different:
                        self.copy_file(d.reference, '/'.join([save_dir, d.reference]))
                        self.copy_file(d.test, '/'.join([save_dir, d.test]))

                if result.missing or result.different:
                    self.fail("The following %s packages are missing or different: %s" %
                            (c, ' '.join(r.test for r in (result.missing + result.different))))

