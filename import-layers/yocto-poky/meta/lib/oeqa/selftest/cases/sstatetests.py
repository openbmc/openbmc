import os
import shutil
import glob
import subprocess

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_test_layer
from oeqa.selftest.cases.sstate import SStateBase
from oeqa.core.decorator.oeid import OETestID

import bb.siggen

class SStateTests(SStateBase):

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
                if r.endswith(("_populate_lic.tgz", "_populate_lic.tgz.siginfo", "_fetch.tgz.siginfo", "_unpack.tgz.siginfo", "_patch.tgz.siginfo")):
                    continue
                file_tracker.append(r)
        else:
            file_tracker = results

        if should_pass:
            self.assertTrue(file_tracker , msg="Could not find sstate files for: %s" % ', '.join(map(str, targets)))
        else:
            self.assertTrue(not file_tracker , msg="Found sstate files in the wrong place for: %s (found %s)" % (', '.join(map(str, targets)), str(file_tracker)))

    @OETestID(975)
    def test_sstate_creation_distro_specific_pass(self):
        self.run_test_sstate_creation(['binutils-cross-'+ self.tune_arch, 'binutils-native'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)

    @OETestID(1374)
    def test_sstate_creation_distro_specific_fail(self):
        self.run_test_sstate_creation(['binutils-cross-'+ self.tune_arch, 'binutils-native'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True, should_pass=False)

    @OETestID(976)
    def test_sstate_creation_distro_nonspecific_pass(self):
        self.run_test_sstate_creation(['linux-libc-headers'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    @OETestID(1375)
    def test_sstate_creation_distro_nonspecific_fail(self):
        self.run_test_sstate_creation(['linux-libc-headers'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True, should_pass=False)

    # Test the sstate files deletion part of the do_cleansstate task
    def run_test_cleansstate_task(self, targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True):
        self.config_sstate(temp_sstate_location, [self.sstate_path])

        bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        tgz_created = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(tgz_created, msg="Could not find sstate .tgz files for: %s (%s)" % (', '.join(map(str, targets)), str(tgz_created)))

        siginfo_created = self.search_sstate('|'.join(map(str, [s + '.*?\.siginfo$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(siginfo_created, msg="Could not find sstate .siginfo files for: %s (%s)" % (', '.join(map(str, targets)), str(siginfo_created)))

        bitbake(['-ccleansstate'] + targets)
        tgz_removed = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(not tgz_removed, msg="do_cleansstate didn't remove .tgz sstate files for: %s (%s)" % (', '.join(map(str, targets)), str(tgz_removed)))

    @OETestID(977)
    def test_cleansstate_task_distro_specific_nonspecific(self):
        targets = ['binutils-cross-'+ self.tune_arch, 'binutils-native']
        targets.append('linux-libc-headers')
        self.run_test_cleansstate_task(targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True)

    @OETestID(1376)
    def test_cleansstate_task_distro_nonspecific(self):
        self.run_test_cleansstate_task(['linux-libc-headers'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    @OETestID(1377)
    def test_cleansstate_task_distro_specific(self):
        targets = ['binutils-cross-'+ self.tune_arch, 'binutils-native']
        targets.append('linux-libc-headers')
        self.run_test_cleansstate_task(targets, distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)


    # Test rebuilding of distro-specific sstate files
    def run_test_rebuild_distro_specific_sstate(self, targets, temp_sstate_location=True):
        self.config_sstate(temp_sstate_location, [self.sstate_path])

        bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        results = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific=False, distro_nonspecific=True)
        filtered_results = []
        for r in results:
            if r.endswith(("_populate_lic.tgz", "_populate_lic.tgz.siginfo")):
                continue
            filtered_results.append(r)
        self.assertTrue(filtered_results == [], msg="Found distro non-specific sstate for: %s (%s)" % (', '.join(map(str, targets)), str(filtered_results)))
        file_tracker_1 = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific=True, distro_nonspecific=False)
        self.assertTrue(len(file_tracker_1) >= len(targets), msg = "Not all sstate files ware created for: %s" % ', '.join(map(str, targets)))

        self.track_for_cleanup(self.distro_specific_sstate + "_old")
        shutil.copytree(self.distro_specific_sstate, self.distro_specific_sstate + "_old")
        shutil.rmtree(self.distro_specific_sstate)

        bitbake(['-cclean'] + targets)
        bitbake(targets)
        file_tracker_2 = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific=True, distro_nonspecific=False)
        self.assertTrue(len(file_tracker_2) >= len(targets), msg = "Not all sstate files ware created for: %s" % ', '.join(map(str, targets)))

        not_recreated = [x for x in file_tracker_1 if x not in file_tracker_2]
        self.assertTrue(not_recreated == [], msg="The following sstate files ware not recreated: %s" % ', '.join(map(str, not_recreated)))

        created_once = [x for x in file_tracker_2 if x not in file_tracker_1]
        self.assertTrue(created_once == [], msg="The following sstate files ware created only in the second run: %s" % ', '.join(map(str, created_once)))

    @OETestID(175)
    def test_rebuild_distro_specific_sstate_cross_native_targets(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + self.tune_arch, 'binutils-native'], temp_sstate_location=True)

    @OETestID(1372)
    def test_rebuild_distro_specific_sstate_cross_target(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + self.tune_arch], temp_sstate_location=True)

    @OETestID(1373)
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
        self.append_config('ERROR_QA_remove = "version-going-backwards"')

        # For not this only checks if random sstate tasks are handled correctly as a group.
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
                target_sstate_before_build = self.search_sstate(target + '.*?\.tgz$')
            bitbake("-cclean %s" % target)
            result = bitbake(target, ignore_status=True)
            if target_config[idx] == target_config[-1]:
                target_sstate_after_build = self.search_sstate(target + '.*?\.tgz$')
                expected_remaining_sstate += [x for x in target_sstate_after_build if x not in target_sstate_before_build if not any(pattern in x for pattern in ignore_patterns)]
            self.remove_config(global_config[idx])
            self.remove_recipeinc(target, target_config[idx])
            self.assertEqual(result.status, 0, msg = "build of %s failed with %s" % (target, result.output))

        runCmd("sstate-cache-management.sh -y --cache-dir=%s --remove-duplicated --extra-archs=%s" % (self.sstate_path, ','.join(map(str, sstate_archs_list))))
        actual_remaining_sstate = [x for x in self.search_sstate(target + '.*?\.tgz$') if not any(pattern in x for pattern in ignore_patterns)]

        actual_not_expected = [x for x in actual_remaining_sstate if x not in expected_remaining_sstate]
        self.assertFalse(actual_not_expected, msg="Files should have been removed but ware not: %s" % ', '.join(map(str, actual_not_expected)))
        expected_not_actual = [x for x in expected_remaining_sstate if x not in actual_remaining_sstate]
        self.assertFalse(expected_not_actual, msg="Extra files ware removed: %s" ', '.join(map(str, expected_not_actual)))

    @OETestID(973)
    def test_sstate_cache_management_script_using_pr_1(self):
        global_config = []
        target_config = []
        global_config.append('')
        target_config.append('PR = "0"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @OETestID(978)
    def test_sstate_cache_management_script_using_pr_2(self):
        global_config = []
        target_config = []
        global_config.append('')
        target_config.append('PR = "0"')
        global_config.append('')
        target_config.append('PR = "1"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @OETestID(979)
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

    @OETestID(974)
    def test_sstate_cache_management_script_using_machine(self):
        global_config = []
        target_config = []
        global_config.append('MACHINE = "qemux86-64"')
        target_config.append('')
        global_config.append('MACHINE = "qemux86"')
        target_config.append('')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @OETestID(1270)
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
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("core-image-sato -S none")
        self.write_config("""
MACHINE = "qemux86"
TMPDIR = "${TOPDIR}/tmp-sstatesamehash2"
BUILD_ARCH = "i686"
BUILD_OS = "linux"
SDKMACHINE = "i686"
PACKAGE_CLASSES = "package_rpm package_ipk package_deb"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("core-image-sato -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                if "core-image-sato" in root:
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


    @OETestID(1271)
    def test_sstate_nativelsbstring_same_hash(self):
        """
        The sstate checksums should be independent of whichever NATIVELSBSTRING is
        detected. Rather than requiring two different build machines and running
        builds, override the variables manually and check using bitbake -S.
        """

        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
NATIVELSBSTRING = \"DistroA\"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("core-image-sato -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
NATIVELSBSTRING = \"DistroB\"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash2")
        bitbake("core-image-sato -S none")

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

    @OETestID(1368)
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
"""
        configB = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemuarm\"
"""
        self.sstate_allarch_samesigs(configA, configB)

    @OETestID(1645)
    def test_sstate_allarch_samesigs_multilib(self):
        """
        The sstate checksums of allarch multilib packages should be independent of whichever
        MACHINE is set. Check this using bitbake -S.
        Also, rather than duplicate the test, check nativesdk stamps are the same between
        the two MACHINE values.
        """

        configA = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
MACHINE = \"qemux86-64\"
require conf/multilib.conf
MULTILIBS = \"multilib:lib32\"
DEFAULTTUNE_virtclass-multilib-lib32 = \"x86\"
"""
        configB = """
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemuarm\"
require conf/multilib.conf
MULTILIBS = \"\"
"""
        self.sstate_allarch_samesigs(configA, configB)

    def sstate_allarch_samesigs(self, configA, configB):

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
        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/all" + self.target_vendor + "-" + self.target_os)
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/all" + self.target_vendor + "-" + self.target_os)
        self.maxDiff = None
        self.assertEqual(files1, files2)

        nativesdkdir = os.path.basename(glob.glob(self.topdir + "/tmp-sstatesamehash/stamps/*-nativesdk*-linux")[0])

        files1 = get_files(self.topdir + "/tmp-sstatesamehash/stamps/" + nativesdkdir)
        files2 = get_files(self.topdir + "/tmp-sstatesamehash2/stamps/" + nativesdkdir)
        self.maxDiff = None
        self.assertEqual(files1, files2)

    @OETestID(1369)
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
DEFAULTTUNE_virtclass-multilib-lib32 = "x86"
""")
        self.track_for_cleanup(self.topdir + "/tmp-sstatesamehash")
        bitbake("world meta-toolchain -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemux86copy\"
require conf/multilib.conf
MULTILIBS = "multilib:lib32"
DEFAULTTUNE_virtclass-multilib-lib32 = "x86"
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


    @OETestID(1498)
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
INHERIT_remove = "buildstats-summary buildhistory uninative"
http_proxy = ""
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
INHERIT_remove = "uninative"
INHERIT += "buildstats-summary buildhistory"
http_proxy = "http://example.com/"
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
        compare_sigfiles(first, files1.keys(), files2.keys(), compare=True)
        compare_sigfiles(rest, files1.keys(), files2.keys(), compare=False)

        self.fail("sstate hashes not identical.")
