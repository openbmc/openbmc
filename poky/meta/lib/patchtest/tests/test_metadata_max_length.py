# Checks related to patch line lengths
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0

import base
import re

class MaxLength(base.Base):
    add_mark = re.compile('\+ ')
    max_length = 200

    def test_max_line_length(self):
        for patch in self.patchset:
            # for the moment, we are just interested in metadata
            if patch.path.endswith('.patch'):
                continue
            payload = str(patch)
            for line in payload.splitlines():
                if self.add_mark.match(line):
                    current_line_length = len(line[1:])
                    if current_line_length > self.max_length:
                        self.fail('Patch line too long (current length %s)' % current_line_length,
                                  'Shorten the corresponding patch line (max length supported %s)' % self.max_length,
                                  data=[('Patch', patch.path), ('Line', '%s ...' % line[0:80])])
