# Checks correct parsing of mboxes
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import re

class MboxFormat(base.Base):

    def test_mbox_format(self):
        if self.unidiff_parse_error:
            self.fail('Series cannot be parsed correctly due to malformed diff lines',
                      'Create the series again using git-format-patch and ensure it can be applied using git am',
                      data=[('Diff line', re.sub('^.+:\s(?<!$)','',self.unidiff_parse_error))])
