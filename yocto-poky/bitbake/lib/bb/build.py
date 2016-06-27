# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
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
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

import os
import sys
import logging
import shlex
import glob
import time
import stat
import bb
import bb.msg
import bb.process
from contextlib import nested
from bb import event, utils

bblogger = logging.getLogger('BitBake')
logger = logging.getLogger('BitBake.Build')

NULL = open(os.devnull, 'r+')

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
__builtins__['bb'] = bb
__builtins__['os'] = os

class FuncFailed(Exception):
    def __init__(self, name = None, logfile = None):
        self.logfile = logfile
        self.name = name
        if name:
            self.msg = 'Function failed: %s' % name
        else:
            self.msg = "Function failed"

    def __str__(self):
        if self.logfile and os.path.exists(self.logfile):
            msg = ("%s (log file is located at %s)" %
                   (self.msg, self.logfile))
        else:
            msg = self.msg
        return msg

class TaskBase(event.Event):
    """Base class for task events"""

    def __init__(self, t, logfile, d):
        self._task = t
        self._package = d.getVar("PF", True)
        self.taskfile = d.getVar("FILE", True)
        self.taskname = self._task
        self.logfile = logfile
        self.time = time.time()
        event.Event.__init__(self)
        self._message = "recipe %s: task %s: %s" % (d.getVar("PF", True), t, self.getDisplayName())

    def getTask(self):
        return self._task

    def setTask(self, task):
        self._task = task

    def getDisplayName(self):
        return bb.event.getName(self)[4:]

    task = property(getTask, setTask, None, "task property")

class TaskStarted(TaskBase):
    """Task execution started"""
    def __init__(self, t, logfile, taskflags, d):
        super(TaskStarted, self).__init__(t, logfile, d)
        self.taskflags = taskflags

class TaskSucceeded(TaskBase):
    """Task execution completed"""

class TaskFailed(TaskBase):
    """Task execution failed"""

    def __init__(self, task, logfile, metadata, errprinted = False):
        self.errprinted = errprinted
        super(TaskFailed, self).__init__(task, logfile, metadata)

class TaskFailedSilent(TaskBase):
    """Task execution failed (silently)"""
    def getDisplayName(self):
        # Don't need to tell the user it was silent
        return "Failed"

class TaskInvalid(TaskBase):

    def __init__(self, task, metadata):
        super(TaskInvalid, self).__init__(task, None, metadata)
        self._message = "No such task '%s'" % task


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

#
# pythonexception allows the python exceptions generated to be raised
# as the real exceptions (not FuncFailed) and without a backtrace at the 
# origin of the failure.
#
def exec_func(func, d, dirs = None, pythonexception=False):
    """Execute a BB 'function'"""

    body = d.getVar(func, False)
    if not body:
        if body is None:
            logger.warn("Function %s doesn't exist", func)
        return

    flags = d.getVarFlags(func)
    cleandirs = flags.get('cleandirs')
    if cleandirs:
        for cdir in d.expand(cleandirs).split():
            bb.utils.remove(cdir, True)
            bb.utils.mkdirhier(cdir)

    if dirs is None:
        dirs = flags.get('dirs')
        if dirs:
            dirs = d.expand(dirs).split()

    if dirs:
        for adir in dirs:
            bb.utils.mkdirhier(adir)
        adir = dirs[-1]
    else:
        adir = d.getVar('B', True)
        bb.utils.mkdirhier(adir)

    ispython = flags.get('python')

    lockflag = flags.get('lockfiles')
    if lockflag:
        lockfiles = [f for f in d.expand(lockflag).split()]
    else:
        lockfiles = None

    tempdir = d.getVar('T', True)

    # or func allows items to be executed outside of the normal
    # task set, such as buildhistory
    task = d.getVar('BB_RUNTASK', True) or func
    if task == func:
        taskfunc = task
    else:
        taskfunc = "%s.%s" % (task, func)

    runfmt = d.getVar('BB_RUNFMT', True) or "run.{func}.{pid}"
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
            exec_func_python(func, d, runfile, cwd=adir, pythonexception=pythonexception)
        else:
            exec_func_shell(func, d, runfile, cwd=adir)

_functionfmt = """
{function}(d)
"""
logformatter = bb.msg.BBLogFormatter("%(levelname)s: %(message)s")
def exec_func_python(func, d, runfile, cwd=None, pythonexception=False):
    """Execute a python BB 'function'"""

    code = _functionfmt.format(function=func)
    bb.utils.mkdirhier(os.path.dirname(runfile))
    with open(runfile, 'w') as script:
        bb.data.emit_func_python(func, script, d)

    if cwd:
        try:
            olddir = os.getcwd()
        except OSError:
            olddir = None
        os.chdir(cwd)

    bb.debug(2, "Executing python function %s" % func)

    try:
        text = "def %s(d):\n%s" % (func, d.getVar(func, False))
        fn = d.getVarFlag(func, "filename", False)
        lineno = int(d.getVarFlag(func, "lineno", False))
        bb.methodpool.insert_method(func, text, fn, lineno - 1)

        comp = utils.better_compile(code, func, "exec_python_func() autogenerated")
        utils.better_exec(comp, {"d": d}, code, "exec_python_func() autogenerated", pythonexception=pythonexception)
    except (bb.parse.SkipRecipe, bb.build.FuncFailed):
        raise
    except:
        if pythonexception:
            raise
        raise FuncFailed(func, None)
    finally:
        bb.debug(2, "Python function %s finished" % func)

        if cwd and olddir:
            try:
                os.chdir(olddir)
            except OSError:
                pass

def shell_trap_code():
    return '''#!/bin/sh\n
# Emit a useful diagnostic if something fails:
bb_exit_handler() {
    ret=$?
    case $ret in
    0)  ;;
    *)  case $BASH_VERSION in
        "") echo "WARNING: exit code $ret from a shell command.";;
        *)  echo "WARNING: ${BASH_SOURCE[0]}:${BASH_LINENO[0]} exit $ret from '$BASH_COMMAND'";;
        esac
        exit $ret
    esac
}
trap 'bb_exit_handler' 0
set -e
'''

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

        if bb.msg.loggerVerboseLogs:
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

    os.chmod(runfile, 0775)

    cmd = runfile
    if d.getVarFlag(func, 'fakeroot', False):
        fakerootcmd = d.getVar('FAKEROOT', True)
        if fakerootcmd:
            cmd = [fakerootcmd, runfile]

    if bb.msg.loggerDefaultVerbose:
        logfile = LogTee(logger, sys.stdout)
    else:
        logfile = sys.stdout

    def readfifo(data):
        lines = data.split('\0')
        for line in lines:
            splitval = line.split(' ', 1)
            cmd = splitval[0]
            if len(splitval) > 1:
                value = splitval[1]
            else:
                value = ''
            if cmd == 'bbplain':
                bb.plain(value)
            elif cmd == 'bbnote':
                bb.note(value)
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

    tempdir = d.getVar('T', True)
    fifopath = os.path.join(tempdir, 'fifo.%s' % os.getpid())
    if os.path.exists(fifopath):
        os.unlink(fifopath)
    os.mkfifo(fifopath)
    with open(fifopath, 'r+') as fifo:
        try:
            bb.debug(2, "Executing shell function %s" % func)

            try:
                with open(os.devnull, 'r+') as stdin:
                    bb.process.run(cmd, shell=False, stdin=stdin, log=logfile, extrafiles=[(fifo,readfifo)])
            except bb.process.CmdError:
                logfn = d.getVar('BB_LOGFILE', True)
                raise FuncFailed(func, logfn)
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

    logger.debug(1, "Executing task %s", task)

    localdata = _task_data(fn, task, d)
    tempdir = localdata.getVar('T', True)
    if not tempdir:
        bb.fatal("T variable not set, unable to build")

    # Change nice level if we're asked to
    nice = localdata.getVar("BB_TASK_NICE_LEVEL", True)
    if nice:
        curnice = os.nice(0)
        nice = int(nice) - curnice
        newnice = os.nice(nice)
        logger.debug(1, "Renice to %s " % newnice)
    ionice = localdata.getVar("BB_TASK_IONICE_LEVEL", True)
    if ionice:
        try:
            cls, prio = ionice.split(".", 1)
            bb.utils.ioprio_set(os.getpid(), int(cls), int(prio))
        except:
            bb.warn("Invalid ionice level %s" % ionice)

    bb.utils.mkdirhier(tempdir)

    # Determine the logfile to generate
    logfmt = localdata.getVar('BB_LOGFMT', True) or 'log.{task}.{pid}'
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
    si = open('/dev/null', 'r')
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

    flags = localdata.getVarFlags(task)

    event.fire(TaskStarted(task, logfn, flags, localdata), localdata)
    try:
        for func in (prefuncs or '').split():
            exec_func(func, localdata)
        exec_func(task, localdata)
        for func in (postfuncs or '').split():
            exec_func(func, localdata)
    except FuncFailed as exc:
        if quieterr:
            event.fire(TaskFailedSilent(task, logfn, localdata), localdata)
        else:
            errprinted = errchk.triggered
            logger.error(str(exc))
            event.fire(TaskFailed(task, logfn, localdata, errprinted), localdata)
        return 1
    finally:
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
        si.close()

        logfile.close()
        if os.path.exists(logfn) and os.path.getsize(logfn) == 0:
            logger.debug(2, "Zero size logfn %s, removing", logfn)
            bb.utils.remove(logfn)
            bb.utils.remove(loglink)
    event.fire(TaskSucceeded(task, logfn, localdata), localdata)

    if not localdata.getVarFlag(task, 'nostamp', False) and not localdata.getVarFlag(task, 'selfstamp', False):
        make_stamp(task, localdata)

    return 0

def exec_task(fn, task, d, profile = False):
    try:
        quieterr = False
        if d.getVarFlag(task, "quieterrors", False) is not None:
            quieterr = True

        if profile:
            profname = "profile-%s.log" % (d.getVar("PN", True) + "-" + task)
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

def stamp_internal(taskname, d, file_name, baseonly=False):
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
        stamp = d.getVar('STAMP', True)
        file_name = d.getVar('BB_FILENAME', True)
        extrainfo = d.getVarFlag(taskflagname, 'stamp-extra-info', True) or ""

    if baseonly:
        return stamp

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
        stamp = d.getVar('STAMPCLEAN', True)
        file_name = d.getVar('BB_FILENAME', True)
        extrainfo = d.getVarFlag(taskflagname, 'stamp-extra-info', True) or ""

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
            if "sigdata" in name:
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
        file_name = d.getVar('BB_FILENAME', True)
        bb.parse.siggen.dump_sigtask(file_name, task, stampbase, True)

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
        taintfn = d.getVar('STAMP', True) + '.' + task + '.taint'
    bb.utils.mkdirhier(os.path.dirname(taintfn))
    # The specific content of the taint file is not really important,
    # we just need it to be random, so a random UUID is used
    with open(taintfn, 'w') as taintf:
        taintf.write(str(uuid.uuid4()))

def stampfile(taskname, d, file_name = None):
    """
    Return the stamp for a given task
    (d can be a data dict or dataCache)
    """
    return stamp_internal(taskname, d, file_name)

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
                task_deps[name][task] = deptask
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
        d.setVar('__BBTASKS', bbtasks)

    d.delVarFlag(task, 'deps')
    for bbtask in d.getVar('__BBTASKS', False) or []:
        deps = d.getVarFlag(bbtask, 'deps', False) or []
        if task in deps:
            deps.remove(task)
            d.setVarFlag(bbtask, 'deps', deps)
