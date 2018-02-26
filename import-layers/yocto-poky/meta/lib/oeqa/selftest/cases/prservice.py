import os
import re
import shutil
import datetime

import oeqa.utils.ftools as ftools
from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd, bitbake, get_bb_var
from oeqa.core.decorator.oeid import OETestID
from oeqa.utils.network import get_free_port

class BitbakePrTests(OESelftestTestCase):

    @classmethod
    def setUpClass(cls):
        super(BitbakePrTests, cls).setUpClass()
        cls.pkgdata_dir = get_bb_var('PKGDATA_DIR')

    def get_pr_version(self, package_name):
        package_data_file = os.path.join(self.pkgdata_dir, 'runtime', package_name)
        package_data = ftools.read_file(package_data_file)
        find_pr = re.search("PKGR: r[0-9]+\.([0-9]+)", package_data)
        self.assertTrue(find_pr, "No PKG revision found in %s" % package_data_file)
        return int(find_pr.group(1))

    def get_task_stamp(self, package_name, recipe_task):
        stampdata = get_bb_var('STAMP', target=package_name).split('/')
        prefix = stampdata[-1]
        package_stamps_path = "/".join(stampdata[:-1])
        stamps = []
        for stamp in os.listdir(package_stamps_path):
            find_stamp = re.match("%s\.%s\.([a-z0-9]{32})" % (re.escape(prefix), recipe_task), stamp)
            if find_stamp:
                stamps.append(find_stamp.group(1))
        self.assertFalse(len(stamps) == 0, msg="Cound not find stamp for task %s for recipe %s" % (recipe_task, package_name))
        self.assertFalse(len(stamps) > 1, msg="Found multiple %s stamps for the %s recipe in the %s directory." % (recipe_task, package_name, package_stamps_path))
        return str(stamps[0])

    def increment_package_pr(self, package_name):
        inc_data = "do_package_append() {\n    bb.build.exec_func('do_test_prserv', d)\n}\ndo_test_prserv() {\necho \"The current date is: %s\"\n}" % datetime.datetime.now()
        self.write_recipeinc(package_name, inc_data)
        res = bitbake(package_name, ignore_status=True)
        self.delete_recipeinc(package_name)
        self.assertEqual(res.status, 0, msg=res.output)

    def config_pr_tests(self, package_name, package_type='rpm', pr_socket='localhost:0'):
        config_package_data = 'PACKAGE_CLASSES = "package_%s"' % package_type
        self.write_config(config_package_data)
        config_server_data = 'PRSERV_HOST = "%s"' % pr_socket
        self.append_config(config_server_data)

    def run_test_pr_service(self, package_name, package_type='rpm', track_task='do_package', pr_socket='localhost:0'):
        self.config_pr_tests(package_name, package_type, pr_socket)

        self.increment_package_pr(package_name)
        pr_1 = self.get_pr_version(package_name)
        stamp_1 = self.get_task_stamp(package_name, track_task)

        self.increment_package_pr(package_name)
        pr_2 = self.get_pr_version(package_name)
        stamp_2 = self.get_task_stamp(package_name, track_task)

        self.assertTrue(pr_2 - pr_1 == 1, "Step between same pkg. revision is greater than 1")
        self.assertTrue(stamp_1 != stamp_2, "Different pkg rev. but same stamp: %s" % stamp_1)

    def run_test_pr_export_import(self, package_name, replace_current_db=True):
        self.config_pr_tests(package_name)

        self.increment_package_pr(package_name)
        pr_1 = self.get_pr_version(package_name)

        exported_db_path = os.path.join(self.builddir, 'export.inc')
        export_result = runCmd("bitbake-prserv-tool export %s" % exported_db_path, ignore_status=True)
        self.assertEqual(export_result.status, 0, msg="PR Service database export failed: %s" % export_result.output)
        self.assertTrue(os.path.exists(exported_db_path))

        if replace_current_db:
            current_db_path = os.path.join(get_bb_var('PERSISTENT_DIR'), 'prserv.sqlite3')
            self.assertTrue(os.path.exists(current_db_path), msg="Path to current PR Service database is invalid: %s" % current_db_path)
            os.remove(current_db_path)

        import_result = runCmd("bitbake-prserv-tool import %s" % exported_db_path, ignore_status=True)
        os.remove(exported_db_path)
        self.assertEqual(import_result.status, 0, msg="PR Service database import failed: %s" % import_result.output)

        self.increment_package_pr(package_name)
        pr_2 = self.get_pr_version(package_name)

        self.assertTrue(pr_2 - pr_1 == 1, "Step between same pkg. revision is greater than 1")

    @OETestID(930)
    def test_import_export_replace_db(self):
        self.run_test_pr_export_import('m4')

    @OETestID(931)
    def test_import_export_override_db(self):
        self.run_test_pr_export_import('m4', replace_current_db=False)

    @OETestID(932)
    def test_pr_service_rpm_arch_dep(self):
        self.run_test_pr_service('m4', 'rpm', 'do_package')

    @OETestID(934)
    def test_pr_service_deb_arch_dep(self):
        self.run_test_pr_service('m4', 'deb', 'do_package')

    @OETestID(933)
    def test_pr_service_ipk_arch_dep(self):
        self.run_test_pr_service('m4', 'ipk', 'do_package')

    @OETestID(935)
    def test_pr_service_rpm_arch_indep(self):
        self.run_test_pr_service('xcursor-transparent-theme', 'rpm', 'do_package')

    @OETestID(937)
    def test_pr_service_deb_arch_indep(self):
        self.run_test_pr_service('xcursor-transparent-theme', 'deb', 'do_package')

    @OETestID(936)
    def test_pr_service_ipk_arch_indep(self):
        self.run_test_pr_service('xcursor-transparent-theme', 'ipk', 'do_package')

    @OETestID(1419)
    def test_stopping_prservice_message(self):
        port = get_free_port()

        runCmd('bitbake-prserv --host localhost --port %s --loglevel=DEBUG --start' % port)
        ret = runCmd('bitbake-prserv --host localhost --port %s --loglevel=DEBUG --stop' % port)

        self.assertEqual(ret.status, 0)

