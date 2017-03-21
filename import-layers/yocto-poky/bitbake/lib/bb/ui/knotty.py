#
# BitBake (No)TTY UI Implementation
#
# Handling output to TTYs or files (no TTY)
#
# Copyright (C) 2006-2012 Richard Purdie
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

from __future__ import division

import os
import sys
import xmlrpc.client as xmlrpclib
import logging
import progressbar
import signal
import bb.msg
import time
import fcntl
import struct
import copy
import atexit

from bb.ui import uihelper

featureSet = [bb.cooker.CookerFeatures.SEND_SANITYEVENTS]

logger = logging.getLogger("BitBake")
interactive = sys.stdout.isatty()

class BBProgress(progressbar.ProgressBar):
    def __init__(self, msg, maxval, widgets=None, extrapos=-1, resize_handler=None):
        self.msg = msg
        self.extrapos = extrapos
        if not widgets:
            widgets = [progressbar.Percentage(), ' ', progressbar.Bar(), ' ',
            progressbar.ETA()]
            self.extrapos = 4

        if resize_handler:
            self._resize_default = resize_handler
        else:
            self._resize_default = signal.getsignal(signal.SIGWINCH)
        progressbar.ProgressBar.__init__(self, maxval, [self.msg + ": "] + widgets, fd=sys.stdout)

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
                if extrastr[-1] != ' ':
                    extrastr += ' '
            else:
                extrastr = ' '
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
    if(qty == 1):
        return singular % qty
    else:
        return plural % qty


class InteractConsoleLogFilter(logging.Filter):
    def __init__(self, tf, format):
        self.tf = tf
        self.format = format

    def filter(self, record):
        if record.levelno == self.format.NOTE and (record.msg.startswith("Running") or record.msg.startswith("recipe ")):
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
                cr = (env['LINES'], env['COLUMNS'])
            except:
                cr = (25, 80)
        return cr

    def __init__(self, main, helper, console, errconsole, format, quiet):
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
            if curses.tigetnum("colors") > 2:
                format.enable_color()
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
        console.addFilter(InteractConsoleLogFilter(self, format))
        errconsole.addFilter(InteractConsoleLogFilter(self, format))

        self.main_progress = None

    def clearFooter(self):
        if self.footer_present:
            lines = self.footer_present
            sys.stdout.buffer.write(self.curses.tparm(self.cuu, lines))
            sys.stdout.buffer.write(self.curses.tparm(self.ed))
            sys.stdout.flush()
        self.footer_present = False

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
            progress = activetasks[t].get("progress", None)
            if progress is not None:
                pbar = activetasks[t].get("progressbar", None)
                rate = activetasks[t].get("rate", None)
                start_time = activetasks[t].get("starttime", None)
                if not pbar or pbar.bouncing != (progress < 0):
                    if progress < 0:
                        pbar = BBProgress("0: %s (pid %s) " % (activetasks[t]["title"], t), 100, widgets=[progressbar.BouncingSlider(), ''], extrapos=2, resize_handler=self.sigwinch_handle)
                        pbar.bouncing = True
                    else:
                        pbar = BBProgress("0: %s (pid %s) " % (activetasks[t]["title"], t), 100, widgets=[progressbar.Percentage(), ' ', progressbar.Bar(), ''], extrapos=4, resize_handler=self.sigwinch_handle)
                        pbar.bouncing = False
                    activetasks[t]["progressbar"] = pbar
                tasks.append((pbar, progress, rate, start_time))
            else:
                start_time = activetasks[t].get("starttime", None)
                if start_time:
                    tasks.append("%s - %ds (pid %s)" % (activetasks[t]["title"], currenttime - start_time, t))
                else:
                    tasks.append("%s (pid %s)" % (activetasks[t]["title"], t))

        if self.main.shutdown:
            content = "Waiting for %s running tasks to finish:" % len(activetasks)
            print(content)
        else:
            if self.quiet:
                content = "Running tasks (%s of %s)" % (self.helper.tasknumber_current, self.helper.tasknumber_total)
            elif not len(activetasks):
                content = "No currently running tasks (%s of %s)" % (self.helper.tasknumber_current, self.helper.tasknumber_total)
            else:
                content = "Currently %2s running tasks (%s of %s)" % (len(activetasks), self.helper.tasknumber_current, self.helper.tasknumber_total)
            maxtask = self.helper.tasknumber_total
            if not self.main_progress or self.main_progress.maxval != maxtask:
                widgets = [' ', progressbar.Percentage(), ' ', progressbar.Bar()]
                self.main_progress = BBProgress("Running tasks", maxtask, widgets=widgets, resize_handler=self.sigwinch_handle)
                self.main_progress.start(False)
            self.main_progress.setmessage(content)
            progress = self.helper.tasknumber_current - 1
            if progress < 0:
                progress = 0
            content = self.main_progress.update(progress)
            print('')
        lines = 1 + int(len(content) / (self.columns + 1))
        if not self.quiet:
            for tasknum, task in enumerate(tasks[:(self.rows - 2)]):
                if isinstance(task, tuple):
                    pbar, progress, rate, start_time = task
                    if not pbar.start_time:
                        pbar.start(False)
                        if start_time:
                            pbar.start_time = start_time
                    pbar.setmessage('%s:%s' % (tasknum, pbar.msg.split(':', 1)[1]))
                    if progress > -1:
                        pbar.setextra(rate)
                        content = pbar.update(progress)
                    else:
                        content = pbar.update(1)
                    print('')
                else:
                    content = "%s: %s" % (tasknum, task)
                    print(content)
                lines = lines + 1 + int(len(content) / (self.columns + 1))
        self.footer_present = lines
        self.lastpids = runningpids[:]
        self.lastcount = self.helper.tasknumber_current

    def finish(self):
        if self.stdinbackup:
            fd = sys.stdin.fileno()
            self.termios.tcsetattr(fd, self.termios.TCSADRAIN, self.stdinbackup)

def _log_settings_from_server(server):
    # Get values of variables which control our output
    includelogs, error = server.runCommand(["getVariable", "BBINCLUDELOGS"])
    if error:
        logger.error("Unable to get the value of BBINCLUDELOGS variable: %s" % error)
        raise BaseException(error)
    loglines, error = server.runCommand(["getVariable", "BBINCLUDELOGS_LINES"])
    if error:
        logger.error("Unable to get the value of BBINCLUDELOGS_LINES variable: %s" % error)
        raise BaseException(error)
    consolelogfile, error = server.runCommand(["getSetVariable", "BB_CONSOLELOG"])
    if error:
        logger.error("Unable to get the value of BB_CONSOLELOG variable: %s" % error)
        raise BaseException(error)
    return includelogs, loglines, consolelogfile

_evt_list = [ "bb.runqueue.runQueueExitWait", "bb.event.LogExecTTY", "logging.LogRecord",
              "bb.build.TaskFailed", "bb.build.TaskBase", "bb.event.ParseStarted",
              "bb.event.ParseProgress", "bb.event.ParseCompleted", "bb.event.CacheLoadStarted",
              "bb.event.CacheLoadProgress", "bb.event.CacheLoadCompleted", "bb.command.CommandFailed",
              "bb.command.CommandExit", "bb.command.CommandCompleted",  "bb.cooker.CookerExit",
              "bb.event.MultipleProviders", "bb.event.NoProvider", "bb.runqueue.sceneQueueTaskStarted",
              "bb.runqueue.runQueueTaskStarted", "bb.runqueue.runQueueTaskFailed", "bb.runqueue.sceneQueueTaskFailed",
              "bb.event.BuildBase", "bb.build.TaskStarted", "bb.build.TaskSucceeded", "bb.build.TaskFailedSilent",
              "bb.build.TaskProgress", "bb.event.ProcessStarted", "bb.event.ProcessProgress", "bb.event.ProcessFinished"]

def main(server, eventHandler, params, tf = TerminalFilter):

    includelogs, loglines, consolelogfile = _log_settings_from_server(server)

    if sys.stdin.isatty() and sys.stdout.isatty():
        log_exec_tty = True
    else:
        log_exec_tty = False

    helper = uihelper.BBUIHelper()

    console = logging.StreamHandler(sys.stdout)
    errconsole = logging.StreamHandler(sys.stderr)
    format_str = "%(levelname)s: %(message)s"
    format = bb.msg.BBLogFormatter(format_str)
    if params.options.quiet:
        bb.msg.addDefaultlogFilter(console, bb.msg.BBLogFilterStdOut, bb.msg.BBLogFormatter.WARNING)
    else:
        bb.msg.addDefaultlogFilter(console, bb.msg.BBLogFilterStdOut)
    bb.msg.addDefaultlogFilter(errconsole, bb.msg.BBLogFilterStdErr)
    console.setFormatter(format)
    errconsole.setFormatter(format)
    logger.addHandler(console)
    logger.addHandler(errconsole)

    bb.utils.set_process_name("KnottyUI")

    if params.options.remote_server and params.options.kill_server:
        server.terminateServer()
        return

    consolelog = None
    if consolelogfile and not params.options.show_environment and not params.options.show_versions:
        bb.utils.mkdirhier(os.path.dirname(consolelogfile))
        conlogformat = bb.msg.BBLogFormatter(format_str)
        consolelog = logging.FileHandler(consolelogfile)
        bb.msg.addDefaultlogFilter(consolelog)
        consolelog.setFormatter(conlogformat)
        logger.addHandler(consolelog)
        loglink = os.path.join(os.path.dirname(consolelogfile), 'console-latest.log')
        bb.utils.remove(loglink)
        try:
           os.symlink(os.path.basename(consolelogfile), loglink)
        except OSError:
           pass

    llevel, debug_domains = bb.msg.constructLogOptions()
    server.runCommand(["setEventMask", server.getEventHandle(), llevel, debug_domains, _evt_list])

    universe = False
    if not params.observe_only:
        params.updateFromServer(server)
        params.updateToServer(server, os.environ.copy())
        cmdline = params.parseActions()
        if not cmdline:
            print("Nothing to do.  Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.")
            return 1
        if 'msg' in cmdline and cmdline['msg']:
            logger.error(cmdline['msg'])
            return 1
        if cmdline['action'][0] == "buildTargets" and "universe" in cmdline['action'][1]:
            universe = True

        ret, error = server.runCommand(cmdline['action'])
        if error:
            logger.error("Command '%s' failed: %s" % (cmdline, error))
            return 1
        elif ret != True:
            logger.error("Command '%s' failed: returned %s" % (cmdline, ret))
            return 1


    parseprogress = None
    cacheprogress = None
    main.shutdown = 0
    interrupted = False
    return_value = 0
    errors = 0
    warnings = 0
    taskfailures = []

    termfilter = tf(main, helper, console, errconsole, format, params.options.quiet)
    atexit.register(termfilter.finish)

    while True:
        try:
            event = eventHandler.waitEvent(0)
            if event is None:
                if main.shutdown > 1:
                    break
                termfilter.updateFooter()
                event = eventHandler.waitEvent(0.25)
                if event is None:
                    continue
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
                if event.levelno >= format.ERROR:
                    errors = errors + 1
                    return_value = 1
                elif event.levelno == format.WARNING:
                    warnings = warnings + 1

                if event.taskpid != 0:
                    # For "normal" logging conditions, don't show note logs from tasks
                    # but do show them if the user has changed the default log level to
                    # include verbose/debug messages
                    if event.levelno <= format.NOTE and (event.levelno < llevel or (event.levelno == format.NOTE and llevel != format.VERBOSE)):
                        continue

                    # Prefix task messages with recipe/task
                    if event.taskpid in helper.running_tasks:
                        taskinfo = helper.running_tasks[event.taskpid]
                        event.msg = taskinfo['title'] + ': ' + event.msg
                if hasattr(event, 'fn'):
                        event.msg = event.fn + ': ' + event.msg
                logger.handle(event)
                continue

            if isinstance(event, bb.build.TaskFailedSilent):
                logger.warning("Logfile for failed setscene task is %s" % event.logfile)
                continue
            if isinstance(event, bb.build.TaskFailed):
                return_value = 1
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
            if isinstance(event, bb.build.TaskBase):
                logger.info(event._message)
                continue
            if isinstance(event, bb.event.ParseStarted):
                if event.total == 0:
                    continue
                parseprogress = new_progress("Parsing recipes", event.total).start()
                continue
            if isinstance(event, bb.event.ParseProgress):
                if parseprogress:
                    parseprogress.update(event.current)
                else:
                    bb.warn("Got ParseProgress event for parsing that never started?")
                continue
            if isinstance(event, bb.event.ParseCompleted):
                if not parseprogress:
                    continue
                parseprogress.finish()
                pasreprogress = None
                if not params.options.quiet:
                    print(("Parsing of %d .bb files complete (%d cached, %d parsed). %d targets, %d skipped, %d masked, %d errors."
                        % ( event.total, event.cached, event.parsed, event.virtuals, event.skipped, event.masked, event.errors)))
                continue

            if isinstance(event, bb.event.CacheLoadStarted):
                cacheprogress = new_progress("Loading cache", event.total).start()
                continue
            if isinstance(event, bb.event.CacheLoadProgress):
                cacheprogress.update(event.current)
                continue
            if isinstance(event, bb.event.CacheLoadCompleted):
                cacheprogress.finish()
                if not params.options.quiet:
                    print("Loaded %d entries from dependency cache." % event.num_entries)
                continue

            if isinstance(event, bb.command.CommandFailed):
                return_value = event.exitcode
                if event.error:
                    errors = errors + 1
                    logger.error("Command execution failed: %s", event.error)
                main.shutdown = 2
                continue
            if isinstance(event, bb.command.CommandExit):
                if not return_value:
                    return_value = event.exitcode
                continue
            if isinstance(event, (bb.command.CommandCompleted, bb.cooker.CookerExit)):
                main.shutdown = 2
                continue
            if isinstance(event, bb.event.MultipleProviders):
                logger.info("multiple providers are available for %s%s (%s)", event._is_runtime and "runtime " or "",
                            event._item,
                            ", ".join(event._candidates))
                rtime = ""
                if event._is_runtime:
                    rtime = "R"
                logger.info("consider defining a PREFERRED_%sPROVIDER entry to match %s" % (rtime, event._item))
                continue
            if isinstance(event, bb.event.NoProvider):
                if event._runtime:
                    r = "R"
                else:
                    r = ""

                extra = ''
                if not event._reasons:
                    if event._close_matches:
                        extra = ". Close matches:\n  %s" % '\n  '.join(event._close_matches)

                # For universe builds, only show these as warnings, not errors
                h = logger.warning
                if not universe:
                    return_value = 1
                    errors = errors + 1
                    h = logger.error

                if event._dependees:
                    h("Nothing %sPROVIDES '%s' (but %s %sDEPENDS on or otherwise requires it)%s", r, event._item, ", ".join(event._dependees), r, extra)
                else:
                    h("Nothing %sPROVIDES '%s'%s", r, event._item, extra)
                if event._reasons:
                    for reason in event._reasons:
                        h("%s", reason)
                continue

            if isinstance(event, bb.runqueue.sceneQueueTaskStarted):
                logger.info("Running setscene task %d of %d (%s)" % (event.stats.completed + event.stats.active + event.stats.failed + 1, event.stats.total, event.taskstring))
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
                taskfailures.append(event.taskstring)
                logger.error("Task (%s) failed with exit code '%s'",
                             event.taskstring, event.exitcode)
                continue

            if isinstance(event, bb.runqueue.sceneQueueTaskFailed):
                logger.warning("Setscene task (%s) failed with exit code '%s' - real task will be run instead",
                               event.taskstring, event.exitcode)
                continue

            if isinstance(event, bb.event.DepTreeGenerated):
                continue

            if isinstance(event, bb.event.ProcessStarted):
                parseprogress = new_progress(event.processname, event.total)
                parseprogress.start(False)
                continue
            if isinstance(event, bb.event.ProcessProgress):
                if parseprogress:
                    parseprogress.update(event.progress)
                else:
                    bb.warn("Got ProcessProgress event for someting that never started?")
                continue
            if isinstance(event, bb.event.ProcessFinished):
                if parseprogress:
                    parseprogress.finish()
                parseprogress = None
                continue

            # ignore
            if isinstance(event, (bb.event.BuildBase,
                                  bb.event.MetadataEvent,
                                  bb.event.StampUpdate,
                                  bb.event.ConfigParsed,
                                  bb.event.RecipeParsed,
                                  bb.event.RecipePreFinalise,
                                  bb.runqueue.runQueueEvent,
                                  bb.event.OperationStarted,
                                  bb.event.OperationCompleted,
                                  bb.event.OperationProgress,
                                  bb.event.DiskFull,
                                  bb.build.TaskProgress)):
                continue

            logger.error("Unknown event: %s", event)

        except EnvironmentError as ioerror:
            termfilter.clearFooter()
            # ignore interrupted io
            if ioerror.args[0] == 4:
                continue
            sys.stderr.write(str(ioerror))
            if not params.observe_only:
                _, error = server.runCommand(["stateForceShutdown"])
            main.shutdown = 2
        except KeyboardInterrupt:
            termfilter.clearFooter()
            if params.observe_only:
                print("\nKeyboard Interrupt, exiting observer...")
                main.shutdown = 2
            if not params.observe_only and main.shutdown == 1:
                print("\nSecond Keyboard Interrupt, stopping...\n")
                _, error = server.runCommand(["stateForceShutdown"])
                if error:
                    logger.error("Unable to cleanly stop: %s" % error)
            if not params.observe_only and main.shutdown == 0:
                print("\nKeyboard Interrupt, closing down...\n")
                interrupted = True
                _, error = server.runCommand(["stateShutdown"])
                if error:
                    logger.error("Unable to cleanly shutdown: %s" % error)
            main.shutdown = main.shutdown + 1
            pass
        except Exception as e:
            import traceback
            sys.stderr.write(traceback.format_exc())
            if not params.observe_only:
                _, error = server.runCommand(["stateForceShutdown"])
            main.shutdown = 2
            return_value = 1
    try:
        termfilter.clearFooter()
        summary = ""
        if taskfailures:
            summary += pluralise("\nSummary: %s task failed:",
                                 "\nSummary: %s tasks failed:", len(taskfailures))
            for failure in taskfailures:
                summary += "\n  %s" % failure
        if warnings:
            summary += pluralise("\nSummary: There was %s WARNING message shown.",
                                 "\nSummary: There were %s WARNING messages shown.", warnings)
        if return_value and errors:
            summary += pluralise("\nSummary: There was %s ERROR message shown, returning a non-zero exit code.",
                                 "\nSummary: There were %s ERROR messages shown, returning a non-zero exit code.", errors)
        if summary and not params.options.quiet:
            print(summary)

        if interrupted:
            print("Execution was interrupted, returning a non-zero exit code.")
            if return_value == 0:
                return_value = 1
    except IOError as e:
        import errno
        if e.errno == errno.EPIPE:
            pass

    if consolelog:
        logger.removeHandler(consolelog)
        consolelog.close()

    return return_value
