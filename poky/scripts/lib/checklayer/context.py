# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import sys
import glob
import re

from oeqa.core.context import OETestContext

class CheckLayerTestContext(OETestContext):
    def __init__(self, td=None, logger=None, layer=None, test_software_layer_signatures=True):
        super(CheckLayerTestContext, self).__init__(td, logger)
        self.layer = layer
        self.test_software_layer_signatures = test_software_layer_signatures
