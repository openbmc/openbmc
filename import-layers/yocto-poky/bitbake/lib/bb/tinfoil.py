# tinfoil: a simple wrapper around cooker for bitbake-based command-line utilities
#
# Copyright (C) 2012-2017 Intel Corporation
# Copyright (C) 2011 Mentor Graphics Corporation
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

import logging
import os
import sys
import atexit
import re
from collections import OrderedDict, defaultdict

import bb.cache
import bb.cooker
import bb.providers
import bb.taskdata
import bb.utils
import bb.command
import bb.remotedata
from bb.cookerdata import CookerConfiguration, ConfigParameters
from bb.main import setup_bitbake, BitBakeConfigParameters, BBMainException
import bb.fetch2


# We need this in order to shut down the connection to the bitbake server,
# otherwise the process will never properly exit
_server_connections = []
def _terminate_connections():
    for connection in _server_connections:
        connection.terminate()
atexit.register(_terminate_connections)

class TinfoilUIException(Exception):
    """Exception raised when the UI returns non-zero from its main function"""
    def __init__(self, returncode):
        self.returncode = returncode
    def __repr__(self):
        return 'UI module main returned %d' % self.returncode

class TinfoilCommandFailed(Exception):
    """Exception raised when run_command fails"""

class TinfoilDataStoreConnector:

    def __init__(self, tinfoil, dsindex):
        self.tinfoil = tinfoil
        self.dsindex = dsindex
    def getVar(self, name):
        value = self.tinfoil.run_command('dataStoreConnectorFindVar', self.dsindex, name)
        overrides = None
        if isinstance(value, dict):
            if '_connector_origtype' in value:
                value['_content'] = self.tinfoil._reconvert_type(value['_content'], value['_connector_origtype'])
                del value['_connector_origtype']
            if '_connector_overrides' in value:
                overrides = value['_connector_overrides']
                del value['_connector_overrides']
        return value, overrides
    def getKeys(self):
        return set(self.tinfoil.run_command('dataStoreConnectorGetKeys', self.dsindex))
    def getVarHistory(self, name):
        return self.tinfoil.run_command('dataStoreConnectorGetVarHistory', self.dsindex, name)
    def expandPythonRef(self, varname, expr, d):
        ds = bb.remotedata.RemoteDatastores.transmit_datastore(d)
        ret = self.tinfoil.run_command('dataStoreConnectorExpandPythonRef', ds, varname, expr)
        return ret
    def setVar(self, varname, value):
        if self.dsindex is None:
            self.tinfoil.run_command('setVariable', varname, value)
        else:
            # Not currently implemented - indicate that setting should
            # be redirected to local side
            return True
    def setVarFlag(self, varname, flagname, value):
        if self.dsindex is None:
            self.tinfoil.run_command('dataStoreConnectorSetVarFlag', self.dsindex, varname, flagname, value)
        else:
            # Not currently implemented - indicate that setting should
            # be redirected to local side
            return True
    def delVar(self, varname):
        if self.dsindex is None:
            self.tinfoil.run_command('dataStoreConnectorDelVar', self.dsindex, varname)
        else:
            # Not currently implemented - indicate that setting should
            # be redirected to local side
            return True
    def delVarFlag(self, varname, flagname):
        if self.dsindex is None:
            self.tinfoil.run_command('dataStoreConnectorDelVar', self.dsindex, varname, flagname)
        else:
            # Not currently implemented - indicate that setting should
            # be redirected to local side
            return True
    def renameVar(self, name, newname):
        if self.dsindex is None:
            self.tinfoil.run_command('dataStoreConnectorRenameVar', self.dsindex, name, newname)
        else:
            # Not currently implemented - indicate that setting should
            # be redirected to local side
            return True

class TinfoilCookerAdapter:
    """
    Provide an adapter for existing code that expects to access a cooker object via Tinfoil,
    since now Tinfoil is on the client side it no longer has direct access.
    """

    class TinfoilCookerCollectionAdapter:
        """ cooker.collection adapter """
        def __init__(self, tinfoil):
            self.tinfoil = tinfoil
        def get_file_appends(self, fn):
            return self.tinfoil.get_file_appends(fn)
        def __getattr__(self, name):
            if name == 'overlayed':
                return self.tinfoil.get_overlayed_recipes()
            elif name == 'bbappends':
                return self.tinfoil.run_command('getAllAppends')
            else:
                raise AttributeError("%s instance has no attribute '%s'" % (self.__class__.__name__, name))

    class TinfoilRecipeCacheAdapter:
        """ cooker.recipecache adapter """
        def __init__(self, tinfoil):
            self.tinfoil = tinfoil
            self._cache = {}

        def get_pkg_pn_fn(self):
            pkg_pn = defaultdict(list, self.tinfoil.run_command('getRecipes') or [])
            pkg_fn = {}
            for pn, fnlist in pkg_pn.items():
                for fn in fnlist:
                    pkg_fn[fn] = pn
            self._cache['pkg_pn'] = pkg_pn
            self._cache['pkg_fn'] = pkg_fn

        def __getattr__(self, name):
            # Grab these only when they are requested since they aren't always used
            if name in self._cache:
                return self._cache[name]
            elif name == 'pkg_pn':
                self.get_pkg_pn_fn()
                return self._cache[name]
            elif name == 'pkg_fn':
                self.get_pkg_pn_fn()
                return self._cache[name]
            elif name == 'deps':
                attrvalue = defaultdict(list, self.tinfoil.run_command('getRecipeDepends') or [])
            elif name == 'rundeps':
                attrvalue = defaultdict(lambda: defaultdict(list), self.tinfoil.run_command('getRuntimeDepends') or [])
            elif name == 'runrecs':
                attrvalue = defaultdict(lambda: defaultdict(list), self.tinfoil.run_command('getRuntimeRecommends') or [])
            elif name == 'pkg_pepvpr':
                attrvalue = self.tinfoil.run_command('getRecipeVersions') or {}
            elif name == 'inherits':
                attrvalue = self.tinfoil.run_command('getRecipeInherits') or {}
            elif name == 'bbfile_priority':
                attrvalue = self.tinfoil.run_command('getBbFilePriority') or {}
            elif name == 'pkg_dp':
                attrvalue = self.tinfoil.run_command('getDefaultPreference') or {}
            else:
                raise AttributeError("%s instance has no attribute '%s'" % (self.__class__.__name__, name))

            self._cache[name] = attrvalue
            return attrvalue

    def __init__(self, tinfoil):
        self.tinfoil = tinfoil
        self.collection = self.TinfoilCookerCollectionAdapter(tinfoil)
        self.recipecaches = {}
        # FIXME all machines
        self.recipecaches[''] = self.TinfoilRecipeCacheAdapter(tinfoil)
        self._cache = {}
    def __getattr__(self, name):
        # Grab these only when they are requested since they aren't always used
        if name in self._cache:
            return self._cache[name]
        elif name == 'skiplist':
            attrvalue = self.tinfoil.get_skipped_recipes()
        elif name == 'bbfile_config_priorities':
            ret = self.tinfoil.run_command('getLayerPriorities')
            bbfile_config_priorities = []
            for collection, pattern, regex, pri in ret:
                bbfile_config_priorities.append((collection, pattern, re.compile(regex), pri))

            attrvalue = bbfile_config_priorities
        else:
            raise AttributeError("%s instance has no attribute '%s'" % (self.__class__.__name__, name))

        self._cache[name] = attrvalue
        return attrvalue

    def findBestProvider(self, pn):
        return self.tinfoil.find_best_provider(pn)


class Tinfoil:

    def __init__(self, output=sys.stdout, tracking=False, setup_logging=True):
        self.logger = logging.getLogger('BitBake')
        self.config_data = None
        self.cooker = None
        self.tracking = tracking
        self.ui_module = None
        self.server_connection = None
        if setup_logging:
            # This is the *client-side* logger, nothing to do with
            # logging messages from the server
            bb.msg.logger_create('BitBake', output)

    def __enter__(self):
        return self

    def __exit__(self, type, value, traceback):
        self.shutdown()

    def prepare(self, config_only=False, config_params=None, quiet=0):
        if self.tracking:
            extrafeatures = [bb.cooker.CookerFeatures.BASEDATASTORE_TRACKING]
        else:
            extrafeatures = []

        if not config_params:
            config_params = TinfoilConfigParameters(config_only=config_only, quiet=quiet)

        cookerconfig = CookerConfiguration()
        cookerconfig.setConfigParameters(config_params)

        server, self.server_connection, ui_module = setup_bitbake(config_params,
                            cookerconfig,
                            extrafeatures)

        self.ui_module = ui_module

        # Ensure the path to bitbake's bin directory is in PATH so that things like
        # bitbake-worker can be run (usually this is the case, but it doesn't have to be)
        path = os.getenv('PATH').split(':')
        bitbakebinpath = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'bin'))
        for entry in path:
            if entry.endswith(os.sep):
                entry = entry[:-1]
            if os.path.abspath(entry) == bitbakebinpath:
                break
        else:
            path.insert(0, bitbakebinpath)
            os.environ['PATH'] = ':'.join(path)

        if self.server_connection:
            _server_connections.append(self.server_connection)
            if config_only:
                config_params.updateToServer(self.server_connection.connection, os.environ.copy())
                self.run_command('parseConfiguration')
            else:
                self.run_actions(config_params)

            self.config_data = bb.data.init()
            connector = TinfoilDataStoreConnector(self, None)
            self.config_data.setVar('_remote_data', connector)
            self.cooker = TinfoilCookerAdapter(self)
            self.cooker_data = self.cooker.recipecaches['']
        else:
            raise Exception('Failed to start bitbake server')

    def run_actions(self, config_params):
        """
        Run the actions specified in config_params through the UI.
        """
        ret = self.ui_module.main(self.server_connection.connection, self.server_connection.events, config_params)
        if ret:
            raise TinfoilUIException(ret)

    def parseRecipes(self):
        """
        Force a parse of all recipes. Normally you should specify
        config_only=False when calling prepare() instead of using this
        function; this function is designed for situations where you need
        to initialise Tinfoil and use it with config_only=True first and
        then conditionally call this function to parse recipes later.
        """
        config_params = TinfoilConfigParameters(config_only=False)
        self.run_actions(config_params)

    def run_command(self, command, *params):
        """
        Run a command on the server (as implemented in bb.command).
        Note that there are two types of command - synchronous and
        asynchronous; in order to receive the results of asynchronous
        commands you will need to set an appropriate event mask
        using set_event_mask() and listen for the result using
        wait_event() - with the correct event mask you'll at least get
        bb.command.CommandCompleted and possibly other events before
        that depending on the command.
        """
        if not self.server_connection:
            raise Exception('Not connected to server (did you call .prepare()?)')

        commandline = [command]
        if params:
            commandline.extend(params)
        result = self.server_connection.connection.runCommand(commandline)
        if result[1]:
            raise TinfoilCommandFailed(result[1])
        return result[0]

    def set_event_mask(self, eventlist):
        """Set the event mask which will be applied within wait_event()"""
        if not self.server_connection:
            raise Exception('Not connected to server (did you call .prepare()?)')
        llevel, debug_domains = bb.msg.constructLogOptions()
        ret = self.run_command('setEventMask', self.server_connection.connection.getEventHandle(), llevel, debug_domains, eventlist)
        if not ret:
            raise Exception('setEventMask failed')

    def wait_event(self, timeout=0):
        """
        Wait for an event from the server for the specified time.
        A timeout of 0 means don't wait if there are no events in the queue.
        Returns the next event in the queue or None if the timeout was
        reached. Note that in order to recieve any events you will
        first need to set the internal event mask using set_event_mask()
        (otherwise whatever event mask the UI set up will be in effect).
        """
        if not self.server_connection:
            raise Exception('Not connected to server (did you call .prepare()?)')
        return self.server_connection.events.waitEvent(timeout)

    def get_overlayed_recipes(self):
        return defaultdict(list, self.run_command('getOverlayedRecipes'))

    def get_skipped_recipes(self):
        return OrderedDict(self.run_command('getSkippedRecipes'))

    def get_all_providers(self):
        return defaultdict(list, self.run_command('allProviders'))

    def find_providers(self):
        return self.run_command('findProviders')

    def find_best_provider(self, pn):
        return self.run_command('findBestProvider', pn)

    def get_runtime_providers(self, rdep):
        return self.run_command('getRuntimeProviders', rdep)

    def get_recipe_file(self, pn):
        """
        Get the file name for the specified recipe/target. Raises
        bb.providers.NoProvider if there is no match or the recipe was
        skipped.
        """
        best = self.find_best_provider(pn)
        if not best or (len(best) > 3 and not best[3]):
            skiplist = self.get_skipped_recipes()
            taskdata = bb.taskdata.TaskData(None, skiplist=skiplist)
            skipreasons = taskdata.get_reasons(pn)
            if skipreasons:
                raise bb.providers.NoProvider('%s is unavailable:\n  %s' % (pn, '  \n'.join(skipreasons)))
            else:
                raise bb.providers.NoProvider('Unable to find any recipe file matching "%s"' % pn)
        return best[3]

    def get_file_appends(self, fn):
        return self.run_command('getFileAppends', fn)

    def parse_recipe(self, pn):
        """
        Parse the specified recipe and return a datastore object
        representing the environment for the recipe.
        """
        fn = self.get_recipe_file(pn)
        return self.parse_recipe_file(fn)

    def parse_recipe_file(self, fn, appends=True, appendlist=None, config_data=None):
        """
        Parse the specified recipe file (with or without bbappends)
        and return a datastore object representing the environment
        for the recipe.
        Parameters:
            fn: recipe file to parse - can be a file path or virtual
                specification
            appends: True to apply bbappends, False otherwise
            appendlist: optional list of bbappend files to apply, if you
                        want to filter them
            config_data: custom config datastore to use. NOTE: if you
                         specify config_data then you cannot use a virtual
                         specification for fn.
        """
        if appends and appendlist == []:
            appends = False
        if config_data:
            dctr = bb.remotedata.RemoteDatastores.transmit_datastore(config_data)
            dscon = self.run_command('parseRecipeFile', fn, appends, appendlist, dctr)
        else:
            dscon = self.run_command('parseRecipeFile', fn, appends, appendlist)
        if dscon:
            return self._reconvert_type(dscon, 'DataStoreConnectionHandle')
        else:
            return None

    def build_file(self, buildfile, task):
        """
        Runs the specified task for just a single recipe (i.e. no dependencies).
        This is equivalent to bitbake -b, except no warning will be printed.
        """
        return self.run_command('buildFile', buildfile, task, True)

    def shutdown(self):
        if self.server_connection:
            self.run_command('clientComplete')
            _server_connections.remove(self.server_connection)
            bb.event.ui_queue = []
            self.server_connection.terminate()
            self.server_connection = None

    def _reconvert_type(self, obj, origtypename):
        """
        Convert an object back to the right type, in the case
        that marshalling has changed it (especially with xmlrpc)
        """
        supported_types = {
            'set': set,
            'DataStoreConnectionHandle': bb.command.DataStoreConnectionHandle,
        }

        origtype = supported_types.get(origtypename, None)
        if origtype is None:
            raise Exception('Unsupported type "%s"' % origtypename)
        if type(obj) == origtype:
            newobj = obj
        elif isinstance(obj, dict):
            # New style class
            newobj = origtype()
            for k,v in obj.items():
                setattr(newobj, k, v)
        else:
            # Assume we can coerce the type
            newobj = origtype(obj)

        if isinstance(newobj, bb.command.DataStoreConnectionHandle):
            connector = TinfoilDataStoreConnector(self, newobj.dsindex)
            newobj = bb.data.init()
            newobj.setVar('_remote_data', connector)

        return newobj


class TinfoilConfigParameters(BitBakeConfigParameters):

    def __init__(self, config_only, **options):
        self.initial_options = options
        # Apply some sane defaults
        if not 'parse_only' in options:
            self.initial_options['parse_only'] = not config_only
        #if not 'status_only' in options:
        #    self.initial_options['status_only'] = config_only
        if not 'ui' in options:
            self.initial_options['ui'] = 'knotty'
        if not 'argv' in options:
            self.initial_options['argv'] = []

        super(TinfoilConfigParameters, self).__init__()

    def parseCommandLine(self, argv=None):
        # We don't want any parameters parsed from the command line
        opts = super(TinfoilConfigParameters, self).parseCommandLine([])
        for key, val in self.initial_options.items():
            setattr(opts[0], key, val)
        return opts
