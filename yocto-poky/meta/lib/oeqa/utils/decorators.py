# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

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

#get the "result" object from one of the upper frames provided that one of these upper frames is a unittest.case frame
class getResults(object):
    def __init__(self):
        #dynamically determine the unittest.case frame and use it to get the name of the test method
        ident = threading.current_thread().ident
        upperf = sys._current_frames()[ident]
        while (upperf.f_globals['__name__'] != 'unittest.case'):
            upperf = upperf.f_back

        def handleList(items):
            ret = []
            # items is a list of tuples, (test, failure) or (_ErrorHandler(), Exception())
            for i in items:
                s = i[0].id()
                #Handle the _ErrorHolder objects from skipModule failures
                if "setUpModule (" in s:
                    ret.append(s.replace("setUpModule (", "").replace(")",""))
                else:
                    ret.append(s)
                # Append also the test without the full path
                testname = s.split('.')[-1]
                if testname:
                    ret.append(testname)
            return ret
        self.faillist = handleList(upperf.f_locals['result'].failures)
        self.errorlist = handleList(upperf.f_locals['result'].errors)
        self.skiplist = handleList(upperf.f_locals['result'].skipped)

    def getFailList(self):
        return self.faillist

    def getErrorList(self):
        return self.errorlist

    def getSkipList(self):
        return self.skiplist

class skipIfFailure(object):

    def __init__(self,testcase):
        self.testcase = testcase

    def __call__(self,f):
        def wrapped_f(*args, **kwargs):
            res = getResults()
            if self.testcase in (res.getFailList() or res.getErrorList()):
                raise unittest.SkipTest("Testcase dependency not met: %s" % self.testcase)
            return f(*args, **kwargs)
        wrapped_f.__name__ = f.__name__
        return wrapped_f

class skipIfSkipped(object):

    def __init__(self,testcase):
        self.testcase = testcase

    def __call__(self,f):
        def wrapped_f(*args, **kwargs):
            res = getResults()
            if self.testcase in res.getSkipList():
                raise unittest.SkipTest("Testcase dependency not met: %s" % self.testcase)
            return f(*args, **kwargs)
        wrapped_f.__name__ = f.__name__
        return wrapped_f

class skipUnlessPassed(object):

    def __init__(self,testcase):
        self.testcase = testcase

    def __call__(self,f):
        def wrapped_f(*args, **kwargs):
            res = getResults()
            if self.testcase in res.getSkipList() or \
                    self.testcase in res.getFailList() or \
                    self.testcase in res.getErrorList():
                raise unittest.SkipTest("Testcase dependency not met: %s" % self.testcase)
            return f(*args, **kwargs)
        wrapped_f.__name__ = f.__name__
        wrapped_f._depends_on = self.testcase
        return wrapped_f

class testcase(object):

    def __init__(self, test_case):
        self.test_case = test_case

    def __call__(self, func):
        def wrapped_f(*args, **kwargs):
            return func(*args, **kwargs)
        wrapped_f.test_case = self.test_case
        wrapped_f.__name__ = func.__name__
        return wrapped_f

class NoParsingFilter(logging.Filter):
    def filter(self, record):
        return record.levelno == 100

def LogResults(original_class):
    orig_method = original_class.run

    from time import strftime, gmtime
    caller = os.path.basename(sys.argv[0])
    timestamp = strftime('%Y%m%d%H%M%S',gmtime())
    logfile = os.path.join(os.getcwd(),'results-'+caller+'.'+timestamp+'.log')
    linkfile = os.path.join(os.getcwd(),'results-'+caller+'.log')

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

        class_name = str(testMethod.im_class).split("'")[1]

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

        for (name, msg) in result.errors:
            if (self._testMethodName == str(name).split(' ')[0]) and (class_name in str(name).split(' ')[1]):
                local_log.results("Testcase "+str(test_case)+": ERROR")
                local_log.results("Testcase "+str(test_case)+":\n"+msg)
                passed = False
        for (name, msg) in result.failures:
            if (self._testMethodName == str(name).split(' ')[0]) and (class_name in str(name).split(' ')[1]):
                local_log.results("Testcase "+str(test_case)+": FAILED")
                local_log.results("Testcase "+str(test_case)+":\n"+msg)
                passed = False
        for (name, msg) in result.skipped:
            if (self._testMethodName == str(name).split(' ')[0]) and (class_name in str(name).split(' ')[1]):
                local_log.results("Testcase "+str(test_case)+": SKIPPED")
                passed = False
        if passed:
            local_log.results("Testcase "+str(test_case)+": PASSED")

        # Create symlink to the current log
        if os.path.exists(linkfile):
            os.remove(linkfile)
        os.symlink(logfile, linkfile)

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
        for name, value in kwargs.iteritems():
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
