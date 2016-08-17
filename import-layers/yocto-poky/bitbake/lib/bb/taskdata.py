#!/usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'TaskData' implementation

Task data collection and handling

"""

# Copyright (C) 2006  Richard Purdie
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
import re
import bb

logger = logging.getLogger("BitBake.TaskData")

def re_match_strings(target, strings):
    """
    Whether or not the string 'target' matches
    any one string of the strings which can be regular expression string
    """
    return any(name == target or re.match(name, target)
               for name in strings)

class TaskData:
    """
    BitBake Task Data implementation
    """
    def __init__(self, abort = True, tryaltconfigs = False, skiplist = None, allowincomplete = False):
        self.build_names_index = []
        self.run_names_index = []
        self.fn_index = []

        self.build_targets = {}
        self.run_targets = {}

        self.external_targets = []

        self.tasks_fnid = []
        self.tasks_name = []
        self.tasks_tdepends = []
        self.tasks_idepends = []
        self.tasks_irdepends = []
        # Cache to speed up task ID lookups
        self.tasks_lookup = {}

        self.depids = {}
        self.rdepids = {}

        self.consider_msgs_cache = []

        self.failed_deps = []
        self.failed_rdeps = []
        self.failed_fnids = []

        self.abort = abort
        self.tryaltconfigs = tryaltconfigs
        self.allowincomplete = allowincomplete

        self.skiplist = skiplist

    def getbuild_id(self, name):
        """
        Return an ID number for the build target name.
        If it doesn't exist, create one.
        """
        if not name in self.build_names_index:
            self.build_names_index.append(name)
            return len(self.build_names_index) - 1

        return self.build_names_index.index(name)

    def getrun_id(self, name):
        """
        Return an ID number for the run target name.
        If it doesn't exist, create one.
        """
        if not name in self.run_names_index:
            self.run_names_index.append(name)
            return len(self.run_names_index) - 1

        return self.run_names_index.index(name)

    def getfn_id(self, name):
        """
        Return an ID number for the filename.
        If it doesn't exist, create one.
        """
        if not name in self.fn_index:
            self.fn_index.append(name)
            return len(self.fn_index) - 1

        return self.fn_index.index(name)

    def gettask_ids(self, fnid):
        """
        Return an array of the ID numbers matching a given fnid.
        """
        ids = []
        if fnid in self.tasks_lookup:
            for task in self.tasks_lookup[fnid]:
                ids.append(self.tasks_lookup[fnid][task])
        return ids

    def gettask_id_fromfnid(self, fnid, task):
        """
        Return an ID number for the task matching fnid and task.
        """
        if fnid in self.tasks_lookup:
            if task in self.tasks_lookup[fnid]:
                return self.tasks_lookup[fnid][task]

        return None

    def gettask_id(self, fn, task, create = True):
        """
        Return an ID number for the task matching fn and task.
        If it doesn't exist, create one by default.
        Optionally return None instead.
        """
        fnid = self.getfn_id(fn)

        if fnid in self.tasks_lookup:
            if task in self.tasks_lookup[fnid]:
                return self.tasks_lookup[fnid][task]

        if not create:
            return None

        self.tasks_name.append(task)
        self.tasks_fnid.append(fnid)
        self.tasks_tdepends.append([])
        self.tasks_idepends.append([])
        self.tasks_irdepends.append([])

        listid = len(self.tasks_name) - 1

        if fnid not in self.tasks_lookup:
            self.tasks_lookup[fnid] = {}
        self.tasks_lookup[fnid][task] = listid

        return listid

    def add_tasks(self, fn, dataCache):
        """
        Add tasks for a given fn to the database
        """

        task_deps = dataCache.task_deps[fn]

        fnid = self.getfn_id(fn)

        if fnid in self.failed_fnids:
            bb.msg.fatal("TaskData", "Trying to re-add a failed file? Something is broken...")

        # Check if we've already seen this fn
        if fnid in self.tasks_fnid:
            return

        self.add_extra_deps(fn, dataCache)

        for task in task_deps['tasks']:

            # Work out task dependencies
            parentids = []
            for dep in task_deps['parents'][task]:
                if dep not in task_deps['tasks']:
                    bb.debug(2, "Not adding dependeny of %s on %s since %s does not exist" % (task, dep, dep))
                    continue
                parentid = self.gettask_id(fn, dep)
                parentids.append(parentid)
            taskid = self.gettask_id(fn, task)
            self.tasks_tdepends[taskid].extend(parentids)

            # Touch all intertask dependencies
            if 'depends' in task_deps and task in task_deps['depends']:
                ids = []
                for dep in task_deps['depends'][task].split():
                    if dep:
                        if ":" not in dep:
                            bb.msg.fatal("TaskData", "Error for %s, dependency %s does not contain ':' character\n. Task 'depends' should be specified in the form 'packagename:task'" % (fn, dep))
                        ids.append(((self.getbuild_id(dep.split(":")[0])), dep.split(":")[1]))
                self.tasks_idepends[taskid].extend(ids)
            if 'rdepends' in task_deps and task in task_deps['rdepends']:
                ids = []
                for dep in task_deps['rdepends'][task].split():
                    if dep:
                        if ":" not in dep:
                            bb.msg.fatal("TaskData", "Error for %s, dependency %s does not contain ':' character\n. Task 'rdepends' should be specified in the form 'packagename:task'" % (fn, dep))
                        ids.append(((self.getrun_id(dep.split(":")[0])), dep.split(":")[1]))
                self.tasks_irdepends[taskid].extend(ids)


        # Work out build dependencies
        if not fnid in self.depids:
            dependids = {}
            for depend in dataCache.deps[fn]:
                dependids[self.getbuild_id(depend)] = None
            self.depids[fnid] = dependids.keys()
            logger.debug(2, "Added dependencies %s for %s", str(dataCache.deps[fn]), fn)

        # Work out runtime dependencies
        if not fnid in self.rdepids:
            rdependids = {}
            rdepends = dataCache.rundeps[fn]
            rrecs = dataCache.runrecs[fn]
            rdependlist = []
            rreclist = []
            for package in rdepends:
                for rdepend in rdepends[package]:
                    rdependlist.append(rdepend)
                    rdependids[self.getrun_id(rdepend)] = None
            for package in rrecs:
                for rdepend in rrecs[package]:
                    rreclist.append(rdepend)
                    rdependids[self.getrun_id(rdepend)] = None
            if rdependlist:
                logger.debug(2, "Added runtime dependencies %s for %s", str(rdependlist), fn)
            if rreclist:
                logger.debug(2, "Added runtime recommendations %s for %s", str(rreclist), fn)
            self.rdepids[fnid] = rdependids.keys()

        for dep in self.depids[fnid]:
            if dep in self.failed_deps:
                self.fail_fnid(fnid)
                return
        for dep in self.rdepids[fnid]:
            if dep in self.failed_rdeps:
                self.fail_fnid(fnid)
                return

    def add_extra_deps(self, fn, dataCache):
        func = dataCache.extradepsfunc.get(fn, None)
        if func:
            bb.providers.buildWorldTargetList(dataCache)
            pn = dataCache.pkg_fn[fn]
            params = {'deps': dataCache.deps[fn],
                      'world_target': dataCache.world_target,
                      'pkg_pn': dataCache.pkg_pn,
                      'self_pn': pn}
            funcname = '_%s_calculate_extra_depends' % pn.replace('-', '_')
            paramlist = ','.join(params.keys())
            func = 'def %s(%s):\n%s\n\n%s(%s)' % (funcname, paramlist, func, funcname, paramlist)
            bb.utils.better_exec(func, params)


    def have_build_target(self, target):
        """
        Have we a build target matching this name?
        """
        targetid = self.getbuild_id(target)

        if targetid in self.build_targets:
            return True
        return False

    def have_runtime_target(self, target):
        """
        Have we a runtime target matching this name?
        """
        targetid = self.getrun_id(target)

        if targetid in self.run_targets:
            return True
        return False

    def add_build_target(self, fn, item):
        """
        Add a build target.
        If already present, append the provider fn to the list
        """
        targetid = self.getbuild_id(item)
        fnid = self.getfn_id(fn)

        if targetid in self.build_targets:
            if fnid in self.build_targets[targetid]:
                return
            self.build_targets[targetid].append(fnid)
            return
        self.build_targets[targetid] = [fnid]

    def add_runtime_target(self, fn, item):
        """
        Add a runtime target.
        If already present, append the provider fn to the list
        """
        targetid = self.getrun_id(item)
        fnid = self.getfn_id(fn)

        if targetid in self.run_targets:
            if fnid in self.run_targets[targetid]:
                return
            self.run_targets[targetid].append(fnid)
            return
        self.run_targets[targetid] = [fnid]

    def mark_external_target(self, item):
        """
        Mark a build target as being externally requested
        """
        targetid = self.getbuild_id(item)

        if targetid not in self.external_targets:
            self.external_targets.append(targetid)

    def get_unresolved_build_targets(self, dataCache):
        """
        Return a list of build targets who's providers
        are unknown.
        """
        unresolved = []
        for target in self.build_names_index:
            if re_match_strings(target, dataCache.ignored_dependencies):
                continue
            if self.build_names_index.index(target) in self.failed_deps:
                continue
            if not self.have_build_target(target):
                unresolved.append(target)
        return unresolved

    def get_unresolved_run_targets(self, dataCache):
        """
        Return a list of runtime targets who's providers
        are unknown.
        """
        unresolved = []
        for target in self.run_names_index:
            if re_match_strings(target, dataCache.ignored_dependencies):
                continue
            if self.run_names_index.index(target) in self.failed_rdeps:
                continue
            if not self.have_runtime_target(target):
                unresolved.append(target)
        return unresolved

    def get_provider(self, item):
        """
        Return a list of providers of item
        """
        targetid = self.getbuild_id(item)

        return self.build_targets[targetid]

    def get_dependees(self, itemid):
        """
        Return a list of targets which depend on item
        """
        dependees = []
        for fnid in self.depids:
            if itemid in self.depids[fnid]:
                dependees.append(fnid)
        return dependees

    def get_dependees_str(self, item):
        """
        Return a list of targets which depend on item as a user readable string
        """
        itemid = self.getbuild_id(item)
        dependees = []
        for fnid in self.depids:
            if itemid in self.depids[fnid]:
                dependees.append(self.fn_index[fnid])
        return dependees

    def get_rdependees(self, itemid):
        """
        Return a list of targets which depend on runtime item
        """
        dependees = []
        for fnid in self.rdepids:
            if itemid in self.rdepids[fnid]:
                dependees.append(fnid)
        return dependees

    def get_rdependees_str(self, item):
        """
        Return a list of targets which depend on runtime item as a user readable string
        """
        itemid = self.getrun_id(item)
        dependees = []
        for fnid in self.rdepids:
            if itemid in self.rdepids[fnid]:
                dependees.append(self.fn_index[fnid])
        return dependees

    def get_reasons(self, item, runtime=False):
        """
        Get the reason(s) for an item not being provided, if any
        """
        reasons = []
        if self.skiplist:
            for fn in self.skiplist:
                skipitem = self.skiplist[fn]
                if skipitem.pn == item:
                    reasons.append("%s was skipped: %s" % (skipitem.pn, skipitem.skipreason))
                elif runtime and item in skipitem.rprovides:
                    reasons.append("%s RPROVIDES %s but was skipped: %s" % (skipitem.pn, item, skipitem.skipreason))
                elif not runtime and item in skipitem.provides:
                    reasons.append("%s PROVIDES %s but was skipped: %s" % (skipitem.pn, item, skipitem.skipreason))
        return reasons

    def get_close_matches(self, item, provider_list):
        import difflib
        if self.skiplist:
            skipped = []
            for fn in self.skiplist:
                skipped.append(self.skiplist[fn].pn)
            full_list = provider_list + skipped
        else:
            full_list = provider_list
        return difflib.get_close_matches(item, full_list, cutoff=0.7)

    def add_provider(self, cfgData, dataCache, item):
        try:
            self.add_provider_internal(cfgData, dataCache, item)
        except bb.providers.NoProvider:
            if self.abort:
                raise
            self.remove_buildtarget(self.getbuild_id(item))

        self.mark_external_target(item)

    def add_provider_internal(self, cfgData, dataCache, item):
        """
        Add the providers of item to the task data
        Mark entries were specifically added externally as against dependencies
        added internally during dependency resolution
        """

        if re_match_strings(item, dataCache.ignored_dependencies):
            return

        if not item in dataCache.providers:
            close_matches = self.get_close_matches(item, dataCache.providers.keys())
            # Is it in RuntimeProviders ?
            all_p = bb.providers.getRuntimeProviders(dataCache, item)
            for fn in all_p:
                new = dataCache.pkg_fn[fn] + " RPROVIDES " + item
                if new not in close_matches:
                    close_matches.append(new)
            bb.event.fire(bb.event.NoProvider(item, dependees=self.get_dependees_str(item), reasons=self.get_reasons(item), close_matches=close_matches), cfgData)
            raise bb.providers.NoProvider(item)

        if self.have_build_target(item):
            return

        all_p = dataCache.providers[item]

        eligible, foundUnique = bb.providers.filterProviders(all_p, item, cfgData, dataCache)
        eligible = [p for p in eligible if not self.getfn_id(p) in self.failed_fnids]

        if not eligible:
            bb.event.fire(bb.event.NoProvider(item, dependees=self.get_dependees_str(item), reasons=["No eligible PROVIDERs exist for '%s'" % item]), cfgData)
            raise bb.providers.NoProvider(item)

        if len(eligible) > 1 and foundUnique == False:
            if item not in self.consider_msgs_cache:
                providers_list = []
                for fn in eligible:
                    providers_list.append(dataCache.pkg_fn[fn])
                bb.event.fire(bb.event.MultipleProviders(item, providers_list), cfgData)
            self.consider_msgs_cache.append(item)

        for fn in eligible:
            fnid = self.getfn_id(fn)
            if fnid in self.failed_fnids:
                continue
            logger.debug(2, "adding %s to satisfy %s", fn, item)
            self.add_build_target(fn, item)
            self.add_tasks(fn, dataCache)


            #item = dataCache.pkg_fn[fn]

    def add_rprovider(self, cfgData, dataCache, item):
        """
        Add the runtime providers of item to the task data
        (takes item names from RDEPENDS/PACKAGES namespace)
        """

        if re_match_strings(item, dataCache.ignored_dependencies):
            return

        if self.have_runtime_target(item):
            return

        all_p = bb.providers.getRuntimeProviders(dataCache, item)

        if not all_p:
            bb.event.fire(bb.event.NoProvider(item, runtime=True, dependees=self.get_rdependees_str(item), reasons=self.get_reasons(item, True)), cfgData)
            raise bb.providers.NoRProvider(item)

        eligible, numberPreferred = bb.providers.filterProvidersRunTime(all_p, item, cfgData, dataCache)
        eligible = [p for p in eligible if not self.getfn_id(p) in self.failed_fnids]

        if not eligible:
            bb.event.fire(bb.event.NoProvider(item, runtime=True, dependees=self.get_rdependees_str(item), reasons=["No eligible RPROVIDERs exist for '%s'" % item]), cfgData)
            raise bb.providers.NoRProvider(item)

        if len(eligible) > 1 and numberPreferred == 0:
            if item not in self.consider_msgs_cache:
                providers_list = []
                for fn in eligible:
                    providers_list.append(dataCache.pkg_fn[fn])
                bb.event.fire(bb.event.MultipleProviders(item, providers_list, runtime=True), cfgData)
            self.consider_msgs_cache.append(item)

        if numberPreferred > 1:
            if item not in self.consider_msgs_cache:
                providers_list = []
                for fn in eligible:
                    providers_list.append(dataCache.pkg_fn[fn])
                bb.event.fire(bb.event.MultipleProviders(item, providers_list, runtime=True), cfgData)
            self.consider_msgs_cache.append(item)
            raise bb.providers.MultipleRProvider(item)

        # run through the list until we find one that we can build
        for fn in eligible:
            fnid = self.getfn_id(fn)
            if fnid in self.failed_fnids:
                continue
            logger.debug(2, "adding '%s' to satisfy runtime '%s'", fn, item)
            self.add_runtime_target(fn, item)
            self.add_tasks(fn, dataCache)

    def fail_fnid(self, fnid, missing_list=None):
        """
        Mark a file as failed (unbuildable)
        Remove any references from build and runtime provider lists

        missing_list, A list of missing requirements for this target
        """
        if fnid in self.failed_fnids:
            return
        if not missing_list:
            missing_list = []
        logger.debug(1, "File '%s' is unbuildable, removing...", self.fn_index[fnid])
        self.failed_fnids.append(fnid)
        for target in self.build_targets:
            if fnid in self.build_targets[target]:
                self.build_targets[target].remove(fnid)
                if len(self.build_targets[target]) == 0:
                    self.remove_buildtarget(target, missing_list)
        for target in self.run_targets:
            if fnid in self.run_targets[target]:
                self.run_targets[target].remove(fnid)
                if len(self.run_targets[target]) == 0:
                    self.remove_runtarget(target, missing_list)

    def remove_buildtarget(self, targetid, missing_list=None):
        """
        Mark a build target as failed (unbuildable)
        Trigger removal of any files that have this as a dependency
        """
        if not missing_list:
            missing_list = [self.build_names_index[targetid]]
        else:
            missing_list = [self.build_names_index[targetid]] + missing_list
        logger.verbose("Target '%s' is unbuildable, removing...\nMissing or unbuildable dependency chain was: %s", self.build_names_index[targetid], missing_list)
        self.failed_deps.append(targetid)
        dependees = self.get_dependees(targetid)
        for fnid in dependees:
            self.fail_fnid(fnid, missing_list)
        for taskid in xrange(len(self.tasks_idepends)):
            idepends = self.tasks_idepends[taskid]
            for (idependid, idependtask) in idepends:
                if idependid == targetid:
                    self.fail_fnid(self.tasks_fnid[taskid], missing_list)

        if self.abort and targetid in self.external_targets:
            target = self.build_names_index[targetid]
            logger.error("Required build target '%s' has no buildable providers.\nMissing or unbuildable dependency chain was: %s", target, missing_list)
            raise bb.providers.NoProvider(target)

    def remove_runtarget(self, targetid, missing_list=None):
        """
        Mark a run target as failed (unbuildable)
        Trigger removal of any files that have this as a dependency
        """
        if not missing_list:
            missing_list = [self.run_names_index[targetid]]
        else:
            missing_list = [self.run_names_index[targetid]] + missing_list

        logger.info("Runtime target '%s' is unbuildable, removing...\nMissing or unbuildable dependency chain was: %s", self.run_names_index[targetid], missing_list)
        self.failed_rdeps.append(targetid)
        dependees = self.get_rdependees(targetid)
        for fnid in dependees:
            self.fail_fnid(fnid, missing_list)
        for taskid in xrange(len(self.tasks_irdepends)):
            irdepends = self.tasks_irdepends[taskid]
            for (idependid, idependtask) in irdepends:
                if idependid == targetid:
                    self.fail_fnid(self.tasks_fnid[taskid], missing_list)

    def add_unresolved(self, cfgData, dataCache):
        """
        Resolve all unresolved build and runtime targets
        """
        logger.info("Resolving any missing task queue dependencies")
        while True:
            added = 0
            for target in self.get_unresolved_build_targets(dataCache):
                try:
                    self.add_provider_internal(cfgData, dataCache, target)
                    added = added + 1
                except bb.providers.NoProvider:
                    targetid = self.getbuild_id(target)
                    if self.abort and targetid in self.external_targets and not self.allowincomplete:
                        raise
                    if not self.allowincomplete:
                        self.remove_buildtarget(targetid)
            for target in self.get_unresolved_run_targets(dataCache):
                try:
                    self.add_rprovider(cfgData, dataCache, target)
                    added = added + 1
                except (bb.providers.NoRProvider, bb.providers.MultipleRProvider):
                    self.remove_runtarget(self.getrun_id(target))
            logger.debug(1, "Resolved " + str(added) + " extra dependencies")
            if added == 0:
                break
        # self.dump_data()

    def get_providermap(self, prefix=None):
        provmap = {}
        for name in self.build_names_index:
            if prefix and not name.startswith(prefix):
                continue
            if self.have_build_target(name):
                provider = self.get_provider(name)
                if provider:
                    provmap[name] = self.fn_index[provider[0]]
        return provmap

    def dump_data(self):
        """
        Dump some debug information on the internal data structures
        """
        logger.debug(3, "build_names:")
        logger.debug(3, ", ".join(self.build_names_index))

        logger.debug(3, "run_names:")
        logger.debug(3, ", ".join(self.run_names_index))

        logger.debug(3, "build_targets:")
        for buildid in xrange(len(self.build_names_index)):
            target = self.build_names_index[buildid]
            targets = "None"
            if buildid in self.build_targets:
                targets = self.build_targets[buildid]
            logger.debug(3, " (%s)%s: %s", buildid, target, targets)

        logger.debug(3, "run_targets:")
        for runid in xrange(len(self.run_names_index)):
            target = self.run_names_index[runid]
            targets = "None"
            if runid in self.run_targets:
                targets = self.run_targets[runid]
            logger.debug(3, " (%s)%s: %s", runid, target, targets)

        logger.debug(3, "tasks:")
        for task in xrange(len(self.tasks_name)):
            logger.debug(3, " (%s)%s - %s: %s",
                       task,
                       self.fn_index[self.tasks_fnid[task]],
                       self.tasks_name[task],
                       self.tasks_tdepends[task])

        logger.debug(3, "dependency ids (per fn):")
        for fnid in self.depids:
            logger.debug(3, " %s %s: %s", fnid, self.fn_index[fnid], self.depids[fnid])

        logger.debug(3, "runtime dependency ids (per fn):")
        for fnid in self.rdepids:
            logger.debug(3, " %s %s: %s", fnid, self.fn_index[fnid], self.rdepids[fnid])
