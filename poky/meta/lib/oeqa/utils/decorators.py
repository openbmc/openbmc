#
# Copyright (C) 2013 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# Some custom decorators that can be used by unittests
# Most useful is skipUnlessPassed which can be used for
# creating dependecies between two test methods.

import os
import logging
import sys
import unittest
import threading
import signal
from functools import wraps

class testcase(object):
    def __init__(self, test_case):
        self.test_case = test_case

    def __call__(self, func):
        @wraps(func)
        def wrapped_f(*args, **kwargs):
            return func(*args, **kwargs)
        wrapped_f.test_case = self.test_case
        wrapped_f.__name__ = func.__name__
        return wrapped_f

class NoParsingFilter(logging.Filter):
    def filter(self, record):
        return record.levelno == 100

import inspect

def LogResults(original_class):
    orig_method = original_class.run

    from time import strftime, gmtime
    caller = os.path.basename(sys.argv[0])
    timestamp = strftime('%Y%m%d%H%M%S',gmtime())
    logfile = os.path.join(os.getcwd(),'results-'+caller+'.'+timestamp+'.log')
    linkfile = os.path.join(os.getcwd(),'results-'+caller+'.log')

    def get_class_that_defined_method(meth):
        if inspect.ismethod(meth):
            for cls in inspect.getmro(meth.__self__.__class__):
               if cls.__dict__.get(meth.__name__) is meth:
                    return cls
            meth = meth.__func__ # fallback to __qualname__ parsing
        if inspect.isfunction(meth):
            cls = getattr(inspect.getmodule(meth),
                          meth.__qualname__.split('.<locals>', 1)[0].rsplit('.', 1)[0])
            if isinstance(cls, type):
               return cls
        return None

    #rewrite the run method of unittest.TestCase to add testcase logging
    def run(self, result, *args, **kws):
        orig_method(self, result, *args, **kws)
        passed = True
        testMethod = getattr(self, self._testMethodName)
        #if test case is decorated then use it's number, else use it's name
        try:
            test_case = testMethod.test_case
        except AttributeError:
            test_case = self._testMethodName

        class_name = str(get_class_that_defined_method(testMethod)).split("'")[1]

        #create custom logging level for filtering.
        custom_log_level = 100
        logging.addLevelName(custom_log_level, 'RESULTS')

        def results(self, message, *args, **kws):
            if self.isEnabledFor(custom_log_level):
                self.log(custom_log_level, message, *args, **kws)
        logging.Logger.results = results

        logging.basicConfig(filename=logfile,
                            filemode='w',
                            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
                            datefmt='%H:%M:%S',
                            level=custom_log_level)
        for handler in logging.root.handlers:
            handler.addFilter(NoParsingFilter())
        local_log = logging.getLogger(caller)

        #check status of tests and record it

        tcid = self.id()
        for (name, msg) in result.errors:
            if tcid == name.id():
                local_log.results("Testcase "+str(test_case)+": ERROR")
                local_log.results("Testcase "+str(test_case)+":\n"+msg)
                passed = False
        for (name, msg) in result.failures:
            if tcid == name.id():
                local_log.results("Testcase "+str(test_case)+": FAILED")
                local_log.results("Testcase "+str(test_case)+":\n"+msg)
                passed = False
        for (name, msg) in result.skipped:
            if tcid == name.id():
                local_log.results("Testcase "+str(test_case)+": SKIPPED")
                passed = False
        if passed:
            local_log.results("Testcase "+str(test_case)+": PASSED")

        # XXX: In order to avoid race condition when test if exists the linkfile
        # use bb.utils.lock, the best solution is to create a unique name for the
        # link file.
        try:
            import bb
            has_bb = True
            lockfilename = linkfile + '.lock'
        except ImportError:
            has_bb = False

        if has_bb:
            lf = bb.utils.lockfile(lockfilename, block=True)
        # Create symlink to the current log
        if os.path.lexists(linkfile):
            os.remove(linkfile)
        os.symlink(logfile, linkfile)
        if has_bb:
            bb.utils.unlockfile(lf)

    original_class.run = run

    return original_class

class TimeOut(BaseException):
    pass

def timeout(seconds):
    def decorator(fn):
        if hasattr(signal, 'alarm'):
            @wraps(fn)
            def wrapped_f(*args, **kw):
                current_frame = sys._getframe()
                def raiseTimeOut(signal, frame):
                    if frame is not current_frame:
                        raise TimeOut('%s seconds' % seconds)
                prev_handler = signal.signal(signal.SIGALRM, raiseTimeOut)
                try:
                    signal.alarm(seconds)
                    return fn(*args, **kw)
                finally:
                    signal.alarm(0)
                    signal.signal(signal.SIGALRM, prev_handler)
            return wrapped_f
        else:
            return fn
    return decorator

__tag_prefix = "tag__"
def tag(*args, **kwargs):
    """Decorator that adds attributes to classes or functions
    for use with the Attribute (-a) plugin.
    """
    def wrap_ob(ob):
        for name in args:
            setattr(ob, __tag_prefix + name, True)
        for name, value in kwargs.items():
            setattr(ob, __tag_prefix + name, value)
        return ob
    return wrap_ob

def gettag(obj, key, default=None):
    key = __tag_prefix + key
    if not isinstance(obj, unittest.TestCase):
        return getattr(obj, key, default)
    tc_method = getattr(obj, obj._testMethodName)
    ret = getattr(tc_method, key, getattr(obj, key, default))
    return ret

def getAllTags(obj):
    def __gettags(o):
        r = {k[len(__tag_prefix):]:getattr(o,k) for k in dir(o) if k.startswith(__tag_prefix)}
        return r
    if not isinstance(obj, unittest.TestCase):
        return __gettags(obj)
    tc_method = getattr(obj, obj._testMethodName)
    ret = __gettags(obj)
    ret.update(__gettags(tc_method))
    return ret

def timeout_handler(seconds):
    def decorator(fn):
        if hasattr(signal, 'alarm'):
            @wraps(fn)
            def wrapped_f(self, *args, **kw):
                current_frame = sys._getframe()
                def raiseTimeOut(signal, frame):
                    if frame is not current_frame:
                        try:
                            self.target.restart()
                            raise TimeOut('%s seconds' % seconds)
                        except:
                            raise TimeOut('%s seconds' % seconds)
                prev_handler = signal.signal(signal.SIGALRM, raiseTimeOut)
                try:
                    signal.alarm(seconds)
                    return fn(self, *args, **kw)
                finally:
                    signal.alarm(0)
                    signal.signal(signal.SIGALRM, prev_handler)
            return wrapped_f
        else:
            return fn
    return decorator
