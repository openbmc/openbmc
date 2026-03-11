#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import importlib
import oeqa.selftest
from oeqa.selftest.case import OESelftestTestCase

class ExternalLayer(OESelftestTestCase):

    def test_list_imported(self):
        """
        Summary: Checks functionality to import tests from other layers.
        Expected: 1. File "external-layer.py" must be in
        oeqa.selftest.__path__
                  2. test_unconditional_pas method must exists
                     in ImportedTests class
        Product: oe-core
        Author: Mariano Lopez <mariano.lopez@intel.com>
        """

        test_file = "external-layer.py"
        test_module = "oeqa.selftest.cases.external-layer"
        method_name = "test_unconditional_pass"

        # Check if "external-layer.py" is in oeqa path
        found_file = search_test_file(test_file)
        self.assertTrue(found_file, msg="Can't find %s in the oeqa path" % test_file)

        # Import oeqa.selftest.external-layer module and search for
        # test_unconditional_pass method of ImportedTests class
        found_method = search_method(test_module, method_name)
        self.assertTrue(method_name, msg="Can't find %s method" % method_name)

def search_test_file(file_name):
    for layer_path in oeqa.selftest.__path__:
        for _, _, files in os.walk(layer_path):
            for f in files:
                if f == file_name:
                    return True
    return False

def search_method(module, method):
    modlib = importlib.import_module(module)
    for var in vars(modlib):
        klass = vars(modlib)[var]
        if isinstance(klass, type(OESelftestTestCase)) and issubclass(klass, OESelftestTestCase):
            for m in dir(klass):
                if m == method:
                    return True
    return False

