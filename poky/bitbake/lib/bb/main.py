#!/usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
# Copyright (C) 2006        Richard Purdie
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

import os
import sys
import logging
import optparse
import warnings
import fcntl
import time
import traceback

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

class BitbakeHelpFormatter(optparse.IndentedHelpFormatter):
    def format_option(self, option):
        # We need to do this here rather than in the text we supply to
        # add_option() because we don't want to call list_extension_modules()
        # on every execution (since it imports all of the modules)
        # Note also that we modify option.help rather than the returned text
        # - this is so that we don't have to re-format the text ourselves
        if option.dest == 'ui':
            valid_uis = list_extension_modules(bb.ui, 'main')
            option.help = option.help.replace('@CHOICES@', present_options(valid_uis))

        return optparse.IndentedHelpFormatter.format_option(self, option)

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
warnings.filterwarnings("ignore")
warnings.filterwarnings("default", module="(<string>$|(oe|bb)\.)")
warnings.filterwarnings("ignore", category=PendingDeprecationWarning)
warnings.filterwarnings("ignore", category=ImportWarning)
warnings.filterwarnings("ignore", category=DeprecationWarning, module="<string>$")
warnings.filterwarnings("ignore", message="With-statements now directly support multiple context managers")

class BitBakeConfigParameters(cookerdata.ConfigParameters):

    def parseCommandLine(self, argv=sys.argv):
        parser = optparse.OptionParser(
            formatter=BitbakeHelpFormatter(),
            version="BitBake Build Tool Core version %s" % bb.__version__,
            usage="""%prog [options] [recipename/target recipe:do_task ...]

    Executes the specified task (default is 'build') for a given set of target recipes (.bb files).
    It is assumed there is a conf/bblayers.conf available in cwd or in BBPATH which
    will provide the layer, BBFILES and other configuration information.""")

        parser.add_option("-b", "--buildfile", action="store", dest="buildfile", default=None,
                          help="Execute tasks from a specific .bb recipe directly. WARNING: Does "
                               "not handle any dependencies from other recipes.")

        parser.add_option("-k", "--continue", action="store_false", dest="abort", default=True,
                          help="Continue as much as possible after an error. While the target that "
                               "failed and anything depending on it cannot be built, as much as "
                               "possible will be built before stopping.")

        parser.add_option("-f", "--force", action="store_true", dest="force", default=False,
                          help="Force the specified targets/task to run (invalidating any "
                               "existing stamp file).")

        parser.add_option("-c", "--cmd", action="store", dest="cmd",
                          help="Specify the task to execute. The exact options available "
                               "depend on the metadata. Some examples might be 'compile'"
                               " or 'populate_sysroot' or 'listtasks' may give a list of "
                               "the tasks available.")

        parser.add_option("-C", "--clear-stamp", action="store", dest="invalidate_stamp",
                          help="Invalidate the stamp for the specified task such as 'compile' "
                               "and then run the default task for the specified target(s).")

        parser.add_option("-r", "--read", action="append", dest="prefile", default=[],
                          help="Read the specified file before bitbake.conf.")

        parser.add_option("-R", "--postread", action="append", dest="postfile", default=[],
                          help="Read the specified file after bitbake.conf.")

        parser.add_option("-v", "--verbose", action="store_true", dest="verbose", default=False,
                          help="Enable tracing of shell tasks (with 'set -x'). "
                               "Also print bb.note(...) messages to stdout (in "
                               "addition to writing them to ${T}/log.do_<task>).")

        parser.add_option("-D", "--debug", action="count", dest="debug", default=0,
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

        parser.add_option("-q", "--quiet", action="count", dest="quiet", default=0,
                          help="Output less log message data to the terminal. You can specify this more than once.")

        parser.add_option("-n", "--dry-run", action="store_true", dest="dry_run", default=False,
                          help="Don't execute, just go through the motions.")

        parser.add_option("-S", "--dump-signatures", action="append", dest="dump_signatures",
                          default=[], metavar="SIGNATURE_HANDLER",
                          help="Dump out the signature construction information, with no task "
                               "execution. The SIGNATURE_HANDLER parameter is passed to the "
                               "handler. Two common values are none and printdiff but the handler "
                               "may define more/less. none means only dump the signature, printdiff"
                               " means compare the dumped signature with the cached one.")

        parser.add_option("-p", "--parse-only", action="store_true",
                          dest="parse_only", default=False,
                          help="Quit after parsing the BB recipes.")

        parser.add_option("-s", "--show-versions", action="store_true",
                          dest="show_versions", default=False,
                          help="Show current and preferred versions of all recipes.")

        parser.add_option("-e", "--environment", action="store_true",
                          dest="show_environment", default=False,
                          help="Show the global or per-recipe environment complete with information"
                               " about where variables were set/changed.")

        parser.add_option("-g", "--graphviz", action="store_true", dest="dot_graph", default=False,
                          help="Save dependency tree information for the specified "
                               "targets in the dot syntax.")

        parser.add_option("-I", "--ignore-deps", action="append",
                          dest="extra_assume_provided", default=[],
                          help="Assume these dependencies don't exist and are already provided "
                               "(equivalent to ASSUME_PROVIDED). Useful to make dependency "
                               "graphs more appealing")

        parser.add_option("-l", "--log-domains", action="append", dest="debug_domains", default=[],
                          help="Show debug logging for the specified logging domains")

        parser.add_option("-P", "--profile", action="store_true", dest="profile", default=False,
                          help="Profile the command and save reports.")

        # @CHOICES@ is substituted out by BitbakeHelpFormatter above
        parser.add_option("-u", "--ui", action="store", dest="ui",
                          default=os.environ.get('BITBAKE_UI', 'knotty'),
                          help="The user interface to use (@CHOICES@ - default %default).")

        parser.add_option("", "--token", action="store", dest="xmlrpctoken",
                          default=os.environ.get("BBTOKEN"),
                          help="Specify the connection token to be used when connecting "
                               "to a remote server.")

        parser.add_option("", "--revisions-changed", action="store_true",
                          dest="revisions_changed", default=False,
                          help="Set the exit code depending on whether upstream floating "
                               "revisions have changed or not.")

        parser.add_option("", "--server-only", action="store_true",
                          dest="server_only", default=False,
                          help="Run bitbake without a UI, only starting a server "
                               "(cooker) process.")

        parser.add_option("-B", "--bind", action="store", dest="bind", default=False,
                          help="The name/address for the bitbake xmlrpc server to bind to.")

        parser.add_option("-T", "--idle-timeout", type=float, dest="server_timeout",
                          default=os.getenv("BB_SERVER_TIMEOUT"),
                          help="Set timeout to unload bitbake server due to inactivity, "
                                "set to -1 means no unload, "
                                "default: Environment variable BB_SERVER_TIMEOUT.")

        parser.add_option("", "--no-setscene", action="store_true",
                          dest="nosetscene", default=False,
                          help="Do not run any setscene tasks. sstate will be ignored and "
                               "everything needed, built.")

        parser.add_option("", "--setscene-only", action="store_true",
                          dest="setsceneonly", default=False,
                          help="Only run setscene tasks, don't run any real tasks.")

        parser.add_option("", "--remote-server", action="store", dest="remote_server",
                          default=os.environ.get("BBSERVER"),
                          help="Connect to the specified server.")

        parser.add_option("-m", "--kill-server", action="store_true",
                          dest="kill_server", default=False,
                          help="Terminate any running bitbake server.")

        parser.add_option("", "--observe-only", action="store_true",
                          dest="observe_only", default=False,
                          help="Connect to a server as an observing-only client.")

        parser.add_option("", "--status-only", action="store_true",
                          dest="status_only", default=False,
                          help="Check the status of the remote bitbake server.")

        parser.add_option("-w", "--write-log", action="store", dest="writeeventlog",
                          default=os.environ.get("BBEVENTLOG"),
                          help="Writes the event log of the build to a bitbake event json file. "
                               "Use '' (empty string) to assign the name automatically.")

        parser.add_option("", "--runall", action="append", dest="runall",
                          help="Run the specified task for any recipe in the taskgraph of the specified target (even if it wouldn't otherwise have run).")

        parser.add_option("", "--runonly", action="append", dest="runonly",
                          help="Run only the specified task within the taskgraph of the specified targets (and any task dependencies those tasks may have).")


        options, targets = parser.parse_args(argv)

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

        return options, targets[1:]


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

    configuration.setConfigParameters(configParams)

    if configParams.server_only and configParams.remote_server:
            raise BBMainException("FATAL: The '--server-only' option conflicts with %s.\n" %
                                  ("the BBSERVER environment variable" if "BBSERVER" in os.environ \
                                   else "the '--remote-server' option"))

    if configParams.observe_only and not (configParams.remote_server or configParams.bind):
        raise BBMainException("FATAL: '--observe-only' can only be used by UI clients "
                              "connecting to a server.\n")

    if "BBDEBUG" in os.environ:
        level = int(os.environ["BBDEBUG"])
        if level > configuration.debug:
            configuration.debug = level

    bb.msg.init_msgconfig(configParams.verbose, configuration.debug,
                          configuration.debug_domains)

    server_connection, ui_module = setup_bitbake(configParams, configuration)
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

def setup_bitbake(configParams, configuration, extrafeatures=None):
    # Ensure logging messages get sent to the UI as events
    handler = bb.event.LogHandler()
    if not configParams.status_only:
        # In status only mode there are no logs and no UI
        logger.addHandler(handler)

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
                topdir, lock = lockBitbake()
                sockname = topdir + "/bitbake.sock"
                if lock:
                    if configParams.status_only or configParams.kill_server:
                        logger.info("bitbake server is not running.")
                        lock.close()
                        return None, None
                    # we start a server with a given configuration
                    logger.info("Starting bitbake server...")
                    # Clear the event queue since we already displayed messages
                    bb.event.ui_queue = []
                    server = bb.server.process.BitBakeServer(lock, sockname, configuration, featureset)

                else:
                    logger.info("Reconnecting to bitbake server...")
                    if not os.path.exists(sockname):
                        logger.info("Previous bitbake instance shutting down?, waiting to retry...")
                        i = 0
                        lock = None
                        # Wait for 5s or until we can get the lock
                        while not lock and i < 50:
                            time.sleep(0.1)
                            _, lock = lockBitbake()
                            i += 1
                        if lock:
                            bb.utils.unlockfile(lock)
                        raise bb.server.process.ProcessTimeout("Bitbake still shutting down as socket exists but no lock?")
                if not configParams.server_only:
                    try:
                        server_connection = bb.server.process.connectProcessServer(sockname, featureset)
                    except EOFError:
                        # The server may have been shutting down but not closed the socket yet. If that happened,
                        # ignore it.
                        pass

                if server_connection or configParams.server_only:
                    break
            except BBMainFatal:
                raise
            except (Exception, bb.server.process.ProcessTimeout) as e:
                if not retries:
                    raise
                retries -= 1
                if isinstance(e, (bb.server.process.ProcessTimeout, BrokenPipeError)):
                    logger.info("Retrying server connection...")
                else:
                    logger.info("Retrying server connection... (%s)" % traceback.format_exc())
            if not retries:
                bb.fatal("Unable to connect to bitbake server, or start one")
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
        bb.error("Unable to find conf/bblayers.conf or conf/bitbake.conf. BBAPTH is unset and/or not in a build directory?")
        raise BBMainFatal
    lockfile = topdir + "/bitbake.lock"
    return topdir, bb.utils.lockfile(lockfile, False, False)

