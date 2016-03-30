#
# BitBake ToasterUI Implementation
# based on (No)TTY UI Implementation by Richard Purdie
#
# Handling output to TTYs or files (no TTY)
#
# Copyright (C) 2006-2012 Richard Purdie
# Copyright (C) 2013      Intel Corporation
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
import time
import sys
try:
    import bb
except RuntimeError as exc:
    sys.exit(str(exc))

from bb.ui import uihelper
from bb.ui.buildinfohelper import BuildInfoHelper

import bb.msg
import logging
import os

# pylint: disable=invalid-name
# module properties for UI modules are read by bitbake and the contract should not be broken


featureSet = [bb.cooker.CookerFeatures.HOB_EXTRA_CACHES, bb.cooker.CookerFeatures.SEND_DEPENDS_TREE, bb.cooker.CookerFeatures.BASEDATASTORE_TRACKING, bb.cooker.CookerFeatures.SEND_SANITYEVENTS]

logger = logging.getLogger("ToasterLogger")
interactive = sys.stdout.isatty()

def _log_settings_from_server(server):
    # Get values of variables which control our output
    includelogs, error = server.runCommand(["getVariable", "BBINCLUDELOGS"])
    if error:
        logger.error("Unable to get the value of BBINCLUDELOGS variable: %s", error)
        raise BaseException(error)
    loglines, error = server.runCommand(["getVariable", "BBINCLUDELOGS_LINES"])
    if error:
        logger.error("Unable to get the value of BBINCLUDELOGS_LINES variable: %s", error)
        raise BaseException(error)
    consolelogfile, error = server.runCommand(["getVariable", "BB_CONSOLELOG"])
    if error:
        logger.error("Unable to get the value of BB_CONSOLELOG variable: %s", error)
        raise BaseException(error)
    return consolelogfile

# create a log file for a single build and direct the logger at it;
# log file name is timestamped to the millisecond (depending
# on system clock accuracy) to ensure it doesn't overlap with
# other log file names
#
# returns (log file, path to log file) for a build
def _open_build_log(log_dir):
    format_str = "%(levelname)s: %(message)s"

    now = time.time()
    now_ms = int((now - int(now)) * 1000)
    time_str = time.strftime('build_%Y%m%d_%H%M%S', time.localtime(now))
    log_file_name = time_str + ('.%d.log' % now_ms)
    build_log_file_path = os.path.join(log_dir, log_file_name)

    build_log = logging.FileHandler(build_log_file_path)

    logformat = bb.msg.BBLogFormatter(format_str)
    build_log.setFormatter(logformat)

    bb.msg.addDefaultlogFilter(build_log)
    logger.addHandler(build_log)

    return (build_log, build_log_file_path)

# stop logging to the build log if it exists
def _close_build_log(build_log):
    if build_log:
        build_log.flush()
        build_log.close()
        logger.removeHandler(build_log)

def main(server, eventHandler, params):
    # set to a logging.FileHandler instance when a build starts;
    # see _open_build_log()
    build_log = None

    # set to the log path when a build starts
    build_log_file_path = None

    helper = uihelper.BBUIHelper()

    # TODO don't use log output to determine when bitbake has started
    #
    # WARNING: this log handler cannot be removed, as localhostbecontroller
    # relies on output in the toaster_ui.log file to determine whether
    # the bitbake server has started, which only happens if
    # this logger is setup here (see the TODO in the loop below)
    console = logging.StreamHandler(sys.stdout)
    format_str = "%(levelname)s: %(message)s"
    formatter = bb.msg.BBLogFormatter(format_str)
    bb.msg.addDefaultlogFilter(console)
    console.setFormatter(formatter)
    logger.addHandler(console)
    logger.setLevel(logging.INFO)

    # verify and warn
    build_history_enabled = True
    inheritlist, _ = server.runCommand(["getVariable", "INHERIT"])

    if not "buildhistory" in inheritlist.split(" "):
        logger.warn("buildhistory is not enabled. Please enable INHERIT += \"buildhistory\" to see image details.")
        build_history_enabled = False

    if not params.observe_only:
        logger.error("ToasterUI can only work in observer mode")
        return 1

    # set to 1 when toasterui needs to shut down
    main.shutdown = 0

    interrupted = False
    return_value = 0
    errors = 0
    warnings = 0
    taskfailures = []
    first = True

    buildinfohelper = BuildInfoHelper(server, build_history_enabled)

    # write our own log files into bitbake's log directory;
    # we're only interested in the path to the parent directory of
    # this file, as we're writing our own logs into the same directory
    consolelogfile = _log_settings_from_server(server)
    log_dir = os.path.dirname(consolelogfile)
    bb.utils.mkdirhier(log_dir)

    while True:
        try:
            event = eventHandler.waitEvent(0.25)
            if first:
                first = False

                # TODO don't use log output to determine when bitbake has started
                #
                # this is the line localhostbecontroller needs to
                # see in toaster_ui.log which it uses to decide whether
                # the bitbake server has started...
                logger.info("ToasterUI waiting for events")

            if event is None:
                if main.shutdown > 0:
                    # if shutting down, close any open build log first
                    _close_build_log(build_log)

                    break
                continue

            helper.eventHandler(event)

            # pylint: disable=protected-access
            # the code will look into the protected variables of the event; no easy way around this

            # we treat ParseStarted as the first event of toaster-triggered
            # builds; that way we get the Build Configuration included in the log
            # and any errors that occur before BuildStarted is fired
            if isinstance(event, bb.event.ParseStarted):
                if not (build_log and build_log_file_path):
                    build_log, build_log_file_path = _open_build_log(log_dir)
                continue

            if isinstance(event, bb.event.BuildStarted):
                # command-line builds don't fire a ParseStarted event,
                # so we have to start the log file for those on BuildStarted instead
                if not (build_log and build_log_file_path):
                    build_log, build_log_file_path = _open_build_log(log_dir)

                buildinfohelper.store_started_build(event, build_log_file_path)

            if isinstance(event, (bb.build.TaskStarted, bb.build.TaskSucceeded, bb.build.TaskFailedSilent)):
                buildinfohelper.update_and_store_task(event)
                logger.info("Logfile for task %s", event.logfile)
                continue

            if isinstance(event, bb.build.TaskBase):
                logger.info(event._message)

            if isinstance(event, bb.event.LogExecTTY):
                logger.info(event.msg)
                continue

            if isinstance(event, logging.LogRecord):
                if event.levelno == -1:
                    event.levelno = formatter.ERROR

                buildinfohelper.store_log_event(event)

                if event.levelno >= formatter.ERROR:
                    errors = errors + 1
                elif event.levelno == formatter.WARNING:
                    warnings = warnings + 1

                # For "normal" logging conditions, don't show note logs from tasks
                # but do show them if the user has changed the default log level to
                # include verbose/debug messages
                if event.taskpid != 0 and event.levelno <= formatter.NOTE:
                    continue

                logger.handle(event)
                continue

            if isinstance(event, bb.build.TaskFailed):
                buildinfohelper.update_and_store_task(event)
                logfile = event.logfile
                if logfile and os.path.exists(logfile):
                    bb.error("Logfile of failure stored in: %s" % logfile)
                continue

            # these events are unprocessed now, but may be used in the future to log
            # timing and error informations from the parsing phase in Toaster
            if isinstance(event, (bb.event.SanityCheckPassed, bb.event.SanityCheck)):
                continue
            if isinstance(event, bb.event.ParseProgress):
                continue
            if isinstance(event, bb.event.ParseCompleted):
                continue
            if isinstance(event, bb.event.CacheLoadStarted):
                continue
            if isinstance(event, bb.event.CacheLoadProgress):
                continue
            if isinstance(event, bb.event.CacheLoadCompleted):
                continue
            if isinstance(event, bb.event.MultipleProviders):
                logger.info("multiple providers are available for %s%s (%s)", event._is_runtime and "runtime " or "",
                            event._item,
                            ", ".join(event._candidates))
                logger.info("consider defining a PREFERRED_PROVIDER entry to match %s", event._item)
                continue

            if isinstance(event, bb.event.NoProvider):
                errors = errors + 1
                if event._runtime:
                    r = "R"
                else:
                    r = ""

                if event._dependees:
                    text = "Nothing %sPROVIDES '%s' (but %s %sDEPENDS on or otherwise requires it)" % (r, event._item, ", ".join(event._dependees), r)
                else:
                    text = "Nothing %sPROVIDES '%s'" % (r, event._item)

                logger.error(text)
                if event._reasons:
                    for reason in event._reasons:
                        logger.error("%s", reason)
                        text += reason
                buildinfohelper.store_log_error(text)
                continue

            if isinstance(event, bb.event.ConfigParsed):
                continue
            if isinstance(event, bb.event.RecipeParsed):
                continue

            # end of saved events

            if isinstance(event, (bb.runqueue.sceneQueueTaskStarted, bb.runqueue.runQueueTaskStarted, bb.runqueue.runQueueTaskSkipped)):
                buildinfohelper.store_started_task(event)
                continue

            if isinstance(event, bb.runqueue.runQueueTaskCompleted):
                buildinfohelper.update_and_store_task(event)
                continue

            if isinstance(event, bb.runqueue.runQueueTaskFailed):
                buildinfohelper.update_and_store_task(event)
                taskfailures.append(event.taskstring)
                logger.error("Task %s (%s) failed with exit code '%s'",
                             event.taskid, event.taskstring, event.exitcode)
                continue

            if isinstance(event, (bb.runqueue.sceneQueueTaskCompleted, bb.runqueue.sceneQueueTaskFailed)):
                buildinfohelper.update_and_store_task(event)
                continue


            if isinstance(event, (bb.event.TreeDataPreparationStarted, bb.event.TreeDataPreparationCompleted)):
                continue

            if isinstance(event, (bb.event.BuildCompleted, bb.command.CommandFailed)):

                errorcode = 0
                if isinstance(event, bb.command.CommandFailed):
                    errors += 1
                    errorcode = 1
                    logger.error("Command execution failed: %s", event.error)

                # turn off logging to the current build log
                _close_build_log(build_log)

                # reset ready for next BuildStarted
                build_log = None

                # update the build info helper on BuildCompleted, not on CommandXXX
                buildinfohelper.update_build_information(event, errors, warnings, taskfailures)
                buildinfohelper.close(errorcode)
                # mark the log output; controllers may kill the toasterUI after seeing this log
                logger.info("ToasterUI build done 1, brbe: %s", buildinfohelper.brbe )

                # we start a new build info
                if buildinfohelper.brbe is not None:
                    logger.debug("ToasterUI under BuildEnvironment management - exiting after the build")
                    server.terminateServer()
                else:
                    logger.debug("ToasterUI prepared for new build")
                    errors = 0
                    warnings = 0
                    taskfailures = []
                    buildinfohelper = BuildInfoHelper(server, build_history_enabled)

                logger.info("ToasterUI build done 2")
                continue

            if isinstance(event, (bb.command.CommandCompleted,
                                  bb.command.CommandFailed,
                                  bb.command.CommandExit)):
                errorcode = 0

                continue

            if isinstance(event, bb.event.MetadataEvent):
                if event.type == "SinglePackageInfo":
                    buildinfohelper.store_build_package_information(event)
                elif event.type == "LayerInfo":
                    buildinfohelper.store_layer_info(event)
                elif event.type == "BuildStatsList":
                    buildinfohelper.store_tasks_stats(event)
                elif event.type == "ImagePkgList":
                    buildinfohelper.store_target_package_data(event)
                elif event.type == "MissedSstate":
                    buildinfohelper.store_missed_state_tasks(event)
                elif event.type == "ImageFileSize":
                    buildinfohelper.update_target_image_file(event)
                elif event.type == "ArtifactFileSize":
                    buildinfohelper.update_artifact_image_file(event)
                elif event.type == "LicenseManifestPath":
                    buildinfohelper.store_license_manifest_path(event)
                else:
                    logger.error("Unprocessed MetadataEvent %s ", str(event))
                continue

            if isinstance(event, bb.cooker.CookerExit):
                # shutdown when bitbake server shuts down
                main.shutdown = 1
                continue

            # ignore
            if isinstance(event, (bb.event.BuildBase,
                                  bb.event.StampUpdate,
                                  bb.event.RecipePreFinalise,
                                  bb.runqueue.runQueueEvent,
                                  bb.runqueue.runQueueExitWait,
                                  bb.event.OperationProgress,
                                  bb.command.CommandFailed,
                                  bb.command.CommandExit,
                                  bb.command.CommandCompleted,
                                  bb.event.ReachableStamps)):
                continue

            if isinstance(event, bb.event.DepTreeGenerated):
                buildinfohelper.store_dependency_information(event)
                continue

            logger.warn("Unknown event: %s", event)
            return_value += 1

        except EnvironmentError as ioerror:
            # ignore interrupted io
            if ioerror.args[0] == 4:
                pass
        except KeyboardInterrupt:
            main.shutdown = 1
        except Exception as e:
            # print errors to log
            import traceback
            from pprint import pformat
            exception_data = traceback.format_exc()
            logger.error("%s\n%s" , e, exception_data)

            _, _, tb = sys.exc_info()
            if tb is not None:
                curr = tb
                while curr is not None:
                    logger.error("Error data dump %s\n%s\n" , traceback.format_tb(curr,1), pformat(curr.tb_frame.f_locals))
                    curr = curr.tb_next

            # save them to database, if possible; if it fails, we already logged to console.
            try:
                buildinfohelper.store_log_exception("%s\n%s" % (str(e), exception_data))
            except Exception as ce:
                logger.error("CRITICAL - Failed to to save toaster exception to the database: %s", str(ce))

            # make sure we return with an error
            return_value += 1

    if interrupted and return_value == 0:
        return_value += 1

    logger.warn("Return value is %d", return_value)
    return return_value
