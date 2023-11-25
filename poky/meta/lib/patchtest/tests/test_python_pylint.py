# Checks related to the python code done with pylint
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only

import base
from io import StringIO
from data import PatchTestInput
from pylint.reporters.text import TextReporter
import pylint.lint as lint


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
        if not PyLint.pythonpatches:
            self.skip('No python related patches, skipping test')

    def pretest_pylint(self):
        for pythonpatch in self.pythonpatches:
            if pythonpatch.is_modified_file:
                pylint_output = StringIO()
                reporter = TextReporter(pylint_output)
                lint.Run([self.pylint_options, pythonpatch.path], reporter=reporter, exit=False)
                for line in pylint_output.readlines():
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
            pylint_output = StringIO()
            reporter = TextReporter(pylint_output)
            lint.Run([self.pylint_options, pythonpatch.path], reporter=reporter, exit=False)
            for line in pylint_output.readlines():
                    if not '*' in line:
                        if line.strip():
                            self.pylint_test[line.strip().split(' ',1)[0]] = line.strip().split(' ',1)[1]

        for issue in self.pylint_test:
             if self.pylint_test[issue] not in self.pylint_pretest.values():
                 self.fail('Errors in your Python code were encountered. Please check your code with a linter and resubmit',
                           data=[('Output', 'Please, fix the listed issues:'), ('', issue + ' ' + self.pylint_test[issue])])
