#
# SPDX-License-Identifier: MIT
#

import os
import shutil
import glob
import subprocess
import tempfile

from oeqa.utils.commands import runCmd, bitbake, get_bb_var, create_temp_layer
from oeqa.selftest.cases.sstate import SStateBase
import oe

import bb.siggen

class SStateTests(SStateBase):
    def test_autorev_sstate_works(self):
        # Test that a git repository which changes is correctly handled by SRCREV = ${AUTOREV}
        # when PV does not contain SRCPV

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


    # Test sstate files creation and their location
    def run_test_sstate_creation(self, targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True, should_pass=True):
        self.config_sstate(temp_sstate_location, [self.sstate_path])

        if  self.temp_sstate_location:
            bitbake(['-cclean'] + targets)
        else:
            bitbake(['-ccleansstate'] + targets)

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

    def test_sstate_creation_distro_specific_pass(self):
        self.run_test_sstate_creation(['binutils-cross-'+ self.tune_arch, 'binutils-native'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)

    def test_sstate_creation_distro_specific_fail(self):
        self.run_test_sstate_creation(['binutils-cross-'+ self.tune_arch, 'binutils-native'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True, should_pass=False)

    def test_sstate_creation_distro_nonspecific_pass(self):
        self.run_test_sstate_creation(['linux-libc-headers'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    def test_sstate_creation_distro_nonspecific_fail(self):
        self.run_test_sstate_creation(['linux-libc-headers'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True, should_pass=False)

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

    def test_rebuild_distro_specific_sstate_cross_native_targets(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + self.tune_arch, 'binutils-native'], temp_sstate_location=True)

    def test_rebuild_distro_specific_sstate_cross_target(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + self.tune_arch], temp_sstate_location=True)

    def test_rebuild_distro_specific_sstate_native_target(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-native'], temp_sstate_location=True)


    # Test the sstate-cache-management script. Each element in the global_config list is used with the corresponding element in the target_config list
    # global_config elements are expected to not generate any sstate files that would be removed by sstate-cache-management.sh (such as changing the value of MACHINE)
    def run_test_sstate_cache_management_script(self, target, global_config=[''], target_config=[''], ignore_patterns=[]):
        self.assertTrue(global_config)
        self.assertTrue(target_config)
        self.assertTrue(len(global_config) == len(target_config), msg='Lists global_config and target_config should have the same number of elements')
        self.config_sstate(temp_sstate_location=True, add_local_mirrors=[self.sstate_path])

        # If buildhistory is enabled, we need to disable version-going-backwards
        # QA checks for this test. It may report errors otherwise.
        self.append_config('ERROR_QA:remove = "version-going-backwards"')

        # For now this only checks if random sstate tasks are handled correctly as a group.
        # In the future we should add control over what tasks we check for.

        sstate_archs_list = []
        expected_remaining_sstate = []
        for idx in range(len(target_config)):
            self.append_config(global_config[idx])
            self.append_recipeinc(target, target_config[idx])
            sstate_arch = get_bb_var('SSTATE_PKGARCH', target)
            if not sstate_arch in sstate_archs_list:
                sstate_archs_list.append(sstate_arch)
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

        runCmd("sstate-cache-management.sh -y --cache-dir=%s --remove-duplicated --extra-archs=%s" % (self.sstate_path, ','.join(map(str, sstate_archs_list))))
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
TCLIBCAPPEND = ""
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
TCLIBCAPPEND = ""
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
TCLIBCAPPEND = \"\"
NATIVELSBSTRING = \"DistroA\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("core-image-weston -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
TCLIBCAPPEND = \"\"
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

    def test_sstate_allarch_samesigs(self):
        """
        The sstate checksums of allarch packages should be independent of whichever
        MACHINE is set. Check this using bitbake -S.
        Also, rather than duplicate the test, check nativesdk stamps are the same between
        the two MACHINE values.
        """

        configA = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
TCLIBCAPPEND = \"\"
MACHINE = \"qemux86-64\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        #OLDEST_KERNEL is arch specific so set to a different value here for testing
        configB = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
TCLIBCAPPEND = \"\"
MACHINE = \"qemuarm\"
OLDEST_KERNEL = \"3.3.0\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        self.sstate_common_samesigs(configA, configB, allarch=True)

    def test_sstate_nativesdk_samesigs_multilib(self):
        """
        check nativesdk stamps are the same between the two MACHINE values.
        """

        configA = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
TCLIBCAPPEND = \"\"
MACHINE = \"qemux86-64\"
require conf/multilib.conf
MULTILIBS = \"multilib:lib32\"
DEFAULTTUNE:virtclass-multilib-lib32 = \"x86\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        configB = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
TCLIBCAPPEND = \"\"
MACHINE = \"qemuarm\"
require conf/multilib.conf
MULTILIBS = \"\"
BB_SIGNATURE_HANDLER = "OEBasicHash"
"""
        self.sstate_common_samesigs(configA, configB)

    def sstate_common_samesigs(self, configA, configB, allarch=False):

        self.write_config(configA)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("world meta-toolchain -S none")
        self.write_config(configB)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("world meta-toolchain -S none")

        def get_files(d):
            f = {}
            for root, dirs, files in os.walk(d):
                for name in files:
                    if "meta-environment" in root or "cross-canadian" in root:
                        continue
                    if "do_build" not in name:
                        # 1.4.1+gitAUTOINC+302fca9f4c-r0.do_package_write_ipk.sigdata.f3a2a38697da743f0dbed8b56aafcf79
                        (_, task, _, shash) = name.rsplit(".", 3)
                        f[os.path.join(os.path.basename(root), task)] = shash
            return f

        nativesdkdir = os.path.basename(glob.glob(self.topdir + "/tmp-sstatesamehash/stamps/*-nativesdk*-linux")[0])

        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/" + nativesdkdir)
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/" + nativesdkdir)
        self.maxDiff = None
        self.assertEqual(files1, files2)

        if allarch:
            allarchdir = os.path.basename(glob.glob(self.topdir + "/tmp-sstatesamehash/stamps/all-*-linux")[0])

            files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/" + allarchdir)
            files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/" + allarchdir)
            self.assertEqual(files1, files2)

    def test_sstate_sametune_samesigs(self):
        """
        The sstate checksums of two identical machines (using the same tune) should be the
        same, apart from changes within the machine specific stamps directory. We use the
        qemux86copy machine to test this. Also include multilibs in the test.
        """

        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
TCLIBCAPPEND = \"\"
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
TCLIBCAPPEND = \"\"
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
                    if "meta-environment" in root or "cross-canadian" in root:
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
TCLIBCAPPEND = \"\"
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
TCLIBCAPPEND = \"\"
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


    def test_sstate_noop_samesigs(self):
        """
        The sstate checksums of two builds with these variables changed or
        classes inherits should be the same.
        """

        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-sstatesamehash"
TCLIBCAPPEND = ""
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
TCLIBCAPPEND = ""
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
        self.write_config("""
TMPDIR = "${TOPDIR}/tmp-sstatesamehash"
""")
        bblayers_conf = 'BBLAYERS += "%s"\nBBLAYERS:remove = "%s"' % (copy_layer_1, core_layer)
        self.write_bblayers_config(bblayers_conf)
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("bash -S none")

        oe.path.copytree(core_layer, copy_layer_2)
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

