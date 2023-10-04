# Checks related to the python code done with pylint
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
from data import PatchTestInput
import pylint.epylint as lint

class PyLint(base.Base):
    pythonpatches  = []
    pylint_pretest = {}
    pylint_test    = {}
    pylint_options = " -E --disable='E0611, E1101, F0401, E0602' --msg-template='L:{line} F:{module} I:{msg}'"

    @classmethod
    def setUpClassLocal(cls):
        # get just those patches touching python files
        cls.pythonpatches = []
        for patch in cls.patchset:
            if patch.path.endswith('.py'):
                if not patch.is_removed_file:
                    cls.pythonpatches.append(patch)

    def setUp(self):
        if self.unidiff_parse_error:
            self.skip('Python-unidiff parse error')
        if not PatchTestInput.repo.canbemerged:
            self.skip('Patch cannot be merged, no reason to execute the test method')
        if not PyLint.pythonpatches:
            self.skip('No python related patches, skipping test')

    def pretest_pylint(self):
        for pythonpatch in self.pythonpatches:
            if pythonpatch.is_modified_file:
                (pylint_stdout, pylint_stderr) = lint.py_run(command_options = pythonpatch.path + self.pylint_options, return_std=True)
                for line in pylint_stdout.readlines():
                    if not '*' in line:
                        if line.strip():
                            self.pylint_pretest[line.strip().split(' ',1)[0]] = line.strip().split(' ',1)[1]

    def test_pylint(self):
        for pythonpatch in self.pythonpatches:
            # a condition checking whether a file is renamed or not
            # unidiff doesn't support this yet
            if pythonpatch.target_file is not pythonpatch.path:
                path = pythonpatch.target_file[2:]
            else:
                path = pythonpatch.path
            (pylint_stdout, pylint_stderr) = lint.py_run(command_options = path + self.pylint_options, return_std=True)
            for line in pylint_stdout.readlines():
                    if not '*' in line:
                        if line.strip():
                            self.pylint_test[line.strip().split(' ',1)[0]] = line.strip().split(' ',1)[1]

        for issue in self.pylint_test:
             if self.pylint_test[issue] not in self.pylint_pretest.values():
                 self.fail('Errors in your Python code were encountered',
                           'Correct the lines introduced by your patch',
                           data=[('Output', 'Please, fix the listed issues:'), ('', issue + ' ' + self.pylint_test[issue])])
