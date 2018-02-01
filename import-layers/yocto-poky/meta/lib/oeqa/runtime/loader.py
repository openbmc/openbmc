# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from oeqa.core.loader import OETestLoader
from oeqa.runtime.case import OERuntimeTestCase

class OERuntimeTestLoader(OETestLoader):
    caseClass = OERuntimeTestCase

    def _getTestCase(self, testCaseClass, tcName):
        case = super(OERuntimeTestLoader, self)._getTestCase(testCaseClass, tcName)

        # Adds custom attributes to the OERuntimeTestCase
        setattr(case, 'target', self.tc.target)

        return case
