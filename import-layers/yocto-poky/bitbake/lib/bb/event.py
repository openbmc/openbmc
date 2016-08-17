# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'Event' implementation

Classes and functions for manipulating 'events' in the
BitBake build tools.
"""

# Copyright (C) 2003, 2004  Chris Larson
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

import os, sys
import warnings
try:
    import cPickle as pickle
except ImportError:
    import pickle
import logging
import atexit
import traceback
import ast
import bb.utils
import bb.compat
import bb.exceptions

# This is the pid for which we should generate the event. This is set when
# the runqueue forks off.
worker_pid = 0
worker_fire = None

logger = logging.getLogger('BitBake.Event')

class Event(object):
    """Base class for events"""

    def __init__(self):
        self.pid = worker_pid

Registered        = 10
AlreadyRegistered = 14

def get_class_handlers():
    return _handlers

def set_class_handlers(h):
    global _handlers
    _handlers = h

def clean_class_handlers():
    return bb.compat.OrderedDict()

# Internal
_handlers = clean_class_handlers()
_ui_handlers = {}
_ui_logfilters = {}
_ui_handler_seq = 0
_event_handler_map = {}
_catchall_handlers = {}
_eventfilter = None
_uiready = False

def execute_handler(name, handler, event, d):
    event.data = d
    addedd = False
    if 'd' not in __builtins__:
        __builtins__['d'] = d
        addedd = True
    try:
        ret = handler(event)
    except (bb.parse.SkipRecipe, bb.BBHandledException):
        raise
    except Exception:
        etype, value, tb = sys.exc_info()
        logger.error("Execution of event handler '%s' failed" % name,
                        exc_info=(etype, value, tb.tb_next))
        raise
    except SystemExit as exc:
        if exc.code != 0:
            logger.error("Execution of event handler '%s' failed" % name)
        raise
    finally:
        del event.data
        if addedd:
            del __builtins__['d']

def fire_class_handlers(event, d):
    if isinstance(event, logging.LogRecord):
        return

    eid = str(event.__class__)[8:-2]
    evt_hmap = _event_handler_map.get(eid, {})
    for name, handler in _handlers.iteritems():
        if name in _catchall_handlers or name in evt_hmap:
            if _eventfilter:
                if not _eventfilter(name, handler, event, d):
                    continue
            execute_handler(name, handler, event, d)

ui_queue = []
@atexit.register
def print_ui_queue():
    """If we're exiting before a UI has been spawned, display any queued
    LogRecords to the console."""
    logger = logging.getLogger("BitBake")
    if not _uiready:
        from bb.msg import BBLogFormatter
        console = logging.StreamHandler(sys.stdout)
        console.setFormatter(BBLogFormatter("%(levelname)s: %(message)s"))
        logger.handlers = [console]

        # First check to see if we have any proper messages
        msgprint = False
        for event in ui_queue:
            if isinstance(event, logging.LogRecord):
                if event.levelno > logging.DEBUG:
                    logger.handle(event)
                    msgprint = True
        if msgprint:
            return

        # Nope, so just print all of the messages we have (including debug messages)
        for event in ui_queue:
            if isinstance(event, logging.LogRecord):
                logger.handle(event)

def fire_ui_handlers(event, d):
    if not _uiready:
        # No UI handlers registered yet, queue up the messages
        ui_queue.append(event)
        return

    errors = []
    for h in _ui_handlers:
        #print "Sending event %s" % event
        try:
             if not _ui_logfilters[h].filter(event):
                 continue
             # We use pickle here since it better handles object instances
             # which xmlrpc's marshaller does not. Events *must* be serializable
             # by pickle.
             if hasattr(_ui_handlers[h].event, "sendpickle"):
                _ui_handlers[h].event.sendpickle((pickle.dumps(event)))
             else:
                _ui_handlers[h].event.send(event)
        except:
            errors.append(h)
    for h in errors:
        del _ui_handlers[h]

def fire(event, d):
    """Fire off an Event"""

    # We can fire class handlers in the worker process context and this is
    # desired so they get the task based datastore.
    # UI handlers need to be fired in the server context so we defer this. They
    # don't have a datastore so the datastore context isn't a problem.

    fire_class_handlers(event, d)
    if worker_fire:
        worker_fire(event, d)
    else:
        fire_ui_handlers(event, d)

def fire_from_worker(event, d):
    fire_ui_handlers(event, d)

noop = lambda _: None
def register(name, handler, mask=None, filename=None, lineno=None):
    """Register an Event handler"""

    # already registered
    if name in _handlers:
        return AlreadyRegistered

    if handler is not None:
        # handle string containing python code
        if isinstance(handler, basestring):
            tmp = "def %s(e):\n%s" % (name, handler)
            try:
                code = bb.methodpool.compile_cache(tmp)
                if not code:
                    if filename is None:
                        filename = "%s(e)" % name
                    code = compile(tmp, filename, "exec", ast.PyCF_ONLY_AST)
                    if lineno is not None:
                        ast.increment_lineno(code, lineno-1)
                    code = compile(code, filename, "exec")
                    bb.methodpool.compile_cache_add(tmp, code)
            except SyntaxError:
                logger.error("Unable to register event handler '%s':\n%s", name,
                             ''.join(traceback.format_exc(limit=0)))
                _handlers[name] = noop
                return
            env = {}
            bb.utils.better_exec(code, env)
            func = bb.utils.better_eval(name, env)
            _handlers[name] = func
        else:
            _handlers[name] = handler

        if not mask or '*' in mask:
            _catchall_handlers[name] = True
        else:
            for m in mask:
                if _event_handler_map.get(m, None) is None:
                    _event_handler_map[m] = {}
                _event_handler_map[m][name] = True

        return Registered

def remove(name, handler):
    """Remove an Event handler"""
    _handlers.pop(name)

def set_eventfilter(func):
    global _eventfilter
    _eventfilter = func

def register_UIHhandler(handler, mainui=False):
    if mainui:
        global _uiready
        _uiready = True
    bb.event._ui_handler_seq = bb.event._ui_handler_seq + 1
    _ui_handlers[_ui_handler_seq] = handler
    level, debug_domains = bb.msg.constructLogOptions()
    _ui_logfilters[_ui_handler_seq] = UIEventFilter(level, debug_domains)
    return _ui_handler_seq

def unregister_UIHhandler(handlerNum):
    if handlerNum in _ui_handlers:
        del _ui_handlers[handlerNum]
    return

# Class to allow filtering of events and specific filtering of LogRecords *before* we put them over the IPC
class UIEventFilter(object):
    def __init__(self, level, debug_domains):
        self.update(None, level, debug_domains)

    def update(self, eventmask, level, debug_domains):
        self.eventmask = eventmask
        self.stdlevel = level
        self.debug_domains = debug_domains

    def filter(self, event):
        if isinstance(event, logging.LogRecord):
            if event.levelno >= self.stdlevel:
                return True
            if event.name in self.debug_domains and event.levelno >= self.debug_domains[event.name]:
                return True
            return False
        eid = str(event.__class__)[8:-2]
        if self.eventmask and eid not in self.eventmask:
            return False
        return True

def set_UIHmask(handlerNum, level, debug_domains, mask):
    if not handlerNum in _ui_handlers:
        return False
    if '*' in mask:
        _ui_logfilters[handlerNum].update(None, level, debug_domains)
    else:
        _ui_logfilters[handlerNum].update(mask, level, debug_domains)
    return True

def getName(e):
    """Returns the name of a class or class instance"""
    if getattr(e, "__name__", None) == None:
        return e.__class__.__name__
    else:
        return e.__name__

class OperationStarted(Event):
    """An operation has begun"""
    def __init__(self, msg = "Operation Started"):
        Event.__init__(self)
        self.msg = msg

class OperationCompleted(Event):
    """An operation has completed"""
    def __init__(self, total, msg = "Operation Completed"):
        Event.__init__(self)
        self.total = total
        self.msg = msg

class OperationProgress(Event):
    """An operation is in progress"""
    def __init__(self, current, total, msg = "Operation in Progress"):
        Event.__init__(self)
        self.current = current
        self.total = total
        self.msg = msg + ": %s/%s" % (current, total);

class ConfigParsed(Event):
    """Configuration Parsing Complete"""

class RecipeEvent(Event):
    def __init__(self, fn):
        self.fn = fn
        Event.__init__(self)

class RecipePreFinalise(RecipeEvent):
    """ Recipe Parsing Complete but not yet finialised"""

class RecipeParsed(RecipeEvent):
    """ Recipe Parsing Complete """

class StampUpdate(Event):
    """Trigger for any adjustment of the stamp files to happen"""

    def __init__(self, targets, stampfns):
        self._targets = targets
        self._stampfns = stampfns
        Event.__init__(self)

    def getStampPrefix(self):
        return self._stampfns

    def getTargets(self):
        return self._targets

    stampPrefix = property(getStampPrefix)
    targets = property(getTargets)

class BuildBase(Event):
    """Base class for bbmake run events"""

    def __init__(self, n, p, failures = 0):
        self._name = n
        self._pkgs = p
        Event.__init__(self)
        self._failures = failures

    def getPkgs(self):
        return self._pkgs

    def setPkgs(self, pkgs):
        self._pkgs = pkgs

    def getName(self):
        return self._name

    def setName(self, name):
        self._name = name

    def getCfg(self):
        return self.data

    def setCfg(self, cfg):
        self.data = cfg

    def getFailures(self):
        """
        Return the number of failed packages
        """
        return self._failures

    pkgs = property(getPkgs, setPkgs, None, "pkgs property")
    name = property(getName, setName, None, "name property")
    cfg = property(getCfg, setCfg, None, "cfg property")





class BuildStarted(BuildBase, OperationStarted):
    """bbmake build run started"""
    def __init__(self, n, p, failures = 0):
        OperationStarted.__init__(self, "Building Started")
        BuildBase.__init__(self, n, p, failures)

class BuildCompleted(BuildBase, OperationCompleted):
    """bbmake build run completed"""
    def __init__(self, total, n, p, failures=0, interrupted=0):
        if not failures:
            OperationCompleted.__init__(self, total, "Building Succeeded")
        else:
            OperationCompleted.__init__(self, total, "Building Failed")
        self._interrupted = interrupted
        BuildBase.__init__(self, n, p, failures)

class DiskFull(Event):
    """Disk full case build aborted"""
    def __init__(self, dev, type, freespace, mountpoint):
        Event.__init__(self)
        self._dev = dev
        self._type = type
        self._free = freespace
        self._mountpoint = mountpoint

class NoProvider(Event):
    """No Provider for an Event"""

    def __init__(self, item, runtime=False, dependees=None, reasons=None, close_matches=None):
        Event.__init__(self)
        self._item = item
        self._runtime = runtime
        self._dependees = dependees
        self._reasons = reasons
        self._close_matches = close_matches

    def getItem(self):
        return self._item

    def isRuntime(self):
        return self._runtime

class MultipleProviders(Event):
    """Multiple Providers"""

    def  __init__(self, item, candidates, runtime = False):
        Event.__init__(self)
        self._item = item
        self._candidates = candidates
        self._is_runtime = runtime

    def isRuntime(self):
        """
        Is this a runtime issue?
        """
        return self._is_runtime

    def getItem(self):
        """
        The name for the to be build item
        """
        return self._item

    def getCandidates(self):
        """
        Get the possible Candidates for a PROVIDER.
        """
        return self._candidates

class ParseStarted(OperationStarted):
    """Recipe parsing for the runqueue has begun"""
    def __init__(self, total):
        OperationStarted.__init__(self, "Recipe parsing Started")
        self.total = total

class ParseCompleted(OperationCompleted):
    """Recipe parsing for the runqueue has completed"""
    def __init__(self, cached, parsed, skipped, masked, virtuals, errors, total):
        OperationCompleted.__init__(self, total, "Recipe parsing Completed")
        self.cached = cached
        self.parsed = parsed
        self.skipped = skipped
        self.virtuals = virtuals
        self.masked = masked
        self.errors = errors
        self.sofar = cached + parsed

class ParseProgress(OperationProgress):
    """Recipe parsing progress"""
    def __init__(self, current, total):
        OperationProgress.__init__(self, current, total, "Recipe parsing")


class CacheLoadStarted(OperationStarted):
    """Loading of the dependency cache has begun"""
    def __init__(self, total):
        OperationStarted.__init__(self, "Loading cache Started")
        self.total = total

class CacheLoadProgress(OperationProgress):
    """Cache loading progress"""
    def __init__(self, current, total):
        OperationProgress.__init__(self, current, total, "Loading cache")

class CacheLoadCompleted(OperationCompleted):
    """Cache loading is complete"""
    def __init__(self, total, num_entries):
        OperationCompleted.__init__(self, total, "Loading cache Completed")
        self.num_entries = num_entries

class TreeDataPreparationStarted(OperationStarted):
    """Tree data preparation started"""
    def __init__(self):
        OperationStarted.__init__(self, "Preparing tree data Started")

class TreeDataPreparationProgress(OperationProgress):
    """Tree data preparation is in progress"""
    def __init__(self, current, total):
        OperationProgress.__init__(self, current, total, "Preparing tree data")

class TreeDataPreparationCompleted(OperationCompleted):
    """Tree data preparation completed"""
    def __init__(self, total):
        OperationCompleted.__init__(self, total, "Preparing tree data Completed")

class DepTreeGenerated(Event):
    """
    Event when a dependency tree has been generated
    """

    def __init__(self, depgraph):
        Event.__init__(self)
        self._depgraph = depgraph

class TargetsTreeGenerated(Event):
    """
    Event when a set of buildable targets has been generated
    """
    def __init__(self, model):
        Event.__init__(self)
        self._model = model

class ReachableStamps(Event):
    """
    An event listing all stamps reachable after parsing
    which the metadata may use to clean up stale data
    """

    def __init__(self, stamps):
        Event.__init__(self)
        self.stamps = stamps

class FilesMatchingFound(Event):
    """
    Event when a list of files matching the supplied pattern has
    been generated
    """
    def __init__(self, pattern, matches):
        Event.__init__(self)
        self._pattern = pattern
        self._matches = matches

class CoreBaseFilesFound(Event):
    """
    Event when a list of appropriate config files has been generated
    """
    def __init__(self, paths):
        Event.__init__(self)
        self._paths = paths

class ConfigFilesFound(Event):
    """
    Event when a list of appropriate config files has been generated
    """
    def __init__(self, variable, values):
        Event.__init__(self)
        self._variable = variable
        self._values = values

class ConfigFilePathFound(Event):
    """
    Event when a path for a config file has been found
    """
    def __init__(self, path):
        Event.__init__(self)
        self._path = path

class MsgBase(Event):
    """Base class for messages"""

    def __init__(self, msg):
        self._message = msg
        Event.__init__(self)

class MsgDebug(MsgBase):
    """Debug Message"""

class MsgNote(MsgBase):
    """Note Message"""

class MsgWarn(MsgBase):
    """Warning Message"""

class MsgError(MsgBase):
    """Error Message"""

class MsgFatal(MsgBase):
    """Fatal Message"""

class MsgPlain(MsgBase):
    """General output"""

class LogExecTTY(Event):
    """Send event containing program to spawn on tty of the logger"""
    def __init__(self, msg, prog, sleep_delay, retries):
        Event.__init__(self)
        self.msg = msg
        self.prog = prog
        self.sleep_delay = sleep_delay
        self.retries = retries

class LogHandler(logging.Handler):
    """Dispatch logging messages as bitbake events"""

    def emit(self, record):
        if record.exc_info:
            etype, value, tb = record.exc_info
            if hasattr(tb, 'tb_next'):
                tb = list(bb.exceptions.extract_traceback(tb, context=3))
            # Need to turn the value into something the logging system can pickle
            value = str(value)
            record.bb_exc_info = (etype, value, tb)
            record.exc_info = None
        fire(record, None)

    def filter(self, record):
        record.taskpid = worker_pid
        return True

class RequestPackageInfo(Event):
    """
    Event to request package information
    """

class PackageInfo(Event):
    """
    Package information for GUI
    """
    def __init__(self, pkginfolist):
        Event.__init__(self)
        self._pkginfolist = pkginfolist

class MetadataEvent(Event):
    """
    Generic event that target for OE-Core classes
    to report information during asynchrous execution
    """
    def __init__(self, eventtype, eventdata):
        Event.__init__(self)
        self.type = eventtype
        self._localdata = eventdata

class SanityCheck(Event):
    """
    Event to run sanity checks, either raise errors or generate events as return status.
    """
    def __init__(self, generateevents = True):
        Event.__init__(self)
        self.generateevents = generateevents

class SanityCheckPassed(Event):
    """
    Event to indicate sanity check has passed
    """

class SanityCheckFailed(Event):
    """
    Event to indicate sanity check has failed
    """
    def __init__(self, msg, network_error=False):
        Event.__init__(self)
        self._msg = msg
        self._network_error = network_error

class NetworkTest(Event):
    """
    Event to run network connectivity tests, either raise errors or generate events as return status.
    """
    def __init__(self, generateevents = True):
        Event.__init__(self)
        self.generateevents = generateevents

class NetworkTestPassed(Event):
    """
    Event to indicate network test has passed
    """

class NetworkTestFailed(Event):
    """
    Event to indicate network test has failed
    """

