# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Build System Python Library
#
# Copyright (C) 2003  Holger Schurig
# Copyright (C) 2003, 2004  Chris Larson
#
# Based on Gentoo's portage.py.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

__version__ = "1.40.0"

import sys
if sys.version_info < (3, 4, 0):
    raise RuntimeError("Sorry, python 3.4.0 or later is required for this version of bitbake")


class BBHandledException(Exception):
    """
    The big dilemma for generic bitbake code is what information to give the user
    when an exception occurs. Any exception inheriting this base exception class
    has already provided information to the user via some 'fired' message type such as
    an explicitly fired event using bb.fire, or a bb.error message. If bitbake 
    encounters an exception derived from this class, no backtrace or other information 
    will be given to the user, its assumed the earlier event provided the relevant information.
    """
    pass

import os
import logging


class NullHandler(logging.Handler):
    def emit(self, record):
        pass

Logger = logging.getLoggerClass()
class BBLogger(Logger):
    def __init__(self, name):
        if name.split(".")[0] == "BitBake":
            self.debug = self.bbdebug
        Logger.__init__(self, name)

    def bbdebug(self, level, msg, *args, **kwargs):
        return self.log(logging.DEBUG - level + 1, msg, *args, **kwargs)

    def plain(self, msg, *args, **kwargs):
        return self.log(logging.INFO + 1, msg, *args, **kwargs)

    def verbose(self, msg, *args, **kwargs):
        return self.log(logging.INFO - 1, msg, *args, **kwargs)

    def verbnote(self, msg, *args, **kwargs):
        return self.log(logging.INFO + 2, msg, *args, **kwargs)


logging.raiseExceptions = False
logging.setLoggerClass(BBLogger)

logger = logging.getLogger("BitBake")
logger.addHandler(NullHandler())
logger.setLevel(logging.DEBUG - 2)

mainlogger = logging.getLogger("BitBake.Main")

# This has to be imported after the setLoggerClass, as the import of bb.msg
# can result in construction of the various loggers.
import bb.msg

from bb import fetch2 as fetch
sys.modules['bb.fetch'] = sys.modules['bb.fetch2']

# Messaging convenience functions
def plain(*args):
    mainlogger.plain(''.join(args))

def debug(lvl, *args):
    if isinstance(lvl, str):
        mainlogger.warning("Passed invalid debug level '%s' to bb.debug", lvl)
        args = (lvl,) + args
        lvl = 1
    mainlogger.debug(lvl, ''.join(args))

def note(*args):
    mainlogger.info(''.join(args))

#
# A higher prioity note which will show on the console but isn't a warning
#
# Something is happening the user should be aware of but they probably did
# something to make it happen
#
def verbnote(*args):
    mainlogger.verbnote(''.join(args))

#
# Warnings - things the user likely needs to pay attention to and fix
#
def warn(*args):
    mainlogger.warning(''.join(args))

def error(*args, **kwargs):
    mainlogger.error(''.join(args), extra=kwargs)

def fatal(*args, **kwargs):
    mainlogger.critical(''.join(args), extra=kwargs)
    raise BBHandledException()

def deprecated(func, name=None, advice=""):
    """This is a decorator which can be used to mark functions
    as deprecated. It will result in a warning being emitted
    when the function is used."""
    import warnings

    if advice:
        advice = ": %s" % advice
    if name is None:
        name = func.__name__

    def newFunc(*args, **kwargs):
        warnings.warn("Call to deprecated function %s%s." % (name,
                                                             advice),
                      category=DeprecationWarning,
                      stacklevel=2)
        return func(*args, **kwargs)
    newFunc.__name__ = func.__name__
    newFunc.__doc__ = func.__doc__
    newFunc.__dict__.update(func.__dict__)
    return newFunc

# For compatibility
def deprecate_import(current, modulename, fromlist, renames = None):
    """Import objects from one module into another, wrapping them with a DeprecationWarning"""
    import sys

    module = __import__(modulename, fromlist = fromlist)
    for position, objname in enumerate(fromlist):
        obj = getattr(module, objname)
        newobj = deprecated(obj, "{0}.{1}".format(current, objname),
                            "Please use {0}.{1} instead".format(modulename, objname))
        if renames:
            newname = renames[position]
        else:
            newname = objname

        setattr(sys.modules[current], newname, newobj)

