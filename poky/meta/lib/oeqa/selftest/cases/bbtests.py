#
# SPDX-License-Identifier: MIT
#

import os
import re

import oeqa.utils.ftools as ftools
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_bb_vars

from oeqa.selftest.case import OESelftestTestCase

class BitbakeTests(OESelftestTestCase):

    def getline(self, res, line):
        for l in res.output.split('\n'):
            if line in l:
                return l

    # Test bitbake can run from the <builddir>/conf directory
    def test_run_bitbake_from_dir_1(self):
        os.chdir(os.path.join(self.builddir, 'conf'))
        self.assertEqual(bitbake('-e').status, 0, msg = "bitbake couldn't run from \"conf\" dir")

    # Test bitbake can run from the <builddir>'s parent directory
    def test_run_bitbake_from_dir_2(self):
        my_env = os.environ.copy()
        my_env['BBPATH'] = my_env['BUILDDIR']
        os.chdir(os.path.dirname(os.environ['BUILDDIR']))
        self.assertEqual(bitbake('-e', env=my_env).status, 0, msg = "bitbake couldn't run from builddir's parent directory")

    # Test bitbake can run from some other random system location (we use /tmp/)
    def test_run_bitbake_from_dir_3(self):
        my_env = os.environ.copy()
        my_env['BBPATH'] = my_env['BUILDDIR']
        os.chdir("/tmp/")
        self.assertEqual(bitbake('-e', env=my_env).status, 0, msg = "bitbake couldn't run from /tmp/")


    def test_event_handler(self):
        self.write_config("INHERIT += \"test_events\"")
        result = bitbake('m4-native')
        find_build_started = re.search(r"NOTE: Test for bb\.event\.BuildStarted(\n.*)*NOTE: Executing.*Tasks", result.output)
        find_build_completed = re.search(r"Tasks Summary:.*(\n.*)*NOTE: Test for bb\.event\.BuildCompleted", result.output)
        self.assertTrue(find_build_started, msg = "Match failed in:\n%s"  % result.output)
        self.assertTrue(find_build_completed, msg = "Match failed in:\n%s" % result.output)
        self.assertNotIn('Test for bb.event.InvalidEvent', result.output)

    def test_local_sstate(self):
        bitbake('m4-native')
        bitbake('m4-native -cclean')
        result = bitbake('m4-native')
        find_setscene = re.search("m4-native.*do_.*_setscene", result.output)
        self.assertTrue(find_setscene, msg = "No \"m4-native.*do_.*_setscene\" message found during bitbake m4-native. bitbake output: %s" % result.output )

    def test_bitbake_invalid_recipe(self):
        result = bitbake('-b asdf', ignore_status=True)
        self.assertTrue("ERROR: Unable to find any recipe file matching 'asdf'" in result.output, msg = "Though asdf recipe doesn't exist, bitbake didn't output any err. message. bitbake output: %s" % result.output)

    def test_bitbake_invalid_target(self):
        result = bitbake('asdf', ignore_status=True)
        self.assertIn("ERROR: Nothing PROVIDES 'asdf'", result.output)

    def test_warnings_errors(self):
        result = bitbake('-b asdf', ignore_status=True)
        find_warnings = re.search("Summary: There w.{2,3}? [1-9][0-9]* WARNING messages* shown", result.output)
        find_errors = re.search("Summary: There w.{2,3}? [1-9][0-9]* ERROR messages* shown", result.output)
        self.assertTrue(find_warnings, msg="Did not find the mumber of warnings at the end of the build:\n" + result.output)
        self.assertTrue(find_errors, msg="Did not find the mumber of errors at the end of the build:\n" + result.output)

    def test_invalid_patch(self):
        # This patch should fail to apply.
        self.write_recipeinc('man-db', 'FILESEXTRAPATHS_prepend := "${THISDIR}/files:"\nSRC_URI += "file://0001-Test-patch-here.patch"')
        self.write_config("INHERIT_remove = \"report-error\"")
        result = bitbake('man-db -c patch', ignore_status=True)
        self.delete_recipeinc('man-db')
        bitbake('-cclean man-db')
        found = False
        for l in result.output.split('\n'):
            if l.startswith("ERROR:") and "failed" in l and "do_patch" in l:
                found = l
        self.assertTrue(found and found.startswith("ERROR:"), msg = "Incorrectly formed patch application didn't fail. bitbake output: %s" % result.output)

    def test_force_task_1(self):
        # test 1 from bug 5875
        test_recipe = 'zlib'
        test_data = "Microsoft Made No Profit From Anyone's Zunes Yo"
        bb_vars = get_bb_vars(['D', 'PKGDEST', 'mandir'], test_recipe)
        image_dir = bb_vars['D']
        pkgsplit_dir = bb_vars['PKGDEST']
        man_dir = bb_vars['mandir']
        self.write_config("PACKAGE_CLASSES = \"package_rpm\"")

        bitbake('-c clean %s' % test_recipe)
        bitbake('-c package -f %s' % test_recipe)
        self.add_command_to_tearDown('bitbake -c clean %s' % test_recipe)

        man_file = os.path.join(image_dir + man_dir, 'man3/zlib.3')
        ftools.append_file(man_file, test_data)
        bitbake('-c package -f %s' % test_recipe)

        man_split_file = os.path.join(pkgsplit_dir, 'zlib-doc' + man_dir, 'man3/zlib.3')
        man_split_content = ftools.read_file(man_split_file)
        self.assertIn(test_data, man_split_content, 'The man file has not changed in packages-split.')

        ret = bitbake(test_recipe)
        self.assertIn('task do_package_write_rpm:', ret.output, 'Task do_package_write_rpm did not re-executed.')

    def test_force_task_2(self):
        # test 2 from bug 5875
        test_recipe = 'zlib'

        bitbake(test_recipe)
        self.add_command_to_tearDown('bitbake -c clean %s' % test_recipe)

        result = bitbake('-C compile %s' % test_recipe)
        look_for_tasks = ['do_compile:', 'do_install:', 'do_populate_sysroot:', 'do_package:']
        for task in look_for_tasks:
            self.assertIn(task, result.output, msg="Couldn't find %s task.")

    def test_bitbake_g(self):
        recipe = 'base-files'
        result = bitbake('-g %s' % recipe)
        for f in ['pn-buildlist', 'task-depends.dot']:
            self.addCleanup(os.remove, f)
        self.assertTrue('Task dependencies saved to \'task-depends.dot\'' in result.output, msg = "No task dependency \"task-depends.dot\" file was generated for the given task target. bitbake output: %s" % result.output)
        self.assertIn(recipe, ftools.read_file(os.path.join(self.builddir, 'task-depends.dot')))

    def test_image_manifest(self):
        bitbake('core-image-minimal')
        bb_vars = get_bb_vars(["DEPLOY_DIR_IMAGE", "IMAGE_LINK_NAME"], "core-image-minimal")
        deploydir = bb_vars["DEPLOY_DIR_IMAGE"]
        imagename = bb_vars["IMAGE_LINK_NAME"]
        manifest = os.path.join(deploydir, imagename + ".manifest")
        self.assertTrue(os.path.islink(manifest), msg="No manifest file created for image. It should have been created in %s" % manifest)

    def test_invalid_recipe_src_uri(self):
        data = 'SRC_URI = "file://invalid"'
        self.write_recipeinc('man-db', data)
        self.write_config("""DL_DIR = \"${TOPDIR}/download-selftest\"
SSTATE_DIR = \"${TOPDIR}/download-selftest\"
INHERIT_remove = \"report-error\"
""")
        self.track_for_cleanup(os.path.join(self.builddir, "download-selftest"))

        bitbake('-ccleanall man-db')
        result = bitbake('-c fetch man-db', ignore_status=True)
        bitbake('-ccleanall man-db')
        self.delete_recipeinc('man-db')
        self.assertEqual(result.status, 1, msg="Command succeded when it should have failed. bitbake output: %s" % result.output)
        self.assertIn('Fetcher failure: Unable to find file file://invalid anywhere. The paths that were searched were:', result.output)
        line = self.getline(result, 'Fetcher failure for URL: \'file://invalid\'. Unable to fetch URL from any source.')
        self.assertTrue(line and line.startswith("ERROR:"), msg = "\"invalid\" file \
doesn't exist, yet fetcher didn't report any error. bitbake output: %s" % result.output)

    def test_rename_downloaded_file(self):
        # TODO unique dldir instead of using cleanall
        # TODO: need to set sstatedir?
        self.write_config("""DL_DIR = \"${TOPDIR}/download-selftest\"
SSTATE_DIR = \"${TOPDIR}/download-selftest\"
""")
        self.track_for_cleanup(os.path.join(self.builddir, "download-selftest"))

        data = 'SRC_URI = "${GNU_MIRROR}/aspell/aspell-${PV}.tar.gz;downloadfilename=test-aspell.tar.gz"'
        self.write_recipeinc('aspell', data)
        result = bitbake('-f -c fetch aspell', ignore_status=True)
        self.delete_recipeinc('aspell')
        self.assertEqual(result.status, 0, msg = "Couldn't fetch aspell. %s" % result.output)
        dl_dir = get_bb_var("DL_DIR")
        self.assertTrue(os.path.isfile(os.path.join(dl_dir, 'test-aspell.tar.gz')), msg = "File rename failed. No corresponding test-aspell.tar.gz file found under %s" % dl_dir)
        self.assertTrue(os.path.isfile(os.path.join(dl_dir, 'test-aspell.tar.gz.done')), "File rename failed. No corresponding test-aspell.tar.gz.done file found under %s" % dl_dir)

    def test_environment(self):
        self.write_config("TEST_ENV=\"localconf\"")
        result = runCmd('bitbake -e | grep TEST_ENV=')
        self.assertIn('localconf', result.output)

    def test_dry_run(self):
        result = runCmd('bitbake -n m4-native')
        self.assertEqual(0, result.status, "bitbake dry run didn't run as expected. %s" % result.output)

    def test_just_parse(self):
        result = runCmd('bitbake -p')
        self.assertEqual(0, result.status, "errors encountered when parsing recipes. %s" % result.output)

    def test_version(self):
        result = runCmd('bitbake -s | grep wget')
        find = re.search(r"wget *:([0-9a-zA-Z\.\-]+)", result.output)
        self.assertTrue(find, "No version returned for searched recipe. bitbake output: %s" % result.output)

    def test_prefile(self):
        preconf = os.path.join(self.builddir, 'conf/prefile.conf')
        self.track_for_cleanup(preconf)
        ftools.write_file(preconf ,"TEST_PREFILE=\"prefile\"")
        result = runCmd('bitbake -r conf/prefile.conf -e | grep TEST_PREFILE=')
        self.assertIn('prefile', result.output)
        self.write_config("TEST_PREFILE=\"localconf\"")
        result = runCmd('bitbake -r conf/prefile.conf -e | grep TEST_PREFILE=')
        self.assertIn('localconf', result.output)

    def test_postfile(self):
        postconf = os.path.join(self.builddir, 'conf/postfile.conf')
        self.track_for_cleanup(postconf)
        ftools.write_file(postconf , "TEST_POSTFILE=\"postfile\"")
        self.write_config("TEST_POSTFILE=\"localconf\"")
        result = runCmd('bitbake -R conf/postfile.conf -e | grep TEST_POSTFILE=')
        self.assertIn('postfile', result.output)

    def test_checkuri(self):
        result = runCmd('bitbake -c checkuri m4')
        self.assertEqual(0, result.status, msg = "\"checkuri\" task was not executed. bitbake output: %s" % result.output)

    def test_continue(self):
        self.write_config("""DL_DIR = \"${TOPDIR}/download-selftest\"
SSTATE_DIR = \"${TOPDIR}/download-selftest\"
INHERIT_remove = \"report-error\"
""")
        self.track_for_cleanup(os.path.join(self.builddir, "download-selftest"))
        self.write_recipeinc('man-db',"\ndo_fail_task () {\nexit 1 \n}\n\naddtask do_fail_task before do_fetch\n" )
        runCmd('bitbake -c cleanall man-db xcursor-transparent-theme')
        result = runCmd('bitbake -c unpack -k man-db xcursor-transparent-theme', ignore_status=True)
        errorpos = result.output.find('ERROR: Function failed: do_fail_task')
        manver = re.search("NOTE: recipe xcursor-transparent-theme-(.*?): task do_unpack: Started", result.output)
        continuepos = result.output.find('NOTE: recipe xcursor-transparent-theme-%s: task do_unpack: Started' % manver.group(1))
        self.assertLess(errorpos,continuepos, msg = "bitbake didn't pass do_fail_task. bitbake output: %s" % result.output)

    def test_non_gplv3(self):
        self.write_config('INCOMPATIBLE_LICENSE = "GPLv3"')
        result = bitbake('selftest-ed', ignore_status=True)
        self.assertEqual(result.status, 0, "Bitbake failed, exit code %s, output %s" % (result.status, result.output))
        lic_dir = get_bb_var('LICENSE_DIRECTORY')
        self.assertFalse(os.path.isfile(os.path.join(lic_dir, 'selftest-ed/generic_GPLv3')))
        self.assertTrue(os.path.isfile(os.path.join(lic_dir, 'selftest-ed/generic_GPLv2')))

    def test_setscene_only(self):
        """ Bitbake option to restore from sstate only within a build (i.e. execute no real tasks, only setscene)"""
        test_recipe = 'ed'

        bitbake(test_recipe)
        bitbake('-c clean %s' % test_recipe)
        ret = bitbake('--setscene-only %s' % test_recipe)

        tasks = re.findall(r'task\s+(do_\S+):', ret.output)

        for task in tasks:
            self.assertIn('_setscene', task, 'A task different from _setscene ran: %s.\n'
                                             'Executed tasks were: %s' % (task, str(tasks)))

    def test_skip_setscene(self):
        test_recipe = 'ed'

        bitbake(test_recipe)
        bitbake('-c clean %s' % test_recipe)

        ret = bitbake('--setscene-only %s' % test_recipe)
        tasks = re.findall(r'task\s+(do_\S+):', ret.output)

        for task in tasks:
            self.assertIn('_setscene', task, 'A task different from _setscene ran: %s.\n'
                                             'Executed tasks were: %s' % (task, str(tasks)))

        # Run without setscene. Should do nothing
        ret = bitbake('--skip-setscene %s' % test_recipe)
        tasks = re.findall(r'task\s+(do_\S+):', ret.output)

        self.assertFalse(tasks, 'Tasks %s ran when they should not have' % (str(tasks)))

        # Clean (leave sstate cache) and run with --skip-setscene. No setscene
        # tasks should run
        bitbake('-c clean %s' % test_recipe)

        ret = bitbake('--skip-setscene %s' % test_recipe)
        tasks = re.findall(r'task\s+(do_\S+):', ret.output)

        for task in tasks:
            self.assertNotIn('_setscene', task, 'A _setscene task ran: %s.\n'
                                                'Executed tasks were: %s' % (task, str(tasks)))

    def test_bbappend_order(self):
        """ Bitbake should bbappend to recipe in a predictable order """
        test_recipe = 'ed'
        bb_vars = get_bb_vars(['SUMMARY', 'PV'], test_recipe)
        test_recipe_summary_before = bb_vars['SUMMARY']
        test_recipe_pv = bb_vars['PV']
        recipe_append_file = test_recipe + '_' + test_recipe_pv + '.bbappend'
        expected_recipe_summary = test_recipe_summary_before

        for i in range(5):
            recipe_append_dir = test_recipe + '_test_' + str(i)
            recipe_append_path = os.path.join(self.testlayer_path, 'recipes-test', recipe_append_dir, recipe_append_file)
            os.mkdir(os.path.join(self.testlayer_path, 'recipes-test', recipe_append_dir))
            feature = 'SUMMARY += "%s"\n' % i
            ftools.write_file(recipe_append_path, feature)
            expected_recipe_summary += ' %s' % i

        self.add_command_to_tearDown('rm -rf %s' % os.path.join(self.testlayer_path, 'recipes-test',
                                                               test_recipe + '_test_*'))

        test_recipe_summary_after = get_bb_var('SUMMARY', test_recipe)
        self.assertEqual(expected_recipe_summary, test_recipe_summary_after)
