#
# SPDX-License-Identifier: MIT
#
# Copyright 2019-2020 by Garmin Ltd. or its subsidiaries

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
import bb.utils
import functools
import multiprocessing
import textwrap
import tempfile
import shutil
import stat
import os
import datetime

exclude_packages = [
	]

def is_excluded(package):
    package_name = os.path.basename(package)
    for i in exclude_packages:
        if package_name.startswith(i):
            return i
    return None

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
    def __init__(self, exclusions):
        self.total = []
        self.missing = []
        self.different = []
        self.different_excluded = []
        self.same = []
        self.active_exclusions = set()
        exclude_packages.extend((exclusions or "").split())

    def add_result(self, r):
        self.total.append(r)
        if r.status == MISSING:
            self.missing.append(r)
        elif r.status == DIFFERENT:
            exclusion = is_excluded(r.reference)
            if exclusion:
                self.different_excluded.append(r)
                self.active_exclusions.add(exclusion)
            else:
                self.different.append(r)
        else:
            self.same.append(r)

    def sort(self):
        self.total.sort()
        self.missing.sort()
        self.different.sort()
        self.different_excluded.sort()
        self.same.sort()

    def __str__(self):
        return 'same=%i different=%i different_excluded=%i missing=%i total=%i\nunused_exclusions=%s' % (len(self.same), len(self.different), len(self.different_excluded), len(self.missing), len(self.total), self.unused_exclusions())

    def unused_exclusions(self):
        return sorted(set(exclude_packages) - self.active_exclusions)

def compare_file(reference, test, diffutils_sysroot):
    result = CompareResult()
    result.reference = reference
    result.test = test

    if not os.path.exists(reference):
        result.status = MISSING
        return result

    r = runCmd(['cmp', '--quiet', reference, test], native_sysroot=diffutils_sysroot, ignore_status=True, sync=False)

    if r.status:
        result.status = DIFFERENT
        return result

    result.status = SAME
    return result

def run_diffoscope(a_dir, b_dir, html_dir, max_report_size=0, **kwargs):
    return runCmd(['diffoscope', '--no-default-limits', '--max-report-size', str(max_report_size),
                   '--exclude-directory-metadata', 'yes', '--html-dir', html_dir, a_dir, b_dir],
                **kwargs)

class DiffoscopeTests(OESelftestTestCase):
    diffoscope_test_files = os.path.join(os.path.dirname(os.path.abspath(__file__)), "diffoscope")

    def test_diffoscope(self):
        bitbake("diffoscope-native -c addto_recipe_sysroot")
        diffoscope_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "diffoscope-native")

        # Check that diffoscope doesn't return an error when the files compare
        # the same (a general check that diffoscope is working)
        with tempfile.TemporaryDirectory() as tmpdir:
            run_diffoscope('A', 'A', tmpdir,
                native_sysroot=diffoscope_sysroot, cwd=self.diffoscope_test_files)

        # Check that diffoscope generates an index.html file when the files are
        # different
        with tempfile.TemporaryDirectory() as tmpdir:
            r = run_diffoscope('A', 'B', tmpdir,
                native_sysroot=diffoscope_sysroot, ignore_status=True, cwd=self.diffoscope_test_files)

            self.assertNotEqual(r.status, 0, msg="diffoscope was successful when an error was expected")
            self.assertTrue(os.path.exists(os.path.join(tmpdir, 'index.html')), "HTML index not found!")

class ReproducibleTests(OESelftestTestCase):
    # Test the reproducibility of whatever is built between sstate_targets and targets

    package_classes = ['deb', 'ipk', 'rpm']

    # Maximum report size, in bytes
    max_report_size = 250 * 1024 * 1024

    # targets are the things we want to test the reproducibility of
    # Have to add the virtual targets manually for now as builds may or may not include them as they're exclude from world
    targets = ['core-image-minimal', 'core-image-sato', 'core-image-full-cmdline', 'core-image-weston', 'world', 'virtual/librpc', 'virtual/libsdl2', 'virtual/crypt']

    # sstate targets are things to pull from sstate to potentially cut build/debugging time
    sstate_targets = []

    save_results = False
    if 'OEQA_DEBUGGING_SAVED_OUTPUT' in os.environ:
        save_results = os.environ['OEQA_DEBUGGING_SAVED_OUTPUT']

    # This variable controls if one of the test builds is allowed to pull from
    # an sstate cache/mirror. The other build is always done clean as a point of
    # comparison.
    # If you know that your sstate archives are reproducible, enabling this
    # will test that and also make the test run faster. If your sstate is not
    # reproducible, disable this in your derived test class
    build_from_sstate = True

    def setUpLocal(self):
        super().setUpLocal()
        needed_vars = [
            'TOPDIR',
            'TARGET_PREFIX',
            'BB_NUMBER_THREADS',
            'BB_HASHSERVE',
            'OEQA_REPRODUCIBLE_TEST_PACKAGE',
            'OEQA_REPRODUCIBLE_TEST_TARGET',
            'OEQA_REPRODUCIBLE_TEST_SSTATE_TARGETS',
            'OEQA_REPRODUCIBLE_EXCLUDED_PACKAGES',
            'OEQA_REPRODUCIBLE_TEST_LEAF_TARGETS',
        ]
        bb_vars = get_bb_vars(needed_vars)
        for v in needed_vars:
            setattr(self, v.lower(), bb_vars[v])

        if bb_vars['OEQA_REPRODUCIBLE_TEST_PACKAGE']:
            self.package_classes = bb_vars['OEQA_REPRODUCIBLE_TEST_PACKAGE'].split()

        if bb_vars['OEQA_REPRODUCIBLE_TEST_TARGET'] or bb_vars['OEQA_REPRODUCIBLE_TEST_LEAF_TARGETS']:
            self.targets = (bb_vars['OEQA_REPRODUCIBLE_TEST_TARGET'] or "").split() + (bb_vars['OEQA_REPRODUCIBLE_TEST_LEAF_TARGETS'] or "").split()

        if bb_vars['OEQA_REPRODUCIBLE_TEST_SSTATE_TARGETS']:
            self.sstate_targets = bb_vars['OEQA_REPRODUCIBLE_TEST_SSTATE_TARGETS'].split()

        if bb_vars['OEQA_REPRODUCIBLE_TEST_LEAF_TARGETS']:
            # Setup to build every DEPENDS of leaf recipes using sstate
            for leaf_recipe in bb_vars['OEQA_REPRODUCIBLE_TEST_LEAF_TARGETS'].split():
                self.sstate_targets.extend(get_bb_var('DEPENDS', leaf_recipe).split())

        self.extraresults = {}
        self.extraresults.setdefault('reproducible', {}).setdefault('files', {})

    def compare_packages(self, reference_dir, test_dir, diffutils_sysroot):
        result = PackageCompareResults(self.oeqa_reproducible_excluded_packages)

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
        self.extraresults['reproducible']['files'].setdefault(package_class, {})[name] = [
                p.reference.split("/./")[1] for p in packages]

    def copy_file(self, source, dest):
        bb.utils.mkdirhier(os.path.dirname(dest))
        shutil.copyfile(source, dest)

    def do_test_build(self, name, use_sstate):
        capture_vars = ['DEPLOY_DIR_' + c.upper() for c in self.package_classes]

        tmpdir = os.path.join(self.topdir, name, 'tmp')
        if os.path.exists(tmpdir):
            bb.utils.remove(tmpdir, recurse=True)
        config = textwrap.dedent('''\
            PACKAGE_CLASSES = "{package_classes}"
            TMPDIR = "{tmpdir}"
            LICENSE_FLAGS_ACCEPTED = "commercial"
            DISTRO_FEATURES:append = ' pam'
            USERADDEXTENSION = "useradd-staticids"
            USERADD_ERROR_DYNAMIC = "skip"
            USERADD_UID_TABLES += "files/static-passwd"
            USERADD_GID_TABLES += "files/static-group"
            ''').format(package_classes=' '.join('package_%s' % c for c in self.package_classes),
                        tmpdir=tmpdir)

        # Export BB_CONSOLELOG to the calling function and make it constant to
        # avoid a case where bitbake would get a timestamp-based filename but
        # oe-selftest would, later, get another.
        capture_vars.append("BB_CONSOLELOG")
        config += 'BB_CONSOLELOG = "${LOG_DIR}/cooker/${MACHINE}/console.log"\n'

        # We want different log files for each build, but a persistent bitbake
        # may reuse the previous log file so restart the bitbake server.
        bitbake("--kill-server")

        def print_condensed_error_log(logs, context_lines=10, tail_lines=20):
            """Prints errors with context and the end of the log."""

            logs = logs.split("\n")
            for i, line in enumerate(logs):
                if line.startswith("ERROR"):
                    self.logger.info("Found ERROR (line %d):" % (i + 1))
                    for l in logs[i-context_lines:i+context_lines]:
                        self.logger.info("      " + l)

            self.logger.info("End of log:")
            for l in logs[-tail_lines:]:
                self.logger.info("      " + l)

        bitbake_failure_count = 0
        if not use_sstate:
            if self.sstate_targets:
               self.logger.info("Building prebuild for %s (sstate allowed)..." % (name))
               self.write_config(config)
               try:
                   bitbake("--continue "+' '.join(self.sstate_targets))
               except AssertionError as e:
                   bitbake_failure_count += 1
                   self.logger.error("Bitbake failed! but keep going... Log:")
                   print_condensed_error_log(str(e))

            # This config fragment will disable using shared and the sstate
            # mirror, forcing a complete build from scratch
            config += textwrap.dedent('''\
                SSTATE_DIR = "${TMPDIR}/sstate"
                SSTATE_MIRRORS = "file://.*/.*-native.*  http://sstate.yoctoproject.org/all/PATH;downloadfilename=PATH file://.*/.*-cross.*  http://sstate.yoctoproject.org/all/PATH;downloadfilename=PATH"
                ''')

        self.logger.info("Building %s (sstate%s allowed)..." % (name, '' if use_sstate else ' NOT'))
        self.write_config(config)
        d = get_bb_vars(capture_vars)
        try:
            # targets used to be called images
            bitbake("--continue "+' '.join(getattr(self, 'images', self.targets)))
        except AssertionError as e:
            bitbake_failure_count += 1
            self.logger.error("Bitbake failed! but keep going... Log:")
            print_condensed_error_log(str(e))

            # The calling function expects the existence of the deploy
            # directories containing the packages.
            # If bitbake failed to create them, do it manually
            for c in self.package_classes:
                deploy = d['DEPLOY_DIR_' + c.upper()]
                if not os.path.exists(deploy):
                    self.logger.info("Manually creating %s" % deploy)
                    bb.utils.mkdirhier(deploy)

        return (d, bitbake_failure_count)

    def test_reproducible_builds(self):
        def strip_topdir(s):
            if s.startswith(self.topdir):
                return s[len(self.topdir):]
            return s

        # Build native utilities
        self.write_config('')
        bitbake("diffoscope-native diffutils-native jquery-native -c addto_recipe_sysroot")
        diffutils_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "diffutils-native")
        diffoscope_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "diffoscope-native")
        jquery_sysroot = get_bb_var("RECIPE_SYSROOT_NATIVE", "jquery-native")

        if self.save_results:
            os.makedirs(self.save_results, exist_ok=True)
            datestr = datetime.datetime.now().strftime('%Y%m%d')
            save_dir = tempfile.mkdtemp(prefix='oe-reproducible-%s-' % datestr, dir=self.save_results)
            os.chmod(save_dir, stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)
            self.logger.info('Non-reproducible packages will be copied to %s', save_dir)

        # The below bug shows that a few reproducible issues are depends on build dir path length.
        # https://bugzilla.yoctoproject.org/show_bug.cgi?id=15554
        # So, the reproducibleA & reproducibleB directories are changed to reproducibleA & reproducibleB-extended to have different size.

        fails = []
        vars_list = [None, None]

        for i, (name, use_sstate) in enumerate(
                                 (('reproducibleA', self.build_from_sstate),
                                 ('reproducibleB-extended', False))):
            (variables, bitbake_failure_count) = self.do_test_build(name, use_sstate)
            if bitbake_failure_count > 0:
                self.logger.error('%s build failed. Trying to compute built packages differences but the test will fail.' % name)
                fails.append("Bitbake %s failure" % name)
                if self.save_results:
                    failure_log_path = os.path.join(save_dir, "bitbake-%s.log" % name)
                    self.logger.info('Failure log for %s will be copied to %s'% (name, failure_log_path))
                    self.copy_file(variables["BB_CONSOLELOG"], failure_log_path)
            vars_list[i] = variables

        vars_A, vars_B = vars_list
        # NOTE: The temp directories from the reproducible build are purposely
        # kept after the build so it can be diffed for debugging.

        for c in self.package_classes:
            with self.subTest(package_class=c):
                package_class = 'package_' + c

                deploy_A = vars_A['DEPLOY_DIR_' + c.upper()]
                deploy_B = vars_B['DEPLOY_DIR_' + c.upper()]

                self.logger.info('Checking %s packages for differences...' % c)
                result = self.compare_packages(deploy_A, deploy_B, diffutils_sysroot)

                self.logger.info('Reproducibility summary for %s: %s' % (c, result))

                self.write_package_list(package_class, 'missing', result.missing)
                self.write_package_list(package_class, 'different', result.different)
                self.write_package_list(package_class, 'different_excluded', result.different_excluded)
                self.write_package_list(package_class, 'same', result.same)

                if self.save_results:
                    for d in result.different:
                        self.copy_file(d.reference, '/'.join([save_dir, 'packages', strip_topdir(d.reference)]))
                        self.copy_file(d.test, '/'.join([save_dir, 'packages', strip_topdir(d.test)]))

                    for d in result.different_excluded:
                        self.copy_file(d.reference, '/'.join([save_dir, 'packages-excluded', strip_topdir(d.reference)]))
                        self.copy_file(d.test, '/'.join([save_dir, 'packages-excluded', strip_topdir(d.test)]))

                if result.different:
                    fails.append("The following %s packages are different and not in exclusion list:\n%s" %
                            (c, '\n'.join(r.test for r in (result.different))))

                if result.missing and len(self.sstate_targets) == 0:
                    fails.append("The following %s packages are missing and not in exclusion list:\n%s" %
                            (c, '\n'.join(r.test for r in (result.missing))))

        # Clean up empty directories
        if self.save_results:
            if not os.listdir(save_dir):
                os.rmdir(save_dir)
            else:
                self.logger.info('Running diffoscope')
                package_dir = os.path.join(save_dir, 'packages')
                package_html_dir = os.path.join(package_dir, 'diff-html')

                # Copy jquery to improve the diffoscope output usability
                self.copy_file(os.path.join(jquery_sysroot, 'usr/share/javascript/jquery/jquery.min.js'), os.path.join(package_html_dir, 'jquery.js'))

                run_diffoscope('reproducibleA', 'reproducibleB-extended', package_html_dir, max_report_size=self.max_report_size,
                        native_sysroot=diffoscope_sysroot, ignore_status=True, cwd=package_dir)

        if fails:
            self.fail('\n'.join(fails))

