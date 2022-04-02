#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from functools import wraps
from abc import ABCMeta

decoratorClasses = set()

def registerDecorator(cls):
    decoratorClasses.add(cls)
    return cls

class OETestDecorator(object, metaclass=ABCMeta):
    case = None # Reference of OETestCase decorated
    attrs = None # Attributes to be loaded by decorator implementation

    def __init__(self, *args, **kwargs):
        if not self.attrs:
            return

        for idx, attr in enumerate(self.attrs):
            if attr in kwargs:
                value = kwargs[attr]
            else:
                value = args[idx]
            setattr(self, attr, value)

    def __call__(self, func):
        @wraps(func)
        def wrapped_f(*args, **kwargs):
            self.attrs = self.attrs # XXX: Enables OETestLoader discover
            return func(*args, **kwargs)
        return wrapped_f

    # OETestLoader call it when is loading test cases.
    # XXX: Most methods would change the registry for later
    # processing; be aware that filtrate method needs to
    # run later than bind, so there could be data (in the
    # registry) of a cases that were filtered.
    def bind(self, registry, case):
        self.case = case
        self.logger = case.tc.logger
        self.case.decorators.append(self)

    # OETestRunner call this method when tries to run
    # the test case.
    def setUpDecorator(self):
        pass

    # OETestRunner call it after a test method has been
    # called even if the method raised an exception.
    def tearDownDecorator(self):
        pass

class OETestDiscover(OETestDecorator):

    # OETestLoader call it after discover test cases
    # needs to return the cases to be run.
    @staticmethod
    def discover(registry):
        return registry['cases']

def OETestTag(*tags):
    def decorator(item):
        if hasattr(item, "__oeqa_testtags"):
            # do not append, create a new list (to handle classes with inheritance)
            item.__oeqa_testtags = list(item.__oeqa_testtags) + list(tags)
        else:
            item.__oeqa_testtags = tags
        return item
    return decorator
