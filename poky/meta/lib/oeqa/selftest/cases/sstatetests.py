#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import glob
import subprocess
import tempfile
import datetime
import re

from oeqa.utils.commands import runCmd, bitbake, get_bb_var, create_temp_layer, get_bb_vars
from oeqa.selftest.case import OESelftestTestCase
from oeqa.core.decorator import OETestTag

import oe
import bb.siggen

# Set to True to preserve stamp files after test execution for debugging failures
keep_temp_files = False

class SStateBase(OESelftestTestCase):

    def setUpLocal(self):
        super(SStateBase, self).setUpLocal()
        self.temp_sstate_location = None
        needed_vars = ['SSTATE_DIR', 'NATIVELSBSTRING', 'TCLIBC', 'TUNE_ARCH',
                       'TOPDIR', 'TARGET_VENDOR', 'TARGET_OS']
        bb_vars = get_bb_vars(needed_vars)
        self.sstate_path = bb_vars['SSTATE_DIR']
        self.hostdistro = bb_vars['NATIVELSBSTRING']
        self.tclibc = bb_vars['TCLIBC']
        self.tune_arch = bb_vars['TUNE_ARCH']
        self.topdir = bb_vars['TOPDIR']
        self.target_vendor = bb_vars['TARGET_VENDOR']
        self.target_os = bb_vars['TARGET_OS']
        self.distro_specific_sstate = os.path.join(self.sstate_path, self.hostdistro)

    def track_for_cleanup(self, path):
        if not keep_temp_files:
            super().track_for_cleanup(path)

    # Creates a special sstate configuration with the option to add sstate mirrors
    def config_sstate(self, temp_sstate_location=False, add_local_mirrors=[]):
        self.temp_sstate_location = temp_sstate_location

        if self.temp_sstate_location:
            temp_sstate_path = os.path.join(self.builddir, "temp_sstate_%s" % datetime.datetime.now().strftime('%Y%m%d%H%M%S'))
            config_temp_sstate = "SSTATE_DIR = \"%s\"" % temp_sstate_path
            self.append_config(config_temp_sstate)
            self.track_for_cleanup(temp_sstate_path)
        bb_vars = get_bb_vars(['SSTATE_DIR', 'NATIVELSBSTRING'])
        self.sstate_path = bb_vars['SSTATE_DIR']
        self.hostdistro = bb_vars['NATIVELSBSTRING']
        self.distro_specific_sstate = os.path.join(self.sstate_path, self.hostdistro)

        if add_local_mirrors:
            config_set_sstate_if_not_set = 'SSTATE_MIRRORS ?= ""'
            self.append_config(config_set_sstate_if_not_set)
            for local_mirror in add_local_mirrors:
                self.assertFalse(os.path.join(local_mirror) == os.path.join(self.sstate_path), msg='Cannot add the current sstate path as a sstate mirror')
                config_sstate_mirror = "SSTATE_MIRRORS += \"file://.* file:///%s/PATH\"" % local_mirror
                self.append_config(config_sstate_mirror)

    # Returns a list containing sstate files
    def search_sstate(self, filename_regex, distro_specific=True, distro_nonspecific=True):
        result = []
        for root, dirs, files in os.walk(self.sstate_path):
            if distro_specific and re.search(r"%s/%s/[a-z0-9]{2}/[a-z0-9]{2}$" % (self.sstate_path, self.hostdistro), root):
                for f in files:
                    if re.search(filename_regex, f):
                        result.append(f)
            if distro_nonspecific and re.search(r"%s/[a-z0-9]{2}/[a-z0-9]{2}$" % self.sstate_path, root):
                for f in files:
                    if re.search(filename_regex, f):
                        result.append(f)
        return result

    # Test sstate files creation and their location and directory perms
    def run_test_sstate_creation(self, targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True, should_pass=True):
        self.config_sstate(temp_sstate_location, [self.sstate_path])

        if  self.temp_sstate_location:
            bitbake(['-cclean'] + targets)
        else:
            bitbake(['-ccleansstate'] + targets)

        # We need to test that the env umask have does not effect sstate directory creation
        # So, first, we'll get the current umask and set it to something we know incorrect
        # See: sstate_task_postfunc for correct umask of os.umask(0o002)
        import os
        def current_umask():
            current_umask = os.umask(0)
            os.umask(current_umask)
            return current_umask

        orig_umask = current_umask()
        # Set it to a umask we know will be 'wrong'
        os.umask(0o022)

        bitbake(targets)
        file_tracker = []
        results = self.search_sstate('|'.join(map(str, targets)), distro_specific, distro_nonspecific)
        if distro_nonspecific:
            for r in results:
                if r.endswith(("_populate_lic.tar.zst", "_populate_lic.tar.zst.siginfo", "_fetch.tar.zst.siginfo", "_unpack.tar.zst.siginfo", "_patch.tar.zst.siginfo")):
                    continue
                file_tracker.append(r)
        else:
            file_tracker = results

        if should_pass:
            self.assertTrue(file_tracker , msg="Could not find sstate files for: %s" % ', '.join(map(str, targets)))
        else:
            self.assertTrue(not file_tracker , msg="Found sstate files in the wrong place for: %s (found %s)" % (', '.join(map(str, targets)), str(file_tracker)))

        # Now we'll walk the tree to check the mode and see if things are incorrect.
        badperms = []
        for root, dirs, files in os.walk(self.sstate_path):
            for directory in dirs:
                if (os.stat(os.path.join(root, directory)).st_mode & 0o777) != 0o775:
                    badperms.append(os.path.join(root, directory))

        # Return to original umask
        os.umask(orig_umask)

        if should_pass:
            self.assertTrue(badperms , msg="Found sstate directories with the wrong permissions: %s (found %s)" % (', '.join(map(str, targets)), str(badperms)))

    # Test the sstate files deletion part of the do_cleansstate task
    def run_test_cleansstate_task(self, targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True):
        self.config_sstate(temp_sstate_location, [self.sstate_path])

        bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        archives_created = self.search_sstate('|'.join(map(str, [s + r'.*?\.tar.zst$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(archives_created, msg="Could not find sstate .tar.zst files for: %s (%s)" % (', '.join(map(str, targets)), str(archives_created)))

        siginfo_created = self.search_sstate('|'.join(map(str, [s + r'.*?\.siginfo$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(siginfo_created, msg="Could not find sstate .siginfo files for: %s (%s)" % (', '.join(map(str, targets)), str(siginfo_created)))

        bitbake(['-ccleansstate'] + targets)
        archives_removed = self.search_sstate('|'.join(map(str, [s + r'.*?\.tar.zst$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(not archives_removed, msg="do_cleansstate didn't remove .tar.zst sstate files for: %s (%s)" % (', '.join(map(str, targets)), str(archives_removed)))

    # Test rebuilding of distro-specific sstate files
    def run_test_rebuild_distro_specific_sstate(self, targets, temp_sstate_location=True):
        self.config_sstate(temp_sstate_location, [self.sstate_path])

        bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        results = self.search_sstate('|'.join(map(str, [s + r'.*?\.tar.zst$' for s in targets])), distro_specific=False, distro_nonspecific=True)
        filtered_results = []
        for r in results:
            if r.endswith(("_populate_lic.tar.zst", "_populate_lic.tar.zst.siginfo")):
                continue
            filtered_results.append(r)
        self.assertTrue(filtered_results == [], msg="Found distro non-specific sstate for: %s (%s)" % (', '.join(map(str, targets)), str(filtered_results)))
        file_tracker_1 = self.search_sstate('|'.join(map(str, [s + r'.*?\.tar.zst$' for s in targets])), distro_specific=True, distro_nonspecific=False)
        self.assertTrue(len(file_tracker_1) >= len(targets), msg = "Not all sstate files were created for: %s" % ', '.join(map(str, targets)))

        self.track_for_cleanup(self.distro_specific_sstate + "_old")
        shutil.copytree(self.distro_specific_sstate, self.distro_specific_sstate + "_old")
        shutil.rmtree(self.distro_specific_sstate)

        bitbake(['-cclean'] + targets)
        bitbake(targets)
        file_tracker_2 = self.search_sstate('|'.join(map(str, [s + r'.*?\.tar.zst$' for s in targets])), distro_specific=True, distro_nonspecific=False)
        self.assertTrue(len(file_tracker_2) >= len(targets), msg = "Not all sstate files were created for: %s" % ', '.join(map(str, targets)))

        not_recreated = [x for x in file_tracker_1 if x not in file_tracker_2]
        self.assertTrue(not_recreated == [], msg="The following sstate files were not recreated: %s" % ', '.join(map(str, not_recreated)))

        created_once = [x for x in file_tracker_2 if x not in file_tracker_1]
        self.assertTrue(created_once == [], msg="The following sstate files were created only in the second run: %s" % ', '.join(map(str, created_once)))

    def sstate_common_samesigs(self, configA, configB, allarch=False):

        self.write_config(configA)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("world meta-toolchain -S none")
        self.write_config(configB)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("world meta-toolchain -S none")

        def get_files(d, result):
            for root, dirs, files in os.walk(d):
                for name in files:
                    if "meta-environment" in root or "cross-canadian" in root:
                        continue
                    if "do_build" not in name:
                        # 1.4.1+gitAUTOINC+302fca9f4c-r0.do_package_write_ipk.sigdata.f3a2a38697da743f0dbed8b56aafcf79
                        (_, task, _, shash) = name.rsplit(".", 3)
                        result[os.path.join(os.path.basename(root), task)] = shash

        files1 = {}
        files2 = {}
        subdirs = sorted(glob.glob(self.topdir + "/tmp-sstatesamehash/stamps/*-nativesdk*-linux"))
        if allarch:
            subdirs.extend(sorted(glob.glob(self.topdir + "/tmp-sstatesamehash/stamps/all-*-linux")))

        for subdir in subdirs:
            nativesdkdir = os.path.basename(subdir)
            get_files(self.topdir + "/tmp-sstatesamehash/stamps/" + nativesdkdir, files1)
            get_files(self.topdir + "/tmp-sstatesamehash2/stamps/" + nativesdkdir, files2)

        self.maxDiff = None
        self.assertEqual(files1, files2)

class SStateTests(SStateBase):
    def test_autorev_sstate_works(self):
        # Test that a git repository which changes is correctly handled by SRCREV = ${AUTOREV}

        tempdir = tempfile.mkdtemp(prefix='sstate_autorev')
        tempdldir = tempfile.mkdtemp(prefix='sstate_autorev_dldir')
        self.track_for_cleanup(tempdir)
        self.track_for_cleanup(tempdldir)
        create_temp_layer(tempdir, 'selftestrecipetool')
        self.add_command_to_tearDown('bitbake-layers remove-layer %s' % tempdir)
        self.append_config("DL_DIR = \"%s\"" % tempdldir)
        runCmd('bitbake-layers add-layer %s' % tempdir)

        # Use dbus-wait as a local git repo we can add a commit between two builds in
        pn = 'dbus-wait'
        srcrev = '6cc6077a36fe2648a5f993fe7c16c9632f946517'
        url = 'git://git.yoctoproject.org/dbus-wait'
        result = runCmd('git clone %s noname' % url, cwd=tempdir)
        srcdir = os.path.join(tempdir, 'noname')
        result = runCmd('git reset --hard %s' % srcrev, cwd=srcdir)
        self.assertTrue(os.path.isfile(os.path.join(srcdir, 'configure.ac')), 'Unable to find configure script in source directory')

        recipefile = os.path.join(tempdir, "recipes-test", "dbus-wait-test", 'dbus-wait-test_git.bb')
        os.makedirs(os.path.dirname(recipefile))
        srcuri = 'git://' + srcdir + ';protocol=file;branch=master'
        result = runCmd(['recipetool', 'create', '-o', recipefile, srcuri])
        self.assertTrue(os.path.isfile(recipefile), 'recipetool did not create recipe file; output:\n%s' % result.output)

        with open(recipefile, 'a') as f:
            f.write('SRCREV = "${AUTOREV}"\n')
            f.write('PV = "1.0"\n')

        bitbake("dbus-wait-test -c fetch")
        with open(os.path.join(srcdir, "bar.txt"), "w") as f:
            f.write("foo")
        result = runCmd('git add bar.txt; git commit -asm "add bar"', cwd=srcdir)
        bitbake("dbus-wait-test -c unpack")

class SStateCreation(SStateBase):
    def test_sstate_creation_distro_specific_pass(self):
        self.run_test_sstate_creation(['binutils-cross-'+ self.tune_arch, 'binutils-native'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)

    def test_sstate_creation_distro_specific_fail(self):
        self.run_test_sstate_creation(['binutils-cross-'+ self.tune_arch, 'binutils-native'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True, should_pass=False)

    def test_sstate_creation_distro_nonspecific_pass(self):
        self.run_test_sstate_creation(['linux-libc-headers'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    def test_sstate_creation_distro_nonspecific_fail(self):
        self.run_test_sstate_creation(['linux-libc-headers'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True, should_pass=False)

class SStateCleanup(SStateBase):
    def test_cleansstate_task_distro_specific_nonspecific(self):
        targets = ['binutils-cross-'+ self.tune_arch, 'binutils-native']
        targets.append('linux-libc-headers')
        self.run_test_cleansstate_task(targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True)

    def test_cleansstate_task_distro_nonspecific(self):
        self.run_test_cleansstate_task(['linux-libc-headers'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    def test_cleansstate_task_distro_specific(self):
        targets = ['binutils-cross-'+ self.tune_arch, 'binutils-native']
        targets.append('linux-libc-headers')
        self.run_test_cleansstate_task(targets, distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)

class SStateDistroTests(SStateBase):
    def test_rebuild_distro_specific_sstate_cross_native_targets(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + self.tune_arch, 'binutils-native'], temp_sstate_location=True)

    def test_rebuild_distro_specific_sstate_cross_target(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + self.tune_arch], temp_sstate_location=True)

    def test_rebuild_distro_specific_sstate_native_target(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-native'], temp_sstate_location=True)

class SStateCacheManagement(SStateBase):
    # Test the sstate-cache-management script. Each element in the global_config list is used with the corresponding element in the target_config list
    # global_config elements are expected to not generate any sstate files that would be removed by sstate-cache-management.py (such as changing the value of MACHINE)
    def run_test_sstate_cache_management_script(self, target, global_config=[''], target_config=[''], ignore_patterns=[]):
        self.assertTrue(global_config)
        self.assertTrue(target_config)
        self.assertTrue(len(global_config) == len(target_config), msg='Lists global_config and target_config should have the same number of elements')

        for idx in range(len(target_config)):
            self.append_config(global_config[idx])
            self.append_recipeinc(target, target_config[idx])
            bitbake(target)
            self.remove_config(global_config[idx])
            self.remove_recipeinc(target, target_config[idx])

        self.config_sstate(temp_sstate_location=True, add_local_mirrors=[self.sstate_path])

        # For now this only checks if random sstate tasks are handled correctly as a group.
        # In the future we should add control over what tasks we check for.

        expected_remaining_sstate = []
        for idx in range(len(target_config)):
            self.append_config(global_config[idx])
            self.append_recipeinc(target, target_config[idx])
            if target_config[idx] == target_config[-1]:
                target_sstate_before_build = self.search_sstate(target + r'.*?\.tar.zst$')
            bitbake("-cclean %s" % target)
            result = bitbake(target, ignore_status=True)
            if target_config[idx] == target_config[-1]:
                target_sstate_after_build = self.search_sstate(target + r'.*?\.tar.zst$')
                expected_remaining_sstate += [x for x in target_sstate_after_build if x not in target_sstate_before_build if not any(pattern in x for pattern in ignore_patterns)]
            self.remove_config(global_config[idx])
            self.remove_recipeinc(target, target_config[idx])
            self.assertEqual(result.status, 0, msg = "build of %s failed with %s" % (target, result.output))

        runCmd("sstate-cache-management.py -y --cache-dir=%s --remove-duplicated" % (self.sstate_path))
        actual_remaining_sstate = [x for x in self.search_sstate(target + r'.*?\.tar.zst$') if not any(pattern in x for pattern in ignore_patterns)]

        actual_not_expected = [x for x in actual_remaining_sstate if x not in expected_remaining_sstate]
        self.assertFalse(actual_not_expected, msg="Files should have been removed but were not: %s" % ', '.join(map(str, actual_not_expected)))
        expected_not_actual = [x for x in expected_remaining_sstate if x not in actual_remaining_sstate]
        self.assertFalse(expected_not_actual, msg="Extra files were removed: %s" ', '.join(map(str, expected_not_actual)))

    def test_sstate_cache_management_script_using_pr_1(self):
        global_config = []
        target_config = []
        global_config.append('')
        target_config.append('PR = "0"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    def test_sstate_cache_management_script_using_pr_2(self):
        global_config = []
        target_config = []
        global_config.append('')
        target_config.append('PR = "0"')
        global_config.append('')
        target_config.append('PR = "1"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    def test_sstate_cache_management_script_using_pr_3(self):
        global_config = []
        target_config = []
        global_config.append('MACHINE = "qemux86-64"')
        target_config.append('PR = "0"')
        global_config.append(global_config[0])
        target_config.append('PR = "1"')
        global_config.append('MACHINE = "qemux86"')
        target_config.append('PR = "1"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    def test_sstate_cache_management_script_using_machine(self):
        global_config = []
        target_config = []
        global_config.append('MACHINE = "qemux86-64"')
        target_config.append('')
        global_config.append('MACHINE = "qemux86"')
        target_config.append('')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

class SStateHashSameSigs(SStateBase):
    def test_sstate_32_64_same_hash(self):
        """
        The sstate checksums for both native and target should not vary whether
        they're built on a 32 or 64 bit system. Rather than requiring two different
        build machines and running a builds, override the variables calling uname()
        manually and check using bitbake -S.
        """

        self.write_config("""
MACHINE = "qemux86"
TMPDIR = "${TOPDIR}/tmp-sstatesamehash"
BUILD_ARCH = "x86_64"
BUILD_OS = "linux"
SDKMACHINE = "x86_64"
PACKAGE_CLASSES = "package_rpm package_ipk package_deb"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("core-image-weston -S none")
        self.write_config("""
MACHINE = "qemux86"
TMPDIR = "${TOPDIR}/tmp-sstatesamehash2"
BUILD_ARCH = "i686"
BUILD_OS = "linux"
SDKMACHINE = "i686"
PACKAGE_CLASSES = "package_rpm package_ipk package_deb"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("core-image-weston -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                if "core-image-weston" in root:
                    # SDKMACHINE changing will change
                    # do_rootfs/do_testimage/do_build stamps of images which
                    # is safe to ignore.
                    continue
                f.extend(os.path.join(root, name) for name in files)
            return f
        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/")
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash").replace("i686-linux", "x86_64-linux").replace("i686" + self.target_vendor + "-linux", "x86_64" + self.target_vendor + "-linux", ) for x in files2]
        self.maxDiff = None
        self.assertCountEqual(files1, files2)


    def test_sstate_nativelsbstring_same_hash(self):
        """
        The sstate checksums should be independent of whichever NATIVELSBSTRING is
        detected. Rather than requiring two different build machines and running
        builds, override the variables manually and check using bitbake -S.
        """

        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
NATIVELSBSTRING = \"DistroA\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("core-image-weston -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
NATIVELSBSTRING = \"DistroB\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("core-image-weston -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                f.extend(os.path.join(root, name) for name in files)
            return f
        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/")
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash") for x in files2]
        self.maxDiff = None
        self.assertCountEqual(files1, files2)

class SStateHashSameSigs2(SStateBase):
    def test_sstate_allarch_samesigs(self):
        """
        The sstate checksums of allarch packages should be independent of whichever
        MACHINE is set. Check this using bitbake -S.
        Also, rather than duplicate the test, check nativesdk stamps are the same between
        the two MACHINE values.
        """

        configA = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
MACHINE = \"qemux86-64\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        #OLDEST_KERNEL is arch specific so set to a different value here for testing
        configB = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemuarm\"
OLDEST_KERNEL = \"3.3.0\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
ERROR_QA:append = " somenewoption"
WARN_QA:append = " someotheroption"
"""
        self.sstate_common_samesigs(configA, configB, allarch=True)

    def test_sstate_nativesdk_samesigs_multilib(self):
        """
        check nativesdk stamps are the same between the two MACHINE values.
        """

        configA = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
MACHINE = \"qemux86-64\"
require conf/multilib.conf
MULTILIBS = \"multilib:lib32\"
DEFAULTTUNE:virtclass-multilib-lib32 = \"x86\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        configB = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemuarm\"
require conf/multilib.conf
MULTILIBS = \"\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        self.sstate_common_samesigs(configA, configB)

class SStateHashSameSigs3(SStateBase):
    def test_sstate_sametune_samesigs(self):
        """
        The sstate checksums of two identical machines (using the same tune) should be the
        same, apart from changes within the machine specific stamps directory. We use the
        qemux86copy machine to test this. Also include multilibs in the test.
        """

        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
MACHINE = \"qemux86\"
require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE:virtclass-multilib-lib32 = "x86"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("world meta-toolchain -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemux86copy\"
require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE:virtclass-multilib-lib32 = "x86"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("world meta-toolchain -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                for name in files:
                    if "meta-environment" in root or "cross-canadian" in root or 'meta-ide-support' in root:
                        continue
                    if "qemux86copy-" in root or "qemux86-" in root:
                        continue
                    if "do_build" not in name and "do_populate_sdk" not in name:
                        f.append(os.path.join(root, name))
            return f
        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps")
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash") for x in files2]
        self.maxDiff = None
        self.assertCountEqual(files1, files2)


    def test_sstate_multilib_or_not_native_samesigs(self):
        """The sstate checksums of two native recipes (and their dependencies)
        where the target is using multilib in one but not the other
        should be the same. We use the qemux86copy machine to test
        this.
        """

        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
MACHINE = \"qemux86\"
require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE:virtclass-multilib-lib32 = "x86"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("binutils-native  -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemux86copy\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("binutils-native -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                for name in files:
                    f.append(os.path.join(root, name))
            return f
        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps")
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash") for x in files2]
        self.maxDiff = None
        self.assertCountEqual(files1, files2)

class SStateHashSameSigs4(SStateBase):
    def test_sstate_noop_samesigs(self):
        """
        The sstate checksums of two builds with these variables changed or
        classes inherits should be the same.
        """

        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-sstatesamehash"
BB_NUMBER_THREADS = "${@oe.utils.cpu_count()}"
PARALLEL_MAKE = "-j 1"
DL_DIR = "${TOPDIR}/download1"
TIME = "111111"
DATE = "20161111"
INHERIT:remove = "buildstats-summary buildhistory uninative"
http_proxy = ""
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        self.track_for_cleanup(self.topdir + "/download1")
        bitbake("world meta-toolchain -S none")
        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-sstatesamehash2"
BB_NUMBER_THREADS = "${@oe.utils.cpu_count()+1}"
PARALLEL_MAKE = "-j 2"
DL_DIR = "${TOPDIR}/download2"
TIME = "222222"
DATE = "20161212"
# Always remove uninative as we're changing proxies
INHERIT:remove = "uninative"
INHERIT += "buildstats-summary buildhistory"
http_proxy = "http://example.com/"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        self.track_for_cleanup(self.topdir + "/download2")
        bitbake("world meta-toolchain -S none")

        def get_files(d):
            f = {}
            for root, dirs, files in os.walk(d):
                for name in files:
                    name, shash = name.rsplit('.', 1)
                    # Extract just the machine and recipe name
                    base = os.sep.join(root.rsplit(os.sep, 2)[-2:] + [name])
                    f[base] = shash
            return f

        def compare_sigfiles(files, files1, files2, compare=False):
            for k in files:
                if k in files1 and k in files2:
                    print("%s differs:" % k)
                    if compare:
                        sigdatafile1 = self.topdir + "/tmp-sstatesamehash/stamps/" + k + "." + files1[k]
                        sigdatafile2 = self.topdir + "/tmp-sstatesamehash2/stamps/" + k + "." + files2[k]
                        output = bb.siggen.compare_sigfiles(sigdatafile1, sigdatafile2)
                        if output:
                            print('\n'.join(output))
                elif k in files1 and k not in files2:
                    print("%s in files1" % k)
                elif k not in files1 and k in files2:
                    print("%s in files2" % k)
                else:
                    assert "shouldn't reach here"

        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/")
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/")
        # Remove items that are identical in both sets
        for k,v in files1.items() & files2.items():
            del files1[k]
            del files2[k]
        if not files1 and not files2:
            # No changes, so we're done
            return

        files = list(files1.keys() | files2.keys())
        # this is an expensive computation, thus just compare the first 'max_sigfiles_to_compare' k files
        max_sigfiles_to_compare = 20
        first, rest = files[:max_sigfiles_to_compare], files[max_sigfiles_to_compare:]
        compare_sigfiles(first, files1, files2, compare=True)
        compare_sigfiles(rest, files1, files2, compare=False)

        self.fail("sstate hashes not identical.")

    def test_sstate_movelayer_samesigs(self):
        """
        The sstate checksums of two builds with the same oe-core layer in two
        different locations should be the same.
        """
        core_layer = os.path.join(
                    self.tc.td["COREBASE"], 'meta')
        copy_layer_1 = self.topdir + "/meta-copy1/meta"
        copy_layer_2 = self.topdir + "/meta-copy2/meta"

        oe.path.copytree(core_layer, copy_layer_1)
        os.symlink(os.path.dirname(core_layer) + "/scripts", self.topdir + "/meta-copy1/scripts")
        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-sstatesamehash"
""")
        bblayers_conf = 'BBLAYERS += "%s"\nBBLAYERS:remove = "%s"' % (copy_layer_1, core_layer)
        self.write_bblayers_config(bblayers_conf)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("bash -S none")

        oe.path.copytree(core_layer, copy_layer_2)
        os.symlink(os.path.dirname(core_layer) + "/scripts", self.topdir + "/meta-copy2/scripts")
        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-sstatesamehash2"
""")
        bblayers_conf = 'BBLAYERS += "%s"\nBBLAYERS:remove = "%s"' % (copy_layer_2, core_layer)
        self.write_bblayers_config(bblayers_conf)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("bash -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                for name in files:
                    f.append(os.path.join(root, name))
            return f
        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps")
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash") for x in files2]
        self.maxDiff = None
        self.assertCountEqual(files1, files2)

class SStateFindSiginfo(SStateBase):
    def test_sstate_compare_sigfiles_and_find_siginfo(self):
        """
        Test the functionality of the find_siginfo: basic function and callback in compare_sigfiles
        """
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstates-findsiginfo\"
MACHINE = \"qemux86-64\"
require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE:virtclass-multilib-lib32 = "x86"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstates-findsiginfo")

        pns = ["binutils", "binutils-native", "lib32-binutils"]
        target_configs = [
"""
TMPVAL1 = "tmpval1"
TMPVAL2 = "tmpval2"
do_tmptask1() {
    echo ${TMPVAL1}
}
do_tmptask2() {
    echo ${TMPVAL2}
}
addtask do_tmptask1
addtask tmptask2 before do_tmptask1
""",
"""
TMPVAL3 = "tmpval3"
TMPVAL4 = "tmpval4"
do_tmptask1() {
    echo ${TMPVAL3}
}
do_tmptask2() {
    echo ${TMPVAL4}
}
addtask do_tmptask1
addtask tmptask2 before do_tmptask1
"""
        ]

        for target_config in target_configs:
            self.write_recipeinc("binutils", target_config)
            for pn in pns:
                bitbake("%s -c do_tmptask1 -S none" % pn)
            self.delete_recipeinc("binutils")

        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)

            def find_siginfo(pn, taskname, sigs=None):
                result = None
                command_complete = False
                tinfoil.set_event_mask(["bb.event.FindSigInfoResult",
                                "bb.command.CommandCompleted"])
                ret = tinfoil.run_command("findSigInfo", pn, taskname, sigs)
                if ret:
                    while result is None or not command_complete:
                        event = tinfoil.wait_event(1)
                        if event:
                            if isinstance(event, bb.command.CommandCompleted):
                                command_complete = True
                            elif isinstance(event, bb.event.FindSigInfoResult):
                                result = event.result
                return result

            def recursecb(key, hash1, hash2):
                nonlocal recursecb_count
                recursecb_count += 1
                hashes = [hash1, hash2]
                hashfiles = find_siginfo(key, None, hashes)
                self.assertCountEqual(hashes, hashfiles)
                bb.siggen.compare_sigfiles(hashfiles[hash1]['path'], hashfiles[hash2]['path'], recursecb)

            for pn in pns:
                recursecb_count = 0
                matches = find_siginfo(pn, "do_tmptask1")
                self.assertGreaterEqual(len(matches), 2)
                latesthashes = sorted(matches.keys(), key=lambda h: matches[h]['time'])[-2:]
                bb.siggen.compare_sigfiles(matches[latesthashes[-2]]['path'], matches[latesthashes[-1]]['path'], recursecb)
                self.assertEqual(recursecb_count,1)

class SStatePrintdiff(SStateBase):
    def run_test_printdiff_changerecipe(self, target, change_recipe, change_bbtask, change_content, expected_sametmp_output, expected_difftmp_output):
        import time
        self.write_config("""
TMPDIR = "${{TOPDIR}}/tmp-sstateprintdiff-sametmp-{}"
""".format(time.time()))
        # Use runall do_build to ensure any indirect sstate is created, e.g. tzcode-native on both x86 and
        # aarch64 hosts since only allarch target recipes depend upon it and it may not be built otherwise.
        # A bitbake -c cleansstate tzcode-native would cause some of these tests to error for example.
        bitbake("--runall build --runall deploy_source_date_epoch {}".format(target))
        bitbake("-S none {}".format(target))
        bitbake(change_bbtask)
        self.write_recipeinc(change_recipe, change_content)
        result_sametmp = bitbake("-S printdiff {}".format(target))

        self.write_config("""
TMPDIR = "${{TOPDIR}}/tmp-sstateprintdiff-difftmp-{}"
""".format(time.time()))
        result_difftmp = bitbake("-S printdiff {}".format(target))

        self.delete_recipeinc(change_recipe)
        for item in expected_sametmp_output:
            self.assertIn(item, result_sametmp.output, msg = "Item {} not found in output:\n{}".format(item, result_sametmp.output))
        for item in expected_difftmp_output:
            self.assertIn(item, result_difftmp.output, msg = "Item {} not found in output:\n{}".format(item, result_difftmp.output))

    def run_test_printdiff_changeconfig(self, target, change_bbtasks, change_content, expected_sametmp_output, expected_difftmp_output):
        import time
        self.write_config("""
TMPDIR = "${{TOPDIR}}/tmp-sstateprintdiff-sametmp-{}"
""".format(time.time()))
        bitbake("--runall build --runall deploy_source_date_epoch {}".format(target))
        bitbake("-S none {}".format(target))
        bitbake(" ".join(change_bbtasks))
        self.append_config(change_content)
        result_sametmp = bitbake("-S printdiff {}".format(target))

        self.write_config("""
TMPDIR = "${{TOPDIR}}/tmp-sstateprintdiff-difftmp-{}"
""".format(time.time()))
        self.append_config(change_content)
        result_difftmp = bitbake("-S printdiff {}".format(target))

        for item in expected_sametmp_output:
            self.assertIn(item, result_sametmp.output, msg = "Item {} not found in output:\n{}".format(item, result_sametmp.output))
        for item in expected_difftmp_output:
            self.assertIn(item, result_difftmp.output, msg = "Item {} not found in output:\n{}".format(item, result_difftmp.output))


    # Check if printdiff walks the full dependency chain from the image target to where the change is in a specific recipe
    def test_image_minimal_vs_perlcross(self):
        expected_output = ("Task perlcross-native:do_install couldn't be used from the cache because:",
"We need hash",
"most recent matching task was")
        expected_sametmp_output = expected_output + (
"Variable do_install value changed",
'+    echo "this changes the task signature"')
        expected_difftmp_output = expected_output

        self.run_test_printdiff_changerecipe("core-image-minimal", "perlcross", "-c do_install perlcross-native",
"""
do_install:append() {
    echo "this changes the task signature"
}
""",
expected_sametmp_output, expected_difftmp_output)

    # Check if changes to gcc-source (which uses tmp/work-shared) are correctly discovered
    def test_gcc_runtime_vs_gcc_source(self):
        gcc_source_pn = 'gcc-source-%s' % get_bb_vars(['PV'], 'gcc')['PV']

        expected_output = ("Task {}:do_preconfigure couldn't be used from the cache because:".format(gcc_source_pn),
"We need hash",
"most recent matching task was")
        expected_sametmp_output = expected_output + (
"Variable do_preconfigure value changed",
'+    print("this changes the task signature")')
        expected_difftmp_output = expected_output

        self.run_test_printdiff_changerecipe("gcc-runtime", "gcc-source", "-c do_preconfigure {}".format(gcc_source_pn),
"""
python do_preconfigure:append() {
    print("this changes the task signature")
}
""",
expected_sametmp_output, expected_difftmp_output)

    # Check if changing a really base task definiton is reported against multiple core recipes using it
    def test_image_minimal_vs_base_do_configure(self):
        change_bbtasks = ('zstd-native:do_configure',
'texinfo-dummy-native:do_configure',
'ldconfig-native:do_configure',
'gettext-minimal-native:do_configure',
'tzcode-native:do_configure',
'makedevs-native:do_configure',
'pigz-native:do_configure',
'update-rc.d-native:do_configure',
'unzip-native:do_configure',
'gnu-config-native:do_configure')

        expected_output = ["Task {} couldn't be used from the cache because:".format(t) for t in change_bbtasks] + [
"We need hash",
"most recent matching task was"]

        expected_sametmp_output = expected_output + [
"Variable base_do_configure value changed",
'+	echo "this changes base_do_configure() definiton "']
        expected_difftmp_output = expected_output

        self.run_test_printdiff_changeconfig("core-image-minimal",change_bbtasks,
"""
INHERIT += "base-do-configure-modified"
""",
expected_sametmp_output, expected_difftmp_output)

class SStateCheckObjectPresence(SStateBase):
    def check_bb_output(self, output, targets, exceptions, check_cdn):
        def is_exception(object, exceptions):
            for e in exceptions:
                if re.search(e, object):
                    return True
            return False

        # sstate is checked for existence of these, but they never get written out to begin with
        exceptions += ["{}.*image_qa".format(t) for t in targets.split()]
        exceptions += ["{}.*deploy_source_date_epoch".format(t) for t in targets.split()]
        exceptions += ["{}.*image_complete".format(t) for t in targets.split()]
        exceptions += ["linux-yocto.*shared_workdir"]
        # these get influnced by IMAGE_FSTYPES tweaks in yocto-autobuilder-helper's config.json (on x86-64)
        # additionally, they depend on noexec (thus, absent stamps) package, install, etc. image tasks,
        # which makes tracing other changes difficult
        exceptions += ["{}.*create_.*spdx".format(t) for t in targets.split()]

        output_l = output.splitlines()
        for l in output_l:
            if l.startswith("Sstate summary"):
                for idx, item in enumerate(l.split()):
                    if item == 'Missed':
                        missing_objects = int(l.split()[idx+1])
                        break
                else:
                    self.fail("Did not find missing objects amount in sstate summary: {}".format(l))
                break
        else:
            self.fail("Did not find 'Sstate summary' line in bitbake output")

        failed_urls = []
        failed_urls_extrainfo = []
        for l in output_l:
            if "SState: Unsuccessful fetch test for" in l and check_cdn:
                missing_object = l.split()[6]
            elif "SState: Looked for but didn't find file" in l and not check_cdn:
                missing_object = l.split()[8]
            else:
                missing_object = None
            if missing_object:
                if not is_exception(missing_object, exceptions):
                    failed_urls.append(missing_object)
                else:
                    missing_objects -= 1

            if "urlopen failed for" in l and not is_exception(l, exceptions):
                failed_urls_extrainfo.append(l)

        self.assertEqual(len(failed_urls), missing_objects, "Amount of reported missing objects does not match failed URLs: {}\nFailed URLs:\n{}\nFetcher diagnostics:\n{}".format(missing_objects, "\n".join(failed_urls), "\n".join(failed_urls_extrainfo)))
        self.assertEqual(len(failed_urls), 0, "Missing objects in the cache:\n{}\nFetcher diagnostics:\n{}".format("\n".join(failed_urls), "\n".join(failed_urls_extrainfo)))

@OETestTag("yocto-mirrors")
class SStateMirrors(SStateCheckObjectPresence):
    def run_test(self, machine, targets, exceptions, check_cdn = True, ignore_errors = False):
        if check_cdn:
            self.config_sstate(True)
            self.append_config("""
MACHINE = "{}"
BB_HASHSERVE_UPSTREAM = "hashserv.yoctoproject.org:8686"
SSTATE_MIRRORS ?= "file://.* http://cdn.jsdelivr.net/yocto/sstate/all/PATH;downloadfilename=PATH"
""".format(machine))
        else:
            self.append_config("""
MACHINE = "{}"
""".format(machine))
        result = bitbake("-DD -n {}".format(targets))
        bitbake("-S none {}".format(targets))
        if ignore_errors:
            return
        self.check_bb_output(result.output, targets, exceptions, check_cdn)

    def test_cdn_mirror_qemux86_64(self):
        exceptions = []
        self.run_test("qemux86-64", "core-image-minimal core-image-full-cmdline core-image-sato-sdk", exceptions)

    def test_cdn_mirror_qemuarm64(self):
        exceptions = []
        self.run_test("qemuarm64", "core-image-minimal core-image-full-cmdline core-image-sato-sdk", exceptions)

    def test_local_cache_qemux86_64(self):
        exceptions = []
        self.run_test("qemux86-64", "core-image-minimal core-image-full-cmdline core-image-sato-sdk", exceptions, check_cdn = False)

    def test_local_cache_qemuarm64(self):
        exceptions = []
        self.run_test("qemuarm64", "core-image-minimal core-image-full-cmdline core-image-sato-sdk", exceptions, check_cdn = False)
