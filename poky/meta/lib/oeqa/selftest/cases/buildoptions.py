#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import re
import glob as g
import shutil
import tempfile
from oeqa.selftest.case import OESelftestTestCase
from oeqa.selftest.cases.buildhistory import BuildhistoryBase
from oeqa.core.decorator.data import skipIfMachine
from oeqa.utils.commands import bitbake, get_bb_var, get_bb_vars
import oeqa.utils.ftools as ftools
from oeqa.core.decorator import OETestTag

class ImageOptionsTests(OESelftestTestCase):

    def test_incremental_image_generation(self):
        image_pkgtype = get_bb_var("IMAGE_PKGTYPE")
        if image_pkgtype != 'rpm':
            self.skipTest('Not using RPM as main package format')
        bitbake("-c clean core-image-minimal")
        self.write_config('INC_RPM_IMAGE_GEN = "1"')
        self.append_config('IMAGE_FEATURES += "ssh-server-openssh"')
        bitbake("core-image-minimal")
        log_data_file = os.path.join(get_bb_var("WORKDIR", "core-image-minimal"), "temp/log.do_rootfs")
        log_data_created = ftools.read_file(log_data_file)
        incremental_created = re.search(r"Installing\s*:\s*packagegroup-core-ssh-openssh", log_data_created)
        self.remove_config('IMAGE_FEATURES += "ssh-server-openssh"')
        self.assertTrue(incremental_created, msg = "Match failed in:\n%s" % log_data_created)
        bitbake("core-image-minimal")
        log_data_removed = ftools.read_file(log_data_file)
        incremental_removed = re.search(r"Erasing\s*:\s*packagegroup-core-ssh-openssh", log_data_removed)
        self.assertTrue(incremental_removed, msg = "Match failed in:\n%s" % log_data_removed)

    def test_ccache_tool(self):
        bitbake("ccache-native")
        bb_vars = get_bb_vars(['SYSROOT_DESTDIR', 'bindir'], 'ccache-native')
        p = bb_vars['SYSROOT_DESTDIR'] + bb_vars['bindir'] + "/" + "ccache"
        self.assertTrue(os.path.isfile(p), msg = "No ccache found (%s)" % p)
        self.write_config('INHERIT += "ccache"')
        recipe = "libgcc-initial"
        self.add_command_to_tearDown('bitbake -c clean %s' % recipe)
        bitbake("%s -c clean" % recipe)
        bitbake("%s -f -c compile" % recipe)
        log_compile = os.path.join(get_bb_var("WORKDIR", recipe), "temp/log.do_compile")
        with open(log_compile, "r") as f:
            loglines = "".join(f.readlines())
        self.assertIn("ccache", loglines, msg="No match for ccache in %s log.do_compile. For further details: %s" % (recipe , log_compile))

    def test_read_only_image(self):
        distro_features = get_bb_var('DISTRO_FEATURES')
        if not ('x11' in distro_features and 'opengl' in distro_features):
            self.skipTest('core-image-sato/weston requires x11 and opengl in distro features')
        self.write_config('IMAGE_FEATURES += "read-only-rootfs"')
        bitbake("core-image-sato core-image-weston")
        # do_image will fail if there are any pending postinsts

class DiskMonTest(OESelftestTestCase):

    def test_stoptask_behavior(self):
        self.write_config('BB_DISKMON_DIRS = "STOPTASKS,${TMPDIR},100000G,100K"\nBB_HEARTBEAT_EVENT = "1"')
        res = bitbake("delay -c delay", ignore_status = True)
        self.assertTrue('ERROR: No new tasks can be executed since the disk space monitor action is "STOPTASKS"!' in res.output, msg = "Tasks should have stopped. Disk monitor is set to STOPTASK: %s" % res.output)
        self.assertEqual(res.status, 1, msg = "bitbake reported exit code %s. It should have been 1. Bitbake output: %s" % (str(res.status), res.output))
        self.write_config('BB_DISKMON_DIRS = "HALT,${TMPDIR},100000G,100K"\nBB_HEARTBEAT_EVENT = "1"')
        res = bitbake("delay -c delay", ignore_status = True)
        self.assertTrue('ERROR: Immediately halt since the disk space monitor action is "HALT"!' in res.output, "Tasks should have been halted immediately. Disk monitor is set to HALT: %s" % res.output)
        self.assertEqual(res.status, 1, msg = "bitbake reported exit code %s. It should have been 1. Bitbake output: %s" % (str(res.status), res.output))
        self.write_config('BB_DISKMON_DIRS = "WARN,${TMPDIR},100000G,100K"\nBB_HEARTBEAT_EVENT = "1"')
        res = bitbake("delay -c delay")
        self.assertTrue('WARNING: The free space' in res.output, msg = "A warning should have been displayed for disk monitor is set to WARN: %s" %res.output)

class SanityOptionsTest(OESelftestTestCase):
    def getline(self, res, line):
        for l in res.output.split('\n'):
            if line in l:
                return l

    def test_options_warnqa_errorqa_switch(self):

        self.write_config("INHERIT:remove = \"report-error\"")
        if "packages-list" not in get_bb_var("ERROR_QA"):
            self.append_config("ERROR_QA:append:pn-xcursor-transparent-theme = \" packages-list\"")

        self.write_recipeinc('xcursor-transparent-theme', 'PACKAGES += \"${PN}-dbg\"')
        self.add_command_to_tearDown('bitbake -c clean xcursor-transparent-theme')
        res = bitbake("xcursor-transparent-theme -f -c package", ignore_status=True)
        self.delete_recipeinc('xcursor-transparent-theme')
        line = self.getline(res, "QA Issue: xcursor-transparent-theme-dbg is listed in PACKAGES multiple times, this leads to packaging errors.")
        self.assertTrue(line and line.startswith("ERROR:"), msg=res.output)
        self.assertEqual(res.status, 1, msg = "bitbake reported exit code %s. It should have been 1. Bitbake output: %s" % (str(res.status), res.output))
        self.write_recipeinc('xcursor-transparent-theme', 'PACKAGES += \"${PN}-dbg\"')
        self.append_config('ERROR_QA:remove:pn-xcursor-transparent-theme = "packages-list"')
        self.append_config('WARN_QA:append:pn-xcursor-transparent-theme = " packages-list"')
        res = bitbake("xcursor-transparent-theme -f -c package")
        self.delete_recipeinc('xcursor-transparent-theme')
        line = self.getline(res, "QA Issue: xcursor-transparent-theme-dbg is listed in PACKAGES multiple times, this leads to packaging errors.")
        self.assertTrue(line and line.startswith("WARNING:"), msg=res.output)

    def test_layer_without_git_dir(self):
        """
        Summary:     Test that layer git revisions are displayed and do not fail without git repository
        Expected:    The build to be successful and without "fatal" errors
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        dirpath = tempfile.mkdtemp()

        dummy_layer_name = 'meta-dummy'
        dummy_layer_path = os.path.join(dirpath, dummy_layer_name)
        dummy_layer_conf_dir = os.path.join(dummy_layer_path, 'conf')
        os.makedirs(dummy_layer_conf_dir)
        dummy_layer_conf_path = os.path.join(dummy_layer_conf_dir, 'layer.conf')

        dummy_layer_content = 'BBPATH .= ":${LAYERDIR}"\n' \
                              'BBFILES += "${LAYERDIR}/recipes-*/*/*.bb ${LAYERDIR}/recipes-*/*/*.bbappend"\n' \
                              'BBFILE_COLLECTIONS += "%s"\n' \
                              'BBFILE_PATTERN_%s = "^${LAYERDIR}/"\n' \
                              'BBFILE_PRIORITY_%s = "6"\n' % (dummy_layer_name, dummy_layer_name, dummy_layer_name)

        ftools.write_file(dummy_layer_conf_path, dummy_layer_content)

        bblayers_conf = 'BBLAYERS += "%s"\n' % dummy_layer_path
        self.write_bblayers_config(bblayers_conf)

        test_recipe = 'ed'

        ret = bitbake('-n %s' % test_recipe)

        err = 'fatal: Not a git repository'

        shutil.rmtree(dirpath)

        self.assertNotIn(err, ret.output)


class BuildhistoryTests(BuildhistoryBase):

    def test_buildhistory_basic(self):
        self.run_buildhistory_operation('xcursor-transparent-theme')
        self.assertTrue(os.path.isdir(get_bb_var('BUILDHISTORY_DIR')), "buildhistory dir was not created.")

    def test_buildhistory_buildtime_pr_backwards(self):
        target = 'xcursor-transparent-theme'
        error = "ERROR:.*QA Issue: Package version for package %s went backwards which would break package feeds \(from .*-r1.* to .*-r0.*\)" % target
        self.run_buildhistory_operation(target, target_config="PR = \"r1\"", change_bh_location=True)
        self.run_buildhistory_operation(target, target_config="PR = \"r0\"", change_bh_location=False, expect_error=True, error_regex=error)

    def test_fileinfo(self):
        self.config_buildhistory()
        bitbake('hicolor-icon-theme')
        history_dir = get_bb_var('BUILDHISTORY_DIR_PACKAGE', 'hicolor-icon-theme')
        self.assertTrue(os.path.isdir(history_dir), 'buildhistory dir was not created.')

        def load_bh(f):
            d = {}
            for line in open(f):
                split = [s.strip() for s in line.split('=', 1)]
                if len(split) > 1:
                    d[split[0]] = split[1]
            return d

        data = load_bh(os.path.join(history_dir, 'hicolor-icon-theme', 'latest'))
        self.assertIn('FILELIST', data)
        self.assertEqual(data['FILELIST'], '/usr/share/icons/hicolor/index.theme')
        self.assertGreater(int(data['PKGSIZE']), 0)

        data = load_bh(os.path.join(history_dir, 'hicolor-icon-theme-dev', 'latest'))
        if 'FILELIST' in data:
            self.assertEqual(data['FILELIST'], '/usr/share/pkgconfig/default-icon-theme.pc')
        self.assertGreater(int(data['PKGSIZE']), 0)

class ArchiverTest(OESelftestTestCase):
    def test_arch_work_dir_and_export_source(self):
        """
        Test for archiving the work directory and exporting the source files.
        """
        self.write_config("""
INHERIT += "archiver"
PACKAGE_CLASSES = "package_rpm"
ARCHIVER_MODE[src] = "original"
ARCHIVER_MODE[srpm] = "1"
""")
        res = bitbake("xcursor-transparent-theme", ignore_status=True)
        self.assertEqual(res.status, 0, "\nCouldn't build xcursortransparenttheme.\nbitbake output %s" % res.output)
        deploy_dir_src = get_bb_var('DEPLOY_DIR_SRC')
        pkgs_path = g.glob(str(deploy_dir_src) + "/allarch*/xcurs*")
        src_file_glob = str(pkgs_path[0]) + "/xcursor*.src.rpm"
        tar_file_glob = str(pkgs_path[0]) + "/xcursor*.tar.xz"
        self.assertTrue((g.glob(src_file_glob) and g.glob(tar_file_glob)), "Couldn't find .src.rpm and .tar.xz files under %s/allarch*/xcursor*" % deploy_dir_src)

class ToolchainOptions(OESelftestTestCase):
    def test_toolchain_fortran(self):
        """
        Test that Fortran works by building a Hello, World binary.
        """

        features = 'FORTRAN:forcevariable = ",fortran"\n'
        self.write_config(features)
        bitbake('fortran-helloworld')

@OETestTag("yocto-mirrors")
class SourceMirroring(OESelftestTestCase):
    # Can we download everything from the Yocto Sources Mirror over http only
    def test_yocto_source_mirror(self):
        self.write_config("""
BB_ALLOWED_NETWORKS = "downloads.yoctoproject.org"
MIRRORS = ""
DL_DIR = "${TMPDIR}/test_downloads"
STAMPS_DIR = "${TMPDIR}/test_stamps"
SSTATE_DIR = "${TMPDIR}/test_sstate-cache"
PREMIRRORS = "\\
    bzr://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    cvs://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    git://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    gitsm://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    hg://.*/.*    http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    osc://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    p4://.*/.*    http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    svn://.*/.*   http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    ftp://.*/.*      http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    http://.*/.*     http://downloads.yoctoproject.org/mirror/sources/ \\n \\
    https://.*/.*    http://downloads.yoctoproject.org/mirror/sources/ \\n"
""")

        bitbake("world --runall fetch --continue")


class Poisoning(OESelftestTestCase):
    def test_poisoning(self):
        res = bitbake("poison", ignore_status=True)
        self.assertNotEqual(res.status, 0)
        self.assertTrue("is unsafe for cross-compilation" in res.output)
