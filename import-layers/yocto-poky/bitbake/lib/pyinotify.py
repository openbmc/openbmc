#!/usr/bin/env python

# pyinotify.py - python interface to inotify
# Copyright (c) 2005-2015 Sebastien Martini <seb@dbzteam.org>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
"""
pyinotify

@author: Sebastien Martini
@license: MIT License
@contact: seb@dbzteam.org
"""

class PyinotifyError(Exception):
    """Indicates exceptions raised by a Pyinotify class."""
    pass


class UnsupportedPythonVersionError(PyinotifyError):
    """
    Raised on unsupported Python versions.
    """
    def __init__(self, version):
        """
        @param version: Current Python version
        @type version: string
        """
        err = 'Python %s is unsupported, requires at least Python 2.4'
        PyinotifyError.__init__(self, err % version)


# Check Python version
import sys
if sys.version_info < (2, 4):
    raise UnsupportedPythonVersionError(sys.version)


# Import directives
import threading
import os
import select
import struct
import fcntl
import errno
import termios
import array
import logging
import atexit
from collections import deque
from datetime import datetime, timedelta
import time
import re
import asyncore
import subprocess

try:
    from functools import reduce
except ImportError:
    pass  # Will fail on Python 2.4 which has reduce() builtin anyway.

try:
    from glob import iglob as glob
except ImportError:
    # Python 2.4 does not have glob.iglob().
    from glob import glob as glob

try:
    import ctypes
    import ctypes.util
except ImportError:
    ctypes = None

try:
    import inotify_syscalls
except ImportError:
    inotify_syscalls = None


__author__ = "seb@dbzteam.org (Sebastien Martini)"

__version__ = "0.9.5"

__metaclass__ = type  # Use new-style classes by default


# Compatibity mode: set to True to improve compatibility with
# Pyinotify 0.7.1. Do not set this variable yourself, call the
# function compatibility_mode() instead.
COMPATIBILITY_MODE = False


class InotifyBindingNotFoundError(PyinotifyError):
    """
    Raised when no inotify support couldn't be found.
    """
    def __init__(self):
        err = "Couldn't find any inotify binding"
        PyinotifyError.__init__(self, err)


class INotifyWrapper:
    """
    Abstract class wrapping access to inotify's functions. This is an
    internal class.
    """
    @staticmethod
    def create():
        # First, try to use ctypes.
        if ctypes:
            inotify = _CtypesLibcINotifyWrapper()
            if inotify.init():
                return inotify
        # Second, see if C extension is compiled.
        if inotify_syscalls:
            inotify = _INotifySyscallsWrapper()
            if inotify.init():
                return inotify

    def get_errno(self):
        """
        Return None is no errno code is available.
        """
        return self._get_errno()

    def str_errno(self):
        code = self.get_errno()
        if code is None:
            return 'Errno: no errno support'
        return 'Errno=%s (%s)' % (os.strerror(code), errno.errorcode[code])

    def inotify_init(self):
        return self._inotify_init()

    def inotify_add_watch(self, fd, pathname, mask):
        # Unicode strings must be encoded to string prior to calling this
        # method.
        assert isinstance(pathname, str)
        return self._inotify_add_watch(fd, pathname, mask)

    def inotify_rm_watch(self, fd, wd):
        return self._inotify_rm_watch(fd, wd)


class _INotifySyscallsWrapper(INotifyWrapper):
    def __init__(self):
        # Stores the last errno value.
        self._last_errno = None

    def init(self):
        assert inotify_syscalls
        return True

    def _get_errno(self):
        return self._last_errno

    def _inotify_init(self):
        try:
            fd = inotify_syscalls.inotify_init()
        except IOError, err:
            self._last_errno = err.errno
            return -1
        return fd

    def _inotify_add_watch(self, fd, pathname, mask):
        try:
            wd = inotify_syscalls.inotify_add_watch(fd, pathname, mask)
        except IOError, err:
            self._last_errno = err.errno
            return -1
        return wd

    def _inotify_rm_watch(self, fd, wd):
        try:
            ret = inotify_syscalls.inotify_rm_watch(fd, wd)
        except IOError, err:
            self._last_errno = err.errno
            return -1
        return ret


class _CtypesLibcINotifyWrapper(INotifyWrapper):
    def __init__(self):
        self._libc = None
        self._get_errno_func = None

    def init(self):
        assert ctypes

        try_libc_name = 'c'
        if sys.platform.startswith('freebsd'):
            try_libc_name = 'inotify'

        libc_name = None
        try:
            libc_name = ctypes.util.find_library(try_libc_name)
        except (OSError, IOError):
            pass  # Will attemp to load it with None anyway.

        if sys.version_info >= (2, 6):
            self._libc = ctypes.CDLL(libc_name, use_errno=True)
            self._get_errno_func = ctypes.get_errno
        else:
            self._libc = ctypes.CDLL(libc_name)
            try:
                location = self._libc.__errno_location
                location.restype = ctypes.POINTER(ctypes.c_int)
                self._get_errno_func = lambda: location().contents.value
            except AttributeError:
                pass

        # Eventually check that libc has needed inotify bindings.
        if (not hasattr(self._libc, 'inotify_init') or
            not hasattr(self._libc, 'inotify_add_watch') or
            not hasattr(self._libc, 'inotify_rm_watch')):
            return False

        self._libc.inotify_init.argtypes = []
        self._libc.inotify_init.restype = ctypes.c_int
        self._libc.inotify_add_watch.argtypes = [ctypes.c_int, ctypes.c_char_p,
                                                 ctypes.c_uint32]
        self._libc.inotify_add_watch.restype = ctypes.c_int
        self._libc.inotify_rm_watch.argtypes = [ctypes.c_int, ctypes.c_int]
        self._libc.inotify_rm_watch.restype = ctypes.c_int
        return True

    def _get_errno(self):
        if self._get_errno_func is not None:
            return self._get_errno_func()
        return None

    def _inotify_init(self):
        assert self._libc is not None
        return self._libc.inotify_init()

    def _inotify_add_watch(self, fd, pathname, mask):
        assert self._libc is not None
        pathname = ctypes.create_string_buffer(pathname)
        return self._libc.inotify_add_watch(fd, pathname, mask)

    def _inotify_rm_watch(self, fd, wd):
        assert self._libc is not None
        return self._libc.inotify_rm_watch(fd, wd)

    def _sysctl(self, *args):
        assert self._libc is not None
        return self._libc.sysctl(*args)


# Logging
def logger_init():
    """Initialize logger instance."""
    log = logging.getLogger("pyinotify")
    console_handler = logging.StreamHandler()
    console_handler.setFormatter(
        logging.Formatter("[%(asctime)s %(name)s %(levelname)s] %(message)s"))
    log.addHandler(console_handler)
    log.setLevel(20)
    return log

log = logger_init()


# inotify's variables
class SysCtlINotify:
    """
    Access (read, write) inotify's variables through sysctl. Usually it
    requires administrator rights to update them.

    Examples:
      - Read max_queued_events attribute: myvar = max_queued_events.value
      - Update max_queued_events attribute: max_queued_events.value = 42
    """

    inotify_attrs = {'max_user_instances': 1,
                     'max_user_watches': 2,
                     'max_queued_events': 3}

    def __init__(self, attrname, inotify_wrapper):
        # FIXME: right now only supporting ctypes
        assert ctypes
        self._attrname = attrname
        self._inotify_wrapper = inotify_wrapper
        sino = ctypes.c_int * 3
        self._attr = sino(5, 20, SysCtlINotify.inotify_attrs[attrname])

    @staticmethod
    def create(attrname):
        """
        Factory method instanciating and returning the right wrapper.
        """
        # FIXME: right now only supporting ctypes
        if ctypes is None:
            return None
        inotify_wrapper = _CtypesLibcINotifyWrapper()
        if not inotify_wrapper.init():
            return None
        return SysCtlINotify(attrname, inotify_wrapper)

    def get_val(self):
        """
        Gets attribute's value. Raises OSError if the operation failed.

        @return: stored value.
        @rtype: int
        """
        oldv = ctypes.c_int(0)
        size = ctypes.c_int(ctypes.sizeof(oldv))
        sysctl = self._inotify_wrapper._sysctl
        res = sysctl(self._attr, 3,
                     ctypes.c_voidp(ctypes.addressof(oldv)),
                     ctypes.addressof(size),
                     None, 0)
        if res == -1:
            raise OSError(self._inotify_wrapper.get_errno(),
                          self._inotify_wrapper.str_errno())
        return oldv.value

    def set_val(self, nval):
        """
        Sets new attribute's value. Raises OSError if the operation failed.

        @param nval: replaces current value by nval.
        @type nval: int
        """
        oldv = ctypes.c_int(0)
        sizeo = ctypes.c_int(ctypes.sizeof(oldv))
        newv = ctypes.c_int(nval)
        sizen = ctypes.c_int(ctypes.sizeof(newv))
        sysctl = self._inotify_wrapper._sysctl
        res = sysctl(self._attr, 3,
                     ctypes.c_voidp(ctypes.addressof(oldv)),
                     ctypes.addressof(sizeo),
                     ctypes.c_voidp(ctypes.addressof(newv)),
                     sizen)
        if res == -1:
            raise OSError(self._inotify_wrapper.get_errno(),
                          self._inotify_wrapper.str_errno())

    value = property(get_val, set_val)

    def __repr__(self):
        return '<%s=%d>' % (self._attrname, self.get_val())


# Inotify's variables
#
# FIXME: currently these variables are only accessible when ctypes is used,
#        otherwise there are set to None.
#
# read: myvar = max_queued_events.value
# update: max_queued_events.value = 42
#
for attrname in ('max_queued_events', 'max_user_instances', 'max_user_watches'):
    globals()[attrname] = SysCtlINotify.create(attrname)


class EventsCodes:
    """
    Set of codes corresponding to each kind of events.
    Some of these flags are used to communicate with inotify, whereas
    the others are sent to userspace by inotify notifying some events.

    @cvar IN_ACCESS: File was accessed.
    @type IN_ACCESS: int
    @cvar IN_MODIFY: File was modified.
    @type IN_MODIFY: int
    @cvar IN_ATTRIB: Metadata changed.
    @type IN_ATTRIB: int
    @cvar IN_CLOSE_WRITE: Writtable file was closed.
    @type IN_CLOSE_WRITE: int
    @cvar IN_CLOSE_NOWRITE: Unwrittable file closed.
    @type IN_CLOSE_NOWRITE: int
    @cvar IN_OPEN: File was opened.
    @type IN_OPEN: int
    @cvar IN_MOVED_FROM: File was moved from X.
    @type IN_MOVED_FROM: int
    @cvar IN_MOVED_TO: File was moved to Y.
    @type IN_MOVED_TO: int
    @cvar IN_CREATE: Subfile was created.
    @type IN_CREATE: int
    @cvar IN_DELETE: Subfile was deleted.
    @type IN_DELETE: int
    @cvar IN_DELETE_SELF: Self (watched item itself) was deleted.
    @type IN_DELETE_SELF: int
    @cvar IN_MOVE_SELF: Self (watched item itself) was moved.
    @type IN_MOVE_SELF: int
    @cvar IN_UNMOUNT: Backing fs was unmounted.
    @type IN_UNMOUNT: int
    @cvar IN_Q_OVERFLOW: Event queued overflowed.
    @type IN_Q_OVERFLOW: int
    @cvar IN_IGNORED: File was ignored.
    @type IN_IGNORED: int
    @cvar IN_ONLYDIR: only watch the path if it is a directory (new
                      in kernel 2.6.15).
    @type IN_ONLYDIR: int
    @cvar IN_DONT_FOLLOW: don't follow a symlink (new in kernel 2.6.15).
                          IN_ONLYDIR we can make sure that we don't watch
                          the target of symlinks.
    @type IN_DONT_FOLLOW: int
    @cvar IN_EXCL_UNLINK: Events are not generated for children after they
                          have been unlinked from the watched directory.
                          (new in kernel 2.6.36).
    @type IN_EXCL_UNLINK: int
    @cvar IN_MASK_ADD: add to the mask of an already existing watch (new
                       in kernel 2.6.14).
    @type IN_MASK_ADD: int
    @cvar IN_ISDIR: Event occurred against dir.
    @type IN_ISDIR: int
    @cvar IN_ONESHOT: Only send event once.
    @type IN_ONESHOT: int
    @cvar ALL_EVENTS: Alias for considering all of the events.
    @type ALL_EVENTS: int
    """

    # The idea here is 'configuration-as-code' - this way, we get our nice class
    # constants, but we also get nice human-friendly text mappings to do lookups
    # against as well, for free:
    FLAG_COLLECTIONS = {'OP_FLAGS': {
        'IN_ACCESS'        : 0x00000001,  # File was accessed
        'IN_MODIFY'        : 0x00000002,  # File was modified
        'IN_ATTRIB'        : 0x00000004,  # Metadata changed
        'IN_CLOSE_WRITE'   : 0x00000008,  # Writable file was closed
        'IN_CLOSE_NOWRITE' : 0x00000010,  # Unwritable file closed
        'IN_OPEN'          : 0x00000020,  # File was opened
        'IN_MOVED_FROM'    : 0x00000040,  # File was moved from X
        'IN_MOVED_TO'      : 0x00000080,  # File was moved to Y
        'IN_CREATE'        : 0x00000100,  # Subfile was created
        'IN_DELETE'        : 0x00000200,  # Subfile was deleted
        'IN_DELETE_SELF'   : 0x00000400,  # Self (watched item itself)
                                          # was deleted
        'IN_MOVE_SELF'     : 0x00000800,  # Self (watched item itself) was moved
        },
                        'EVENT_FLAGS': {
        'IN_UNMOUNT'       : 0x00002000,  # Backing fs was unmounted
        'IN_Q_OVERFLOW'    : 0x00004000,  # Event queued overflowed
        'IN_IGNORED'       : 0x00008000,  # File was ignored
        },
                        'SPECIAL_FLAGS': {
        'IN_ONLYDIR'       : 0x01000000,  # only watch the path if it is a
                                          # directory
        'IN_DONT_FOLLOW'   : 0x02000000,  # don't follow a symlink
        'IN_EXCL_UNLINK'   : 0x04000000,  # exclude events on unlinked objects
        'IN_MASK_ADD'      : 0x20000000,  # add to the mask of an already
                                          # existing watch
        'IN_ISDIR'         : 0x40000000,  # event occurred against dir
        'IN_ONESHOT'       : 0x80000000,  # only send event once
        },
                        }

    def maskname(mask):
        """
        Returns the event name associated to mask. IN_ISDIR is appended to
        the result when appropriate. Note: only one event is returned, because
        only one event can be raised at a given time.

        @param mask: mask.
        @type mask: int
        @return: event name.
        @rtype: str
        """
        ms = mask
        name = '%s'
        if mask & IN_ISDIR:
            ms = mask - IN_ISDIR
            name = '%s|IN_ISDIR'
        return name % EventsCodes.ALL_VALUES[ms]

    maskname = staticmethod(maskname)


# So let's now turn the configuration into code
EventsCodes.ALL_FLAGS = {}
EventsCodes.ALL_VALUES = {}
for flagc, valc in EventsCodes.FLAG_COLLECTIONS.items():
    # Make the collections' members directly accessible through the
    # class dictionary
    setattr(EventsCodes, flagc, valc)

    # Collect all the flags under a common umbrella
    EventsCodes.ALL_FLAGS.update(valc)

    # Make the individual masks accessible as 'constants' at globals() scope
    # and masknames accessible by values.
    for name, val in valc.items():
        globals()[name] = val
        EventsCodes.ALL_VALUES[val] = name


# all 'normal' events
ALL_EVENTS = reduce(lambda x, y: x | y, EventsCodes.OP_FLAGS.values())
EventsCodes.ALL_FLAGS['ALL_EVENTS'] = ALL_EVENTS
EventsCodes.ALL_VALUES[ALL_EVENTS] = 'ALL_EVENTS'


class _Event:
    """
    Event structure, represent events raised by the system. This
    is the base class and should be subclassed.

    """
    def __init__(self, dict_):
        """
        Attach attributes (contained in dict_) to self.

        @param dict_: Set of attributes.
        @type dict_: dictionary
        """
        for tpl in dict_.items():
            setattr(self, *tpl)

    def __repr__(self):
        """
        @return: Generic event string representation.
        @rtype: str
        """
        s = ''
        for attr, value in sorted(self.__dict__.items(), key=lambda x: x[0]):
            if attr.startswith('_'):
                continue
            if attr == 'mask':
                value = hex(getattr(self, attr))
            elif isinstance(value, basestring) and not value:
                value = "''"
            s += ' %s%s%s' % (output_format.field_name(attr),
                              output_format.punctuation('='),
                              output_format.field_value(value))

        s = '%s%s%s %s' % (output_format.punctuation('<'),
                           output_format.class_name(self.__class__.__name__),
                           s,
                           output_format.punctuation('>'))
        return s

    def __str__(self):
        return repr(self)


class _RawEvent(_Event):
    """
    Raw event, it contains only the informations provided by the system.
    It doesn't infer anything.
    """
    def __init__(self, wd, mask, cookie, name):
        """
        @param wd: Watch Descriptor.
        @type wd: int
        @param mask: Bitmask of events.
        @type mask: int
        @param cookie: Cookie.
        @type cookie: int
        @param name: Basename of the file or directory against which the
                     event was raised in case where the watched directory
                     is the parent directory. None if the event was raised
                     on the watched item itself.
        @type name: string or None
        """
        # Use this variable to cache the result of str(self), this object
        # is immutable.
        self._str = None
        # name: remove trailing '\0'
        d = {'wd': wd,
             'mask': mask,
             'cookie': cookie,
             'name': name.rstrip('\0')}
        _Event.__init__(self, d)
        log.debug(str(self))

    def __str__(self):
        if self._str is None:
            self._str = _Event.__str__(self)
        return self._str


class Event(_Event):
    """
    This class contains all the useful informations about the observed
    event. However, the presence of each field is not guaranteed and
    depends on the type of event. In effect, some fields are irrelevant
    for some kind of event (for example 'cookie' is meaningless for
    IN_CREATE whereas it is mandatory for IN_MOVE_TO).

    The possible fields are:
      - wd (int): Watch Descriptor.
      - mask (int): Mask.
      - maskname (str): Readable event name.
      - path (str): path of the file or directory being watched.
      - name (str): Basename of the file or directory against which the
              event was raised in case where the watched directory
              is the parent directory. None if the event was raised
              on the watched item itself. This field is always provided
              even if the string is ''.
      - pathname (str): Concatenation of 'path' and 'name'.
      - src_pathname (str): Only present for IN_MOVED_TO events and only in
              the case where IN_MOVED_FROM events are watched too. Holds the
              source pathname from where pathname was moved from.
      - cookie (int): Cookie.
      - dir (bool): True if the event was raised against a directory.

    """
    def __init__(self, raw):
        """
        Concretely, this is the raw event plus inferred infos.
        """
        _Event.__init__(self, raw)
        self.maskname = EventsCodes.maskname(self.mask)
        if COMPATIBILITY_MODE:
            self.event_name = self.maskname
        try:
            if self.name:
                self.pathname = os.path.abspath(os.path.join(self.path,
                                                             self.name))
            else:
                self.pathname = os.path.abspath(self.path)
        except AttributeError, err:
            # Usually it is not an error some events are perfectly valids
            # despite the lack of these attributes.
            log.debug(err)


class ProcessEventError(PyinotifyError):
    """
    ProcessEventError Exception. Raised on ProcessEvent error.
    """
    def __init__(self, err):
        """
        @param err: Exception error description.
        @type err: string
        """
        PyinotifyError.__init__(self, err)


class _ProcessEvent:
    """
    Abstract processing event class.
    """
    def __call__(self, event):
        """
        To behave like a functor the object must be callable.
        This method is a dispatch method. Its lookup order is:
          1. process_MASKNAME method
          2. process_FAMILY_NAME method
          3. otherwise calls process_default

        @param event: Event to be processed.
        @type event: Event object
        @return: By convention when used from the ProcessEvent class:
                 - Returning False or None (default value) means keep on
                 executing next chained functors (see chain.py example).
                 - Returning True instead means do not execute next
                   processing functions.
        @rtype: bool
        @raise ProcessEventError: Event object undispatchable,
                                  unknown event.
        """
        stripped_mask = event.mask - (event.mask & IN_ISDIR)
        maskname = EventsCodes.ALL_VALUES.get(stripped_mask)
        if maskname is None:
            raise ProcessEventError("Unknown mask 0x%08x" % stripped_mask)

        # 1- look for process_MASKNAME
        meth = getattr(self, 'process_' + maskname, None)
        if meth is not None:
            return meth(event)
        # 2- look for process_FAMILY_NAME
        meth = getattr(self, 'process_IN_' + maskname.split('_')[1], None)
        if meth is not None:
            return meth(event)
        # 3- default call method process_default
        return self.process_default(event)

    def __repr__(self):
        return '<%s>' % self.__class__.__name__


class _SysProcessEvent(_ProcessEvent):
    """
    There is three kind of processing according to each event:

      1. special handling (deletion from internal container, bug, ...).
      2. default treatment: which is applied to the majority of events.
      3. IN_ISDIR is never sent alone, he is piggybacked with a standard
         event, he is not processed as the others events, instead, its
         value is captured and appropriately aggregated to dst event.
    """
    def __init__(self, wm, notifier):
        """

        @param wm: Watch Manager.
        @type wm: WatchManager instance
        @param notifier: Notifier.
        @type notifier: Notifier instance
        """
        self._watch_manager = wm  # watch manager
        self._notifier = notifier  # notifier
        self._mv_cookie = {}  # {cookie(int): (src_path(str), date), ...}
        self._mv = {}  # {src_path(str): (dst_path(str), date), ...}

    def cleanup(self):
        """
        Cleanup (delete) old (>1mn) records contained in self._mv_cookie
        and self._mv.
        """
        date_cur_ = datetime.now()
        for seq in [self._mv_cookie, self._mv]:
            for k in seq.keys():
                if (date_cur_ - seq[k][1]) > timedelta(minutes=1):
                    log.debug('Cleanup: deleting entry %s', seq[k][0])
                    del seq[k]

    def process_IN_CREATE(self, raw_event):
        """
        If the event affects a directory and the auto_add flag of the
        targetted watch is set to True, a new watch is added on this
        new directory, with the same attribute values than those of
        this watch.
        """
        if raw_event.mask & IN_ISDIR:
            watch_ = self._watch_manager.get_watch(raw_event.wd)
            created_dir = os.path.join(watch_.path, raw_event.name)
            if watch_.auto_add and not watch_.exclude_filter(created_dir):
                addw = self._watch_manager.add_watch
                # The newly monitored directory inherits attributes from its
                # parent directory.
                addw_ret = addw(created_dir, watch_.mask,
                                proc_fun=watch_.proc_fun,
                                rec=False, auto_add=watch_.auto_add,
                                exclude_filter=watch_.exclude_filter)

                # Trick to handle mkdir -p /d1/d2/t3 where d1 is watched and
                # d2 and t3 (directory or file) are created.
                # Since the directory d2 is new, then everything inside it must
                # also be new.
                created_dir_wd = addw_ret.get(created_dir)
                if ((created_dir_wd is not None) and (created_dir_wd > 0) and
                    os.path.isdir(created_dir)):
                    try:
                        for name in os.listdir(created_dir):
                            inner = os.path.join(created_dir, name)
                            if self._watch_manager.get_wd(inner) is not None:
                                continue
                            # Generate (simulate) creation events for sub-
                            # directories and files.
                            if os.path.isfile(inner):
                                # symlinks are handled as files.
                                flags = IN_CREATE
                            elif os.path.isdir(inner):
                                flags = IN_CREATE | IN_ISDIR
                            else:
                                # This path should not be taken.
                                continue
                            rawevent = _RawEvent(created_dir_wd, flags, 0, name)
                            self._notifier.append_event(rawevent)
                    except OSError, err:
                        msg = "process_IN_CREATE, invalid directory %s: %s"
                        log.debug(msg % (created_dir, str(err)))
        return self.process_default(raw_event)

    def process_IN_MOVED_FROM(self, raw_event):
        """
        Map the cookie with the source path (+ date for cleaning).
        """
        watch_ = self._watch_manager.get_watch(raw_event.wd)
        path_ = watch_.path
        src_path = os.path.normpath(os.path.join(path_, raw_event.name))
        self._mv_cookie[raw_event.cookie] = (src_path, datetime.now())
        return self.process_default(raw_event, {'cookie': raw_event.cookie})

    def process_IN_MOVED_TO(self, raw_event):
        """
        Map the source path with the destination path (+ date for
        cleaning).
        """
        watch_ = self._watch_manager.get_watch(raw_event.wd)
        path_ = watch_.path
        dst_path = os.path.normpath(os.path.join(path_, raw_event.name))
        mv_ = self._mv_cookie.get(raw_event.cookie)
        to_append = {'cookie': raw_event.cookie}
        if mv_ is not None:
            self._mv[mv_[0]] = (dst_path, datetime.now())
            # Let's assume that IN_MOVED_FROM event is always queued before
            # that its associated (they share a common cookie) IN_MOVED_TO
            # event is queued itself. It is then possible in that scenario
            # to provide as additional information to the IN_MOVED_TO event
            # the original pathname of the moved file/directory.
            to_append['src_pathname'] = mv_[0]
        elif (raw_event.mask & IN_ISDIR and watch_.auto_add and
              not watch_.exclude_filter(dst_path)):
            # We got a diretory that's "moved in" from an unknown source and
            # auto_add is enabled. Manually add watches to the inner subtrees.
            # The newly monitored directory inherits attributes from its
            # parent directory.
            self._watch_manager.add_watch(dst_path, watch_.mask,
                                          proc_fun=watch_.proc_fun,
                                          rec=True, auto_add=True,
                                          exclude_filter=watch_.exclude_filter)
        return self.process_default(raw_event, to_append)

    def process_IN_MOVE_SELF(self, raw_event):
        """
        STATUS: the following bug has been fixed in recent kernels (FIXME:
        which version ?). Now it raises IN_DELETE_SELF instead.

        Old kernels were bugged, this event raised when the watched item
        were moved, so we had to update its path, but under some circumstances
        it was impossible: if its parent directory and its destination
        directory wasn't watched. The kernel (see include/linux/fsnotify.h)
        doesn't bring us enough informations like the destination path of
        moved items.
        """
        watch_ = self._watch_manager.get_watch(raw_event.wd)
        src_path = watch_.path
        mv_ = self._mv.get(src_path)
        if mv_:
            dest_path = mv_[0]
            watch_.path = dest_path
            # add the separator to the source path to avoid overlapping
            # path issue when testing with startswith()
            src_path += os.path.sep
            src_path_len = len(src_path)
            # The next loop renames all watches with src_path as base path.
            # It seems that IN_MOVE_SELF does not provide IN_ISDIR information
            # therefore the next loop is iterated even if raw_event is a file.
            for w in self._watch_manager.watches.values():
                if w.path.startswith(src_path):
                    # Note that dest_path is a normalized path.
                    w.path = os.path.join(dest_path, w.path[src_path_len:])
        else:
            log.error("The pathname '%s' of this watch %s has probably changed "
                      "and couldn't be updated, so it cannot be trusted "
                      "anymore. To fix this error move directories/files only "
                      "between watched parents directories, in this case e.g. "
                      "put a watch on '%s'.",
                      watch_.path, watch_,
                      os.path.normpath(os.path.join(watch_.path,
                                                    os.path.pardir)))
            if not watch_.path.endswith('-unknown-path'):
                watch_.path += '-unknown-path'
        return self.process_default(raw_event)

    def process_IN_Q_OVERFLOW(self, raw_event):
        """
        Only signal an overflow, most of the common flags are irrelevant
        for this event (path, wd, name).
        """
        return Event({'mask': raw_event.mask})

    def process_IN_IGNORED(self, raw_event):
        """
        The watch descriptor raised by this event is now ignored (forever),
        it can be safely deleted from the watch manager dictionary.
        After this event we can be sure that neither the event queue nor
        the system will raise an event associated to this wd again.
        """
        event_ = self.process_default(raw_event)
        self._watch_manager.del_watch(raw_event.wd)
        return event_

    def process_default(self, raw_event, to_append=None):
        """
        Commons handling for the followings events:

        IN_ACCESS, IN_MODIFY, IN_ATTRIB, IN_CLOSE_WRITE, IN_CLOSE_NOWRITE,
        IN_OPEN, IN_DELETE, IN_DELETE_SELF, IN_UNMOUNT.
        """
        watch_ = self._watch_manager.get_watch(raw_event.wd)
        if raw_event.mask & (IN_DELETE_SELF | IN_MOVE_SELF):
            # Unfornulately this information is not provided by the kernel
            dir_ = watch_.dir
        else:
            dir_ = bool(raw_event.mask & IN_ISDIR)
        dict_ = {'wd': raw_event.wd,
                 'mask': raw_event.mask,
                 'path': watch_.path,
                 'name': raw_event.name,
                 'dir': dir_}
        if COMPATIBILITY_MODE:
            dict_['is_dir'] = dir_
        if to_append is not None:
            dict_.update(to_append)
        return Event(dict_)


class ProcessEvent(_ProcessEvent):
    """
    Process events objects, can be specialized via subclassing, thus its
    behavior can be overriden:

    Note: you should not override __init__ in your subclass instead define
    a my_init() method, this method will be called automatically from the
    constructor of this class with its optionals parameters.

      1. Provide specialized individual methods, e.g. process_IN_DELETE for
         processing a precise type of event (e.g. IN_DELETE in this case).
      2. Or/and provide methods for processing events by 'family', e.g.
         process_IN_CLOSE method will process both IN_CLOSE_WRITE and
         IN_CLOSE_NOWRITE events (if process_IN_CLOSE_WRITE and
         process_IN_CLOSE_NOWRITE aren't defined though).
      3. Or/and override process_default for catching and processing all
         the remaining types of events.
    """
    pevent = None

    def __init__(self, pevent=None, **kargs):
        """
        Enable chaining of ProcessEvent instances.

        @param pevent: Optional callable object, will be called on event
                       processing (before self).
        @type pevent: callable
        @param kargs: This constructor is implemented as a template method
                      delegating its optionals keyworded arguments to the
                      method my_init().
        @type kargs: dict
        """
        self.pevent = pevent
        self.my_init(**kargs)

    def my_init(self, **kargs):
        """
        This method is called from ProcessEvent.__init__(). This method is
        empty here and must be redefined to be useful. In effect, if you
        need to specifically initialize your subclass' instance then you
        just have to override this method in your subclass. Then all the
        keyworded arguments passed to ProcessEvent.__init__() will be
        transmitted as parameters to this method. Beware you MUST pass
        keyword arguments though.

        @param kargs: optional delegated arguments from __init__().
        @type kargs: dict
        """
        pass

    def __call__(self, event):
        stop_chaining = False
        if self.pevent is not None:
            # By default methods return None so we set as guideline
            # that methods asking for stop chaining must explicitely
            # return non None or non False values, otherwise the default
            # behavior will be to accept chain call to the corresponding
            # local method.
            stop_chaining = self.pevent(event)
        if not stop_chaining:
            return _ProcessEvent.__call__(self, event)

    def nested_pevent(self):
        return self.pevent

    def process_IN_Q_OVERFLOW(self, event):
        """
        By default this method only reports warning messages, you can overredide
        it by subclassing ProcessEvent and implement your own
        process_IN_Q_OVERFLOW method. The actions you can take on receiving this
        event is either to update the variable max_queued_events in order to
        handle more simultaneous events or to modify your code in order to
        accomplish a better filtering diminishing the number of raised events.
        Because this method is defined, IN_Q_OVERFLOW will never get
        transmitted as arguments to process_default calls.

        @param event: IN_Q_OVERFLOW event.
        @type event: dict
        """
        log.warning('Event queue overflowed.')

    def process_default(self, event):
        """
        Default processing event method. By default does nothing. Subclass
        ProcessEvent and redefine this method in order to modify its behavior.

        @param event: Event to be processed. Can be of any type of events but
                      IN_Q_OVERFLOW events (see method process_IN_Q_OVERFLOW).
        @type event: Event instance
        """
        pass


class PrintAllEvents(ProcessEvent):
    """
    Dummy class used to print events strings representations. For instance this
    class is used from command line to print all received events to stdout.
    """
    def my_init(self, out=None):
        """
        @param out: Where events will be written.
        @type out: Object providing a valid file object interface.
        """
        if out is None:
            out = sys.stdout
        self._out = out

    def process_default(self, event):
        """
        Writes event string representation to file object provided to
        my_init().

        @param event: Event to be processed. Can be of any type of events but
                      IN_Q_OVERFLOW events (see method process_IN_Q_OVERFLOW).
        @type event: Event instance
        """
        self._out.write(str(event))
        self._out.write('\n')
        self._out.flush()


class ChainIfTrue(ProcessEvent):
    """
    Makes conditional chaining depending on the result of the nested
    processing instance.
    """
    def my_init(self, func):
        """
        Method automatically called from base class constructor.
        """
        self._func = func

    def process_default(self, event):
        return not self._func(event)


class Stats(ProcessEvent):
    """
    Compute and display trivial statistics about processed events.
    """
    def my_init(self):
        """
        Method automatically called from base class constructor.
        """
        self._start_time = time.time()
        self._stats = {}
        self._stats_lock = threading.Lock()

    def process_default(self, event):
        """
        Processes |event|.
        """
        self._stats_lock.acquire()
        try:
            events = event.maskname.split('|')
            for event_name in events:
                count = self._stats.get(event_name, 0)
                self._stats[event_name] = count + 1
        finally:
            self._stats_lock.release()

    def _stats_copy(self):
        self._stats_lock.acquire()
        try:
            return self._stats.copy()
        finally:
            self._stats_lock.release()

    def __repr__(self):
        stats = self._stats_copy()

        elapsed = int(time.time() - self._start_time)
        elapsed_str = ''
        if elapsed < 60:
            elapsed_str = str(elapsed) + 'sec'
        elif 60 <= elapsed < 3600:
            elapsed_str = '%dmn%dsec' % (elapsed / 60, elapsed % 60)
        elif 3600 <= elapsed < 86400:
            elapsed_str = '%dh%dmn' % (elapsed / 3600, (elapsed % 3600) / 60)
        elif elapsed >= 86400:
            elapsed_str = '%dd%dh' % (elapsed / 86400, (elapsed % 86400) / 3600)
        stats['ElapsedTime'] = elapsed_str

        l = []
        for ev, value in sorted(stats.items(), key=lambda x: x[0]):
            l.append(' %s=%s' % (output_format.field_name(ev),
                                 output_format.field_value(value)))
        s = '<%s%s >' % (output_format.class_name(self.__class__.__name__),
                         ''.join(l))
        return s

    def dump(self, filename):
        """
        Dumps statistics.

        @param filename: filename where stats will be dumped, filename is
                         created and must not exist prior to this call.
        @type filename: string
        """
        flags = os.O_WRONLY|os.O_CREAT|os.O_NOFOLLOW|os.O_EXCL
        fd = os.open(filename, flags, 0600)
        os.write(fd, str(self))
        os.close(fd)

    def __str__(self, scale=45):
        stats = self._stats_copy()
        if not stats:
            return ''

        m = max(stats.values())
        unity = float(scale) / m
        fmt = '%%-26s%%-%ds%%s' % (len(output_format.field_value('@' * scale))
                                   + 1)
        def func(x):
            return fmt % (output_format.field_name(x[0]),
                          output_format.field_value('@' * int(x[1] * unity)),
                          output_format.simple('%d' % x[1], 'yellow'))
        s = '\n'.join(map(func, sorted(stats.items(), key=lambda x: x[0])))
        return s


class NotifierError(PyinotifyError):
    """
    Notifier Exception. Raised on Notifier error.

    """
    def __init__(self, err):
        """
        @param err: Exception string's description.
        @type err: string
        """
        PyinotifyError.__init__(self, err)


class Notifier:
    """
    Read notifications, process events.

    """
    def __init__(self, watch_manager, default_proc_fun=None, read_freq=0,
                 threshold=0, timeout=None):
        """
        Initialization. read_freq, threshold and timeout parameters are used
        when looping.

        @param watch_manager: Watch Manager.
        @type watch_manager: WatchManager instance
        @param default_proc_fun: Default processing method. If None, a new
                                 instance of PrintAllEvents will be assigned.
        @type default_proc_fun: instance of ProcessEvent
        @param read_freq: if read_freq == 0, events are read asap,
                          if read_freq is > 0, this thread sleeps
                          max(0, read_freq - timeout) seconds. But if
                          timeout is None it may be different because
                          poll is blocking waiting for something to read.
        @type read_freq: int
        @param threshold: File descriptor will be read only if the accumulated
                          size to read becomes >= threshold. If != 0, you likely
                          want to use it in combination with an appropriate
                          value for read_freq because without that you would
                          keep looping without really reading anything and that
                          until the amount of events to read is >= threshold.
                          At least with read_freq set you might sleep.
        @type threshold: int
        @param timeout:
            https://docs.python.org/3/library/select.html#polling-objects
        @type timeout: int
        """
        # Watch Manager instance
        self._watch_manager = watch_manager
        # File descriptor
        self._fd = self._watch_manager.get_fd()
        # Poll object and registration
        self._pollobj = select.poll()
        self._pollobj.register(self._fd, select.POLLIN)
        # This pipe is correctely initialized and used by ThreadedNotifier
        self._pipe = (-1, -1)
        # Event queue
        self._eventq = deque()
        # System processing functor, common to all events
        self._sys_proc_fun = _SysProcessEvent(self._watch_manager, self)
        # Default processing method
        self._default_proc_fun = default_proc_fun
        if default_proc_fun is None:
            self._default_proc_fun = PrintAllEvents()
        # Loop parameters
        self._read_freq = read_freq
        self._threshold = threshold
        self._timeout = timeout
        # Coalesce events option
        self._coalesce = False
        # set of str(raw_event), only used when coalesce option is True
        self._eventset = set()

    def append_event(self, event):
        """
        Append a raw event to the event queue.

        @param event: An event.
        @type event: _RawEvent instance.
        """
        self._eventq.append(event)

    def proc_fun(self):
        return self._default_proc_fun

    def coalesce_events(self, coalesce=True):
        """
        Coalescing events. Events are usually processed by batchs, their size
        depend on various factors. Thus, before processing them, events received
        from inotify are aggregated in a fifo queue. If this coalescing
        option is enabled events are filtered based on their unicity, only
        unique events are enqueued, doublons are discarded. An event is unique
        when the combination of its fields (wd, mask, cookie, name) is unique
        among events of a same batch. After a batch of events is processed any
        events is accepted again. By default this option is disabled, you have
        to explictly call this function to turn it on.

        @param coalesce: Optional new coalescing value. True by default.
        @type coalesce: Bool
        """
        self._coalesce = coalesce
        if not coalesce:
            self._eventset.clear()

    def check_events(self, timeout=None):
        """
        Check for new events available to read, blocks up to timeout
        milliseconds.

        @param timeout: If specified it overrides the corresponding instance
                        attribute _timeout.
        @type timeout: int

        @return: New events to read.
        @rtype: bool
        """
        while True:
            try:
                # blocks up to 'timeout' milliseconds
                if timeout is None:
                    timeout = self._timeout
                ret = self._pollobj.poll(timeout)
            except select.error, err:
                if err[0] == errno.EINTR:
                    continue # interrupted, retry
                else:
                    raise
            else:
                break

        if not ret or (self._pipe[0] == ret[0][0]):
            return False
        # only one fd is polled
        return ret[0][1] & select.POLLIN

    def read_events(self):
        """
        Read events from device, build _RawEvents, and enqueue them.
        """
        buf_ = array.array('i', [0])
        # get event queue size
        if fcntl.ioctl(self._fd, termios.FIONREAD, buf_, 1) == -1:
            return
        queue_size = buf_[0]
        if queue_size < self._threshold:
            log.debug('(fd: %d) %d bytes available to read but threshold is '
                      'fixed to %d bytes', self._fd, queue_size,
                      self._threshold)
            return

        try:
            # Read content from file
            r = os.read(self._fd, queue_size)
        except Exception, msg:
            raise NotifierError(msg)
        log.debug('Event queue size: %d', queue_size)
        rsum = 0  # counter
        while rsum < queue_size:
            s_size = 16
            # Retrieve wd, mask, cookie and fname_len
            wd, mask, cookie, fname_len = struct.unpack('iIII',
                                                        r[rsum:rsum+s_size])
            # Retrieve name
            fname, = struct.unpack('%ds' % fname_len,
                                   r[rsum + s_size:rsum + s_size + fname_len])
            rawevent = _RawEvent(wd, mask, cookie, fname)
            if self._coalesce:
                # Only enqueue new (unique) events.
                raweventstr = str(rawevent)
                if raweventstr not in self._eventset:
                    self._eventset.add(raweventstr)
                    self._eventq.append(rawevent)
            else:
                self._eventq.append(rawevent)
            rsum += s_size + fname_len

    def process_events(self):
        """
        Routine for processing events from queue by calling their
        associated proccessing method (an instance of ProcessEvent).
        It also does internal processings, to keep the system updated.
        """
        while self._eventq:
            raw_event = self._eventq.popleft()  # pop next event
            if self._watch_manager.ignore_events:
                log.debug("Event ignored: %s" % repr(raw_event))
                continue
            watch_ = self._watch_manager.get_watch(raw_event.wd)
            if (watch_ is None) and not (raw_event.mask & IN_Q_OVERFLOW):
                if not (raw_event.mask & IN_IGNORED):
                    # Not really sure how we ended up here, nor how we should
                    # handle these types of events and if it is appropriate to
                    # completly skip them (like we are doing here).
                    log.warning("Unable to retrieve Watch object associated to %s",
                                repr(raw_event))
                continue
            revent = self._sys_proc_fun(raw_event)  # system processings
            if watch_ and watch_.proc_fun:
                watch_.proc_fun(revent)  # user processings
            else:
                self._default_proc_fun(revent)
        self._sys_proc_fun.cleanup()  # remove olds MOVED_* events records
        if self._coalesce:
            self._eventset.clear()

    def __daemonize(self, pid_file=None, stdin=os.devnull, stdout=os.devnull,
                    stderr=os.devnull):
        """
        @param pid_file: file where the pid will be written. If pid_file=None
                         the pid is written to
                         /var/run/<sys.argv[0]|pyinotify>.pid, if pid_file=False
                         no pid_file is written.
        @param stdin:
        @param stdout:
        @param stderr: files associated to common streams.
        """
        if pid_file is None:
            dirname = '/var/run/'
            basename = os.path.basename(sys.argv[0]) or 'pyinotify'
            pid_file = os.path.join(dirname, basename + '.pid')

        if pid_file != False and os.path.lexists(pid_file):
            err = 'Cannot daemonize: pid file %s already exists.' % pid_file
            raise NotifierError(err)

        def fork_daemon():
            # Adapted from Chad J. Schroeder's recipe
            # @see http://code.activestate.com/recipes/278731/
            pid = os.fork()
            if (pid == 0):
                # parent 2
                os.setsid()
                pid = os.fork()
                if (pid == 0):
                    # child
                    os.chdir('/')
                    os.umask(022)
                else:
                    # parent 2
                    os._exit(0)
            else:
                # parent 1
                os._exit(0)

            fd_inp = os.open(stdin, os.O_RDONLY)
            os.dup2(fd_inp, 0)
            fd_out = os.open(stdout, os.O_WRONLY|os.O_CREAT, 0600)
            os.dup2(fd_out, 1)
            fd_err = os.open(stderr, os.O_WRONLY|os.O_CREAT, 0600)
            os.dup2(fd_err, 2)

        # Detach task
        fork_daemon()

        # Write pid
        if pid_file != False:
            flags = os.O_WRONLY|os.O_CREAT|os.O_NOFOLLOW|os.O_EXCL
            fd_pid = os.open(pid_file, flags, 0600)
            os.write(fd_pid, str(os.getpid()) + '\n')
            os.close(fd_pid)
            # Register unlink function
            atexit.register(lambda : os.unlink(pid_file))

    def _sleep(self, ref_time):
        # Only consider sleeping if read_freq is > 0
        if self._read_freq > 0:
            cur_time = time.time()
            sleep_amount = self._read_freq - (cur_time - ref_time)
            if sleep_amount > 0:
                log.debug('Now sleeping %d seconds', sleep_amount)
                time.sleep(sleep_amount)

    def loop(self, callback=None, daemonize=False, **args):
        """
        Events are read only one time every min(read_freq, timeout)
        seconds at best and only if the size to read is >= threshold.
        After this method returns it must not be called again for the same
        instance.

        @param callback: Functor called after each event processing iteration.
                         Expects to receive the notifier object (self) as first
                         parameter. If this function returns True the loop is
                         immediately terminated otherwise the loop method keeps
                         looping.
        @type callback: callable object or function
        @param daemonize: This thread is daemonized if set to True.
        @type daemonize: boolean
        @param args: Optional and relevant only if daemonize is True. Remaining
                     keyworded arguments are directly passed to daemonize see
                     __daemonize() method. If pid_file=None or is set to a
                     pathname the caller must ensure the file does not exist
                     before this method is called otherwise an exception
                     pyinotify.NotifierError will be raised. If pid_file=False
                     it is still daemonized but the pid is not written in any
                     file.
        @type args: various
        """
        if daemonize:
            self.__daemonize(**args)

        # Read and process events forever
        while 1:
            try:
                self.process_events()
                if (callback is not None) and (callback(self) is True):
                    break
                ref_time = time.time()
                # check_events is blocking
                if self.check_events():
                    self._sleep(ref_time)
                    self.read_events()
            except KeyboardInterrupt:
                # Stop monitoring if sigint is caught (Control-C).
                log.debug('Pyinotify stops monitoring.')
                break
        # Close internals
        self.stop()

    def stop(self):
        """
        Close inotify's instance (close its file descriptor).
        It destroys all existing watches, pending events,...
        This method is automatically called at the end of loop().
        """
        self._pollobj.unregister(self._fd)
        os.close(self._fd)
        self._sys_proc_fun = None


class ThreadedNotifier(threading.Thread, Notifier):
    """
    This notifier inherits from threading.Thread for instanciating a separate
    thread, and also inherits from Notifier, because it is a threaded notifier.

    Note that every functionality provided by this class is also provided
    through Notifier class. Moreover Notifier should be considered first because
    it is not threaded and could be easily daemonized.
    """
    def __init__(self, watch_manager, default_proc_fun=None, read_freq=0,
                 threshold=0, timeout=None):
        """
        Initialization, initialize base classes. read_freq, threshold and
        timeout parameters are used when looping.

        @param watch_manager: Watch Manager.
        @type watch_manager: WatchManager instance
        @param default_proc_fun: Default processing method. See base class.
        @type default_proc_fun: instance of ProcessEvent
        @param read_freq: if read_freq == 0, events are read asap,
                          if read_freq is > 0, this thread sleeps
                          max(0, read_freq - timeout) seconds.
        @type read_freq: int
        @param threshold: File descriptor will be read only if the accumulated
                          size to read becomes >= threshold. If != 0, you likely
                          want to use it in combination with an appropriate
                          value set for read_freq because without that you would
                          keep looping without really reading anything and that
                          until the amount of events to read is >= threshold. At
                          least with read_freq you might sleep.
        @type threshold: int
        @param timeout:
            https://docs.python.org/3/library/select.html#polling-objects
        @type timeout: int
        """
        # Init threading base class
        threading.Thread.__init__(self)
        # Stop condition
        self._stop_event = threading.Event()
        # Init Notifier base class
        Notifier.__init__(self, watch_manager, default_proc_fun, read_freq,
                          threshold, timeout)
        # Create a new pipe used for thread termination
        self._pipe = os.pipe()
        self._pollobj.register(self._pipe[0], select.POLLIN)

    def stop(self):
        """
        Stop notifier's loop. Stop notification. Join the thread.
        """
        self._stop_event.set()
        os.write(self._pipe[1], 'stop')
        threading.Thread.join(self)
        Notifier.stop(self)
        self._pollobj.unregister(self._pipe[0])
        os.close(self._pipe[0])
        os.close(self._pipe[1])

    def loop(self):
        """
        Thread's main loop. Don't meant to be called by user directly.
        Call inherited start() method instead.

        Events are read only once time every min(read_freq, timeout)
        seconds at best and only if the size of events to read is >= threshold.
        """
        # When the loop must be terminated .stop() is called, 'stop'
        # is written to pipe fd so poll() returns and .check_events()
        # returns False which make evaluate the While's stop condition
        # ._stop_event.isSet() wich put an end to the thread's execution.
        while not self._stop_event.isSet():
            self.process_events()
            ref_time = time.time()
            if self.check_events():
                self._sleep(ref_time)
                self.read_events()

    def run(self):
        """
        Start thread's loop: read and process events until the method
        stop() is called.
        Never call this method directly, instead call the start() method
        inherited from threading.Thread, which then will call run() in
        its turn.
        """
        self.loop()


class AsyncNotifier(asyncore.file_dispatcher, Notifier):
    """
    This notifier inherits from asyncore.file_dispatcher in order to be able to
    use pyinotify along with the asyncore framework.

    """
    def __init__(self, watch_manager, default_proc_fun=None, read_freq=0,
                 threshold=0, timeout=None, channel_map=None):
        """
        Initializes the async notifier. The only additional parameter is
        'channel_map' which is the optional asyncore private map. See
        Notifier class for the meaning of the others parameters.

        """
        Notifier.__init__(self, watch_manager, default_proc_fun, read_freq,
                          threshold, timeout)
        asyncore.file_dispatcher.__init__(self, self._fd, channel_map)

    def handle_read(self):
        """
        When asyncore tells us we can read from the fd, we proceed processing
        events. This method can be overridden for handling a notification
        differently.

        """
        self.read_events()
        self.process_events()


class TornadoAsyncNotifier(Notifier):
    """
    Tornado ioloop adapter.

    """
    def __init__(self, watch_manager, ioloop, callback=None,
                 default_proc_fun=None, read_freq=0, threshold=0, timeout=None,
                 channel_map=None):
        """
        Note that if later you must call ioloop.close() be sure to let the
        default parameter to all_fds=False.

        See example tornado_notifier.py for an example using this notifier.

        @param ioloop: Tornado's IO loop.
        @type ioloop: tornado.ioloop.IOLoop instance.
        @param callback: Functor called at the end of each call to handle_read
                         (IOLoop's read handler). Expects to receive the
                         notifier object (self) as single parameter.
        @type callback: callable object or function
        """
        self.io_loop = ioloop
        self.handle_read_callback = callback
        Notifier.__init__(self, watch_manager, default_proc_fun, read_freq,
                          threshold, timeout)
        ioloop.add_handler(self._fd, self.handle_read, ioloop.READ)

    def stop(self):
        self.io_loop.remove_handler(self._fd)
        Notifier.stop(self)

    def handle_read(self, *args, **kwargs):
        """
        See comment in AsyncNotifier.

        """
        self.read_events()
        self.process_events()
        if self.handle_read_callback is not None:
            self.handle_read_callback(self)


class AsyncioNotifier(Notifier):
    """

    asyncio/trollius event loop adapter.

    """
    def __init__(self, watch_manager, loop, callback=None,
                 default_proc_fun=None, read_freq=0, threshold=0, timeout=None):
        """

        See examples/asyncio_notifier.py for an example usage.

        @param loop: asyncio or trollius event loop instance.
        @type loop: asyncio.BaseEventLoop or trollius.BaseEventLoop instance.
        @param callback: Functor called at the end of each call to handle_read.
                         Expects to receive the notifier object (self) as
                         single parameter.
        @type callback: callable object or function

        """
        self.loop = loop
        self.handle_read_callback = callback
        Notifier.__init__(self, watch_manager, default_proc_fun, read_freq,
                          threshold, timeout)
        loop.add_reader(self._fd, self.handle_read)

    def stop(self):
        self.loop.remove_reader(self._fd)
        Notifier.stop(self)

    def handle_read(self, *args, **kwargs):
        self.read_events()
        self.process_events()
        if self.handle_read_callback is not None:
            self.handle_read_callback(self)


class Watch:
    """
    Represent a watch, i.e. a file or directory being watched.

    """
    __slots__ = ('wd', 'path', 'mask', 'proc_fun', 'auto_add',
                 'exclude_filter', 'dir')

    def __init__(self, wd, path, mask, proc_fun, auto_add, exclude_filter):
        """
        Initializations.

        @param wd: Watch descriptor.
        @type wd: int
        @param path: Path of the file or directory being watched.
        @type path: str
        @param mask: Mask.
        @type mask: int
        @param proc_fun: Processing callable object.
        @type proc_fun:
        @param auto_add: Automatically add watches on new directories.
        @type auto_add: bool
        @param exclude_filter: Boolean function, used to exclude new
                               directories from being automatically watched.
                               See WatchManager.__init__
        @type exclude_filter: callable object
        """
        self.wd = wd
        self.path = path
        self.mask = mask
        self.proc_fun = proc_fun
        self.auto_add = auto_add
        self.exclude_filter = exclude_filter
        self.dir = os.path.isdir(self.path)

    def __repr__(self):
        """
        @return: String representation.
        @rtype: str
        """
        s = ' '.join(['%s%s%s' % (output_format.field_name(attr),
                                  output_format.punctuation('='),
                                  output_format.field_value(getattr(self,
                                                                    attr))) \
                      for attr in self.__slots__ if not attr.startswith('_')])

        s = '%s%s %s %s' % (output_format.punctuation('<'),
                            output_format.class_name(self.__class__.__name__),
                            s,
                            output_format.punctuation('>'))
        return s


class ExcludeFilter:
    """
    ExcludeFilter is an exclusion filter.

    """
    def __init__(self, arg_lst):
        """
        Examples:
          ef1 = ExcludeFilter(["/etc/rc.*", "/etc/hostname"])
          ef2 = ExcludeFilter("/my/path/exclude.lst")
          Where exclude.lst contains:
          /etc/rc.*
          /etc/hostname

        Note: it is not possible to exclude a file if its encapsulating
              directory is itself watched. See this issue for more details
              https://github.com/seb-m/pyinotify/issues/31

        @param arg_lst: is either a list of patterns or a filename from which
                        patterns will be loaded.
        @type arg_lst: list of str or str
        """
        if isinstance(arg_lst, str):
            lst = self._load_patterns_from_file(arg_lst)
        elif isinstance(arg_lst, list):
            lst = arg_lst
        else:
            raise TypeError

        self._lregex = []
        for regex in lst:
            self._lregex.append(re.compile(regex, re.UNICODE))

    def _load_patterns_from_file(self, filename):
        lst = []
        file_obj = file(filename, 'r')
        try:
            for line in file_obj.readlines():
                # Trim leading an trailing whitespaces
                pattern = line.strip()
                if not pattern or pattern.startswith('#'):
                    continue
                lst.append(pattern)
        finally:
            file_obj.close()
        return lst

    def _match(self, regex, path):
        return regex.match(path) is not None

    def __call__(self, path):
        """
        @param path: Path to match against provided regexps.
        @type path: str
        @return: Return True if path has been matched and should
                 be excluded, False otherwise.
        @rtype: bool
        """
        for regex in self._lregex:
            if self._match(regex, path):
                return True
        return False


class WatchManagerError(Exception):
    """
    WatchManager Exception. Raised on error encountered on watches
    operations.

    """
    def __init__(self, msg, wmd):
        """
        @param msg: Exception string's description.
        @type msg: string
        @param wmd: This dictionary contains the wd assigned to paths of the
                    same call for which watches were successfully added.
        @type wmd: dict
        """
        self.wmd = wmd
        Exception.__init__(self, msg)


class WatchManager:
    """
    Provide operations for watching files and directories. Its internal
    dictionary is used to reference watched items. When used inside
    threaded code, one must instanciate as many WatchManager instances as
    there are ThreadedNotifier instances.

    """
    def __init__(self, exclude_filter=lambda path: False):
        """
        Initialization: init inotify, init watch manager dictionary.
        Raise OSError if initialization fails, raise InotifyBindingNotFoundError
        if no inotify binding was found (through ctypes or from direct access to
        syscalls).

        @param exclude_filter: boolean function, returns True if current
                               path must be excluded from being watched.
                               Convenient for providing a common exclusion
                               filter for every call to add_watch.
        @type exclude_filter: callable object
        """
        self._ignore_events = False
        self._exclude_filter = exclude_filter
        self._wmd = {}  # watch dict key: watch descriptor, value: watch

        self._inotify_wrapper = INotifyWrapper.create()
        if self._inotify_wrapper is None:
            raise InotifyBindingNotFoundError()

        self._fd = self._inotify_wrapper.inotify_init() # file descriptor
        if self._fd < 0:
            err = 'Cannot initialize new instance of inotify, %s'
            raise OSError(err % self._inotify_wrapper.str_errno())

    def close(self):
        """
        Close inotify's file descriptor, this action will also automatically
        remove (i.e. stop watching) all its associated watch descriptors.
        After a call to this method the WatchManager's instance become useless
        and cannot be reused, a new instance must then be instanciated. It
        makes sense to call this method in few situations for instance if
        several independant WatchManager must be instanciated or if all watches
        must be removed and no other watches need to be added.
        """
        os.close(self._fd)

    def get_fd(self):
        """
        Return assigned inotify's file descriptor.

        @return: File descriptor.
        @rtype: int
        """
        return self._fd

    def get_watch(self, wd):
        """
        Get watch from provided watch descriptor wd.

        @param wd: Watch descriptor.
        @type wd: int
        """
        return self._wmd.get(wd)

    def del_watch(self, wd):
        """
        Remove watch entry associated to watch descriptor wd.

        @param wd: Watch descriptor.
        @type wd: int
        """
        try:
            del self._wmd[wd]
        except KeyError, err:
            log.error('Cannot delete unknown watch descriptor %s' % str(err))

    @property
    def watches(self):
        """
        Get a reference on the internal watch manager dictionary.

        @return: Internal watch manager dictionary.
        @rtype: dict
        """
        return self._wmd

    def __format_path(self, path):
        """
        Format path to its internal (stored in watch manager) representation.
        """
        # Unicode strings are converted back to strings, because it seems
        # that inotify_add_watch from ctypes does not work well when
        # it receives an ctypes.create_unicode_buffer instance as argument.
        # Therefore even wd are indexed with bytes string and not with
        # unicode paths.
        if isinstance(path, unicode):
            path = path.encode(sys.getfilesystemencoding())
        return os.path.normpath(path)

    def __add_watch(self, path, mask, proc_fun, auto_add, exclude_filter):
        """
        Add a watch on path, build a Watch object and insert it in the
        watch manager dictionary. Return the wd value.
        """
        path = self.__format_path(path)
        if auto_add and not mask & IN_CREATE:
            mask |= IN_CREATE
        wd = self._inotify_wrapper.inotify_add_watch(self._fd, path, mask)
        if wd < 0:
            return wd
        watch = Watch(wd=wd, path=path, mask=mask, proc_fun=proc_fun,
                      auto_add=auto_add, exclude_filter=exclude_filter)
        self._wmd[wd] = watch
        log.debug('New %s', watch)
        return wd

    def __glob(self, path, do_glob):
        if do_glob:
            return glob(path)
        else:
            return [path]

    def add_watch(self, path, mask, proc_fun=None, rec=False,
                  auto_add=False, do_glob=False, quiet=True,
                  exclude_filter=None):
        """
        Add watch(s) on the provided |path|(s) with associated |mask| flag
        value and optionally with a processing |proc_fun| function and
        recursive flag |rec| set to True.
        Ideally |path| components should not be unicode objects. Note that
        although unicode paths are accepted there are converted to byte
        strings before a watch is put on that path. The encoding used for
        converting the unicode object is given by sys.getfilesystemencoding().
        If |path| si already watched it is ignored, but if it is called with
        option rec=True a watch is put on each one of its not-watched
        subdirectory.

        @param path: Path to watch, the path can either be a file or a
                     directory. Also accepts a sequence (list) of paths.
        @type path: string or list of strings
        @param mask: Bitmask of events.
        @type mask: int
        @param proc_fun: Processing object.
        @type proc_fun: function or ProcessEvent instance or instance of
                        one of its subclasses or callable object.
        @param rec: Recursively add watches from path on all its
                    subdirectories, set to False by default (doesn't
                    follows symlinks in any case).
        @type rec: bool
        @param auto_add: Automatically add watches on newly created
                         directories in watched parent |path| directory.
                         If |auto_add| is True, IN_CREATE is ored with |mask|
                         when the watch is added.
        @type auto_add: bool
        @param do_glob: Do globbing on pathname (see standard globbing
                        module for more informations).
        @type do_glob: bool
        @param quiet: if False raises a WatchManagerError exception on
                      error. See example not_quiet.py.
        @type quiet: bool
        @param exclude_filter: predicate (boolean function), which returns
                               True if the current path must be excluded
                               from being watched. This argument has
                               precedence over exclude_filter passed to
                               the class' constructor.
        @type exclude_filter: callable object
        @return: dict of paths associated to watch descriptors. A wd value
                 is positive if the watch was added sucessfully,
                 otherwise the value is negative. If the path was invalid
                 or was already watched it is not included into this returned
                 dictionary.
        @rtype: dict of {str: int}
        """
        ret_ = {} # return {path: wd, ...}

        if exclude_filter is None:
            exclude_filter = self._exclude_filter

        # normalize args as list elements
        for npath in self.__format_param(path):
            # unix pathname pattern expansion
            for apath in self.__glob(npath, do_glob):
                # recursively list subdirs according to rec param
                for rpath in self.__walk_rec(apath, rec):
                    if not exclude_filter(rpath):
                        wd = ret_[rpath] = self.__add_watch(rpath, mask,
                                                            proc_fun,
                                                            auto_add,
                                                            exclude_filter)
                        if wd < 0:
                            err = ('add_watch: cannot watch %s WD=%d, %s' % \
                                       (rpath, wd,
                                        self._inotify_wrapper.str_errno()))
                            if quiet:
                                log.error(err)
                            else:
                                raise WatchManagerError(err, ret_)
                    else:
                        # Let's say -2 means 'explicitely excluded
                        # from watching'.
                        ret_[rpath] = -2
        return ret_

    def __get_sub_rec(self, lpath):
        """
        Get every wd from self._wmd if its path is under the path of
        one (at least) of those in lpath. Doesn't follow symlinks.

        @param lpath: list of watch descriptor
        @type lpath: list of int
        @return: list of watch descriptor
        @rtype: list of int
        """
        for d in lpath:
            root = self.get_path(d)
            if root is not None:
                # always keep root
                yield d
            else:
                # if invalid
                continue

            # nothing else to expect
            if not os.path.isdir(root):
                continue

            # normalization
            root = os.path.normpath(root)
            # recursion
            lend = len(root)
            for iwd in self._wmd.items():
                cur = iwd[1].path
                pref = os.path.commonprefix([root, cur])
                if root == os.sep or (len(pref) == lend and \
                                      len(cur) > lend and \
                                      cur[lend] == os.sep):
                    yield iwd[1].wd

    def update_watch(self, wd, mask=None, proc_fun=None, rec=False,
                     auto_add=False, quiet=True):
        """
        Update existing watch descriptors |wd|. The |mask| value, the
        processing object |proc_fun|, the recursive param |rec| and the
        |auto_add| and |quiet| flags can all be updated.

        @param wd: Watch Descriptor to update. Also accepts a list of
                   watch descriptors.
        @type wd: int or list of int
        @param mask: Optional new bitmask of events.
        @type mask: int
        @param proc_fun: Optional new processing function.
        @type proc_fun: function or ProcessEvent instance or instance of
                        one of its subclasses or callable object.
        @param rec: Optionally adds watches recursively on all
                    subdirectories contained into |wd| directory.
        @type rec: bool
        @param auto_add: Automatically adds watches on newly created
                         directories in the watch's path corresponding to |wd|.
                         If |auto_add| is True, IN_CREATE is ored with |mask|
                         when the watch is updated.
        @type auto_add: bool
        @param quiet: If False raises a WatchManagerError exception on
                      error. See example not_quiet.py
        @type quiet: bool
        @return: dict of watch descriptors associated to booleans values.
                 True if the corresponding wd has been successfully
                 updated, False otherwise.
        @rtype: dict of {int: bool}
        """
        lwd = self.__format_param(wd)
        if rec:
            lwd = self.__get_sub_rec(lwd)

        ret_ = {}  # return {wd: bool, ...}
        for awd in lwd:
            apath = self.get_path(awd)
            if not apath or awd < 0:
                err = 'update_watch: invalid WD=%d' % awd
                if quiet:
                    log.error(err)
                    continue
                raise WatchManagerError(err, ret_)

            if mask:
                wd_ = self._inotify_wrapper.inotify_add_watch(self._fd, apath,
                                                              mask)
                if wd_ < 0:
                    ret_[awd] = False
                    err = ('update_watch: cannot update %s WD=%d, %s' % \
                               (apath, wd_, self._inotify_wrapper.str_errno()))
                    if quiet:
                        log.error(err)
                        continue
                    raise WatchManagerError(err, ret_)

                assert(awd == wd_)

            if proc_fun or auto_add:
                watch_ = self._wmd[awd]

            if proc_fun:
                watch_.proc_fun = proc_fun

            if auto_add:
                watch_.auto_add = auto_add

            ret_[awd] = True
            log.debug('Updated watch - %s', self._wmd[awd])
        return ret_

    def __format_param(self, param):
        """
        @param param: Parameter.
        @type param: string or int
        @return: wrap param.
        @rtype: list of type(param)
        """
        if isinstance(param, list):
            for p_ in param:
                yield p_
        else:
            yield param

    def get_wd(self, path):
        """
        Returns the watch descriptor associated to path. This method
        presents a prohibitive cost, always prefer to keep the WD
        returned by add_watch(). If the path is unknown it returns None.

        @param path: Path.
        @type path: str
        @return: WD or None.
        @rtype: int or None
        """
        path = self.__format_path(path)
        for iwd in self._wmd.items():
            if iwd[1].path == path:
                return iwd[0]

    def get_path(self, wd):
        """
        Returns the path associated to WD, if WD is unknown it returns None.

        @param wd: Watch descriptor.
        @type wd: int
        @return: Path or None.
        @rtype: string or None
        """
        watch_ = self._wmd.get(wd)
        if watch_ is not None:
            return watch_.path

    def __walk_rec(self, top, rec):
        """
        Yields each subdirectories of top, doesn't follow symlinks.
        If rec is false, only yield top.

        @param top: root directory.
        @type top: string
        @param rec: recursive flag.
        @type rec: bool
        @return: path of one subdirectory.
        @rtype: string
        """
        if not rec or os.path.islink(top) or not os.path.isdir(top):
            yield top
        else:
            for root, dirs, files in os.walk(top):
                yield root

    def rm_watch(self, wd, rec=False, quiet=True):
        """
        Removes watch(s).

        @param wd: Watch Descriptor of the file or directory to unwatch.
                   Also accepts a list of WDs.
        @type wd: int or list of int.
        @param rec: Recursively removes watches on every already watched
                    subdirectories and subfiles.
        @type rec: bool
        @param quiet: If False raises a WatchManagerError exception on
                      error. See example not_quiet.py
        @type quiet: bool
        @return: dict of watch descriptors associated to booleans values.
                 True if the corresponding wd has been successfully
                 removed, False otherwise.
        @rtype: dict of {int: bool}
        """
        lwd = self.__format_param(wd)
        if rec:
            lwd = self.__get_sub_rec(lwd)

        ret_ = {}  # return {wd: bool, ...}
        for awd in lwd:
            # remove watch
            wd_ = self._inotify_wrapper.inotify_rm_watch(self._fd, awd)
            if wd_ < 0:
                ret_[awd] = False
                err = ('rm_watch: cannot remove WD=%d, %s' % \
                           (awd, self._inotify_wrapper.str_errno()))
                if quiet:
                    log.error(err)
                    continue
                raise WatchManagerError(err, ret_)

            # Remove watch from our dictionary
            if awd in self._wmd:
                del self._wmd[awd]
            ret_[awd] = True
            log.debug('Watch WD=%d (%s) removed', awd, self.get_path(awd))
        return ret_


    def watch_transient_file(self, filename, mask, proc_class):
        """
        Watch a transient file, which will be created and deleted frequently
        over time (e.g. pid file).

        @attention: Currently under the call to this function it is not
        possible to correctly watch the events triggered into the same
        base directory than the directory where is located this watched
        transient file. For instance it would be wrong to make these
        two successive calls: wm.watch_transient_file('/var/run/foo.pid', ...)
        and wm.add_watch('/var/run/', ...)

        @param filename: Filename.
        @type filename: string
        @param mask: Bitmask of events, should contain IN_CREATE and IN_DELETE.
        @type mask: int
        @param proc_class: ProcessEvent (or of one of its subclass), beware of
                           accepting a ProcessEvent's instance as argument into
                           __init__, see transient_file.py example for more
                           details.
        @type proc_class: ProcessEvent's instance or of one of its subclasses.
        @return: Same as add_watch().
        @rtype: Same as add_watch().
        """
        dirname = os.path.dirname(filename)
        if dirname == '':
            return {}  # Maintains coherence with add_watch()
        basename = os.path.basename(filename)
        # Assuming we are watching at least for IN_CREATE and IN_DELETE
        mask |= IN_CREATE | IN_DELETE

        def cmp_name(event):
            if getattr(event, 'name') is None:
                return False
            return basename == event.name
        return self.add_watch(dirname, mask,
                              proc_fun=proc_class(ChainIfTrue(func=cmp_name)),
                              rec=False,
                              auto_add=False, do_glob=False,
                              exclude_filter=lambda path: False)

    def get_ignore_events(self):
        return self._ignore_events

    def set_ignore_events(self, nval):
        self._ignore_events = nval

    ignore_events = property(get_ignore_events, set_ignore_events,
                             "Make watch manager ignoring new events.")



class RawOutputFormat:
    """
    Format string representations.
    """
    def __init__(self, format=None):
        self.format = format or {}

    def simple(self, s, attribute):
        if not isinstance(s, str):
            s = str(s)
        return (self.format.get(attribute, '') + s +
                self.format.get('normal', ''))

    def punctuation(self, s):
        """Punctuation color."""
        return self.simple(s, 'normal')

    def field_value(self, s):
        """Field value color."""
        return self.simple(s, 'purple')

    def field_name(self, s):
        """Field name color."""
        return self.simple(s, 'blue')

    def class_name(self, s):
        """Class name color."""
        return self.format.get('red', '') + self.simple(s, 'bold')

output_format = RawOutputFormat()

class ColoredOutputFormat(RawOutputFormat):
    """
    Format colored string representations.
    """
    def __init__(self):
        f = {'normal': '\033[0m',
             'black': '\033[30m',
             'red': '\033[31m',
             'green': '\033[32m',
             'yellow': '\033[33m',
             'blue': '\033[34m',
             'purple': '\033[35m',
             'cyan': '\033[36m',
             'bold': '\033[1m',
             'uline': '\033[4m',
             'blink': '\033[5m',
             'invert': '\033[7m'}
        RawOutputFormat.__init__(self, f)


def compatibility_mode():
    """
    Use this function to turn on the compatibility mode. The compatibility
    mode is used to improve compatibility with Pyinotify 0.7.1 (or older)
    programs. The compatibility mode provides additional variables 'is_dir',
    'event_name', 'EventsCodes.IN_*' and 'EventsCodes.ALL_EVENTS' as
    Pyinotify 0.7.1 provided. Do not call this function from new programs!!
    Especially if there are developped for Pyinotify >= 0.8.x.
    """
    setattr(EventsCodes, 'ALL_EVENTS', ALL_EVENTS)
    for evname in globals():
        if evname.startswith('IN_'):
            setattr(EventsCodes, evname, globals()[evname])
    global COMPATIBILITY_MODE
    COMPATIBILITY_MODE = True


def command_line():
    """
    By default the watched path is '/tmp' and all types of events are
    monitored. Events monitoring serves forever, type c^c to stop it.
    """
    from optparse import OptionParser

    usage = "usage: %prog [options] [path1] [path2] [pathn]"

    parser = OptionParser(usage=usage)
    parser.add_option("-v", "--verbose", action="store_true",
                      dest="verbose", help="Verbose mode")
    parser.add_option("-r", "--recursive", action="store_true",
                      dest="recursive",
                      help="Add watches recursively on paths")
    parser.add_option("-a", "--auto_add", action="store_true",
                      dest="auto_add",
                      help="Automatically add watches on new directories")
    parser.add_option("-g", "--glob", action="store_true",
                      dest="glob",
                      help="Treat paths as globs")
    parser.add_option("-e", "--events-list", metavar="EVENT[,...]",
                      dest="events_list",
                      help=("A comma-separated list of events to watch for - "
                           "see the documentation for valid options (defaults"
                           " to everything)"))
    parser.add_option("-s", "--stats", action="store_true",
                      dest="stats",
                      help="Display dummy statistics")
    parser.add_option("-V", "--version", action="store_true",
                      dest="version",  help="Pyinotify version")
    parser.add_option("-f", "--raw-format", action="store_true",
                      dest="raw_format",
                      help="Disable enhanced output format.")
    parser.add_option("-c", "--command", action="store",
                      dest="command",
                      help="Shell command to run upon event")

    (options, args) = parser.parse_args()

    if options.verbose:
        log.setLevel(10)

    if options.version:
        print(__version__)

    if not options.raw_format:
        global output_format
        output_format = ColoredOutputFormat()

    if len(args) < 1:
        path = '/tmp'  # default watched path
    else:
        path = args

    # watch manager instance
    wm = WatchManager()
    # notifier instance and init
    if options.stats:
        notifier = Notifier(wm, default_proc_fun=Stats(), read_freq=5)
    else:
        notifier = Notifier(wm, default_proc_fun=PrintAllEvents())

    # What mask to apply
    mask = 0
    if options.events_list:
        events_list = options.events_list.split(',')
        for ev in events_list:
            evcode = EventsCodes.ALL_FLAGS.get(ev, 0)
            if evcode:
                mask |= evcode
            else:
                parser.error("The event '%s' specified with option -e"
                             " is not valid" % ev)
    else:
        mask = ALL_EVENTS

    # stats
    cb_fun = None
    if options.stats:
        def cb(s):
            sys.stdout.write(repr(s.proc_fun()))
            sys.stdout.write('\n')
            sys.stdout.write(str(s.proc_fun()))
            sys.stdout.write('\n')
            sys.stdout.flush()
        cb_fun = cb

    # External command
    if options.command:
        def cb(s):
            subprocess.Popen(options.command, shell=True)
        cb_fun = cb

    log.debug('Start monitoring %s, (press c^c to halt pyinotify)' % path)

    wm.add_watch(path, mask, rec=options.recursive, auto_add=options.auto_add, do_glob=options.glob)
    # Loop forever (until sigint signal get caught)
    notifier.loop(callback=cb_fun)


if __name__ == '__main__':
    command_line()
