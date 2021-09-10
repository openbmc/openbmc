#
# SPDX-License-Identifier: MIT
#

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd
from oeqa.utils import CommandError

import subprocess
import threading
import time
import signal

class MemLogger(object):
    def __init__(self):
        self.info_msgs = []
        self.error_msgs = []

    def info(self, msg):
        self.info_msgs.append(msg)

    def error(self, msg):
        self.error_msgs.append(msg)

class RunCmdTests(OESelftestTestCase):
    """ Basic tests for runCmd() utility function """

    # The delta is intentionally smaller than the timeout, to detect cases where
    # we incorrectly apply the timeout more than once.
    TIMEOUT = 10
    DELTA = 8

    def test_result_okay(self):
        result = runCmd("true")
        self.assertEqual(result.status, 0)

    def test_result_false(self):
        result = runCmd("false", ignore_status=True)
        self.assertEqual(result.status, 1)

    def test_shell(self):
        # A shell is used for all string commands.
        result = runCmd("false; true", ignore_status=True)
        self.assertEqual(result.status, 0)

    def test_no_shell(self):
        self.assertRaises(FileNotFoundError,
                          runCmd, "false; true", shell=False)

    def test_list_not_found(self):
        self.assertRaises(FileNotFoundError,
                          runCmd, ["false; true"])

    def test_list_okay(self):
        result = runCmd(["true"])
        self.assertEqual(result.status, 0)

    def test_result_assertion(self):
        self.assertRaisesRegexp(AssertionError, "Command 'echo .* false' returned non-zero exit status 1:\nfoobar",
                                runCmd, "echo foobar >&2; false", shell=True)

    def test_result_exception(self):
        self.assertRaisesRegexp(CommandError, "Command 'echo .* false' returned non-zero exit status 1 with output: foobar",
                                runCmd, "echo foobar >&2; false", shell=True, assert_error=False)

    def test_output(self):
        result = runCmd("echo stdout; echo stderr >&2", shell=True, sync=False)
        self.assertEqual("stdout\nstderr", result.output)
        self.assertEqual("", result.error)

    def test_output_split(self):
        result = runCmd("echo stdout; echo stderr >&2", shell=True, stderr=subprocess.PIPE, sync=False)
        self.assertEqual("stdout", result.output)
        self.assertEqual("stderr", result.error)

    def test_timeout(self):
        numthreads = threading.active_count()
        start = time.time()
        # Killing a hanging process only works when not using a shell?!
        result = runCmd(['sleep', '60'], timeout=self.TIMEOUT, ignore_status=True, sync=False)
        self.assertEqual(result.status, -signal.SIGTERM)
        end = time.time()
        self.assertLess(end - start, self.TIMEOUT + self.DELTA)
        self.assertEqual(numthreads, threading.active_count(), msg="Thread counts were not equal before (%s) and after (%s), active threads: %s" % (numthreads, threading.active_count(), threading.enumerate()))

    def test_timeout_split(self):
        numthreads = threading.active_count()
        start = time.time()
        # Killing a hanging process only works when not using a shell?!
        result = runCmd(['sleep', '60'], timeout=self.TIMEOUT, ignore_status=True, stderr=subprocess.PIPE, sync=False)
        self.assertEqual(result.status, -signal.SIGTERM)
        end = time.time()
        self.assertLess(end - start, self.TIMEOUT + self.DELTA)
        self.assertEqual(numthreads, threading.active_count(), msg="Thread counts were not equal before (%s) and after (%s), active threads: %s" % (numthreads, threading.active_count(), threading.enumerate()))

    def test_stdin(self):
        numthreads = threading.active_count()
        result = runCmd("cat", data=b"hello world", timeout=self.TIMEOUT, sync=False)
        self.assertEqual("hello world", result.output)
        self.assertEqual(numthreads, threading.active_count(), msg="Thread counts were not equal before (%s) and after (%s), active threads: %s" % (numthreads, threading.active_count(), threading.enumerate()))
        self.assertEqual(numthreads, 1)

    def test_stdin_timeout(self):
        numthreads = threading.active_count()
        start = time.time()
        result = runCmd(['sleep', '60'], data=b"hello world", timeout=self.TIMEOUT, ignore_status=True, sync=False)
        self.assertEqual(result.status, -signal.SIGTERM)
        end = time.time()
        self.assertLess(end - start, self.TIMEOUT + self.DELTA)
        self.assertEqual(numthreads, threading.active_count(), msg="Thread counts were not equal before (%s) and after (%s), active threads: %s" % (numthreads, threading.active_count(), threading.enumerate()))

    def test_log(self):
        log = MemLogger()
        result = runCmd("echo stdout; echo stderr >&2", shell=True, output_log=log, sync=False)
        self.assertEqual(["Running: echo stdout; echo stderr >&2", "stdout", "stderr"], log.info_msgs)
        self.assertEqual([], log.error_msgs)

    def test_log_split(self):
        log = MemLogger()
        result = runCmd("echo stdout; echo stderr >&2", shell=True, output_log=log, stderr=subprocess.PIPE, sync=False)
        self.assertEqual(["Running: echo stdout; echo stderr >&2", "stdout"], log.info_msgs)
        self.assertEqual(["stderr"], log.error_msgs)
