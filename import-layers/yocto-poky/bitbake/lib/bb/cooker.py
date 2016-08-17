#!/usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
# Copyright (C) 2003 - 2005 Michael 'Mickey' Lauer
# Copyright (C) 2005        Holger Hans Peter Freyther
# Copyright (C) 2005        ROAD GmbH
# Copyright (C) 2006 - 2007 Richard Purdie
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

from __future__ import print_function
import sys, os, glob, os.path, re, time
import atexit
import itertools
import logging
import multiprocessing
import sre_constants
import threading
from cStringIO import StringIO
from contextlib import closing
from functools import wraps
from collections import defaultdict
import bb, bb.exceptions, bb.command
from bb import utils, data, parse, event, cache, providers, taskdata, runqueue, build
import Queue
import signal
import subprocess
import errno
import prserv.serv
import pyinotify

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
    initial, parsing, running, shutdown, forceshutdown, stopped, error = range(7)

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
    _feature_list = [HOB_EXTRA_CACHES, SEND_DEPENDS_TREE, BASEDATASTORE_TRACKING, SEND_SANITYEVENTS] = range(4)

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

    def next(self):
        return self._features.next()


#============================================================================#
# BBCooker
#============================================================================#
class BBCooker:
    """
    Manages one bitbake build run
    """

    def __init__(self, configuration, featureSet=None):
        self.recipecache = None
        self.skiplist = {}
        self.featureset = CookerFeatures()
        if featureSet:
            for f in featureSet:
                self.featureset.setFeature(f)

        self.configuration = configuration

        self.configwatcher = pyinotify.WatchManager()
        self.configwatcher.bbseen = []
        self.configwatcher.bbwatchedfiles = []
        self.confignotifier = pyinotify.Notifier(self.configwatcher, self.config_notifications)
        self.watchmask = pyinotify.IN_CLOSE_WRITE | pyinotify.IN_CREATE | pyinotify.IN_DELETE | \
                         pyinotify.IN_DELETE_SELF | pyinotify.IN_MODIFY | pyinotify.IN_MOVE_SELF | \
                         pyinotify.IN_MOVED_FROM | pyinotify.IN_MOVED_TO 
        self.watcher = pyinotify.WatchManager()
        self.watcher.bbseen = []
        self.watcher.bbwatchedfiles = []
        self.notifier = pyinotify.Notifier(self.watcher, self.notifications)

        # If being called by something like tinfoil, we need to clean cached data 
        # which may now be invalid
        bb.parse.__mtime_cache = {}
        bb.parse.BBHandler.cached_statements = {}

        self.initConfigurationData()

        self.inotify_modified_files = []

        def _process_inotify_updates(server, notifier_list, abort):
            for n in notifier_list:
                if n.check_events(timeout=0):
                    # read notified events and enqeue them
                    n.read_events()
                    n.process_events()
            return 1.0

        self.configuration.server_register_idlecallback(_process_inotify_updates, [self.confignotifier, self.notifier])

        self.baseconfig_valid = True
        self.parsecache_valid = False

        # Take a lock so only one copy of bitbake can run against a given build
        # directory at a time
        if not self.lockBitbake():
            bb.fatal("Only one copy of bitbake should be run against a build directory")
        try:
            self.lock.seek(0)
            self.lock.truncate()
            if len(configuration.interface) >= 2:
                self.lock.write("%s:%s\n" % (configuration.interface[0], configuration.interface[1]));
            self.lock.flush()
        except:
            pass

        # TOSTOP must not be set or our children will hang when they output
        fd = sys.stdout.fileno()
        if os.isatty(fd):
            import termios
            tcattr = termios.tcgetattr(fd)
            if tcattr[3] & termios.TOSTOP:
                buildlog.info("The terminal had the TOSTOP bit set, clearing...")
                tcattr[3] = tcattr[3] & ~termios.TOSTOP
                termios.tcsetattr(fd, termios.TCSANOW, tcattr)

        self.command = bb.command.Command(self)
        self.state = state.initial

        self.parser = None

        signal.signal(signal.SIGTERM, self.sigterm_exception)
        # Let SIGHUP exit as SIGTERM
        signal.signal(signal.SIGHUP, self.sigterm_exception)

    def config_notifications(self, event):
        if not event.pathname in self.configwatcher.bbwatchedfiles:
            return
        if not event.pathname in self.inotify_modified_files:
            self.inotify_modified_files.append(event.pathname)
        self.baseconfig_valid = False

    def notifications(self, event):
        if not event.pathname in self.inotify_modified_files:
            self.inotify_modified_files.append(event.pathname)
        self.parsecache_valid = False

    def add_filewatch(self, deps, watcher=None):
        if not watcher:
            watcher = self.watcher
        for i in deps:
            watcher.bbwatchedfiles.append(i[0])
            f = os.path.dirname(i[0])
            if f in watcher.bbseen:
                continue
            watcher.bbseen.append(f)
            watchtarget = None
            while True:
                # We try and add watches for files that don't exist but if they did, would influence
                # the parser. The parent directory of these files may not exist, in which case we need 
                # to watch any parent that does exist for changes.
                try:
                    watcher.add_watch(f, self.watchmask, quiet=False)
                    if watchtarget:
                        watcher.bbwatchedfiles.append(watchtarget)
                    break
                except pyinotify.WatchManagerError as e:
                    if 'ENOENT' in str(e):
                        watchtarget = f
                        f = os.path.dirname(f)
                        if f in watcher.bbseen:
                            break
                        watcher.bbseen.append(f)
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
            consolelog = self.data.getVar("BB_CONSOLELOG", True)

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

        if consolelog:
            self.data.setVar("BB_CONSOLELOG", consolelog)

        # we log all events to a file if so directed
        if self.configuration.writeeventlog:
            import json, pickle
            DEFAULT_EVENTFILE = self.configuration.writeeventlog
            class EventLogWriteHandler():

                class EventWriter():
                    def __init__(self, cooker):
                        self.file_inited = None
                        self.cooker = cooker
                        self.event_queue = []

                    def init_file(self):
                        try:
                            # delete the old log
                            os.remove(DEFAULT_EVENTFILE)
                        except:
                            pass

                        # write current configuration data
                        with open(DEFAULT_EVENTFILE, "w") as f:
                            f.write("%s\n" % json.dumps({ "allvariables" : self.cooker.getAllKeysWithFlags(["doc", "func"])}))

                    def write_event(self, event):
                        with open(DEFAULT_EVENTFILE, "a") as f:
                            try:
                                f.write("%s\n" % json.dumps({"class":event.__module__ + "." + event.__class__.__name__, "vars":json.dumps(pickle.dumps(event)) }))
                            except Exception as e:
                                import traceback
                                print(e, traceback.format_exc(e))


                    def send(self, event):
                        event_class = event.__module__ + "." + event.__class__.__name__

                        # init on bb.event.BuildStarted
                        if self.file_inited is None:
                            if  event_class == "bb.event.BuildStarted":
                                self.init_file()
                                self.file_inited = True

                                # write pending events
                                for e in self.event_queue:
                                    self.write_event(e)

                                # also write the current event
                                self.write_event(event)

                            else:
                                # queue all events until the file is inited
                                self.event_queue.append(event)

                        else:
                            # we have the file, just write the event
                            self.write_event(event)

                # set our handler's event processor
                event = EventWriter(self)       # self is the cooker here


            # set up cooker features for this mock UI handler

            # we need to write the dependency tree in the log
            self.featureset.setFeature(CookerFeatures.SEND_DEPENDS_TREE)
            # register the log file writer as UI Handler
            bb.event.register_UIHhandler(EventLogWriteHandler())


        #
        # Copy of the data store which has been expanded.
        # Used for firing events and accessing variables where expansion needs to be accounted for
        #
        self.expanded_data = bb.data.createCopy(self.data)
        bb.data.update_data(self.expanded_data)
        bb.parse.init_parser(self.expanded_data)

        if CookerFeatures.BASEDATASTORE_TRACKING in self.featureset:
            self.disableDataTracking()

        self.data.renameVar("__depends", "__base_depends")
        self.add_filewatch(self.data.getVar("__base_depends", False), self.configwatcher)


    def enableDataTracking(self):
        self.configuration.tracking = True
        if hasattr(self, "data"):
            self.data.enableTracking()

    def disableDataTracking(self):
        self.configuration.tracking = False
        if hasattr(self, "data"):
            self.data.disableTracking()

    def modifyConfigurationVar(self, var, val, default_file, op):
        if op == "append":
            self.appendConfigurationVar(var, val, default_file)
        elif op == "set":
            self.saveConfigurationVar(var, val, default_file, "=")
        elif op == "earlyAssign":
            self.saveConfigurationVar(var, val, default_file, "?=")


    def appendConfigurationVar(self, var, val, default_file):
        #add append var operation to the end of default_file
        default_file = bb.cookerdata.findConfigFile(default_file, self.data)

        total = "#added by hob"
        total += "\n%s += \"%s\"\n" % (var, val)

        with open(default_file, 'a') as f:
            f.write(total)

        #add to history
        loginfo = {"op":"append", "file":default_file, "line":total.count("\n")}
        self.data.appendVar(var, val, **loginfo)

    def saveConfigurationVar(self, var, val, default_file, op):

        replaced = False
        #do not save if nothing changed
        if str(val) == self.data.getVar(var, False):
            return

        conf_files = self.data.varhistory.get_variable_files(var)

        #format the value when it is a list
        if isinstance(val, list):
            listval = ""
            for value in val:
                listval += "%s   " % value
            val = listval

        topdir = self.data.getVar("TOPDIR", False)

        #comment or replace operations made on var
        for conf_file in conf_files:
            if topdir in conf_file:
                with open(conf_file, 'r') as f:
                    contents = f.readlines()

                lines = self.data.varhistory.get_variable_lines(var, conf_file)
                for line in lines:
                    total = ""
                    i = 0
                    for c in contents:
                        total += c
                        i = i + 1
                        if i==int(line):
                            end_index = len(total)
                    index = total.rfind(var, 0, end_index)

                    begin_line = total.count("\n",0,index)
                    end_line = int(line)

                    #check if the variable was saved before in the same way
                    #if true it replace the place where the variable was declared
                    #else it comments it
                    if contents[begin_line-1]== "#added by hob\n":
                        contents[begin_line] = "%s %s \"%s\"\n" % (var, op, val)
                        replaced = True
                    else:
                        for ii in range(begin_line, end_line):
                            contents[ii] = "#" + contents[ii]

                with open(conf_file, 'w') as f:
                    f.writelines(contents)

        if replaced == False:
            #remove var from history
            self.data.varhistory.del_var_history(var)

            #add var to the end of default_file
            default_file = bb.cookerdata.findConfigFile(default_file, self.data)

            #add the variable on a single line, to be easy to replace the second time
            total = "\n#added by hob"
            total += "\n%s %s \"%s\"\n" % (var, op, val)

            with open(default_file, 'a') as f:
                f.write(total)

            #add to history
            loginfo = {"op":"set", "file":default_file, "line":total.count("\n")}
            self.data.setVar(var, val, **loginfo)

    def removeConfigurationVar(self, var):
        conf_files = self.data.varhistory.get_variable_files(var)
        topdir = self.data.getVar("TOPDIR", False)

        for conf_file in conf_files:
            if topdir in conf_file:
                with open(conf_file, 'r') as f:
                    contents = f.readlines()

                lines = self.data.varhistory.get_variable_lines(var, conf_file)
                for line in lines:
                    total = ""
                    i = 0
                    for c in contents:
                        total += c
                        i = i + 1
                        if i==int(line):
                            end_index = len(total)
                    index = total.rfind(var, 0, end_index)

                    begin_line = total.count("\n",0,index)

                    #check if the variable was saved before in the same way
                    if contents[begin_line-1]== "#added by hob\n":
                        contents[begin_line-1] = contents[begin_line] = "\n"
                    else:
                        contents[begin_line] = "\n"
                    #remove var from history
                    self.data.varhistory.del_var_history(var, conf_file, line)
                    #remove variable
                    self.data.delVar(var)

                with open(conf_file, 'w') as f:
                    f.writelines(contents)

    def createConfigFile(self, name):
        path = os.getcwd()
        confpath = os.path.join(path, "conf", name)
        open(confpath, 'w').close()

    def parseConfiguration(self):
        # Set log file verbosity
        verboselogs = bb.utils.to_boolean(self.data.getVar("BB_VERBOSE_LOGS", False))
        if verboselogs:
            bb.msg.loggerVerboseLogs = True

        # Change nice level if we're asked to
        nice = self.data.getVar("BB_NICE_LEVEL", True)
        if nice:
            curnice = os.nice(0)
            nice = int(nice) - curnice
            buildlog.verbose("Renice to %s " % os.nice(nice))

        if self.recipecache:
            del self.recipecache
        self.recipecache = bb.cache.CacheData(self.caches_array)

        self.handleCollections( self.data.getVar("BBFILE_COLLECTIONS", True) )

    def updateConfigOpts(self, options, environment):
        clean = True
        for o in options:
            if o in ['prefile', 'postfile']:
                clean = False
                server_val = getattr(self.configuration, "%s_server" % o)
                if not options[o] and server_val:
                    # restore value provided on server start
                    setattr(self.configuration, o, server_val)
                    continue
            setattr(self.configuration, o, options[o])
        for k in bb.utils.approved_variables():
            if k in environment and k not in self.configuration.env:
                logger.debug(1, "Updating environment variable %s to %s" % (k, environment[k]))
                self.configuration.env[k] = environment[k]
                clean = False
            if k in self.configuration.env and k not in environment:
                logger.debug(1, "Updating environment variable %s (deleted)" % (k))
                del self.configuration.env[k]
                clean = False
            if k not in self.configuration.env and k not in environment:
                 continue
            if environment[k] != self.configuration.env[k]:
                logger.debug(1, "Updating environment variable %s to %s" % (k, environment[k]))
                self.configuration.env[k] = environment[k]
                clean = False
        if not clean:
            logger.debug(1, "Base environment change, triggering reparse")
            self.baseconfig_valid = False        
            self.reset()

    def runCommands(self, server, data, abort):
        """
        Run any queued asynchronous command
        This is done by the idle handler so it runs in true context rather than
        tied to any UI.
        """

        return self.command.runAsyncCommand()

    def showVersions(self):

        pkg_pn = self.recipecache.pkg_pn
        (latest_versions, preferred_versions) = bb.providers.findProviders(self.data, self.recipecache, pkg_pn)

        logger.plain("%-35s %25s %25s", "Recipe Name", "Latest Version", "Preferred Version")
        logger.plain("%-35s %25s %25s\n", "===========", "==============", "=================")

        for p in sorted(pkg_pn):
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
        if not pkgs_to_build:
            pkgs_to_build = []

        if buildfile:
            # Parse the configuration here. We need to do it explicitly here since
            # this showEnvironment() code path doesn't use the cache
            self.parseConfiguration()

            fn, cls = bb.cache.Cache.virtualfn2realfn(buildfile)
            fn = self.matchFile(fn)
            fn = bb.cache.Cache.realfn2virtual(fn, cls)
        elif len(pkgs_to_build) == 1:
            ignore = self.expanded_data.getVar("ASSUME_PROVIDED", True) or ""
            if pkgs_to_build[0] in set(ignore.split()):
                bb.fatal("%s is in ASSUME_PROVIDED" % pkgs_to_build[0])

            taskdata, runlist, pkgs_to_build = self.buildTaskData(pkgs_to_build, None, self.configuration.abort, allowincomplete=True)

            targetid = taskdata.getbuild_id(pkgs_to_build[0])
            fnid = taskdata.build_targets[targetid][0]
            fn = taskdata.fn_index[fnid]
        else:
            envdata = self.data

        if fn:
            try:
                envdata = bb.cache.Cache.loadDataFull(fn, self.collection.get_file_appends(fn), self.data)
            except Exception as e:
                parselog.exception("Unable to read %s", fn)
                raise

        # Display history
        with closing(StringIO()) as env:
            self.data.inchistory.emit(env)
            logger.plain(env.getvalue())

        # emit variables and shell functions
        data.update_data(envdata)
        with closing(StringIO()) as env:
            data.emit_env(env, envdata, True)
            logger.plain(env.getvalue())

        # emit the metadata which isnt valid shell
        data.expandKeys(envdata)
        for e in envdata.keys():
            if data.getVarFlag( e, 'python', envdata ):
                logger.plain("\npython %s () {\n%s}\n", e, envdata.getVar(e, False))


    def buildTaskData(self, pkgs_to_build, task, abort, allowincomplete=False):
        """
        Prepare a runqueue and taskdata object for iteration over pkgs_to_build
        """
        bb.event.fire(bb.event.TreeDataPreparationStarted(), self.data)

        # A task of None means use the default task
        if task is None:
            task = self.configuration.cmd

        fulltargetlist = self.checkPackages(pkgs_to_build)

        localdata = data.createCopy(self.data)
        bb.data.update_data(localdata)
        bb.data.expandKeys(localdata)
        taskdata = bb.taskdata.TaskData(abort, skiplist=self.skiplist, allowincomplete=allowincomplete)

        current = 0
        runlist = []
        for k in fulltargetlist:
            ktask = task
            if ":do_" in k:
                k2 = k.split(":do_")
                k = k2[0]
                ktask = k2[1]
            taskdata.add_provider(localdata, self.recipecache, k)
            current += 1
            if not ktask.startswith("do_"):
                ktask = "do_%s" % ktask
            runlist.append([k, ktask])
            bb.event.fire(bb.event.TreeDataPreparationProgress(current, len(fulltargetlist)), self.data)
        taskdata.add_unresolved(localdata, self.recipecache)
        bb.event.fire(bb.event.TreeDataPreparationCompleted(len(fulltargetlist)), self.data)
        return taskdata, runlist, fulltargetlist

    def prepareTreeData(self, pkgs_to_build, task):
        """
        Prepare a runqueue and taskdata object for iteration over pkgs_to_build
        """

        # We set abort to False here to prevent unbuildable targets raising
        # an exception when we're just generating data
        taskdata, runlist, pkgs_to_build = self.buildTaskData(pkgs_to_build, task, False, allowincomplete=True)

        return runlist, taskdata

    ######## WARNING : this function requires cache_extra to be enabled ########

    def generateTaskDepTreeData(self, pkgs_to_build, task):
        """
        Create a dependency graph of pkgs_to_build including reverse dependency
        information.
        """
        runlist, taskdata = self.prepareTreeData(pkgs_to_build, task)
        rq = bb.runqueue.RunQueue(self, self.data, self.recipecache, taskdata, runlist)
        rq.rqdata.prepare()
        return self.buildDependTree(rq, taskdata)


    def buildDependTree(self, rq, taskdata):
        seen_fnids = []
        depend_tree = {}
        depend_tree["depends"] = {}
        depend_tree["tdepends"] = {}
        depend_tree["pn"] = {}
        depend_tree["rdepends-pn"] = {}
        depend_tree["packages"] = {}
        depend_tree["rdepends-pkg"] = {}
        depend_tree["rrecs-pkg"] = {}
        depend_tree['providermap'] = {}
        depend_tree["layer-priorities"] = self.recipecache.bbfile_config_priorities

        for name, fn in taskdata.get_providermap().iteritems():
            pn = self.recipecache.pkg_fn[fn]
            if name != pn:
                version = "%s:%s-%s" % self.recipecache.pkg_pepvpr[fn]
                depend_tree['providermap'][name] = (pn, version)

        for task in xrange(len(rq.rqdata.runq_fnid)):
            taskname = rq.rqdata.runq_task[task]
            fnid = rq.rqdata.runq_fnid[task]
            fn = taskdata.fn_index[fnid]
            pn = self.recipecache.pkg_fn[fn]
            version  = "%s:%s-%s" % self.recipecache.pkg_pepvpr[fn]
            if pn not in depend_tree["pn"]:
                depend_tree["pn"][pn] = {}
                depend_tree["pn"][pn]["filename"] = fn
                depend_tree["pn"][pn]["version"] = version
                depend_tree["pn"][pn]["inherits"] = self.recipecache.inherits.get(fn, None)

                # if we have extra caches, list all attributes they bring in
                extra_info = []
                for cache_class in self.caches_array:
                    if type(cache_class) is type and issubclass(cache_class, bb.cache.RecipeInfoCommon) and hasattr(cache_class, 'cachefields'):
                        cachefields = getattr(cache_class, 'cachefields', [])
                        extra_info = extra_info + cachefields

                # for all attributes stored, add them to the dependency tree
                for ei in extra_info:
                    depend_tree["pn"][pn][ei] = vars(self.recipecache)[ei][fn]


            for dep in rq.rqdata.runq_depends[task]:
                depfn = taskdata.fn_index[rq.rqdata.runq_fnid[dep]]
                deppn = self.recipecache.pkg_fn[depfn]
                dotname = "%s.%s" % (pn, rq.rqdata.runq_task[task])
                if not dotname in depend_tree["tdepends"]:
                    depend_tree["tdepends"][dotname] = []
                depend_tree["tdepends"][dotname].append("%s.%s" % (deppn, rq.rqdata.runq_task[dep]))
            if fnid not in seen_fnids:
                seen_fnids.append(fnid)
                packages = []

                depend_tree["depends"][pn] = []
                for dep in taskdata.depids[fnid]:
                    depend_tree["depends"][pn].append(taskdata.build_names_index[dep])

                depend_tree["rdepends-pn"][pn] = []
                for rdep in taskdata.rdepids[fnid]:
                    depend_tree["rdepends-pn"][pn].append(taskdata.run_names_index[rdep])

                rdepends = self.recipecache.rundeps[fn]
                for package in rdepends:
                    depend_tree["rdepends-pkg"][package] = []
                    for rdepend in rdepends[package]:
                        depend_tree["rdepends-pkg"][package].append(rdepend)
                    packages.append(package)

                rrecs = self.recipecache.runrecs[fn]
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
                        depend_tree["packages"][package]["filename"] = fn
                        depend_tree["packages"][package]["version"] = version

        return depend_tree

    ######## WARNING : this function requires cache_extra to be enabled ########
    def generatePkgDepTreeData(self, pkgs_to_build, task):
        """
        Create a dependency tree of pkgs_to_build, returning the data.
        """
        _, taskdata = self.prepareTreeData(pkgs_to_build, task)
        tasks_fnid = []
        if len(taskdata.tasks_name) != 0:
            for task in xrange(len(taskdata.tasks_name)):
                tasks_fnid.append(taskdata.tasks_fnid[task])

        seen_fnids = []
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

        for task in xrange(len(tasks_fnid)):
            fnid = tasks_fnid[task]
            fn = taskdata.fn_index[fnid]
            pn = self.recipecache.pkg_fn[fn]

            if pn not in depend_tree["pn"]:
                depend_tree["pn"][pn] = {}
                depend_tree["pn"][pn]["filename"] = fn
                version  = "%s:%s-%s" % self.recipecache.pkg_pepvpr[fn]
                depend_tree["pn"][pn]["version"] = version
                rdepends = self.recipecache.rundeps[fn]
                rrecs = self.recipecache.runrecs[fn]
                depend_tree["pn"][pn]["inherits"] = self.recipecache.inherits.get(fn, None)

                # for all extra attributes stored, add them to the dependency tree
                for ei in extra_info:
                    depend_tree["pn"][pn][ei] = vars(self.recipecache)[ei][fn]

            if fnid not in seen_fnids:
                seen_fnids.append(fnid)

                depend_tree["depends"][pn] = []
                for dep in taskdata.depids[fnid]:
                    item = taskdata.build_names_index[dep]
                    pn_provider = ""
                    targetid = taskdata.getbuild_id(item)
                    if targetid in taskdata.build_targets and taskdata.build_targets[targetid]:
                        id = taskdata.build_targets[targetid][0]
                        fn_provider = taskdata.fn_index[id]
                        pn_provider = self.recipecache.pkg_fn[fn_provider]
                    else:
                        pn_provider = item
                    depend_tree["depends"][pn].append(pn_provider)

                depend_tree["rdepends-pn"][pn] = []
                for rdep in taskdata.rdepids[fnid]:
                    item = taskdata.run_names_index[rdep]
                    pn_rprovider = ""
                    targetid = taskdata.getrun_id(item)
                    if targetid in taskdata.run_targets and taskdata.run_targets[targetid]:
                        id = taskdata.run_targets[targetid][0]
                        fn_rprovider = taskdata.fn_index[id]
                        pn_rprovider = self.recipecache.pkg_fn[fn_rprovider]
                    else:
                        pn_rprovider = item
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

        # Prints a flattened form of package-depends below where subpackages of a package are merged into the main pn
        depends_file = file('pn-depends.dot', 'w' )
        buildlist_file = file('pn-buildlist', 'w' )
        print("digraph depends {", file=depends_file)
        for pn in depgraph["pn"]:
            fn = depgraph["pn"][pn]["filename"]
            version = depgraph["pn"][pn]["version"]
            print('"%s" [label="%s %s\\n%s"]' % (pn, pn, version, fn), file=depends_file)
            print("%s" % pn, file=buildlist_file)
        buildlist_file.close()
        logger.info("PN build list saved to 'pn-buildlist'")
        for pn in depgraph["depends"]:
            for depend in depgraph["depends"][pn]:
                print('"%s" -> "%s" [style=solid]' % (pn, depend), file=depends_file)
        for pn in depgraph["rdepends-pn"]:
            for rdepend in depgraph["rdepends-pn"][pn]:
                print('"%s" -> "%s" [style=dashed]' % (pn, rdepend), file=depends_file)
        print("}", file=depends_file)
        logger.info("PN dependencies saved to 'pn-depends.dot'")

        depends_file = file('package-depends.dot', 'w' )
        print("digraph depends {", file=depends_file)
        for package in depgraph["packages"]:
            pn = depgraph["packages"][package]["pn"]
            fn = depgraph["packages"][package]["filename"]
            version = depgraph["packages"][package]["version"]
            if package == pn:
                print('"%s" [label="%s %s\\n%s"]' % (pn, pn, version, fn), file=depends_file)
            else:
                print('"%s" [label="%s(%s) %s\\n%s"]' % (package, package, pn, version, fn), file=depends_file)
            for depend in depgraph["depends"][pn]:
                print('"%s" -> "%s" [style=solid]' % (package, depend), file=depends_file)
        for package in depgraph["rdepends-pkg"]:
            for rdepend in depgraph["rdepends-pkg"][package]:
                print('"%s" -> "%s" [style=dashed]' % (package, rdepend), file=depends_file)
        for package in depgraph["rrecs-pkg"]:
            for rdepend in depgraph["rrecs-pkg"][package]:
                print('"%s" -> "%s" [style=dotted]' % (package, rdepend), file=depends_file)
        print("}", file=depends_file)
        logger.info("Package dependencies saved to 'package-depends.dot'")

        tdepends_file = file('task-depends.dot', 'w' )
        print("digraph depends {", file=tdepends_file)
        for task in depgraph["tdepends"]:
            (pn, taskname) = task.rsplit(".", 1)
            fn = depgraph["pn"][pn]["filename"]
            version = depgraph["pn"][pn]["version"]
            print('"%s.%s" [label="%s %s\\n%s\\n%s"]' % (pn, taskname, pn, taskname, version, fn), file=tdepends_file)
            for dep in depgraph["tdepends"][task]:
                print('"%s" -> "%s"' % (task, dep), file=tdepends_file)
        print("}", file=tdepends_file)
        logger.info("Task dependencies saved to 'task-depends.dot'")

    def show_appends_with_no_recipes(self):
        # Determine which bbappends haven't been applied

        # First get list of recipes, including skipped
        recipefns = self.recipecache.pkg_fn.keys()
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

        localdata = data.createCopy(self.data)
        bb.data.update_data(localdata)
        bb.data.expandKeys(localdata)

        # Handle PREFERRED_PROVIDERS
        for p in (localdata.getVar('PREFERRED_PROVIDERS', True) or "").split():
            try:
                (providee, provider) = p.split(':')
            except:
                providerlog.critical("Malformed option in PREFERRED_PROVIDERS variable: %s" % p)
                continue
            if providee in self.recipecache.preferred and self.recipecache.preferred[providee] != provider:
                providerlog.error("conflicting preferences for %s: both %s and %s specified", providee, provider, self.recipecache.preferred[providee])
            self.recipecache.preferred[providee] = provider

    def findCoreBaseFiles(self, subdir, configfile):
        corebase = self.data.getVar('COREBASE', True) or ""
        paths = []
        for root, dirs, files in os.walk(corebase + '/' + subdir):
            for d in dirs:
                configfilepath = os.path.join(root, d, configfile)
                if os.path.exists(configfilepath):
                    paths.append(os.path.join(root, d))

        if paths:
            bb.event.fire(bb.event.CoreBaseFilesFound(paths), self.data)

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
        bbpaths = self.data.getVar('BBPATH', True).split(':')
        for path in bbpaths:
            dirpath = os.path.join(path, directory)
            if os.path.exists(dirpath):
                for root, dirs, files in os.walk(dirpath):
                    for f in files:
                        if filepattern in f:
                            matches.append(f)

        if matches:
            bb.event.fire(bb.event.FilesMatchingFound(filepattern, matches), self.data)

    def findConfigFiles(self, varname):
        """
        Find config files which are appropriate values for varname.
        i.e. MACHINE, DISTRO
        """
        possible = []
        var = varname.lower()

        data = self.data
        # iterate configs
        bbpaths = data.getVar('BBPATH', True).split(':')
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

        for pfn in self.recipecache.pkg_fn:
            inherits = self.recipecache.inherits.get(pfn, None)
            if inherits and klass in inherits:
                pkg_list.append(self.recipecache.pkg_fn[pfn])

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


    def handleCollections( self, collections ):
        """Handle collections"""
        errors = False
        self.recipecache.bbfile_config_priorities = []
        if collections:
            collection_priorities = {}
            collection_depends = {}
            collection_list = collections.split()
            min_prio = 0
            for c in collection_list:
                # Get collection priority if defined explicitly
                priority = self.data.getVar("BBFILE_PRIORITY_%s" % c, True)
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
                deps = self.data.getVar("LAYERDEPENDS_%s" % c, True)
                if deps:
                    try:
                        deplist = bb.utils.explode_dep_versions2(deps)
                    except bb.utils.VersionStringException as vse:
                        bb.fatal('Error parsing LAYERDEPENDS_%s: %s' % (c, str(vse)))
                    for dep, oplist in deplist.iteritems():
                        if dep in collection_list:
                            for opstr in oplist:
                                layerver = self.data.getVar("LAYERVERSION_%s" % dep, True)
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
                    collection_depends[c] = deplist.keys()
                else:
                    collection_depends[c] = []

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
                regex = self.data.getVar("BBFILE_PATTERN_%s" % c, True)
                if regex == None:
                    parselog.error("BBFILE_PATTERN_%s not defined" % c)
                    errors = True
                    continue
                try:
                    cre = re.compile(regex)
                except re.error:
                    parselog.error("BBFILE_PATTERN_%s \"%s\" is not a valid regular expression", c, regex)
                    errors = True
                    continue
                self.recipecache.bbfile_config_priorities.append((c, regex, cre, collection_priorities[c]))
        if errors:
            # We've already printed the actual error(s)
            raise CollectionError("Errors during parsing layer configuration")

    def buildSetVars(self):
        """
        Setup any variables needed before starting a build
        """
        t = time.gmtime() 
        if not self.data.getVar("BUILDNAME", False):
            self.data.setVar("BUILDNAME", "${DATE}${TIME}")
        self.data.setVar("BUILDSTART", time.strftime('%m/%d/%Y %H:%M:%S', t))
        self.data.setVar("DATE", time.strftime('%Y%m%d', t))
        self.data.setVar("TIME", time.strftime('%H%M%S', t))

    def matchFiles(self, bf):
        """
        Find the .bb files which match the expression in 'buildfile'.
        """
        if bf.startswith("/") or bf.startswith("../"):
            bf = os.path.abspath(bf)

        self.collection = CookerCollectFiles(self.recipecache.bbfile_config_priorities)
        filelist, masked = self.collection.collect_bbfiles(self.data, self.expanded_data)
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

        # Too many people use -b because they think it's how you normally
        # specify a target to be built, so show a warning
        bb.warn("Buildfile specified, dependencies will not be handled. If this is not what you want, do not use -b / --buildfile.")

        # Parse the configuration here. We need to do it explicitly here since
        # buildFile() doesn't use the cache
        self.parseConfiguration()

        # If we are told to do the None task then query the default task
        if (task == None):
            task = self.configuration.cmd

        fn, cls = bb.cache.Cache.virtualfn2realfn(buildfile)
        fn = self.matchFile(fn)

        self.buildSetVars()

        infos = bb.cache.Cache.parse(fn, self.collection.get_file_appends(fn), \
                                     self.data,
                                     self.caches_array)
        infos = dict(infos)

        fn = bb.cache.Cache.realfn2virtual(fn, cls)
        try:
            info_array = infos[fn]
        except KeyError:
            bb.fatal("%s does not exist" % fn)

        if info_array[0].skipped:
            bb.fatal("%s was skipped: %s" % (fn, info_array[0].skipreason))

        self.recipecache.add_from_recipeinfo(fn, info_array)

        # Tweak some variables
        item = info_array[0].pn
        self.recipecache.ignored_dependencies = set()
        self.recipecache.bbfile_priority[fn] = 1

        # Remove external dependencies
        self.recipecache.task_deps[fn]['depends'] = {}
        self.recipecache.deps[fn] = []
        self.recipecache.rundeps[fn] = []
        self.recipecache.runrecs[fn] = []

        # Invalidate task for target if force mode active
        if self.configuration.force:
            logger.verbose("Invalidate task %s, %s", task, fn)
            if not task.startswith("do_"):
                task = "do_%s" % task
            bb.parse.siggen.invalidate_task(task, self.recipecache, fn)

        # Setup taskdata structure
        taskdata = bb.taskdata.TaskData(self.configuration.abort)
        taskdata.add_provider(self.data, self.recipecache, item)

        buildname = self.data.getVar("BUILDNAME", True)
        bb.event.fire(bb.event.BuildStarted(buildname, [item]), self.expanded_data)

        # Execute the runqueue
        if not task.startswith("do_"):
            task = "do_%s" % task
        runlist = [[item, task]]

        rq = bb.runqueue.RunQueue(self, self.data, self.recipecache, taskdata, runlist)

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
                return False

            if not retval:
                bb.event.fire(bb.event.BuildCompleted(len(rq.rqdata.runq_fnid), buildname, item, failures, interrupted), self.expanded_data)
                self.command.finishAsyncCommand(msg)
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
                bb.event.fire(bb.event.BuildCompleted(len(rq.rqdata.runq_fnid), buildname, targets, failures, interrupted), self.data)
                self.command.finishAsyncCommand(msg)
                return False
            if retval is True:
                return True
            return retval

        build.reset_cache()
        self.buildSetVars()

        # If we are told to do the None task then query the default task
        if (task == None):
            task = self.configuration.cmd

        if not task.startswith("do_"):
            task = "do_%s" % task

        taskdata, runlist, fulltargetlist = self.buildTaskData(targets, task, self.configuration.abort)

        buildname = self.data.getVar("BUILDNAME", False)

        # make targets to always look as <target>:do_<task>
        ntargets = []
        for target in fulltargetlist:
            if ":" in target:
                if ":do_" not in target:
                    target = "%s:do_%s" % tuple(target.split(":", 1))
            else:
                target = "%s:%s" % (target, task)
            ntargets.append(target)

        bb.event.fire(bb.event.BuildStarted(buildname, ntargets), self.data)

        rq = bb.runqueue.RunQueue(self, self.data, self.recipecache, taskdata, runlist)
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
    'v' : v ,
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


    def generateNewImage(self, image, base_image, package_queue, timestamp, description):
        '''
        Create a new image with a "require"/"inherit" base_image statement
        '''
        if timestamp:
            image_name = os.path.splitext(image)[0]
            timestr = time.strftime("-%Y%m%d-%H%M%S")
            dest = image_name + str(timestr) + ".bb"
        else:
            if not image.endswith(".bb"):
                dest = image + ".bb"
            else:
                dest = image

        basename = False
        if base_image:
            with open(base_image, 'r') as f:
                require_line = f.readline()
                p = re.compile("IMAGE_BASENAME *=")
                for line in f:
                    if p.search(line):
                        basename = True

        with open(dest, "w") as imagefile:
            if base_image is None:
                imagefile.write("inherit core-image\n")
            else:
                topdir = self.data.getVar("TOPDIR", False)
                if topdir in base_image:
                    base_image = require_line.split()[1]
                imagefile.write("require " + base_image + "\n")
            image_install = "IMAGE_INSTALL = \""
            for package in package_queue:
                image_install += str(package) + " "
            image_install += "\"\n"
            imagefile.write(image_install)

            description_var = "DESCRIPTION = \"" + description + "\"\n"
            imagefile.write(description_var)

            if basename:
                # If this is overwritten in a inherited image, reset it to default
                image_basename = "IMAGE_BASENAME = \"${PN}\"\n"
                imagefile.write(image_basename)

        self.state = state.initial
        if timestamp:
            return timestr

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
            self.baseconfig_valid = True
            self.parsecache_valid = False

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
            self.parseConfiguration ()
            if CookerFeatures.SEND_SANITYEVENTS in self.featureset:
                bb.event.fire(bb.event.SanityCheck(False), self.data)

            ignore = self.expanded_data.getVar("ASSUME_PROVIDED", True) or ""
            self.recipecache.ignored_dependencies = set(ignore.split())

            for dep in self.configuration.extra_assume_provided:
                self.recipecache.ignored_dependencies.add(dep)

            self.collection = CookerCollectFiles(self.recipecache.bbfile_config_priorities)
            (filelist, masked) = self.collection.collect_bbfiles(self.data, self.expanded_data)

            self.parser = CookerParser(self, filelist, masked)
            self.parsecache_valid = True

        self.state = state.parsing

        if not self.parser.parse_next():
            collectlog.debug(1, "parsing complete")
            if self.parser.error:
                raise bb.BBHandledException()
            self.show_appends_with_no_recipes()
            self.handlePrefProviders()
            self.recipecache.bbfile_priority = self.collection.collection_priorities(self.recipecache.pkg_fn, self.data)
            self.state = state.running

            # Send an event listing all stamps reachable after parsing
            # which the metadata may use to clean up stale data
            event = bb.event.ReachableStamps(self.recipecache.stamp)
            bb.event.fire(event, self.expanded_data)
            return None

        return True

    def checkPackages(self, pkgs_to_build):

        # Return a copy, don't modify the original
        pkgs_to_build = pkgs_to_build[:]

        if len(pkgs_to_build) == 0:
            raise NothingToBuild

        ignore = (self.expanded_data.getVar("ASSUME_PROVIDED", True) or "").split()
        for pkg in pkgs_to_build:
            if pkg in ignore:
                parselog.warn("Explicit target \"%s\" is in ASSUME_PROVIDED, ignoring" % pkg)

        if 'world' in pkgs_to_build:
            bb.providers.buildWorldTargetList(self.recipecache)
            pkgs_to_build.remove('world')
            for t in self.recipecache.world_target:
                pkgs_to_build.append(t)

        if 'universe' in pkgs_to_build:
            parselog.warn("The \"universe\" target is only intended for testing and may produce errors.")
            parselog.debug(1, "collating packages for \"universe\"")
            pkgs_to_build.remove('universe')
            for t in self.recipecache.universe_target:
                pkgs_to_build.append(t)

        return pkgs_to_build




    def pre_serve(self):
        # Empty the environment. The environment will be populated as
        # necessary from the data store.
        #bb.utils.empty_environment()
        try:
            self.prhost = prserv.serv.auto_start(self.data)
        except prserv.serv.PRServiceConfigError:
            bb.event.fire(CookerExit(), self.expanded_data)
            self.state = state.error
        return

    def post_serve(self):
        prserv.serv.auto_shutdown(self.data)
        bb.event.fire(CookerExit(), self.expanded_data)
        lockfile = self.lock.name
        self.lock.close()
        self.lock = None

        while not self.lock:
            with bb.utils.timeout(3):
                self.lock = bb.utils.lockfile(lockfile, shared=False, retry=False, block=True)
                if not self.lock:
                    # Some systems may not have lsof available
                    procs = None
                    try:
                        procs = subprocess.check_output(["lsof", '-w', lockfile], stderr=subprocess.STDOUT)
                    except OSError as e:
                        if e.errno != errno.ENOENT:
                            raise
                    if procs is None:
                        # Fall back to fuser if lsof is unavailable
                        try:
                            procs = subprocess.check_output(["fuser", '-v', lockfile], stderr=subprocess.STDOUT)
                        except OSError as e:
                            if e.errno != errno.ENOENT:
                                raise

                    msg = "Delaying shutdown due to active processes which appear to be holding bitbake.lock"
                    if procs:
                        msg += ":\n%s" % str(procs)
                    print(msg)


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

    def lockBitbake(self):
        if not hasattr(self, 'lock'):
            self.lock = None
            if self.data:
                lockfile = self.data.expand("${TOPDIR}/bitbake.lock")
                if lockfile:
                    self.lock = bb.utils.lockfile(lockfile, False, False)
        return self.lock

    def unlockBitbake(self):
        if hasattr(self, 'lock') and self.lock:
            bb.utils.unlockfile(self.lock)

def server_main(cooker, func, *args):
    cooker.pre_serve()

    if cooker.configuration.profile:
        try:
            import cProfile as profile
        except:
            import profile
        prof = profile.Profile()

        ret = profile.Profile.runcall(prof, func, *args)

        prof.dump_stats("profile.log")
        bb.utils.process_profilelog("profile.log")
        print("Raw profiling information saved to profile.log and processed statistics to profile.log.processed")

    else:
        ret = func(*args)

    cooker.post_serve()

    return ret

class CookerExit(bb.event.Event):
    """
    Notify clients of the Cooker shutdown
    """

    def __init__(self):
        bb.event.Event.__init__(self)


class CookerCollectFiles(object):
    def __init__(self, priorities):
        self.bbappends = []
        self.bbfile_config_priorities = priorities

    def calc_bbfile_priority( self, filename, matched = None ):
        for _, _, regex, pri in self.bbfile_config_priorities:
            if regex.match(filename):
                if matched != None:
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

        files = (config.getVar( "BBFILES", True) or "").split()
        config.setVar("BBFILES", " ".join(files))

        # Sort files by priority
        files.sort( key=lambda fileitem: self.calc_bbfile_priority(fileitem) )

        if not len(files):
            files = self.get_bbfiles()

        if not len(files):
            collectlog.error("no recipe files to build, check your BBPATH and BBFILES?")
            bb.event.fire(CookerExit(), eventdata)

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

        bbmask = config.getVar('BBMASK', True)

        if bbmask:
            # First validate the individual regular expressions and ignore any
            # that do not compile
            bbmasks = []
            for mask in bbmask.split():
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

        return (bbfiles, masked)

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
            realfn, cls = bb.cache.Cache.virtualfn2realfn(p)
            priorities[p] = self.calc_bbfile_priority(realfn, matched)

        # Don't show the warning if the BBFILE_PATTERN did match .bbappend files
        unmatched = set()
        for _, _, regex, pri in self.bbfile_config_priorities:
            if not regex in matched:
                unmatched.add(regex)

        def findmatch(regex):
            for b in self.bbappends:
                (bbfile, append) = b
                if regex.match(append):
                    return True
            return False

        for unmatch in unmatched.copy():
            if findmatch(unmatch):
                unmatched.remove(unmatch)

        for collection, pattern, regex, _ in self.bbfile_config_priorities:
            if regex in unmatched:
                if d.getVar('BBFILE_PATTERN_IGNORE_EMPTY_%s' % collection, True) != '1':
                    collectlog.warn("No bb files matched BBFILE_PATTERN_%s '%s'" % (collection, pattern))

        return priorities

class ParsingFailure(Exception):
    def __init__(self, realexception, recipe):
        self.realexception = realexception
        self.recipe = recipe
        Exception.__init__(self, realexception, recipe)

class Feeder(multiprocessing.Process):
    def __init__(self, jobs, to_parsers, quit):
        self.quit = quit
        self.jobs = jobs
        self.to_parsers = to_parsers
        multiprocessing.Process.__init__(self)

    def run(self):
        while True:
            try:
                quit = self.quit.get_nowait()
            except Queue.Empty:
                pass
            else:
                if quit == 'cancel':
                    self.to_parsers.cancel_join_thread()
                break

            try:
                job = self.jobs.pop()
            except IndexError:
                break

            try:
                self.to_parsers.put(job, timeout=0.5)
            except Queue.Full:
                self.jobs.insert(0, job)
                continue

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
            except Queue.Empty:
                pass
            else:
                self.results.cancel_join_thread()
                break

            if pending:
                result = pending.pop()
            else:
                try:
                    job = self.jobs.get(timeout=0.25)
                except Queue.Empty:
                    continue

                if job is None:
                    break
                result = self.parse(*job)

            try:
                self.results.put(result, timeout=0.25)
            except Queue.Full:
                pending.append(result)

    def parse(self, filename, appends, caches_array):
        try:
            # Record the filename we're parsing into any events generated
            def parse_filter(self, record):
                record.taskpid = bb.event.worker_pid
                record.fn = filename
                return True

            # Reset our environment and handlers to the original settings
            bb.utils.set_context(self.context.copy())
            bb.event.set_class_handlers(self.handlers.copy())
            bb.event.LogHandler.filter = parse_filter

            return True, bb.cache.Cache.parse(filename, appends, self.cfg, caches_array)
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

class CookerParser(object):
    def __init__(self, cooker, filelist, masked):
        self.filelist = filelist
        self.cooker = cooker
        self.cfgdata = cooker.data
        self.cfghash = cooker.data_hash

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

        self.bb_cache = bb.cache.Cache(self.cfgdata, self.cfghash, cooker.caches_array)
        self.fromcache = []
        self.willparse = []
        for filename in self.filelist:
            appends = self.cooker.collection.get_file_appends(filename)
            if not self.bb_cache.cacheValid(filename, appends):
                self.willparse.append((filename, appends, cooker.caches_array))
            else:
                self.fromcache.append((filename, appends))
        self.toparse = self.total - len(self.fromcache)
        self.progress_chunk = max(self.toparse / 100, 1)

        self.num_processes = min(int(self.cfgdata.getVar("BB_NUMBER_PARSE_THREADS", True) or
                                 multiprocessing.cpu_count()), len(self.willparse))

        self.start()
        self.haveshutdown = False

    def start(self):
        self.results = self.load_cached()
        self.processes = []
        if self.toparse:
            bb.event.fire(bb.event.ParseStarted(self.toparse), self.cfgdata)
            def init():
                Parser.cfg = self.cfgdata
                bb.utils.set_process_name(multiprocessing.current_process().name)
                multiprocessing.util.Finalize(None, bb.codeparser.parser_cache_save, exitpriority=1)
                multiprocessing.util.Finalize(None, bb.fetch.fetcher_parse_save, exitpriority=1)

            self.feeder_quit = multiprocessing.Queue(maxsize=1)
            self.parser_quit = multiprocessing.Queue(maxsize=self.num_processes)
            self.jobs = multiprocessing.Queue(maxsize=self.num_processes)
            self.result_queue = multiprocessing.Queue()
            self.feeder = Feeder(self.willparse, self.jobs, self.feeder_quit)
            self.feeder.start()
            for i in range(0, self.num_processes):
                parser = Parser(self.jobs, self.result_queue, self.parser_quit, init, self.cooker.configuration.profile)
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
            self.feeder_quit.put(None)
            for process in self.processes:
                self.parser_quit.put(None)
        else:
            self.feeder_quit.put('cancel')

            self.parser_quit.cancel_join_thread()
            for process in self.processes:
                self.parser_quit.put(None)

            self.jobs.cancel_join_thread()

        for process in self.processes:
            if force:
                process.join(.1)
                process.terminate()
            else:
                process.join()
        self.feeder.join()

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
            cached, infos = self.bb_cache.load(filename, appends, self.cfgdata)
            yield not cached, infos

    def parse_generator(self):
        while True:
            if self.parsed >= self.toparse:
                break

            try:
                result = self.result_queue.get(timeout=0.25)
            except Queue.Empty:
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
            parsed, result = self.results.next()
        except StopIteration:
            self.shutdown()
            return False
        except bb.BBHandledException as exc:
            self.error += 1
            logger.error('Failed to parse recipe: %s' % exc.recipe)
            self.shutdown(clean=False)
            return False
        except ParsingFailure as exc:
            self.error += 1
            logger.error('Unable to parse %s: %s' %
                     (exc.recipe, bb.exceptions.to_string(exc.realexception)))
            self.shutdown(clean=False)
            return False
        except bb.parse.ParseError as exc:
            self.error += 1
            logger.error(str(exc))
            self.shutdown(clean=False)
            return False
        except bb.data_smart.ExpansionError as exc:
            self.error += 1
            _, value, _ = sys.exc_info()
            logger.error('ExpansionError during parsing %s: %s', value.recipe, str(exc))
            self.shutdown(clean=False)
            return False
        except Exception as exc:
            self.error += 1
            etype, value, tb = sys.exc_info()
            if hasattr(value, "recipe"):
                logger.error('Unable to parse %s', value.recipe,
                            exc_info=(etype, value, exc.traceback))
            else:
                # Most likely, an exception occurred during raising an exception
                import traceback
                logger.error('Exception during parse: %s' % traceback.format_exc())
            self.shutdown(clean=False)
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
            self.bb_cache.add_info(virtualfn, info_array, self.cooker.recipecache,
                                        parsed=parsed, watcher = self.cooker.add_filewatch)
        return True

    def reparse(self, filename):
        infos = self.bb_cache.parse(filename,
                                    self.cooker.collection.get_file_appends(filename),
                                    self.cfgdata, self.cooker.caches_array)
        for vfn, info_array in infos:
            self.cooker.recipecache.add_from_recipeinfo(vfn, info_array)
