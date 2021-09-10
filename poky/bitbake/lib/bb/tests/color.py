#
# BitBake Test for ANSI color code filtering
#
# Copyright (C) 2020  Agilent Technologies, Inc.
# Author: Chris Laplante <chris.laplante@agilent.com>
#
# SPDX-License-Identifier: MIT
#

import unittest
import bb.progress
import bb.data
import bb.event
from bb.progress import filter_color, filter_color_n
import io
import re


class ProgressWatcher:
    def __init__(self):
        self._reports = []

    def handle_event(self, event):
        self._reports.append((event.progress, event.rate))

    def reports(self):
        return self._reports


class ColorCodeTests(unittest.TestCase):
    def setUp(self):
        self.d = bb.data.init()
        self._progress_watcher = ProgressWatcher()
        bb.event.register("bb.build.TaskProgress", self._progress_watcher.handle_event, data=self.d)

    def tearDown(self):
        bb.event.remove("bb.build.TaskProgress", None)

    def test_filter_color(self):
        input_string = "[01;35m[K~~~~~~~~~~~~^~~~~~~~[m[K"
        filtered = filter_color(input_string)
        self.assertEqual(filtered, "~~~~~~~~~~~~^~~~~~~~")

    def test_filter_color_n(self):
        input_string = "[01;35m[K~~~~~~~~~~~~^~~~~~~~[m[K"
        filtered, code_count = filter_color_n(input_string)
        self.assertEqual(filtered, "~~~~~~~~~~~~^~~~~~~~")
        self.assertEqual(code_count, 4)

    def test_LineFilterProgressHandler_color_filtering(self):
        class CustomProgressHandler(bb.progress.LineFilterProgressHandler):
            PROGRESS_REGEX = re.compile(r"Progress: (?P<progress>\d+)%")

            def writeline(self, line):
                match = self.PROGRESS_REGEX.match(line)
                if match:
                    self.update(int(match.group("progress")))
                    return False
                return True

        buffer = io.StringIO()
        handler = CustomProgressHandler(self.d, buffer)
        handler.write("Program output!\n")
        handler.write("More output!\n")
        handler.write("Progress: [01;35m[K10[m[K%\n") # 10%
        handler.write("Even more\n")
        handler.write("[01;35m[KProgress: 50[m[K%\n") # 50%
        handler.write("[01;35m[KProgress: 60[m[K%\n") # 60%
        handler.write("Pro[01;35m[Kgress: [m[K100%\n") # 100%

        expected = [(10, None), (50, None), (60, None), (100, None)]
        self.assertEqual(self._progress_watcher.reports(), expected)

        self.assertEqual(buffer.getvalue(), "Program output!\nMore output!\nEven more\n")

    def test_BasicProgressHandler_color_filtering(self):
        buffer = io.StringIO()
        handler = bb.progress.BasicProgressHandler(self.d, outfile=buffer)
        handler.write("[01;35m[K1[m[K%\n") # 1%
        handler.write("[01;35m[K2[m[K%\n") # 2%
        handler.write("[01;35m[K10[m[K%\n") # 10%
        handler.write("[01;35m[K100[m[K%\n") # 100%

        expected = [(0, None), (1, None), (2, None), (10, None), (100, None)]
        self.assertListEqual(self._progress_watcher.reports(), expected)

    def test_OutOfProgressHandler_color_filtering(self):
        buffer = io.StringIO()
        handler = bb.progress.OutOfProgressHandler(self.d, r'(\d+) of (\d+)', outfile=buffer)
        handler.write("[01;35m[KText text 1 of[m[K 5") # 1/5
        handler.write("[01;35m[KText text 3 of[m[K 5") # 3/5
        handler.write("[01;35m[KText text 5 of[m[K 5") # 5/5

        expected = [(0, None), (20.0, None), (60.0, None), (100.0, None)]
        self.assertListEqual(self._progress_watcher.reports(), expected)
