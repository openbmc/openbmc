#!/usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
BitBake 'RunQueue' implementation

Handles preparation and execution of a queue of tasks
"""

# Copyright (C) 2006-2007  Richard Purdie
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

import copy
import os
import sys
import signal
import stat
import fcntl
import errno
import logging
import re
import bb
from bb import msg, data, event
from bb import monitordisk
import subprocess

try:
    import cPickle as pickle
except ImportError:
    import pickle

bblogger = logging.getLogger("BitBake")
logger = logging.getLogger("BitBake.RunQueue")

__find_md5__ = re.compile( r'(?i)(?<![a-z0-9])[a-f0-9]{32}(?![a-z0-9])' )

class RunQueueStats:
    """
    Holds statistics on the tasks handled by the associated runQueue
    """
    def __init__(self, total):
        self.completed = 0
        self.skipped = 0
        self.failed = 0
        self.active = 0
        self.total = total

    def copy(self):
        obj = self.__class__(self.total)
        obj.__dict__.update(self.__dict__)
        return obj

    def taskFailed(self):
        self.active = self.active - 1
        self.failed = self.failed + 1

    def taskCompleted(self, number = 1):
        self.active = self.active - number
        self.completed = self.completed + number

    def taskSkipped(self, number = 1):
        self.active = self.active + number
        self.skipped = self.skipped + number

    def taskActive(self):
        self.active = self.active + 1

# These values indicate the next step due to be run in the
# runQueue state machine
runQueuePrepare = 2
runQueueSceneInit = 3
runQueueSceneRun = 4
runQueueRunInit = 5
runQueueRunning = 6
runQueueFailed = 7
runQueueCleanUp = 8
runQueueComplete = 9

class RunQueueScheduler(object):
    """
    Control the order tasks are scheduled in.
    """
    name = "basic"

    def __init__(self, runqueue, rqdata):
        """
        The default scheduler just returns the first buildable task (the
        priority map is sorted by task number)
        """
        self.rq = runqueue
        self.rqdata = rqdata
        self.numTasks = len(self.rqdata.runq_fnid)

        self.prio_map = []
        self.prio_map.extend(range(self.numTasks))

        self.buildable = []
        self.stamps = {}
        for taskid in xrange(self.numTasks):
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[taskid]]
            taskname = self.rqdata.runq_task[taskid]
            self.stamps[taskid] = bb.build.stampfile(taskname, self.rqdata.dataCache, fn)
            if self.rq.runq_buildable[taskid] == 1:
                self.buildable.append(taskid)

        self.rev_prio_map = None

    def next_buildable_task(self):
        """
        Return the id of the first task we find that is buildable
        """
        self.buildable = [x for x in self.buildable if not self.rq.runq_running[x] == 1]
        if not self.buildable:
            return None
        if len(self.buildable) == 1:
            taskid = self.buildable[0]
            stamp = self.stamps[taskid]
            if stamp not in self.rq.build_stamps.itervalues():
                return taskid

        if not self.rev_prio_map:
            self.rev_prio_map = range(self.numTasks)
            for taskid in xrange(self.numTasks):
                self.rev_prio_map[self.prio_map[taskid]] = taskid

        best = None
        bestprio = None
        for taskid in self.buildable:
            prio = self.rev_prio_map[taskid]
            if bestprio is None or bestprio > prio:
                stamp = self.stamps[taskid]
                if stamp in self.rq.build_stamps.itervalues():
                    continue
                bestprio = prio
                best = taskid

        return best

    def next(self):
        """
        Return the id of the task we should build next
        """
        if self.rq.stats.active < self.rq.number_tasks:
            return self.next_buildable_task()

    def newbuilable(self, task):
        self.buildable.append(task)

class RunQueueSchedulerSpeed(RunQueueScheduler):
    """
    A scheduler optimised for speed. The priority map is sorted by task weight,
    heavier weighted tasks (tasks needed by the most other tasks) are run first.
    """
    name = "speed"

    def __init__(self, runqueue, rqdata):
        """
        The priority map is sorted by task weight.
        """
        RunQueueScheduler.__init__(self, runqueue, rqdata)

        sortweight = sorted(copy.deepcopy(self.rqdata.runq_weight))
        copyweight = copy.deepcopy(self.rqdata.runq_weight)
        self.prio_map = []

        for weight in sortweight:
            idx = copyweight.index(weight)
            self.prio_map.append(idx)
            copyweight[idx] = -1

        self.prio_map.reverse()

class RunQueueSchedulerCompletion(RunQueueSchedulerSpeed):
    """
    A scheduler optimised to complete .bb files are quickly as possible. The
    priority map is sorted by task weight, but then reordered so once a given
    .bb file starts to build, it's completed as quickly as possible. This works
    well where disk space is at a premium and classes like OE's rm_work are in
    force.
    """
    name = "completion"

    def __init__(self, runqueue, rqdata):
        RunQueueSchedulerSpeed.__init__(self, runqueue, rqdata)

        #FIXME - whilst this groups all fnids together it does not reorder the
        #fnid groups optimally.

        basemap = copy.deepcopy(self.prio_map)
        self.prio_map = []
        while (len(basemap) > 0):
            entry = basemap.pop(0)
            self.prio_map.append(entry)
            fnid = self.rqdata.runq_fnid[entry]
            todel = []
            for entry in basemap:
                entry_fnid = self.rqdata.runq_fnid[entry]
                if entry_fnid == fnid:
                    todel.append(basemap.index(entry))
                    self.prio_map.append(entry)
            todel.reverse()
            for idx in todel:
                del basemap[idx]

class RunQueueData:
    """
    BitBake Run Queue implementation
    """
    def __init__(self, rq, cooker, cfgData, dataCache, taskData, targets):
        self.cooker = cooker
        self.dataCache = dataCache
        self.taskData = taskData
        self.targets = targets
        self.rq = rq
        self.warn_multi_bb = False

        self.stampwhitelist = cfgData.getVar("BB_STAMP_WHITELIST", True) or ""
        self.multi_provider_whitelist = (cfgData.getVar("MULTI_PROVIDER_WHITELIST", True) or "").split()

        self.reset()

    def reset(self):
        self.runq_fnid = []
        self.runq_task = []
        self.runq_depends = []
        self.runq_revdeps = []
        self.runq_hash = []

    def runq_depends_names(self, ids):
        import re
        ret = []
        for id in self.runq_depends[ids]:
            nam = os.path.basename(self.get_user_idstring(id))
            nam = re.sub("_[^,]*,", ",", nam)
            ret.extend([nam])
        return ret

    def get_task_name(self, task):
        return self.runq_task[task]

    def get_task_file(self, task):
        return self.taskData.fn_index[self.runq_fnid[task]]

    def get_task_hash(self, task):
        return self.runq_hash[task]

    def get_user_idstring(self, task, task_name_suffix = ""):
        fn = self.taskData.fn_index[self.runq_fnid[task]]
        taskname = self.runq_task[task] + task_name_suffix
        return "%s, %s" % (fn, taskname)

    def get_short_user_idstring(self, task, task_name_suffix = ""):
        fn = self.taskData.fn_index[self.runq_fnid[task]]
        pn = self.dataCache.pkg_fn[fn]
        taskname = self.runq_task[task] + task_name_suffix
        return "%s:%s" % (pn, taskname)


    def get_task_id(self, fnid, taskname):
        for listid in xrange(len(self.runq_fnid)):
            if self.runq_fnid[listid] == fnid and self.runq_task[listid] == taskname:
                return listid
        return None

    def circular_depchains_handler(self, tasks):
        """
        Some tasks aren't buildable, likely due to circular dependency issues.
        Identify the circular dependencies and print them in a user readable format.
        """
        from copy import deepcopy

        valid_chains = []
        explored_deps = {}
        msgs = []

        def chain_reorder(chain):
            """
            Reorder a dependency chain so the lowest task id is first
            """
            lowest = 0
            new_chain = []
            for entry in xrange(len(chain)):
                if chain[entry] < chain[lowest]:
                    lowest = entry
            new_chain.extend(chain[lowest:])
            new_chain.extend(chain[:lowest])
            return new_chain

        def chain_compare_equal(chain1, chain2):
            """
            Compare two dependency chains and see if they're the same
            """
            if len(chain1) != len(chain2):
                return False
            for index in xrange(len(chain1)):
                if chain1[index] != chain2[index]:
                    return False
            return True

        def chain_array_contains(chain, chain_array):
            """
            Return True if chain_array contains chain
            """
            for ch in chain_array:
                if chain_compare_equal(ch, chain):
                    return True
            return False

        def find_chains(taskid, prev_chain):
            prev_chain.append(taskid)
            total_deps = []
            total_deps.extend(self.runq_revdeps[taskid])
            for revdep in self.runq_revdeps[taskid]:
                if revdep in prev_chain:
                    idx = prev_chain.index(revdep)
                    # To prevent duplicates, reorder the chain to start with the lowest taskid
                    # and search through an array of those we've already printed
                    chain = prev_chain[idx:]
                    new_chain = chain_reorder(chain)
                    if not chain_array_contains(new_chain, valid_chains):
                        valid_chains.append(new_chain)
                        msgs.append("Dependency loop #%d found:\n" % len(valid_chains))
                        for dep in new_chain:
                            msgs.append("  Task %s (%s) (dependent Tasks %s)\n" % (dep, self.get_user_idstring(dep), self.runq_depends_names(dep)))
                        msgs.append("\n")
                    if len(valid_chains) > 10:
                        msgs.append("Aborted dependency loops search after 10 matches.\n")
                        return msgs
                    continue
                scan = False
                if revdep not in explored_deps:
                    scan = True
                elif revdep in explored_deps[revdep]:
                    scan = True
                else:
                    for dep in prev_chain:
                        if dep in explored_deps[revdep]:
                            scan = True
                if scan:
                    find_chains(revdep, copy.deepcopy(prev_chain))
                for dep in explored_deps[revdep]:
                    if dep not in total_deps:
                        total_deps.append(dep)

            explored_deps[taskid] = total_deps

        for task in tasks:
            find_chains(task, [])

        return msgs

    def calculate_task_weights(self, endpoints):
        """
        Calculate a number representing the "weight" of each task. Heavier weighted tasks
        have more dependencies and hence should be executed sooner for maximum speed.

        This function also sanity checks the task list finding tasks that are not
        possible to execute due to circular dependencies.
        """

        numTasks = len(self.runq_fnid)
        weight = []
        deps_left = []
        task_done = []

        for listid in xrange(numTasks):
            task_done.append(False)
            weight.append(1)
            deps_left.append(len(self.runq_revdeps[listid]))

        for listid in endpoints:
            weight[listid] = 10
            task_done[listid] = True

        while True:
            next_points = []
            for listid in endpoints:
                for revdep in self.runq_depends[listid]:
                    weight[revdep] = weight[revdep] + weight[listid]
                    deps_left[revdep] = deps_left[revdep] - 1
                    if deps_left[revdep] == 0:
                        next_points.append(revdep)
                        task_done[revdep] = True
            endpoints = next_points
            if len(next_points) == 0:
                break

        # Circular dependency sanity check
        problem_tasks = []
        for task in xrange(numTasks):
            if task_done[task] is False or deps_left[task] != 0:
                problem_tasks.append(task)
                logger.debug(2, "Task %s (%s) is not buildable", task, self.get_user_idstring(task))
                logger.debug(2, "(Complete marker was %s and the remaining dependency count was %s)\n", task_done[task], deps_left[task])

        if problem_tasks:
            message = "Unbuildable tasks were found.\n"
            message = message + "These are usually caused by circular dependencies and any circular dependency chains found will be printed below. Increase the debug level to see a list of unbuildable tasks.\n\n"
            message = message + "Identifying dependency loops (this may take a short while)...\n"
            logger.error(message)

            msgs = self.circular_depchains_handler(problem_tasks)

            message = "\n"
            for msg in msgs:
                message = message + msg
            bb.msg.fatal("RunQueue", message)

        return weight

    def prepare(self):
        """
        Turn a set of taskData into a RunQueue and compute data needed
        to optimise the execution order.
        """

        runq_build = []
        recursivetasks = {}
        recursiveitasks = {}
        recursivetasksselfref = set()

        taskData = self.taskData

        if len(taskData.tasks_name) == 0:
            # Nothing to do
            return 0

        logger.info("Preparing RunQueue")

        # Step A - Work out a list of tasks to run
        #
        # Taskdata gives us a list of possible providers for every build and run
        # target ordered by priority. It also gives information on each of those
        # providers.
        #
        # To create the actual list of tasks to execute we fix the list of
        # providers and then resolve the dependencies into task IDs. This
        # process is repeated for each type of dependency (tdepends, deptask,
        # rdeptast, recrdeptask, idepends).

        def add_build_dependencies(depids, tasknames, depends):
            for depid in depids:
                # Won't be in build_targets if ASSUME_PROVIDED
                if depid not in taskData.build_targets:
                    continue
                depdata = taskData.build_targets[depid][0]
                if depdata is None:
                    continue
                for taskname in tasknames:
                    taskid = taskData.gettask_id_fromfnid(depdata, taskname)
                    if taskid is not None:
                        depends.add(taskid)

        def add_runtime_dependencies(depids, tasknames, depends):
            for depid in depids:
                if depid not in taskData.run_targets:
                    continue
                depdata = taskData.run_targets[depid][0]
                if depdata is None:
                    continue
                for taskname in tasknames:
                    taskid = taskData.gettask_id_fromfnid(depdata, taskname)
                    if taskid is not None:
                        depends.add(taskid)

        def add_resolved_dependencies(depids, tasknames, depends):
            for depid in depids:
                for taskname in tasknames:
                    taskid = taskData.gettask_id_fromfnid(depid, taskname)
                    if taskid is not None:
                        depends.add(taskid)

        for task in xrange(len(taskData.tasks_name)):
            depends = set()
            fnid = taskData.tasks_fnid[task]
            fn = taskData.fn_index[fnid]
            task_deps = self.dataCache.task_deps[fn]

            #logger.debug(2, "Processing %s:%s", fn, taskData.tasks_name[task])

            if fnid not in taskData.failed_fnids:

                # Resolve task internal dependencies
                #
                # e.g. addtask before X after Y
                depends = set(taskData.tasks_tdepends[task])

                # Resolve 'deptask' dependencies
                #
                # e.g. do_sometask[deptask] = "do_someothertask"
                # (makes sure sometask runs after someothertask of all DEPENDS)
                if 'deptask' in task_deps and taskData.tasks_name[task] in task_deps['deptask']:
                    tasknames = task_deps['deptask'][taskData.tasks_name[task]].split()
                    add_build_dependencies(taskData.depids[fnid], tasknames, depends)

                # Resolve 'rdeptask' dependencies
                #
                # e.g. do_sometask[rdeptask] = "do_someothertask"
                # (makes sure sometask runs after someothertask of all RDEPENDS)
                if 'rdeptask' in task_deps and taskData.tasks_name[task] in task_deps['rdeptask']:
                    tasknames = task_deps['rdeptask'][taskData.tasks_name[task]].split()
                    add_runtime_dependencies(taskData.rdepids[fnid], tasknames, depends)

                # Resolve inter-task dependencies
                #
                # e.g. do_sometask[depends] = "targetname:do_someothertask"
                # (makes sure sometask runs after targetname's someothertask)
                idepends = taskData.tasks_idepends[task]
                for (depid, idependtask) in idepends:
                    if depid in taskData.build_targets and not depid in taskData.failed_deps:
                        # Won't be in build_targets if ASSUME_PROVIDED
                        depdata = taskData.build_targets[depid][0]
                        if depdata is not None:
                            taskid = taskData.gettask_id_fromfnid(depdata, idependtask)
                            if taskid is None:
                                bb.msg.fatal("RunQueue", "Task %s in %s depends upon non-existent task %s in %s" % (taskData.tasks_name[task], fn, idependtask, taskData.fn_index[depdata]))
                            depends.add(taskid)
                irdepends = taskData.tasks_irdepends[task]
                for (depid, idependtask) in irdepends:
                    if depid in taskData.run_targets:
                        # Won't be in run_targets if ASSUME_PROVIDED
                        depdata = taskData.run_targets[depid][0]
                        if depdata is not None:
                            taskid = taskData.gettask_id_fromfnid(depdata, idependtask)
                            if taskid is None:
                                bb.msg.fatal("RunQueue", "Task %s in %s rdepends upon non-existent task %s in %s" % (taskData.tasks_name[task], fn, idependtask, taskData.fn_index[depdata]))
                            depends.add(taskid)

                # Resolve recursive 'recrdeptask' dependencies (Part A)
                #
                # e.g. do_sometask[recrdeptask] = "do_someothertask"
                # (makes sure sometask runs after someothertask of all DEPENDS, RDEPENDS and intertask dependencies, recursively)
                # We cover the recursive part of the dependencies below
                if 'recrdeptask' in task_deps and taskData.tasks_name[task] in task_deps['recrdeptask']:
                    tasknames = task_deps['recrdeptask'][taskData.tasks_name[task]].split()
                    recursivetasks[task] = tasknames
                    add_build_dependencies(taskData.depids[fnid], tasknames, depends)
                    add_runtime_dependencies(taskData.rdepids[fnid], tasknames, depends)
                    if taskData.tasks_name[task] in tasknames:
                        recursivetasksselfref.add(task)

                    if 'recideptask' in task_deps and taskData.tasks_name[task] in task_deps['recideptask']:
                        recursiveitasks[task] = []
                        for t in task_deps['recideptask'][taskData.tasks_name[task]].split():
                            newdep = taskData.gettask_id_fromfnid(fnid, t)
                            recursiveitasks[task].append(newdep)

            self.runq_fnid.append(taskData.tasks_fnid[task])
            self.runq_task.append(taskData.tasks_name[task])
            self.runq_depends.append(depends)
            self.runq_revdeps.append(set())
            self.runq_hash.append("")

            runq_build.append(0)

        # Resolve recursive 'recrdeptask' dependencies (Part B)
        #
        # e.g. do_sometask[recrdeptask] = "do_someothertask"
        # (makes sure sometask runs after someothertask of all DEPENDS, RDEPENDS and intertask dependencies, recursively)
        # We need to do this separately since we need all of self.runq_depends to be complete before this is processed
        extradeps = {}
        for task in recursivetasks:
            extradeps[task] = set(self.runq_depends[task])
            tasknames = recursivetasks[task]
            seendeps = set()
            seenfnid = []

            def generate_recdeps(t):
                newdeps = set()
                add_resolved_dependencies([taskData.tasks_fnid[t]], tasknames, newdeps)
                extradeps[task].update(newdeps)
                seendeps.add(t)
                newdeps.add(t)
                for i in newdeps:
                    for n in self.runq_depends[i]:
                        if n not in seendeps:
                            generate_recdeps(n)
            generate_recdeps(task)

            if task in recursiveitasks:
                for dep in recursiveitasks[task]:
                    generate_recdeps(dep)

        # Remove circular references so that do_a[recrdeptask] = "do_a do_b" can work
        for task in recursivetasks:
            extradeps[task].difference_update(recursivetasksselfref)

        for task in xrange(len(taskData.tasks_name)):
            # Add in extra dependencies
            if task in extradeps:
                 self.runq_depends[task] = extradeps[task]
            # Remove all self references
            if task in self.runq_depends[task]:
                logger.debug(2, "Task %s (%s %s) contains self reference! %s", task, taskData.fn_index[taskData.tasks_fnid[task]], taskData.tasks_name[task], self.runq_depends[task])
                self.runq_depends[task].remove(task)

        # Step B - Mark all active tasks
        #
        # Start with the tasks we were asked to run and mark all dependencies
        # as active too. If the task is to be 'forced', clear its stamp. Once
        # all active tasks are marked, prune the ones we don't need.

        logger.verbose("Marking Active Tasks")

        def mark_active(listid, depth):
            """
            Mark an item as active along with its depends
            (calls itself recursively)
            """

            if runq_build[listid] == 1:
                return

            runq_build[listid] = 1

            depends = self.runq_depends[listid]
            for depend in depends:
                mark_active(depend, depth+1)

        self.target_pairs = []
        for target in self.targets:
            targetid = taskData.getbuild_id(target[0])

            if targetid not in taskData.build_targets:
                continue

            if targetid in taskData.failed_deps:
                continue

            fnid = taskData.build_targets[targetid][0]
            fn = taskData.fn_index[fnid]
            task = target[1]
            parents = False
            if task.endswith('-'):
                parents = True
                task = task[:-1]

            self.target_pairs.append((fn, task))

            if fnid in taskData.failed_fnids:
                continue

            if task not in taskData.tasks_lookup[fnid]:
                import difflib
                close_matches = difflib.get_close_matches(task, taskData.tasks_lookup[fnid], cutoff=0.7)
                if close_matches:
                    extra = ". Close matches:\n  %s" % "\n  ".join(close_matches)
                else:
                    extra = ""
                bb.msg.fatal("RunQueue", "Task %s does not exist for target %s%s" % (task, target[0], extra))
  
            # For tasks called "XXXX-", ony run their dependencies
            listid = taskData.tasks_lookup[fnid][task]
            if parents:
                for i in self.runq_depends[listid]:
                    mark_active(i, 1)
            else:
                mark_active(listid, 1)

        # Step C - Prune all inactive tasks
        #
        # Once all active tasks are marked, prune the ones we don't need.

        maps = []
        delcount = 0
        for listid in xrange(len(self.runq_fnid)):
            if runq_build[listid-delcount] == 1:
                maps.append(listid-delcount)
            else:
                del self.runq_fnid[listid-delcount]
                del self.runq_task[listid-delcount]
                del self.runq_depends[listid-delcount]
                del runq_build[listid-delcount]
                del self.runq_revdeps[listid-delcount]
                del self.runq_hash[listid-delcount]
                delcount = delcount + 1
                maps.append(-1)

        #
        # Step D - Sanity checks and computation
        #

        # Check to make sure we still have tasks to run
        if len(self.runq_fnid) == 0:
            if not taskData.abort:
                bb.msg.fatal("RunQueue", "All buildable tasks have been run but the build is incomplete (--continue mode). Errors for the tasks that failed will have been printed above.")
            else:
                bb.msg.fatal("RunQueue", "No active tasks and not in --continue mode?! Please report this bug.")

        logger.verbose("Pruned %s inactive tasks, %s left", delcount, len(self.runq_fnid))

        # Remap the dependencies to account for the deleted tasks
        # Check we didn't delete a task we depend on
        for listid in xrange(len(self.runq_fnid)):
            newdeps = []
            origdeps = self.runq_depends[listid]
            for origdep in origdeps:
                if maps[origdep] == -1:
                    bb.msg.fatal("RunQueue", "Invalid mapping - Should never happen!")
                newdeps.append(maps[origdep])
            self.runq_depends[listid] = set(newdeps)

        logger.verbose("Assign Weightings")

        # Generate a list of reverse dependencies to ease future calculations
        for listid in xrange(len(self.runq_fnid)):
            for dep in self.runq_depends[listid]:
                self.runq_revdeps[dep].add(listid)

        # Identify tasks at the end of dependency chains
        # Error on circular dependency loops (length two)
        endpoints = []
        for listid in xrange(len(self.runq_fnid)):
            revdeps = self.runq_revdeps[listid]
            if len(revdeps) == 0:
                endpoints.append(listid)
            for dep in revdeps:
                if dep in self.runq_depends[listid]:
                    #self.dump_data(taskData)
                    bb.msg.fatal("RunQueue", "Task %s (%s) has circular dependency on %s (%s)" % (taskData.fn_index[self.runq_fnid[dep]], self.runq_task[dep], taskData.fn_index[self.runq_fnid[listid]], self.runq_task[listid]))

        logger.verbose("Compute totals (have %s endpoint(s))", len(endpoints))

        # Calculate task weights
        # Check of higher length circular dependencies
        self.runq_weight = self.calculate_task_weights(endpoints)

        # Sanity Check - Check for multiple tasks building the same provider
        prov_list = {}
        seen_fn = []
        for task in xrange(len(self.runq_fnid)):
            fn = taskData.fn_index[self.runq_fnid[task]]
            if fn in seen_fn:
                continue
            seen_fn.append(fn)
            for prov in self.dataCache.fn_provides[fn]:
                if prov not in prov_list:
                    prov_list[prov] = [fn]
                elif fn not in prov_list[prov]:
                    prov_list[prov].append(fn)
        for prov in prov_list:
            if len(prov_list[prov]) > 1 and prov not in self.multi_provider_whitelist:
                seen_pn = []
                # If two versions of the same PN are being built its fatal, we don't support it.
                for fn in prov_list[prov]:
                    pn = self.dataCache.pkg_fn[fn]
                    if pn not in seen_pn:
                        seen_pn.append(pn)
                    else:
                        bb.fatal("Multiple versions of %s are due to be built (%s). Only one version of a given PN should be built in any given build. You likely need to set PREFERRED_VERSION_%s to select the correct version or don't depend on multiple versions." % (pn, " ".join(prov_list[prov]), pn))
                msg = "Multiple .bb files are due to be built which each provide %s:\n  %s" % (prov, "\n  ".join(prov_list[prov]))
                #
                # Construct a list of things which uniquely depend on each provider
                # since this may help the user figure out which dependency is triggering this warning
                #
                msg += "\nA list of tasks depending on these providers is shown and may help explain where the dependency comes from."
                deplist = {}
                commondeps = None
                for provfn in prov_list[prov]:
                    deps = set()
                    for task, fnid in enumerate(self.runq_fnid):
                        fn = taskData.fn_index[fnid]
                        if fn != provfn:
                            continue
                        for dep in self.runq_revdeps[task]:
                            fn = taskData.fn_index[self.runq_fnid[dep]]
                            if fn == provfn:
                                continue
                            deps.add(self.get_short_user_idstring(dep))
                    if not commondeps:
                        commondeps = set(deps)
                    else:
                        commondeps &= deps
                    deplist[provfn] = deps
                for provfn in deplist:
                    msg += "\n%s has unique dependees:\n  %s" % (provfn, "\n  ".join(deplist[provfn] - commondeps))
                #
                # Construct a list of provides and runtime providers for each recipe
                # (rprovides has to cover RPROVIDES, PACKAGES, PACKAGES_DYNAMIC)
                #
                msg += "\nIt could be that one recipe provides something the other doesn't and should. The following provider and runtime provider differences may be helpful."
                provide_results = {}
                rprovide_results = {}
                commonprovs = None
                commonrprovs = None
                for provfn in prov_list[prov]:
                    provides = set(self.dataCache.fn_provides[provfn])
                    rprovides = set()
                    for rprovide in self.dataCache.rproviders:
                        if provfn in self.dataCache.rproviders[rprovide]:
                            rprovides.add(rprovide)
                    for package in self.dataCache.packages:
                        if provfn in self.dataCache.packages[package]:
                            rprovides.add(package)
                    for package in self.dataCache.packages_dynamic:
                        if provfn in self.dataCache.packages_dynamic[package]:
                            rprovides.add(package)
                    if not commonprovs:
                        commonprovs = set(provides)
                    else:
                        commonprovs &= provides
                    provide_results[provfn] = provides
                    if not commonrprovs:
                        commonrprovs = set(rprovides)
                    else:
                        commonrprovs &= rprovides
                    rprovide_results[provfn] = rprovides
                #msg += "\nCommon provides:\n  %s" % ("\n  ".join(commonprovs))
                #msg += "\nCommon rprovides:\n  %s" % ("\n  ".join(commonrprovs))
                for provfn in prov_list[prov]:
                    msg += "\n%s has unique provides:\n  %s" % (provfn, "\n  ".join(provide_results[provfn] - commonprovs))
                    msg += "\n%s has unique rprovides:\n  %s" % (provfn, "\n  ".join(rprovide_results[provfn] - commonrprovs))

                if self.warn_multi_bb:
                    logger.warn(msg)
                else:
                    logger.error(msg)

        # Create a whitelist usable by the stamp checks
        stampfnwhitelist = []
        for entry in self.stampwhitelist.split():
            entryid = self.taskData.getbuild_id(entry)
            if entryid not in self.taskData.build_targets:
                continue
            fnid = self.taskData.build_targets[entryid][0]
            fn = self.taskData.fn_index[fnid]
            stampfnwhitelist.append(fn)
        self.stampfnwhitelist = stampfnwhitelist

        # Iterate over the task list looking for tasks with a 'setscene' function
        self.runq_setscene = []
        if not self.cooker.configuration.nosetscene:
            for task in range(len(self.runq_fnid)):
                setscene = taskData.gettask_id(self.taskData.fn_index[self.runq_fnid[task]], self.runq_task[task] + "_setscene", False)
                if not setscene:
                    continue
                self.runq_setscene.append(task)

        def invalidate_task(fn, taskname, error_nostamp):
            taskdep = self.dataCache.task_deps[fn]
            fnid = self.taskData.getfn_id(fn)
            if taskname not in taskData.tasks_lookup[fnid]:
                logger.warn("Task %s does not exist, invalidating this task will have no effect" % taskname)
            if 'nostamp' in taskdep and taskname in taskdep['nostamp']:
                if error_nostamp:
                    bb.fatal("Task %s is marked nostamp, cannot invalidate this task" % taskname)
                else:
                    bb.debug(1, "Task %s is marked nostamp, cannot invalidate this task" % taskname)
            else:
                logger.verbose("Invalidate task %s, %s", taskname, fn)
                bb.parse.siggen.invalidate_task(taskname, self.dataCache, fn)

        # Invalidate task if force mode active
        if self.cooker.configuration.force:
            for (fn, target) in self.target_pairs:
                invalidate_task(fn, target, False)

        # Invalidate task if invalidate mode active
        if self.cooker.configuration.invalidate_stamp:
            for (fn, target) in self.target_pairs:
                for st in self.cooker.configuration.invalidate_stamp.split(','):
                    if not st.startswith("do_"):
                        st = "do_%s" % st
                    invalidate_task(fn, st, True)

        # Create and print to the logs a virtual/xxxx -> PN (fn) table
        virtmap = taskData.get_providermap(prefix="virtual/")
        virtpnmap = {}
        for v in virtmap:
            virtpnmap[v] = self.dataCache.pkg_fn[virtmap[v]]
            bb.debug(2, "%s resolved to: %s (%s)" % (v, virtpnmap[v], virtmap[v]))
        if hasattr(bb.parse.siggen, "tasks_resolved"):
            bb.parse.siggen.tasks_resolved(virtmap, virtpnmap, self.dataCache)

        # Iterate over the task list and call into the siggen code
        dealtwith = set()
        todeal = set(range(len(self.runq_fnid)))
        while len(todeal) > 0:
            for task in todeal.copy():
                if len(self.runq_depends[task] - dealtwith) == 0:
                    dealtwith.add(task)
                    todeal.remove(task)
                    procdep = []
                    for dep in self.runq_depends[task]:
                        procdep.append(self.taskData.fn_index[self.runq_fnid[dep]] + "." + self.runq_task[dep])
                    self.runq_hash[task] = bb.parse.siggen.get_taskhash(self.taskData.fn_index[self.runq_fnid[task]], self.runq_task[task], procdep, self.dataCache)

        bb.parse.siggen.writeout_file_checksum_cache()
        return len(self.runq_fnid)

    def dump_data(self, taskQueue):
        """
        Dump some debug information on the internal data structures
        """
        logger.debug(3, "run_tasks:")
        for task in xrange(len(self.rqdata.runq_task)):
            logger.debug(3, " (%s)%s - %s: %s   Deps %s RevDeps %s", task,
                         taskQueue.fn_index[self.rqdata.runq_fnid[task]],
                         self.rqdata.runq_task[task],
                         self.rqdata.runq_weight[task],
                         self.rqdata.runq_depends[task],
                         self.rqdata.runq_revdeps[task])

        logger.debug(3, "sorted_tasks:")
        for task1 in xrange(len(self.rqdata.runq_task)):
            if task1 in self.prio_map:
                task = self.prio_map[task1]
                logger.debug(3, " (%s)%s - %s: %s   Deps %s RevDeps %s", task,
                           taskQueue.fn_index[self.rqdata.runq_fnid[task]],
                           self.rqdata.runq_task[task],
                           self.rqdata.runq_weight[task],
                           self.rqdata.runq_depends[task],
                           self.rqdata.runq_revdeps[task])

class RunQueue:
    def __init__(self, cooker, cfgData, dataCache, taskData, targets):

        self.cooker = cooker
        self.cfgData = cfgData
        self.rqdata = RunQueueData(self, cooker, cfgData, dataCache, taskData, targets)

        self.stamppolicy = cfgData.getVar("BB_STAMP_POLICY", True) or "perfile"
        self.hashvalidate = cfgData.getVar("BB_HASHCHECK_FUNCTION", True) or None
        self.setsceneverify = cfgData.getVar("BB_SETSCENE_VERIFY_FUNCTION", True) or None
        self.depvalidate = cfgData.getVar("BB_SETSCENE_DEPVALID", True) or None

        self.state = runQueuePrepare

        # For disk space monitor
        self.dm = monitordisk.diskMonitor(cfgData)

        self.rqexe = None
        self.worker = None
        self.workerpipe = None
        self.fakeworker = None
        self.fakeworkerpipe = None

    def _start_worker(self, fakeroot = False, rqexec = None):
        logger.debug(1, "Starting bitbake-worker")
        magic = "decafbad"
        if self.cooker.configuration.profile:
            magic = "decafbadbad"
        if fakeroot:
            magic = magic + "beef"
            fakerootcmd = self.cfgData.getVar("FAKEROOTCMD", True)
            fakerootenv = (self.cfgData.getVar("FAKEROOTBASEENV", True) or "").split()
            env = os.environ.copy()
            for key, value in (var.split('=') for var in fakerootenv):
                env[key] = value
            worker = subprocess.Popen([fakerootcmd, "bitbake-worker", magic], stdout=subprocess.PIPE, stdin=subprocess.PIPE, env=env)
        else:
            worker = subprocess.Popen(["bitbake-worker", magic], stdout=subprocess.PIPE, stdin=subprocess.PIPE)
        bb.utils.nonblockingfd(worker.stdout)
        workerpipe = runQueuePipe(worker.stdout, None, self.cfgData, self, rqexec)

        workerdata = {
            "taskdeps" : self.rqdata.dataCache.task_deps,
            "fakerootenv" : self.rqdata.dataCache.fakerootenv,
            "fakerootdirs" : self.rqdata.dataCache.fakerootdirs,
            "fakerootnoenv" : self.rqdata.dataCache.fakerootnoenv,
            "sigdata" : bb.parse.siggen.get_taskdata(),
            "runq_hash" : self.rqdata.runq_hash,
            "logdefaultdebug" : bb.msg.loggerDefaultDebugLevel,
            "logdefaultverbose" : bb.msg.loggerDefaultVerbose,
            "logdefaultverboselogs" : bb.msg.loggerVerboseLogs,
            "logdefaultdomain" : bb.msg.loggerDefaultDomains,
            "prhost" : self.cooker.prhost,
            "buildname" : self.cfgData.getVar("BUILDNAME", True),
            "date" : self.cfgData.getVar("DATE", True),
            "time" : self.cfgData.getVar("TIME", True),
        }

        worker.stdin.write("<cookerconfig>" + pickle.dumps(self.cooker.configuration) + "</cookerconfig>")
        worker.stdin.write("<workerdata>" + pickle.dumps(workerdata) + "</workerdata>")
        worker.stdin.flush()

        return worker, workerpipe

    def _teardown_worker(self, worker, workerpipe):
        if not worker:
            return
        logger.debug(1, "Teardown for bitbake-worker")
        try:
           worker.stdin.write("<quit></quit>")
           worker.stdin.flush()
        except IOError:
           pass
        while worker.returncode is None:
            workerpipe.read()
            worker.poll()
        while workerpipe.read():
            continue
        workerpipe.close()

    def start_worker(self):
        if self.worker:
            self.teardown_workers()
        self.teardown = False
        self.worker, self.workerpipe = self._start_worker()

    def start_fakeworker(self, rqexec):
        if not self.fakeworker:
            self.fakeworker, self.fakeworkerpipe = self._start_worker(True, rqexec)

    def teardown_workers(self):
        self.teardown = True
        self._teardown_worker(self.worker, self.workerpipe)
        self.worker = None
        self.workerpipe = None
        self._teardown_worker(self.fakeworker, self.fakeworkerpipe)
        self.fakeworker = None
        self.fakeworkerpipe = None

    def read_workers(self):
        self.workerpipe.read()
        if self.fakeworkerpipe:
            self.fakeworkerpipe.read()

    def active_fds(self):
        fds = []
        if self.workerpipe:
            fds.append(self.workerpipe.input)
        if self.fakeworkerpipe:
            fds.append(self.fakeworkerpipe.input)
        return fds

    def check_stamp_task(self, task, taskname = None, recurse = False, cache = None):
        def get_timestamp(f):
            try:
                if not os.access(f, os.F_OK):
                    return None
                return os.stat(f)[stat.ST_MTIME]
            except:
                return None

        if self.stamppolicy == "perfile":
            fulldeptree = False
        else:
            fulldeptree = True
            stampwhitelist = []
            if self.stamppolicy == "whitelist":
                stampwhitelist = self.rqdata.stampfnwhitelist

        fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
        if taskname is None:
            taskname = self.rqdata.runq_task[task]

        stampfile = bb.build.stampfile(taskname, self.rqdata.dataCache, fn)

        # If the stamp is missing, it's not current
        if not os.access(stampfile, os.F_OK):
            logger.debug(2, "Stampfile %s not available", stampfile)
            return False
        # If it's a 'nostamp' task, it's not current
        taskdep = self.rqdata.dataCache.task_deps[fn]
        if 'nostamp' in taskdep and taskname in taskdep['nostamp']:
            logger.debug(2, "%s.%s is nostamp\n", fn, taskname)
            return False

        if taskname != "do_setscene" and taskname.endswith("_setscene"):
            return True

        if cache is None:
            cache = {}

        iscurrent = True
        t1 = get_timestamp(stampfile)
        for dep in self.rqdata.runq_depends[task]:
            if iscurrent:
                fn2 = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[dep]]
                taskname2 = self.rqdata.runq_task[dep]
                stampfile2 = bb.build.stampfile(taskname2, self.rqdata.dataCache, fn2)
                stampfile3 = bb.build.stampfile(taskname2 + "_setscene", self.rqdata.dataCache, fn2)
                t2 = get_timestamp(stampfile2)
                t3 = get_timestamp(stampfile3)
                if t3 and t3 > t2:
                   continue
                if fn == fn2 or (fulldeptree and fn2 not in stampwhitelist):
                    if not t2:
                        logger.debug(2, 'Stampfile %s does not exist', stampfile2)
                        iscurrent = False
                    if t1 < t2:
                        logger.debug(2, 'Stampfile %s < %s', stampfile, stampfile2)
                        iscurrent = False
                    if recurse and iscurrent:
                        if dep in cache:
                            iscurrent = cache[dep]
                            if not iscurrent:
                                logger.debug(2, 'Stampfile for dependency %s:%s invalid (cached)' % (fn2, taskname2))
                        else:
                            iscurrent = self.check_stamp_task(dep, recurse=True, cache=cache)
                            cache[dep] = iscurrent
        if recurse:
            cache[task] = iscurrent
        return iscurrent

    def _execute_runqueue(self):
        """
        Run the tasks in a queue prepared by rqdata.prepare()
        Upon failure, optionally try to recover the build using any alternate providers
        (if the abort on failure configuration option isn't set)
        """

        retval = True

        if self.state is runQueuePrepare:
            self.rqexe = RunQueueExecuteDummy(self)
            if self.rqdata.prepare() == 0:
                self.state = runQueueComplete
            else:
                self.state = runQueueSceneInit

                # we are ready to run, see if any UI client needs the dependency info
                if bb.cooker.CookerFeatures.SEND_DEPENDS_TREE in self.cooker.featureset:
                    depgraph = self.cooker.buildDependTree(self, self.rqdata.taskData)
                    bb.event.fire(bb.event.DepTreeGenerated(depgraph), self.cooker.data)

        if self.state is runQueueSceneInit:
            dump = self.cooker.configuration.dump_signatures
            if dump:
                if 'printdiff' in dump:
                    invalidtasks = self.print_diffscenetasks()
                self.dump_signatures(dump)
                if 'printdiff' in dump:
                    self.write_diffscenetasks(invalidtasks)
                self.state = runQueueComplete
            else:
                self.start_worker()
                self.rqexe = RunQueueExecuteScenequeue(self)

        if self.state in [runQueueSceneRun, runQueueRunning, runQueueCleanUp]:
            self.dm.check(self)

        if self.state is runQueueSceneRun:
            retval = self.rqexe.execute()

        if self.state is runQueueRunInit:
            if self.cooker.configuration.setsceneonly:
                self.state = runQueueComplete
            else:
                logger.info("Executing RunQueue Tasks")
                self.rqexe = RunQueueExecuteTasks(self)
                self.state = runQueueRunning

        if self.state is runQueueRunning:
            retval = self.rqexe.execute()

        if self.state is runQueueCleanUp:
            retval = self.rqexe.finish()

        if (self.state is runQueueComplete or self.state is runQueueFailed) and self.rqexe:
            self.teardown_workers()
            if self.rqexe.stats.failed:
                logger.info("Tasks Summary: Attempted %d tasks of which %d didn't need to be rerun and %d failed.", self.rqexe.stats.completed + self.rqexe.stats.failed, self.rqexe.stats.skipped, self.rqexe.stats.failed)
            else:
                # Let's avoid the word "failed" if nothing actually did
                logger.info("Tasks Summary: Attempted %d tasks of which %d didn't need to be rerun and all succeeded.", self.rqexe.stats.completed, self.rqexe.stats.skipped)

        if self.state is runQueueFailed:
            if not self.rqdata.taskData.tryaltconfigs:
                raise bb.runqueue.TaskFailure(self.rqexe.failed_fnids)
            for fnid in self.rqexe.failed_fnids:
                self.rqdata.taskData.fail_fnid(fnid)
            self.rqdata.reset()

        if self.state is runQueueComplete:
            # All done
            return False

        # Loop
        return retval

    def execute_runqueue(self):
        # Catch unexpected exceptions and ensure we exit when an error occurs, not loop.
        try:
            return self._execute_runqueue()
        except bb.runqueue.TaskFailure:
            raise
        except SystemExit:
            raise
        except bb.BBHandledException:
            try:
                self.teardown_workers()
            except:
                pass
            self.state = runQueueComplete
            raise
        except:
            logger.error("An uncaught exception occured in runqueue, please see the failure below:")
            try:
                self.teardown_workers()
            except:
                pass
            self.state = runQueueComplete
            raise

    def finish_runqueue(self, now = False):
        if not self.rqexe:
            self.state = runQueueComplete
            return

        if now:
            self.rqexe.finish_now()
        else:
            self.rqexe.finish()

    def dump_signatures(self, options):
        done = set()
        bb.note("Reparsing files to collect dependency data")
        for task in range(len(self.rqdata.runq_fnid)):
            if self.rqdata.runq_fnid[task] not in done:
                fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
                the_data = bb.cache.Cache.loadDataFull(fn, self.cooker.collection.get_file_appends(fn), self.cooker.data)
                done.add(self.rqdata.runq_fnid[task])

        bb.parse.siggen.dump_sigs(self.rqdata.dataCache, options)

        return

    def print_diffscenetasks(self):

        valid = []
        sq_hash = []
        sq_hashfn = []
        sq_fn = []
        sq_taskname = []
        sq_task = []
        noexec = []
        stamppresent = []
        valid_new = set()

        for task in xrange(len(self.rqdata.runq_fnid)):
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
            taskname = self.rqdata.runq_task[task]
            taskdep = self.rqdata.dataCache.task_deps[fn]

            if 'noexec' in taskdep and taskname in taskdep['noexec']:
                noexec.append(task)
                continue

            sq_fn.append(fn)
            sq_hashfn.append(self.rqdata.dataCache.hashfn[fn])
            sq_hash.append(self.rqdata.runq_hash[task])
            sq_taskname.append(taskname)
            sq_task.append(task)
        locs = { "sq_fn" : sq_fn, "sq_task" : sq_taskname, "sq_hash" : sq_hash, "sq_hashfn" : sq_hashfn, "d" : self.cooker.expanded_data }
        try:
            call = self.hashvalidate + "(sq_fn, sq_task, sq_hash, sq_hashfn, d, siginfo=True)"
            valid = bb.utils.better_eval(call, locs)
        # Handle version with no siginfo parameter
        except TypeError:
            call = self.hashvalidate + "(sq_fn, sq_task, sq_hash, sq_hashfn, d)"
            valid = bb.utils.better_eval(call, locs)
        for v in valid:
            valid_new.add(sq_task[v])

        # Tasks which are both setscene and noexec never care about dependencies
        # We therefore find tasks which are setscene and noexec and mark their
        # unique dependencies as valid.
        for task in noexec:
            if task not in self.rqdata.runq_setscene:
                continue
            for dep in self.rqdata.runq_depends[task]:
                hasnoexecparents = True
                for dep2 in self.rqdata.runq_revdeps[dep]:
                    if dep2 in self.rqdata.runq_setscene and dep2 in noexec:
                        continue
                    hasnoexecparents = False
                    break
                if hasnoexecparents:
                    valid_new.add(dep)

        invalidtasks = set()
        for task in xrange(len(self.rqdata.runq_fnid)):
            if task not in valid_new and task not in noexec:
                invalidtasks.add(task)

        found = set()
        processed = set()
        for task in invalidtasks:
            toprocess = set([task])
            while toprocess:
                next = set()
                for t in toprocess:
                    for dep in self.rqdata.runq_depends[t]:
                        if dep in invalidtasks:
                            found.add(task)
                        if dep not in processed:
                            processed.add(dep)
                            next.add(dep)
                toprocess = next
                if task in found:
                    toprocess = set()

        tasklist = []
        for task in invalidtasks.difference(found):
            tasklist.append(self.rqdata.get_user_idstring(task))

        if tasklist:
            bb.plain("The differences between the current build and any cached tasks start at the following tasks:\n" + "\n".join(tasklist))

        return invalidtasks.difference(found)

    def write_diffscenetasks(self, invalidtasks):

        # Define recursion callback
        def recursecb(key, hash1, hash2):
            hashes = [hash1, hash2]
            hashfiles = bb.siggen.find_siginfo(key, None, hashes, self.cfgData)

            recout = []
            if len(hashfiles) == 2:
                out2 = bb.siggen.compare_sigfiles(hashfiles[hash1], hashfiles[hash2], recursecb)
                recout.extend(list('  ' + l for l in out2))
            else:
                recout.append("Unable to find matching sigdata for %s with hashes %s or %s" % (key, hash1, hash2))

            return recout


        for task in invalidtasks:
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
            pn = self.rqdata.dataCache.pkg_fn[fn]
            taskname = self.rqdata.runq_task[task]
            h = self.rqdata.runq_hash[task]
            matches = bb.siggen.find_siginfo(pn, taskname, [], self.cfgData)
            match = None
            for m in matches:
                if h in m:
                    match = m
            if match is None:
                bb.fatal("Can't find a task we're supposed to have written out? (hash: %s)?" % h)
            matches = {k : v for k, v in matches.iteritems() if h not in k}
            if matches:
                latestmatch = sorted(matches.keys(), key=lambda f: matches[f])[-1]
                prevh = __find_md5__.search(latestmatch).group(0)
                output = bb.siggen.compare_sigfiles(latestmatch, match, recursecb)
                bb.plain("\nTask %s:%s couldn't be used from the cache because:\n  We need hash %s, closest matching task was %s\n  " % (pn, taskname, h, prevh) + '\n  '.join(output))

class RunQueueExecute:

    def __init__(self, rq):
        self.rq = rq
        self.cooker = rq.cooker
        self.cfgData = rq.cfgData
        self.rqdata = rq.rqdata

        self.number_tasks = int(self.cfgData.getVar("BB_NUMBER_THREADS", True) or 1)
        self.scheduler = self.cfgData.getVar("BB_SCHEDULER", True) or "speed"

        self.runq_buildable = []
        self.runq_running = []
        self.runq_complete = []

        self.build_stamps = {}
        self.build_stamps2 = []
        self.failed_fnids = []

        self.stampcache = {}

        rq.workerpipe.setrunqueueexec(self)
        if rq.fakeworkerpipe:
            rq.fakeworkerpipe.setrunqueueexec(self)

        if self.number_tasks <= 0:
             bb.fatal("Invalid BB_NUMBER_THREADS %s" % self.number_tasks)

    def runqueue_process_waitpid(self, task, status):

        # self.build_stamps[pid] may not exist when use shared work directory.
        if task in self.build_stamps:
            self.build_stamps2.remove(self.build_stamps[task])
            del self.build_stamps[task]

        if status != 0:
            self.task_fail(task, status)
        else:
            self.task_complete(task)
        return True

    def finish_now(self):

        for worker in [self.rq.worker, self.rq.fakeworker]:
            if not worker:
                continue
            try:
                worker.stdin.write("<finishnow></finishnow>")
                worker.stdin.flush()
            except IOError:
                # worker must have died?
                pass

        if len(self.failed_fnids) != 0:
            self.rq.state = runQueueFailed
            return

        self.rq.state = runQueueComplete
        return

    def finish(self):
        self.rq.state = runQueueCleanUp

        if self.stats.active > 0:
            bb.event.fire(runQueueExitWait(self.stats.active), self.cfgData)
            self.rq.read_workers()
            return self.rq.active_fds()

        if len(self.failed_fnids) != 0:
            self.rq.state = runQueueFailed
            return True

        self.rq.state = runQueueComplete
        return True

    def check_dependencies(self, task, taskdeps, setscene = False):
        if not self.rq.depvalidate:
            return False

        taskdata = {}
        taskdeps.add(task)
        for dep in taskdeps:
            if setscene:
                depid = self.rqdata.runq_setscene[dep]
            else:
                depid = dep
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[depid]]
            pn = self.rqdata.dataCache.pkg_fn[fn]
            taskname = self.rqdata.runq_task[depid]
            taskdata[dep] = [pn, taskname, fn]
        call = self.rq.depvalidate + "(task, taskdata, notneeded, d)"
        locs = { "task" : task, "taskdata" : taskdata, "notneeded" : self.scenequeue_notneeded, "d" : self.cooker.expanded_data }
        valid = bb.utils.better_eval(call, locs)
        return valid

class RunQueueExecuteDummy(RunQueueExecute):
    def __init__(self, rq):
        self.rq = rq
        self.stats = RunQueueStats(0)

    def finish(self):
        self.rq.state = runQueueComplete
        return

class RunQueueExecuteTasks(RunQueueExecute):
    def __init__(self, rq):
        RunQueueExecute.__init__(self, rq)

        self.stats = RunQueueStats(len(self.rqdata.runq_fnid))

        self.stampcache = {}

        initial_covered = self.rq.scenequeue_covered.copy()

        # Mark initial buildable tasks
        for task in xrange(self.stats.total):
            self.runq_running.append(0)
            self.runq_complete.append(0)
            if len(self.rqdata.runq_depends[task]) == 0:
                self.runq_buildable.append(1)
            else:
                self.runq_buildable.append(0)
            if len(self.rqdata.runq_revdeps[task]) > 0 and self.rqdata.runq_revdeps[task].issubset(self.rq.scenequeue_covered):
                self.rq.scenequeue_covered.add(task)

        found = True
        while found:
            found = False
            for task in xrange(self.stats.total):
                if task in self.rq.scenequeue_covered:
                    continue
                logger.debug(1, 'Considering %s (%s): %s' % (task, self.rqdata.get_user_idstring(task), str(self.rqdata.runq_revdeps[task])))

                if len(self.rqdata.runq_revdeps[task]) > 0 and self.rqdata.runq_revdeps[task].issubset(self.rq.scenequeue_covered):
                    found = True
                    self.rq.scenequeue_covered.add(task)

        logger.debug(1, 'Skip list (pre setsceneverify) %s', sorted(self.rq.scenequeue_covered))

        # Allow the metadata to elect for setscene tasks to run anyway
        covered_remove = set()
        if self.rq.setsceneverify:
            invalidtasks = []
            for task in xrange(len(self.rqdata.runq_task)):
                fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
                taskname = self.rqdata.runq_task[task]
                taskdep = self.rqdata.dataCache.task_deps[fn]

                if 'noexec' in taskdep and taskname in taskdep['noexec']:
                    continue
                if self.rq.check_stamp_task(task, taskname + "_setscene", cache=self.stampcache):
                    logger.debug(2, 'Setscene stamp current for task %s(%s)', task, self.rqdata.get_user_idstring(task))
                    continue
                if self.rq.check_stamp_task(task, taskname, recurse = True, cache=self.stampcache):
                    logger.debug(2, 'Normal stamp current for task %s(%s)', task, self.rqdata.get_user_idstring(task))
                    continue
                invalidtasks.append(task)

            call = self.rq.setsceneverify + "(covered, tasknames, fnids, fns, d, invalidtasks=invalidtasks)"
            call2 = self.rq.setsceneverify + "(covered, tasknames, fnids, fns, d)"
            locs = { "covered" : self.rq.scenequeue_covered, "tasknames" : self.rqdata.runq_task, "fnids" : self.rqdata.runq_fnid, "fns" : self.rqdata.taskData.fn_index, "d" : self.cooker.expanded_data, "invalidtasks" : invalidtasks }
            # Backwards compatibility with older versions without invalidtasks
            try:
                covered_remove = bb.utils.better_eval(call, locs)
            except TypeError:
                covered_remove = bb.utils.better_eval(call2, locs)

        def removecoveredtask(task):
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
            taskname = self.rqdata.runq_task[task] + '_setscene'
            bb.build.del_stamp(taskname, self.rqdata.dataCache, fn)
            self.rq.scenequeue_covered.remove(task)

        toremove = covered_remove
        for task in toremove:
            logger.debug(1, 'Not skipping task %s due to setsceneverify', task)
        while toremove:
            covered_remove = []
            for task in toremove:
                removecoveredtask(task)
                for deptask in self.rqdata.runq_depends[task]:
                    if deptask not in self.rq.scenequeue_covered:
                        continue
                    if deptask in toremove or deptask in covered_remove or deptask in initial_covered:
                        continue
                    logger.debug(1, 'Task %s depends on task %s so not skipping' % (task, deptask))
                    covered_remove.append(deptask)
            toremove = covered_remove

        logger.debug(1, 'Full skip list %s', self.rq.scenequeue_covered)

        event.fire(bb.event.StampUpdate(self.rqdata.target_pairs, self.rqdata.dataCache.stamp), self.cfgData)

        schedulers = self.get_schedulers()
        for scheduler in schedulers:
            if self.scheduler == scheduler.name:
                self.sched = scheduler(self, self.rqdata)
                logger.debug(1, "Using runqueue scheduler '%s'", scheduler.name)
                break
        else:
            bb.fatal("Invalid scheduler '%s'.  Available schedulers: %s" %
                     (self.scheduler, ", ".join(obj.name for obj in schedulers)))

    def get_schedulers(self):
        schedulers = set(obj for obj in globals().values()
                             if type(obj) is type and
                                issubclass(obj, RunQueueScheduler))

        user_schedulers = self.cfgData.getVar("BB_SCHEDULERS", True)
        if user_schedulers:
            for sched in user_schedulers.split():
                if not "." in sched:
                    bb.note("Ignoring scheduler '%s' from BB_SCHEDULERS: not an import" % sched)
                    continue

                modname, name = sched.rsplit(".", 1)
                try:
                    module = __import__(modname, fromlist=(name,))
                except ImportError as exc:
                    logger.critical("Unable to import scheduler '%s' from '%s': %s" % (name, modname, exc))
                    raise SystemExit(1)
                else:
                    schedulers.add(getattr(module, name))
        return schedulers

    def setbuildable(self, task):
        self.runq_buildable[task] = 1
        self.sched.newbuilable(task)

    def task_completeoutright(self, task):
        """
        Mark a task as completed
        Look at the reverse dependencies and mark any task with
        completed dependencies as buildable
        """
        self.runq_complete[task] = 1
        for revdep in self.rqdata.runq_revdeps[task]:
            if self.runq_running[revdep] == 1:
                continue
            if self.runq_buildable[revdep] == 1:
                continue
            alldeps = 1
            for dep in self.rqdata.runq_depends[revdep]:
                if self.runq_complete[dep] != 1:
                    alldeps = 0
            if alldeps == 1:
                self.setbuildable(revdep)
                fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[revdep]]
                taskname = self.rqdata.runq_task[revdep]
                logger.debug(1, "Marking task %s (%s, %s) as buildable", revdep, fn, taskname)

    def task_complete(self, task):
        self.stats.taskCompleted()
        bb.event.fire(runQueueTaskCompleted(task, self.stats, self.rq), self.cfgData)
        self.task_completeoutright(task)

    def task_fail(self, task, exitcode):
        """
        Called when a task has failed
        Updates the state engine with the failure
        """
        self.stats.taskFailed()
        fnid = self.rqdata.runq_fnid[task]
        self.failed_fnids.append(fnid)
        bb.event.fire(runQueueTaskFailed(task, self.stats, exitcode, self.rq), self.cfgData)
        if self.rqdata.taskData.abort:
            self.rq.state = runQueueCleanUp

    def task_skip(self, task, reason):
        self.runq_running[task] = 1
        self.setbuildable(task)
        bb.event.fire(runQueueTaskSkipped(task, self.stats, self.rq, reason), self.cfgData)
        self.task_completeoutright(task)
        self.stats.taskCompleted()
        self.stats.taskSkipped()

    def execute(self):
        """
        Run the tasks in a queue prepared by rqdata.prepare()
        """

        self.rq.read_workers()
        

        if self.stats.total == 0:
            # nothing to do
            self.rq.state = runQueueCleanUp

        task = self.sched.next()
        if task is not None:
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]]
            taskname = self.rqdata.runq_task[task]

            if task in self.rq.scenequeue_covered:
                logger.debug(2, "Setscene covered task %s (%s)", task,
                                self.rqdata.get_user_idstring(task))
                self.task_skip(task, "covered")
                return True

            if self.rq.check_stamp_task(task, taskname, cache=self.stampcache):
                logger.debug(2, "Stamp current task %s (%s)", task,
                                self.rqdata.get_user_idstring(task))
                self.task_skip(task, "existing")
                return True

            taskdep = self.rqdata.dataCache.task_deps[fn]
            if 'noexec' in taskdep and taskname in taskdep['noexec']:
                startevent = runQueueTaskStarted(task, self.stats, self.rq,
                                                 noexec=True)
                bb.event.fire(startevent, self.cfgData)
                self.runq_running[task] = 1
                self.stats.taskActive()
                if not self.cooker.configuration.dry_run:
                    bb.build.make_stamp(taskname, self.rqdata.dataCache, fn)
                self.task_complete(task)
                return True
            else:
                startevent = runQueueTaskStarted(task, self.stats, self.rq)
                bb.event.fire(startevent, self.cfgData)

            taskdepdata = self.build_taskdepdata(task)

            taskdep = self.rqdata.dataCache.task_deps[fn]
            if 'fakeroot' in taskdep and taskname in taskdep['fakeroot'] and not self.cooker.configuration.dry_run:
                if not self.rq.fakeworker:
                    try:
                        self.rq.start_fakeworker(self)
                    except OSError as exc:
                        logger.critical("Failed to spawn fakeroot worker to run %s:%s: %s" % (fn, taskname, str(exc)))
                        self.rq.state = runQueueFailed
                        return True
                self.rq.fakeworker.stdin.write("<runtask>" + pickle.dumps((fn, task, taskname, False, self.cooker.collection.get_file_appends(fn), taskdepdata)) + "</runtask>")
                self.rq.fakeworker.stdin.flush()
            else:
                self.rq.worker.stdin.write("<runtask>" + pickle.dumps((fn, task, taskname, False, self.cooker.collection.get_file_appends(fn), taskdepdata)) + "</runtask>")
                self.rq.worker.stdin.flush()

            self.build_stamps[task] = bb.build.stampfile(taskname, self.rqdata.dataCache, fn)
            self.build_stamps2.append(self.build_stamps[task]) 
            self.runq_running[task] = 1
            self.stats.taskActive()
            if self.stats.active < self.number_tasks:
                return True

        if self.stats.active > 0:
            self.rq.read_workers()
            return self.rq.active_fds()

        if len(self.failed_fnids) != 0:
            self.rq.state = runQueueFailed
            return True

        # Sanity Checks
        for task in xrange(self.stats.total):
            if self.runq_buildable[task] == 0:
                logger.error("Task %s never buildable!", task)
            if self.runq_running[task] == 0:
                logger.error("Task %s never ran!", task)
            if self.runq_complete[task] == 0:
                logger.error("Task %s never completed!", task)
        self.rq.state = runQueueComplete

        return True

    def build_taskdepdata(self, task):
        taskdepdata = {}
        next = self.rqdata.runq_depends[task]
        next.add(task)
        while next:
            additional = []
            for revdep in next:
                fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[revdep]]
                pn = self.rqdata.dataCache.pkg_fn[fn]
                taskname = self.rqdata.runq_task[revdep]
                deps = self.rqdata.runq_depends[revdep]
                provides = self.rqdata.dataCache.fn_provides[fn]
                taskdepdata[revdep] = [pn, taskname, fn, deps, provides]
                for revdep2 in deps:
                    if revdep2 not in taskdepdata:
                        additional.append(revdep2)
            next = additional

        #bb.note("Task %s: " % task + str(taskdepdata).replace("], ", "],\n"))
        return taskdepdata

class RunQueueExecuteScenequeue(RunQueueExecute):
    def __init__(self, rq):
        RunQueueExecute.__init__(self, rq)

        self.scenequeue_covered = set()
        self.scenequeue_notcovered = set()
        self.scenequeue_notneeded = set()

        # If we don't have any setscene functions, skip this step
        if len(self.rqdata.runq_setscene) == 0:
            rq.scenequeue_covered = set()
            rq.state = runQueueRunInit
            return

        self.stats = RunQueueStats(len(self.rqdata.runq_setscene))

        sq_revdeps = []
        sq_revdeps_new = []
        sq_revdeps_squash = []
        self.sq_harddeps = {}

        # We need to construct a dependency graph for the setscene functions. Intermediate
        # dependencies between the setscene tasks only complicate the code. This code
        # therefore aims to collapse the huge runqueue dependency tree into a smaller one
        # only containing the setscene functions.

        for task in xrange(self.stats.total):
            self.runq_running.append(0)
            self.runq_complete.append(0)
            self.runq_buildable.append(0)

        # First process the chains up to the first setscene task.
        endpoints = {}
        for task in xrange(len(self.rqdata.runq_fnid)):
            sq_revdeps.append(copy.copy(self.rqdata.runq_revdeps[task]))
            sq_revdeps_new.append(set())
            if (len(self.rqdata.runq_revdeps[task]) == 0) and task not in self.rqdata.runq_setscene:
                endpoints[task] = set()

        # Secondly process the chains between setscene tasks.
        for task in self.rqdata.runq_setscene:
            for dep in self.rqdata.runq_depends[task]:
                    if dep not in endpoints:
                        endpoints[dep] = set()
                    endpoints[dep].add(task)

        def process_endpoints(endpoints):
            newendpoints = {}
            for point, task in endpoints.items():
                tasks = set()
                if task:
                    tasks |= task
                if sq_revdeps_new[point]:
                    tasks |= sq_revdeps_new[point]
                sq_revdeps_new[point] = set()
                if point in self.rqdata.runq_setscene:
                    sq_revdeps_new[point] = tasks
                    tasks = set()
                for dep in self.rqdata.runq_depends[point]:
                    if point in sq_revdeps[dep]:
                        sq_revdeps[dep].remove(point)
                    if tasks:
                        sq_revdeps_new[dep] |= tasks
                    if (len(sq_revdeps[dep]) == 0 or len(sq_revdeps_new[dep]) != 0) and dep not in self.rqdata.runq_setscene:
                        newendpoints[dep] = task
            if len(newendpoints) != 0:
                process_endpoints(newendpoints)

        process_endpoints(endpoints)

        # Build a list of setscene tasks which are "unskippable"
        # These are direct endpoints referenced by the build
        endpoints2 = {}
        sq_revdeps2 = []
        sq_revdeps_new2 = []
        def process_endpoints2(endpoints):
            newendpoints = {}
            for point, task in endpoints.items():
                tasks = set([point])
                if task:
                    tasks |= task
                if sq_revdeps_new2[point]:
                    tasks |= sq_revdeps_new2[point]
                sq_revdeps_new2[point] = set()
                if point in self.rqdata.runq_setscene:
                    sq_revdeps_new2[point] = tasks
                for dep in self.rqdata.runq_depends[point]:
                    if point in sq_revdeps2[dep]:
                        sq_revdeps2[dep].remove(point)
                    if tasks:
                        sq_revdeps_new2[dep] |= tasks
                    if (len(sq_revdeps2[dep]) == 0 or len(sq_revdeps_new2[dep]) != 0) and dep not in self.rqdata.runq_setscene:
                        newendpoints[dep] = tasks
            if len(newendpoints) != 0:
                process_endpoints2(newendpoints)
        for task in xrange(len(self.rqdata.runq_fnid)):
            sq_revdeps2.append(copy.copy(self.rqdata.runq_revdeps[task]))
            sq_revdeps_new2.append(set())
            if (len(self.rqdata.runq_revdeps[task]) == 0) and task not in self.rqdata.runq_setscene:
                endpoints2[task] = set()
        process_endpoints2(endpoints2)
        self.unskippable = []
        for task in self.rqdata.runq_setscene:
            if sq_revdeps_new2[task]:
                self.unskippable.append(self.rqdata.runq_setscene.index(task))

        for task in xrange(len(self.rqdata.runq_fnid)):
            if task in self.rqdata.runq_setscene:
                deps = set()
                for dep in sq_revdeps_new[task]:
                    deps.add(self.rqdata.runq_setscene.index(dep))
                sq_revdeps_squash.append(deps)
            elif len(sq_revdeps_new[task]) != 0:
                bb.msg.fatal("RunQueue", "Something went badly wrong during scenequeue generation, aborting. Please report this problem.")

        # Resolve setscene inter-task dependencies
        # e.g. do_sometask_setscene[depends] = "targetname:do_someothertask_setscene"
        # Note that anything explicitly depended upon will have its reverse dependencies removed to avoid circular dependencies
        for task in self.rqdata.runq_setscene:
                realid = self.rqdata.taskData.gettask_id(self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[task]], self.rqdata.runq_task[task] + "_setscene", False)
                idepends = self.rqdata.taskData.tasks_idepends[realid]
                for (depid, idependtask) in idepends:
                    if depid not in self.rqdata.taskData.build_targets:
                        continue

                    depdata = self.rqdata.taskData.build_targets[depid][0]
                    if depdata is None:
                         continue
                    dep = self.rqdata.taskData.fn_index[depdata]
                    taskid = self.rqdata.get_task_id(self.rqdata.taskData.getfn_id(dep), idependtask.replace("_setscene", ""))
                    if taskid is None:
                        bb.msg.fatal("RunQueue", "Task %s_setscene depends upon non-existent task %s:%s" % (self.rqdata.get_user_idstring(task), dep, idependtask))

                    if not self.rqdata.runq_setscene.index(taskid) in self.sq_harddeps:
                        self.sq_harddeps[self.rqdata.runq_setscene.index(taskid)] = set()
                    self.sq_harddeps[self.rqdata.runq_setscene.index(taskid)].add(self.rqdata.runq_setscene.index(task))

                    sq_revdeps_squash[self.rqdata.runq_setscene.index(task)].add(self.rqdata.runq_setscene.index(taskid))
                    # Have to zero this to avoid circular dependencies
                    sq_revdeps_squash[self.rqdata.runq_setscene.index(taskid)] = set()

        for task in self.sq_harddeps:
             for dep in self.sq_harddeps[task]:
                 sq_revdeps_squash[dep].add(task)

        #for task in xrange(len(sq_revdeps_squash)):
        #    realtask = self.rqdata.runq_setscene[task]
        #    bb.warn("Task %s: %s_setscene is %s " % (task, self.rqdata.get_user_idstring(realtask) , sq_revdeps_squash[task]))

        self.sq_deps = []
        self.sq_revdeps = sq_revdeps_squash
        self.sq_revdeps2 = copy.deepcopy(self.sq_revdeps)

        for task in xrange(len(self.sq_revdeps)):
            self.sq_deps.append(set())
        for task in xrange(len(self.sq_revdeps)):
            for dep in self.sq_revdeps[task]:
                self.sq_deps[dep].add(task)

        for task in xrange(len(self.sq_revdeps)):
            if len(self.sq_revdeps[task]) == 0:
                self.runq_buildable[task] = 1

        self.outrightfail = []
        if self.rq.hashvalidate:
            sq_hash = []
            sq_hashfn = []
            sq_fn = []
            sq_taskname = []
            sq_task = []
            noexec = []
            stamppresent = []
            for task in xrange(len(self.sq_revdeps)):
                realtask = self.rqdata.runq_setscene[task]
                fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[realtask]]
                taskname = self.rqdata.runq_task[realtask]
                taskdep = self.rqdata.dataCache.task_deps[fn]

                if 'noexec' in taskdep and taskname in taskdep['noexec']:
                    noexec.append(task)
                    self.task_skip(task)
                    bb.build.make_stamp(taskname + "_setscene", self.rqdata.dataCache, fn)
                    continue

                if self.rq.check_stamp_task(realtask, taskname + "_setscene", cache=self.stampcache):
                    logger.debug(2, 'Setscene stamp current for task %s(%s)', task, self.rqdata.get_user_idstring(realtask))
                    stamppresent.append(task)
                    self.task_skip(task)
                    continue

                if self.rq.check_stamp_task(realtask, taskname, recurse = True, cache=self.stampcache):
                    logger.debug(2, 'Normal stamp current for task %s(%s)', task, self.rqdata.get_user_idstring(realtask))
                    stamppresent.append(task)
                    self.task_skip(task)
                    continue

                sq_fn.append(fn)
                sq_hashfn.append(self.rqdata.dataCache.hashfn[fn])
                sq_hash.append(self.rqdata.runq_hash[realtask])
                sq_taskname.append(taskname)
                sq_task.append(task)
            call = self.rq.hashvalidate + "(sq_fn, sq_task, sq_hash, sq_hashfn, d)"
            locs = { "sq_fn" : sq_fn, "sq_task" : sq_taskname, "sq_hash" : sq_hash, "sq_hashfn" : sq_hashfn, "d" : self.cooker.expanded_data }
            valid = bb.utils.better_eval(call, locs)

            valid_new = stamppresent
            for v in valid:
                valid_new.append(sq_task[v])

            for task in xrange(len(self.sq_revdeps)):
                if task not in valid_new and task not in noexec:
                    realtask = self.rqdata.runq_setscene[task]
                    logger.debug(2, 'No package found, so skipping setscene task %s',
                                 self.rqdata.get_user_idstring(realtask))
                    self.outrightfail.append(task)

        logger.info('Executing SetScene Tasks')

        self.rq.state = runQueueSceneRun

    def scenequeue_updatecounters(self, task, fail = False):
        for dep in self.sq_deps[task]:
            if fail and task in self.sq_harddeps and dep in self.sq_harddeps[task]:
                realtask = self.rqdata.runq_setscene[task]
                realdep = self.rqdata.runq_setscene[dep]
                logger.debug(2, "%s was unavailable and is a hard dependency of %s so skipping" % (self.rqdata.get_user_idstring(realtask), self.rqdata.get_user_idstring(realdep)))
                self.scenequeue_updatecounters(dep, fail)
                continue
            if task not in self.sq_revdeps2[dep]:
                # May already have been removed by the fail case above
                continue
            self.sq_revdeps2[dep].remove(task)
            if len(self.sq_revdeps2[dep]) == 0:
                self.runq_buildable[dep] = 1

    def task_completeoutright(self, task):
        """
        Mark a task as completed
        Look at the reverse dependencies and mark any task with
        completed dependencies as buildable
        """

        index = self.rqdata.runq_setscene[task]
        logger.debug(1, 'Found task %s which could be accelerated',
                        self.rqdata.get_user_idstring(index))

        self.scenequeue_covered.add(task)
        self.scenequeue_updatecounters(task)

    def task_complete(self, task):
        self.stats.taskCompleted()
        bb.event.fire(sceneQueueTaskCompleted(task, self.stats, self.rq), self.cfgData)
        self.task_completeoutright(task)

    def task_fail(self, task, result):
        self.stats.taskFailed()
        bb.event.fire(sceneQueueTaskFailed(task, self.stats, result, self), self.cfgData)
        self.scenequeue_notcovered.add(task)
        self.scenequeue_updatecounters(task, True)

    def task_failoutright(self, task):
        self.runq_running[task] = 1
        self.runq_buildable[task] = 1
        self.stats.taskCompleted()
        self.stats.taskSkipped()
        index = self.rqdata.runq_setscene[task]
        self.scenequeue_notcovered.add(task)
        self.scenequeue_updatecounters(task, True)

    def task_skip(self, task):
        self.runq_running[task] = 1
        self.runq_buildable[task] = 1
        self.task_completeoutright(task)
        self.stats.taskCompleted()
        self.stats.taskSkipped()

    def execute(self):
        """
        Run the tasks in a queue prepared by prepare_runqueue
        """

        self.rq.read_workers()

        task = None
        if self.stats.active < self.number_tasks:
            # Find the next setscene to run
            for nexttask in xrange(self.stats.total):
                if self.runq_buildable[nexttask] == 1 and self.runq_running[nexttask] != 1:
                    if nexttask in self.unskippable:
                        logger.debug(2, "Setscene task %s is unskippable" % self.rqdata.get_user_idstring(self.rqdata.runq_setscene[nexttask]))                      
                    if nexttask not in self.unskippable and len(self.sq_revdeps[nexttask]) > 0 and self.sq_revdeps[nexttask].issubset(self.scenequeue_covered) and self.check_dependencies(nexttask, self.sq_revdeps[nexttask], True):
                        realtask = self.rqdata.runq_setscene[nexttask]
                        fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[realtask]]
                        foundtarget = False
                        for target in self.rqdata.target_pairs:
                            if target[0] == fn and target[1] == self.rqdata.runq_task[realtask]:
                                foundtarget = True
                                break
                        if not foundtarget:
                            logger.debug(2, "Skipping setscene for task %s" % self.rqdata.get_user_idstring(self.rqdata.runq_setscene[nexttask]))
                            self.task_skip(nexttask)
                            self.scenequeue_notneeded.add(nexttask)
                            return True
                    if nexttask in self.outrightfail:
                        self.task_failoutright(nexttask)
                        return True
                    task = nexttask
                    break
        if task is not None:
            realtask = self.rqdata.runq_setscene[task]
            fn = self.rqdata.taskData.fn_index[self.rqdata.runq_fnid[realtask]]

            taskname = self.rqdata.runq_task[realtask] + "_setscene"
            if self.rq.check_stamp_task(realtask, self.rqdata.runq_task[realtask], recurse = True, cache=self.stampcache):
                logger.debug(2, 'Stamp for underlying task %s(%s) is current, so skipping setscene variant',
                             task, self.rqdata.get_user_idstring(realtask))
                self.task_failoutright(task)
                return True

            if self.cooker.configuration.force:
                for target in self.rqdata.target_pairs:
                    if target[0] == fn and target[1] == self.rqdata.runq_task[realtask]:
                        self.task_failoutright(task)
                        return True

            if self.rq.check_stamp_task(realtask, taskname, cache=self.stampcache):
                logger.debug(2, 'Setscene stamp current task %s(%s), so skip it and its dependencies',
                             task, self.rqdata.get_user_idstring(realtask))
                self.task_skip(task)
                return True

            startevent = sceneQueueTaskStarted(task, self.stats, self.rq)
            bb.event.fire(startevent, self.cfgData)

            taskdep = self.rqdata.dataCache.task_deps[fn]
            if 'fakeroot' in taskdep and taskname in taskdep['fakeroot']:
                if not self.rq.fakeworker:
                    self.rq.start_fakeworker(self)
                self.rq.fakeworker.stdin.write("<runtask>" + pickle.dumps((fn, realtask, taskname, True, self.cooker.collection.get_file_appends(fn), None)) + "</runtask>")
                self.rq.fakeworker.stdin.flush()
            else:
                self.rq.worker.stdin.write("<runtask>" + pickle.dumps((fn, realtask, taskname, True, self.cooker.collection.get_file_appends(fn), None)) + "</runtask>")
                self.rq.worker.stdin.flush()

            self.runq_running[task] = 1
            self.stats.taskActive()
            if self.stats.active < self.number_tasks:
                return True

        if self.stats.active > 0:
            self.rq.read_workers()
            return self.rq.active_fds()

        #for task in xrange(self.stats.total):
        #    if self.runq_running[task] != 1:
        #        buildable = self.runq_buildable[task]
        #        revdeps = self.sq_revdeps[task]
        #        bb.warn("Found we didn't run %s %s %s %s" % (task, buildable, str(revdeps), self.rqdata.get_user_idstring(self.rqdata.runq_setscene[task])))

        # Convert scenequeue_covered task numbers into full taskgraph ids
        oldcovered = self.scenequeue_covered
        self.rq.scenequeue_covered = set()
        for task in oldcovered:
            self.rq.scenequeue_covered.add(self.rqdata.runq_setscene[task])

        logger.debug(1, 'We can skip tasks %s', sorted(self.rq.scenequeue_covered))

        self.rq.state = runQueueRunInit

        completeevent = sceneQueueComplete(self.stats, self.rq)
        bb.event.fire(completeevent, self.cfgData)

        return True

    def runqueue_process_waitpid(self, task, status):
        task = self.rq.rqdata.runq_setscene.index(task)

        RunQueueExecute.runqueue_process_waitpid(self, task, status)

class TaskFailure(Exception):
    """
    Exception raised when a task in a runqueue fails
    """
    def __init__(self, x):
        self.args = x


class runQueueExitWait(bb.event.Event):
    """
    Event when waiting for task processes to exit
    """

    def __init__(self, remain):
        self.remain = remain
        self.message = "Waiting for %s active tasks to finish" % remain
        bb.event.Event.__init__(self)

class runQueueEvent(bb.event.Event):
    """
    Base runQueue event class
    """
    def __init__(self, task, stats, rq):
        self.taskid = task
        self.taskstring = rq.rqdata.get_user_idstring(task)
        self.taskname = rq.rqdata.get_task_name(task)
        self.taskfile = rq.rqdata.get_task_file(task)
        self.taskhash = rq.rqdata.get_task_hash(task)
        self.stats = stats.copy()
        bb.event.Event.__init__(self)

class sceneQueueEvent(runQueueEvent):
    """
    Base sceneQueue event class
    """
    def __init__(self, task, stats, rq, noexec=False):
        runQueueEvent.__init__(self, task, stats, rq)
        realtask = rq.rqdata.runq_setscene[task]
        self.taskstring = rq.rqdata.get_user_idstring(realtask, "_setscene")
        self.taskname = rq.rqdata.get_task_name(realtask) + "_setscene"
        self.taskfile = rq.rqdata.get_task_file(realtask)
        self.taskhash = rq.rqdata.get_task_hash(realtask)

class runQueueTaskStarted(runQueueEvent):
    """
    Event notifying a task was started
    """
    def __init__(self, task, stats, rq, noexec=False):
        runQueueEvent.__init__(self, task, stats, rq)
        self.noexec = noexec

class sceneQueueTaskStarted(sceneQueueEvent):
    """
    Event notifying a setscene task was started
    """
    def __init__(self, task, stats, rq, noexec=False):
        sceneQueueEvent.__init__(self, task, stats, rq)
        self.noexec = noexec

class runQueueTaskFailed(runQueueEvent):
    """
    Event notifying a task failed
    """
    def __init__(self, task, stats, exitcode, rq):
        runQueueEvent.__init__(self, task, stats, rq)
        self.exitcode = exitcode

class sceneQueueTaskFailed(sceneQueueEvent):
    """
    Event notifying a setscene task failed
    """
    def __init__(self, task, stats, exitcode, rq):
        sceneQueueEvent.__init__(self, task, stats, rq)
        self.exitcode = exitcode

class sceneQueueComplete(sceneQueueEvent):
    """
    Event when all the sceneQueue tasks are complete
    """
    def __init__(self, stats, rq):
        self.stats = stats.copy()
        bb.event.Event.__init__(self)

class runQueueTaskCompleted(runQueueEvent):
    """
    Event notifying a task completed
    """

class sceneQueueTaskCompleted(sceneQueueEvent):
    """
    Event notifying a setscene task completed
    """

class runQueueTaskSkipped(runQueueEvent):
    """
    Event notifying a task was skipped
    """
    def __init__(self, task, stats, rq, reason):
        runQueueEvent.__init__(self, task, stats, rq)
        self.reason = reason

class runQueuePipe():
    """
    Abstraction for a pipe between a worker thread and the server
    """
    def __init__(self, pipein, pipeout, d, rq, rqexec):
        self.input = pipein
        if pipeout:
            pipeout.close()
        bb.utils.nonblockingfd(self.input)
        self.queue = ""
        self.d = d
        self.rq = rq
        self.rqexec = rqexec

    def setrunqueueexec(self, rqexec):
        self.rqexec = rqexec

    def read(self):
        for w in [self.rq.worker, self.rq.fakeworker]:
            if not w:
                continue
            w.poll()
            if w.returncode is not None and not self.rq.teardown:
                name = None
                if self.rq.worker and w.pid == self.rq.worker.pid:
                    name = "Worker"
                elif self.rq.fakeworker and w.pid == self.rq.fakeworker.pid:
                    name = "Fakeroot"
                bb.error("%s process (%s) exited unexpectedly (%s), shutting down..." % (name, w.pid, str(w.returncode)))
                self.rq.finish_runqueue(True)

        start = len(self.queue)
        try:
            self.queue = self.queue + self.input.read(102400)
        except (OSError, IOError) as e:
            if e.errno != errno.EAGAIN:
                raise
        end = len(self.queue)
        found = True
        while found and len(self.queue):
            found = False
            index = self.queue.find("</event>")
            while index != -1 and self.queue.startswith("<event>"):
                try:
                    event = pickle.loads(self.queue[7:index])
                except ValueError as e:
                    bb.msg.fatal("RunQueue", "failed load pickle '%s': '%s'" % (e, self.queue[7:index]))
                bb.event.fire_from_worker(event, self.d)
                found = True
                self.queue = self.queue[index+8:]
                index = self.queue.find("</event>")
            index = self.queue.find("</exitcode>")
            while index != -1 and self.queue.startswith("<exitcode>"):
                try:
                    task, status = pickle.loads(self.queue[10:index])
                except ValueError as e:
                    bb.msg.fatal("RunQueue", "failed load pickle '%s': '%s'" % (e, self.queue[10:index]))
                self.rqexec.runqueue_process_waitpid(task, status)
                found = True
                self.queue = self.queue[index+11:]
                index = self.queue.find("</exitcode>")
        return (end > start)

    def close(self):
        while self.read():
            continue
        if len(self.queue) > 0:
            print("Warning, worker left partial message: %s" % self.queue)
        self.input.close()
