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

class TaskEntry:
    def __init__(self):
        self.tdepends = []
        self.idepends = []
        self.irdepends = []

class TaskData:
    """
    BitBake Task Data implementation
    """
    def __init__(self, abort = True, skiplist = None, allowincomplete = False):
        self.build_targets = {}
        self.run_targets = {}

        self.external_targets = []

        self.seenfns = []
        self.taskentries = {}

        self.depids = {}
        self.rdepids = {}

        self.consider_msgs_cache = []

        self.failed_deps = []
        self.failed_rdeps = []
        self.failed_fns = []

        self.abort = abort
        self.allowincomplete = allowincomplete

        self.skiplist = skiplist

        self.mcdepends = []

    def add_tasks(self, fn, dataCache):
        """
        Add tasks for a given fn to the database
        """

        task_deps = dataCache.task_deps[fn]

        if fn in self.failed_fns:
            bb.msg.fatal("TaskData", "Trying to re-add a failed file? Something is broken...")

        # Check if we've already seen this fn
        if fn in self.seenfns:
            return

        self.seenfns.append(fn)

        self.add_extra_deps(fn, dataCache)

        def add_mcdepends(task):
            for dep in task_deps['mcdepends'][task].split():
                if len(dep.split(':')) != 5:
                    bb.msg.fatal("TaskData", "Error for %s:%s[%s], multiconfig dependency %s does not contain exactly four  ':' characters.\n Task '%s' should be specified in the form 'multiconfig:fromMC:toMC:packagename:task'" % (fn, task, 'mcdepends', dep, 'mcdepends'))
                if dep not in self.mcdepends:
                    self.mcdepends.append(dep)

        # Common code for dep_name/depends = 'depends'/idepends and 'rdepends'/irdepends
        def handle_deps(task, dep_name, depends, seen):
            if dep_name in task_deps and task in task_deps[dep_name]:
                ids = []
                for dep in task_deps[dep_name][task].split():
                    if dep:
                        parts = dep.split(":")
                        if len(parts) != 2:
                            bb.msg.fatal("TaskData", "Error for %s:%s[%s], dependency %s in '%s' does not contain exactly one ':' character.\n Task '%s' should be specified in the form 'packagename:task'" % (fn, task, dep_name, dep, task_deps[dep_name][task], dep_name))
                        ids.append((parts[0], parts[1]))
                        seen(parts[0])
                depends.extend(ids)

        for task in task_deps['tasks']:

            tid = "%s:%s" % (fn, task)
            self.taskentries[tid] = TaskEntry()

            # Work out task dependencies
            parentids = []
            for dep in task_deps['parents'][task]:
                if dep not in task_deps['tasks']:
                    bb.debug(2, "Not adding dependency of %s on %s since %s does not exist" % (task, dep, dep))
                    continue
                parentid = "%s:%s" % (fn, dep)
                parentids.append(parentid)
            self.taskentries[tid].tdepends.extend(parentids)


            # Touch all intertask dependencies
            handle_deps(task, 'depends', self.taskentries[tid].idepends, self.seen_build_target)
            handle_deps(task, 'rdepends', self.taskentries[tid].irdepends, self.seen_run_target)

            if 'mcdepends' in task_deps and task in task_deps['mcdepends']:
                add_mcdepends(task)

        # Work out build dependencies
        if not fn in self.depids:
            dependids = set()
            for depend in dataCache.deps[fn]:
                dependids.add(depend)
            self.depids[fn] = list(dependids)
            logger.debug(2, "Added dependencies %s for %s", str(dataCache.deps[fn]), fn)

        # Work out runtime dependencies
        if not fn in self.rdepids:
            rdependids = set()
            rdepends = dataCache.rundeps[fn]
            rrecs = dataCache.runrecs[fn]
            rdependlist = []
            rreclist = []
            for package in rdepends:
                for rdepend in rdepends[package]:
                    rdependlist.append(rdepend)
                    rdependids.add(rdepend)
            for package in rrecs:
                for rdepend in rrecs[package]:
                    rreclist.append(rdepend)
                    rdependids.add(rdepend)
            if rdependlist:
                logger.debug(2, "Added runtime dependencies %s for %s", str(rdependlist), fn)
            if rreclist:
                logger.debug(2, "Added runtime recommendations %s for %s", str(rreclist), fn)
            self.rdepids[fn] = list(rdependids)

        for dep in self.depids[fn]:
            self.seen_build_target(dep)
            if dep in self.failed_deps:
                self.fail_fn(fn)
                return
        for dep in self.rdepids[fn]:
            self.seen_run_target(dep)
            if dep in self.failed_rdeps:
                self.fail_fn(fn)
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
        if target in self.build_targets and self.build_targets[target]:
            return True
        return False

    def have_runtime_target(self, target):
        """
        Have we a runtime target matching this name?
        """
        if target in self.run_targets and self.run_targets[target]:
            return True
        return False

    def seen_build_target(self, name):
        """
        Maintain a list of build targets
        """
        if name not in self.build_targets:
            self.build_targets[name] = []

    def add_build_target(self, fn, item):
        """
        Add a build target.
        If already present, append the provider fn to the list
        """
        if item in self.build_targets:
            if fn in self.build_targets[item]:
                return
            self.build_targets[item].append(fn)
            return
        self.build_targets[item] = [fn]

    def seen_run_target(self, name):
        """
        Maintain a list of runtime build targets
        """
        if name not in self.run_targets:
            self.run_targets[name] = []

    def add_runtime_target(self, fn, item):
        """
        Add a runtime target.
        If already present, append the provider fn to the list
        """
        if item in self.run_targets:
            if fn in self.run_targets[item]:
                return
            self.run_targets[item].append(fn)
            return
        self.run_targets[item] = [fn]

    def mark_external_target(self, target):
        """
        Mark a build target as being externally requested
        """
        if target not in self.external_targets:
            self.external_targets.append(target)

    def get_unresolved_build_targets(self, dataCache):
        """
        Return a list of build targets who's providers
        are unknown.
        """
        unresolved = []
        for target in self.build_targets:
            if re_match_strings(target, dataCache.ignored_dependencies):
                continue
            if target in self.failed_deps:
                continue
            if not self.build_targets[target]:
                unresolved.append(target)
        return unresolved

    def get_unresolved_run_targets(self, dataCache):
        """
        Return a list of runtime targets who's providers
        are unknown.
        """
        unresolved = []
        for target in self.run_targets:
            if re_match_strings(target, dataCache.ignored_dependencies):
                continue
            if target in self.failed_rdeps:
                continue
            if not self.run_targets[target]:
                unresolved.append(target)
        return unresolved

    def get_provider(self, item):
        """
        Return a list of providers of item
        """
        return self.build_targets[item]

    def get_dependees(self, item):
        """
        Return a list of targets which depend on item
        """
        dependees = []
        for fn in self.depids:
            if item in self.depids[fn]:
                dependees.append(fn)
        return dependees

    def get_rdependees(self, item):
        """
        Return a list of targets which depend on runtime item
        """
        dependees = []
        for fn in self.rdepids:
            if item in self.rdepids[fn]:
                dependees.append(fn)
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
            self.remove_buildtarget(item)

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
            close_matches = self.get_close_matches(item, list(dataCache.providers.keys()))
            # Is it in RuntimeProviders ?
            all_p = bb.providers.getRuntimeProviders(dataCache, item)
            for fn in all_p:
                new = dataCache.pkg_fn[fn] + " RPROVIDES " + item
                if new not in close_matches:
                    close_matches.append(new)
            bb.event.fire(bb.event.NoProvider(item, dependees=self.get_dependees(item), reasons=self.get_reasons(item), close_matches=close_matches), cfgData)
            raise bb.providers.NoProvider(item)

        if self.have_build_target(item):
            return

        all_p = dataCache.providers[item]

        eligible, foundUnique = bb.providers.filterProviders(all_p, item, cfgData, dataCache)
        eligible = [p for p in eligible if not p in self.failed_fns]

        if not eligible:
            bb.event.fire(bb.event.NoProvider(item, dependees=self.get_dependees(item), reasons=["No eligible PROVIDERs exist for '%s'" % item]), cfgData)
            raise bb.providers.NoProvider(item)

        if len(eligible) > 1 and foundUnique == False:
            if item not in self.consider_msgs_cache:
                providers_list = []
                for fn in eligible:
                    providers_list.append(dataCache.pkg_fn[fn])
                bb.event.fire(bb.event.MultipleProviders(item, providers_list), cfgData)
            self.consider_msgs_cache.append(item)

        for fn in eligible:
            if fn in self.failed_fns:
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
            bb.event.fire(bb.event.NoProvider(item, runtime=True, dependees=self.get_rdependees(item), reasons=self.get_reasons(item, True)), cfgData)
            raise bb.providers.NoRProvider(item)

        eligible, numberPreferred = bb.providers.filterProvidersRunTime(all_p, item, cfgData, dataCache)
        eligible = [p for p in eligible if not p in self.failed_fns]

        if not eligible:
            bb.event.fire(bb.event.NoProvider(item, runtime=True, dependees=self.get_rdependees(item), reasons=["No eligible RPROVIDERs exist for '%s'" % item]), cfgData)
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
            if fn in self.failed_fns:
                continue
            logger.debug(2, "adding '%s' to satisfy runtime '%s'", fn, item)
            self.add_runtime_target(fn, item)
            self.add_tasks(fn, dataCache)

    def fail_fn(self, fn, missing_list=None):
        """
        Mark a file as failed (unbuildable)
        Remove any references from build and runtime provider lists

        missing_list, A list of missing requirements for this target
        """
        if fn in self.failed_fns:
            return
        if not missing_list:
            missing_list = []
        logger.debug(1, "File '%s' is unbuildable, removing...", fn)
        self.failed_fns.append(fn)
        for target in self.build_targets:
            if fn in self.build_targets[target]:
                self.build_targets[target].remove(fn)
                if len(self.build_targets[target]) == 0:
                    self.remove_buildtarget(target, missing_list)
        for target in self.run_targets:
            if fn in self.run_targets[target]:
                self.run_targets[target].remove(fn)
                if len(self.run_targets[target]) == 0:
                    self.remove_runtarget(target, missing_list)

    def remove_buildtarget(self, target, missing_list=None):
        """
        Mark a build target as failed (unbuildable)
        Trigger removal of any files that have this as a dependency
        """
        if not missing_list:
            missing_list = [target]
        else:
            missing_list = [target] + missing_list
        logger.verbose("Target '%s' is unbuildable, removing...\nMissing or unbuildable dependency chain was: %s", target, missing_list)
        self.failed_deps.append(target)
        dependees = self.get_dependees(target)
        for fn in dependees:
            self.fail_fn(fn, missing_list)
        for tid in self.taskentries:
            for (idepend, idependtask) in self.taskentries[tid].idepends:
                if idepend == target:
                    fn = tid.rsplit(":",1)[0]
                    self.fail_fn(fn, missing_list)

        if self.abort and target in self.external_targets:
            logger.error("Required build target '%s' has no buildable providers.\nMissing or unbuildable dependency chain was: %s", target, missing_list)
            raise bb.providers.NoProvider(target)

    def remove_runtarget(self, target, missing_list=None):
        """
        Mark a run target as failed (unbuildable)
        Trigger removal of any files that have this as a dependency
        """
        if not missing_list:
            missing_list = [target]
        else:
            missing_list = [target] + missing_list

        logger.info("Runtime target '%s' is unbuildable, removing...\nMissing or unbuildable dependency chain was: %s", target, missing_list)
        self.failed_rdeps.append(target)
        dependees = self.get_rdependees(target)
        for fn in dependees:
            self.fail_fn(fn, missing_list)
        for tid in self.taskentries:
            for (idepend, idependtask) in self.taskentries[tid].irdepends:
                if idepend == target:
                    fn = tid.rsplit(":",1)[0]
                    self.fail_fn(fn, missing_list)

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
                    if self.abort and target in self.external_targets and not self.allowincomplete:
                        raise
                    if not self.allowincomplete:
                        self.remove_buildtarget(target)
            for target in self.get_unresolved_run_targets(dataCache):
                try:
                    self.add_rprovider(cfgData, dataCache, target)
                    added = added + 1
                except (bb.providers.NoRProvider, bb.providers.MultipleRProvider):
                    self.remove_runtarget(target)
            logger.debug(1, "Resolved " + str(added) + " extra dependencies")
            if added == 0:
                break
        # self.dump_data()

    def get_providermap(self, prefix=None):
        provmap = {}
        for name in self.build_targets:
            if prefix and not name.startswith(prefix):
                continue
            if self.have_build_target(name):
                provider = self.get_provider(name)
                if provider:
                    provmap[name] = provider[0]
        return provmap

    def get_mcdepends(self):
        return self.mcdepends

    def dump_data(self):
        """
        Dump some debug information on the internal data structures
        """
        logger.debug(3, "build_names:")
        logger.debug(3, ", ".join(self.build_targets))

        logger.debug(3, "run_names:")
        logger.debug(3, ", ".join(self.run_targets))

        logger.debug(3, "build_targets:")
        for target in self.build_targets:
            targets = "None"
            if target in self.build_targets:
                targets = self.build_targets[target]
            logger.debug(3, " %s: %s", target, targets)

        logger.debug(3, "run_targets:")
        for target in self.run_targets:
            targets = "None"
            if target in self.run_targets:
                targets = self.run_targets[target]
            logger.debug(3, " %s: %s", target, targets)

        logger.debug(3, "tasks:")
        for tid in self.taskentries:
            logger.debug(3, " %s: %s %s %s",
                       tid,
                       self.taskentries[tid].idepends,
                       self.taskentries[tid].irdepends,
                       self.taskentries[tid].tdepends)

        logger.debug(3, "dependency ids (per fn):")
        for fn in self.depids:
            logger.debug(3, " %s: %s", fn, self.depids[fn])

        logger.debug(3, "runtime dependency ids (per fn):")
        for fn in self.rdepids:
            logger.debug(3, " %s: %s", fn, self.rdepids[fn])
