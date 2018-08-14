#!/usr/bin/env python3

# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import unittest
import logging
import tempfile

from common import setup_sys_path, TestBase
setup_sys_path()

from oeqa.core.runner import OEStreamLogger

class TestRunner(TestBase):
    def test_stream_logger(self):
        fp = tempfile.TemporaryFile(mode='w+')

        logging.basicConfig(format='%(message)s', stream=fp)
        logger = logging.getLogger()
        logger.setLevel(logging.INFO)

        oeSL = OEStreamLogger(logger)

        lines = ['init', 'bigline_' * 65535, 'morebigline_' * 65535 * 4, 'end']
        for line in lines:
            oeSL.write(line)

        fp.seek(0)
        fp_lines = fp.readlines()
        for i, fp_line in enumerate(fp_lines):
            fp_line = fp_line.strip()
            self.assertEqual(lines[i], fp_line)

        fp.close()

if __name__ == '__main__':
    unittest.main()
