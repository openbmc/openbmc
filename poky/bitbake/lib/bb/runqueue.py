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
import pickle
from multiprocessing import Process

bblogger = logging.getLogger("BitBake")
logger = logging.getLogger("BitBake.RunQueue")

__find_md5__ = re.compile( r'(?i)(?<![a-z0-9])[a-f0-9]{32}(?![a-z0-9])' )

def fn_from_tid(tid):
     return tid.rsplit(":", 1)[0]

def taskname_from_tid(tid):
    return tid.rsplit(":", 1)[1]

def split_tid(tid):
    (mc, fn, taskname, _) = split_tid_mcfn(tid)
    return (mc, fn, taskname)

def split_tid_mcfn(tid):
    if tid.startswith('multiconfig:'):
        elems = tid.split(':')
        mc = elems[1]
        fn = ":".join(elems[2:-1])
        taskname = elems[-1]
        mcfn = "multiconfig:" + mc + ":" + fn
    else:
        tid = tid.rsplit(":", 1)
        mc = ""
        fn = tid[0]
        taskname = tid[1]
        mcfn = fn

    return (mc, fn, taskname, mcfn)

def build_tid(mc, fn, taskname):
    if mc:
        return "multiconfig:" + mc + ":" + fn + ":" + taskname
    return fn + ":" + taskname

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
        self.numTasks = len(self.rqdata.runtaskentries)

        self.prio_map = [self.rqdata.runtaskentries.keys()]

        self.buildable = []
        self.stamps = {}
        for tid in self.rqdata.runtaskentries:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            self.stamps[tid] = bb.build.stampfile(taskname, self.rqdata.dataCaches[mc], taskfn, noextra=True)
            if tid in self.rq.runq_buildable:
                self.buildable.append(tid)

        self.rev_prio_map = None

    def next_buildable_task(self):
        """
        Return the id of the first task we find that is buildable
        """
        self.buildable = [x for x in self.buildable if x not in self.rq.runq_running]
        if not self.buildable:
            return None
        if len(self.buildable) == 1:
            tid = self.buildable[0]
            stamp = self.stamps[tid]
            if stamp not in self.rq.build_stamps.values():
                return tid

        if not self.rev_prio_map:
            self.rev_prio_map = {}
            for tid in self.rqdata.runtaskentries:
                self.rev_prio_map[tid] = self.prio_map.index(tid)

        best = None
        bestprio = None
        for tid in self.buildable:
            prio = self.rev_prio_map[tid]
            if bestprio is None or bestprio > prio:
                stamp = self.stamps[tid]
                if stamp in self.rq.build_stamps.values():
                    continue
                bestprio = prio
                best = tid

        return best

    def next(self):
        """
        Return the id of the task we should build next
        """
        if self.rq.stats.active < self.rq.number_tasks:
            return self.next_buildable_task()

    def newbuildable(self, task):
        self.buildable.append(task)

    def describe_task(self, taskid):
        result = 'ID %s' % taskid
        if self.rev_prio_map:
            result = result + (' pri %d' % self.rev_prio_map[taskid])
        return result

    def dump_prio(self, comment):
        bb.debug(3, '%s (most important first):\n%s' %
                 (comment,
                  '\n'.join(['%d. %s' % (index + 1, self.describe_task(taskid)) for
                             index, taskid in enumerate(self.prio_map)])))

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

        weights = {}
        for tid in self.rqdata.runtaskentries:
            weight = self.rqdata.runtaskentries[tid].weight
            if not weight in weights:
                weights[weight] = []
            weights[weight].append(tid)

        self.prio_map = []
        for weight in sorted(weights):
            for w in weights[weight]:
                self.prio_map.append(w)

        self.prio_map.reverse()

class RunQueueSchedulerCompletion(RunQueueSchedulerSpeed):
    """
    A scheduler optimised to complete .bb files as quickly as possible. The
    priority map is sorted by task weight, but then reordered so once a given
    .bb file starts to build, it's completed as quickly as possible by
    running all tasks related to the same .bb file one after the after.
    This works well where disk space is at a premium and classes like OE's
    rm_work are in force.
    """
    name = "completion"

    def __init__(self, runqueue, rqdata):
        super(RunQueueSchedulerCompletion, self).__init__(runqueue, rqdata)

        # Extract list of tasks for each recipe, with tasks sorted
        # ascending from "must run first" (typically do_fetch) to
        # "runs last" (do_build). The speed scheduler prioritizes
        # tasks that must run first before the ones that run later;
        # this is what we depend on here.
        task_lists = {}
        for taskid in self.prio_map:
            fn, taskname = taskid.rsplit(':', 1)
            task_lists.setdefault(fn, []).append(taskname)

        # Now unify the different task lists. The strategy is that
        # common tasks get skipped and new ones get inserted after the
        # preceeding common one(s) as they are found. Because task
        # lists should differ only by their number of tasks, but not
        # the ordering of the common tasks, this should result in a
        # deterministic result that is a superset of the individual
        # task ordering.
        all_tasks = []
        for recipe, new_tasks in task_lists.items():
            index = 0
            old_task = all_tasks[index] if index < len(all_tasks) else None
            for new_task in new_tasks:
                if old_task == new_task:
                    # Common task, skip it. This is the fast-path which
                    # avoids a full search.
                    index += 1
                    old_task = all_tasks[index] if index < len(all_tasks) else None
                else:
                    try:
                        index = all_tasks.index(new_task)
                        # Already present, just not at the current
                        # place. We re-synchronized by changing the
                        # index so that it matches again. Now
                        # move on to the next existing task.
                        index += 1
                        old_task = all_tasks[index] if index < len(all_tasks) else None
                    except ValueError:
                        # Not present. Insert before old_task, which
                        # remains the same (but gets shifted back).
                        all_tasks.insert(index, new_task)
                        index += 1
        bb.debug(3, 'merged task list: %s'  % all_tasks)

        # Now reverse the order so that tasks that finish the work on one
        # recipe are considered more imporant (= come first). The ordering
        # is now so that do_build is most important.
        all_tasks.reverse()

        # Group tasks of the same kind before tasks of less important
        # kinds at the head of the queue (because earlier = lower
        # priority number = runs earlier), while preserving the
        # ordering by recipe. If recipe foo is more important than
        # bar, then the goal is to work on foo's do_populate_sysroot
        # before bar's do_populate_sysroot and on the more important
        # tasks of foo before any of the less important tasks in any
        # other recipe (if those other recipes are more important than
        # foo).
        #
        # All of this only applies when tasks are runable. Explicit
        # dependencies still override this ordering by priority.
        #
        # Here's an example why this priority re-ordering helps with
        # minimizing disk usage. Consider a recipe foo with a higher
        # priority than bar where foo DEPENDS on bar. Then the
        # implicit rule (from base.bbclass) is that foo's do_configure
        # depends on bar's do_populate_sysroot. This ensures that
        # bar's do_populate_sysroot gets done first. Normally the
        # tasks from foo would continue to run once that is done, and
        # bar only gets completed and cleaned up later. By ordering
        # bar's task that depend on bar's do_populate_sysroot before foo's
        # do_configure, that problem gets avoided.
        task_index = 0
        self.dump_prio('original priorities')
        for task in all_tasks:
            for index in range(task_index, self.numTasks):
                taskid = self.prio_map[index]
                taskname = taskid.rsplit(':', 1)[1]
                if taskname == task:
                    del self.prio_map[index]
                    self.prio_map.insert(task_index, taskid)
                    task_index += 1
        self.dump_prio('completion priorities')

class RunTaskEntry(object):
    def __init__(self):
        self.depends = set()
        self.revdeps = set()
        self.hash = None
        self.task = None
        self.weight = 1

class RunQueueData:
    """
    BitBake Run Queue implementation
    """
    def __init__(self, rq, cooker, cfgData, dataCaches, taskData, targets):
        self.cooker = cooker
        self.dataCaches = dataCaches
        self.taskData = taskData
        self.targets = targets
        self.rq = rq
        self.warn_multi_bb = False

        self.stampwhitelist = cfgData.getVar("BB_STAMP_WHITELIST") or ""
        self.multi_provider_whitelist = (cfgData.getVar("MULTI_PROVIDER_WHITELIST") or "").split()
        self.setscenewhitelist = get_setscene_enforce_whitelist(cfgData)
        self.setscenewhitelist_checked = False
        self.setscene_enforce = (cfgData.getVar('BB_SETSCENE_ENFORCE') == "1")
        self.init_progress_reporter = bb.progress.DummyMultiStageProcessProgressReporter()

        self.reset()

    def reset(self):
        self.runtaskentries = {}

    def runq_depends_names(self, ids):
        import re
        ret = []
        for id in ids:
            nam = os.path.basename(id)
            nam = re.sub("_[^,]*,", ",", nam)
            ret.extend([nam])
        return ret

    def get_task_hash(self, tid):
        return self.runtaskentries[tid].hash

    def get_user_idstring(self, tid, task_name_suffix = ""):
        return tid + task_name_suffix

    def get_short_user_idstring(self, task, task_name_suffix = ""):
        (mc, fn, taskname, taskfn) = split_tid_mcfn(task)
        pn = self.dataCaches[mc].pkg_fn[taskfn]
        taskname = taskname_from_tid(task) + task_name_suffix
        return "%s:%s" % (pn, taskname)

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
            for entry in range(len(chain)):
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
            for index in range(len(chain1)):
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

        def find_chains(tid, prev_chain):
            prev_chain.append(tid)
            total_deps = []
            total_deps.extend(self.runtaskentries[tid].revdeps)
            for revdep in self.runtaskentries[tid].revdeps:
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
                            msgs.append("  Task %s (dependent Tasks %s)\n" % (dep, self.runq_depends_names(self.runtaskentries[dep].depends)))
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

            explored_deps[tid] = total_deps

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

        numTasks = len(self.runtaskentries)
        weight = {}
        deps_left = {}
        task_done = {}

        for tid in self.runtaskentries:
            task_done[tid] = False
            weight[tid] = 1
            deps_left[tid] = len(self.runtaskentries[tid].revdeps)

        for tid in endpoints:
            weight[tid] = 10
            task_done[tid] = True

        while True:
            next_points = []
            for tid in endpoints:
                for revdep in self.runtaskentries[tid].depends:
                    weight[revdep] = weight[revdep] + weight[tid]
                    deps_left[revdep] = deps_left[revdep] - 1
                    if deps_left[revdep] == 0:
                        next_points.append(revdep)
                        task_done[revdep] = True
            endpoints = next_points
            if len(next_points) == 0:
                break

        # Circular dependency sanity check
        problem_tasks = []
        for tid in self.runtaskentries:
            if task_done[tid] is False or deps_left[tid] != 0:
                problem_tasks.append(tid)
                logger.debug(2, "Task %s is not buildable", tid)
                logger.debug(2, "(Complete marker was %s and the remaining dependency count was %s)\n", task_done[tid], deps_left[tid])
            self.runtaskentries[tid].weight = weight[tid]

        if problem_tasks:
            message = "%s unbuildable tasks were found.\n" % len(problem_tasks)
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

        runq_build = {}
        recursivetasks = {}
        recursiveitasks = {}
        recursivetasksselfref = set()

        taskData = self.taskData

        found = False
        for mc in self.taskData:
            if len(taskData[mc].taskentries) > 0:
                found = True
                break
        if not found:
            # Nothing to do
            return 0

        self.init_progress_reporter.start()
        self.init_progress_reporter.next_stage()

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

        def add_build_dependencies(depids, tasknames, depends, mc):
            for depname in depids:
                # Won't be in build_targets if ASSUME_PROVIDED
                if depname not in taskData[mc].build_targets or not taskData[mc].build_targets[depname]:
                    continue
                depdata = taskData[mc].build_targets[depname][0]
                if depdata is None:
                    continue
                for taskname in tasknames:
                    t = depdata + ":" + taskname
                    if t in taskData[mc].taskentries:
                        depends.add(t)

        def add_runtime_dependencies(depids, tasknames, depends, mc):
            for depname in depids:
                if depname not in taskData[mc].run_targets or not taskData[mc].run_targets[depname]:
                    continue
                depdata = taskData[mc].run_targets[depname][0]
                if depdata is None:
                    continue
                for taskname in tasknames:
                    t = depdata + ":" + taskname
                    if t in taskData[mc].taskentries:
                        depends.add(t)

        for mc in taskData:
            for tid in taskData[mc].taskentries:

                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                #runtid = build_tid(mc, fn, taskname)

                #logger.debug(2, "Processing %s,%s:%s", mc, fn, taskname)

                depends = set()
                task_deps = self.dataCaches[mc].task_deps[taskfn]

                self.runtaskentries[tid] = RunTaskEntry()

                if fn in taskData[mc].failed_fns:
                    continue

                # Resolve task internal dependencies
                #
                # e.g. addtask before X after Y
                for t in taskData[mc].taskentries[tid].tdepends:
                    (_, depfn, deptaskname, _) = split_tid_mcfn(t)
                    depends.add(build_tid(mc, depfn, deptaskname))

                # Resolve 'deptask' dependencies
                #
                # e.g. do_sometask[deptask] = "do_someothertask"
                # (makes sure sometask runs after someothertask of all DEPENDS)
                if 'deptask' in task_deps and taskname in task_deps['deptask']:
                    tasknames = task_deps['deptask'][taskname].split()
                    add_build_dependencies(taskData[mc].depids[taskfn], tasknames, depends, mc)

                # Resolve 'rdeptask' dependencies
                #
                # e.g. do_sometask[rdeptask] = "do_someothertask"
                # (makes sure sometask runs after someothertask of all RDEPENDS)
                if 'rdeptask' in task_deps and taskname in task_deps['rdeptask']:
                    tasknames = task_deps['rdeptask'][taskname].split()
                    add_runtime_dependencies(taskData[mc].rdepids[taskfn], tasknames, depends, mc)

                # Resolve inter-task dependencies
                #
                # e.g. do_sometask[depends] = "targetname:do_someothertask"
                # (makes sure sometask runs after targetname's someothertask)
                idepends = taskData[mc].taskentries[tid].idepends
                for (depname, idependtask) in idepends:
                    if depname in taskData[mc].build_targets and taskData[mc].build_targets[depname] and not depname in taskData[mc].failed_deps:
                        # Won't be in build_targets if ASSUME_PROVIDED
                        depdata = taskData[mc].build_targets[depname][0]
                        if depdata is not None:
                            t = depdata + ":" + idependtask
                            depends.add(t)
                            if t not in taskData[mc].taskentries:
                                bb.msg.fatal("RunQueue", "Task %s in %s depends upon non-existent task %s in %s" % (taskname, fn, idependtask, depdata))
                irdepends = taskData[mc].taskentries[tid].irdepends
                for (depname, idependtask) in irdepends:
                    if depname in taskData[mc].run_targets:
                        # Won't be in run_targets if ASSUME_PROVIDED
                        if not taskData[mc].run_targets[depname]:
                            continue
                        depdata = taskData[mc].run_targets[depname][0]
                        if depdata is not None:
                            t = depdata + ":" + idependtask
                            depends.add(t)
                            if t not in taskData[mc].taskentries:
                                bb.msg.fatal("RunQueue", "Task %s in %s rdepends upon non-existent task %s in %s" % (taskname, fn, idependtask, depdata))

                # Resolve recursive 'recrdeptask' dependencies (Part A)
                #
                # e.g. do_sometask[recrdeptask] = "do_someothertask"
                # (makes sure sometask runs after someothertask of all DEPENDS, RDEPENDS and intertask dependencies, recursively)
                # We cover the recursive part of the dependencies below
                if 'recrdeptask' in task_deps and taskname in task_deps['recrdeptask']:
                    tasknames = task_deps['recrdeptask'][taskname].split()
                    recursivetasks[tid] = tasknames
                    add_build_dependencies(taskData[mc].depids[taskfn], tasknames, depends, mc)
                    add_runtime_dependencies(taskData[mc].rdepids[taskfn], tasknames, depends, mc)
                    if taskname in tasknames:
                        recursivetasksselfref.add(tid)

                    if 'recideptask' in task_deps and taskname in task_deps['recideptask']:
                        recursiveitasks[tid] = []
                        for t in task_deps['recideptask'][taskname].split():
                            newdep = build_tid(mc, fn, t)
                            recursiveitasks[tid].append(newdep)

                self.runtaskentries[tid].depends = depends
                # Remove all self references
                self.runtaskentries[tid].depends.discard(tid)

        #self.dump_data()

        self.init_progress_reporter.next_stage()

        # Resolve recursive 'recrdeptask' dependencies (Part B)
        #
        # e.g. do_sometask[recrdeptask] = "do_someothertask"
        # (makes sure sometask runs after someothertask of all DEPENDS, RDEPENDS and intertask dependencies, recursively)
        # We need to do this separately since we need all of runtaskentries[*].depends to be complete before this is processed

        # Generating/interating recursive lists of dependencies is painful and potentially slow
        # Precompute recursive task dependencies here by:
        #     a) create a temp list of reverse dependencies (revdeps)
        #     b) walk up the ends of the chains (when a given task no longer has dependencies i.e. len(deps) == 0)
        #     c) combine the total list of dependencies in cumulativedeps
        #     d) optimise by pre-truncating 'task' off the items in cumulativedeps (keeps items in sets lower)


        revdeps = {}
        deps = {}
        cumulativedeps = {}
        for tid in self.runtaskentries:
            deps[tid] = set(self.runtaskentries[tid].depends)
            revdeps[tid] = set()
            cumulativedeps[tid] = set()
        # Generate a temp list of reverse dependencies
        for tid in self.runtaskentries:
            for dep in self.runtaskentries[tid].depends:
                revdeps[dep].add(tid)
        # Find the dependency chain endpoints
        endpoints = set()
        for tid in self.runtaskentries:
            if len(deps[tid]) == 0:
                endpoints.add(tid)
        # Iterate the chains collating dependencies
        while endpoints:
            next = set()
            for tid in endpoints:
                for dep in revdeps[tid]:
                    cumulativedeps[dep].add(fn_from_tid(tid))
                    cumulativedeps[dep].update(cumulativedeps[tid])
                    if tid in deps[dep]:
                        deps[dep].remove(tid)
                    if len(deps[dep]) == 0:
                        next.add(dep)
            endpoints = next
        #for tid in deps:
        #    if len(deps[tid]) != 0:
        #        bb.warn("Sanity test failure, dependencies left for %s (%s)" % (tid, deps[tid]))

        # Loop here since recrdeptasks can depend upon other recrdeptasks and we have to
        # resolve these recursively until we aren't adding any further extra dependencies
        extradeps = True
        while extradeps:
            extradeps = 0
            for tid in recursivetasks:
                tasknames = recursivetasks[tid]

                totaldeps = set(self.runtaskentries[tid].depends)
                if tid in recursiveitasks:
                    totaldeps.update(recursiveitasks[tid])
                    for dep in recursiveitasks[tid]:
                        if dep not in self.runtaskentries:
                            continue
                        totaldeps.update(self.runtaskentries[dep].depends)

                deps = set()
                for dep in totaldeps:
                    if dep in cumulativedeps:
                        deps.update(cumulativedeps[dep])

                for t in deps:
                    for taskname in tasknames:
                        newtid = t + ":" + taskname
                        if newtid == tid:
                            continue
                        if newtid in self.runtaskentries and newtid not in self.runtaskentries[tid].depends:
                            extradeps += 1
                            self.runtaskentries[tid].depends.add(newtid)

                # Handle recursive tasks which depend upon other recursive tasks
                deps = set()
                for dep in self.runtaskentries[tid].depends.intersection(recursivetasks):
                    deps.update(self.runtaskentries[dep].depends.difference(self.runtaskentries[tid].depends))
                for newtid in deps:
                    for taskname in tasknames:
                        if not newtid.endswith(":" + taskname):
                            continue
                        if newtid in self.runtaskentries:
                            extradeps += 1
                            self.runtaskentries[tid].depends.add(newtid)

            bb.debug(1, "Added %s recursive dependencies in this loop" % extradeps)

        # Remove recrdeptask circular references so that do_a[recrdeptask] = "do_a do_b" can work
        for tid in recursivetasksselfref:
            self.runtaskentries[tid].depends.difference_update(recursivetasksselfref)

        self.init_progress_reporter.next_stage()

        #self.dump_data()

        # Step B - Mark all active tasks
        #
        # Start with the tasks we were asked to run and mark all dependencies
        # as active too. If the task is to be 'forced', clear its stamp. Once
        # all active tasks are marked, prune the ones we don't need.

        logger.verbose("Marking Active Tasks")

        def mark_active(tid, depth):
            """
            Mark an item as active along with its depends
            (calls itself recursively)
            """

            if tid in runq_build:
                return

            runq_build[tid] = 1

            depends = self.runtaskentries[tid].depends
            for depend in depends:
                mark_active(depend, depth+1)

        self.target_tids = []
        for (mc, target, task, fn) in self.targets:

            if target not in taskData[mc].build_targets or not taskData[mc].build_targets[target]:
                continue

            if target in taskData[mc].failed_deps:
                continue

            parents = False
            if task.endswith('-'):
                parents = True
                task = task[:-1]

            if fn in taskData[mc].failed_fns:
                continue

            # fn already has mc prefix
            tid = fn + ":" + task
            self.target_tids.append(tid)
            if tid not in taskData[mc].taskentries:
                import difflib
                tasks = []
                for x in taskData[mc].taskentries:
                    if x.startswith(fn + ":"):
                        tasks.append(taskname_from_tid(x))
                close_matches = difflib.get_close_matches(task, tasks, cutoff=0.7)
                if close_matches:
                    extra = ". Close matches:\n  %s" % "\n  ".join(close_matches)
                else:
                    extra = ""
                bb.msg.fatal("RunQueue", "Task %s does not exist for target %s (%s)%s" % (task, target, tid, extra))

            # For tasks called "XXXX-", ony run their dependencies
            if parents:
                for i in self.runtaskentries[tid].depends:
                    mark_active(i, 1)
            else:
                mark_active(tid, 1)

        self.init_progress_reporter.next_stage()

        # Step C - Prune all inactive tasks
        #
        # Once all active tasks are marked, prune the ones we don't need.

        delcount = {}
        for tid in list(self.runtaskentries.keys()):
            if tid not in runq_build:
                delcount[tid] = self.runtaskentries[tid]
                del self.runtaskentries[tid]

        # Handle --runall
        if self.cooker.configuration.runall:
            # re-run the mark_active and then drop unused tasks from new list
            runq_build = {}

            for task in self.cooker.configuration.runall:
                runall_tids = set()
                for tid in list(self.runtaskentries):
                    wanttid = fn_from_tid(tid) + ":do_%s" % task
                    if wanttid in delcount:
                        self.runtaskentries[wanttid] = delcount[wanttid]
                    if wanttid in self.runtaskentries:
                        runall_tids.add(wanttid)

                for tid in list(runall_tids):
                    mark_active(tid,1)

            for tid in list(self.runtaskentries.keys()):
                if tid not in runq_build:
                    delcount[tid] = self.runtaskentries[tid]
                    del self.runtaskentries[tid]

            if len(self.runtaskentries) == 0:
                bb.msg.fatal("RunQueue", "Could not find any tasks with the tasknames %s to run within the recipes of the taskgraphs of the targets %s" % (str(self.cooker.configuration.runall), str(self.targets)))

        self.init_progress_reporter.next_stage()

        # Handle runonly
        if self.cooker.configuration.runonly:
            # re-run the mark_active and then drop unused tasks from new list
            runq_build = {}

            for task in self.cooker.configuration.runonly:
                runonly_tids = { k: v for k, v in self.runtaskentries.items() if taskname_from_tid(k) == "do_%s" % task }

                for tid in list(runonly_tids):
                    mark_active(tid,1)

            for tid in list(self.runtaskentries.keys()):
                if tid not in runq_build:
                    delcount[tid] = self.runtaskentries[tid]
                    del self.runtaskentries[tid]

            if len(self.runtaskentries) == 0:
                bb.msg.fatal("RunQueue", "Could not find any tasks with the tasknames %s to run within the taskgraphs of the targets %s" % (str(self.cooker.configuration.runonly), str(self.targets)))

        #
        # Step D - Sanity checks and computation
        #

        # Check to make sure we still have tasks to run
        if len(self.runtaskentries) == 0:
            if not taskData[''].abort:
                bb.msg.fatal("RunQueue", "All buildable tasks have been run but the build is incomplete (--continue mode). Errors for the tasks that failed will have been printed above.")
            else:
                bb.msg.fatal("RunQueue", "No active tasks and not in --continue mode?! Please report this bug.")

        logger.verbose("Pruned %s inactive tasks, %s left", len(delcount), len(self.runtaskentries))

        logger.verbose("Assign Weightings")

        self.init_progress_reporter.next_stage()

        # Generate a list of reverse dependencies to ease future calculations
        for tid in self.runtaskentries:
            for dep in self.runtaskentries[tid].depends:
                self.runtaskentries[dep].revdeps.add(tid)

        self.init_progress_reporter.next_stage()

        # Identify tasks at the end of dependency chains
        # Error on circular dependency loops (length two)
        endpoints = []
        for tid in self.runtaskentries:
            revdeps = self.runtaskentries[tid].revdeps
            if len(revdeps) == 0:
                endpoints.append(tid)
            for dep in revdeps:
                if dep in self.runtaskentries[tid].depends:
                    bb.msg.fatal("RunQueue", "Task %s has circular dependency on %s" % (tid, dep))


        logger.verbose("Compute totals (have %s endpoint(s))", len(endpoints))

        self.init_progress_reporter.next_stage()

        # Calculate task weights
        # Check of higher length circular dependencies
        self.runq_weight = self.calculate_task_weights(endpoints)

        self.init_progress_reporter.next_stage()

        # Sanity Check - Check for multiple tasks building the same provider
        for mc in self.dataCaches:
            prov_list = {}
            seen_fn = []
            for tid in self.runtaskentries:
                (tidmc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                if taskfn in seen_fn:
                    continue
                if mc != tidmc:
                    continue
                seen_fn.append(taskfn)
                for prov in self.dataCaches[mc].fn_provides[taskfn]:
                    if prov not in prov_list:
                        prov_list[prov] = [taskfn]
                    elif taskfn not in prov_list[prov]:
                        prov_list[prov].append(taskfn)
            for prov in prov_list:
                if len(prov_list[prov]) < 2:
                    continue
                if prov in self.multi_provider_whitelist:
                    continue
                seen_pn = []
                # If two versions of the same PN are being built its fatal, we don't support it.
                for fn in prov_list[prov]:
                    pn = self.dataCaches[mc].pkg_fn[fn]
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
                    for tid in self.runtaskentries:
                        fn = fn_from_tid(tid)
                        if fn != provfn:
                            continue
                        for dep in self.runtaskentries[tid].revdeps:
                            fn = fn_from_tid(dep)
                            if fn == provfn:
                                continue
                            deps.add(dep)
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
                    provides = set(self.dataCaches[mc].fn_provides[provfn])
                    rprovides = set()
                    for rprovide in self.dataCaches[mc].rproviders:
                        if provfn in self.dataCaches[mc].rproviders[rprovide]:
                            rprovides.add(rprovide)
                    for package in self.dataCaches[mc].packages:
                        if provfn in self.dataCaches[mc].packages[package]:
                            rprovides.add(package)
                    for package in self.dataCaches[mc].packages_dynamic:
                        if provfn in self.dataCaches[mc].packages_dynamic[package]:
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
                    logger.warning(msg)
                else:
                    logger.error(msg)

        self.init_progress_reporter.next_stage()

        # Create a whitelist usable by the stamp checks
        self.stampfnwhitelist = {}
        for mc in self.taskData: 
            self.stampfnwhitelist[mc] = []
            for entry in self.stampwhitelist.split():
                if entry not in self.taskData[mc].build_targets:
                    continue
                fn = self.taskData.build_targets[entry][0]
                self.stampfnwhitelist[mc].append(fn)

        self.init_progress_reporter.next_stage()

        # Iterate over the task list looking for tasks with a 'setscene' function
        self.runq_setscene_tids = []
        if not self.cooker.configuration.nosetscene:
            for tid in self.runtaskentries:
                (mc, fn, taskname, _) = split_tid_mcfn(tid)
                setscenetid = tid + "_setscene"
                if setscenetid not in taskData[mc].taskentries:
                    continue
                self.runq_setscene_tids.append(tid)

        def invalidate_task(tid, error_nostamp):
            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            taskdep = self.dataCaches[mc].task_deps[taskfn]
            if fn + ":" + taskname not in taskData[mc].taskentries:
                logger.warning("Task %s does not exist, invalidating this task will have no effect" % taskname)
            if 'nostamp' in taskdep and taskname in taskdep['nostamp']:
                if error_nostamp:
                    bb.fatal("Task %s is marked nostamp, cannot invalidate this task" % taskname)
                else:
                    bb.debug(1, "Task %s is marked nostamp, cannot invalidate this task" % taskname)
            else:
                logger.verbose("Invalidate task %s, %s", taskname, fn)
                bb.parse.siggen.invalidate_task(taskname, self.dataCaches[mc], fn)

        self.init_progress_reporter.next_stage()

        # Invalidate task if force mode active
        if self.cooker.configuration.force:
            for tid in self.target_tids:
                invalidate_task(tid, False)

        # Invalidate task if invalidate mode active
        if self.cooker.configuration.invalidate_stamp:
            for tid in self.target_tids:
                fn = fn_from_tid(tid)
                for st in self.cooker.configuration.invalidate_stamp.split(','):
                    if not st.startswith("do_"):
                        st = "do_%s" % st
                    invalidate_task(fn + ":" + st, True)

        self.init_progress_reporter.next_stage()

        # Create and print to the logs a virtual/xxxx -> PN (fn) table
        for mc in taskData:
            virtmap = taskData[mc].get_providermap(prefix="virtual/")
            virtpnmap = {}
            for v in virtmap:
                virtpnmap[v] = self.dataCaches[mc].pkg_fn[virtmap[v]]
                bb.debug(2, "%s resolved to: %s (%s)" % (v, virtpnmap[v], virtmap[v]))
            if hasattr(bb.parse.siggen, "tasks_resolved"):
                bb.parse.siggen.tasks_resolved(virtmap, virtpnmap, self.dataCaches[mc])

        self.init_progress_reporter.next_stage()

        # Iterate over the task list and call into the siggen code
        dealtwith = set()
        todeal = set(self.runtaskentries)
        while len(todeal) > 0:
            for tid in todeal.copy():
                if len(self.runtaskentries[tid].depends - dealtwith) == 0:
                    dealtwith.add(tid)
                    todeal.remove(tid)
                    procdep = []
                    for dep in self.runtaskentries[tid].depends:
                        procdep.append(fn_from_tid(dep) + "." + taskname_from_tid(dep))
                    (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                    self.runtaskentries[tid].hash = bb.parse.siggen.get_taskhash(taskfn, taskname, procdep, self.dataCaches[mc])
                    task = self.runtaskentries[tid].task

        bb.parse.siggen.writeout_file_checksum_cache()

        #self.dump_data()
        return len(self.runtaskentries)

    def dump_data(self):
        """
        Dump some debug information on the internal data structures
        """
        logger.debug(3, "run_tasks:")
        for tid in self.runtaskentries:
            logger.debug(3, " %s: %s   Deps %s RevDeps %s", tid,
                         self.runtaskentries[tid].weight,
                         self.runtaskentries[tid].depends,
                         self.runtaskentries[tid].revdeps)

class RunQueueWorker():
    def __init__(self, process, pipe):
        self.process = process
        self.pipe = pipe

class RunQueue:
    def __init__(self, cooker, cfgData, dataCaches, taskData, targets):

        self.cooker = cooker
        self.cfgData = cfgData
        self.rqdata = RunQueueData(self, cooker, cfgData, dataCaches, taskData, targets)

        self.stamppolicy = cfgData.getVar("BB_STAMP_POLICY") or "perfile"
        self.hashvalidate = cfgData.getVar("BB_HASHCHECK_FUNCTION") or None
        self.setsceneverify = cfgData.getVar("BB_SETSCENE_VERIFY_FUNCTION2") or None
        self.depvalidate = cfgData.getVar("BB_SETSCENE_DEPVALID") or None

        self.state = runQueuePrepare

        # For disk space monitor
        # Invoked at regular time intervals via the bitbake heartbeat event
        # while the build is running. We generate a unique name for the handler
        # here, just in case that there ever is more than one RunQueue instance,
        # start the handler when reaching runQueueSceneRun, and stop it when
        # done with the build.
        self.dm = monitordisk.diskMonitor(cfgData)
        self.dm_event_handler_name = '_bb_diskmonitor_' + str(id(self))
        self.dm_event_handler_registered = False
        self.rqexe = None
        self.worker = {}
        self.fakeworker = {}

    def _start_worker(self, mc, fakeroot = False, rqexec = None):
        logger.debug(1, "Starting bitbake-worker")
        magic = "decafbad"
        if self.cooker.configuration.profile:
            magic = "decafbadbad"
        if fakeroot:
            magic = magic + "beef"
            mcdata = self.cooker.databuilder.mcdata[mc]
            fakerootcmd = mcdata.getVar("FAKEROOTCMD")
            fakerootenv = (mcdata.getVar("FAKEROOTBASEENV") or "").split()
            env = os.environ.copy()
            for key, value in (var.split('=') for var in fakerootenv):
                env[key] = value
            worker = subprocess.Popen([fakerootcmd, "bitbake-worker", magic], stdout=subprocess.PIPE, stdin=subprocess.PIPE, env=env)
        else:
            worker = subprocess.Popen(["bitbake-worker", magic], stdout=subprocess.PIPE, stdin=subprocess.PIPE)
        bb.utils.nonblockingfd(worker.stdout)
        workerpipe = runQueuePipe(worker.stdout, None, self.cfgData, self, rqexec)

        runqhash = {}
        for tid in self.rqdata.runtaskentries:
            runqhash[tid] = self.rqdata.runtaskentries[tid].hash

        workerdata = {
            "taskdeps" : self.rqdata.dataCaches[mc].task_deps,
            "fakerootenv" : self.rqdata.dataCaches[mc].fakerootenv,
            "fakerootdirs" : self.rqdata.dataCaches[mc].fakerootdirs,
            "fakerootnoenv" : self.rqdata.dataCaches[mc].fakerootnoenv,
            "sigdata" : bb.parse.siggen.get_taskdata(),
            "runq_hash" : runqhash,
            "logdefaultdebug" : bb.msg.loggerDefaultDebugLevel,
            "logdefaultverbose" : bb.msg.loggerDefaultVerbose,
            "logdefaultverboselogs" : bb.msg.loggerVerboseLogs,
            "logdefaultdomain" : bb.msg.loggerDefaultDomains,
            "prhost" : self.cooker.prhost,
            "buildname" : self.cfgData.getVar("BUILDNAME"),
            "date" : self.cfgData.getVar("DATE"),
            "time" : self.cfgData.getVar("TIME"),
        }

        worker.stdin.write(b"<cookerconfig>" + pickle.dumps(self.cooker.configuration) + b"</cookerconfig>")
        worker.stdin.write(b"<extraconfigdata>" + pickle.dumps(self.cooker.extraconfigdata) + b"</extraconfigdata>")
        worker.stdin.write(b"<workerdata>" + pickle.dumps(workerdata) + b"</workerdata>")
        worker.stdin.flush()

        return RunQueueWorker(worker, workerpipe)

    def _teardown_worker(self, worker):
        if not worker:
            return
        logger.debug(1, "Teardown for bitbake-worker")
        try:
           worker.process.stdin.write(b"<quit></quit>")
           worker.process.stdin.flush()
           worker.process.stdin.close()
        except IOError:
           pass
        while worker.process.returncode is None:
            worker.pipe.read()
            worker.process.poll()
        while worker.pipe.read():
            continue
        worker.pipe.close()

    def start_worker(self):
        if self.worker:
            self.teardown_workers()
        self.teardown = False
        for mc in self.rqdata.dataCaches:
            self.worker[mc] = self._start_worker(mc)

    def start_fakeworker(self, rqexec, mc):
        if not mc in self.fakeworker:
            self.fakeworker[mc] = self._start_worker(mc, True, rqexec)

    def teardown_workers(self):
        self.teardown = True
        for mc in self.worker:
            self._teardown_worker(self.worker[mc])
        self.worker = {}
        for mc in self.fakeworker:
            self._teardown_worker(self.fakeworker[mc])
        self.fakeworker = {}

    def read_workers(self):
        for mc in self.worker:
            self.worker[mc].pipe.read()
        for mc in self.fakeworker:
            self.fakeworker[mc].pipe.read()

    def active_fds(self):
        fds = []
        for mc in self.worker:
            fds.append(self.worker[mc].pipe.input)
        for mc in self.fakeworker:
            fds.append(self.fakeworker[mc].pipe.input)
        return fds

    def check_stamp_task(self, tid, taskname = None, recurse = False, cache = None):
        def get_timestamp(f):
            try:
                if not os.access(f, os.F_OK):
                    return None
                return os.stat(f)[stat.ST_MTIME]
            except:
                return None

        (mc, fn, tn, taskfn) = split_tid_mcfn(tid)
        if taskname is None:
            taskname = tn

        if self.stamppolicy == "perfile":
            fulldeptree = False
        else:
            fulldeptree = True
            stampwhitelist = []
            if self.stamppolicy == "whitelist":
                stampwhitelist = self.rqdata.stampfnwhitelist[mc]

        stampfile = bb.build.stampfile(taskname, self.rqdata.dataCaches[mc], taskfn)

        # If the stamp is missing, it's not current
        if not os.access(stampfile, os.F_OK):
            logger.debug(2, "Stampfile %s not available", stampfile)
            return False
        # If it's a 'nostamp' task, it's not current
        taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
        if 'nostamp' in taskdep and taskname in taskdep['nostamp']:
            logger.debug(2, "%s.%s is nostamp\n", fn, taskname)
            return False

        if taskname != "do_setscene" and taskname.endswith("_setscene"):
            return True

        if cache is None:
            cache = {}

        iscurrent = True
        t1 = get_timestamp(stampfile)
        for dep in self.rqdata.runtaskentries[tid].depends:
            if iscurrent:
                (mc2, fn2, taskname2, taskfn2) = split_tid_mcfn(dep)
                stampfile2 = bb.build.stampfile(taskname2, self.rqdata.dataCaches[mc2], taskfn2)
                stampfile3 = bb.build.stampfile(taskname2 + "_setscene", self.rqdata.dataCaches[mc2], taskfn2)
                t2 = get_timestamp(stampfile2)
                t3 = get_timestamp(stampfile3)
                if t3 and not t2:
                    continue
                if t3 and t3 > t2:
                    continue
                if fn == fn2 or (fulldeptree and fn2 not in stampwhitelist):
                    if not t2:
                        logger.debug(2, 'Stampfile %s does not exist', stampfile2)
                        iscurrent = False
                        break
                    if t1 < t2:
                        logger.debug(2, 'Stampfile %s < %s', stampfile, stampfile2)
                        iscurrent = False
                        break
                    if recurse and iscurrent:
                        if dep in cache:
                            iscurrent = cache[dep]
                            if not iscurrent:
                                logger.debug(2, 'Stampfile for dependency %s:%s invalid (cached)' % (fn2, taskname2))
                        else:
                            iscurrent = self.check_stamp_task(dep, recurse=True, cache=cache)
                            cache[dep] = iscurrent
        if recurse:
            cache[tid] = iscurrent
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
            # NOTE: if you add, remove or significantly refactor the stages of this
            # process then you should recalculate the weightings here. This is quite
            # easy to do - just change the next line temporarily to pass debug=True as
            # the last parameter and you'll get a printout of the weightings as well
            # as a map to the lines where next_stage() was called. Of course this isn't
            # critical, but it helps to keep the progress reporting accurate.
            self.rqdata.init_progress_reporter = bb.progress.MultiStageProcessProgressReporter(self.cooker.data,
                                                            "Initialising tasks",
                                                            [43, 967, 4, 3, 1, 5, 3, 7, 13, 1, 2, 1, 1, 246, 35, 1, 38, 1, 35, 2, 338, 204, 142, 3, 3, 37, 244])
            if self.rqdata.prepare() == 0:
                self.state = runQueueComplete
            else:
                self.state = runQueueSceneInit
                self.rqdata.init_progress_reporter.next_stage()

                # we are ready to run,  emit dependency info to any UI or class which
                # needs it
                depgraph = self.cooker.buildDependTree(self, self.rqdata.taskData)
                self.rqdata.init_progress_reporter.next_stage()
                bb.event.fire(bb.event.DepTreeGenerated(depgraph), self.cooker.data)

        if self.state is runQueueSceneInit:
            if not self.dm_event_handler_registered:
                 res = bb.event.register(self.dm_event_handler_name,
                                         lambda x: self.dm.check(self) if self.state in [runQueueSceneRun, runQueueRunning, runQueueCleanUp] else False,
                                         ('bb.event.HeartbeatEvent',))
                 self.dm_event_handler_registered = True

            dump = self.cooker.configuration.dump_signatures
            if dump:
                self.rqdata.init_progress_reporter.finish()
                if 'printdiff' in dump:
                    invalidtasks = self.print_diffscenetasks()
                self.dump_signatures(dump)
                if 'printdiff' in dump:
                    self.write_diffscenetasks(invalidtasks)
                self.state = runQueueComplete
            else:
                self.rqdata.init_progress_reporter.next_stage()
                self.start_worker()
                self.rqdata.init_progress_reporter.next_stage()
                self.rqexe = RunQueueExecuteScenequeue(self)

        if self.state is runQueueSceneRun:
            retval = self.rqexe.execute()

        if self.state is runQueueRunInit:
            if self.cooker.configuration.setsceneonly:
                self.state = runQueueComplete
            else:
                # Just in case we didn't setscene
                self.rqdata.init_progress_reporter.finish()
                logger.info("Executing RunQueue Tasks")
                self.rqexe = RunQueueExecuteTasks(self)
                self.state = runQueueRunning

        if self.state is runQueueRunning:
            retval = self.rqexe.execute()

        if self.state is runQueueCleanUp:
            retval = self.rqexe.finish()

        build_done = self.state is runQueueComplete or self.state is runQueueFailed

        if build_done and self.dm_event_handler_registered:
            bb.event.remove(self.dm_event_handler_name, None)
            self.dm_event_handler_registered = False

        if build_done and self.rqexe:
            self.teardown_workers()
            if self.rqexe.stats.failed:
                logger.info("Tasks Summary: Attempted %d tasks of which %d didn't need to be rerun and %d failed.", self.rqexe.stats.completed + self.rqexe.stats.failed, self.rqexe.stats.skipped, self.rqexe.stats.failed)
            else:
                # Let's avoid the word "failed" if nothing actually did
                logger.info("Tasks Summary: Attempted %d tasks of which %d didn't need to be rerun and all succeeded.", self.rqexe.stats.completed, self.rqexe.stats.skipped)

        if self.state is runQueueFailed:
            raise bb.runqueue.TaskFailure(self.rqexe.failed_tids)

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
        except Exception as err:
            logger.exception("An uncaught exception occurred in runqueue")
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

    def rq_dump_sigfn(self, fn, options):
        bb_cache = bb.cache.NoCache(self.cooker.databuilder)
        the_data = bb_cache.loadDataFull(fn, self.cooker.collection.get_file_appends(fn))
        siggen = bb.parse.siggen
        dataCaches = self.rqdata.dataCaches
        siggen.dump_sigfn(fn, dataCaches, options)

    def dump_signatures(self, options):
        fns = set()
        bb.note("Reparsing files to collect dependency data")

        for tid in self.rqdata.runtaskentries:
            fn = fn_from_tid(tid)
            fns.add(fn)

        max_process = int(self.cfgData.getVar("BB_NUMBER_PARSE_THREADS") or os.cpu_count() or 1)
        # We cannot use the real multiprocessing.Pool easily due to some local data
        # that can't be pickled. This is a cheap multi-process solution.
        launched = []
        while fns:
            if len(launched) < max_process:
                p = Process(target=self.rq_dump_sigfn, args=(fns.pop(), options))
                p.start()
                launched.append(p)
            for q in launched:
                # The finished processes are joined when calling is_alive()
                if not q.is_alive():
                    launched.remove(q)
        for p in launched:
                p.join()

        bb.parse.siggen.dump_sigs(self.rqdata.dataCaches, options)

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

        for tid in self.rqdata.runtaskentries:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]

            if 'noexec' in taskdep and taskname in taskdep['noexec']:
                noexec.append(tid)
                continue

            sq_fn.append(fn)
            sq_hashfn.append(self.rqdata.dataCaches[mc].hashfn[taskfn])
            sq_hash.append(self.rqdata.runtaskentries[tid].hash)
            sq_taskname.append(taskname)
            sq_task.append(tid)
        locs = { "sq_fn" : sq_fn, "sq_task" : sq_taskname, "sq_hash" : sq_hash, "sq_hashfn" : sq_hashfn, "d" : self.cooker.data }
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
        for tid in noexec:
            if tid not in self.rqdata.runq_setscene_tids:
                continue
            for dep in self.rqdata.runtaskentries[tid].depends:
                hasnoexecparents = True
                for dep2 in self.rqdata.runtaskentries[dep].revdeps:
                    if dep2 in self.rqdata.runq_setscene_tids and dep2 in noexec:
                        continue
                    hasnoexecparents = False
                    break
                if hasnoexecparents:
                    valid_new.add(dep)

        invalidtasks = set()
        for tid in self.rqdata.runtaskentries:
            if tid not in valid_new and tid not in noexec:
                invalidtasks.add(tid)

        found = set()
        processed = set()
        for tid in invalidtasks:
            toprocess = set([tid])
            while toprocess:
                next = set()
                for t in toprocess:
                    for dep in self.rqdata.runtaskentries[t].depends:
                        if dep in invalidtasks:
                            found.add(tid)
                        if dep not in processed:
                            processed.add(dep)
                            next.add(dep)
                toprocess = next
                if tid in found:
                    toprocess = set()

        tasklist = []
        for tid in invalidtasks.difference(found):
            tasklist.append(tid)

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


        for tid in invalidtasks:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
            h = self.rqdata.runtaskentries[tid].hash
            matches = bb.siggen.find_siginfo(pn, taskname, [], self.cfgData)
            match = None
            for m in matches:
                if h in m:
                    match = m
            if match is None:
                bb.fatal("Can't find a task we're supposed to have written out? (hash: %s)?" % h)
            matches = {k : v for k, v in iter(matches.items()) if h not in k}
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

        self.number_tasks = int(self.cfgData.getVar("BB_NUMBER_THREADS") or 1)
        self.scheduler = self.cfgData.getVar("BB_SCHEDULER") or "speed"

        self.runq_buildable = set()
        self.runq_running = set()
        self.runq_complete = set()

        self.build_stamps = {}
        self.build_stamps2 = []
        self.failed_tids = []

        self.stampcache = {}

        for mc in rq.worker:
            rq.worker[mc].pipe.setrunqueueexec(self)
        for mc in rq.fakeworker:
            rq.fakeworker[mc].pipe.setrunqueueexec(self)

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
        for mc in self.rq.worker:
            try:
                self.rq.worker[mc].process.stdin.write(b"<finishnow></finishnow>")
                self.rq.worker[mc].process.stdin.flush()
            except IOError:
                # worker must have died?
                pass
        for mc in self.rq.fakeworker:
            try:
                self.rq.fakeworker[mc].process.stdin.write(b"<finishnow></finishnow>")
                self.rq.fakeworker[mc].process.stdin.flush()
            except IOError:
                # worker must have died?
                pass

        if len(self.failed_tids) != 0:
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

        if len(self.failed_tids) != 0:
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
            (mc, fn, taskname, taskfn) = split_tid_mcfn(dep)
            pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
            taskdata[dep] = [pn, taskname, fn]
        call = self.rq.depvalidate + "(task, taskdata, notneeded, d)"
        locs = { "task" : task, "taskdata" : taskdata, "notneeded" : self.scenequeue_notneeded, "d" : self.cooker.data }
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

        self.stats = RunQueueStats(len(self.rqdata.runtaskentries))

        self.stampcache = {}

        initial_covered = self.rq.scenequeue_covered.copy()

        # Mark initial buildable tasks
        for tid in self.rqdata.runtaskentries:
            if len(self.rqdata.runtaskentries[tid].depends) == 0:
                self.runq_buildable.add(tid)
            if len(self.rqdata.runtaskentries[tid].revdeps) > 0 and self.rqdata.runtaskentries[tid].revdeps.issubset(self.rq.scenequeue_covered):
                self.rq.scenequeue_covered.add(tid)

        found = True
        while found:
            found = False
            for tid in self.rqdata.runtaskentries:
                if tid in self.rq.scenequeue_covered:
                    continue
                logger.debug(1, 'Considering %s: %s' % (tid, str(self.rqdata.runtaskentries[tid].revdeps)))

                if len(self.rqdata.runtaskentries[tid].revdeps) > 0 and self.rqdata.runtaskentries[tid].revdeps.issubset(self.rq.scenequeue_covered):
                    if tid in self.rq.scenequeue_notcovered:
                        continue
                    found = True
                    self.rq.scenequeue_covered.add(tid)

        logger.debug(1, 'Skip list (pre setsceneverify) %s', sorted(self.rq.scenequeue_covered))

        # Allow the metadata to elect for setscene tasks to run anyway
        covered_remove = set()
        if self.rq.setsceneverify:
            invalidtasks = []
            tasknames = {}
            fns = {}
            for tid in self.rqdata.runtaskentries:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
                fns[tid] = taskfn
                tasknames[tid] = taskname
                if 'noexec' in taskdep and taskname in taskdep['noexec']:
                    continue
                if self.rq.check_stamp_task(tid, taskname + "_setscene", cache=self.stampcache):
                    logger.debug(2, 'Setscene stamp current for task %s', tid)
                    continue
                if self.rq.check_stamp_task(tid, taskname, recurse = True, cache=self.stampcache):
                    logger.debug(2, 'Normal stamp current for task %s', tid)
                    continue
                invalidtasks.append(tid)

            call = self.rq.setsceneverify + "(covered, tasknames, fns, d, invalidtasks=invalidtasks)"
            locs = { "covered" : self.rq.scenequeue_covered, "tasknames" : tasknames, "fns" : fns, "d" : self.cooker.data, "invalidtasks" : invalidtasks }
            covered_remove = bb.utils.better_eval(call, locs)

        def removecoveredtask(tid):
            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            taskname = taskname + '_setscene'
            bb.build.del_stamp(taskname, self.rqdata.dataCaches[mc], taskfn)
            self.rq.scenequeue_covered.remove(tid)

        toremove = covered_remove
        for task in toremove:
            logger.debug(1, 'Not skipping task %s due to setsceneverify', task)
        while toremove:
            covered_remove = []
            for task in toremove:
                removecoveredtask(task)
                for deptask in self.rqdata.runtaskentries[task].depends:
                    if deptask not in self.rq.scenequeue_covered:
                        continue
                    if deptask in toremove or deptask in covered_remove or deptask in initial_covered:
                        continue
                    logger.debug(1, 'Task %s depends on task %s so not skipping' % (task, deptask))
                    covered_remove.append(deptask)
            toremove = covered_remove

        logger.debug(1, 'Full skip list %s', self.rq.scenequeue_covered)


        for mc in self.rqdata.dataCaches:
            target_pairs = []
            for tid in self.rqdata.target_tids:
                (tidmc, fn, taskname, _) = split_tid_mcfn(tid)
                if tidmc == mc:
                    target_pairs.append((fn, taskname))

            event.fire(bb.event.StampUpdate(target_pairs, self.rqdata.dataCaches[mc].stamp), self.cfgData)

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

        user_schedulers = self.cfgData.getVar("BB_SCHEDULERS")
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
        self.runq_buildable.add(task)
        self.sched.newbuildable(task)

    def task_completeoutright(self, task):
        """
        Mark a task as completed
        Look at the reverse dependencies and mark any task with
        completed dependencies as buildable
        """
        self.runq_complete.add(task)
        for revdep in self.rqdata.runtaskentries[task].revdeps:
            if revdep in self.runq_running:
                continue
            if revdep in self.runq_buildable:
                continue
            alldeps = 1
            for dep in self.rqdata.runtaskentries[revdep].depends:
                if dep not in self.runq_complete:
                    alldeps = 0
            if alldeps == 1:
                self.setbuildable(revdep)
                fn = fn_from_tid(revdep)
                taskname = taskname_from_tid(revdep)
                logger.debug(1, "Marking task %s as buildable", revdep)

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
        self.failed_tids.append(task)
        bb.event.fire(runQueueTaskFailed(task, self.stats, exitcode, self.rq), self.cfgData)
        if self.rqdata.taskData[''].abort:
            self.rq.state = runQueueCleanUp

    def task_skip(self, task, reason):
        self.runq_running.add(task)
        self.setbuildable(task)
        bb.event.fire(runQueueTaskSkipped(task, self.stats, self.rq, reason), self.cfgData)
        self.task_completeoutright(task)
        self.stats.taskCompleted()
        self.stats.taskSkipped()

    def execute(self):
        """
        Run the tasks in a queue prepared by rqdata.prepare()
        """

        if self.rqdata.setscenewhitelist is not None and not self.rqdata.setscenewhitelist_checked:
            self.rqdata.setscenewhitelist_checked = True

            # Check tasks that are going to run against the whitelist
            def check_norun_task(tid, showerror=False):
                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                # Ignore covered tasks
                if tid in self.rq.scenequeue_covered:
                    return False
                # Ignore stamped tasks
                if self.rq.check_stamp_task(tid, taskname, cache=self.stampcache):
                    return False
                # Ignore noexec tasks
                taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
                if 'noexec' in taskdep and taskname in taskdep['noexec']:
                    return False

                pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
                if not check_setscene_enforce_whitelist(pn, taskname, self.rqdata.setscenewhitelist):
                    if showerror:
                        if tid in self.rqdata.runq_setscene_tids:
                            logger.error('Task %s.%s attempted to execute unexpectedly and should have been setscened' % (pn, taskname))
                        else:
                            logger.error('Task %s.%s attempted to execute unexpectedly' % (pn, taskname))
                    return True
                return False
            # Look to see if any tasks that we think shouldn't run are going to
            unexpected = False
            for tid in self.rqdata.runtaskentries:
                if check_norun_task(tid):
                    unexpected = True
                    break
            if unexpected:
                # Run through the tasks in the rough order they'd have executed and print errors
                # (since the order can be useful - usually missing sstate for the last few tasks
                # is the cause of the problem)
                task = self.sched.next()
                while task is not None:
                    check_norun_task(task, showerror=True)
                    self.task_skip(task, 'Setscene enforcement check')
                    task = self.sched.next()

                self.rq.state = runQueueCleanUp
                return True

        self.rq.read_workers()

        if self.stats.total == 0:
            # nothing to do
            self.rq.state = runQueueCleanUp

        task = self.sched.next()
        if task is not None:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(task)

            if task in self.rq.scenequeue_covered:
                logger.debug(2, "Setscene covered task %s", task)
                self.task_skip(task, "covered")
                return True

            if self.rq.check_stamp_task(task, taskname, cache=self.stampcache):
                logger.debug(2, "Stamp current task %s", task)

                self.task_skip(task, "existing")
                return True

            taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
            if 'noexec' in taskdep and taskname in taskdep['noexec']:
                startevent = runQueueTaskStarted(task, self.stats, self.rq,
                                                 noexec=True)
                bb.event.fire(startevent, self.cfgData)
                self.runq_running.add(task)
                self.stats.taskActive()
                if not (self.cooker.configuration.dry_run or self.rqdata.setscene_enforce):
                    bb.build.make_stamp(taskname, self.rqdata.dataCaches[mc], taskfn)
                self.task_complete(task)
                return True
            else:
                startevent = runQueueTaskStarted(task, self.stats, self.rq)
                bb.event.fire(startevent, self.cfgData)

            taskdepdata = self.build_taskdepdata(task)

            taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
            if 'fakeroot' in taskdep and taskname in taskdep['fakeroot'] and not (self.cooker.configuration.dry_run or self.rqdata.setscene_enforce):
                if not mc in self.rq.fakeworker:
                    try:
                        self.rq.start_fakeworker(self, mc)
                    except OSError as exc:
                        logger.critical("Failed to spawn fakeroot worker to run %s: %s" % (task, str(exc)))
                        self.rq.state = runQueueFailed
                        self.stats.taskFailed()
                        return True
                self.rq.fakeworker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, False, self.cooker.collection.get_file_appends(taskfn), taskdepdata, self.rqdata.setscene_enforce)) + b"</runtask>")
                self.rq.fakeworker[mc].process.stdin.flush()
            else:
                self.rq.worker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, False, self.cooker.collection.get_file_appends(taskfn), taskdepdata, self.rqdata.setscene_enforce)) + b"</runtask>")
                self.rq.worker[mc].process.stdin.flush()

            self.build_stamps[task] = bb.build.stampfile(taskname, self.rqdata.dataCaches[mc], taskfn, noextra=True)
            self.build_stamps2.append(self.build_stamps[task])
            self.runq_running.add(task)
            self.stats.taskActive()
            if self.stats.active < self.number_tasks:
                return True

        if self.stats.active > 0:
            self.rq.read_workers()
            return self.rq.active_fds()

        if len(self.failed_tids) != 0:
            self.rq.state = runQueueFailed
            return True

        # Sanity Checks
        for task in self.rqdata.runtaskentries:
            if task not in self.runq_buildable:
                logger.error("Task %s never buildable!", task)
            if task not in self.runq_running:
                logger.error("Task %s never ran!", task)
            if task not in self.runq_complete:
                logger.error("Task %s never completed!", task)
        self.rq.state = runQueueComplete

        return True

    def build_taskdepdata(self, task):
        taskdepdata = {}
        next = self.rqdata.runtaskentries[task].depends
        next.add(task)
        while next:
            additional = []
            for revdep in next:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(revdep)
                pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
                deps = self.rqdata.runtaskentries[revdep].depends
                provides = self.rqdata.dataCaches[mc].fn_provides[taskfn]
                taskhash = self.rqdata.runtaskentries[revdep].hash
                taskdepdata[revdep] = [pn, taskname, fn, deps, provides, taskhash]
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
        if len(self.rqdata.runq_setscene_tids) == 0:
            rq.scenequeue_covered = set()
            rq.state = runQueueRunInit
            return

        self.stats = RunQueueStats(len(self.rqdata.runq_setscene_tids))

        sq_revdeps = {}
        sq_revdeps_new = {}
        sq_revdeps_squash = {}
        self.sq_harddeps = {}
        self.stamps = {}

        # We need to construct a dependency graph for the setscene functions. Intermediate
        # dependencies between the setscene tasks only complicate the code. This code
        # therefore aims to collapse the huge runqueue dependency tree into a smaller one
        # only containing the setscene functions.

        self.rqdata.init_progress_reporter.next_stage()

        # First process the chains up to the first setscene task.
        endpoints = {}
        for tid in self.rqdata.runtaskentries:
            sq_revdeps[tid] = copy.copy(self.rqdata.runtaskentries[tid].revdeps)
            sq_revdeps_new[tid] = set()
            if (len(sq_revdeps[tid]) == 0) and tid not in self.rqdata.runq_setscene_tids:
                #bb.warn("Added endpoint %s" % (tid))
                endpoints[tid] = set()

        self.rqdata.init_progress_reporter.next_stage()

        # Secondly process the chains between setscene tasks.
        for tid in self.rqdata.runq_setscene_tids:
            #bb.warn("Added endpoint 2 %s" % (tid))
            for dep in self.rqdata.runtaskentries[tid].depends:
                    if tid in sq_revdeps[dep]:
                        sq_revdeps[dep].remove(tid)
                    if dep not in endpoints:
                        endpoints[dep] = set()
                    #bb.warn("  Added endpoint 3 %s" % (dep))
                    endpoints[dep].add(tid)

        self.rqdata.init_progress_reporter.next_stage()

        def process_endpoints(endpoints):
            newendpoints = {}
            for point, task in endpoints.items():
                tasks = set()
                if task:
                    tasks |= task
                if sq_revdeps_new[point]:
                    tasks |= sq_revdeps_new[point]
                sq_revdeps_new[point] = set()
                if point in self.rqdata.runq_setscene_tids:
                    sq_revdeps_new[point] = tasks
                    tasks = set()
                    continue
                for dep in self.rqdata.runtaskentries[point].depends:
                    if point in sq_revdeps[dep]:
                        sq_revdeps[dep].remove(point)
                    if tasks:
                        sq_revdeps_new[dep] |= tasks
                    if len(sq_revdeps[dep]) == 0 and dep not in self.rqdata.runq_setscene_tids:
                        newendpoints[dep] = task
            if len(newendpoints) != 0:
                process_endpoints(newendpoints)

        process_endpoints(endpoints)

        self.rqdata.init_progress_reporter.next_stage()

        # Build a list of setscene tasks which are "unskippable"
        # These are direct endpoints referenced by the build
        endpoints2 = {}
        sq_revdeps2 = {}
        sq_revdeps_new2 = {}
        def process_endpoints2(endpoints):
            newendpoints = {}
            for point, task in endpoints.items():
                tasks = set([point])
                if task:
                    tasks |= task
                if sq_revdeps_new2[point]:
                    tasks |= sq_revdeps_new2[point]
                sq_revdeps_new2[point] = set()
                if point in self.rqdata.runq_setscene_tids:
                    sq_revdeps_new2[point] = tasks
                for dep in self.rqdata.runtaskentries[point].depends:
                    if point in sq_revdeps2[dep]:
                        sq_revdeps2[dep].remove(point)
                    if tasks:
                        sq_revdeps_new2[dep] |= tasks
                    if (len(sq_revdeps2[dep]) == 0 or len(sq_revdeps_new2[dep]) != 0) and dep not in self.rqdata.runq_setscene_tids:
                        newendpoints[dep] = tasks
            if len(newendpoints) != 0:
                process_endpoints2(newendpoints)
        for tid in self.rqdata.runtaskentries:
            sq_revdeps2[tid] = copy.copy(self.rqdata.runtaskentries[tid].revdeps)
            sq_revdeps_new2[tid] = set()
            if (len(sq_revdeps2[tid]) == 0) and tid not in self.rqdata.runq_setscene_tids:
                endpoints2[tid] = set()
        process_endpoints2(endpoints2)
        self.unskippable = []
        for tid in self.rqdata.runq_setscene_tids:
            if sq_revdeps_new2[tid]:
                self.unskippable.append(tid)

        self.rqdata.init_progress_reporter.next_stage(len(self.rqdata.runtaskentries))

        for taskcounter, tid in enumerate(self.rqdata.runtaskentries):
            if tid in self.rqdata.runq_setscene_tids:
                deps = set()
                for dep in sq_revdeps_new[tid]:
                    deps.add(dep)
                sq_revdeps_squash[tid] = deps
            elif len(sq_revdeps_new[tid]) != 0:
                bb.msg.fatal("RunQueue", "Something went badly wrong during scenequeue generation, aborting. Please report this problem.")
            self.rqdata.init_progress_reporter.update(taskcounter)

        self.rqdata.init_progress_reporter.next_stage()

        # Resolve setscene inter-task dependencies
        # e.g. do_sometask_setscene[depends] = "targetname:do_someothertask_setscene"
        # Note that anything explicitly depended upon will have its reverse dependencies removed to avoid circular dependencies
        for tid in self.rqdata.runq_setscene_tids:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                realtid = tid + "_setscene"
                idepends = self.rqdata.taskData[mc].taskentries[realtid].idepends
                self.stamps[tid] = bb.build.stampfile(taskname + "_setscene", self.rqdata.dataCaches[mc], taskfn, noextra=True)
                for (depname, idependtask) in idepends:

                    if depname not in self.rqdata.taskData[mc].build_targets:
                        continue

                    depfn = self.rqdata.taskData[mc].build_targets[depname][0]
                    if depfn is None:
                         continue
                    deptid = depfn + ":" + idependtask.replace("_setscene", "")
                    if deptid not in self.rqdata.runtaskentries:
                        bb.msg.fatal("RunQueue", "Task %s depends upon non-existent task %s:%s" % (realtid, depfn, idependtask))

                    if not deptid in self.sq_harddeps:
                        self.sq_harddeps[deptid] = set()
                    self.sq_harddeps[deptid].add(tid)

                    sq_revdeps_squash[tid].add(deptid)
                    # Have to zero this to avoid circular dependencies
                    sq_revdeps_squash[deptid] = set()

        self.rqdata.init_progress_reporter.next_stage()

        for task in self.sq_harddeps:
             for dep in self.sq_harddeps[task]:
                 sq_revdeps_squash[dep].add(task)

        self.rqdata.init_progress_reporter.next_stage()

        #for tid in sq_revdeps_squash:
        #    for dep in sq_revdeps_squash[tid]:
        #        data = data + "\n   %s" % dep
        #    bb.warn("Task %s_setscene: is %s " % (tid, data

        self.sq_deps = {}
        self.sq_revdeps = sq_revdeps_squash
        self.sq_revdeps2 = copy.deepcopy(self.sq_revdeps)

        for tid in self.sq_revdeps:
            self.sq_deps[tid] = set()
        for tid in self.sq_revdeps:
            for dep in self.sq_revdeps[tid]:
                self.sq_deps[dep].add(tid)

        self.rqdata.init_progress_reporter.next_stage()

        for tid in self.sq_revdeps:
            if len(self.sq_revdeps[tid]) == 0:
                self.runq_buildable.add(tid)

        self.rqdata.init_progress_reporter.finish()

        self.outrightfail = []
        if self.rq.hashvalidate:
            sq_hash = []
            sq_hashfn = []
            sq_fn = []
            sq_taskname = []
            sq_task = []
            noexec = []
            stamppresent = []
            for tid in self.sq_revdeps:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)

                taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]

                if 'noexec' in taskdep and taskname in taskdep['noexec']:
                    noexec.append(tid)
                    self.task_skip(tid)
                    bb.build.make_stamp(taskname + "_setscene", self.rqdata.dataCaches[mc], taskfn)
                    continue

                if self.rq.check_stamp_task(tid, taskname + "_setscene", cache=self.stampcache):
                    logger.debug(2, 'Setscene stamp current for task %s', tid)
                    stamppresent.append(tid)
                    self.task_skip(tid)
                    continue

                if self.rq.check_stamp_task(tid, taskname, recurse = True, cache=self.stampcache):
                    logger.debug(2, 'Normal stamp current for task %s', tid)
                    stamppresent.append(tid)
                    self.task_skip(tid)
                    continue

                sq_fn.append(fn)
                sq_hashfn.append(self.rqdata.dataCaches[mc].hashfn[taskfn])
                sq_hash.append(self.rqdata.runtaskentries[tid].hash)
                sq_taskname.append(taskname)
                sq_task.append(tid)
            call = self.rq.hashvalidate + "(sq_fn, sq_task, sq_hash, sq_hashfn, d)"
            locs = { "sq_fn" : sq_fn, "sq_task" : sq_taskname, "sq_hash" : sq_hash, "sq_hashfn" : sq_hashfn, "d" : self.cooker.data }
            valid = bb.utils.better_eval(call, locs)

            valid_new = stamppresent
            for v in valid:
                valid_new.append(sq_task[v])

            for tid in self.sq_revdeps:
                if tid not in valid_new and tid not in noexec:
                    logger.debug(2, 'No package found, so skipping setscene task %s', tid)
                    self.outrightfail.append(tid)

        logger.info('Executing SetScene Tasks')

        self.rq.state = runQueueSceneRun

    def scenequeue_updatecounters(self, task, fail = False):
        for dep in self.sq_deps[task]:
            if fail and task in self.sq_harddeps and dep in self.sq_harddeps[task]:
                logger.debug(2, "%s was unavailable and is a hard dependency of %s so skipping" % (task, dep))
                self.scenequeue_updatecounters(dep, fail)
                continue
            if task not in self.sq_revdeps2[dep]:
                # May already have been removed by the fail case above
                continue
            self.sq_revdeps2[dep].remove(task)
            if len(self.sq_revdeps2[dep]) == 0:
                self.runq_buildable.add(dep)

    def task_completeoutright(self, task):
        """
        Mark a task as completed
        Look at the reverse dependencies and mark any task with
        completed dependencies as buildable
        """

        logger.debug(1, 'Found task %s which could be accelerated', task)
        self.scenequeue_covered.add(task)
        self.scenequeue_updatecounters(task)

    def check_taskfail(self, task):
        if self.rqdata.setscenewhitelist is not None:
            realtask = task.split('_setscene')[0]
            (mc, fn, taskname, taskfn) = split_tid_mcfn(realtask)
            pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
            if not check_setscene_enforce_whitelist(pn, taskname, self.rqdata.setscenewhitelist):
                logger.error('Task %s.%s failed' % (pn, taskname + "_setscene"))
                self.rq.state = runQueueCleanUp

    def task_complete(self, task):
        self.stats.taskCompleted()
        bb.event.fire(sceneQueueTaskCompleted(task, self.stats, self.rq), self.cfgData)
        self.task_completeoutright(task)

    def task_fail(self, task, result):
        self.stats.taskFailed()
        bb.event.fire(sceneQueueTaskFailed(task, self.stats, result, self), self.cfgData)
        self.scenequeue_notcovered.add(task)
        self.scenequeue_updatecounters(task, True)
        self.check_taskfail(task)

    def task_failoutright(self, task):
        self.runq_running.add(task)
        self.runq_buildable.add(task)
        self.stats.taskCompleted()
        self.stats.taskSkipped()
        self.scenequeue_notcovered.add(task)
        self.scenequeue_updatecounters(task, True)

    def task_skip(self, task):
        self.runq_running.add(task)
        self.runq_buildable.add(task)
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
            for nexttask in self.rqdata.runq_setscene_tids:
                if nexttask in self.runq_buildable and nexttask not in self.runq_running and self.stamps[nexttask] not in self.build_stamps.values():
                    if nexttask in self.unskippable:
                        logger.debug(2, "Setscene task %s is unskippable" % nexttask)
                    if nexttask not in self.unskippable and len(self.sq_revdeps[nexttask]) > 0 and self.sq_revdeps[nexttask].issubset(self.scenequeue_covered) and self.check_dependencies(nexttask, self.sq_revdeps[nexttask], True):
                        fn = fn_from_tid(nexttask)
                        foundtarget = False

                        if nexttask in self.rqdata.target_tids:
                            foundtarget = True
                        if not foundtarget:
                            logger.debug(2, "Skipping setscene for task %s" % nexttask)
                            self.task_skip(nexttask)
                            self.scenequeue_notneeded.add(nexttask)
                            return True
                    if nexttask in self.outrightfail:
                        self.task_failoutright(nexttask)
                        return True
                    task = nexttask
                    break
        if task is not None:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(task)
            taskname = taskname + "_setscene"
            if self.rq.check_stamp_task(task, taskname_from_tid(task), recurse = True, cache=self.stampcache):
                logger.debug(2, 'Stamp for underlying task %s is current, so skipping setscene variant', task)
                self.task_failoutright(task)
                return True

            if self.cooker.configuration.force:
                if task in self.rqdata.target_tids:
                    self.task_failoutright(task)
                    return True

            if self.rq.check_stamp_task(task, taskname, cache=self.stampcache):
                logger.debug(2, 'Setscene stamp current task %s, so skip it and its dependencies', task)
                self.task_skip(task)
                return True

            startevent = sceneQueueTaskStarted(task, self.stats, self.rq)
            bb.event.fire(startevent, self.cfgData)

            taskdepdata = self.build_taskdepdata(task)

            taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
            if 'fakeroot' in taskdep and taskname in taskdep['fakeroot'] and not self.cooker.configuration.dry_run:
                if not mc in self.rq.fakeworker:
                    self.rq.start_fakeworker(self, mc)
                self.rq.fakeworker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, True, self.cooker.collection.get_file_appends(taskfn), taskdepdata, False)) + b"</runtask>")
                self.rq.fakeworker[mc].process.stdin.flush()
            else:
                self.rq.worker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, True, self.cooker.collection.get_file_appends(taskfn), taskdepdata, False)) + b"</runtask>")
                self.rq.worker[mc].process.stdin.flush()

            self.build_stamps[task] = bb.build.stampfile(taskname, self.rqdata.dataCaches[mc], taskfn, noextra=True)
            self.build_stamps2.append(self.build_stamps[task])
            self.runq_running.add(task)
            self.stats.taskActive()
            if self.stats.active < self.number_tasks:
                return True

        if self.stats.active > 0:
            self.rq.read_workers()
            return self.rq.active_fds()

        #for tid in self.sq_revdeps:
        #    if tid not in self.runq_running:
        #        buildable = tid in self.runq_buildable
        #        revdeps = self.sq_revdeps[tid]
        #        bb.warn("Found we didn't run %s %s %s" % (tid, buildable, str(revdeps)))

        self.rq.scenequeue_covered = self.scenequeue_covered
        self.rq.scenequeue_notcovered = self.scenequeue_notcovered

        logger.debug(1, 'We can skip tasks %s', "\n".join(sorted(self.rq.scenequeue_covered)))

        self.rq.state = runQueueRunInit

        completeevent = sceneQueueComplete(self.stats, self.rq)
        bb.event.fire(completeevent, self.cfgData)

        return True

    def runqueue_process_waitpid(self, task, status):
        RunQueueExecute.runqueue_process_waitpid(self, task, status)


    def build_taskdepdata(self, task):
        def getsetscenedeps(tid):
            deps = set()
            (mc, fn, taskname, _) = split_tid_mcfn(tid)
            realtid = tid + "_setscene"
            idepends = self.rqdata.taskData[mc].taskentries[realtid].idepends
            for (depname, idependtask) in idepends:
                if depname not in self.rqdata.taskData[mc].build_targets:
                    continue

                depfn = self.rqdata.taskData[mc].build_targets[depname][0]
                if depfn is None:
                     continue
                deptid = depfn + ":" + idependtask.replace("_setscene", "")
                deps.add(deptid)
            return deps

        taskdepdata = {}
        next = getsetscenedeps(task)
        next.add(task)
        while next:
            additional = []
            for revdep in next:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(revdep)
                pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
                deps = getsetscenedeps(revdep)
                provides = self.rqdata.dataCaches[mc].fn_provides[taskfn]
                taskhash = self.rqdata.runtaskentries[revdep].hash
                taskdepdata[revdep] = [pn, taskname, fn, deps, provides, taskhash]
                for revdep2 in deps:
                    if revdep2 not in taskdepdata:
                        additional.append(revdep2)
            next = additional

        #bb.note("Task %s: " % task + str(taskdepdata).replace("], ", "],\n"))
        return taskdepdata

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
        self.taskstring = task
        self.taskname = taskname_from_tid(task)
        self.taskfile = fn_from_tid(task)
        self.taskhash = rq.rqdata.get_task_hash(task)
        self.stats = stats.copy()
        bb.event.Event.__init__(self)

class sceneQueueEvent(runQueueEvent):
    """
    Base sceneQueue event class
    """
    def __init__(self, task, stats, rq, noexec=False):
        runQueueEvent.__init__(self, task, stats, rq)
        self.taskstring = task + "_setscene"
        self.taskname = taskname_from_tid(task) + "_setscene"
        self.taskfile = fn_from_tid(task)
        self.taskhash = rq.rqdata.get_task_hash(task)

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

    def __str__(self):
        return "Task (%s) failed with exit code '%s'" % (self.taskstring, self.exitcode)

class sceneQueueTaskFailed(sceneQueueEvent):
    """
    Event notifying a setscene task failed
    """
    def __init__(self, task, stats, exitcode, rq):
        sceneQueueEvent.__init__(self, task, stats, rq)
        self.exitcode = exitcode

    def __str__(self):
        return "Setscene task (%s) failed with exit code '%s' - real task will be run instead" % (self.taskstring, self.exitcode)

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
        self.queue = b""
        self.d = d
        self.rq = rq
        self.rqexec = rqexec

    def setrunqueueexec(self, rqexec):
        self.rqexec = rqexec

    def read(self):
        for workers, name in [(self.rq.worker, "Worker"), (self.rq.fakeworker, "Fakeroot")]:
            for worker in workers.values():
                worker.process.poll()
                if worker.process.returncode is not None and not self.rq.teardown:
                    bb.error("%s process (%s) exited unexpectedly (%s), shutting down..." % (name, worker.process.pid, str(worker.process.returncode)))
                    self.rq.finish_runqueue(True)

        start = len(self.queue)
        try:
            self.queue = self.queue + (self.input.read(102400) or b"")
        except (OSError, IOError) as e:
            if e.errno != errno.EAGAIN:
                raise
        end = len(self.queue)
        found = True
        while found and len(self.queue):
            found = False
            index = self.queue.find(b"</event>")
            while index != -1 and self.queue.startswith(b"<event>"):
                try:
                    event = pickle.loads(self.queue[7:index])
                except ValueError as e:
                    bb.msg.fatal("RunQueue", "failed load pickle '%s': '%s'" % (e, self.queue[7:index]))
                bb.event.fire_from_worker(event, self.d)
                found = True
                self.queue = self.queue[index+8:]
                index = self.queue.find(b"</event>")
            index = self.queue.find(b"</exitcode>")
            while index != -1 and self.queue.startswith(b"<exitcode>"):
                try:
                    task, status = pickle.loads(self.queue[10:index])
                except ValueError as e:
                    bb.msg.fatal("RunQueue", "failed load pickle '%s': '%s'" % (e, self.queue[10:index]))
                self.rqexec.runqueue_process_waitpid(task, status)
                found = True
                self.queue = self.queue[index+11:]
                index = self.queue.find(b"</exitcode>")
        return (end > start)

    def close(self):
        while self.read():
            continue
        if len(self.queue) > 0:
            print("Warning, worker left partial message: %s" % self.queue)
        self.input.close()

def get_setscene_enforce_whitelist(d):
    if d.getVar('BB_SETSCENE_ENFORCE') != '1':
        return None
    whitelist = (d.getVar("BB_SETSCENE_ENFORCE_WHITELIST") or "").split()
    outlist = []
    for item in whitelist[:]:
        if item.startswith('%:'):
            for target in sys.argv[1:]:
                if not target.startswith('-'):
                    outlist.append(target.split(':')[0] + ':' + item.split(':')[1])
        else:
            outlist.append(item)
    return outlist

def check_setscene_enforce_whitelist(pn, taskname, whitelist):
    import fnmatch
    if whitelist is not None:
        item = '%s:%s' % (pn, taskname)
        for whitelist_item in whitelist:
            if fnmatch.fnmatch(item, whitelist_item):
                return True
        return False
    return True
