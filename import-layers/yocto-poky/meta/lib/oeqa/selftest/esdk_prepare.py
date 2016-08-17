#!/usr/bin/env python

import shutil, tempfile
import sys
import os
import imp
import unittest
try:
    from oeqa.utils.commands import get_bb_var
except ImportError:
    pass

# module under test
module_file_name = "ext-sdk-prepare.py"
module_path = ""

class ExtSdkPrepareTest(unittest.TestCase):

    """ unit test for fix for Yocto #9019 """

    @classmethod
    def setUpClass(self):
        # copy module under test to temp dir
        self.test_dir = tempfile.mkdtemp()
        module_dest_path = os.path.join(self.test_dir, module_file_name)
        try:
            shutil.copy(module_path, self.test_dir)
            # load module under test
            self.test_mod = imp.load_source("", module_dest_path)
        except:
            print "error: unable to copy or load %s [src: %s, dst: %s]" % \
                    (module_file_name, module_path, module_dest_path)
            sys.exit(1)

    def test_prepare_unexpected(self):
        # test data
        # note: pathnames have been truncated from the actual bitbake
        # output as they are not important for the test.
        test_data = ( 
            'NOTE: Running noexec task 9 of 6539 (ID: 28, quilt/quilt-native_0.64.bb, do_build)\n'
            'NOTE: Running task 10 of 6539 (ID: 29, quilt/quilt-native_0.64.bb, do_package)\n'
            'NOTE: Running task 11 of 6539 (ID: 30, quilt/quilt-native_0.64.bb, do_rm_work)\n'
            'NOTE: Running noexec task 6402 of 6539 (ID: 1, images/core-image-sato.bb, do_patch)\n'
            'NOTE: Running task 6538 of 6539 (ID: 14, images/core-image-sato.bb, do_rm_work)\n'
        )
        # expected warning output
        expected = [ (' task 10 of 6539 (ID: 29, quilt/quilt-native_0.64.bb, do_package)') ]
        # recipe to test, matching test input data
        recipes = [ "core-image-sato.bb" ]

        # run the test
        output = self.test_mod.check_unexpected(test_data, recipes)
        self.assertEqual(output, expected)

    @classmethod
    def tearDownClass(self):
        # remove temp dir
        shutil.rmtree(self.test_dir)

if __name__ == '__main__':
    # running from command line - i.e., not under oe-selftest
    # directory containing module under test comes from command line
    if len(sys.argv) == 2 and os.path.isdir(sys.argv[1]):
        module_path = os.path.join(sys.argv[1], module_file_name)
        suite = unittest.TestLoader().loadTestsFromTestCase(ExtSdkPrepareTest)
        unittest.TextTestRunner().run(suite)
    else:
        progname = os.path.basename(sys.argv[0])
        print "%s: missing directory path" % progname
        print "usage: %s /path/to/directory-of(ext-sdk-prepare.py)" % progname
        sys.exit(1)
else:
    # running under oe-selftest
    # determine module source dir from COREBASE and expected path
    module_path = os.path.join(get_bb_var("COREBASE"), "meta", "files", module_file_name)
