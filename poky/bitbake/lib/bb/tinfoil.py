# tinfoil: a simple wrapper around cooker for bitbake-based command-line utilities
#
# Copyright (C) 2012-2017 Intel Corporation
# Copyright (C) 2011 Mentor Graphics Corporation
# Copyright (C) 2006-2012 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os
import sys
import time
import atexit
import re
from collections import OrderedDict, defaultdict
from functools import partial

import bb.cache
import bb.cooker
import bb.providers
import bb.taskdata
import bb.utils
import bb.command
import bb.remotedata
from bb.main import setup_bitbake, BitBakeConfigParameters
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

class TinfoilDataStoreConnectorVarHistory:
    def __init__(self, tinfoil, dsindex):
        self.tinfoil = tinfoil
        self.dsindex = dsindex

    def remoteCommand(self, cmd, *args, **kwargs):
        return self.tinfoil.run_command('dataStoreConnectorVarHistCmd', self.dsindex, cmd, args, kwargs)

    def emit(self, var, oval, val, o, d):
        ret = self.tinfoil.run_command('dataStoreConnectorVarHistCmdEmit', self.dsindex, var, oval, val, d.dsindex)
        o.write(ret)

    def __getattr__(self, name):
        if not hasattr(bb.data_smart.VariableHistory, name):
            raise AttributeError("VariableHistory has no such method %s" % name)

        newfunc = partial(self.remoteCommand, name)
        setattr(self, name, newfunc)
        return newfunc

class TinfoilDataStoreConnectorIncHistory:
    def __init__(self, tinfoil, dsindex):
        self.tinfoil = tinfoil
        self.dsindex = dsindex

    def remoteCommand(self, cmd, *args, **kwargs):
        return self.tinfoil.run_command('dataStoreConnectorIncHistCmd', self.dsindex, cmd, args, kwargs)

    def __getattr__(self, name):
        if not hasattr(bb.data_smart.IncludeHistory, name):
            raise AttributeError("IncludeHistory has no such method %s" % name)

        newfunc = partial(self.remoteCommand, name)
        setattr(self, name, newfunc)
        return newfunc

class TinfoilDataStoreConnector:
    """
    Connector object used to enable access to datastore objects via tinfoil
    Method calls are transmitted to the remote datastore for processing, if a datastore is
    returned we return a connector object for the new store
    """

    def __init__(self, tinfoil, dsindex):
        self.tinfoil = tinfoil
        self.dsindex = dsindex
        self.varhistory = TinfoilDataStoreConnectorVarHistory(tinfoil, dsindex)
        self.inchistory = TinfoilDataStoreConnectorIncHistory(tinfoil, dsindex)

    def remoteCommand(self, cmd, *args, **kwargs):
        ret = self.tinfoil.run_command('dataStoreConnectorCmd', self.dsindex, cmd, args, kwargs)
        if isinstance(ret, bb.command.DataStoreConnectionHandle):
            return TinfoilDataStoreConnector(self.tinfoil, ret.dsindex)
        return ret

    def __getattr__(self, name):
        if not hasattr(bb.data._dict_type, name):
            raise AttributeError("Data store has no such method %s" % name)

        newfunc = partial(self.remoteCommand, name)
        setattr(self, name, newfunc)
        return newfunc

    def __iter__(self):
        keys = self.tinfoil.run_command('dataStoreConnectorCmd', self.dsindex, "keys", [], {})
        for k in keys:
            yield k

class TinfoilCookerAdapter:
    """
    Provide an adapter for existing code that expects to access a cooker object via Tinfoil,
    since now Tinfoil is on the client side it no longer has direct access.
    """

    class TinfoilCookerCollectionAdapter:
        """ cooker.collection adapter """
        def __init__(self, tinfoil, mc=''):
            self.tinfoil = tinfoil
            self.mc = mc
        def get_file_appends(self, fn):
            return self.tinfoil.get_file_appends(fn, self.mc)
        def __getattr__(self, name):
            if name == 'overlayed':
                return self.tinfoil.get_overlayed_recipes(self.mc)
            elif name == 'bbappends':
                return self.tinfoil.run_command('getAllAppends', self.mc)
            else:
                raise AttributeError("%s instance has no attribute '%s'" % (self.__class__.__name__, name))

    class TinfoilRecipeCacheAdapter:
        """ cooker.recipecache adapter """
        def __init__(self, tinfoil, mc=''):
            self.tinfoil = tinfoil
            self.mc = mc
            self._cache = {}

        def get_pkg_pn_fn(self):
            pkg_pn = defaultdict(list, self.tinfoil.run_command('getRecipes', self.mc) or [])
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
                attrvalue = defaultdict(list, self.tinfoil.run_command('getRecipeDepends', self.mc) or [])
            elif name == 'rundeps':
                attrvalue = defaultdict(lambda: defaultdict(list), self.tinfoil.run_command('getRuntimeDepends', self.mc) or [])
            elif name == 'runrecs':
                attrvalue = defaultdict(lambda: defaultdict(list), self.tinfoil.run_command('getRuntimeRecommends', self.mc) or [])
            elif name == 'pkg_pepvpr':
                attrvalue = self.tinfoil.run_command('getRecipeVersions', self.mc) or {}
            elif name == 'inherits':
                attrvalue = self.tinfoil.run_command('getRecipeInherits', self.mc) or {}
            elif name == 'bbfile_priority':
                attrvalue = self.tinfoil.run_command('getBbFilePriority', self.mc) or {}
            elif name == 'pkg_dp':
                attrvalue = self.tinfoil.run_command('getDefaultPreference', self.mc) or {}
            elif name == 'fn_provides':
                attrvalue = self.tinfoil.run_command('getRecipeProvides', self.mc) or {}
            elif name == 'packages':
                attrvalue = self.tinfoil.run_command('getRecipePackages', self.mc) or {}
            elif name == 'packages_dynamic':
                attrvalue = self.tinfoil.run_command('getRecipePackagesDynamic', self.mc) or {}
            elif name == 'rproviders':
                attrvalue = self.tinfoil.run_command('getRProviders', self.mc) or {}
            else:
                raise AttributeError("%s instance has no attribute '%s'" % (self.__class__.__name__, name))

            self._cache[name] = attrvalue
            return attrvalue

    class TinfoilSkiplistByMcAdapter:
        def __init__(self, tinfoil):
            self.tinfoil = tinfoil

        def __getitem__(self, mc):
            return self.tinfoil.get_skipped_recipes(mc)

    def __init__(self, tinfoil):
        self.tinfoil = tinfoil
        self.multiconfigs = [''] + (tinfoil.config_data.getVar('BBMULTICONFIG') or '').split()
        self.collections = {}
        self.recipecaches = {}
        self.skiplist_by_mc = self.TinfoilSkiplistByMcAdapter(tinfoil)
        for mc in self.multiconfigs:
            self.collections[mc] = self.TinfoilCookerCollectionAdapter(tinfoil, mc)
            self.recipecaches[mc] = self.TinfoilRecipeCacheAdapter(tinfoil, mc)
        self._cache = {}
    def __getattr__(self, name):
        # Grab these only when they are requested since they aren't always used
        if name in self._cache:
            return self._cache[name]
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


class TinfoilRecipeInfo:
    """
    Provides a convenient representation of the cached information for a single recipe.
    Some attributes are set on construction, others are read on-demand (which internally
    may result in a remote procedure call to the bitbake server the first time).
    Note that only information which is cached is available through this object - if
    you need other variable values you will need to parse the recipe using
    Tinfoil.parse_recipe().
    """
    def __init__(self, recipecache, d, pn, fn, fns):
        self._recipecache = recipecache
        self._d = d
        self.pn = pn
        self.fn = fn
        self.fns = fns
        self.inherit_files = recipecache.inherits[fn]
        self.depends = recipecache.deps[fn]
        (self.pe, self.pv, self.pr) = recipecache.pkg_pepvpr[fn]
        self._cached_packages = None
        self._cached_rprovides = None
        self._cached_packages_dynamic = None

    def __getattr__(self, name):
        if name == 'alternates':
            return [x for x in self.fns if x != self.fn]
        elif name == 'rdepends':
            return self._recipecache.rundeps[self.fn]
        elif name == 'rrecommends':
            return self._recipecache.runrecs[self.fn]
        elif name == 'provides':
            return self._recipecache.fn_provides[self.fn]
        elif name == 'packages':
            if self._cached_packages is None:
                self._cached_packages = []
                for pkg, fns in self._recipecache.packages.items():
                    if self.fn in fns:
                        self._cached_packages.append(pkg)
            return self._cached_packages
        elif name == 'packages_dynamic':
            if self._cached_packages_dynamic is None:
                self._cached_packages_dynamic = []
                for pkg, fns in self._recipecache.packages_dynamic.items():
                    if self.fn in fns:
                        self._cached_packages_dynamic.append(pkg)
            return self._cached_packages_dynamic
        elif name == 'rprovides':
            if self._cached_rprovides is None:
                self._cached_rprovides = []
                for pkg, fns in self._recipecache.rproviders.items():
                    if self.fn in fns:
                        self._cached_rprovides.append(pkg)
            return self._cached_rprovides
        else:
            raise AttributeError("%s instance has no attribute '%s'" % (self.__class__.__name__, name))
    def inherits(self, only_recipe=False):
        """
        Get the inherited classes for a recipe. Returns the class names only.
        Parameters:
            only_recipe: True to return only the classes inherited by the recipe
                         itself, False to return all classes inherited within
                         the context for the recipe (which includes globally
                         inherited classes).
        """
        if only_recipe:
            global_inherit = [x for x in (self._d.getVar('BBINCLUDED') or '').split() if x.endswith('.bbclass')]
        else:
            global_inherit = []
        for clsfile in self.inherit_files:
            if only_recipe and clsfile in global_inherit:
                continue
            clsname = os.path.splitext(os.path.basename(clsfile))[0]
            yield clsname
    def __str__(self):
        return '%s' % self.pn


class Tinfoil:
    """
    Tinfoil - an API for scripts and utilities to query
    BitBake internals and perform build operations.
    """

    def __init__(self, output=sys.stdout, tracking=False, setup_logging=True):
        """
        Create a new tinfoil object.
        Parameters:
            output: specifies where console output should be sent. Defaults
                    to sys.stdout.
            tracking: True to enable variable history tracking, False to
                    disable it (default). Enabling this has a minor
                    performance impact so typically it isn't enabled
                    unless you need to query variable history.
            setup_logging: True to setup a logger so that things like
                    bb.warn() will work immediately and timeout warnings
                    are visible; False to let BitBake do this itself.
        """
        self.logger = logging.getLogger('BitBake')
        self.config_data = None
        self.cooker = None
        self.tracking = tracking
        self.ui_module = None
        self.server_connection = None
        self.recipes_parsed = False
        self.quiet = 0
        self.oldhandlers = self.logger.handlers[:]
        self.localhandlers = []
        if setup_logging:
            # This is the *client-side* logger, nothing to do with
            # logging messages from the server
            bb.msg.logger_create('BitBake', output)
            for handler in self.logger.handlers:
                if handler not in self.oldhandlers:
                    self.localhandlers.append(handler)

    def __enter__(self):
        return self

    def __exit__(self, type, value, traceback):
        self.shutdown()

    def prepare(self, config_only=False, config_params=None, quiet=0, extra_features=None):
        """
        Prepares the underlying BitBake system to be used via tinfoil.
        This function must be called prior to calling any of the other
        functions in the API.
        NOTE: if you call prepare() you must absolutely call shutdown()
        before your code terminates. You can use a "with" block to ensure
        this happens e.g.

            with bb.tinfoil.Tinfoil() as tinfoil:
                tinfoil.prepare()
                ...

        Parameters:
            config_only: True to read only the configuration and not load
                        the cache / parse recipes. This is useful if you just
                        want to query the value of a variable at the global
                        level or you want to do anything else that doesn't
                        involve knowing anything about the recipes in the
                        current configuration. False loads the cache / parses
                        recipes.
            config_params: optionally specify your own configuration
                        parameters. If not specified an instance of
                        TinfoilConfigParameters will be created internally.
            quiet:      quiet level controlling console output - equivalent
                        to bitbake's -q/--quiet option. Default of 0 gives
                        the same output level as normal bitbake execution.
            extra_features: extra features to be added to the feature
                        set requested from the server. See
                        CookerFeatures._feature_list for possible
                        features.
        """
        self.quiet = quiet

        if self.tracking:
            extrafeatures = [bb.cooker.CookerFeatures.BASEDATASTORE_TRACKING]
        else:
            extrafeatures = []

        if extra_features:
            extrafeatures += extra_features

        if not config_params:
            config_params = TinfoilConfigParameters(config_only=config_only, quiet=quiet)

        if not config_only:
            # Disable local loggers because the UI module is going to set up its own
            for handler in self.localhandlers:
                self.logger.handlers.remove(handler)
            self.localhandlers = []

        self.server_connection, ui_module = setup_bitbake(config_params, extrafeatures)

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
                self.recipes_parsed = True

            self.config_data = TinfoilDataStoreConnector(self, 0)
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
        Legacy function - use parse_recipes() instead.
        """
        self.parse_recipes()

    def parse_recipes(self):
        """
        Load information on all recipes. Normally you should specify
        config_only=False when calling prepare() instead of using this
        function; this function is designed for situations where you need
        to initialise Tinfoil and use it with config_only=True first and
        then conditionally call this function to parse recipes later.
        """
        config_params = TinfoilConfigParameters(config_only=False, quiet=self.quiet)
        self.run_actions(config_params)
        self.recipes_parsed = True

    def modified_files(self):
        """
        Notify the server it needs to revalidate it's caches since the client has modified files
        """
        self.run_command("revalidateCaches")

    def run_command(self, command, *params, handle_events=True):
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
        try:
            result = self.server_connection.connection.runCommand(commandline)
        finally:
            while handle_events:
                event = self.wait_event()
                if not event:
                    break
                if isinstance(event, logging.LogRecord):
                    if event.taskpid == 0 or event.levelno > logging.INFO:
                        self.logger.handle(event)
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
        reached. Note that in order to receive any events you will
        first need to set the internal event mask using set_event_mask()
        (otherwise whatever event mask the UI set up will be in effect).
        """
        if not self.server_connection:
            raise Exception('Not connected to server (did you call .prepare()?)')
        return self.server_connection.events.waitEvent(timeout)

    def get_overlayed_recipes(self, mc=''):
        """
        Find recipes which are overlayed (i.e. where recipes exist in multiple layers)
        """
        return defaultdict(list, self.run_command('getOverlayedRecipes', mc))

    def get_skipped_recipes(self, mc=''):
        """
        Find recipes which were skipped (i.e. SkipRecipe was raised
        during parsing).
        """
        return OrderedDict(self.run_command('getSkippedRecipes', mc))

    def get_all_providers(self, mc=''):
        return defaultdict(list, self.run_command('allProviders', mc))

    def find_providers(self, mc=''):
        return self.run_command('findProviders', mc)

    def find_best_provider(self, pn):
        return self.run_command('findBestProvider', pn)

    def get_runtime_providers(self, rdep):
        return self.run_command('getRuntimeProviders', rdep)

    # TODO: teach this method about mc
    def get_recipe_file(self, pn):
        """
        Get the file name for the specified recipe/target. Raises
        bb.providers.NoProvider if there is no match or the recipe was
        skipped.
        """
        best = self.find_best_provider(pn)
        if not best or (len(best) > 3 and not best[3]):
            # TODO: pass down mc
            skiplist = self.get_skipped_recipes()
            taskdata = bb.taskdata.TaskData(None, skiplist=skiplist)
            skipreasons = taskdata.get_reasons(pn)
            if skipreasons:
                raise bb.providers.NoProvider('%s is unavailable:\n  %s' % (pn, '  \n'.join(skipreasons)))
            else:
                raise bb.providers.NoProvider('Unable to find any recipe file matching "%s"' % pn)
        return best[3]

    def get_file_appends(self, fn, mc=''):
        """
        Find the bbappends for a recipe file
        """
        return self.run_command('getFileAppends', fn, mc)

    def all_recipes(self, mc='', sort=True):
        """
        Enable iterating over all recipes in the current configuration.
        Returns an iterator over TinfoilRecipeInfo objects created on demand.
        Parameters:
            mc: The multiconfig, default of '' uses the main configuration.
            sort: True to sort recipes alphabetically (default), False otherwise
        """
        recipecache = self.cooker.recipecaches[mc]
        if sort:
            recipes = sorted(recipecache.pkg_pn.items())
        else:
            recipes = recipecache.pkg_pn.items()
        for pn, fns in recipes:
            prov = self.find_best_provider(pn)
            recipe = TinfoilRecipeInfo(recipecache,
                                       self.config_data,
                                       pn=pn,
                                       fn=prov[3],
                                       fns=fns)
            yield recipe

    def all_recipe_files(self, mc='', variants=True, preferred_only=False):
        """
        Enable iterating over all recipe files in the current configuration.
        Returns an iterator over file paths.
        Parameters:
            mc: The multiconfig, default of '' uses the main configuration.
            variants: True to include variants of recipes created through
                      BBCLASSEXTEND (default) or False to exclude them
            preferred_only: True to include only the preferred recipe where
                      multiple exist providing the same PN, False to list
                      all recipes
        """
        recipecache = self.cooker.recipecaches[mc]
        if preferred_only:
            files = []
            for pn in recipecache.pkg_pn.keys():
                prov = self.find_best_provider(pn)
                files.append(prov[3])
        else:
            files = recipecache.pkg_fn.keys()
        for fn in sorted(files):
            if not variants and fn.startswith('virtual:'):
                continue
            yield fn


    def get_recipe_info(self, pn, mc=''):
        """
        Get information on a specific recipe in the current configuration by name (PN).
        Returns a TinfoilRecipeInfo object created on demand.
        Parameters:
            mc: The multiconfig, default of '' uses the main configuration.
        """
        recipecache = self.cooker.recipecaches[mc]
        prov = self.find_best_provider(pn)
        fn = prov[3]
        if fn:
            actual_pn = recipecache.pkg_fn[fn]
            recipe = TinfoilRecipeInfo(recipecache,
                                        self.config_data,
                                        pn=actual_pn,
                                        fn=fn,
                                        fns=recipecache.pkg_pn[actual_pn])
            return recipe
        else:
            return None

    def parse_recipe(self, pn):
        """
        Parse the specified recipe and return a datastore object
        representing the environment for the recipe.
        """
        fn = self.get_recipe_file(pn)
        return self.parse_recipe_file(fn)

    def finalizeData(self):
        """
        Run anonymous functions and expand keys
        """
        return self._reconvert_type(self.run_command('finalizeData'), 'DataStoreConnectionHandle')

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
        """
        if self.tracking:
            # Enable history tracking just for the parse operation
            self.run_command('enableDataTracking')
        try:
            if appends and appendlist == []:
                appends = False
            if config_data:
                 config_data = bb.data.createCopy(config_data)
                 dscon = self.run_command('parseRecipeFile', fn, appends, appendlist, config_data.dsindex)
            else:
                dscon = self.run_command('parseRecipeFile', fn, appends, appendlist)
            if dscon:
                return self._reconvert_type(dscon, 'DataStoreConnectionHandle')
            else:
                return None
        finally:
            if self.tracking:
                self.run_command('disableDataTracking')

    def build_file(self, buildfile, task, internal=True):
        """
        Runs the specified task for just a single recipe (i.e. no dependencies).
        This is equivalent to bitbake -b, except with the default internal=True
        no warning about dependencies will be produced, normal info messages
        from the runqueue will be silenced and BuildInit, BuildStarted and
        BuildCompleted events will not be fired.
        """
        return self.run_command('buildFile', buildfile, task, internal)

    def build_targets(self, targets, task=None, handle_events=True, extra_events=None, event_callback=None):
        """
        Builds the specified targets. This is equivalent to a normal invocation
        of bitbake. Has built-in event handling which is enabled by default and
        can be extended if needed.
        Parameters:
            targets:
                One or more targets to build. Can be a list or a
                space-separated string.
            task:
                The task to run; if None then the value of BB_DEFAULT_TASK
                will be used. Default None.
            handle_events:
                True to handle events in a similar way to normal bitbake
                invocation with knotty; False to return immediately (on the
                assumption that the caller will handle the events instead).
                Default True.
            extra_events:
                An optional list of events to add to the event mask (if
                handle_events=True). If you add events here you also need
                to specify a callback function in event_callback that will
                handle the additional events. Default None.
            event_callback:
                An optional function taking a single parameter which
                will be called first upon receiving any event (if
                handle_events=True) so that the caller can override or
                extend the event handling. Default None.
        """
        if isinstance(targets, str):
            targets = targets.split()
        if not task:
            task = self.config_data.getVar('BB_DEFAULT_TASK')

        if handle_events:
            # A reasonable set of default events matching up with those we handle below
            eventmask = [
                        'bb.event.BuildStarted',
                        'bb.event.BuildCompleted',
                        'logging.LogRecord',
                        'bb.event.NoProvider',
                        'bb.command.CommandCompleted',
                        'bb.command.CommandFailed',
                        'bb.build.TaskStarted',
                        'bb.build.TaskFailed',
                        'bb.build.TaskSucceeded',
                        'bb.build.TaskFailedSilent',
                        'bb.build.TaskProgress',
                        'bb.runqueue.runQueueTaskStarted',
                        'bb.runqueue.sceneQueueTaskStarted',
                        'bb.event.ProcessStarted',
                        'bb.event.ProcessProgress',
                        'bb.event.ProcessFinished',
                        ]
            if extra_events:
                eventmask.extend(extra_events)
            ret = self.set_event_mask(eventmask)

        includelogs = self.config_data.getVar('BBINCLUDELOGS')
        loglines = self.config_data.getVar('BBINCLUDELOGS_LINES')

        ret = self.run_command('buildTargets', targets, task)
        if handle_events:
            lastevent = time.time()
            result = False
            # Borrowed from knotty, instead somewhat hackily we use the helper
            # as the object to store "shutdown" on
            helper = bb.ui.uihelper.BBUIHelper()
            helper.shutdown = 0
            parseprogress = None
            termfilter = bb.ui.knotty.TerminalFilter(helper, helper, self.logger.handlers, quiet=self.quiet)
            try:
                while True:
                    try:
                        event = self.wait_event(0.25)
                        if event:
                            lastevent = time.time()
                            if event_callback and event_callback(event):
                                continue
                            if helper.eventHandler(event):
                                if isinstance(event, bb.build.TaskFailedSilent):
                                    self.logger.warning("Logfile for failed setscene task is %s" % event.logfile)
                                elif isinstance(event, bb.build.TaskFailed):
                                    bb.ui.knotty.print_event_log(event, includelogs, loglines, termfilter)
                                continue
                            if isinstance(event, bb.event.ProcessStarted):
                                if self.quiet > 1:
                                    continue
                                parseprogress = bb.ui.knotty.new_progress(event.processname, event.total)
                                parseprogress.start(False)
                                continue
                            if isinstance(event, bb.event.ProcessProgress):
                                if self.quiet > 1:
                                    continue
                                if parseprogress:
                                    parseprogress.update(event.progress)
                                else:
                                    bb.warn("Got ProcessProgress event for something that never started?")
                                continue
                            if isinstance(event, bb.event.ProcessFinished):
                                if self.quiet > 1:
                                    continue
                                if parseprogress:
                                    parseprogress.finish()
                                parseprogress = None
                                continue
                            if isinstance(event, bb.command.CommandCompleted):
                                result = True
                                break
                            if isinstance(event, (bb.command.CommandFailed, bb.command.CommandExit)):
                                self.logger.error(str(event))
                                result = False
                                break
                            if isinstance(event, logging.LogRecord):
                                if event.taskpid == 0 or event.levelno > logging.INFO:
                                    self.logger.handle(event)
                                continue
                            if isinstance(event, bb.event.NoProvider):
                                self.logger.error(str(event))
                                result = False
                                break
                        elif helper.shutdown > 1:
                            break
                        termfilter.updateFooter()
                        if time.time() > (lastevent + (3*60)):
                            if not self.run_command('ping', handle_events=False):
                                print("\nUnable to ping server and no events, closing down...\n")
                                return False
                    except KeyboardInterrupt:
                        termfilter.clearFooter()
                        if helper.shutdown == 1:
                            print("\nSecond Keyboard Interrupt, stopping...\n")
                            ret = self.run_command("stateForceShutdown")
                            if ret and ret[2]:
                                self.logger.error("Unable to cleanly stop: %s" % ret[2])
                        elif helper.shutdown == 0:
                            print("\nKeyboard Interrupt, closing down...\n")
                            interrupted = True
                            ret = self.run_command("stateShutdown")
                            if ret and ret[2]:
                                self.logger.error("Unable to cleanly shutdown: %s" % ret[2])
                        helper.shutdown = helper.shutdown + 1
                termfilter.clearFooter()
            finally:
                termfilter.finish()
            if helper.failed_tasks:
                result = False
            return result
        else:
            return ret

    def shutdown(self):
        """
        Shut down tinfoil. Disconnects from the server and gracefully
        releases any associated resources. You must call this function if
        prepare() has been called, or use a with... block when you create
        the tinfoil object which will ensure that it gets called.
        """
        try:
            if self.server_connection:
                try:
                    self.run_command('clientComplete')
                finally:
                    _server_connections.remove(self.server_connection)
                    bb.event.ui_queue = []
                    self.server_connection.terminate()
                    self.server_connection = None

        finally:
            # Restore logging handlers to how it looked when we started
            if self.oldhandlers:
                for handler in self.logger.handlers:
                    if handler not in self.oldhandlers:
                        self.logger.handlers.remove(handler)

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
            newobj = TinfoilDataStoreConnector(self, newobj.dsindex)

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
