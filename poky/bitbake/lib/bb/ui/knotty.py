#
# BitBake (No)TTY UI Implementation
#
# Handling output to TTYs or files (no TTY)
#
# Copyright (C) 2006-2012 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

from __future__ import division

import os
import sys
import logging
import progressbar
import signal
import bb.msg
import time
import fcntl
import struct
import copy
import atexit
from itertools import groupby

from bb.ui import uihelper
import bb.build
import bb.command
import bb.cooker
import bb.event
import bb.runqueue
import bb.utils

featureSet = [bb.cooker.CookerFeatures.SEND_SANITYEVENTS, bb.cooker.CookerFeatures.BASEDATASTORE_TRACKING]

logger = logging.getLogger("BitBake")
interactive = sys.stdout.isatty()

class BBProgress(progressbar.ProgressBar):
    def __init__(self, msg, maxval, widgets=None, extrapos=-1, resize_handler=None):
        self.msg = msg
        self.extrapos = extrapos
        if not widgets:
            widgets = [': ', progressbar.Percentage(), ' ', progressbar.Bar(),
                       ' ', progressbar.ETA()]
            self.extrapos = 5

        if resize_handler:
            self._resize_default = resize_handler
        else:
            self._resize_default = signal.getsignal(signal.SIGWINCH)
        progressbar.ProgressBar.__init__(self, maxval, [self.msg] + widgets, fd=sys.stdout)

    def _handle_resize(self, signum=None, frame=None):
        progressbar.ProgressBar._handle_resize(self, signum, frame)
        if self._resize_default:
            self._resize_default(signum, frame)

    def finish(self):
        progressbar.ProgressBar.finish(self)
        if self._resize_default:
            signal.signal(signal.SIGWINCH, self._resize_default)

    def setmessage(self, msg):
        self.msg = msg
        self.widgets[0] = msg

    def setextra(self, extra):
        if self.extrapos > -1:
            if extra:
                extrastr = str(extra)
                if extrastr[0] != ' ':
                    extrastr = ' ' + extrastr
            else:
                extrastr = ''
            self.widgets[self.extrapos] = extrastr

    def _need_update(self):
        # We always want the bar to print when update() is called
        return True

class NonInteractiveProgress(object):
    fobj = sys.stdout

    def __init__(self, msg, maxval):
        self.msg = msg
        self.maxval = maxval
        self.finished = False

    def start(self, update=True):
        self.fobj.write("%s..." % self.msg)
        self.fobj.flush()
        return self

    def update(self, value):
        pass

    def finish(self):
        if self.finished:
            return
        self.fobj.write("done.\n")
        self.fobj.flush()
        self.finished = True

def new_progress(msg, maxval):
    if interactive:
        return BBProgress(msg, maxval)
    else:
        return NonInteractiveProgress(msg, maxval)

def pluralise(singular, plural, qty):
    if qty == 1:
        return singular % qty
    else:
        return plural % qty


class InteractConsoleLogFilter(logging.Filter):
    def __init__(self, tf):
        self.tf = tf
        super().__init__()

    def filter(self, record):
        if record.levelno == bb.msg.BBLogFormatter.NOTE and (record.msg.startswith("Running") or record.msg.startswith("recipe ")):
            return False
        self.tf.clearFooter()
        return True

class TerminalFilter(object):
    rows = 25
    columns = 80

    def sigwinch_handle(self, signum, frame):
        self.rows, self.columns = self.getTerminalColumns()
        if self._sigwinch_default:
            self._sigwinch_default(signum, frame)

    def getTerminalColumns(self):
        def ioctl_GWINSZ(fd):
            try:
                cr = struct.unpack('hh', fcntl.ioctl(fd, self.termios.TIOCGWINSZ, '1234'))
            except:
                return None
            return cr
        cr = ioctl_GWINSZ(sys.stdout.fileno())
        if not cr:
            try:
                fd = os.open(os.ctermid(), os.O_RDONLY)
                cr = ioctl_GWINSZ(fd)
                os.close(fd)
            except:
                pass
        if not cr:
            try:
                cr = (os.environ['LINES'], os.environ['COLUMNS'])
            except:
                cr = (25, 80)
        return cr

    def __init__(self, main, helper, handlers, quiet):
        self.main = main
        self.helper = helper
        self.cuu = None
        self.stdinbackup = None
        self.interactive = sys.stdout.isatty()
        self.footer_present = False
        self.lastpids = []
        self.lasttime = None
        self.quiet = quiet

        if not self.interactive:
            return

        try:
            import curses
        except ImportError:
            sys.exit("FATAL: The knotty ui could not load the required curses python module.")

        import termios
        self.curses = curses
        self.termios = termios
        try:
            fd = sys.stdin.fileno()
            self.stdinbackup = termios.tcgetattr(fd)
            new = copy.deepcopy(self.stdinbackup)
            new[3] = new[3] & ~termios.ECHO
            termios.tcsetattr(fd, termios.TCSADRAIN, new)
            curses.setupterm()
            if curses.tigetnum("colors") > 2 and os.environ.get('NO_COLOR', '') == '':
                for h in handlers:
                    try:
                        h.formatter.enable_color()
                    except AttributeError:
                        pass
            self.ed = curses.tigetstr("ed")
            if self.ed:
                self.cuu = curses.tigetstr("cuu")
            try:
                self._sigwinch_default = signal.getsignal(signal.SIGWINCH)
                signal.signal(signal.SIGWINCH, self.sigwinch_handle)
            except:
                pass
            self.rows, self.columns = self.getTerminalColumns()
        except:
            self.cuu = None
        if not self.cuu:
            self.interactive = False
            bb.note("Unable to use interactive mode for this terminal, using fallback")
            return

        for h in handlers:
            h.addFilter(InteractConsoleLogFilter(self))

        self.main_progress = None

    def clearFooter(self):
        if self.footer_present:
            lines = self.footer_present
            sys.stdout.buffer.write(self.curses.tparm(self.cuu, lines))
            sys.stdout.buffer.write(self.curses.tparm(self.ed))
            sys.stdout.flush()
        self.footer_present = False

    def elapsed(self, sec):
        hrs = int(sec / 3600.0)
        sec -= hrs * 3600
        min = int(sec / 60.0)
        sec -= min * 60
        if hrs > 0:
            return "%dh%dm%ds" % (hrs, min, sec)
        elif min > 0:
            return "%dm%ds" % (min, sec)
        else:
            return "%ds" % (sec)

    def keepAlive(self, t):
        if not self.cuu:
            print("Bitbake still alive (no events for %ds). Active tasks:" % t)
            for t in self.helper.running_tasks:
                print(t)
            sys.stdout.flush()

    def updateFooter(self):
        if not self.cuu:
            return
        activetasks = self.helper.running_tasks
        failedtasks = self.helper.failed_tasks
        runningpids = self.helper.running_pids
        currenttime = time.time()
        if not self.lasttime or (currenttime - self.lasttime > 5):
            self.helper.needUpdate = True
            self.lasttime = currenttime
        if self.footer_present and not self.helper.needUpdate:
            return
        self.helper.needUpdate = False
        if self.footer_present:
            self.clearFooter()
        if (not self.helper.tasknumber_total or self.helper.tasknumber_current == self.helper.tasknumber_total) and not len(activetasks):
            return
        tasks = []
        for t in runningpids:
            start_time = activetasks[t].get("starttime", None)
            if start_time:
                msg = "%s - %s (pid %s)" % (activetasks[t]["title"], self.elapsed(currenttime - start_time), activetasks[t]["pid"])
            else:
                msg = "%s (pid %s)" % (activetasks[t]["title"], activetasks[t]["pid"])
            progress = activetasks[t].get("progress", None)
            if progress is not None:
                pbar = activetasks[t].get("progressbar", None)
                rate = activetasks[t].get("rate", None)
                if not pbar or pbar.bouncing != (progress < 0):
                    if progress < 0:
                        pbar = BBProgress("0: %s" % msg, 100, widgets=[' ', progressbar.BouncingSlider(), ''], extrapos=3, resize_handler=self.sigwinch_handle)
                        pbar.bouncing = True
                    else:
                        pbar = BBProgress("0: %s" % msg, 100, widgets=[' ', progressbar.Percentage(), ' ', progressbar.Bar(), ''], extrapos=5, resize_handler=self.sigwinch_handle)
                        pbar.bouncing = False
                    activetasks[t]["progressbar"] = pbar
                tasks.append((pbar, msg, progress, rate, start_time))
            else:
                tasks.append(msg)

        if self.main.shutdown:
            content = pluralise("Waiting for %s running task to finish",
                                "Waiting for %s running tasks to finish", len(activetasks))
            if not self.quiet:
                content += ':'
            print(content)
        else:
            scene_tasks = "%s of %s" % (self.helper.setscene_current, self.helper.setscene_total)
            cur_tasks = "%s of %s" % (self.helper.tasknumber_current, self.helper.tasknumber_total)

            content = ''
            if not self.quiet:
                msg = "Setscene tasks: %s" % scene_tasks
                content += msg + "\n"
                print(msg)

            if self.quiet:
                msg = "Running tasks (%s, %s)" % (scene_tasks, cur_tasks)
            elif not len(activetasks):
                msg = "No currently running tasks (%s)" % cur_tasks
            else:
                msg = "Currently %2s running tasks (%s)" % (len(activetasks), cur_tasks)
            maxtask = self.helper.tasknumber_total
            if not self.main_progress or self.main_progress.maxval != maxtask:
                widgets = [' ', progressbar.Percentage(), ' ', progressbar.Bar()]
                self.main_progress = BBProgress("Running tasks", maxtask, widgets=widgets, resize_handler=self.sigwinch_handle)
                self.main_progress.start(False)
            self.main_progress.setmessage(msg)
            progress = max(0, self.helper.tasknumber_current - 1)
            content += self.main_progress.update(progress)
            print('')
        lines = self.getlines(content)
        if not self.quiet:
            for tasknum, task in enumerate(tasks[:(self.rows - 1 - lines)]):
                if isinstance(task, tuple):
                    pbar, msg, progress, rate, start_time = task
                    if not pbar.start_time:
                        pbar.start(False)
                        if start_time:
                            pbar.start_time = start_time
                    pbar.setmessage('%s: %s' % (tasknum, msg))
                    pbar.setextra(rate)
                    if progress > -1:
                        content = pbar.update(progress)
                    else:
                        content = pbar.update(1)
                    print('')
                else:
                    content = "%s: %s" % (tasknum, task)
                    print(content)
                lines = lines + self.getlines(content)
        self.footer_present = lines
        self.lastpids = runningpids[:]
        self.lastcount = self.helper.tasknumber_current

    def getlines(self, content):
        lines = 0
        for line in content.split("\n"):
            lines = lines + 1 + int(len(line) / (self.columns + 1))
        return lines

    def finish(self):
        if self.stdinbackup:
            fd = sys.stdin.fileno()
            self.termios.tcsetattr(fd, self.termios.TCSADRAIN, self.stdinbackup)

def print_event_log(event, includelogs, loglines, termfilter):
    # FIXME refactor this out further
    logfile = event.logfile
    if logfile and os.path.exists(logfile):
        termfilter.clearFooter()
        bb.error("Logfile of failure stored in: %s" % logfile)
        if includelogs and not event.errprinted:
            print("Log data follows:")
            f = open(logfile, "r")
            lines = []
            while True:
                l = f.readline()
                if l == '':
                    break
                l = l.rstrip()
                if loglines:
                    lines.append(' | %s' % l)
                    if len(lines) > int(loglines):
                        lines.pop(0)
                else:
                    print('| %s' % l)
            f.close()
            if lines:
                for line in lines:
                    print(line)

def _log_settings_from_server(server, observe_only):
    # Get values of variables which control our output
    includelogs, error = server.runCommand(["getVariable", "BBINCLUDELOGS"])
    if error:
        logger.error("Unable to get the value of BBINCLUDELOGS variable: %s" % error)
        raise BaseException(error)
    loglines, error = server.runCommand(["getVariable", "BBINCLUDELOGS_LINES"])
    if error:
        logger.error("Unable to get the value of BBINCLUDELOGS_LINES variable: %s" % error)
        raise BaseException(error)
    if observe_only:
        cmd = 'getVariable'
    else:
        cmd = 'getSetVariable'
    consolelogfile, error = server.runCommand([cmd, "BB_CONSOLELOG"])
    if error:
        logger.error("Unable to get the value of BB_CONSOLELOG variable: %s" % error)
        raise BaseException(error)
    logconfigfile, error = server.runCommand([cmd, "BB_LOGCONFIG"])
    if error:
        logger.error("Unable to get the value of BB_LOGCONFIG variable: %s" % error)
        raise BaseException(error)
    return includelogs, loglines, consolelogfile, logconfigfile

_evt_list = [ "bb.runqueue.runQueueExitWait", "bb.event.LogExecTTY", "logging.LogRecord",
              "bb.build.TaskFailed", "bb.build.TaskBase", "bb.event.ParseStarted",
              "bb.event.ParseProgress", "bb.event.ParseCompleted", "bb.event.CacheLoadStarted",
              "bb.event.CacheLoadProgress", "bb.event.CacheLoadCompleted", "bb.command.CommandFailed",
              "bb.command.CommandExit", "bb.command.CommandCompleted",  "bb.cooker.CookerExit",
              "bb.event.MultipleProviders", "bb.event.NoProvider", "bb.runqueue.sceneQueueTaskStarted",
              "bb.runqueue.runQueueTaskStarted", "bb.runqueue.runQueueTaskFailed", "bb.runqueue.sceneQueueTaskFailed",
              "bb.event.BuildBase", "bb.build.TaskStarted", "bb.build.TaskSucceeded", "bb.build.TaskFailedSilent",
              "bb.build.TaskProgress", "bb.event.ProcessStarted", "bb.event.ProcessProgress", "bb.event.ProcessFinished"]

def drain_events_errorhandling(eventHandler):
    # We don't have logging setup, we do need to show any events we see before exiting
    event = True
    logger = bb.msg.logger_create('bitbake', sys.stdout)
    while event:
        event = eventHandler.waitEvent(0)
        if isinstance(event, logging.LogRecord):
            logger.handle(event)

def main(server, eventHandler, params, tf = TerminalFilter):

    try:
        if not params.observe_only:
            params.updateToServer(server, os.environ.copy())

        includelogs, loglines, consolelogfile, logconfigfile = _log_settings_from_server(server, params.observe_only)

        loglevel, _ = bb.msg.constructLogOptions()
    except bb.BBHandledException:
        drain_events_errorhandling(eventHandler)
        return 1
    except Exception as e:
        # bitbake-server comms failure
        early_logger = bb.msg.logger_create('bitbake', sys.stdout)
        early_logger.fatal("Attempting to set server environment: %s", e)
        return 1

    if params.options.quiet == 0:
        console_loglevel = loglevel
    elif params.options.quiet > 2:
        console_loglevel = bb.msg.BBLogFormatter.ERROR
    else:
        console_loglevel = bb.msg.BBLogFormatter.WARNING

    logconfig = {
        "version": 1,
        "handlers": {
            "BitBake.console": {
                "class": "logging.StreamHandler",
                "formatter": "BitBake.consoleFormatter",
                "level": console_loglevel,
                "stream": "ext://sys.stdout",
                "filters": ["BitBake.stdoutFilter"],
                ".": {
                    "is_console": True,
                },
            },
            "BitBake.errconsole": {
                "class": "logging.StreamHandler",
                "formatter": "BitBake.consoleFormatter",
                "level": loglevel,
                "stream": "ext://sys.stderr",
                "filters": ["BitBake.stderrFilter"],
                ".": {
                    "is_console": True,
                },
            },
            # This handler can be used if specific loggers should print on
            # the console at a lower severity than the default. It will
            # display any messages sent to it that are lower than then
            # BitBake.console logging level (so as to prevent duplication of
            # messages). Nothing is attached to this handler by default
            "BitBake.verbconsole": {
                "class": "logging.StreamHandler",
                "formatter": "BitBake.consoleFormatter",
                "level": 1,
                "stream": "ext://sys.stdout",
                "filters": ["BitBake.verbconsoleFilter"],
                ".": {
                    "is_console": True,
                },
            },
        },
        "formatters": {
            # This format instance will get color output enabled by the
            # terminal
            "BitBake.consoleFormatter" : {
                "()": "bb.msg.BBLogFormatter",
                "format": "%(levelname)s: %(message)s"
            },
            # The file log requires a separate instance so that it doesn't get
            # color enabled
            "BitBake.logfileFormatter": {
                "()": "bb.msg.BBLogFormatter",
                "format": "%(levelname)s: %(message)s"
            }
        },
        "filters": {
            "BitBake.stdoutFilter": {
                "()": "bb.msg.LogFilterLTLevel",
                "level": "ERROR"
            },
            "BitBake.stderrFilter": {
                "()": "bb.msg.LogFilterGEQLevel",
                "level": "ERROR"
            },
            "BitBake.verbconsoleFilter": {
                "()": "bb.msg.LogFilterLTLevel",
                "level": console_loglevel
            },
        },
        "loggers": {
            "BitBake": {
                "level": loglevel,
                "handlers": ["BitBake.console", "BitBake.errconsole"],
            }
        },
        "disable_existing_loggers": False
    }

    # Enable the console log file if enabled
    if consolelogfile and not params.options.show_environment and not params.options.show_versions:
        logconfig = bb.msg.mergeLoggingConfig(logconfig, {
                "version": 1,
                "handlers" : {
                    "BitBake.consolelog": {
                        "class": "logging.FileHandler",
                        "formatter": "BitBake.logfileFormatter",
                        "level": loglevel,
                        "filename": consolelogfile,
                    },
                    # Just like verbconsole, anything sent here will go to the
                    # log file, unless it would go to BitBake.consolelog
                    "BitBake.verbconsolelog" : {
                        "class": "logging.FileHandler",
                        "formatter": "BitBake.logfileFormatter",
                        "level": 1,
                        "filename": consolelogfile,
                        "filters": ["BitBake.verbconsolelogFilter"],
                    },
                },
                "filters": {
                    "BitBake.verbconsolelogFilter": {
                        "()": "bb.msg.LogFilterLTLevel",
                        "level": loglevel,
                    },
                },
                "loggers": {
                    "BitBake": {
                        "handlers": ["BitBake.consolelog"],
                    },

                    # Other interesting things that we want to keep an eye on
                    # in the log files in case someone has an issue, but not
                    # necessarily show to the user on the console
                    "BitBake.SigGen.HashEquiv": {
                        "level": "VERBOSE",
                        "handlers": ["BitBake.verbconsolelog"],
                    },
                    "BitBake.RunQueue.HashEquiv": {
                        "level": "VERBOSE",
                        "handlers": ["BitBake.verbconsolelog"],
                    }
                }
            })

        consolelogdirname = os.path.dirname(consolelogfile)
        # `bb.utils.mkdirhier` has this check, but it reports failure using bb.fatal, which logs
        # to the very logger we are trying to set up.
        if '${' in str(consolelogdirname):
            print(
                "FATAL: Directory name {} contains unexpanded bitbake variable. This may cause build failures and WORKDIR pollution.".format(
                    consolelogdirname))
            if '${MACHINE}' in consolelogdirname:
                print("HINT: It looks like you forgot to set MACHINE in local.conf.")

        bb.utils.mkdirhier(consolelogdirname)
        loglink = os.path.join(consolelogdirname, 'console-latest.log')
        bb.utils.remove(loglink)
        try:
            os.symlink(os.path.basename(consolelogfile), loglink)
        except OSError:
            pass

    # Add the logging domains specified by the user on the command line
    for (domainarg, iterator) in groupby(params.debug_domains):
        dlevel = len(tuple(iterator))
        l = logconfig["loggers"].setdefault("BitBake.%s" % domainarg, {})
        l["level"] = logging.DEBUG - dlevel + 1
        l.setdefault("handlers", []).extend(["BitBake.verbconsole"])

    conf = bb.msg.setLoggingConfig(logconfig, logconfigfile)

    if sys.stdin.isatty() and sys.stdout.isatty():
        log_exec_tty = True
    else:
        log_exec_tty = False

    should_print_hyperlinks = sys.stdout.isatty() and os.environ.get('NO_COLOR', '') == ''

    helper = uihelper.BBUIHelper()

    # Look for the specially designated handlers which need to be passed to the
    # terminal handler
    console_handlers = [h for h in conf.config['handlers'].values() if getattr(h, 'is_console', False)]

    bb.utils.set_process_name("KnottyUI")

    if params.options.remote_server and params.options.kill_server:
        server.terminateServer()
        return

    llevel, debug_domains = bb.msg.constructLogOptions()
    try:
        server.runCommand(["setEventMask", server.getEventHandle(), llevel, debug_domains, _evt_list])
    except (BrokenPipeError, EOFError) as e:
        # bitbake-server comms failure
        logger.fatal("Attempting to set event mask: %s", e)
        return 1

    # The logging_tree module is *extremely* helpful in debugging logging
    # domains. Uncomment here to dump the logging tree when bitbake starts
    #import logging_tree
    #logging_tree.printout()

    universe = False
    if not params.observe_only:
        try:
            params.updateFromServer(server)
        except Exception as e:
            logger.fatal("Fetching command line: %s", e)
            return 1
        cmdline = params.parseActions()
        if not cmdline:
            print("Nothing to do.  Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.")
            return 1
        if 'msg' in cmdline and cmdline['msg']:
            logger.error(cmdline['msg'])
            return 1
        if cmdline['action'][0] == "buildTargets" and "universe" in cmdline['action'][1]:
            universe = True

        try:
            ret, error = server.runCommand(cmdline['action'])
        except (BrokenPipeError, EOFError) as e:
            # bitbake-server comms failure
            logger.fatal("Command '{}' failed: %s".format(cmdline), e)
            return 1
        if error:
            logger.error("Command '%s' failed: %s" % (cmdline, error))
            return 1
        elif not ret:
            logger.error("Command '%s' failed: returned %s" % (cmdline, ret))
            return 1


    parseprogress = None
    cacheprogress = None
    main.shutdown = 0
    interrupted = False
    return_value = 0
    errors = 0
    warnings = 0
    taskfailures = {}

    printintervaldelta = 10 * 60 # 10 minutes
    printinterval = printintervaldelta
    pinginterval = 1 * 60 # 1 minute
    lastevent = lastprint = time.time()

    termfilter = tf(main, helper, console_handlers, params.options.quiet)
    atexit.register(termfilter.finish)

    # shutdown levels
    # 0 - normal operation
    # 1 - no new task execution, let current running tasks finish
    # 2 - interrupting currently executing tasks
    # 3 - we're done, exit
    while main.shutdown < 3:
        try:
            if (lastprint + printinterval) <= time.time():
                termfilter.keepAlive(printinterval)
                printinterval += printintervaldelta
            event = eventHandler.waitEvent(0)
            if event is None:
                if (lastevent + pinginterval) <= time.time():
                    ret, error = server.runCommand(["ping"])
                    if error or not ret:
                        termfilter.clearFooter()
                        print("No reply after pinging server (%s, %s), exiting." % (str(error), str(ret)))
                        return_value = 3
                        main.shutdown = 3
                    lastevent = time.time()
                if not parseprogress:
                    termfilter.updateFooter()
                event = eventHandler.waitEvent(0.25)
                if event is None:
                    continue
            lastevent = time.time()
            helper.eventHandler(event)
            if isinstance(event, bb.runqueue.runQueueExitWait):
                if not main.shutdown:
                    main.shutdown = 1
                continue
            if isinstance(event, bb.event.LogExecTTY):
                if log_exec_tty:
                    tries = event.retries
                    while tries:
                        print("Trying to run: %s" % event.prog)
                        if os.system(event.prog) == 0:
                            break
                        time.sleep(event.sleep_delay)
                        tries -= 1
                    if tries:
                        continue
                logger.warning(event.msg)
                continue

            if isinstance(event, logging.LogRecord):
                lastprint = time.time()
                printinterval = printintervaldelta
                if event.levelno >= bb.msg.BBLogFormatter.ERRORONCE:
                    errors = errors + 1
                    return_value = 1
                elif event.levelno == bb.msg.BBLogFormatter.WARNING:
                    warnings = warnings + 1

                if event.taskpid != 0:
                    # For "normal" logging conditions, don't show note logs from tasks
                    # but do show them if the user has changed the default log level to
                    # include verbose/debug messages
                    if event.levelno <= bb.msg.BBLogFormatter.NOTE and (event.levelno < llevel or (event.levelno == bb.msg.BBLogFormatter.NOTE and llevel != bb.msg.BBLogFormatter.VERBOSE)):
                        continue

                    # Prefix task messages with recipe/task
                    if event.taskpid in helper.pidmap and event.levelno not in [bb.msg.BBLogFormatter.PLAIN, bb.msg.BBLogFormatter.WARNONCE, bb.msg.BBLogFormatter.ERRORONCE]:
                        taskinfo = helper.running_tasks[helper.pidmap[event.taskpid]]
                        event.msg = taskinfo['title'] + ': ' + event.msg
                if hasattr(event, 'fn') and event.levelno not in [bb.msg.BBLogFormatter.WARNONCE, bb.msg.BBLogFormatter.ERRORONCE]:
                    event.msg = event.fn + ': ' + event.msg
                logging.getLogger(event.name).handle(event)
                continue

            if isinstance(event, bb.build.TaskFailedSilent):
                logger.warning("Logfile for failed setscene task is %s" % event.logfile)
                continue
            if isinstance(event, bb.build.TaskFailed):
                return_value = 1
                print_event_log(event, includelogs, loglines, termfilter)
                k = "{}:{}".format(event._fn, event._task)
                taskfailures[k] = event.logfile
            if isinstance(event, bb.build.TaskBase):
                logger.info(event._message)
                continue
            if isinstance(event, bb.event.ParseStarted):
                if params.options.quiet > 1:
                    continue
                if event.total == 0:
                    continue
                termfilter.clearFooter()
                parseprogress = new_progress("Parsing recipes", event.total).start()
                continue
            if isinstance(event, bb.event.ParseProgress):
                if params.options.quiet > 1:
                    continue
                if parseprogress:
                    parseprogress.update(event.current)
                else:
                    bb.warn("Got ParseProgress event for parsing that never started?")
                continue
            if isinstance(event, bb.event.ParseCompleted):
                if params.options.quiet > 1:
                    continue
                if not parseprogress:
                    continue
                parseprogress.finish()
                parseprogress = None
                if params.options.quiet == 0:
                    print(("Parsing of %d .bb files complete (%d cached, %d parsed). %d targets, %d skipped, %d masked, %d errors."
                        % ( event.total, event.cached, event.parsed, event.virtuals, event.skipped, event.masked, event.errors)))
                continue

            if isinstance(event, bb.event.CacheLoadStarted):
                if params.options.quiet > 1:
                    continue
                cacheprogress = new_progress("Loading cache", event.total).start()
                continue
            if isinstance(event, bb.event.CacheLoadProgress):
                if params.options.quiet > 1:
                    continue
                cacheprogress.update(event.current)
                continue
            if isinstance(event, bb.event.CacheLoadCompleted):
                if params.options.quiet > 1:
                    continue
                cacheprogress.finish()
                if params.options.quiet == 0:
                    print("Loaded %d entries from dependency cache." % event.num_entries)
                continue

            if isinstance(event, bb.command.CommandFailed):
                return_value = event.exitcode
                if event.error:
                    errors = errors + 1
                    logger.error(str(event))
                main.shutdown = 3
                continue
            if isinstance(event, bb.command.CommandExit):
                if not return_value:
                    return_value = event.exitcode
                main.shutdown = 3
                continue
            if isinstance(event, (bb.command.CommandCompleted, bb.cooker.CookerExit)):
                main.shutdown = 3
                continue
            if isinstance(event, bb.event.MultipleProviders):
                logger.info(str(event))
                continue
            if isinstance(event, bb.event.NoProvider):
                # For universe builds, only show these as warnings, not errors
                if not universe:
                    return_value = 1
                    errors = errors + 1
                    logger.error(str(event))
                else:
                    logger.warning(str(event))
                continue

            if isinstance(event, bb.runqueue.sceneQueueTaskStarted):
                logger.info("Running setscene task %d of %d (%s)" % (event.stats.setscene_covered + event.stats.setscene_active + event.stats.setscene_notcovered + 1, event.stats.setscene_total, event.taskstring))
                continue

            if isinstance(event, bb.runqueue.runQueueTaskStarted):
                if event.noexec:
                    tasktype = 'noexec task'
                else:
                    tasktype = 'task'
                logger.info("Running %s %d of %d (%s)",
                            tasktype,
                            event.stats.completed + event.stats.active +
                                event.stats.failed + 1,
                            event.stats.total, event.taskstring)
                continue

            if isinstance(event, bb.runqueue.runQueueTaskFailed):
                return_value = 1
                taskfailures.setdefault(event.taskstring)
                logger.error(str(event))
                continue

            if isinstance(event, bb.runqueue.sceneQueueTaskFailed):
                logger.warning(str(event))
                continue

            if isinstance(event, bb.event.DepTreeGenerated):
                continue

            if isinstance(event, bb.event.ProcessStarted):
                if params.options.quiet > 1:
                    continue
                termfilter.clearFooter()
                parseprogress = new_progress(event.processname, event.total)
                parseprogress.start(False)
                continue
            if isinstance(event, bb.event.ProcessProgress):
                if params.options.quiet > 1:
                    continue
                if parseprogress:
                    parseprogress.update(event.progress)
                else:
                    bb.warn("Got ProcessProgress event for someting that never started?")
                continue
            if isinstance(event, bb.event.ProcessFinished):
                if params.options.quiet > 1:
                    continue
                if parseprogress:
                    parseprogress.finish()
                parseprogress = None
                continue

            # ignore
            if isinstance(event, (bb.event.BuildBase,
                                  bb.event.MetadataEvent,
                                  bb.event.ConfigParsed,
                                  bb.event.MultiConfigParsed,
                                  bb.event.RecipeParsed,
                                  bb.event.RecipePreFinalise,
                                  bb.runqueue.runQueueEvent,
                                  bb.event.OperationStarted,
                                  bb.event.OperationCompleted,
                                  bb.event.OperationProgress,
                                  bb.event.DiskFull,
                                  bb.event.HeartbeatEvent,
                                  bb.build.TaskProgress)):
                continue

            logger.error("Unknown event: %s", event)

        except (BrokenPipeError, EOFError) as e:
            # bitbake-server comms failure, don't attempt further comms and exit
            logger.fatal("Executing event: %s", e)
            return_value = 1
            errors = errors + 1
            main.shutdown = 3
        except EnvironmentError as ioerror:
            termfilter.clearFooter()
            # ignore interrupted io
            if ioerror.args[0] == 4:
                continue
            sys.stderr.write(str(ioerror))
            main.shutdown = 2
            if not params.observe_only:
                try:
                    _, error = server.runCommand(["stateForceShutdown"])
                except (BrokenPipeError, EOFError) as e:
                    # bitbake-server comms failure, don't attempt further comms and exit
                    logger.fatal("Unable to force shutdown: %s", e)
                    main.shutdown = 3
        except KeyboardInterrupt:
            termfilter.clearFooter()
            if params.observe_only:
                print("\nKeyboard Interrupt, exiting observer...")
                main.shutdown = 2

            def state_force_shutdown():
                print("\nSecond Keyboard Interrupt, stopping...\n")
                try:
                    _, error = server.runCommand(["stateForceShutdown"])
                    if error:
                        logger.error("Unable to cleanly stop: %s" % error)
                except (BrokenPipeError, EOFError) as e:
                    # bitbake-server comms failure
                    logger.fatal("Unable to cleanly stop: %s", e)

            if not params.observe_only and main.shutdown == 1:
                state_force_shutdown()

            if not params.observe_only and main.shutdown == 0:
                print("\nKeyboard Interrupt, closing down...\n")
                interrupted = True
                # Capture the second KeyboardInterrupt during stateShutdown is running
                try:
                    _, error = server.runCommand(["stateShutdown"])
                    if error:
                        logger.error("Unable to cleanly shutdown: %s" % error)
                except (BrokenPipeError, EOFError) as e:
                    # bitbake-server comms failure
                    logger.fatal("Unable to cleanly shutdown: %s", e)
                except KeyboardInterrupt:
                    state_force_shutdown()

            main.shutdown = main.shutdown + 1
        except Exception as e:
            import traceback
            sys.stderr.write(traceback.format_exc())
            main.shutdown = 2
            if not params.observe_only:
                try:
                    _, error = server.runCommand(["stateForceShutdown"])
                except (BrokenPipeError, EOFError) as e:
                    # bitbake-server comms failure, don't attempt further comms and exit
                    logger.fatal("Unable to force shutdown: %s", e)
                    main.shudown = 3
            return_value = 1
    try:
        termfilter.clearFooter()
        summary = ""
        def format_hyperlink(url, link_text):
            if should_print_hyperlinks:
                start = f'\033]8;;{url}\033\\'
                end = '\033]8;;\033\\'
                return f'{start}{link_text}{end}'
            return link_text

        if taskfailures:
            summary += pluralise("\nSummary: %s task failed:",
                                 "\nSummary: %s tasks failed:", len(taskfailures))
            for (failure, log_file) in taskfailures.items():
                summary += "\n  %s" % failure
                if log_file:
                    hyperlink = format_hyperlink(f"file://{log_file}", log_file)
                    summary += "\n    log: {}".format(hyperlink)
        if warnings:
            summary += pluralise("\nSummary: There was %s WARNING message.",
                                 "\nSummary: There were %s WARNING messages.", warnings)
        if return_value and errors:
            summary += pluralise("\nSummary: There was %s ERROR message, returning a non-zero exit code.",
                                 "\nSummary: There were %s ERROR messages, returning a non-zero exit code.", errors)
        if summary and params.options.quiet == 0:
            print(summary)

        if interrupted:
            print("Execution was interrupted, returning a non-zero exit code.")
            if return_value == 0:
                return_value = 1
    except IOError as e:
        import errno
        if e.errno == errno.EPIPE:
            pass

    logging.shutdown()

    return return_value
