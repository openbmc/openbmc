#
# BitBake 'Build' implementation
#
# Core code for function execution and task handling in the
# BitBake build tools.
#
# Copyright (C) 2003, 2004  Chris Larson
#
# Based on Gentoo's portage.py.
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import sys
import logging
import glob
import itertools
import time
import re
import stat
import bb
import bb.msg
import bb.process
import bb.progress
from bb import data, event, utils

bblogger = logging.getLogger('BitBake')
logger = logging.getLogger('BitBake.Build')

verboseShellLogging = False
verboseStdoutLogging = False

__mtime_cache = {}

def cached_mtime_noerror(f):
    if f not in __mtime_cache:
        try:
            __mtime_cache[f] = os.stat(f)[stat.ST_MTIME]
        except OSError:
            return 0
    return __mtime_cache[f]

def reset_cache():
    global __mtime_cache
    __mtime_cache = {}

# When we execute a Python function, we'd like certain things
# in all namespaces, hence we add them to __builtins__.
# If we do not do this and use the exec globals, they will
# not be available to subfunctions.
if hasattr(__builtins__, '__setitem__'):
    builtins = __builtins__
else:
    builtins = __builtins__.__dict__

builtins['bb'] = bb
builtins['os'] = os

class TaskBase(event.Event):
    """Base class for task events"""

    def __init__(self, t, fn, logfile, d):
        self._task = t
        self._fn = fn
        self._package = d.getVar("PF")
        self._mc = d.getVar("BB_CURRENT_MC")
        self.taskfile = d.getVar("FILE")
        self.taskname = self._task
        self.logfile = logfile
        self.time = time.time()
        self.pn = d.getVar("PN")
        self.pv = d.getVar("PV")
        event.Event.__init__(self)
        self._message = "recipe %s: task %s: %s" % (d.getVar("PF"), t, self.getDisplayName())

    def getTask(self):
        return self._task

    def setTask(self, task):
        self._task = task

    def getDisplayName(self):
        return bb.event.getName(self)[4:]

    task = property(getTask, setTask, None, "task property")

class TaskStarted(TaskBase):
    """Task execution started"""
    def __init__(self, t, fn, logfile, taskflags, d):
        super(TaskStarted, self).__init__(t, fn, logfile, d)
        self.taskflags = taskflags

class TaskSucceeded(TaskBase):
    """Task execution completed"""

class TaskFailed(TaskBase):
    """Task execution failed"""

    def __init__(self, task, fn, logfile, metadata, errprinted = False):
        self.errprinted = errprinted
        super(TaskFailed, self).__init__(task, fn, logfile, metadata)

class TaskFailedSilent(TaskBase):
    """Task execution failed (silently)"""
    def getDisplayName(self):
        # Don't need to tell the user it was silent
        return "Failed"

class TaskInvalid(TaskBase):

    def __init__(self, task, fn, metadata):
        super(TaskInvalid, self).__init__(task, fn, None, metadata)
        self._message = "No such task '%s'" % task

class TaskProgress(event.Event):
    """
    Task made some progress that could be reported to the user, usually in
    the form of a progress bar or similar.
    NOTE: this class does not inherit from TaskBase since it doesn't need
    to - it's fired within the task context itself, so we don't have any of
    the context information that you do in the case of the other events.
    The event PID can be used to determine which task it came from.
    The progress value is normally 0-100, but can also be negative
    indicating that progress has been made but we aren't able to determine
    how much.
    The rate is optional, this is simply an extra string to display to the
    user if specified.
    """
    def __init__(self, progress, rate=None):
        self.progress = progress
        self.rate = rate
        event.Event.__init__(self)


class LogTee(object):
    def __init__(self, logger, outfile):
        self.outfile = outfile
        self.logger = logger
        self.name = self.outfile.name

    def write(self, string):
        self.logger.plain(string)
        self.outfile.write(string)

    def __enter__(self):
        self.outfile.__enter__()
        return self

    def __exit__(self, *excinfo):
        self.outfile.__exit__(*excinfo)

    def __repr__(self):
        return '<LogTee {0}>'.format(self.name)

    def flush(self):
        self.outfile.flush()


class StdoutNoopContextManager:
    """
    This class acts like sys.stdout, but adds noop __enter__ and __exit__ methods.
    """
    def __enter__(self):
        return sys.stdout

    def __exit__(self, *exc_info):
        pass

    def write(self, string):
        return sys.stdout.write(string)

    def flush(self):
        sys.stdout.flush()

    @property
    def name(self):
        return sys.stdout.name


def exec_func(func, d, dirs = None):
    """Execute a BB 'function'"""

    try:
        oldcwd = os.getcwd()
    except:
        oldcwd = None

    flags = d.getVarFlags(func)
    cleandirs = flags.get('cleandirs') if flags else None
    if cleandirs:
        for cdir in d.expand(cleandirs).split():
            bb.utils.remove(cdir, True)
            bb.utils.mkdirhier(cdir)

    if flags and dirs is None:
        dirs = flags.get('dirs')
        if dirs:
            dirs = d.expand(dirs).split()

    if dirs:
        for adir in dirs:
            bb.utils.mkdirhier(adir)
        adir = dirs[-1]
    else:
        adir = None

    body = d.getVar(func, False)
    if not body:
        if body is None:
            logger.warning("Function %s doesn't exist", func)
        return

    ispython = flags.get('python')

    lockflag = flags.get('lockfiles')
    if lockflag:
        lockfiles = [f for f in d.expand(lockflag).split()]
    else:
        lockfiles = None

    tempdir = d.getVar('T')

    # or func allows items to be executed outside of the normal
    # task set, such as buildhistory
    task = d.getVar('BB_RUNTASK') or func
    if task == func:
        taskfunc = task
    else:
        taskfunc = "%s.%s" % (task, func)

    runfmt = d.getVar('BB_RUNFMT') or "run.{func}.{pid}"
    runfn = runfmt.format(taskfunc=taskfunc, task=task, func=func, pid=os.getpid())
    runfile = os.path.join(tempdir, runfn)
    bb.utils.mkdirhier(os.path.dirname(runfile))

    # Setup the courtesy link to the runfn, only for tasks
    # we create the link 'just' before the run script is created
    # if we create it after, and if the run script fails, then the
    # link won't be created as an exception would be fired.
    if task == func:
        runlink = os.path.join(tempdir, 'run.{0}'.format(task))
        if runlink:
            bb.utils.remove(runlink)

            try:
                os.symlink(runfn, runlink)
            except OSError:
                pass

    with bb.utils.fileslocked(lockfiles):
        if ispython:
            exec_func_python(func, d, runfile, cwd=adir)
        else:
            exec_func_shell(func, d, runfile, cwd=adir)

    try:
        curcwd = os.getcwd()
    except:
        curcwd = None

    if oldcwd and curcwd != oldcwd:
        try:
            bb.warn("Task %s changed cwd to %s" % (func, curcwd))
            os.chdir(oldcwd)
        except:
            pass

_functionfmt = """
{function}(d)
"""
logformatter = bb.msg.BBLogFormatter("%(levelname)s: %(message)s")
def exec_func_python(func, d, runfile, cwd=None):
    """Execute a python BB 'function'"""

    code = _functionfmt.format(function=func)
    bb.utils.mkdirhier(os.path.dirname(runfile))
    with open(runfile, 'w') as script:
        bb.data.emit_func_python(func, script, d)

    if cwd:
        try:
            olddir = os.getcwd()
        except OSError as e:
            bb.warn("%s: Cannot get cwd: %s" % (func, e))
            olddir = None
        os.chdir(cwd)

    bb.debug(2, "Executing python function %s" % func)

    try:
        text = "def %s(d):\n%s" % (func, d.getVar(func, False))
        fn = d.getVarFlag(func, "filename", False)
        lineno = int(d.getVarFlag(func, "lineno", False))
        bb.methodpool.insert_method(func, text, fn, lineno - 1)

        comp = utils.better_compile(code, func, "exec_func_python() autogenerated")
        utils.better_exec(comp, {"d": d}, code, "exec_func_python() autogenerated")
    finally:
        # We want any stdout/stderr to be printed before any other log messages to make debugging
        # more accurate. In some cases we seem to lose stdout/stderr entirely in logging tests without this.
        sys.stdout.flush()
        sys.stderr.flush()
        bb.debug(2, "Python function %s finished" % func)

        if cwd and olddir:
            try:
                os.chdir(olddir)
            except OSError as e:
                bb.warn("%s: Cannot restore cwd %s: %s" % (func, olddir, e))

def shell_trap_code():
    return '''#!/bin/sh\n
__BITBAKE_LAST_LINE=0

# Emit a useful diagnostic if something fails:
bb_sh_exit_handler() {
    ret=$?
    if [ "$ret" != 0 ]; then
        echo "WARNING: exit code $ret from a shell command."
    fi
    exit $ret
}

bb_bash_exit_handler() {
    ret=$?
    { set +x; } > /dev/null
    trap "" DEBUG
    if [ "$ret" != 0 ]; then
        echo "WARNING: ${BASH_SOURCE[0]}:${__BITBAKE_LAST_LINE} exit $ret from '$1'"

        echo "WARNING: Backtrace (BB generated script): "
        for i in $(seq 1 $((${#FUNCNAME[@]} - 1))); do
            if [ "$i" -eq 1 ]; then
                echo -e "\t#$((i)): ${FUNCNAME[$i]}, ${BASH_SOURCE[$((i-1))]}, line ${__BITBAKE_LAST_LINE}"
            else
                echo -e "\t#$((i)): ${FUNCNAME[$i]}, ${BASH_SOURCE[$((i-1))]}, line ${BASH_LINENO[$((i-1))]}"
            fi
        done
    fi
    exit $ret
}

bb_bash_debug_handler() {
    local line=${BASH_LINENO[0]}
    # For some reason the DEBUG trap trips with lineno=1 when scripts exit; ignore it
    if [ "$line" -eq 1 ]; then
        return
    fi

    # Track the line number of commands as they execute. This is so we can have access to the failing line number
    # in the EXIT trap. See http://gnu-bash.2382.n7.nabble.com/trap-echo-quot-trap-exit-on-LINENO-quot-EXIT-gt-wrong-linenumber-td3666.html
    if [ "${FUNCNAME[1]}" != "bb_bash_exit_handler" ]; then
        __BITBAKE_LAST_LINE=$line
    fi
}

case $BASH_VERSION in
"") trap 'bb_sh_exit_handler' 0
    set -e
    ;;
*)  trap 'bb_bash_exit_handler "$BASH_COMMAND"' 0
    trap '{ bb_bash_debug_handler; } 2>/dev/null' DEBUG
    set -e
    shopt -s extdebug
    ;;
esac
'''

def create_progress_handler(func, progress, logfile, d):
    if progress == 'percent':
        # Use default regex
        return bb.progress.BasicProgressHandler(d, outfile=logfile)
    elif progress.startswith('percent:'):
        # Use specified regex
        return bb.progress.BasicProgressHandler(d, regex=progress.split(':', 1)[1], outfile=logfile)
    elif progress.startswith('outof:'):
        # Use specified regex
        return bb.progress.OutOfProgressHandler(d, regex=progress.split(':', 1)[1], outfile=logfile)
    elif progress.startswith("custom:"):
        # Use a custom progress handler that was injected via OE_EXTRA_IMPORTS or __builtins__
        import functools
        from types import ModuleType

        parts = progress.split(":", 2)
        _, cls, otherargs = parts[0], parts[1], (parts[2] or None) if parts[2:] else None
        if cls:
            def resolve(x, y):
                if not x:
                    return None
                if isinstance(x, ModuleType):
                    return getattr(x, y, None)
                return x.get(y)
            cls_obj = functools.reduce(resolve, cls.split("."), bb.utils._context)
            if not cls_obj:
                # Fall-back on __builtins__
                cls_obj = functools.reduce(resolve, cls.split("."), __builtins__)
            if cls_obj:
                return cls_obj(d, outfile=logfile, otherargs=otherargs)
            bb.warn('%s: unknown custom progress handler in task progress varflag value "%s", ignoring' % (func, cls))
    else:
        bb.warn('%s: invalid task progress varflag value "%s", ignoring' % (func, progress))

    return logfile

def exec_func_shell(func, d, runfile, cwd=None):
    """Execute a shell function from the metadata

    Note on directory behavior.  The 'dirs' varflag should contain a list
    of the directories you need created prior to execution.  The last
    item in the list is where we will chdir/cd to.
    """

    # Don't let the emitted shell script override PWD
    d.delVarFlag('PWD', 'export')

    with open(runfile, 'w') as script:
        script.write(shell_trap_code())

        bb.data.emit_func(func, script, d)

        if verboseShellLogging or bb.utils.to_boolean(d.getVar("BB_VERBOSE_LOGS", False)):
            script.write("set -x\n")
        if cwd:
            script.write("cd '%s'\n" % cwd)
        script.write("%s\n" % func)
        script.write('''
# cleanup
ret=$?
trap '' 0
exit $ret
''')

    os.chmod(runfile, 0o775)

    cmd = runfile
    if d.getVarFlag(func, 'fakeroot', False):
        fakerootcmd = d.getVar('FAKEROOT')
        if fakerootcmd:
            cmd = [fakerootcmd, runfile]

    if verboseStdoutLogging:
        logfile = LogTee(logger, StdoutNoopContextManager())
    else:
        logfile = StdoutNoopContextManager()

    progress = d.getVarFlag(func, 'progress')
    if progress:
        try:
            logfile = create_progress_handler(func, progress, logfile, d)
        except:
            from traceback import format_exc
            logger.error("Failed to create progress handler")
            logger.error(format_exc())
            raise

    fifobuffer = bytearray()
    def readfifo(data):
        nonlocal fifobuffer
        fifobuffer.extend(data)
        while fifobuffer:
            message, token, nextmsg = fifobuffer.partition(b"\00")
            if token:
                splitval = message.split(b' ', 1)
                cmd = splitval[0].decode("utf-8")
                if len(splitval) > 1:
                    value = splitval[1].decode("utf-8")
                else:
                    value = ''
                if cmd == 'bbplain':
                    bb.plain(value)
                elif cmd == 'bbnote':
                    bb.note(value)
                elif cmd == 'bbverbnote':
                    bb.verbnote(value)
                elif cmd == 'bbwarn':
                    bb.warn(value)
                elif cmd == 'bberror':
                    bb.error(value)
                elif cmd == 'bbfatal':
                    # The caller will call exit themselves, so bb.error() is
                    # what we want here rather than bb.fatal()
                    bb.error(value)
                elif cmd == 'bbfatal_log':
                    bb.error(value, forcelog=True)
                elif cmd == 'bbdebug':
                    splitval = value.split(' ', 1)
                    level = int(splitval[0])
                    value = splitval[1]
                    bb.debug(level, value)
                else:
                    bb.warn("Unrecognised command '%s' on FIFO" % cmd)
                fifobuffer = nextmsg
            else:
                break

    tempdir = d.getVar('T')
    fifopath = os.path.join(tempdir, 'fifo.%s' % os.getpid())
    if os.path.exists(fifopath):
        os.unlink(fifopath)
    os.mkfifo(fifopath)
    with open(fifopath, 'r+b', buffering=0) as fifo:
        try:
            bb.debug(2, "Executing shell function %s" % func)
            with open(os.devnull, 'r+') as stdin, logfile:
                bb.process.run(cmd, shell=False, stdin=stdin, log=logfile, extrafiles=[(fifo,readfifo)])
        except bb.process.ExecutionError as exe:
            # Find the backtrace that the shell trap generated
            backtrace_marker_regex = re.compile(r"WARNING: Backtrace \(BB generated script\)")
            stdout_lines = (exe.stdout or "").split("\n")
            backtrace_start_line = None
            for i, line in enumerate(reversed(stdout_lines)):
                if backtrace_marker_regex.search(line):
                    backtrace_start_line = len(stdout_lines) - i
                    break

            # Read the backtrace frames, starting at the location we just found
            backtrace_entry_regex = re.compile(r"#(?P<frameno>\d+): (?P<funcname>[^\s]+), (?P<file>.+?), line ("
                                               r"?P<lineno>\d+)")
            backtrace_frames = []
            if backtrace_start_line:
                for line in itertools.islice(stdout_lines, backtrace_start_line, None):
                    match = backtrace_entry_regex.search(line)
                    if match:
                        backtrace_frames.append(match.groupdict())

            with open(runfile, "r") as script:
                script_lines = [line.rstrip() for line in script.readlines()]

            # For each backtrace frame, search backwards in the script (from the line number called out by the frame),
            # to find the comment that emit_vars injected when it wrote the script. This will give us the metadata
            # filename (e.g. .bb or .bbclass) and line number where the shell function was originally defined.
            script_metadata_comment_regex = re.compile(r"# line: (?P<lineno>\d+), file: (?P<file>.+)")
            better_frames = []
            # Skip the very last frame since it's just the call to the shell task in the body of the script
            for frame in backtrace_frames[:-1]:
                # Check whether the frame corresponds to a function defined in the script vs external script.
                if os.path.samefile(frame["file"], runfile):
                    # Search backwards from the frame lineno to locate the comment that BB injected
                    i = int(frame["lineno"]) - 1
                    while i >= 0:
                        match = script_metadata_comment_regex.match(script_lines[i])
                        if match:
                            # Calculate the relative line in the function itself
                            relative_line_in_function = int(frame["lineno"]) - i - 2
                            # Calculate line in the function as declared in the metadata
                            metadata_function_line = relative_line_in_function + int(match["lineno"])
                            better_frames.append("#{frameno}: {funcname}, {file}, line {lineno}".format(
                                frameno=frame["frameno"],
                                funcname=frame["funcname"],
                                file=match["file"],
                                lineno=metadata_function_line
                            ))
                            break
                        i -= 1
                else:
                    better_frames.append("#{frameno}: {funcname}, {file}, line {lineno}".format(**frame))

            if better_frames:
                better_frames = ("\t{0}".format(frame) for frame in better_frames)
                exe.extra_message = "\nBacktrace (metadata-relative locations):\n{0}".format("\n".join(better_frames))
            raise
        finally:
            os.unlink(fifopath)

    bb.debug(2, "Shell function %s finished" % func)

def _task_data(fn, task, d):
    localdata = bb.data.createCopy(d)
    localdata.setVar('BB_FILENAME', fn)
    localdata.setVar('BB_CURRENTTASK', task[3:])
    localdata.setVar('OVERRIDES', 'task-%s:%s' %
                     (task[3:].replace('_', '-'), d.getVar('OVERRIDES', False)))
    localdata.finalize()
    bb.data.expandKeys(localdata)
    return localdata

def _exec_task(fn, task, d, quieterr):
    """Execute a BB 'task'

    Execution of a task involves a bit more setup than executing a function,
    running it with its own local metadata, and with some useful variables set.
    """
    if not d.getVarFlag(task, 'task', False):
        event.fire(TaskInvalid(task, d), d)
        logger.error("No such task: %s" % task)
        return 1

    logger.debug("Executing task %s", task)

    localdata = _task_data(fn, task, d)
    tempdir = localdata.getVar('T')
    if not tempdir:
        bb.fatal("T variable not set, unable to build")

    # Change nice level if we're asked to
    nice = localdata.getVar("BB_TASK_NICE_LEVEL")
    if nice:
        curnice = os.nice(0)
        nice = int(nice) - curnice
        newnice = os.nice(nice)
        logger.debug("Renice to %s " % newnice)
    ionice = localdata.getVar("BB_TASK_IONICE_LEVEL")
    if ionice:
        try:
            cls, prio = ionice.split(".", 1)
            bb.utils.ioprio_set(os.getpid(), int(cls), int(prio))
        except:
            bb.warn("Invalid ionice level %s" % ionice)

    bb.utils.mkdirhier(tempdir)

    # Determine the logfile to generate
    logfmt = localdata.getVar('BB_LOGFMT') or 'log.{task}.{pid}'
    logbase = logfmt.format(task=task, pid=os.getpid())

    # Document the order of the tasks...
    logorder = os.path.join(tempdir, 'log.task_order')
    try:
        with open(logorder, 'a') as logorderfile:
            logorderfile.write('{0} ({1}): {2}\n'.format(task, os.getpid(), logbase))
    except OSError:
        logger.exception("Opening log file '%s'", logorder)
        pass

    # Setup the courtesy link to the logfn
    loglink = os.path.join(tempdir, 'log.{0}'.format(task))
    logfn = os.path.join(tempdir, logbase)
    if loglink:
        bb.utils.remove(loglink)

        try:
           os.symlink(logbase, loglink)
        except OSError:
           pass

    prefuncs = localdata.getVarFlag(task, 'prefuncs', expand=True)
    postfuncs = localdata.getVarFlag(task, 'postfuncs', expand=True)

    class ErrorCheckHandler(logging.Handler):
        def __init__(self):
            self.triggered = False
            logging.Handler.__init__(self, logging.ERROR)
        def emit(self, record):
            if getattr(record, 'forcelog', False):
                self.triggered = False
            else:
                self.triggered = True

    # Handle logfiles
    try:
        bb.utils.mkdirhier(os.path.dirname(logfn))
        logfile = open(logfn, 'w')
    except OSError:
        logger.exception("Opening log file '%s'", logfn)
        pass

    # Dup the existing fds so we dont lose them
    osi = [os.dup(sys.stdin.fileno()), sys.stdin.fileno()]
    oso = [os.dup(sys.stdout.fileno()), sys.stdout.fileno()]
    ose = [os.dup(sys.stderr.fileno()), sys.stderr.fileno()]

    # Replace those fds with our own
    with open('/dev/null', 'r') as si:
        os.dup2(si.fileno(), osi[1])
    os.dup2(logfile.fileno(), oso[1])
    os.dup2(logfile.fileno(), ose[1])

    # Ensure Python logging goes to the logfile
    handler = logging.StreamHandler(logfile)
    handler.setFormatter(logformatter)
    # Always enable full debug output into task logfiles
    handler.setLevel(logging.DEBUG - 2)
    bblogger.addHandler(handler)

    errchk = ErrorCheckHandler()
    bblogger.addHandler(errchk)

    localdata.setVar('BB_LOGFILE', logfn)
    localdata.setVar('BB_RUNTASK', task)
    localdata.setVar('BB_TASK_LOGGER', bblogger)

    flags = localdata.getVarFlags(task)

    try:
        try:
            event.fire(TaskStarted(task, fn, logfn, flags, localdata), localdata)

            for func in (prefuncs or '').split():
                exec_func(func, localdata)
            exec_func(task, localdata)
            for func in (postfuncs or '').split():
                exec_func(func, localdata)
        finally:
            # Need to flush and close the logs before sending events where the
            # UI may try to look at the logs.
            sys.stdout.flush()
            sys.stderr.flush()

            bblogger.removeHandler(handler)

            # Restore the backup fds
            os.dup2(osi[0], osi[1])
            os.dup2(oso[0], oso[1])
            os.dup2(ose[0], ose[1])

            # Close the backup fds
            os.close(osi[0])
            os.close(oso[0])
            os.close(ose[0])

            logfile.close()
            if os.path.exists(logfn) and os.path.getsize(logfn) == 0:
                logger.debug2("Zero size logfn %s, removing", logfn)
                bb.utils.remove(logfn)
                bb.utils.remove(loglink)
    except bb.BBHandledException:
        event.fire(TaskFailed(task, fn, logfn, localdata, True), localdata)
        return 1
    except (Exception, SystemExit) as exc:
        if quieterr:
            event.fire(TaskFailedSilent(task, fn, logfn, localdata), localdata)
        else:
            errprinted = errchk.triggered
            # If the output is already on stdout, we've printed the information in the
            # logs once already so don't duplicate
            if verboseStdoutLogging:
                errprinted = True
            logger.error(repr(exc))
            event.fire(TaskFailed(task, fn, logfn, localdata, errprinted), localdata)
        return 1

    event.fire(TaskSucceeded(task, fn, logfn, localdata), localdata)

    if not localdata.getVarFlag(task, 'nostamp', False) and not localdata.getVarFlag(task, 'selfstamp', False):
        make_stamp(task, localdata)

    return 0

def exec_task(fn, task, d, profile = False):
    try:
        quieterr = False
        if d.getVarFlag(task, "quieterrors", False) is not None:
            quieterr = True

        if profile:
            profname = "profile-%s.log" % (d.getVar("PN") + "-" + task)
            try:
                import cProfile as profile
            except:
                import profile
            prof = profile.Profile()
            ret = profile.Profile.runcall(prof, _exec_task, fn, task, d, quieterr)
            prof.dump_stats(profname)
            bb.utils.process_profilelog(profname)

            return ret
        else:
            return _exec_task(fn, task, d, quieterr)

    except Exception:
        from traceback import format_exc
        if not quieterr:
            logger.error("Build of %s failed" % (task))
            logger.error(format_exc())
            failedevent = TaskFailed(task, None, d, True)
            event.fire(failedevent, d)
        return 1

def stamp_internal(taskname, d, file_name, baseonly=False, noextra=False):
    """
    Internal stamp helper function
    Makes sure the stamp directory exists
    Returns the stamp path+filename

    In the bitbake core, d can be a CacheData and file_name will be set.
    When called in task context, d will be a data store, file_name will not be set
    """
    taskflagname = taskname
    if taskname.endswith("_setscene") and taskname != "do_setscene":
        taskflagname = taskname.replace("_setscene", "")

    if file_name:
        stamp = d.stamp[file_name]
        extrainfo = d.stamp_extrainfo[file_name].get(taskflagname) or ""
    else:
        stamp = d.getVar('STAMP')
        file_name = d.getVar('BB_FILENAME')
        extrainfo = d.getVarFlag(taskflagname, 'stamp-extra-info') or ""

    if baseonly:
        return stamp
    if noextra:
        extrainfo = ""

    if not stamp:
        return

    stamp = bb.parse.siggen.stampfile(stamp, file_name, taskname, extrainfo)

    stampdir = os.path.dirname(stamp)
    if cached_mtime_noerror(stampdir) == 0:
        bb.utils.mkdirhier(stampdir)

    return stamp

def stamp_cleanmask_internal(taskname, d, file_name):
    """
    Internal stamp helper function to generate stamp cleaning mask
    Returns the stamp path+filename

    In the bitbake core, d can be a CacheData and file_name will be set.
    When called in task context, d will be a data store, file_name will not be set
    """
    taskflagname = taskname
    if taskname.endswith("_setscene") and taskname != "do_setscene":
        taskflagname = taskname.replace("_setscene", "")

    if file_name:
        stamp = d.stampclean[file_name]
        extrainfo = d.stamp_extrainfo[file_name].get(taskflagname) or ""
    else:
        stamp = d.getVar('STAMPCLEAN')
        file_name = d.getVar('BB_FILENAME')
        extrainfo = d.getVarFlag(taskflagname, 'stamp-extra-info') or ""

    if not stamp:
        return []

    cleanmask = bb.parse.siggen.stampcleanmask(stamp, file_name, taskname, extrainfo)

    return [cleanmask, cleanmask.replace(taskflagname, taskflagname + "_setscene")]

def make_stamp(task, d, file_name = None):
    """
    Creates/updates a stamp for a given task
    (d can be a data dict or dataCache)
    """
    cleanmask = stamp_cleanmask_internal(task, d, file_name)
    for mask in cleanmask:
        for name in glob.glob(mask):
            # Preserve sigdata files in the stamps directory
            if "sigdata" in name or "sigbasedata" in name:
                continue
            # Preserve taint files in the stamps directory
            if name.endswith('.taint'):
                continue
            os.unlink(name)

    stamp = stamp_internal(task, d, file_name)
    # Remove the file and recreate to force timestamp
    # change on broken NFS filesystems
    if stamp:
        bb.utils.remove(stamp)
        open(stamp, "w").close()

    # If we're in task context, write out a signature file for each task
    # as it completes
    if not task.endswith("_setscene") and task != "do_setscene" and not file_name:
        stampbase = stamp_internal(task, d, None, True)
        file_name = d.getVar('BB_FILENAME')
        bb.parse.siggen.dump_sigtask(file_name, task, stampbase, True)

def find_stale_stamps(task, d, file_name=None):
    current = stamp_internal(task, d, file_name)
    current2 = stamp_internal(task + "_setscene", d, file_name)
    cleanmask = stamp_cleanmask_internal(task, d, file_name)
    found = []
    for mask in cleanmask:
        for name in glob.glob(mask):
            if "sigdata" in name or "sigbasedata" in name:
                continue
            if name.endswith('.taint'):
                continue
            if name == current or name == current2:
                continue
            logger.debug2("Stampfile %s does not match %s or %s" % (name, current, current2))
            found.append(name)
    return found

def del_stamp(task, d, file_name = None):
    """
    Removes a stamp for a given task
    (d can be a data dict or dataCache)
    """
    stamp = stamp_internal(task, d, file_name)
    bb.utils.remove(stamp)

def write_taint(task, d, file_name = None):
    """
    Creates a "taint" file which will force the specified task and its
    dependents to be re-run the next time by influencing the value of its
    taskhash.
    (d can be a data dict or dataCache)
    """
    import uuid
    if file_name:
        taintfn = d.stamp[file_name] + '.' + task + '.taint'
    else:
        taintfn = d.getVar('STAMP') + '.' + task + '.taint'
    bb.utils.mkdirhier(os.path.dirname(taintfn))
    # The specific content of the taint file is not really important,
    # we just need it to be random, so a random UUID is used
    with open(taintfn, 'w') as taintf:
        taintf.write(str(uuid.uuid4()))

def stampfile(taskname, d, file_name = None, noextra=False):
    """
    Return the stamp for a given task
    (d can be a data dict or dataCache)
    """
    return stamp_internal(taskname, d, file_name, noextra=noextra)

def add_tasks(tasklist, d):
    task_deps = d.getVar('_task_deps', False)
    if not task_deps:
        task_deps = {}
    if not 'tasks' in task_deps:
        task_deps['tasks'] = []
    if not 'parents' in task_deps:
        task_deps['parents'] = {}

    for task in tasklist:
        task = d.expand(task)

        d.setVarFlag(task, 'task', 1)

        if not task in task_deps['tasks']:
            task_deps['tasks'].append(task)

        flags = d.getVarFlags(task)
        def getTask(name):
            if not name in task_deps:
                task_deps[name] = {}
            if name in flags:
                deptask = d.expand(flags[name])
                if name in ['noexec', 'fakeroot', 'nostamp']:
                    if deptask != '1':
                        bb.warn("In a future version of BitBake, setting the '{}' flag to something other than '1' "
                                "will result in the flag not being set. See YP bug #13808.".format(name))

                task_deps[name][task] = deptask
        getTask('mcdepends')
        getTask('depends')
        getTask('rdepends')
        getTask('deptask')
        getTask('rdeptask')
        getTask('recrdeptask')
        getTask('recideptask')
        getTask('nostamp')
        getTask('fakeroot')
        getTask('noexec')
        getTask('umask')
        task_deps['parents'][task] = []
        if 'deps' in flags:
            for dep in flags['deps']:
                # Check and warn for "addtask task after foo" while foo does not exist
                #if not dep in tasklist:
                #    bb.warn('%s: dependent task %s for %s does not exist' % (d.getVar('PN'), dep, task))
                dep = d.expand(dep)
                task_deps['parents'][task].append(dep)

    # don't assume holding a reference
    d.setVar('_task_deps', task_deps)

def addtask(task, before, after, d):
    if task[:3] != "do_":
        task = "do_" + task

    d.setVarFlag(task, "task", 1)
    bbtasks = d.getVar('__BBTASKS', False) or []
    if task not in bbtasks:
        bbtasks.append(task)
    d.setVar('__BBTASKS', bbtasks)

    existing = d.getVarFlag(task, "deps", False) or []
    if after is not None:
        # set up deps for function
        for entry in after.split():
            if entry not in existing:
                existing.append(entry)
    d.setVarFlag(task, "deps", existing)
    if before is not None:
        # set up things that depend on this func
        for entry in before.split():
            existing = d.getVarFlag(entry, "deps", False) or []
            if task not in existing:
                d.setVarFlag(entry, "deps", [task] + existing)

def deltask(task, d):
    if task[:3] != "do_":
        task = "do_" + task

    bbtasks = d.getVar('__BBTASKS', False) or []
    if task in bbtasks:
        bbtasks.remove(task)
        d.delVarFlag(task, 'task')
        d.setVar('__BBTASKS', bbtasks)

    d.delVarFlag(task, 'deps')
    for bbtask in d.getVar('__BBTASKS', False) or []:
        deps = d.getVarFlag(bbtask, 'deps', False) or []
        if task in deps:
            deps.remove(task)
            d.setVarFlag(bbtask, 'deps', deps)

def preceedtask(task, with_recrdeptasks, d):
    """
    Returns a set of tasks in the current recipe which were specified as
    precondition by the task itself ("after") or which listed themselves
    as precondition ("before"). Preceeding tasks specified via the
    "recrdeptask" are included in the result only if requested. Beware
    that this may lead to the task itself being listed.
    """
    preceed = set()

    # Ignore tasks which don't exist
    tasks = d.getVar('__BBTASKS', False)
    if task not in tasks:
        return preceed

    preceed.update(d.getVarFlag(task, 'deps') or [])
    if with_recrdeptasks:
        recrdeptask = d.getVarFlag(task, 'recrdeptask')
        if recrdeptask:
            preceed.update(recrdeptask.split())
    return preceed

def tasksbetween(task_start, task_end, d):
    """
    Return the list of tasks between two tasks in the current recipe,
    where task_start is to start at and task_end is the task to end at
    (and task_end has a dependency chain back to task_start).
    """
    outtasks = []
    tasks = list(filter(lambda k: d.getVarFlag(k, "task"), d.keys()))
    def follow_chain(task, endtask, chain=None):
        if not chain:
            chain = []
        if task in chain:
            bb.fatal("Circular task dependencies as %s depends on itself via the chain %s" % (task, " -> ".join(chain)))
        chain.append(task)
        for othertask in tasks:
            if othertask == task:
                continue
            if task == endtask:
                for ctask in chain:
                    if ctask not in outtasks:
                        outtasks.append(ctask)
            else:
                deps = d.getVarFlag(othertask, 'deps', False)
                if task in deps:
                    follow_chain(othertask, endtask, chain)
        chain.pop()
    follow_chain(task_start, task_end)
    return outtasks
