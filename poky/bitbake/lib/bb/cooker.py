#
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
# Copyright (C) 2006 - 2007 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys, os, glob, os.path, re, time
import itertools
import logging
import multiprocessing
import sre_constants
import threading
from io import StringIO, UnsupportedOperation
from contextlib import closing
from collections import defaultdict, namedtuple
import bb, bb.exceptions, bb.command
from bb import utils, data, parse, event, cache, providers, taskdata, runqueue, build
import queue
import signal
import prserv.serv
import pyinotify
import json
import pickle
import codecs
import hashserv

logger      = logging.getLogger("BitBake")
collectlog  = logging.getLogger("BitBake.Collection")
buildlog    = logging.getLogger("BitBake.Build")
parselog    = logging.getLogger("BitBake.Parsing")
providerlog = logging.getLogger("BitBake.Provider")

class NoSpecificMatch(bb.BBHandledException):
    """
    Exception raised when no or multiple file matches are found
    """

class NothingToBuild(Exception):
    """
    Exception raised when there is nothing to build
    """

class CollectionError(bb.BBHandledException):
    """
    Exception raised when layer configuration is incorrect
    """

class state:
    initial, parsing, running, shutdown, forceshutdown, stopped, error = list(range(7))

    @classmethod
    def get_name(cls, code):
        for name in dir(cls):
            value = getattr(cls, name)
            if type(value) == type(cls.initial) and value == code:
                return name
        raise ValueError("Invalid status code: %s" % code)


class SkippedPackage:
    def __init__(self, info = None, reason = None):
        self.pn = None
        self.skipreason = None
        self.provides = None
        self.rprovides = None

        if info:
            self.pn = info.pn
            self.skipreason = info.skipreason
            self.provides = info.provides
            self.rprovides = info.rprovides
        elif reason:
            self.skipreason = reason


class CookerFeatures(object):
    _feature_list = [HOB_EXTRA_CACHES, BASEDATASTORE_TRACKING, SEND_SANITYEVENTS] = list(range(3))

    def __init__(self):
        self._features=set()

    def setFeature(self, f):
        # validate we got a request for a feature we support
        if f not in CookerFeatures._feature_list:
            return
        self._features.add(f)

    def __contains__(self, f):
        return f in self._features

    def __iter__(self):
        return self._features.__iter__()

    def __next__(self):
        return next(self._features)


class EventWriter:
    def __init__(self, cooker, eventfile):
        self.file_inited = None
        self.cooker = cooker
        self.eventfile = eventfile
        self.event_queue = []

    def write_event(self, event):
        with open(self.eventfile, "a") as f:
            try:
                str_event = codecs.encode(pickle.dumps(event), 'base64').decode('utf-8')
                f.write("%s\n" % json.dumps({"class": event.__module__ + "." + event.__class__.__name__,
                                             "vars": str_event}))
            except Exception as err:
                import traceback
                print(err, traceback.format_exc())

    def send(self, event):
        if self.file_inited:
            # we have the file, just write the event
            self.write_event(event)
        else:
            # init on bb.event.BuildStarted
            name = "%s.%s" % (event.__module__, event.__class__.__name__)
            if name in ("bb.event.BuildStarted", "bb.cooker.CookerExit"):
                with open(self.eventfile, "w") as f:
                    f.write("%s\n" % json.dumps({ "allvariables" : self.cooker.getAllKeysWithFlags(["doc", "func"])}))

                self.file_inited = True

                # write pending events
                for evt in self.event_queue:
                    self.write_event(evt)

                # also write the current event
                self.write_event(event)
            else:
                # queue all events until the file is inited
                self.event_queue.append(event)

#============================================================================#
# BBCooker
#============================================================================#
class BBCooker:
    """
    Manages one bitbake build run
    """

    def __init__(self, configuration, featureSet=None):
        self.recipecaches = None
        self.skiplist = {}
        self.featureset = CookerFeatures()
        if featureSet:
            for f in featureSet:
                self.featureset.setFeature(f)

        self.configuration = configuration

        bb.debug(1, "BBCooker starting %s" % time.time())
        sys.stdout.flush()

        self.configwatcher = pyinotify.WatchManager()
        bb.debug(1, "BBCooker pyinotify1 %s" % time.time())
        sys.stdout.flush()

        self.configwatcher.bbseen = set()
        self.configwatcher.bbwatchedfiles = set()
        self.confignotifier = pyinotify.Notifier(self.configwatcher, self.config_notifications)
        bb.debug(1, "BBCooker pyinotify2 %s" % time.time())
        sys.stdout.flush()
        self.watchmask = pyinotify.IN_CLOSE_WRITE | pyinotify.IN_CREATE | pyinotify.IN_DELETE | \
                         pyinotify.IN_DELETE_SELF | pyinotify.IN_MODIFY | pyinotify.IN_MOVE_SELF | \
                         pyinotify.IN_MOVED_FROM | pyinotify.IN_MOVED_TO
        self.watcher = pyinotify.WatchManager()
        bb.debug(1, "BBCooker pyinotify3 %s" % time.time())
        sys.stdout.flush()
        self.watcher.bbseen = set()
        self.watcher.bbwatchedfiles = set()
        self.notifier = pyinotify.Notifier(self.watcher, self.notifications)

        bb.debug(1, "BBCooker pyinotify complete %s" % time.time())
        sys.stdout.flush()

        # If being called by something like tinfoil, we need to clean cached data
        # which may now be invalid
        bb.parse.clear_cache()
        bb.parse.BBHandler.cached_statements = {}

        self.ui_cmdline = None
        self.hashserv = None
        self.hashservaddr = None

        self.initConfigurationData()

        bb.debug(1, "BBCooker parsed base configuration %s" % time.time())
        sys.stdout.flush()

        # we log all events to a file if so directed
        if self.configuration.writeeventlog:
            # register the log file writer as UI Handler
            writer = EventWriter(self, self.configuration.writeeventlog)
            EventLogWriteHandler = namedtuple('EventLogWriteHandler', ['event'])
            bb.event.register_UIHhandler(EventLogWriteHandler(writer))

        self.inotify_modified_files = []

        def _process_inotify_updates(server, cooker, abort):
            cooker.process_inotify_updates()
            return 1.0

        self.configuration.server_register_idlecallback(_process_inotify_updates, self)

        # TOSTOP must not be set or our children will hang when they output
        try:
            fd = sys.stdout.fileno()
            if os.isatty(fd):
                import termios
                tcattr = termios.tcgetattr(fd)
                if tcattr[3] & termios.TOSTOP:
                    buildlog.info("The terminal had the TOSTOP bit set, clearing...")
                    tcattr[3] = tcattr[3] & ~termios.TOSTOP
                    termios.tcsetattr(fd, termios.TCSANOW, tcattr)
        except UnsupportedOperation:
            pass

        self.command = bb.command.Command(self)
        self.state = state.initial

        self.parser = None

        signal.signal(signal.SIGTERM, self.sigterm_exception)
        # Let SIGHUP exit as SIGTERM
        signal.signal(signal.SIGHUP, self.sigterm_exception)

        bb.debug(1, "BBCooker startup complete %s" % time.time())
        sys.stdout.flush()

    def process_inotify_updates(self):
        for n in [self.confignotifier, self.notifier]:
            if n.check_events(timeout=0):
                # read notified events and enqeue them
                n.read_events()
                n.process_events()

    def config_notifications(self, event):
        if event.maskname == "IN_Q_OVERFLOW":
            bb.warn("inotify event queue overflowed, invalidating caches.")
            self.parsecache_valid = False
            self.baseconfig_valid = False
            bb.parse.clear_cache()
            return
        if not event.pathname in self.configwatcher.bbwatchedfiles:
            return
        if not event.pathname in self.inotify_modified_files:
            self.inotify_modified_files.append(event.pathname)
        self.baseconfig_valid = False

    def notifications(self, event):
        if event.maskname == "IN_Q_OVERFLOW":
            bb.warn("inotify event queue overflowed, invalidating caches.")
            self.parsecache_valid = False
            bb.parse.clear_cache()
            return
        if event.pathname.endswith("bitbake-cookerdaemon.log") \
                or event.pathname.endswith("bitbake.lock"):
            return
        if not event.pathname in self.inotify_modified_files:
            self.inotify_modified_files.append(event.pathname)
        self.parsecache_valid = False

    def add_filewatch(self, deps, watcher=None, dirs=False):
        if not watcher:
            watcher = self.watcher
        for i in deps:
            watcher.bbwatchedfiles.add(i[0])
            if dirs:
                f = i[0]
            else:
                f = os.path.dirname(i[0])
            if f in watcher.bbseen:
                continue
            watcher.bbseen.add(f)
            watchtarget = None
            while True:
                # We try and add watches for files that don't exist but if they did, would influence
                # the parser. The parent directory of these files may not exist, in which case we need
                # to watch any parent that does exist for changes.
                try:
                    watcher.add_watch(f, self.watchmask, quiet=False)
                    if watchtarget:
                        watcher.bbwatchedfiles.add(watchtarget)
                    break
                except pyinotify.WatchManagerError as e:
                    if 'ENOENT' in str(e):
                        watchtarget = f
                        f = os.path.dirname(f)
                        if f in watcher.bbseen:
                            break
                        watcher.bbseen.add(f)
                        continue
                    if 'ENOSPC' in str(e):
                        providerlog.error("No space left on device or exceeds fs.inotify.max_user_watches?")
                        providerlog.error("To check max_user_watches: sysctl -n fs.inotify.max_user_watches.")
                        providerlog.error("To modify max_user_watches: sysctl -n -w fs.inotify.max_user_watches=<value>.")
                        providerlog.error("Root privilege is required to modify max_user_watches.")
                    raise

    def sigterm_exception(self, signum, stackframe):
        if signum == signal.SIGTERM:
            bb.warn("Cooker received SIGTERM, shutting down...")
        elif signum == signal.SIGHUP:
            bb.warn("Cooker received SIGHUP, shutting down...")
        self.state = state.forceshutdown

    def setFeatures(self, features):
        # we only accept a new feature set if we're in state initial, so we can reset without problems
        if not self.state in [state.initial, state.shutdown, state.forceshutdown, state.stopped, state.error]:
            raise Exception("Illegal state for feature set change")
        original_featureset = list(self.featureset)
        for feature in features:
            self.featureset.setFeature(feature)
        bb.debug(1, "Features set %s (was %s)" % (original_featureset, list(self.featureset)))
        if (original_featureset != list(self.featureset)) and self.state != state.error:
            self.reset()

    def initConfigurationData(self):

        self.state = state.initial
        self.caches_array = []

        # Need to preserve BB_CONSOLELOG over resets
        consolelog = None
        if hasattr(self, "data"):
            consolelog = self.data.getVar("BB_CONSOLELOG")

        if CookerFeatures.BASEDATASTORE_TRACKING in self.featureset:
            self.enableDataTracking()

        all_extra_cache_names = []
        # We hardcode all known cache types in a single place, here.
        if CookerFeatures.HOB_EXTRA_CACHES in self.featureset:
            all_extra_cache_names.append("bb.cache_extra:HobRecipeInfo")

        caches_name_array = ['bb.cache:CoreRecipeInfo'] + all_extra_cache_names

        # At least CoreRecipeInfo will be loaded, so caches_array will never be empty!
        # This is the entry point, no further check needed!
        for var in caches_name_array:
            try:
                module_name, cache_name = var.split(':')
                module = __import__(module_name, fromlist=(cache_name,))
                self.caches_array.append(getattr(module, cache_name))
            except ImportError as exc:
                logger.critical("Unable to import extra RecipeInfo '%s' from '%s': %s" % (cache_name, module_name, exc))
                sys.exit("FATAL: Failed to import extra cache class '%s'." % cache_name)

        self.databuilder = bb.cookerdata.CookerDataBuilder(self.configuration, False)
        self.databuilder.parseBaseConfiguration()
        self.data = self.databuilder.data
        self.data_hash = self.databuilder.data_hash
        self.extraconfigdata = {}

        if consolelog:
            self.data.setVar("BB_CONSOLELOG", consolelog)

        self.data.setVar('BB_CMDLINE', self.ui_cmdline)

        if CookerFeatures.BASEDATASTORE_TRACKING in self.featureset:
            self.disableDataTracking()

        for mc in self.databuilder.mcdata.values():
            mc.renameVar("__depends", "__base_depends")
            self.add_filewatch(mc.getVar("__base_depends", False), self.configwatcher)

        self.baseconfig_valid = True
        self.parsecache_valid = False

    def handlePRServ(self):
        # Setup a PR Server based on the new configuration
        try:
            self.prhost = prserv.serv.auto_start(self.data)
        except prserv.serv.PRServiceConfigError as e:
            bb.fatal("Unable to start PR Server, exitting")

        if self.data.getVar("BB_HASHSERVE") == "auto":
            # Create a new hash server bound to a unix domain socket
            if not self.hashserv:
                dbfile = (self.data.getVar("PERSISTENT_DIR") or self.data.getVar("CACHE")) + "/hashserv.db"
                self.hashservaddr = "unix://%s/hashserve.sock" % self.data.getVar("TOPDIR")
                self.hashserv = hashserv.create_server(self.hashservaddr, dbfile, sync=False)
                self.hashserv.process = multiprocessing.Process(target=self.hashserv.serve_forever)
                self.hashserv.process.start()
            self.data.setVar("BB_HASHSERVE", self.hashservaddr)
            self.databuilder.origdata.setVar("BB_HASHSERVE", self.hashservaddr)
            self.databuilder.data.setVar("BB_HASHSERVE", self.hashservaddr)
            for mc in self.databuilder.mcdata:
                self.databuilder.mcdata[mc].setVar("BB_HASHSERVE", self.hashservaddr)

        bb.parse.init_parser(self.data)

    def enableDataTracking(self):
        self.configuration.tracking = True
        if hasattr(self, "data"):
            self.data.enableTracking()

    def disableDataTracking(self):
        self.configuration.tracking = False
        if hasattr(self, "data"):
            self.data.disableTracking()

    def parseConfiguration(self):
        # Set log file verbosity
        verboselogs = bb.utils.to_boolean(self.data.getVar("BB_VERBOSE_LOGS", False))
        if verboselogs:
            bb.msg.loggerVerboseLogs = True

        # Change nice level if we're asked to
        nice = self.data.getVar("BB_NICE_LEVEL")
        if nice:
            curnice = os.nice(0)
            nice = int(nice) - curnice
            buildlog.verbose("Renice to %s " % os.nice(nice))

        if self.recipecaches:
            del self.recipecaches
        self.multiconfigs = self.databuilder.mcdata.keys()
        self.recipecaches = {}
        for mc in self.multiconfigs:
            self.recipecaches[mc] = bb.cache.CacheData(self.caches_array)

        self.handleCollections(self.data.getVar("BBFILE_COLLECTIONS"))

        self.parsecache_valid = False

    def updateConfigOpts(self, options, environment, cmdline):
        self.ui_cmdline = cmdline
        clean = True
        for o in options:
            if o in ['prefile', 'postfile']:
                # Only these options may require a reparse
                try:
                    if getattr(self.configuration, o) == options[o]:
                        # Value is the same, no need to mark dirty
                        continue
                except AttributeError:
                    pass
                logger.debug(1, "Marking as dirty due to '%s' option change to '%s'" % (o, options[o]))
                print("Marking as dirty due to '%s' option change to '%s'" % (o, options[o]))
                clean = False
            setattr(self.configuration, o, options[o])
        for k in bb.utils.approved_variables():
            if k in environment and k not in self.configuration.env:
                logger.debug(1, "Updating new environment variable %s to %s" % (k, environment[k]))
                self.configuration.env[k] = environment[k]
                clean = False
            if k in self.configuration.env and k not in environment:
                logger.debug(1, "Updating environment variable %s (deleted)" % (k))
                del self.configuration.env[k]
                clean = False
            if k not in self.configuration.env and k not in environment:
                continue
            if environment[k] != self.configuration.env[k]:
                logger.debug(1, "Updating environment variable %s from %s to %s" % (k, self.configuration.env[k], environment[k]))
                self.configuration.env[k] = environment[k]
                clean = False
        if not clean:
            logger.debug(1, "Base environment change, triggering reparse")
            self.reset()

    def runCommands(self, server, data, abort):
        """
        Run any queued asynchronous command
        This is done by the idle handler so it runs in true context rather than
        tied to any UI.
        """

        return self.command.runAsyncCommand()

    def showVersions(self):

        (latest_versions, preferred_versions) = self.findProviders()

        logger.plain("%-35s %25s %25s", "Recipe Name", "Latest Version", "Preferred Version")
        logger.plain("%-35s %25s %25s\n", "===========", "==============", "=================")

        for p in sorted(self.recipecaches[''].pkg_pn):
            pref = preferred_versions[p]
            latest = latest_versions[p]

            prefstr = pref[0][0] + ":" + pref[0][1] + '-' + pref[0][2]
            lateststr = latest[0][0] + ":" + latest[0][1] + "-" + latest[0][2]

            if pref == latest:
                prefstr = ""

            logger.plain("%-35s %25s %25s", p, lateststr, prefstr)

    def showEnvironment(self, buildfile=None, pkgs_to_build=None):
        """
        Show the outer or per-recipe environment
        """
        fn = None
        envdata = None
        mc = ''
        if not pkgs_to_build:
            pkgs_to_build = []

        orig_tracking = self.configuration.tracking
        if not orig_tracking:
            self.enableDataTracking()
            self.reset()

        def mc_base(p):
            if p.startswith('mc:'):
                s = p.split(':')
                if len(s) == 2:
                    return s[1]
            return None

        if buildfile:
            # Parse the configuration here. We need to do it explicitly here since
            # this showEnvironment() code path doesn't use the cache
            self.parseConfiguration()

            fn, cls, mc = bb.cache.virtualfn2realfn(buildfile)
            fn = self.matchFile(fn)
            fn = bb.cache.realfn2virtual(fn, cls, mc)
        elif len(pkgs_to_build) == 1:
            mc = mc_base(pkgs_to_build[0])
            if not mc:
                ignore = self.data.getVar("ASSUME_PROVIDED") or ""
                if pkgs_to_build[0] in set(ignore.split()):
                    bb.fatal("%s is in ASSUME_PROVIDED" % pkgs_to_build[0])

                taskdata, runlist = self.buildTaskData(pkgs_to_build, None, self.configuration.abort, allowincomplete=True)

                mc = runlist[0][0]
                fn = runlist[0][3]

        if fn:
            try:
                bb_cache = bb.cache.Cache(self.databuilder, self.data_hash, self.caches_array)
                envdata = bb_cache.loadDataFull(fn, self.collection.get_file_appends(fn))
            except Exception as e:
                parselog.exception("Unable to read %s", fn)
                raise
        else:
            if not mc in self.databuilder.mcdata:
                bb.fatal('Not multiconfig named "%s" found' % mc)
            envdata = self.databuilder.mcdata[mc]
            data.expandKeys(envdata)
            parse.ast.runAnonFuncs(envdata)

        # Display history
        with closing(StringIO()) as env:
            self.data.inchistory.emit(env)
            logger.plain(env.getvalue())

        # emit variables and shell functions
        with closing(StringIO()) as env:
            data.emit_env(env, envdata, True)
            logger.plain(env.getvalue())

        # emit the metadata which isnt valid shell
        for e in sorted(envdata.keys()):
            if envdata.getVarFlag(e, 'func', False) and envdata.getVarFlag(e, 'python', False):
                logger.plain("\npython %s () {\n%s}\n", e, envdata.getVar(e, False))

        if not orig_tracking:
            self.disableDataTracking()
            self.reset()

    def buildTaskData(self, pkgs_to_build, task, abort, allowincomplete=False):
        """
        Prepare a runqueue and taskdata object for iteration over pkgs_to_build
        """
        bb.event.fire(bb.event.TreeDataPreparationStarted(), self.data)

        # A task of None means use the default task
        if task is None:
            task = self.configuration.cmd
        if not task.startswith("do_"):
            task = "do_%s" % task

        targetlist = self.checkPackages(pkgs_to_build, task)
        fulltargetlist = []
        defaulttask_implicit = ''
        defaulttask_explicit = False
        wildcard = False

        # Wild card expansion:
        # Replace string such as "mc:*:bash"
        # into "mc:A:bash mc:B:bash bash"
        for k in targetlist:
            if k.startswith("mc:"):
                if wildcard:
                    bb.fatal('multiconfig conflict')
                if k.split(":")[1] == "*":
                    wildcard = True
                    for mc in self.multiconfigs:
                        if mc:
                            fulltargetlist.append(k.replace('*', mc))
                        # implicit default task
                        else:
                            defaulttask_implicit = k.split(":")[2]
                else:
                    fulltargetlist.append(k)
            else:
                defaulttask_explicit = True
                fulltargetlist.append(k)

        if not defaulttask_explicit and defaulttask_implicit != '':
            fulltargetlist.append(defaulttask_implicit)

        bb.debug(1,"Target list: %s" % (str(fulltargetlist)))
        taskdata = {}
        localdata = {}

        for mc in self.multiconfigs:
            taskdata[mc] = bb.taskdata.TaskData(abort, skiplist=self.skiplist, allowincomplete=allowincomplete)
            localdata[mc] = data.createCopy(self.databuilder.mcdata[mc])
            bb.data.expandKeys(localdata[mc])

        current = 0
        runlist = []
        for k in fulltargetlist:
            mc = ""
            if k.startswith("mc:"):
                mc = k.split(":")[1]
                k = ":".join(k.split(":")[2:])
            ktask = task
            if ":do_" in k:
                k2 = k.split(":do_")
                k = k2[0]
                ktask = k2[1]
            taskdata[mc].add_provider(localdata[mc], self.recipecaches[mc], k)
            current += 1
            if not ktask.startswith("do_"):
                ktask = "do_%s" % ktask
            if k not in taskdata[mc].build_targets or not taskdata[mc].build_targets[k]:
                # e.g. in ASSUME_PROVIDED
                continue
            fn = taskdata[mc].build_targets[k][0]
            runlist.append([mc, k, ktask, fn])
            bb.event.fire(bb.event.TreeDataPreparationProgress(current, len(fulltargetlist)), self.data)

        havemc = False
        for mc in self.multiconfigs:
            if taskdata[mc].get_mcdepends():
                havemc = True

        # No need to do check providers if there are no mcdeps or not an mc build
        if havemc or len(self.multiconfigs) > 1:
            seen = set()
            new = True
            # Make sure we can provide the multiconfig dependency
            while new:
                mcdeps = set()
                # Add unresolved first, so we can get multiconfig indirect dependencies on time
                for mc in self.multiconfigs:
                    taskdata[mc].add_unresolved(localdata[mc], self.recipecaches[mc])
                    mcdeps |= set(taskdata[mc].get_mcdepends())
                new = False
                for mc in self.multiconfigs:
                    for k in mcdeps:
                        if k in seen:
                            continue
                        l = k.split(':')
                        depmc = l[2]
                        if depmc not in self.multiconfigs:
                            bb.fatal("Multiconfig dependency %s depends on nonexistent mc configuration %s" % (k,depmc))
                        else:
                            logger.debug(1, "Adding providers for multiconfig dependency %s" % l[3])
                            taskdata[depmc].add_provider(localdata[depmc], self.recipecaches[depmc], l[3])
                            seen.add(k)
                            new = True

        for mc in self.multiconfigs:
            taskdata[mc].add_unresolved(localdata[mc], self.recipecaches[mc])

        bb.event.fire(bb.event.TreeDataPreparationCompleted(len(fulltargetlist)), self.data)
        return taskdata, runlist

    def prepareTreeData(self, pkgs_to_build, task):
        """
        Prepare a runqueue and taskdata object for iteration over pkgs_to_build
        """

        # We set abort to False here to prevent unbuildable targets raising
        # an exception when we're just generating data
        taskdata, runlist = self.buildTaskData(pkgs_to_build, task, False, allowincomplete=True)

        return runlist, taskdata

    ######## WARNING : this function requires cache_extra to be enabled ########

    def generateTaskDepTreeData(self, pkgs_to_build, task):
        """
        Create a dependency graph of pkgs_to_build including reverse dependency
        information.
        """
        if not task.startswith("do_"):
            task = "do_%s" % task

        runlist, taskdata = self.prepareTreeData(pkgs_to_build, task)
        rq = bb.runqueue.RunQueue(self, self.data, self.recipecaches, taskdata, runlist)
        rq.rqdata.prepare()
        return self.buildDependTree(rq, taskdata)

    @staticmethod
    def add_mc_prefix(mc, pn):
        if mc:
            return "mc:%s:%s" % (mc, pn)
        return pn

    def buildDependTree(self, rq, taskdata):
        seen_fns = []
        depend_tree = {}
        depend_tree["depends"] = {}
        depend_tree["tdepends"] = {}
        depend_tree["pn"] = {}
        depend_tree["rdepends-pn"] = {}
        depend_tree["packages"] = {}
        depend_tree["rdepends-pkg"] = {}
        depend_tree["rrecs-pkg"] = {}
        depend_tree['providermap'] = {}
        depend_tree["layer-priorities"] = self.bbfile_config_priorities

        for mc in taskdata:
            for name, fn in list(taskdata[mc].get_providermap().items()):
                pn = self.recipecaches[mc].pkg_fn[fn]
                pn = self.add_mc_prefix(mc, pn)
                if name != pn:
                    version = "%s:%s-%s" % self.recipecaches[mc].pkg_pepvpr[fn]
                    depend_tree['providermap'][name] = (pn, version)

        for tid in rq.rqdata.runtaskentries:
            (mc, fn, taskname, taskfn) = bb.runqueue.split_tid_mcfn(tid)
            pn = self.recipecaches[mc].pkg_fn[taskfn]
            pn = self.add_mc_prefix(mc, pn)
            version  = "%s:%s-%s" % self.recipecaches[mc].pkg_pepvpr[taskfn]
            if pn not in depend_tree["pn"]:
                depend_tree["pn"][pn] = {}
                depend_tree["pn"][pn]["filename"] = taskfn
                depend_tree["pn"][pn]["version"] = version
                depend_tree["pn"][pn]["inherits"] = self.recipecaches[mc].inherits.get(taskfn, None)

                # if we have extra caches, list all attributes they bring in
                extra_info = []
                for cache_class in self.caches_array:
                    if type(cache_class) is type and issubclass(cache_class, bb.cache.RecipeInfoCommon) and hasattr(cache_class, 'cachefields'):
                        cachefields = getattr(cache_class, 'cachefields', [])
                        extra_info = extra_info + cachefields

                # for all attributes stored, add them to the dependency tree
                for ei in extra_info:
                    depend_tree["pn"][pn][ei] = vars(self.recipecaches[mc])[ei][taskfn]


            dotname = "%s.%s" % (pn, bb.runqueue.taskname_from_tid(tid))
            if not dotname in depend_tree["tdepends"]:
                depend_tree["tdepends"][dotname] = []
            for dep in rq.rqdata.runtaskentries[tid].depends:
                (depmc, depfn, _, deptaskfn) = bb.runqueue.split_tid_mcfn(dep)
                deppn = self.recipecaches[depmc].pkg_fn[deptaskfn]
                depend_tree["tdepends"][dotname].append("%s.%s" % (deppn, bb.runqueue.taskname_from_tid(dep)))
            if taskfn not in seen_fns:
                seen_fns.append(taskfn)
                packages = []

                depend_tree["depends"][pn] = []
                for dep in taskdata[mc].depids[taskfn]:
                    depend_tree["depends"][pn].append(dep)

                depend_tree["rdepends-pn"][pn] = []
                for rdep in taskdata[mc].rdepids[taskfn]:
                    depend_tree["rdepends-pn"][pn].append(rdep)

                rdepends = self.recipecaches[mc].rundeps[taskfn]
                for package in rdepends:
                    depend_tree["rdepends-pkg"][package] = []
                    for rdepend in rdepends[package]:
                        depend_tree["rdepends-pkg"][package].append(rdepend)
                    packages.append(package)

                rrecs = self.recipecaches[mc].runrecs[taskfn]
                for package in rrecs:
                    depend_tree["rrecs-pkg"][package] = []
                    for rdepend in rrecs[package]:
                        depend_tree["rrecs-pkg"][package].append(rdepend)
                    if not package in packages:
                        packages.append(package)

                for package in packages:
                    if package not in depend_tree["packages"]:
                        depend_tree["packages"][package] = {}
                        depend_tree["packages"][package]["pn"] = pn
                        depend_tree["packages"][package]["filename"] = taskfn
                        depend_tree["packages"][package]["version"] = version

        return depend_tree

    ######## WARNING : this function requires cache_extra to be enabled ########
    def generatePkgDepTreeData(self, pkgs_to_build, task):
        """
        Create a dependency tree of pkgs_to_build, returning the data.
        """
        if not task.startswith("do_"):
            task = "do_%s" % task

        _, taskdata = self.prepareTreeData(pkgs_to_build, task)

        seen_fns = []
        depend_tree = {}
        depend_tree["depends"] = {}
        depend_tree["pn"] = {}
        depend_tree["rdepends-pn"] = {}
        depend_tree["rdepends-pkg"] = {}
        depend_tree["rrecs-pkg"] = {}

        # if we have extra caches, list all attributes they bring in
        extra_info = []
        for cache_class in self.caches_array:
            if type(cache_class) is type and issubclass(cache_class, bb.cache.RecipeInfoCommon) and hasattr(cache_class, 'cachefields'):
                cachefields = getattr(cache_class, 'cachefields', [])
                extra_info = extra_info + cachefields

        tids = []
        for mc in taskdata:
            for tid in taskdata[mc].taskentries:
                tids.append(tid)

        for tid in tids:
            (mc, fn, taskname, taskfn) = bb.runqueue.split_tid_mcfn(tid)

            pn = self.recipecaches[mc].pkg_fn[taskfn]
            pn = self.add_mc_prefix(mc, pn)

            if pn not in depend_tree["pn"]:
                depend_tree["pn"][pn] = {}
                depend_tree["pn"][pn]["filename"] = taskfn
                version  = "%s:%s-%s" % self.recipecaches[mc].pkg_pepvpr[taskfn]
                depend_tree["pn"][pn]["version"] = version
                rdepends = self.recipecaches[mc].rundeps[taskfn]
                rrecs = self.recipecaches[mc].runrecs[taskfn]
                depend_tree["pn"][pn]["inherits"] = self.recipecaches[mc].inherits.get(taskfn, None)

                # for all extra attributes stored, add them to the dependency tree
                for ei in extra_info:
                    depend_tree["pn"][pn][ei] = vars(self.recipecaches[mc])[ei][taskfn]

            if taskfn not in seen_fns:
                seen_fns.append(taskfn)

                depend_tree["depends"][pn] = []
                for dep in taskdata[mc].depids[taskfn]:
                    pn_provider = ""
                    if dep in taskdata[mc].build_targets and taskdata[mc].build_targets[dep]:
                        fn_provider = taskdata[mc].build_targets[dep][0]
                        pn_provider = self.recipecaches[mc].pkg_fn[fn_provider]
                    else:
                        pn_provider = dep
                    pn_provider = self.add_mc_prefix(mc, pn_provider)
                    depend_tree["depends"][pn].append(pn_provider)

                depend_tree["rdepends-pn"][pn] = []
                for rdep in taskdata[mc].rdepids[taskfn]:
                    pn_rprovider = ""
                    if rdep in taskdata[mc].run_targets and taskdata[mc].run_targets[rdep]:
                        fn_rprovider = taskdata[mc].run_targets[rdep][0]
                        pn_rprovider = self.recipecaches[mc].pkg_fn[fn_rprovider]
                    else:
                        pn_rprovider = rdep
                    pn_rprovider = self.add_mc_prefix(mc, pn_rprovider)
                    depend_tree["rdepends-pn"][pn].append(pn_rprovider)

                depend_tree["rdepends-pkg"].update(rdepends)
                depend_tree["rrecs-pkg"].update(rrecs)

        return depend_tree

    def generateDepTreeEvent(self, pkgs_to_build, task):
        """
        Create a task dependency graph of pkgs_to_build.
        Generate an event with the result
        """
        depgraph = self.generateTaskDepTreeData(pkgs_to_build, task)
        bb.event.fire(bb.event.DepTreeGenerated(depgraph), self.data)

    def generateDotGraphFiles(self, pkgs_to_build, task):
        """
        Create a task dependency graph of pkgs_to_build.
        Save the result to a set of .dot files.
        """

        depgraph = self.generateTaskDepTreeData(pkgs_to_build, task)

        with open('pn-buildlist', 'w') as f:
            for pn in depgraph["pn"]:
                f.write(pn + "\n")
        logger.info("PN build list saved to 'pn-buildlist'")

        # Remove old format output files to ensure no confusion with stale data
        try:
            os.unlink('pn-depends.dot')
        except FileNotFoundError:
            pass
        try:
            os.unlink('package-depends.dot')
        except FileNotFoundError:
            pass
        try:
            os.unlink('recipe-depends.dot')
        except FileNotFoundError:
            pass

        with open('task-depends.dot', 'w') as f:
            f.write("digraph depends {\n")
            for task in sorted(depgraph["tdepends"]):
                (pn, taskname) = task.rsplit(".", 1)
                fn = depgraph["pn"][pn]["filename"]
                version = depgraph["pn"][pn]["version"]
                f.write('"%s.%s" [label="%s %s\\n%s\\n%s"]\n' % (pn, taskname, pn, taskname, version, fn))
                for dep in sorted(depgraph["tdepends"][task]):
                    f.write('"%s" -> "%s"\n' % (task, dep))
            f.write("}\n")
        logger.info("Task dependencies saved to 'task-depends.dot'")

    def show_appends_with_no_recipes(self):
        # Determine which bbappends haven't been applied

        # First get list of recipes, including skipped
        recipefns = list(self.recipecaches[''].pkg_fn.keys())
        recipefns.extend(self.skiplist.keys())

        # Work out list of bbappends that have been applied
        applied_appends = []
        for fn in recipefns:
            applied_appends.extend(self.collection.get_file_appends(fn))

        appends_without_recipes = []
        for _, appendfn in self.collection.bbappends:
            if not appendfn in applied_appends:
                appends_without_recipes.append(appendfn)

        if appends_without_recipes:
            msg = 'No recipes available for:\n  %s' % '\n  '.join(appends_without_recipes)
            warn_only = self.data.getVar("BB_DANGLINGAPPENDS_WARNONLY", \
                 False) or "no"
            if warn_only.lower() in ("1", "yes", "true"):
                bb.warn(msg)
            else:
                bb.fatal(msg)

    def handlePrefProviders(self):

        for mc in self.multiconfigs:
            localdata = data.createCopy(self.databuilder.mcdata[mc])
            bb.data.expandKeys(localdata)

            # Handle PREFERRED_PROVIDERS
            for p in (localdata.getVar('PREFERRED_PROVIDERS') or "").split():
                try:
                    (providee, provider) = p.split(':')
                except:
                    providerlog.critical("Malformed option in PREFERRED_PROVIDERS variable: %s" % p)
                    continue
                if providee in self.recipecaches[mc].preferred and self.recipecaches[mc].preferred[providee] != provider:
                    providerlog.error("conflicting preferences for %s: both %s and %s specified", providee, provider, self.recipecaches[mc].preferred[providee])
                self.recipecaches[mc].preferred[providee] = provider

    def findConfigFilePath(self, configfile):
        """
        Find the location on disk of configfile and if it exists and was parsed by BitBake
        emit the ConfigFilePathFound event with the path to the file.
        """
        path = bb.cookerdata.findConfigFile(configfile, self.data)
        if not path:
            return

        # Generate a list of parsed configuration files by searching the files
        # listed in the __depends and __base_depends variables with a .conf suffix.
        conffiles = []
        dep_files = self.data.getVar('__base_depends', False) or []
        dep_files = dep_files + (self.data.getVar('__depends', False) or [])

        for f in dep_files:
            if f[0].endswith(".conf"):
                conffiles.append(f[0])

        _, conf, conffile = path.rpartition("conf/")
        match = os.path.join(conf, conffile)
        # Try and find matches for conf/conffilename.conf as we don't always
        # have the full path to the file.
        for cfg in conffiles:
            if cfg.endswith(match):
                bb.event.fire(bb.event.ConfigFilePathFound(path),
                              self.data)
                break

    def findFilesMatchingInDir(self, filepattern, directory):
        """
        Searches for files containing the substring 'filepattern' which are children of
        'directory' in each BBPATH. i.e. to find all rootfs package classes available
        to BitBake one could call findFilesMatchingInDir(self, 'rootfs_', 'classes')
        or to find all machine configuration files one could call:
        findFilesMatchingInDir(self, '.conf', 'conf/machine')
        """

        matches = []
        bbpaths = self.data.getVar('BBPATH').split(':')
        for path in bbpaths:
            dirpath = os.path.join(path, directory)
            if os.path.exists(dirpath):
                for root, dirs, files in os.walk(dirpath):
                    for f in files:
                        if filepattern in f:
                            matches.append(f)

        if matches:
            bb.event.fire(bb.event.FilesMatchingFound(filepattern, matches), self.data)

    def findProviders(self, mc=''):
        return bb.providers.findProviders(self.databuilder.mcdata[mc], self.recipecaches[mc], self.recipecaches[mc].pkg_pn)

    def findBestProvider(self, pn, mc=''):
        if pn in self.recipecaches[mc].providers:
            filenames = self.recipecaches[mc].providers[pn]
            eligible, foundUnique = bb.providers.filterProviders(filenames, pn, self.databuilder.mcdata[mc], self.recipecaches[mc])
            filename = eligible[0]
            return None, None, None, filename
        elif pn in self.recipecaches[mc].pkg_pn:
            return bb.providers.findBestProvider(pn, self.databuilder.mcdata[mc], self.recipecaches[mc], self.recipecaches[mc].pkg_pn)
        else:
            return None, None, None, None

    def findConfigFiles(self, varname):
        """
        Find config files which are appropriate values for varname.
        i.e. MACHINE, DISTRO
        """
        possible = []
        var = varname.lower()

        data = self.data
        # iterate configs
        bbpaths = data.getVar('BBPATH').split(':')
        for path in bbpaths:
            confpath = os.path.join(path, "conf", var)
            if os.path.exists(confpath):
                for root, dirs, files in os.walk(confpath):
                    # get all child files, these are appropriate values
                    for f in files:
                        val, sep, end = f.rpartition('.')
                        if end == 'conf':
                            possible.append(val)

        if possible:
            bb.event.fire(bb.event.ConfigFilesFound(var, possible), self.data)

    def findInheritsClass(self, klass):
        """
        Find all recipes which inherit the specified class
        """
        pkg_list = []

        for pfn in self.recipecaches[''].pkg_fn:
            inherits = self.recipecaches[''].inherits.get(pfn, None)
            if inherits and klass in inherits:
                pkg_list.append(self.recipecaches[''].pkg_fn[pfn])

        return pkg_list

    def generateTargetsTree(self, klass=None, pkgs=None):
        """
        Generate a dependency tree of buildable targets
        Generate an event with the result
        """
        # if the caller hasn't specified a pkgs list default to universe
        if not pkgs:
            pkgs = ['universe']
        # if inherited_class passed ensure all recipes which inherit the
        # specified class are included in pkgs
        if klass:
            extra_pkgs = self.findInheritsClass(klass)
            pkgs = pkgs + extra_pkgs

        # generate a dependency tree for all our packages
        tree = self.generatePkgDepTreeData(pkgs, 'build')
        bb.event.fire(bb.event.TargetsTreeGenerated(tree), self.data)

    def interactiveMode( self ):
        """Drop off into a shell"""
        try:
            from bb import shell
        except ImportError:
            parselog.exception("Interactive mode not available")
            sys.exit(1)
        else:
            shell.start( self )


    def handleCollections(self, collections):
        """Handle collections"""
        errors = False
        self.bbfile_config_priorities = []
        if collections:
            collection_priorities = {}
            collection_depends = {}
            collection_list = collections.split()
            min_prio = 0
            for c in collection_list:
                bb.debug(1,'Processing %s in collection list' % (c))

                # Get collection priority if defined explicitly
                priority = self.data.getVar("BBFILE_PRIORITY_%s" % c)
                if priority:
                    try:
                        prio = int(priority)
                    except ValueError:
                        parselog.error("invalid value for BBFILE_PRIORITY_%s: \"%s\"", c, priority)
                        errors = True
                    if min_prio == 0 or prio < min_prio:
                        min_prio = prio
                    collection_priorities[c] = prio
                else:
                    collection_priorities[c] = None

                # Check dependencies and store information for priority calculation
                deps = self.data.getVar("LAYERDEPENDS_%s" % c)
                if deps:
                    try:
                        depDict = bb.utils.explode_dep_versions2(deps)
                    except bb.utils.VersionStringException as vse:
                        bb.fatal('Error parsing LAYERDEPENDS_%s: %s' % (c, str(vse)))
                    for dep, oplist in list(depDict.items()):
                        if dep in collection_list:
                            for opstr in oplist:
                                layerver = self.data.getVar("LAYERVERSION_%s" % dep)
                                (op, depver) = opstr.split()
                                if layerver:
                                    try:
                                        res = bb.utils.vercmp_string_op(layerver, depver, op)
                                    except bb.utils.VersionStringException as vse:
                                        bb.fatal('Error parsing LAYERDEPENDS_%s: %s' % (c, str(vse)))
                                    if not res:
                                        parselog.error("Layer '%s' depends on version %s of layer '%s', but version %s is currently enabled in your configuration. Check that you are using the correct matching versions/branches of these two layers.", c, opstr, dep, layerver)
                                        errors = True
                                else:
                                    parselog.error("Layer '%s' depends on version %s of layer '%s', which exists in your configuration but does not specify a version. Check that you are using the correct matching versions/branches of these two layers.", c, opstr, dep)
                                    errors = True
                        else:
                            parselog.error("Layer '%s' depends on layer '%s', but this layer is not enabled in your configuration", c, dep)
                            errors = True
                    collection_depends[c] = list(depDict.keys())
                else:
                    collection_depends[c] = []

                # Check recommends and store information for priority calculation
                recs = self.data.getVar("LAYERRECOMMENDS_%s" % c)
                if recs:
                    try:
                        recDict = bb.utils.explode_dep_versions2(recs)
                    except bb.utils.VersionStringException as vse:
                        bb.fatal('Error parsing LAYERRECOMMENDS_%s: %s' % (c, str(vse)))
                    for rec, oplist in list(recDict.items()):
                        if rec in collection_list:
                            if oplist:
                                opstr = oplist[0]
                                layerver = self.data.getVar("LAYERVERSION_%s" % rec)
                                if layerver:
                                    (op, recver) = opstr.split()
                                    try:
                                        res = bb.utils.vercmp_string_op(layerver, recver, op)
                                    except bb.utils.VersionStringException as vse:
                                        bb.fatal('Error parsing LAYERRECOMMENDS_%s: %s' % (c, str(vse)))
                                    if not res:
                                        parselog.debug(3,"Layer '%s' recommends version %s of layer '%s', but version %s is currently enabled in your configuration. Check that you are using the correct matching versions/branches of these two layers.", c, opstr, rec, layerver)
                                        continue
                                else:
                                    parselog.debug(3,"Layer '%s' recommends version %s of layer '%s', which exists in your configuration but does not specify a version. Check that you are using the correct matching versions/branches of these two layers.", c, opstr, rec)
                                    continue
                            parselog.debug(3,"Layer '%s' recommends layer '%s', so we are adding it", c, rec)
                            collection_depends[c].append(rec)
                        else:
                            parselog.debug(3,"Layer '%s' recommends layer '%s', but this layer is not enabled in your configuration", c, rec)

            # Recursively work out collection priorities based on dependencies
            def calc_layer_priority(collection):
                if not collection_priorities[collection]:
                    max_depprio = min_prio
                    for dep in collection_depends[collection]:
                        calc_layer_priority(dep)
                        depprio = collection_priorities[dep]
                        if depprio > max_depprio:
                            max_depprio = depprio
                    max_depprio += 1
                    parselog.debug(1, "Calculated priority of layer %s as %d", collection, max_depprio)
                    collection_priorities[collection] = max_depprio

            # Calculate all layer priorities using calc_layer_priority and store in bbfile_config_priorities
            for c in collection_list:
                calc_layer_priority(c)
                regex = self.data.getVar("BBFILE_PATTERN_%s" % c)
                if regex is None:
                    parselog.error("BBFILE_PATTERN_%s not defined" % c)
                    errors = True
                    continue
                elif regex == "":
                    parselog.debug(1, "BBFILE_PATTERN_%s is empty" % c)
                    cre = re.compile('^NULL$')
                    errors = False
                else:
                    try:
                        cre = re.compile(regex)
                    except re.error:
                        parselog.error("BBFILE_PATTERN_%s \"%s\" is not a valid regular expression", c, regex)
                        errors = True
                        continue
                self.bbfile_config_priorities.append((c, regex, cre, collection_priorities[c]))
        if errors:
            # We've already printed the actual error(s)
            raise CollectionError("Errors during parsing layer configuration")

    def buildSetVars(self):
        """
        Setup any variables needed before starting a build
        """
        t = time.gmtime()
        for mc in self.databuilder.mcdata:
            ds = self.databuilder.mcdata[mc]
            if not ds.getVar("BUILDNAME", False):
                ds.setVar("BUILDNAME", "${DATE}${TIME}")
            ds.setVar("BUILDSTART", time.strftime('%m/%d/%Y %H:%M:%S', t))
            ds.setVar("DATE", time.strftime('%Y%m%d', t))
            ds.setVar("TIME", time.strftime('%H%M%S', t))

    def reset_mtime_caches(self):
        """
        Reset mtime caches - this is particularly important when memory resident as something
        which is cached is not unlikely to have changed since the last invocation (e.g. a
        file associated with a recipe might have been modified by the user).
        """
        build.reset_cache()
        bb.fetch._checksum_cache.mtime_cache.clear()
        siggen_cache = getattr(bb.parse.siggen, 'checksum_cache', None)
        if siggen_cache:
            bb.parse.siggen.checksum_cache.mtime_cache.clear()

    def matchFiles(self, bf):
        """
        Find the .bb files which match the expression in 'buildfile'.
        """
        if bf.startswith("/") or bf.startswith("../"):
            bf = os.path.abspath(bf)

        self.collection = CookerCollectFiles(self.bbfile_config_priorities)
        filelist, masked, searchdirs = self.collection.collect_bbfiles(self.data, self.data)
        try:
            os.stat(bf)
            bf = os.path.abspath(bf)
            return [bf]
        except OSError:
            regexp = re.compile(bf)
            matches = []
            for f in filelist:
                if regexp.search(f) and os.path.isfile(f):
                    matches.append(f)
            return matches

    def matchFile(self, buildfile):
        """
        Find the .bb file which matches the expression in 'buildfile'.
        Raise an error if multiple files
        """
        matches = self.matchFiles(buildfile)
        if len(matches) != 1:
            if matches:
                msg = "Unable to match '%s' to a specific recipe file - %s matches found:" % (buildfile, len(matches))
                if matches:
                    for f in matches:
                        msg += "\n    %s" % f
                parselog.error(msg)
            else:
                parselog.error("Unable to find any recipe file matching '%s'" % buildfile)
            raise NoSpecificMatch
        return matches[0]

    def buildFile(self, buildfile, task):
        """
        Build the file matching regexp buildfile
        """
        bb.event.fire(bb.event.BuildInit(), self.data)

        # Too many people use -b because they think it's how you normally
        # specify a target to be built, so show a warning
        bb.warn("Buildfile specified, dependencies will not be handled. If this is not what you want, do not use -b / --buildfile.")

        self.buildFileInternal(buildfile, task)

    def buildFileInternal(self, buildfile, task, fireevents=True, quietlog=False):
        """
        Build the file matching regexp buildfile
        """

        # Parse the configuration here. We need to do it explicitly here since
        # buildFile() doesn't use the cache
        self.parseConfiguration()

        # If we are told to do the None task then query the default task
        if task is None:
            task = self.configuration.cmd
        if not task.startswith("do_"):
            task = "do_%s" % task

        fn, cls, mc = bb.cache.virtualfn2realfn(buildfile)
        fn = self.matchFile(fn)

        self.buildSetVars()
        self.reset_mtime_caches()

        bb_cache = bb.cache.Cache(self.databuilder, self.data_hash, self.caches_array)

        infos = bb_cache.parse(fn, self.collection.get_file_appends(fn))
        infos = dict(infos)

        fn = bb.cache.realfn2virtual(fn, cls, mc)
        try:
            info_array = infos[fn]
        except KeyError:
            bb.fatal("%s does not exist" % fn)

        if info_array[0].skipped:
            bb.fatal("%s was skipped: %s" % (fn, info_array[0].skipreason))

        self.recipecaches[mc].add_from_recipeinfo(fn, info_array)

        # Tweak some variables
        item = info_array[0].pn
        self.recipecaches[mc].ignored_dependencies = set()
        self.recipecaches[mc].bbfile_priority[fn] = 1
        self.configuration.limited_deps = True

        # Remove external dependencies
        self.recipecaches[mc].task_deps[fn]['depends'] = {}
        self.recipecaches[mc].deps[fn] = []
        self.recipecaches[mc].rundeps[fn] = defaultdict(list)
        self.recipecaches[mc].runrecs[fn] = defaultdict(list)

        # Invalidate task for target if force mode active
        if self.configuration.force:
            logger.verbose("Invalidate task %s, %s", task, fn)
            bb.parse.siggen.invalidate_task(task, self.recipecaches[mc], fn)

        # Setup taskdata structure
        taskdata = {}
        taskdata[mc] = bb.taskdata.TaskData(self.configuration.abort)
        taskdata[mc].add_provider(self.databuilder.mcdata[mc], self.recipecaches[mc], item)

        if quietlog:
            rqloglevel = bb.runqueue.logger.getEffectiveLevel()
            bb.runqueue.logger.setLevel(logging.WARNING)

        buildname = self.databuilder.mcdata[mc].getVar("BUILDNAME")
        if fireevents:
            bb.event.fire(bb.event.BuildStarted(buildname, [item]), self.databuilder.mcdata[mc])

        # Execute the runqueue
        runlist = [[mc, item, task, fn]]

        rq = bb.runqueue.RunQueue(self, self.data, self.recipecaches, taskdata, runlist)

        def buildFileIdle(server, rq, abort):

            msg = None
            interrupted = 0
            if abort or self.state == state.forceshutdown:
                rq.finish_runqueue(True)
                msg = "Forced shutdown"
                interrupted = 2
            elif self.state == state.shutdown:
                rq.finish_runqueue(False)
                msg = "Stopped build"
                interrupted = 1
            failures = 0
            try:
                retval = rq.execute_runqueue()
            except runqueue.TaskFailure as exc:
                failures += len(exc.args)
                retval = False
            except SystemExit as exc:
                self.command.finishAsyncCommand(str(exc))
                if quietlog:
                    bb.runqueue.logger.setLevel(rqloglevel)
                return False

            if not retval:
                if fireevents:
                    bb.event.fire(bb.event.BuildCompleted(len(rq.rqdata.runtaskentries), buildname, item, failures, interrupted), self.databuilder.mcdata[mc])
                self.command.finishAsyncCommand(msg)
                # We trashed self.recipecaches above
                self.parsecache_valid = False
                self.configuration.limited_deps = False
                bb.parse.siggen.reset(self.data)
                if quietlog:
                    bb.runqueue.logger.setLevel(rqloglevel)
                return False
            if retval is True:
                return True
            return retval

        self.configuration.server_register_idlecallback(buildFileIdle, rq)

    def buildTargets(self, targets, task):
        """
        Attempt to build the targets specified
        """

        def buildTargetsIdle(server, rq, abort):
            msg = None
            interrupted = 0
            if abort or self.state == state.forceshutdown:
                rq.finish_runqueue(True)
                msg = "Forced shutdown"
                interrupted = 2
            elif self.state == state.shutdown:
                rq.finish_runqueue(False)
                msg = "Stopped build"
                interrupted = 1
            failures = 0
            try:
                retval = rq.execute_runqueue()
            except runqueue.TaskFailure as exc:
                failures += len(exc.args)
                retval = False
            except SystemExit as exc:
                self.command.finishAsyncCommand(str(exc))
                return False

            if not retval:
                try:
                    for mc in self.multiconfigs:
                        bb.event.fire(bb.event.BuildCompleted(len(rq.rqdata.runtaskentries), buildname, targets, failures, interrupted), self.databuilder.mcdata[mc])
                finally:
                    self.command.finishAsyncCommand(msg)
                return False
            if retval is True:
                return True
            return retval

        self.reset_mtime_caches()
        self.buildSetVars()

        # If we are told to do the None task then query the default task
        if task is None:
            task = self.configuration.cmd

        if not task.startswith("do_"):
            task = "do_%s" % task

        packages = [target if ':' in target else '%s:%s' % (target, task) for target in targets]

        bb.event.fire(bb.event.BuildInit(packages), self.data)

        taskdata, runlist = self.buildTaskData(targets, task, self.configuration.abort)

        buildname = self.data.getVar("BUILDNAME", False)

        # make targets to always look as <target>:do_<task>
        ntargets = []
        for target in runlist:
            if target[0]:
                ntargets.append("mc:%s:%s:%s" % (target[0], target[1], target[2]))
            ntargets.append("%s:%s" % (target[1], target[2]))

        for mc in self.multiconfigs:
            bb.event.fire(bb.event.BuildStarted(buildname, ntargets), self.databuilder.mcdata[mc])

        rq = bb.runqueue.RunQueue(self, self.data, self.recipecaches, taskdata, runlist)
        if 'universe' in targets:
            rq.rqdata.warn_multi_bb = True

        self.configuration.server_register_idlecallback(buildTargetsIdle, rq)


    def getAllKeysWithFlags(self, flaglist):
        dump = {}
        for k in self.data.keys():
            try:
                expand = True
                flags = self.data.getVarFlags(k)
                if flags and "func" in flags and "python" in flags:
                    expand = False
                v = self.data.getVar(k, expand)
                if not k.startswith("__") and not isinstance(v, bb.data_smart.DataSmart):
                    dump[k] = {
    'v' : str(v) ,
    'history' : self.data.varhistory.variable(k),
                    }
                    for d in flaglist:
                        if flags and d in flags:
                            dump[k][d] = flags[d]
                        else:
                            dump[k][d] = None
            except Exception as e:
                print(e)
        return dump


    def updateCacheSync(self):
        if self.state == state.running:
            return

        # reload files for which we got notifications
        for p in self.inotify_modified_files:
            bb.parse.update_cache(p)
            if p in bb.parse.BBHandler.cached_statements:
                del bb.parse.BBHandler.cached_statements[p]
        self.inotify_modified_files = []

        if not self.baseconfig_valid:
            logger.debug(1, "Reloading base configuration data")
            self.initConfigurationData()
            self.handlePRServ()

    # This is called for all async commands when self.state != running
    def updateCache(self):
        if self.state == state.running:
            return

        if self.state in (state.shutdown, state.forceshutdown, state.error):
            if hasattr(self.parser, 'shutdown'):
                self.parser.shutdown(clean=False, force = True)
            raise bb.BBHandledException()

        if self.state != state.parsing:
            self.updateCacheSync()

        if self.state != state.parsing and not self.parsecache_valid:
            bb.parse.siggen.reset(self.data)
            self.parseConfiguration ()
            if CookerFeatures.SEND_SANITYEVENTS in self.featureset:
                for mc in self.multiconfigs:
                    bb.event.fire(bb.event.SanityCheck(False), self.databuilder.mcdata[mc])

            for mc in self.multiconfigs:
                ignore = self.databuilder.mcdata[mc].getVar("ASSUME_PROVIDED") or ""
                self.recipecaches[mc].ignored_dependencies = set(ignore.split())

                for dep in self.configuration.extra_assume_provided:
                    self.recipecaches[mc].ignored_dependencies.add(dep)

            self.collection = CookerCollectFiles(self.bbfile_config_priorities)
            (filelist, masked, searchdirs) = self.collection.collect_bbfiles(self.data, self.data)

            # Add inotify watches for directories searched for bb/bbappend files
            for dirent in searchdirs:
                self.add_filewatch([[dirent]], dirs=True)

            self.parser = CookerParser(self, filelist, masked)
            self.parsecache_valid = True

        self.state = state.parsing

        if not self.parser.parse_next():
            collectlog.debug(1, "parsing complete")
            if self.parser.error:
                raise bb.BBHandledException()
            self.show_appends_with_no_recipes()
            self.handlePrefProviders()
            for mc in self.multiconfigs:
                self.recipecaches[mc].bbfile_priority = self.collection.collection_priorities(self.recipecaches[mc].pkg_fn, self.data)
            self.state = state.running

            # Send an event listing all stamps reachable after parsing
            # which the metadata may use to clean up stale data
            for mc in self.multiconfigs:
                event = bb.event.ReachableStamps(self.recipecaches[mc].stamp)
                bb.event.fire(event, self.databuilder.mcdata[mc])
            return None

        return True

    def checkPackages(self, pkgs_to_build, task=None):

        # Return a copy, don't modify the original
        pkgs_to_build = pkgs_to_build[:]

        if len(pkgs_to_build) == 0:
            raise NothingToBuild

        ignore = (self.data.getVar("ASSUME_PROVIDED") or "").split()
        for pkg in pkgs_to_build.copy():
            if pkg in ignore:
                parselog.warning("Explicit target \"%s\" is in ASSUME_PROVIDED, ignoring" % pkg)
            if pkg.startswith("multiconfig:"):
                pkgs_to_build.remove(pkg)
                pkgs_to_build.append(pkg.replace("multiconfig:", "mc:"))

        if 'world' in pkgs_to_build:
            pkgs_to_build.remove('world')
            for mc in self.multiconfigs:
                bb.providers.buildWorldTargetList(self.recipecaches[mc], task)
                for t in self.recipecaches[mc].world_target:
                    if mc:
                        t = "mc:" + mc + ":" + t
                    pkgs_to_build.append(t)

        if 'universe' in pkgs_to_build:
            parselog.verbnote("The \"universe\" target is only intended for testing and may produce errors.")
            parselog.debug(1, "collating packages for \"universe\"")
            pkgs_to_build.remove('universe')
            for mc in self.multiconfigs:
                for t in self.recipecaches[mc].universe_target:
                    if task:
                        foundtask = False
                        for provider_fn in self.recipecaches[mc].providers[t]:
                            if task in self.recipecaches[mc].task_deps[provider_fn]['tasks']:
                                foundtask = True
                                break
                        if not foundtask:
                            bb.debug(1, "Skipping %s for universe tasks as task %s doesn't exist" % (t, task))
                            continue
                    if mc:
                        t = "mc:" + mc + ":" + t
                    pkgs_to_build.append(t)

        return pkgs_to_build

    def pre_serve(self):
        # We now are in our own process so we can call this here.
        # PRServ exits if its parent process exits
        self.handlePRServ()
        return

    def post_serve(self):
        prserv.serv.auto_shutdown()
        if self.hashserv:
            self.hashserv.process.terminate()
            self.hashserv.process.join()
        bb.event.fire(CookerExit(), self.data)

    def shutdown(self, force = False):
        if force:
            self.state = state.forceshutdown
        else:
            self.state = state.shutdown

        if self.parser:
            self.parser.shutdown(clean=not force, force=force)

    def finishcommand(self):
        self.state = state.initial

    def reset(self):
        self.initConfigurationData()
        self.handlePRServ()

    def clientComplete(self):
        """Called when the client is done using the server"""
        self.finishcommand()
        self.extraconfigdata = {}
        self.command.reset()
        self.databuilder.reset()
        self.data = self.databuilder.data
        self.parsecache_valid = False
        self.baseconfig_valid = False


class CookerExit(bb.event.Event):
    """
    Notify clients of the Cooker shutdown
    """

    def __init__(self):
        bb.event.Event.__init__(self)


class CookerCollectFiles(object):
    def __init__(self, priorities):
        self.bbappends = []
        # Priorities is a list of tupples, with the second element as the pattern.
        # We need to sort the list with the longest pattern first, and so on to
        # the shortest.  This allows nested layers to be properly evaluated.
        self.bbfile_config_priorities = sorted(priorities, key=lambda tup: tup[1], reverse=True)

    def calc_bbfile_priority( self, filename, matched = None ):
        for _, _, regex, pri in self.bbfile_config_priorities:
            if regex.match(filename):
                if matched is not None:
                    if not regex in matched:
                        matched.add(regex)
                return pri
        return 0

    def get_bbfiles(self):
        """Get list of default .bb files by reading out the current directory"""
        path = os.getcwd()
        contents = os.listdir(path)
        bbfiles = []
        for f in contents:
            if f.endswith(".bb"):
                bbfiles.append(os.path.abspath(os.path.join(path, f)))
        return bbfiles

    def find_bbfiles(self, path):
        """Find all the .bb and .bbappend files in a directory"""
        found = []
        for dir, dirs, files in os.walk(path):
            for ignored in ('SCCS', 'CVS', '.svn'):
                if ignored in dirs:
                    dirs.remove(ignored)
            found += [os.path.join(dir, f) for f in files if (f.endswith(['.bb', '.bbappend']))]

        return found

    def collect_bbfiles(self, config, eventdata):
        """Collect all available .bb build files"""
        masked = 0

        collectlog.debug(1, "collecting .bb files")

        files = (config.getVar( "BBFILES") or "").split()
        config.setVar("BBFILES", " ".join(files))

        # Sort files by priority
        files.sort( key=lambda fileitem: self.calc_bbfile_priority(fileitem) )

        if not len(files):
            files = self.get_bbfiles()

        if not len(files):
            collectlog.error("no recipe files to build, check your BBPATH and BBFILES?")
            bb.event.fire(CookerExit(), eventdata)

        # We need to track where we look so that we can add inotify watches. There
        # is no nice way to do this, this is horrid. We intercept the os.listdir()
        # (or os.scandir() for python 3.6+) calls while we run glob().
        origlistdir = os.listdir
        if hasattr(os, 'scandir'):
            origscandir = os.scandir
        searchdirs = []

        def ourlistdir(d):
            searchdirs.append(d)
            return origlistdir(d)

        def ourscandir(d):
            searchdirs.append(d)
            return origscandir(d)

        os.listdir = ourlistdir
        if hasattr(os, 'scandir'):
            os.scandir = ourscandir
        try:
            # Can't use set here as order is important
            newfiles = []
            for f in files:
                if os.path.isdir(f):
                    dirfiles = self.find_bbfiles(f)
                    for g in dirfiles:
                        if g not in newfiles:
                            newfiles.append(g)
                else:
                    globbed = glob.glob(f)
                    if not globbed and os.path.exists(f):
                        globbed = [f]
                    # glob gives files in order on disk. Sort to be deterministic.
                    for g in sorted(globbed):
                        if g not in newfiles:
                            newfiles.append(g)
        finally:
            os.listdir = origlistdir
            if hasattr(os, 'scandir'):
                os.scandir = origscandir

        bbmask = config.getVar('BBMASK')

        if bbmask:
            # First validate the individual regular expressions and ignore any
            # that do not compile
            bbmasks = []
            for mask in bbmask.split():
                # When constructing an older style single regex, it's possible for BBMASK
                # to end up beginning with '|', which matches and masks _everything_.
                if mask.startswith("|"):
                    collectlog.warning("BBMASK contains regular expression beginning with '|', fixing: %s" % mask)
                    mask = mask[1:]
                try:
                    re.compile(mask)
                    bbmasks.append(mask)
                except sre_constants.error:
                    collectlog.critical("BBMASK contains an invalid regular expression, ignoring: %s" % mask)

            # Then validate the combined regular expressions. This should never
            # fail, but better safe than sorry...
            bbmask = "|".join(bbmasks)
            try:
                bbmask_compiled = re.compile(bbmask)
            except sre_constants.error:
                collectlog.critical("BBMASK is not a valid regular expression, ignoring: %s" % bbmask)
                bbmask = None

        bbfiles = []
        bbappend = []
        for f in newfiles:
            if bbmask and bbmask_compiled.search(f):
                collectlog.debug(1, "skipping masked file %s", f)
                masked += 1
                continue
            if f.endswith('.bb'):
                bbfiles.append(f)
            elif f.endswith('.bbappend'):
                bbappend.append(f)
            else:
                collectlog.debug(1, "skipping %s: unknown file extension", f)

        # Build a list of .bbappend files for each .bb file
        for f in bbappend:
            base = os.path.basename(f).replace('.bbappend', '.bb')
            self.bbappends.append((base, f))

        # Find overlayed recipes
        # bbfiles will be in priority order which makes this easy
        bbfile_seen = dict()
        self.overlayed = defaultdict(list)
        for f in reversed(bbfiles):
            base = os.path.basename(f)
            if base not in bbfile_seen:
                bbfile_seen[base] = f
            else:
                topfile = bbfile_seen[base]
                self.overlayed[topfile].append(f)

        return (bbfiles, masked, searchdirs)

    def get_file_appends(self, fn):
        """
        Returns a list of .bbappend files to apply to fn
        """
        filelist = []
        f = os.path.basename(fn)
        for b in self.bbappends:
            (bbappend, filename) = b
            if (bbappend == f) or ('%' in bbappend and bbappend.startswith(f[:bbappend.index('%')])):
                filelist.append(filename)
        return filelist

    def collection_priorities(self, pkgfns, d):

        priorities = {}

        # Calculate priorities for each file
        matched = set()
        for p in pkgfns:
            realfn, cls, mc = bb.cache.virtualfn2realfn(p)
            priorities[p] = self.calc_bbfile_priority(realfn, matched)

        unmatched = set()
        for _, _, regex, pri in self.bbfile_config_priorities:
            if not regex in matched:
                unmatched.add(regex)

        # Don't show the warning if the BBFILE_PATTERN did match .bbappend files
        def find_bbappend_match(regex):
            for b in self.bbappends:
                (bbfile, append) = b
                if regex.match(append):
                    # If the bbappend is matched by already "matched set", return False
                    for matched_regex in matched:
                        if matched_regex.match(append):
                            return False
                    return True
            return False

        for unmatch in unmatched.copy():
            if find_bbappend_match(unmatch):
                unmatched.remove(unmatch)

        for collection, pattern, regex, _ in self.bbfile_config_priorities:
            if regex in unmatched:
                if d.getVar('BBFILE_PATTERN_IGNORE_EMPTY_%s' % collection) != '1':
                    collectlog.warning("No bb files matched BBFILE_PATTERN_%s '%s'" % (collection, pattern))

        return priorities

class ParsingFailure(Exception):
    def __init__(self, realexception, recipe):
        self.realexception = realexception
        self.recipe = recipe
        Exception.__init__(self, realexception, recipe)

class Parser(multiprocessing.Process):
    def __init__(self, jobs, results, quit, init, profile):
        self.jobs = jobs
        self.results = results
        self.quit = quit
        self.init = init
        multiprocessing.Process.__init__(self)
        self.context = bb.utils.get_context().copy()
        self.handlers = bb.event.get_class_handlers().copy()
        self.profile = profile

    def run(self):

        if not self.profile:
            self.realrun()
            return

        try:
            import cProfile as profile
        except:
            import profile
        prof = profile.Profile()
        try:
            profile.Profile.runcall(prof, self.realrun)
        finally:
            logfile = "profile-parse-%s.log" % multiprocessing.current_process().name
            prof.dump_stats(logfile)

    def realrun(self):
        if self.init:
            self.init()

        pending = []
        while True:
            try:
                self.quit.get_nowait()
            except queue.Empty:
                pass
            else:
                self.results.cancel_join_thread()
                break

            if pending:
                result = pending.pop()
            else:
                try:
                    job = self.jobs.pop()
                except IndexError:
                    break
                result = self.parse(*job)
                # Clear the siggen cache after parsing to control memory usage, its huge
                bb.parse.siggen.postparsing_clean_cache()
            try:
                self.results.put(result, timeout=0.25)
            except queue.Full:
                pending.append(result)

    def parse(self, filename, appends):
        try:
            origfilter = bb.event.LogHandler.filter
            # Record the filename we're parsing into any events generated
            def parse_filter(self, record):
                record.taskpid = bb.event.worker_pid
                record.fn = filename
                return True

            # Reset our environment and handlers to the original settings
            bb.utils.set_context(self.context.copy())
            bb.event.set_class_handlers(self.handlers.copy())
            bb.event.LogHandler.filter = parse_filter

            return True, self.bb_cache.parse(filename, appends)
        except Exception as exc:
            tb = sys.exc_info()[2]
            exc.recipe = filename
            exc.traceback = list(bb.exceptions.extract_traceback(tb, context=3))
            return True, exc
        # Need to turn BaseExceptions into Exceptions here so we gracefully shutdown
        # and for example a worker thread doesn't just exit on its own in response to
        # a SystemExit event for example.
        except BaseException as exc:
            return True, ParsingFailure(exc, filename)
        finally:
            bb.event.LogHandler.filter = origfilter

class CookerParser(object):
    def __init__(self, cooker, filelist, masked):
        self.filelist = filelist
        self.cooker = cooker
        self.cfgdata = cooker.data
        self.cfghash = cooker.data_hash
        self.cfgbuilder = cooker.databuilder

        # Accounting statistics
        self.parsed = 0
        self.cached = 0
        self.error = 0
        self.masked = masked

        self.skipped = 0
        self.virtuals = 0
        self.total = len(filelist)

        self.current = 0
        self.process_names = []

        self.bb_cache = bb.cache.Cache(self.cfgbuilder, self.cfghash, cooker.caches_array)
        self.fromcache = []
        self.willparse = []
        for filename in self.filelist:
            appends = self.cooker.collection.get_file_appends(filename)
            if not self.bb_cache.cacheValid(filename, appends):
                self.willparse.append((filename, appends))
            else:
                self.fromcache.append((filename, appends))
        self.toparse = self.total - len(self.fromcache)
        self.progress_chunk = int(max(self.toparse / 100, 1))

        self.num_processes = min(int(self.cfgdata.getVar("BB_NUMBER_PARSE_THREADS") or
                                 multiprocessing.cpu_count()), len(self.willparse))

        self.start()
        self.haveshutdown = False

    def start(self):
        self.results = self.load_cached()
        self.processes = []
        if self.toparse:
            bb.event.fire(bb.event.ParseStarted(self.toparse), self.cfgdata)
            def init():
                Parser.bb_cache = self.bb_cache
                bb.utils.set_process_name(multiprocessing.current_process().name)
                multiprocessing.util.Finalize(None, bb.codeparser.parser_cache_save, exitpriority=1)
                multiprocessing.util.Finalize(None, bb.fetch.fetcher_parse_save, exitpriority=1)

            self.parser_quit = multiprocessing.Queue(maxsize=self.num_processes)
            self.result_queue = multiprocessing.Queue()

            def chunkify(lst,n):
                return [lst[i::n] for i in range(n)]
            self.jobs = chunkify(self.willparse, self.num_processes)

            for i in range(0, self.num_processes):
                parser = Parser(self.jobs[i], self.result_queue, self.parser_quit, init, self.cooker.configuration.profile)
                parser.start()
                self.process_names.append(parser.name)
                self.processes.append(parser)

            self.results = itertools.chain(self.results, self.parse_generator())

    def shutdown(self, clean=True, force=False):
        if not self.toparse:
            return
        if self.haveshutdown:
            return
        self.haveshutdown = True

        if clean:
            event = bb.event.ParseCompleted(self.cached, self.parsed,
                                            self.skipped, self.masked,
                                            self.virtuals, self.error,
                                            self.total)

            bb.event.fire(event, self.cfgdata)
            for process in self.processes:
                self.parser_quit.put(None)
        else:
            self.parser_quit.cancel_join_thread()
            for process in self.processes:
                self.parser_quit.put(None)

        # Cleanup the queue before call process.join(), otherwise there might be
        # deadlocks.
        while True:
            try:
               self.result_queue.get(timeout=0.25)
            except queue.Empty:
                break

        for process in self.processes:
            if force:
                process.join(.1)
                process.terminate()
            else:
                process.join()

        sync = threading.Thread(target=self.bb_cache.sync)
        sync.start()
        multiprocessing.util.Finalize(None, sync.join, exitpriority=-100)
        bb.codeparser.parser_cache_savemerge()
        bb.fetch.fetcher_parse_done()
        if self.cooker.configuration.profile:
            profiles = []
            for i in self.process_names:
                logfile = "profile-parse-%s.log" % i
                if os.path.exists(logfile):
                    profiles.append(logfile)

            pout = "profile-parse.log.processed"
            bb.utils.process_profilelog(profiles, pout = pout)
            print("Processed parsing statistics saved to %s" % (pout))

    def load_cached(self):
        for filename, appends in self.fromcache:
            cached, infos = self.bb_cache.load(filename, appends)
            yield not cached, infos

    def parse_generator(self):
        while True:
            if self.parsed >= self.toparse:
                break

            try:
                result = self.result_queue.get(timeout=0.25)
            except queue.Empty:
                pass
            else:
                value = result[1]
                if isinstance(value, BaseException):
                    raise value
                else:
                    yield result

    def parse_next(self):
        result = []
        parsed = None
        try:
            parsed, result = next(self.results)
        except StopIteration:
            self.shutdown()
            return False
        except bb.BBHandledException as exc:
            self.error += 1
            logger.error('Failed to parse recipe: %s' % exc.recipe)
            self.shutdown(clean=False, force=True)
            return False
        except ParsingFailure as exc:
            self.error += 1
            logger.error('Unable to parse %s: %s' %
                     (exc.recipe, bb.exceptions.to_string(exc.realexception)))
            self.shutdown(clean=False, force=True)
            return False
        except bb.parse.ParseError as exc:
            self.error += 1
            logger.error(str(exc))
            self.shutdown(clean=False, force=True)
            return False
        except bb.data_smart.ExpansionError as exc:
            self.error += 1
            bbdir = os.path.dirname(__file__) + os.sep
            etype, value, _ = sys.exc_info()
            tb = list(itertools.dropwhile(lambda e: e.filename.startswith(bbdir), exc.traceback))
            logger.error('ExpansionError during parsing %s', value.recipe,
                         exc_info=(etype, value, tb))
            self.shutdown(clean=False, force=True)
            return False
        except Exception as exc:
            self.error += 1
            etype, value, tb = sys.exc_info()
            if hasattr(value, "recipe"):
                logger.error('Unable to parse %s' % value.recipe,
                            exc_info=(etype, value, exc.traceback))
            else:
                # Most likely, an exception occurred during raising an exception
                import traceback
                logger.error('Exception during parse: %s' % traceback.format_exc())
            self.shutdown(clean=False, force=True)
            return False

        self.current += 1
        self.virtuals += len(result)
        if parsed:
            self.parsed += 1
            if self.parsed % self.progress_chunk == 0:
                bb.event.fire(bb.event.ParseProgress(self.parsed, self.toparse),
                              self.cfgdata)
        else:
            self.cached += 1

        for virtualfn, info_array in result:
            if info_array[0].skipped:
                self.skipped += 1
                self.cooker.skiplist[virtualfn] = SkippedPackage(info_array[0])
            (fn, cls, mc) = bb.cache.virtualfn2realfn(virtualfn)
            self.bb_cache.add_info(virtualfn, info_array, self.cooker.recipecaches[mc],
                                        parsed=parsed, watcher = self.cooker.add_filewatch)
        return True

    def reparse(self, filename):
        infos = self.bb_cache.parse(filename, self.cooker.collection.get_file_appends(filename))
        for vfn, info_array in infos:
            (fn, cls, mc) = bb.cache.virtualfn2realfn(vfn)
            self.cooker.recipecaches[mc].add_from_recipeinfo(vfn, info_array)
