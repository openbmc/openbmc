#
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
# Copyright (C) 2006        Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import sys
import logging
import argparse
import warnings
import fcntl
import time
import traceback
import datetime

import bb
from bb import event
import bb.msg
from bb import cooker
from bb import ui
from bb import server
from bb import cookerdata

import bb.server.process
import bb.server.xmlrpcclient

logger = logging.getLogger("BitBake")

class BBMainException(Exception):
    pass

class BBMainFatal(bb.BBHandledException):
    pass

def present_options(optionlist):
    if len(optionlist) > 1:
        return ' or '.join([', '.join(optionlist[:-1]), optionlist[-1]])
    else:
        return optionlist[0]

class BitbakeHelpFormatter(argparse.HelpFormatter):
    def _get_help_string(self, action):
        # We need to do this here rather than in the text we supply to
        # add_option() because we don't want to call list_extension_modules()
        # on every execution (since it imports all of the modules)
        # Note also that we modify option.help rather than the returned text
        # - this is so that we don't have to re-format the text ourselves
        if action.dest == 'ui':
            valid_uis = list_extension_modules(bb.ui, 'main')
            return action.help.replace('@CHOICES@', present_options(valid_uis))

        return action.help

def list_extension_modules(pkg, checkattr):
    """
    Lists extension modules in a specific Python package
    (e.g. UIs, servers). NOTE: Calling this function will import all of the
    submodules of the specified module in order to check for the specified
    attribute; this can have unusual side-effects. As a result, this should
    only be called when displaying help text or error messages.
    Parameters:
        pkg: previously imported Python package to list
        checkattr: attribute to look for in module to determine if it's valid
            as the type of extension you are looking for
    """
    import pkgutil
    pkgdir = os.path.dirname(pkg.__file__)

    modules = []
    for _, modulename, _ in pkgutil.iter_modules([pkgdir]):
        if os.path.isdir(os.path.join(pkgdir, modulename)):
            # ignore directories
            continue
        try:
            module = __import__(pkg.__name__, fromlist=[modulename])
        except:
            # If we can't import it, it's not valid
            continue
        module_if = getattr(module, modulename)
        if getattr(module_if, 'hidden_extension', False):
            continue
        if not checkattr or hasattr(module_if, checkattr):
            modules.append(modulename)
    return modules

def import_extension_module(pkg, modulename, checkattr):
    try:
        # Dynamically load the UI based on the ui name. Although we
        # suggest a fixed set this allows you to have flexibility in which
        # ones are available.
        module = __import__(pkg.__name__, fromlist=[modulename])
        return getattr(module, modulename)
    except AttributeError:
        modules = present_options(list_extension_modules(pkg, checkattr))
        raise BBMainException('FATAL: Unable to import extension module "%s" from %s. '
                              'Valid extension modules: %s' % (modulename, pkg.__name__, modules))

# Display bitbake/OE warnings via the BitBake.Warnings logger, ignoring others"""
warnlog = logging.getLogger("BitBake.Warnings")
_warnings_showwarning = warnings.showwarning
def _showwarning(message, category, filename, lineno, file=None, line=None):
    if file is not None:
        if _warnings_showwarning is not None:
            _warnings_showwarning(message, category, filename, lineno, file, line)
    else:
        s = warnings.formatwarning(message, category, filename, lineno)
        warnlog.warning(s)

warnings.showwarning = _showwarning

def create_bitbake_parser():
    parser = argparse.ArgumentParser(
        description="""\
            It is assumed there is a conf/bblayers.conf available in cwd or in BBPATH which
            will provide the layer, BBFILES and other configuration information.
            """,
        formatter_class=BitbakeHelpFormatter,
        allow_abbrev=False,
        add_help=False, # help is manually added below in a specific argument group
    )

    general_group  = parser.add_argument_group('General options')
    task_group     = parser.add_argument_group('Task control options')
    exec_group     = parser.add_argument_group('Execution control options')
    logging_group  = parser.add_argument_group('Logging/output control options')
    server_group   = parser.add_argument_group('Server options')
    config_group   = parser.add_argument_group('Configuration options')

    general_group.add_argument("targets", nargs="*", metavar="recipename/target",
                        help="Execute the specified task (default is 'build') for these target "
                             "recipes (.bb files).")

    general_group.add_argument("-s", "--show-versions", action="store_true",
                        help="Show current and preferred versions of all recipes.")

    general_group.add_argument("-e", "--environment", action="store_true",
                        dest="show_environment",
                        help="Show the global or per-recipe environment complete with information"
                             " about where variables were set/changed.")

    general_group.add_argument("-g", "--graphviz", action="store_true", dest="dot_graph",
                        help="Save dependency tree information for the specified "
                             "targets in the dot syntax.")

    # @CHOICES@ is substituted out by BitbakeHelpFormatter above
    general_group.add_argument("-u", "--ui",
                        default=os.environ.get('BITBAKE_UI', 'knotty'),
                        help="The user interface to use (@CHOICES@ - default %(default)s).")

    general_group.add_argument("--version", action="store_true",
                        help="Show programs version and exit.")

    general_group.add_argument('-h', '--help', action='help',
                        help='Show this help message and exit.')


    task_group.add_argument("-f", "--force", action="store_true",
                        help="Force the specified targets/task to run (invalidating any "
                             "existing stamp file).")

    task_group.add_argument("-c", "--cmd",
                        help="Specify the task to execute. The exact options available "
                             "depend on the metadata. Some examples might be 'compile'"
                             " or 'populate_sysroot' or 'listtasks' may give a list of "
                             "the tasks available.")

    task_group.add_argument("-C", "--clear-stamp", dest="invalidate_stamp",
                        help="Invalidate the stamp for the specified task such as 'compile' "
                             "and then run the default task for the specified target(s).")

    task_group.add_argument("--runall", action="append", default=[],
                        help="Run the specified task for any recipe in the taskgraph of the "
                             "specified target (even if it wouldn't otherwise have run).")

    task_group.add_argument("--runonly", action="append",
                        help="Run only the specified task within the taskgraph of the "
                             "specified targets (and any task dependencies those tasks may have).")

    task_group.add_argument("--no-setscene", action="store_true",
                        dest="nosetscene",
                        help="Do not run any setscene tasks. sstate will be ignored and "
                            "everything needed, built.")

    task_group.add_argument("--skip-setscene", action="store_true",
                        dest="skipsetscene",
                        help="Skip setscene tasks if they would be executed. Tasks previously "
                            "restored from sstate will be kept, unlike --no-setscene.")

    task_group.add_argument("--setscene-only", action="store_true",
                        dest="setsceneonly",
                        help="Only run setscene tasks, don't run any real tasks.")


    exec_group.add_argument("-n", "--dry-run", action="store_true",
                        help="Don't execute, just go through the motions.")

    exec_group.add_argument("-p", "--parse-only", action="store_true",
                        help="Quit after parsing the BB recipes.")

    exec_group.add_argument("-k", "--continue", action="store_false", dest="halt",
                        help="Continue as much as possible after an error. While the target that "
                             "failed and anything depending on it cannot be built, as much as "
                             "possible will be built before stopping.")

    exec_group.add_argument("-P", "--profile", action="store_true",
                        help="Profile the command and save reports.")

    exec_group.add_argument("-S", "--dump-signatures", action="append",
                        default=[], metavar="SIGNATURE_HANDLER",
                        help="Dump out the signature construction information, with no task "
                             "execution. The SIGNATURE_HANDLER parameter is passed to the "
                             "handler. Two common values are none and printdiff but the handler "
                             "may define more/less. none means only dump the signature, printdiff"
                             " means compare the dumped signature with the cached one.")

    exec_group.add_argument("--revisions-changed", action="store_true",
                        help="Set the exit code depending on whether upstream floating "
                            "revisions have changed or not.")

    exec_group.add_argument("-b", "--buildfile",
                        help="Execute tasks from a specific .bb recipe directly. WARNING: Does "
                             "not handle any dependencies from other recipes.")

    logging_group.add_argument("-D", "--debug", action="count", default=0,
                        help="Increase the debug level. You can specify this "
                             "more than once. -D sets the debug level to 1, "
                             "where only bb.debug(1, ...) messages are printed "
                             "to stdout; -DD sets the debug level to 2, where "
                             "both bb.debug(1, ...) and bb.debug(2, ...) "
                             "messages are printed; etc. Without -D, no debug "
                             "messages are printed. Note that -D only affects "
                             "output to stdout. All debug messages are written "
                             "to ${T}/log.do_taskname, regardless of the debug "
                             "level.")

    logging_group.add_argument("-l", "--log-domains", action="append", dest="debug_domains",
                        default=[],
                        help="Show debug logging for the specified logging domains.")

    logging_group.add_argument("-v", "--verbose", action="store_true",
                        help="Enable tracing of shell tasks (with 'set -x'). "
                             "Also print bb.note(...) messages to stdout (in "
                             "addition to writing them to ${T}/log.do_<task>).")

    logging_group.add_argument("-q", "--quiet", action="count", default=0,
                        help="Output less log message data to the terminal. You can specify this "
                             "more than once.")

    logging_group.add_argument("-w", "--write-log", dest="writeeventlog",
                        default=os.environ.get("BBEVENTLOG"),
                        help="Writes the event log of the build to a bitbake event json file. "
                            "Use '' (empty string) to assign the name automatically.")


    server_group.add_argument("-B", "--bind", default=False,
                        help="The name/address for the bitbake xmlrpc server to bind to.")

    server_group.add_argument("-T", "--idle-timeout", type=float, dest="server_timeout",
                        default=os.getenv("BB_SERVER_TIMEOUT"),
                        help="Set timeout to unload bitbake server due to inactivity, "
                             "set to -1 means no unload, "
                             "default: Environment variable BB_SERVER_TIMEOUT.")

    server_group.add_argument("--remote-server",
                        default=os.environ.get("BBSERVER"),
                        help="Connect to the specified server.")

    server_group.add_argument("-m", "--kill-server", action="store_true",
                        help="Terminate any running bitbake server.")

    server_group.add_argument("--token", dest="xmlrpctoken",
                        default=os.environ.get("BBTOKEN"),
                        help="Specify the connection token to be used when connecting "
                             "to a remote server.")

    server_group.add_argument("--observe-only", action="store_true",
                        help="Connect to a server as an observing-only client.")

    server_group.add_argument("--status-only", action="store_true",
                        help="Check the status of the remote bitbake server.")

    server_group.add_argument("--server-only", action="store_true",
                        help="Run bitbake without a UI, only starting a server "
                            "(cooker) process.")


    config_group.add_argument("-r", "--read", action="append", dest="prefile", default=[],
                        help="Read the specified file before bitbake.conf.")

    config_group.add_argument("-R", "--postread", action="append", dest="postfile", default=[],
                        help="Read the specified file after bitbake.conf.")


    config_group.add_argument("-I", "--ignore-deps", action="append",
                        dest="extra_assume_provided", default=[],
                        help="Assume these dependencies don't exist and are already provided "
                             "(equivalent to ASSUME_PROVIDED). Useful to make dependency "
                             "graphs more appealing.")

    return parser


class BitBakeConfigParameters(cookerdata.ConfigParameters):
    def parseCommandLine(self, argv=sys.argv):
        parser = create_bitbake_parser()
        options = parser.parse_intermixed_args(argv[1:])

        if options.version:
            print("BitBake Build Tool Core version %s" % bb.__version__)
            sys.exit(0)

        if options.quiet and options.verbose:
            parser.error("options --quiet and --verbose are mutually exclusive")

        if options.quiet and options.debug:
            parser.error("options --quiet and --debug are mutually exclusive")

        # use configuration files from environment variables
        if "BBPRECONF" in os.environ:
            options.prefile.append(os.environ["BBPRECONF"])

        if "BBPOSTCONF" in os.environ:
            options.postfile.append(os.environ["BBPOSTCONF"])

        # fill in proper log name if not supplied
        if options.writeeventlog is not None and len(options.writeeventlog) == 0:
            from datetime import datetime
            eventlog = "bitbake_eventlog_%s.json" % datetime.now().strftime("%Y%m%d%H%M%S")
            options.writeeventlog = eventlog

        if options.bind:
            try:
                #Checking that the port is a number and is a ':' delimited value
                (host, port) = options.bind.split(':')
                port = int(port)
            except (ValueError,IndexError):
                raise BBMainException("FATAL: Malformed host:port bind parameter")
            options.xmlrpcinterface = (host, port)
        else:
            options.xmlrpcinterface = (None, 0)

        return options, options.targets


def bitbake_main(configParams, configuration):

    # Python multiprocessing requires /dev/shm on Linux
    if sys.platform.startswith('linux') and not os.access('/dev/shm', os.W_OK | os.X_OK):
        raise BBMainException("FATAL: /dev/shm does not exist or is not writable")

    # Unbuffer stdout to avoid log truncation in the event
    # of an unorderly exit as well as to provide timely
    # updates to log files for use with tail
    try:
        if sys.stdout.name == '<stdout>':
            # Reopen with O_SYNC (unbuffered)
            fl = fcntl.fcntl(sys.stdout.fileno(), fcntl.F_GETFL)
            fl |= os.O_SYNC
            fcntl.fcntl(sys.stdout.fileno(), fcntl.F_SETFL, fl)
    except:
        pass

    if configParams.server_only and configParams.remote_server:
            raise BBMainException("FATAL: The '--server-only' option conflicts with %s.\n" %
                                  ("the BBSERVER environment variable" if "BBSERVER" in os.environ \
                                   else "the '--remote-server' option"))

    if configParams.observe_only and not (configParams.remote_server or configParams.bind):
        raise BBMainException("FATAL: '--observe-only' can only be used by UI clients "
                              "connecting to a server.\n")

    if "BBDEBUG" in os.environ:
        level = int(os.environ["BBDEBUG"])
        if level > configParams.debug:
            configParams.debug = level

    bb.msg.init_msgconfig(configParams.verbose, configParams.debug,
                          configParams.debug_domains)

    server_connection, ui_module = setup_bitbake(configParams)
    # No server connection
    if server_connection is None:
        if configParams.status_only:
            return 1
        if configParams.kill_server:
            return 0

    if not configParams.server_only:
        if configParams.status_only:
            server_connection.terminate()
            return 0

        try:
            for event in bb.event.ui_queue:
                server_connection.events.queue_event(event)
            bb.event.ui_queue = []

            return ui_module.main(server_connection.connection, server_connection.events,
                                  configParams)
        finally:
            server_connection.terminate()
    else:
        return 0

    return 1

def timestamp():
    return datetime.datetime.now().strftime('%H:%M:%S.%f')

def setup_bitbake(configParams, extrafeatures=None):
    # Ensure logging messages get sent to the UI as events
    handler = bb.event.LogHandler()
    if not configParams.status_only:
        # In status only mode there are no logs and no UI
        logger.addHandler(handler)

    if configParams.dump_signatures:
        if extrafeatures is None:
            extrafeatures = []
        extrafeatures.append(bb.cooker.CookerFeatures.RECIPE_SIGGEN_INFO)

    if configParams.server_only:
        featureset = []
        ui_module = None
    else:
        ui_module = import_extension_module(bb.ui, configParams.ui, 'main')
        # Collect the feature set for the UI
        featureset = getattr(ui_module, "featureSet", [])

    if extrafeatures:
        for feature in extrafeatures:
            if not feature in featureset:
                featureset.append(feature)

    server_connection = None

    # Clear away any spurious environment variables while we stoke up the cooker
    # (done after import_extension_module() above since for example import gi triggers env var usage)
    cleanedvars = bb.utils.clean_environment()

    if configParams.remote_server:
        # Connect to a remote XMLRPC server
        server_connection = bb.server.xmlrpcclient.connectXMLRPC(configParams.remote_server, featureset,
                                                                 configParams.observe_only, configParams.xmlrpctoken)
    else:
        retries = 8
        while retries:
            try:
                topdir, lock, lockfile = lockBitbake()
                sockname = topdir + "/bitbake.sock"
                if lock:
                    if configParams.status_only or configParams.kill_server:
                        logger.info("bitbake server is not running.")
                        lock.close()
                        return None, None
                    # we start a server with a given featureset
                    logger.info("Starting bitbake server...")
                    # Clear the event queue since we already displayed messages
                    bb.event.ui_queue = []
                    server = bb.server.process.BitBakeServer(lock, sockname, featureset, configParams.server_timeout, configParams.xmlrpcinterface, configParams.profile)

                else:
                    logger.info("Reconnecting to bitbake server...")
                    if not os.path.exists(sockname):
                        logger.info("Previous bitbake instance shutting down?, waiting to retry... (%s)" % timestamp())
                        procs = bb.server.process.get_lockfile_process_msg(lockfile)
                        if procs:
                            logger.info("Processes holding bitbake.lock (missing socket %s):\n%s" % (sockname, procs))
                        logger.info("Directory listing: %s" % (str(os.listdir(topdir))))
                        i = 0
                        lock = None
                        # Wait for 5s or until we can get the lock
                        while not lock and i < 50:
                            time.sleep(0.1)
                            _, lock, _ = lockBitbake()
                            i += 1
                        if lock:
                            bb.utils.unlockfile(lock)
                        raise bb.server.process.ProcessTimeout("Bitbake still shutting down as socket exists but no lock?")
                if not configParams.server_only:
                    server_connection = bb.server.process.connectProcessServer(sockname, featureset)

                if server_connection or configParams.server_only:
                    break
            except BBMainFatal:
                raise
            except (Exception, bb.server.process.ProcessTimeout, SystemExit) as e:
                # SystemExit does not inherit from the Exception class, needs to be included explicitly
                if not retries:
                    raise
                retries -= 1
                tryno = 8 - retries
                if isinstance(e, (bb.server.process.ProcessTimeout, BrokenPipeError, EOFError, SystemExit)):
                    logger.info("Retrying server connection (#%d)... (%s)" % (tryno, timestamp()))
                else:
                    logger.info("Retrying server connection (#%d)... (%s, %s)" % (tryno, traceback.format_exc(), timestamp()))

            if not retries:
                bb.fatal("Unable to connect to bitbake server, or start one (server startup failures would be in bitbake-cookerdaemon.log).")
            bb.event.print_ui_queue()
            if retries < 5:
                time.sleep(5)

    if configParams.kill_server:
        server_connection.connection.terminateServer()
        server_connection.terminate()
        bb.event.ui_queue = []
        logger.info("Terminated bitbake server.")
        return None, None

    # Restore the environment in case the UI needs it
    for k in cleanedvars:
        os.environ[k] = cleanedvars[k]

    logger.removeHandler(handler)

    return server_connection, ui_module

def lockBitbake():
    topdir = bb.cookerdata.findTopdir()
    if not topdir:
        bb.error("Unable to find conf/bblayers.conf or conf/bitbake.conf. BBPATH is unset and/or not in a build directory?")
        raise BBMainFatal
    lockfile = topdir + "/bitbake.lock"
    return topdir, bb.utils.lockfile(lockfile, False, False), lockfile

