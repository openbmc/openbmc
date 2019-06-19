#
# SPDX-License-Identifier: MIT
#
# Copyright 2019 by Garmin Ltd. or its subsidiaries

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
import functools
import multiprocessing
import textwrap
import unittest

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
    package_classes = ['deb']
    images = ['core-image-minimal']

    def setUpLocal(self):
        super().setUpLocal()
        needed_vars = ['TOPDIR', 'TARGET_PREFIX', 'BB_NUMBER_THREADS']
        bb_vars = get_bb_vars(needed_vars)
        for v in needed_vars:
            setattr(self, v.lower(), bb_vars[v])

        if not hasattr(self.tc, "extraresults"):
            self.tc.extraresults = {}
        self.extras = self.tc.extraresults

        self.extras.setdefault('reproducible.rawlogs', {})['log'] = ''

    def append_to_log(self, msg):
        self.extras['reproducible.rawlogs']['log'] += msg

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

    @unittest.skip("Reproducible builds do not yet pass")
    def test_reproducible_builds(self):
        capture_vars = ['DEPLOY_DIR_' + c.upper() for c in self.package_classes]

        common_config = textwrap.dedent('''\
            INHERIT += "reproducible_build"
            PACKAGE_CLASSES = "%s"
            ''') % (' '.join('package_%s' % c for c in self.package_classes))

        # Do an initial build. It's acceptable for this build to use sstate
        self.write_config(common_config)
        vars_reference = get_bb_vars(capture_vars)
        bitbake(' '.join(self.images))

        # Build native utilities
        bitbake("diffutils-native -c addto_recipe_sysroot")
        diffutils_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "diffutils-native")

        # Perform another build. This build should *not* share sstate or pull
        # from any mirrors, but sharing a DL_DIR is fine
        self.write_config(textwrap.dedent('''\
            TMPDIR = "${TOPDIR}/reproducible/tmp"
            SSTATE_DIR = "${TMPDIR}/sstate"
            SSTATE_MIRROR = ""
            ''') + common_config)
        vars_test = get_bb_vars(capture_vars)
        bitbake(' '.join(self.images))

        for c in self.package_classes:
            package_class = 'package_' + c

            deploy_reference = vars_reference['DEPLOY_DIR_' + c.upper()]
            deploy_test = vars_test['DEPLOY_DIR_' + c.upper()]

            result = self.compare_packages(deploy_reference, deploy_test, diffutils_sysroot)

            self.logger.info('Reproducibility summary for %s: %s' % (c, result))

            self.append_to_log('\n'.join("%s: %s" % (r.status, r.test) for r in result.total))

            if result.missing or result.different:
                self.fail("The following %s packages are missing or different: %s" %
                        (c, ' '.join(r.test for r in (result.missing + result.different))))

