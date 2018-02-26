# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import threading
import multiprocessing
import queue
import time

from unittest.suite import TestSuite

from oeqa.core.loader import OETestLoader
from oeqa.core.runner import OEStreamLogger, OETestResult, OETestRunner
from oeqa.core.context import OETestContext

class OETestLoaderThreaded(OETestLoader):
    def __init__(self, tc, module_paths, modules, tests, modules_required,
            filters, process_num=0, *args, **kwargs):
        super(OETestLoaderThreaded, self).__init__(tc, module_paths, modules,
                tests, modules_required, filters, *args, **kwargs)

        self.process_num = process_num

    def discover(self):
        suite = super(OETestLoaderThreaded, self).discover()

        if self.process_num <= 0:
            self.process_num = min(multiprocessing.cpu_count(),
                    len(suite._tests))

        suites = []
        for _ in range(self.process_num):
            suites.append(self.suiteClass())

        def _search_for_module_idx(suites, case):
            """
                Cases in the same module needs to be run
                in the same thread because PyUnit keeps track
                of setUp{Module, Class,} and tearDown{Module, Class,}.
            """

            for idx in range(self.process_num):
                suite = suites[idx]
                for c in suite._tests:
                    if case.__module__ == c.__module__:
                        return idx

            return -1

        def _search_for_depend_idx(suites, depends):
            """
                Dependency cases needs to be run in the same
                thread, because OEQA framework look at the state
                of dependant test to figure out if skip or not.
            """

            for idx in range(self.process_num):
                suite = suites[idx]

                for case in suite._tests:
                    if case.id() in depends:
                        return idx
            return -1

        def _get_best_idx(suites):
            sizes = [len(suite._tests) for suite in suites]
            return sizes.index(min(sizes))

        def _fill_suites(suite):
            idx = -1
            for case in suite:
                if isinstance(case, TestSuite):
                    _fill_suites(case)
                else:
                    idx = _search_for_module_idx(suites, case)

                    depends = {}
                    if 'depends' in self.tc._registry:
                        depends = self.tc._registry['depends']

                    if idx == -1 and case.id() in depends:
                        case_depends = depends[case.id()] 
                        idx = _search_for_depend_idx(suites, case_depends)

                    if idx == -1:
                        idx = _get_best_idx(suites)

                    suites[idx].addTest(case)
        _fill_suites(suite)

        suites_tmp = suites
        suites = []
        for suite in suites_tmp:
            if len(suite._tests) > 0:
                suites.append(suite)

        return suites

class OEStreamLoggerThreaded(OEStreamLogger):
    _lock = threading.Lock()
    buffers = {}

    def write(self, msg):
        tid = threading.get_ident()

        if not tid in self.buffers:
            self.buffers[tid] = ""

        if msg:
            self.buffers[tid] += msg

    def finish(self):
        tid = threading.get_ident()
        
        self._lock.acquire()
        self.logger.info('THREAD: %d' % tid)
        self.logger.info('-' * 70)
        for line in self.buffers[tid].split('\n'):
            self.logger.info(line)
        self._lock.release()

class OETestResultThreadedInternal(OETestResult):
    def _tc_map_results(self):
        tid = threading.get_ident()
        
        # PyUnit generates a result for every test module run, test
        # if the thread already has an entry to avoid lose the previous
        # test module results.
        if not tid in self.tc._results:
            self.tc._results[tid] = {}
            self.tc._results[tid]['failures'] = self.failures
            self.tc._results[tid]['errors'] = self.errors
            self.tc._results[tid]['skipped'] = self.skipped
            self.tc._results[tid]['expectedFailures'] = self.expectedFailures

class OETestResultThreaded(object):
    _results = {}
    _lock = threading.Lock()

    def __init__(self, tc):
        self.tc = tc

    def _fill_tc_results(self):
        tids = list(self.tc._results.keys())
        fields = ['failures', 'errors', 'skipped', 'expectedFailures']

        for tid in tids:
            result = self.tc._results[tid]
            for field in fields:
                if not field in self.tc._results:
                    self.tc._results[field] = []
                self.tc._results[field].extend(result[field])

    def addResult(self, result, run_start_time, run_end_time):
        tid = threading.get_ident()

        self._lock.acquire()
        self._results[tid] = {}
        self._results[tid]['result'] = result
        self._results[tid]['run_start_time'] = run_start_time 
        self._results[tid]['run_end_time'] = run_end_time 
        self._results[tid]['result'] = result
        self._lock.release()

    def wasSuccessful(self):
        wasSuccessful = True
        for tid in self._results.keys():
            wasSuccessful = wasSuccessful and \
                    self._results[tid]['result'].wasSuccessful()
        return wasSuccessful

    def stop(self):
        for tid in self._results.keys():
            self._results[tid]['result'].stop()

    def logSummary(self, component, context_msg=''):
        elapsed_time = (self.tc._run_end_time - self.tc._run_start_time)

        self.tc.logger.info("SUMMARY:")
        self.tc.logger.info("%s (%s) - Ran %d tests in %.3fs" % (component,
            context_msg, len(self.tc._registry['cases']), elapsed_time))
        if self.wasSuccessful():
            msg = "%s - OK - All required tests passed" % component
        else:
            msg = "%s - FAIL - Required tests failed" % component
        self.tc.logger.info(msg)

    def logDetails(self):
        if list(self._results):
            tid = list(self._results)[0]
            result = self._results[tid]['result']
            result.logDetails()

class _Worker(threading.Thread):
    """Thread executing tasks from a given tasks queue"""
    def __init__(self, tasks, result, stream):
        threading.Thread.__init__(self)
        self.tasks = tasks

        self.result = result
        self.stream = stream

    def run(self):
        while True:
            try:
                func, args, kargs = self.tasks.get(block=False)
            except queue.Empty:
                break

            try:
                run_start_time = time.time()
                rc = func(*args, **kargs)
                run_end_time = time.time()
                self.result.addResult(rc, run_start_time, run_end_time)
                self.stream.finish()
            except Exception as e:
                print(e)
            finally:
                self.tasks.task_done()

class _ThreadedPool:
    """Pool of threads consuming tasks from a queue"""
    def __init__(self, num_workers, num_tasks, stream=None, result=None):
        self.tasks = queue.Queue(num_tasks)
        self.workers = []

        for _ in range(num_workers):
            worker = _Worker(self.tasks, result, stream)
            self.workers.append(worker)

    def start(self):
        for worker in self.workers:
            worker.start()

    def add_task(self, func, *args, **kargs):
        """Add a task to the queue"""
        self.tasks.put((func, args, kargs))

    def wait_completion(self):
        """Wait for completion of all the tasks in the queue"""
        self.tasks.join()
        for worker in self.workers:
            worker.join()

class OETestRunnerThreaded(OETestRunner):
    streamLoggerClass = OEStreamLoggerThreaded

    def __init__(self, tc, *args, **kwargs):
        super(OETestRunnerThreaded, self).__init__(tc, *args, **kwargs)
        self.resultclass = OETestResultThreadedInternal # XXX: XML reporting overrides at __init__

    def run(self, suites):
        result = OETestResultThreaded(self.tc)

        pool = _ThreadedPool(len(suites), len(suites), stream=self.stream,
                result=result)
        for s in suites:
            pool.add_task(super(OETestRunnerThreaded, self).run, s)
        pool.start()
        pool.wait_completion()
        result._fill_tc_results()

        return result

class OETestContextThreaded(OETestContext):
    loaderClass = OETestLoaderThreaded
    runnerClass = OETestRunnerThreaded

    def loadTests(self, module_paths, modules=[], tests=[],
            modules_manifest="", modules_required=[], filters={}, process_num=0):
        if modules_manifest:
            modules = self._read_modules_from_manifest(modules_manifest)

        self.loader = self.loaderClass(self, module_paths, modules, tests,
                modules_required, filters, process_num)
        self.suites = self.loader.discover()
