#!/usr/bin/env python3
#
# SPDX-License-Identifier: GPL-2.0-or-later
#
# Modified for use in OE by Richard Purdie, 2018
#
# Modified by: Corey Goldberg, 2013
#   License: GPLv2+
#
# Original code from:
#   Bazaar (bzrlib.tests.__init__.py, v2.6, copied Jun 01 2013)
#   Copyright (C) 2005-2011 Canonical Ltd
#   License: GPLv2+

import os
import sys
import traceback
import unittest
import subprocess
import testtools
import threading
import time
import io
import json
import subunit

from queue import Queue
from itertools import cycle
from subunit import ProtocolTestCase, TestProtocolClient
from subunit.test_results import AutoTimingTestResultDecorator
from testtools import ThreadsafeForwardingResult, iterate_tests
from testtools.content import Content
from testtools.content_type import ContentType
from oeqa.utils.commands import get_test_layer

import bb.utils
import oe.path

_all__ = [
    'ConcurrentTestSuite',
    'fork_for_tests',
    'partition_tests',
]

#
# Patch the version from testtools to allow access to _test_start and allow
# computation of timing information and threading progress
#
class BBThreadsafeForwardingResult(ThreadsafeForwardingResult):

    def __init__(self, target, semaphore, threadnum, totalinprocess, totaltests):
        super(BBThreadsafeForwardingResult, self).__init__(target, semaphore)
        self.threadnum = threadnum
        self.totalinprocess = totalinprocess
        self.totaltests = totaltests

    def _add_result_with_semaphore(self, method, test, *args, **kwargs):
        self.semaphore.acquire()
        try:
            if self._test_start:
                self.result.starttime[test.id()] = self._test_start.timestamp()
                self.result.threadprogress[self.threadnum].append(test.id())
                totalprogress = sum(len(x) for x in self.result.threadprogress.values())
                self.result.progressinfo[test.id()] = "%s: %s/%s %s/%s (%ss) (%s)" % (
                    self.threadnum,
                    len(self.result.threadprogress[self.threadnum]),
                    self.totalinprocess,
                    totalprogress,
                    self.totaltests,
                    "{0:.2f}".format(time.time()-self._test_start.timestamp()),
                    test.id())
        finally:
            self.semaphore.release()
        super(BBThreadsafeForwardingResult, self)._add_result_with_semaphore(method, test, *args, **kwargs)

class ProxyTestResult:
    # a very basic TestResult proxy, in order to modify add* calls
    def __init__(self, target):
        self.result = target
        self.failed_tests = 0

    def _addResult(self, method, test, *args, exception = False, **kwargs):
        return method(test, *args, **kwargs)

    def addError(self, test, err = None, **kwargs):
        self.failed_tests += 1
        self._addResult(self.result.addError, test, err, exception = True, **kwargs)

    def addFailure(self, test, err = None, **kwargs):
        self.failed_tests += 1
        self._addResult(self.result.addFailure, test, err, exception = True, **kwargs)

    def addSuccess(self, test, **kwargs):
        self._addResult(self.result.addSuccess, test, **kwargs)

    def addExpectedFailure(self, test, err = None, **kwargs):
        self._addResult(self.result.addExpectedFailure, test, err, exception = True, **kwargs)

    def addUnexpectedSuccess(self, test, **kwargs):
        self._addResult(self.result.addUnexpectedSuccess, test, **kwargs)

    def wasSuccessful(self):
        return self.failed_tests == 0

    def __getattr__(self, attr):
        return getattr(self.result, attr)

class ExtraResultsDecoderTestResult(ProxyTestResult):
    def _addResult(self, method, test, *args, exception = False, **kwargs):
        if "details" in kwargs and "extraresults" in kwargs["details"]:
            if isinstance(kwargs["details"]["extraresults"], Content):
                kwargs = kwargs.copy()
                kwargs["details"] = kwargs["details"].copy()
                extraresults = kwargs["details"]["extraresults"]
                data = bytearray()
                for b in extraresults.iter_bytes():
                    data += b
                extraresults = json.loads(data.decode())
                kwargs["details"]["extraresults"] = extraresults
        return method(test, *args, **kwargs)

class ExtraResultsEncoderTestResult(ProxyTestResult):
    def _addResult(self, method, test, *args, exception = False, **kwargs):
        if hasattr(test, "extraresults"):
            extras = lambda : [json.dumps(test.extraresults).encode()]
            kwargs = kwargs.copy()
            if "details" not in kwargs:
                kwargs["details"] = {}
            else:
                kwargs["details"] = kwargs["details"].copy()
            kwargs["details"]["extraresults"] = Content(ContentType("application", "json", {'charset': 'utf8'}), extras)
        # if using details, need to encode any exceptions into the details obj,
        # testtools does not handle "err" and "details" together.
        if "details" in kwargs and exception and (len(args) >= 1 and args[0] is not None):
            kwargs["details"]["traceback"] = testtools.content.TracebackContent(args[0], test)
            args = []
        return method(test, *args, **kwargs)

#
# We have to patch subunit since it doesn't understand how to handle addError
# outside of a running test case. This can happen if classSetUp() fails
# for a class of tests. This unfortunately has horrible internal knowledge.
#
def outSideTestaddError(self, offset, line):
    """An 'error:' directive has been read."""
    test_name = line[offset:-1].decode('utf8')
    self.parser._current_test = subunit.RemotedTestCase(test_name)
    self.parser.current_test_description = test_name
    self.parser._state = self.parser._reading_error_details
    self.parser._reading_error_details.set_simple()
    self.parser.subunitLineReceived(line)

subunit._OutSideTest.addError = outSideTestaddError

# Like outSideTestaddError above, we need an equivalent for skips
# happening at the setUpClass() level, otherwise we will see "UNKNOWN"
# as a result for concurrent tests
#
def outSideTestaddSkip(self, offset, line):
    """A 'skip:' directive has been read."""
    test_name = line[offset:-1].decode('utf8')
    self.parser._current_test = subunit.RemotedTestCase(test_name)
    self.parser.current_test_description = test_name
    self.parser._state = self.parser._reading_skip_details
    self.parser._reading_skip_details.set_simple()
    self.parser.subunitLineReceived(line)

subunit._OutSideTest.addSkip = outSideTestaddSkip

#
# A dummy structure to add to io.StringIO so that the .buffer object
# is available and accepts writes. This allows unittest with buffer=True
# to interact ok with subunit which wants to access sys.stdout.buffer.
#
class dummybuf(object):
   def __init__(self, parent):
       self.p = parent
   def write(self, data):
       self.p.write(data.decode("utf-8"))

#
# Taken from testtools.ConncurrencyTestSuite but modified for OE use
#
class ConcurrentTestSuite(unittest.TestSuite):

    def __init__(self, suite, processes, setupfunc, removefunc):
        super(ConcurrentTestSuite, self).__init__([suite])
        self.processes = processes
        self.setupfunc = setupfunc
        self.removefunc = removefunc

    def run(self, result):
        tests, totaltests = fork_for_tests(self.processes, self)
        try:
            threads = {}
            queue = Queue()
            semaphore = threading.Semaphore(1)
            result.threadprogress = {}
            for i, (test, testnum) in enumerate(tests):
                result.threadprogress[i] = []
                process_result = BBThreadsafeForwardingResult(
                        ExtraResultsDecoderTestResult(result),
                        semaphore, i, testnum, totaltests)
                # Force buffering of stdout/stderr so the console doesn't get corrupted by test output
                # as per default in parent code
                process_result.buffer = True
                # We have to add a buffer object to stdout to keep subunit happy
                process_result._stderr_buffer = io.StringIO()
                process_result._stderr_buffer.buffer = dummybuf(process_result._stderr_buffer)
                process_result._stdout_buffer = io.StringIO()
                process_result._stdout_buffer.buffer = dummybuf(process_result._stdout_buffer)
                reader_thread = threading.Thread(
                    target=self._run_test, args=(test, process_result, queue))
                threads[test] = reader_thread, process_result
                reader_thread.start()
            while threads:
                finished_test = queue.get()
                threads[finished_test][0].join()
                del threads[finished_test]
        except:
            for thread, process_result in threads.values():
                process_result.stop()
            raise
        finally:
            for test in tests:
                test[0]._stream.close()

    def _run_test(self, test, process_result, queue):
        try:
            try:
                test.run(process_result)
            except Exception:
                # The run logic itself failed
                case = testtools.ErrorHolder(
                    "broken-runner",
                    error=sys.exc_info())
                case.run(process_result)
        finally:
            queue.put(test)

def fork_for_tests(concurrency_num, suite):
    result = []
    if 'BUILDDIR' in os.environ:
        selftestdir = get_test_layer()

    test_blocks = partition_tests(suite, concurrency_num)
    # Clear the tests from the original suite so it doesn't keep them alive
    suite._tests[:] = []
    totaltests = sum(len(x) for x in test_blocks)
    for process_tests in test_blocks:
        numtests = len(process_tests)
        process_suite = unittest.TestSuite(process_tests)
        # Also clear each split list so new suite has only reference
        process_tests[:] = []
        c2pread, c2pwrite = os.pipe()
        # Clear buffers before fork to avoid duplicate output
        sys.stdout.flush()
        sys.stderr.flush()
        pid = os.fork()
        if pid == 0:
            ourpid = os.getpid()
            try:
                newbuilddir = None
                stream = os.fdopen(c2pwrite, 'wb', 1)
                os.close(c2pread)

                (builddir, newbuilddir) = suite.setupfunc("-st-" + str(ourpid), selftestdir, process_suite)

                # Leave stderr and stdout open so we can see test noise
                # Close stdin so that the child goes away if it decides to
                # read from stdin (otherwise its a roulette to see what
                # child actually gets keystrokes for pdb etc).
                newsi = os.open(os.devnull, os.O_RDWR)
                os.dup2(newsi, sys.stdin.fileno())

                subunit_client = TestProtocolClient(stream)
                # Force buffering of stdout/stderr so the console doesn't get corrupted by test output
                # as per default in parent code
                subunit_client.buffer = True
                subunit_result = AutoTimingTestResultDecorator(subunit_client)
                unittest_result = process_suite.run(ExtraResultsEncoderTestResult(subunit_result))
                if ourpid != os.getpid():
                    os._exit(0)
                if newbuilddir and unittest_result.wasSuccessful():
                    suite.removefunc(newbuilddir)
            except:
                # Don't do anything with process children
                if ourpid != os.getpid():
                    os._exit(1)
                # Try and report traceback on stream, but exit with error
                # even if stream couldn't be created or something else
                # goes wrong.  The traceback is formatted to a string and
                # written in one go to avoid interleaving lines from
                # multiple failing children.
                try:
                    stream.write(traceback.format_exc().encode('utf-8'))
                except:
                    sys.stderr.write(traceback.format_exc())
                finally:
                    if newbuilddir:
                        suite.removefunc(newbuilddir)
                    stream.flush()
                    os._exit(1)
            stream.flush()
            os._exit(0)
        else:
            os.close(c2pwrite)
            stream = os.fdopen(c2pread, 'rb', 1)
            test = ProtocolTestCase(stream)
            result.append((test, numtests))
    return result, totaltests

def partition_tests(suite, count):
    # Keep tests from the same class together but allow tests from modules
    # to go to different processes to aid parallelisation.
    modules = {}
    for test in iterate_tests(suite):
        m = test.__module__ + "." + test.__class__.__name__
        if m not in modules:
            modules[m] = []
        modules[m].append(test)

    # Simply divide the test blocks between the available processes
    partitions = [list() for _ in range(count)]
    for partition, m in zip(cycle(partitions), modules):
        partition.extend(modules[m])

    # No point in empty threads so drop them
    return [p for p in partitions if p]

