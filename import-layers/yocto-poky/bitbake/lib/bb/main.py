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

import bb
from bb import event
import bb.msg
from bb import cooker
from bb import ui
from bb import server
from bb import cookerdata

logger = logging.getLogger("BitBake")

class BBMainException(Exception):
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
        elif option.dest == 'servertype':
            valid_server_types = list_extension_modules(bb.server, 'BitBakeServer')
            option.help = option.help.replace('@CHOICES@', present_options(valid_server_types))

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

        parser.add_option("-a", "--tryaltconfigs", action="store_true",
                          dest="tryaltconfigs", default=False,
                          help="Continue with builds by trying to use alternative providers "
                               "where possible.")

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
                          help="Output more log message data to the terminal.")

        parser.add_option("-D", "--debug", action="count", dest="debug", default=0,
                          help="Increase the debug level. You can specify this more than once.")

        parser.add_option("-q", "--quiet", action="store_true", dest="quiet", default=False,
                          help="Output less log message data to the terminal.")

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

        # @CHOICES@ is substituted out by BitbakeHelpFormatter above
        parser.add_option("-t", "--servertype", action="store", dest="servertype",
                          default=["process", "xmlrpc"]["BBSERVER" in os.environ],
                          help="Choose which server type to use (@CHOICES@ - default %default).")

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

        parser.add_option("", "--foreground", action="store_true",
                          help="Run bitbake server in foreground.")

        parser.add_option("-B", "--bind", action="store", dest="bind", default=False,
                          help="The name/address for the bitbake server to bind to.")

        parser.add_option("-T", "--idle-timeout", type=int,
                          default=int(os.environ.get("BBTIMEOUT", "0")),
                          help="Set timeout to unload bitbake server due to inactivity")

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
                          help="Terminate the remote server.")

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

        # if BBSERVER says to autodetect, let's do that
        if options.remote_server:
            port = -1
            if options.remote_server != 'autostart':
                host, port = options.remote_server.split(":", 2)
                port = int(port)
            # use automatic port if port set to -1, means read it from
            # the bitbake.lock file; this is a bit tricky, but we always expect
            # to be in the base of the build directory if we need to have a
            # chance to start the server later, anyway
            if port == -1:
                lock_location = "./bitbake.lock"
                # we try to read the address at all times; if the server is not started,
                # we'll try to start it after the first connect fails, below
                try:
                    lf = open(lock_location, 'r')
                    remotedef = lf.readline()
                    [host, port] = remotedef.split(":")
                    port = int(port)
                    lf.close()
                    options.remote_server = remotedef
                except Exception as e:
                    if options.remote_server != 'autostart':
                        raise BBMainException("Failed to read bitbake.lock (%s), invalid port" % str(e))

        return options, targets[1:]


def start_server(servermodule, configParams, configuration, features):
    server = servermodule.BitBakeServer()
    single_use = not configParams.server_only and os.getenv('BBSERVER') != 'autostart'
    if configParams.bind:
        (host, port) = configParams.bind.split(':')
        server.initServer((host, int(port)), single_use=single_use,
                          idle_timeout=configParams.idle_timeout)
        configuration.interface = [server.serverImpl.host, server.serverImpl.port]
    else:
        server.initServer(single_use=single_use)
        configuration.interface = []

    try:
        configuration.setServerRegIdleCallback(server.getServerIdleCB())

        cooker = bb.cooker.BBCooker(configuration, features)

        server.addcooker(cooker)
        server.saveConnectionDetails()
    except Exception as e:
        while hasattr(server, "event_queue"):
            import queue
            try:
                event = server.event_queue.get(block=False)
            except (queue.Empty, IOError):
                break
            if isinstance(event, logging.LogRecord):
                logger.handle(event)
        raise
    if not configParams.foreground:
        server.detach()
    cooker.lock.close()
    return server


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

    ui_module = import_extension_module(bb.ui, configParams.ui, 'main')
    servermodule = import_extension_module(bb.server, configParams.servertype, 'BitBakeServer')

    if configParams.server_only:
        if configParams.servertype != "xmlrpc":
            raise BBMainException("FATAL: If '--server-only' is defined, we must set the "
                                  "servertype as 'xmlrpc'.\n")
        if not configParams.bind:
            raise BBMainException("FATAL: The '--server-only' option requires a name/address "
                                  "to bind to with the -B option.\n")
        else:
            try:
                #Checking that the port is a number
                int(configParams.bind.split(":")[1])
            except (ValueError,IndexError):
                raise BBMainException(
                        "FATAL: Malformed host:port bind parameter")
        if configParams.remote_server:
            raise BBMainException("FATAL: The '--server-only' option conflicts with %s.\n" %
                                  ("the BBSERVER environment variable" if "BBSERVER" in os.environ \
                                   else "the '--remote-server' option"))

    elif configParams.foreground:
        raise BBMainException("FATAL: The '--foreground' option can only be used "
                              "with --server-only.\n")

    if configParams.bind and configParams.servertype != "xmlrpc":
        raise BBMainException("FATAL: If '-B' or '--bind' is defined, we must "
                              "set the servertype as 'xmlrpc'.\n")

    if configParams.remote_server and configParams.servertype != "xmlrpc":
        raise BBMainException("FATAL: If '--remote-server' is defined, we must "
                              "set the servertype as 'xmlrpc'.\n")

    if configParams.observe_only and (not configParams.remote_server or configParams.bind):
        raise BBMainException("FATAL: '--observe-only' can only be used by UI clients "
                              "connecting to a server.\n")

    if configParams.kill_server and not configParams.remote_server:
        raise BBMainException("FATAL: '--kill-server' can only be used to "
                              "terminate a remote server")

    if "BBDEBUG" in os.environ:
        level = int(os.environ["BBDEBUG"])
        if level > configuration.debug:
            configuration.debug = level

    bb.msg.init_msgconfig(configParams.verbose, configuration.debug,
                          configuration.debug_domains)

    # Ensure logging messages get sent to the UI as events
    handler = bb.event.LogHandler()
    if not configParams.status_only:
        # In status only mode there are no logs and no UI
        logger.addHandler(handler)

    # Clear away any spurious environment variables while we stoke up the cooker
    cleanedvars = bb.utils.clean_environment()

    featureset = []
    if not configParams.server_only:
        # Collect the feature set for the UI
        featureset = getattr(ui_module, "featureSet", [])

    if configParams.server_only:
        for param in ('prefile', 'postfile'):
            value = getattr(configParams, param)
            if value:
                setattr(configuration, "%s_server" % param, value)
                param = "%s_server" % param

    if not configParams.remote_server:
        # we start a server with a given configuration
        server = start_server(servermodule, configParams, configuration, featureset)
        bb.event.ui_queue = []
    else:
        if os.getenv('BBSERVER') == 'autostart':
            if configParams.remote_server == 'autostart' or \
               not servermodule.check_connection(configParams.remote_server, timeout=2):
                configParams.bind = 'localhost:0'
                srv = start_server(servermodule, configParams, configuration, featureset)
                configParams.remote_server = '%s:%d' % tuple(configuration.interface)
                bb.event.ui_queue = []

        # we start a stub server that is actually a XMLRPClient that connects to a real server
        server = servermodule.BitBakeXMLRPCClient(configParams.observe_only,
                                                  configParams.xmlrpctoken)
        server.saveConnectionDetails(configParams.remote_server)


    if not configParams.server_only:
        try:
            server_connection = server.establishConnection(featureset)
        except Exception as e:
            bb.fatal("Could not connect to server %s: %s" % (configParams.remote_server, str(e)))

        if configParams.kill_server:
            server_connection.connection.terminateServer()
            bb.event.ui_queue = []
            return 0

        server_connection.setupEventQueue()

        # Restore the environment in case the UI needs it
        for k in cleanedvars:
            os.environ[k] = cleanedvars[k]

        logger.removeHandler(handler)


        if configParams.status_only:
            server_connection.terminate()
            return 0

        try:
            return ui_module.main(server_connection.connection, server_connection.events,
                                  configParams)
        finally:
            bb.event.ui_queue = []
            server_connection.terminate()
    else:
        print("Bitbake server address: %s, server port: %s" % (server.serverImpl.host,
                                                               server.serverImpl.port))
        if configParams.foreground:
            server.serverImpl.serve_forever()
        return 0

    return 1
