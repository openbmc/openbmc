import os
import re
import glob as g
import shutil
import tempfile
from oeqa.selftest.base import oeSelfTest
from oeqa.selftest.buildhistory import BuildhistoryBase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars
import oeqa.utils.ftools as ftools
from oeqa.utils.decorators import testcase

class ImageOptionsTests(oeSelfTest):

    @testcase(761)
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
        incremental_created = re.search("Installing  : packagegroup-core-ssh-openssh", log_data_created)
        self.remove_config('IMAGE_FEATURES += "ssh-server-openssh"')
        self.assertTrue(incremental_created, msg = "Match failed in:\n%s" % log_data_created)
        bitbake("core-image-minimal")
        log_data_removed = ftools.read_file(log_data_file)
        incremental_removed = re.search("Erasing     : packagegroup-core-ssh-openssh", log_data_removed)
        self.assertTrue(incremental_removed, msg = "Match failed in:\n%s" % log_data_removed)

    @testcase(286)
    def test_ccache_tool(self):
        bitbake("ccache-native")
        bb_vars = get_bb_vars(['SYSROOT_DESTDIR', 'bindir'], 'ccache-native')
        p = bb_vars['SYSROOT_DESTDIR'] + bb_vars['bindir'] + "/" + "ccache"
        self.assertTrue(os.path.isfile(p), msg = "No ccache found (%s)" % p)
        self.write_config('INHERIT += "ccache"')
        self.add_command_to_tearDown('bitbake -c clean m4')
        bitbake("m4 -f -c compile")
        log_compile = os.path.join(get_bb_var("WORKDIR","m4"), "temp/log.do_compile")
        res = runCmd("grep ccache %s" % log_compile, ignore_status=True)
        self.assertEqual(0, res.status, msg="No match for ccache in m4 log.do_compile. For further details: %s" % log_compile)

    @testcase(1435)
    def test_read_only_image(self):
        distro_features = get_bb_var('DISTRO_FEATURES')
        if not ('x11' in distro_features and 'opengl' in distro_features):
            self.skipTest('core-image-sato requires x11 and opengl in distro features')
        self.write_config('IMAGE_FEATURES += "read-only-rootfs"')
        bitbake("core-image-sato")
        # do_image will fail if there are any pending postinsts

class DiskMonTest(oeSelfTest):

    @testcase(277)
    def test_stoptask_behavior(self):
        self.write_config('BB_DISKMON_DIRS = "STOPTASKS,${TMPDIR},100000G,100K"')
        res = bitbake("m4", ignore_status = True)
        self.assertTrue('ERROR: No new tasks can be executed since the disk space monitor action is "STOPTASKS"!' in res.output, msg = "Tasks should have stopped. Disk monitor is set to STOPTASK: %s" % res.output)
        self.assertEqual(res.status, 1, msg = "bitbake reported exit code %s. It should have been 1. Bitbake output: %s" % (str(res.status), res.output))
        self.write_config('BB_DISKMON_DIRS = "ABORT,${TMPDIR},100000G,100K"')
        res = bitbake("m4", ignore_status = True)
        self.assertTrue('ERROR: Immediately abort since the disk space monitor action is "ABORT"!' in res.output, "Tasks should have been aborted immediatelly. Disk monitor is set to ABORT: %s" % res.output)
        self.assertEqual(res.status, 1, msg = "bitbake reported exit code %s. It should have been 1. Bitbake output: %s" % (str(res.status), res.output))
        self.write_config('BB_DISKMON_DIRS = "WARN,${TMPDIR},100000G,100K"')
        res = bitbake("m4")
        self.assertTrue('WARNING: The free space' in res.output, msg = "A warning should have been displayed for disk monitor is set to WARN: %s" %res.output)

class SanityOptionsTest(oeSelfTest):
    def getline(self, res, line):
        for l in res.output.split('\n'):
            if line in l:
                return l

    @testcase(927)
    def test_options_warnqa_errorqa_switch(self):

        self.write_config("INHERIT_remove = \"report-error\"")
        if "packages-list" not in get_bb_var("ERROR_QA"):
            self.append_config("ERROR_QA_append = \" packages-list\"")

        self.write_recipeinc('xcursor-transparent-theme', 'PACKAGES += \"${PN}-dbg\"')
        self.add_command_to_tearDown('bitbake -c clean xcursor-transparent-theme')
        res = bitbake("xcursor-transparent-theme -f -c package", ignore_status=True)
        self.delete_recipeinc('xcursor-transparent-theme')
        line = self.getline(res, "QA Issue: xcursor-transparent-theme-dbg is listed in PACKAGES multiple times, this leads to packaging errors.")
        self.assertTrue(line and line.startswith("ERROR:"), msg=res.output)
        self.assertEqual(res.status, 1, msg = "bitbake reported exit code %s. It should have been 1. Bitbake output: %s" % (str(res.status), res.output))
        self.write_recipeinc('xcursor-transparent-theme', 'PACKAGES += \"${PN}-dbg\"')
        self.append_config('ERROR_QA_remove = "packages-list"')
        self.append_config('WARN_QA_append = " packages-list"')
        res = bitbake("xcursor-transparent-theme -f -c package")
        self.delete_recipeinc('xcursor-transparent-theme')
        line = self.getline(res, "QA Issue: xcursor-transparent-theme-dbg is listed in PACKAGES multiple times, this leads to packaging errors.")
        self.assertTrue(line and line.startswith("WARNING:"), msg=res.output)

    @testcase(278)
    def test_sanity_unsafe_script_references(self):
        self.write_config('WARN_QA_append = " unsafe-references-in-scripts"')

        self.add_command_to_tearDown('bitbake -c clean gzip')
        res = bitbake("gzip -f -c package_qa")
        line = self.getline(res, "QA Issue: gzip")
        self.assertFalse(line, "WARNING: QA Issue: gzip message is present in bitbake's output and shouldn't be: %s" % res.output)

        self.append_config("""
do_install_append_pn-gzip () {
	echo "\n${bindir}/test" >> ${D}${bindir}/zcat
}
""")
        res = bitbake("gzip -f -c package_qa")
        line = self.getline(res, "QA Issue: gzip")
        self.assertTrue(line and line.startswith("WARNING:"), "WARNING: QA Issue: gzip message is not present in bitbake's output: %s" % res.output)

    @testcase(1421)
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

    @testcase(293)
    def test_buildhistory_basic(self):
        self.run_buildhistory_operation('xcursor-transparent-theme')
        self.assertTrue(os.path.isdir(get_bb_var('BUILDHISTORY_DIR')), "buildhistory dir was not created.")

    @testcase(294)
    def test_buildhistory_buildtime_pr_backwards(self):
        target = 'xcursor-transparent-theme'
        error = "ERROR:.*QA Issue: Package version for package %s went backwards which would break package feeds from (.*-r1.* to .*-r0.*)" % target
        self.run_buildhistory_operation(target, target_config="PR = \"r1\"", change_bh_location=True)
        self.run_buildhistory_operation(target, target_config="PR = \"r0\"", change_bh_location=False, expect_error=True, error_regex=error)

class ArchiverTest(oeSelfTest):
    @testcase(926)
    def test_arch_work_dir_and_export_source(self):
        """
        Test for archiving the work directory and exporting the source files.
        """
        self.write_config("INHERIT += \"archiver\"\nARCHIVER_MODE[src] = \"original\"\nARCHIVER_MODE[srpm] = \"1\"")
        res = bitbake("xcursor-transparent-theme", ignore_status=True)
        self.assertEqual(res.status, 0, "\nCouldn't build xcursortransparenttheme.\nbitbake output %s" % res.output)
        deploy_dir_src = get_bb_var('DEPLOY_DIR_SRC')
        pkgs_path = g.glob(str(deploy_dir_src) + "/allarch*/xcurs*")
        src_file_glob = str(pkgs_path[0]) + "/xcursor*.src.rpm"
        tar_file_glob = str(pkgs_path[0]) + "/xcursor*.tar.gz"
        self.assertTrue((g.glob(src_file_glob) and g.glob(tar_file_glob)), "Couldn't find .src.rpm and .tar.gz files under %s/allarch*/xcursor*" % deploy_dir_src)
