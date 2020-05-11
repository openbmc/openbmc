"""
BitBake 'Event' implementation

Classes and functions for manipulating 'events' in the
BitBake build tools.
"""

# Copyright (C) 2003, 2004  Chris Larson
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys
import pickle
import logging
import atexit
import traceback
import ast
import threading

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


class HeartbeatEvent(Event):
    """Triggered at regular time intervals of 10 seconds. Other events can fire much more often
       (runQueueTaskStarted when there are many short tasks) or not at all for long periods
       of time (again runQueueTaskStarted, when there is just one long-running task), so this
       event is more suitable for doing some task-independent work occassionally."""
    def __init__(self, time):
        Event.__init__(self)
        self.time = time

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
_thread_lock = threading.Lock()
_thread_lock_enabled = False

if hasattr(__builtins__, '__setitem__'):
    builtins = __builtins__
else:
    builtins = __builtins__.__dict__

def enable_threadlock():
    global _thread_lock_enabled
    _thread_lock_enabled = True

def disable_threadlock():
    global _thread_lock_enabled
    _thread_lock_enabled = False

def execute_handler(name, handler, event, d):
    event.data = d
    addedd = False
    if 'd' not in builtins:
        builtins['d'] = d
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
            del builtins['d']

def fire_class_handlers(event, d):
    if isinstance(event, logging.LogRecord):
        return

    eid = str(event.__class__)[8:-2]
    evt_hmap = _event_handler_map.get(eid, {})
    for name, handler in list(_handlers.items()):
        if name in _catchall_handlers or name in evt_hmap:
            if _eventfilter:
                if not _eventfilter(name, handler, event, d):
                    continue
            execute_handler(name, handler, event, d)

ui_queue = []
@atexit.register
def print_ui_queue():
    global ui_queue
    """If we're exiting before a UI has been spawned, display any queued
    LogRecords to the console."""
    logger = logging.getLogger("BitBake")
    if not _uiready:
        from bb.msg import BBLogFormatter
        # Flush any existing buffered content
        sys.stdout.flush()
        sys.stderr.flush()
        stdout = logging.StreamHandler(sys.stdout)
        stderr = logging.StreamHandler(sys.stderr)
        formatter = BBLogFormatter("%(levelname)s: %(message)s")
        stdout.setFormatter(formatter)
        stderr.setFormatter(formatter)

        # First check to see if we have any proper messages
        msgprint = False
        msgerrs = False

        # Should we print to stderr?
        for event in ui_queue[:]:
            if isinstance(event, logging.LogRecord) and event.levelno >= logging.WARNING:
                msgerrs = True
                break

        if msgerrs:
            logger.addHandler(stderr)
        else:
            logger.addHandler(stdout)

        for event in ui_queue[:]:
            if isinstance(event, logging.LogRecord):
                if event.levelno > logging.DEBUG:
                    logger.handle(event)
                    msgprint = True

        # Nope, so just print all of the messages we have (including debug messages)
        if not msgprint:
            for event in ui_queue[:]:
                if isinstance(event, logging.LogRecord):
                    logger.handle(event)
        if msgerrs:
            logger.removeHandler(stderr)
        else:
            logger.removeHandler(stdout)
        ui_queue = []

def fire_ui_handlers(event, d):
    global _thread_lock
    global _thread_lock_enabled

    if not _uiready:
        # No UI handlers registered yet, queue up the messages
        ui_queue.append(event)
        return

    if _thread_lock_enabled:
        _thread_lock.acquire()

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

    if _thread_lock_enabled:
        _thread_lock.release()

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
        # If messages have been queued up, clear the queue
        global _uiready, ui_queue
        if _uiready and ui_queue:
            for queue_event in ui_queue:
                fire_ui_handlers(queue_event, d)
            ui_queue = []
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
        if isinstance(handler, str):
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
    if name in _catchall_handlers:
        _catchall_handlers.pop(name)
    for event in _event_handler_map.keys():
        if name in _event_handler_map[event]:
            _event_handler_map[event].pop(name)

def get_handlers():
    return _handlers

def set_handlers(handlers):
    global _handlers
    _handlers = handlers

def set_eventfilter(func):
    global _eventfilter
    _eventfilter = func

def register_UIHhandler(handler, mainui=False):
    bb.event._ui_handler_seq = bb.event._ui_handler_seq + 1
    _ui_handlers[_ui_handler_seq] = handler
    level, debug_domains = bb.msg.constructLogOptions()
    _ui_logfilters[_ui_handler_seq] = UIEventFilter(level, debug_domains)
    if mainui:
        global _uiready
        _uiready = _ui_handler_seq
    return _ui_handler_seq

def unregister_UIHhandler(handlerNum, mainui=False):
    if mainui:
        global _uiready
        _uiready = False
    if handlerNum in _ui_handlers:
        del _ui_handlers[handlerNum]
    return

def get_uihandler():
    if _uiready is False:
        return None
    return _uiready

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
    if getattr(e, "__name__", None) is None:
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

class MultiConfigParsed(Event):
    """Multi-Config Parsing Complete"""
    def __init__(self, mcdata):
        self.mcdata = mcdata
        Event.__init__(self)

class RecipeEvent(Event):
    def __init__(self, fn):
        self.fn = fn
        Event.__init__(self)

class RecipePreFinalise(RecipeEvent):
    """ Recipe Parsing Complete but not yet finalised"""

class RecipeTaskPreProcess(RecipeEvent):
    """
    Recipe Tasks about to be finalised
    The list of tasks should be final at this point and handlers
    are only able to change interdependencies
    """
    def __init__(self, fn, tasklist):
        self.fn = fn
        self.tasklist = tasklist
        Event.__init__(self)

class RecipeParsed(RecipeEvent):
    """ Recipe Parsing Complete """

class BuildBase(Event):
    """Base class for bitbake build events"""

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

    def getFailures(self):
        """
        Return the number of failed packages
        """
        return self._failures

    pkgs = property(getPkgs, setPkgs, None, "pkgs property")
    name = property(getName, setName, None, "name property")

class BuildInit(BuildBase):
    """buildFile or buildTargets was invoked"""
    def __init__(self, p=[]):
        name = None
        BuildBase.__init__(self, name, p)

class BuildStarted(BuildBase, OperationStarted):
    """Event when builds start"""
    def __init__(self, n, p, failures = 0):
        OperationStarted.__init__(self, "Building Started")
        BuildBase.__init__(self, n, p, failures)

class BuildCompleted(BuildBase, OperationCompleted):
    """Event when builds have completed"""
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

class DiskUsageSample:
    def __init__(self, available_bytes, free_bytes, total_bytes):
        # Number of bytes available to non-root processes.
        self.available_bytes = available_bytes
        # Number of bytes available to root processes.
        self.free_bytes = free_bytes
        # Total capacity of the volume.
        self.total_bytes = total_bytes

class MonitorDiskEvent(Event):
    """If BB_DISKMON_DIRS is set, then this event gets triggered each time disk space is checked.
       Provides information about devices that are getting monitored."""
    def __init__(self, disk_usage):
        Event.__init__(self)
        # hash of device root path -> DiskUsageSample
        self.disk_usage = disk_usage

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

    def __str__(self):
        msg = ''
        if self._runtime:
            r = "R"
        else:
            r = ""

        extra = ''
        if not self._reasons:
            if self._close_matches:
                extra = ". Close matches:\n  %s" % '\n  '.join(sorted(set(self._close_matches)))

        if self._dependees:
            msg = "Nothing %sPROVIDES '%s' (but %s %sDEPENDS on or otherwise requires it)%s" % (r, self._item, ", ".join(self._dependees), r, extra)
        else:
            msg = "Nothing %sPROVIDES '%s'%s" % (r, self._item, extra)
        if self._reasons:
            for reason in self._reasons:
                msg += '\n' + reason
        return msg


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

    def __str__(self):
        msg = "Multiple providers are available for %s%s (%s)" % (self._is_runtime and "runtime " or "",
                            self._item,
                            ", ".join(self._candidates))
        rtime = ""
        if self._is_runtime:
            rtime = "R"
        msg += "\nConsider defining a PREFERRED_%sPROVIDER entry to match %s" % (rtime, self._item)
        return msg

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
            record.bb_exc_info = (etype, value, tb)
            record.bb_exc_formatted = bb.exceptions.format_exception(etype, value, tb, limit=5)
            value = str(value)
            record.exc_info = None
        fire(record, None)

    def filter(self, record):
        record.taskpid = worker_pid
        return True

class MetadataEvent(Event):
    """
    Generic event that target for OE-Core classes
    to report information during asynchrous execution
    """
    def __init__(self, eventtype, eventdata):
        Event.__init__(self)
        self.type = eventtype
        self._localdata = eventdata

class ProcessStarted(Event):
    """
    Generic process started event (usually part of the initial startup)
    where further progress events will be delivered
    """
    def __init__(self, processname, total):
        Event.__init__(self)
        self.processname = processname
        self.total = total

class ProcessProgress(Event):
    """
    Generic process progress event (usually part of the initial startup)
    """
    def __init__(self, processname, progress):
        Event.__init__(self)
        self.processname = processname
        self.progress = progress

class ProcessFinished(Event):
    """
    Generic process finished event (usually part of the initial startup)
    """
    def __init__(self, processname):
        Event.__init__(self)
        self.processname = processname

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

class FindSigInfoResult(Event):
    """
    Event to return results from findSigInfo command
    """
    def __init__(self, result):
        Event.__init__(self)
        self.result = result
