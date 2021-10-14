"""
BitBake 'RunQueue' implementation

Handles preparation and execution of a queue of tasks
"""

# Copyright (C) 2006-2007  Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import copy
import os
import sys
import stat
import errno
import logging
import re
import bb
from bb import msg, event
from bb import monitordisk
import subprocess
import pickle
from multiprocessing import Process
import shlex
import pprint

bblogger = logging.getLogger("BitBake")
logger = logging.getLogger("BitBake.RunQueue")
hashequiv_logger = logging.getLogger("BitBake.RunQueue.HashEquiv")

__find_sha256__ = re.compile( r'(?i)(?<![a-z0-9])[a-f0-9]{64}(?![a-z0-9])' )

def fn_from_tid(tid):
     return tid.rsplit(":", 1)[0]

def taskname_from_tid(tid):
    return tid.rsplit(":", 1)[1]

def mc_from_tid(tid):
    if tid.startswith('mc:') and tid.count(':') >= 2:
        return tid.split(':')[1]
    return ""

def split_tid(tid):
    (mc, fn, taskname, _) = split_tid_mcfn(tid)
    return (mc, fn, taskname)

def split_mc(n):
    if n.startswith("mc:") and n.count(':') >= 2:
        _, mc, n = n.split(":", 2)
        return (mc, n)
    return ('', n)

def split_tid_mcfn(tid):
    if tid.startswith('mc:') and tid.count(':') >= 2:
        elems = tid.split(':')
        mc = elems[1]
        fn = ":".join(elems[2:-1])
        taskname = elems[-1]
        mcfn = "mc:" + mc + ":" + fn
    else:
        tid = tid.rsplit(":", 1)
        mc = ""
        fn = tid[0]
        taskname = tid[1]
        mcfn = fn

    return (mc, fn, taskname, mcfn)

def build_tid(mc, fn, taskname):
    if mc:
        return "mc:" + mc + ":" + fn + ":" + taskname
    return fn + ":" + taskname

# Index used to pair up potentially matching multiconfig tasks
# We match on PN, taskname and hash being equal
def pending_hash_index(tid, rqdata):
    (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
    pn = rqdata.dataCaches[mc].pkg_fn[taskfn]
    h = rqdata.runtaskentries[tid].unihash
    return pn + ":" + "taskname" + h

class RunQueueStats:
    """
    Holds statistics on the tasks handled by the associated runQueue
    """
    def __init__(self, total, setscene_total):
        self.completed = 0
        self.skipped = 0
        self.failed = 0
        self.active = 0
        self.setscene_active = 0
        self.setscene_covered = 0
        self.setscene_notcovered = 0
        self.setscene_total = setscene_total
        self.total = total

    def copy(self):
        obj = self.__class__(self.total, self.setscene_total)
        obj.__dict__.update(self.__dict__)
        return obj

    def taskFailed(self):
        self.active = self.active - 1
        self.failed = self.failed + 1

    def taskCompleted(self):
        self.active = self.active - 1
        self.completed = self.completed + 1

    def taskSkipped(self):
        self.active = self.active + 1
        self.skipped = self.skipped + 1

    def taskActive(self):
        self.active = self.active + 1

    def updateCovered(self, covered, notcovered):
        self.setscene_covered = covered
        self.setscene_notcovered = notcovered

    def updateActiveSetscene(self, active):
        self.setscene_active = active

# These values indicate the next step due to be run in the
# runQueue state machine
runQueuePrepare = 2
runQueueSceneInit = 3
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

        self.buildable = set()
        self.skip_maxthread = {}
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
        # Once tasks are running we don't need to worry about them again
        self.buildable.difference_update(self.rq.runq_running)
        buildable = set(self.buildable)
        buildable.difference_update(self.rq.holdoff_tasks)
        buildable.intersection_update(self.rq.tasks_covered | self.rq.tasks_notcovered)
        if not buildable:
            return None

        # Filter out tasks that have a max number of threads that have been exceeded
        skip_buildable = {}
        for running in self.rq.runq_running.difference(self.rq.runq_complete):
            rtaskname = taskname_from_tid(running)
            if rtaskname not in self.skip_maxthread:
                self.skip_maxthread[rtaskname] = self.rq.cfgData.getVarFlag(rtaskname, "number_threads")
            if not self.skip_maxthread[rtaskname]:
                continue
            if rtaskname in skip_buildable:
                skip_buildable[rtaskname] += 1
            else:
                skip_buildable[rtaskname] = 1

        if len(buildable) == 1:
            tid = buildable.pop()
            taskname = taskname_from_tid(tid)
            if taskname in skip_buildable and skip_buildable[taskname] >= int(self.skip_maxthread[taskname]):
                return None
            stamp = self.stamps[tid]
            if stamp not in self.rq.build_stamps.values():
                return tid

        if not self.rev_prio_map:
            self.rev_prio_map = {}
            for tid in self.rqdata.runtaskentries:
                self.rev_prio_map[tid] = self.prio_map.index(tid)

        best = None
        bestprio = None
        for tid in buildable:
            taskname = taskname_from_tid(tid)
            if taskname in skip_buildable and skip_buildable[taskname] >= int(self.skip_maxthread[taskname]):
                continue
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
        if self.rq.can_start_task():
            return self.next_buildable_task()

    def newbuildable(self, task):
        self.buildable.add(task)

    def removebuildable(self, task):
        self.buildable.remove(task)

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
        self.unihash = None
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
        self.setscenewhitelist = get_setscene_enforce_whitelist(cfgData, targets)
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

    def get_task_unihash(self, tid):
        return self.runtaskentries[tid].unihash

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

        class TooManyLoops(Exception):
            pass

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
                        raise TooManyLoops
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

        try:
            for task in tasks:
                find_chains(task, [])
        except TooManyLoops:
            pass

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
                logger.debug2("Task %s is not buildable", tid)
                logger.debug2("(Complete marker was %s and the remaining dependency count was %s)\n", task_done[tid], deps_left[tid])
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

        def add_mc_dependencies(mc, tid):
            mcdeps = taskData[mc].get_mcdepends()
            for dep in mcdeps:
                mcdependency = dep.split(':')
                pn = mcdependency[3]
                frommc = mcdependency[1]
                mcdep = mcdependency[2]
                deptask = mcdependency[4]
                if mc == frommc:
                    fn = taskData[mcdep].build_targets[pn][0]
                    newdep = '%s:%s' % (fn,deptask)
                    taskData[mc].taskentries[tid].tdepends.append(newdep)

        for mc in taskData:
            for tid in taskData[mc].taskentries:

                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                #runtid = build_tid(mc, fn, taskname)

                #logger.debug2("Processing %s,%s:%s", mc, fn, taskname)

                depends = set()
                task_deps = self.dataCaches[mc].task_deps[taskfn]

                self.runtaskentries[tid] = RunTaskEntry()

                if fn in taskData[mc].failed_fns:
                    continue

                # We add multiconfig dependencies before processing internal task deps (tdepends)
                if 'mcdepends' in task_deps and taskname in task_deps['mcdepends']:
                    add_mc_dependencies(mc, tid)

                # Resolve task internal dependencies
                #
                # e.g. addtask before X after Y
                for t in taskData[mc].taskentries[tid].tdepends:
                    (depmc, depfn, deptaskname, _) = split_tid_mcfn(t)
                    depends.add(build_tid(depmc, depfn, deptaskname))

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
                bb.parse.siggen.invalidate_task(taskname, self.dataCaches[mc], taskfn)

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
                if not task.startswith("do_"):
                    task = "do_{0}".format(task)
                runall_tids = set()
                for tid in list(self.runtaskentries):
                    wanttid = "{0}:{1}".format(fn_from_tid(tid), task)
                    if wanttid in delcount:
                        self.runtaskentries[wanttid] = delcount[wanttid]
                    if wanttid in self.runtaskentries:
                        runall_tids.add(wanttid)

                for tid in list(runall_tids):
                    mark_active(tid,1)
                    if self.cooker.configuration.force:
                        invalidate_task(tid, False)

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
                if not task.startswith("do_"):
                    task = "do_{0}".format(task)
                runonly_tids = { k: v for k, v in self.runtaskentries.items() if taskname_from_tid(k) == task }

                for tid in list(runonly_tids):
                    mark_active(tid,1)
                    if self.cooker.configuration.force:
                        invalidate_task(tid, False)

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
                    logger.verbnote(msg)
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
        self.runq_setscene_tids = set()
        if not self.cooker.configuration.nosetscene:
            for tid in self.runtaskentries:
                (mc, fn, taskname, _) = split_tid_mcfn(tid)
                setscenetid = tid + "_setscene"
                if setscenetid not in taskData[mc].taskentries:
                    continue
                self.runq_setscene_tids.add(tid)

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

        bb.parse.siggen.set_setscene_tasks(self.runq_setscene_tids)

        # Iterate over the task list and call into the siggen code
        dealtwith = set()
        todeal = set(self.runtaskentries)
        while len(todeal) > 0:
            for tid in todeal.copy():
                if len(self.runtaskentries[tid].depends - dealtwith) == 0:
                    dealtwith.add(tid)
                    todeal.remove(tid)
                    self.prepare_task_hash(tid)

        bb.parse.siggen.writeout_file_checksum_cache()

        #self.dump_data()
        return len(self.runtaskentries)

    def prepare_task_hash(self, tid):
        dc = bb.parse.siggen.get_data_caches(self.dataCaches, mc_from_tid(tid))
        bb.parse.siggen.prep_taskhash(tid, self.runtaskentries[tid].depends, dc)
        self.runtaskentries[tid].hash = bb.parse.siggen.get_taskhash(tid, self.runtaskentries[tid].depends, dc)
        self.runtaskentries[tid].unihash = bb.parse.siggen.get_unihash(tid)

    def dump_data(self):
        """
        Dump some debug information on the internal data structures
        """
        logger.debug3("run_tasks:")
        for tid in self.runtaskentries:
            logger.debug3(" %s: %s   Deps %s RevDeps %s", tid,
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
        self.depvalidate = cfgData.getVar("BB_SETSCENE_DEPVALID") or None

        self.state = runQueuePrepare

        # For disk space monitor
        # Invoked at regular time intervals via the bitbake heartbeat event
        # while the build is running. We generate a unique name for the handler
        # here, just in case that there ever is more than one RunQueue instance,
        # start the handler when reaching runQueueSceneInit, and stop it when
        # done with the build.
        self.dm = monitordisk.diskMonitor(cfgData)
        self.dm_event_handler_name = '_bb_diskmonitor_' + str(id(self))
        self.dm_event_handler_registered = False
        self.rqexe = None
        self.worker = {}
        self.fakeworker = {}

    def _start_worker(self, mc, fakeroot = False, rqexec = None):
        logger.debug("Starting bitbake-worker")
        magic = "decafbad"
        if self.cooker.configuration.profile:
            magic = "decafbadbad"
        fakerootlogs = None
        if fakeroot:
            magic = magic + "beef"
            mcdata = self.cooker.databuilder.mcdata[mc]
            fakerootcmd = shlex.split(mcdata.getVar("FAKEROOTCMD"))
            fakerootenv = (mcdata.getVar("FAKEROOTBASEENV") or "").split()
            env = os.environ.copy()
            for key, value in (var.split('=') for var in fakerootenv):
                env[key] = value
            worker = subprocess.Popen(fakerootcmd + ["bitbake-worker", magic], stdout=subprocess.PIPE, stdin=subprocess.PIPE, env=env)
            fakerootlogs = self.rqdata.dataCaches[mc].fakerootlogs
        else:
            worker = subprocess.Popen(["bitbake-worker", magic], stdout=subprocess.PIPE, stdin=subprocess.PIPE)
        bb.utils.nonblockingfd(worker.stdout)
        workerpipe = runQueuePipe(worker.stdout, None, self.cfgData, self, rqexec, fakerootlogs=fakerootlogs)

        workerdata = {
            "taskdeps" : self.rqdata.dataCaches[mc].task_deps,
            "fakerootenv" : self.rqdata.dataCaches[mc].fakerootenv,
            "fakerootdirs" : self.rqdata.dataCaches[mc].fakerootdirs,
            "fakerootnoenv" : self.rqdata.dataCaches[mc].fakerootnoenv,
            "sigdata" : bb.parse.siggen.get_taskdata(),
            "logdefaultlevel" : bb.msg.loggerDefaultLogLevel,
            "build_verbose_shell" : self.cooker.configuration.build_verbose_shell,
            "build_verbose_stdout" : self.cooker.configuration.build_verbose_stdout,
            "logdefaultdomain" : bb.msg.loggerDefaultDomains,
            "prhost" : self.cooker.prhost,
            "buildname" : self.cfgData.getVar("BUILDNAME"),
            "date" : self.cfgData.getVar("DATE"),
            "time" : self.cfgData.getVar("TIME"),
            "hashservaddr" : self.cooker.hashservaddr,
            "umask" : self.cfgData.getVar("BB_DEFAULT_UMASK"),
        }

        worker.stdin.write(b"<cookerconfig>" + pickle.dumps(self.cooker.configuration) + b"</cookerconfig>")
        worker.stdin.write(b"<extraconfigdata>" + pickle.dumps(self.cooker.extraconfigdata) + b"</extraconfigdata>")
        worker.stdin.write(b"<workerdata>" + pickle.dumps(workerdata) + b"</workerdata>")
        worker.stdin.flush()

        return RunQueueWorker(worker, workerpipe)

    def _teardown_worker(self, worker):
        if not worker:
            return
        logger.debug("Teardown for bitbake-worker")
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
            logger.debug2("Stampfile %s not available", stampfile)
            return False
        # If it's a 'nostamp' task, it's not current
        taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
        if 'nostamp' in taskdep and taskname in taskdep['nostamp']:
            logger.debug2("%s.%s is nostamp\n", fn, taskname)
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
                        logger.debug2('Stampfile %s does not exist', stampfile2)
                        iscurrent = False
                        break
                    if t1 < t2:
                        logger.debug2('Stampfile %s < %s', stampfile, stampfile2)
                        iscurrent = False
                        break
                    if recurse and iscurrent:
                        if dep in cache:
                            iscurrent = cache[dep]
                            if not iscurrent:
                                logger.debug2('Stampfile for dependency %s:%s invalid (cached)' % (fn2, taskname2))
                        else:
                            iscurrent = self.check_stamp_task(dep, recurse=True, cache=cache)
                            cache[dep] = iscurrent
        if recurse:
            cache[tid] = iscurrent
        return iscurrent

    def validate_hashes(self, tocheck, data, currentcount=0, siginfo=False, summary=True):
        valid = set()
        if self.hashvalidate:
            sq_data = {}
            sq_data['hash'] = {}
            sq_data['hashfn'] = {}
            sq_data['unihash'] = {}
            for tid in tocheck:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
                sq_data['hash'][tid] = self.rqdata.runtaskentries[tid].hash
                sq_data['hashfn'][tid] = self.rqdata.dataCaches[mc].hashfn[taskfn]
                sq_data['unihash'][tid] = self.rqdata.runtaskentries[tid].unihash

            valid = self.validate_hash(sq_data, data, siginfo, currentcount, summary)

        return valid

    def validate_hash(self, sq_data, d, siginfo, currentcount, summary):
        locs = {"sq_data" : sq_data, "d" : d, "siginfo" : siginfo, "currentcount" : currentcount, "summary" : summary}

        # Metadata has **kwargs so args can be added, sq_data can also gain new fields
        call = self.hashvalidate + "(sq_data, d, siginfo=siginfo, currentcount=currentcount, summary=summary)"

        return bb.utils.better_eval(call, locs)

    def _execute_runqueue(self):
        """
        Run the tasks in a queue prepared by rqdata.prepare()
        Upon failure, optionally try to recover the build using any alternate providers
        (if the abort on failure configuration option isn't set)
        """

        retval = True

        if self.state is runQueuePrepare:
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
                bb.parse.siggen.save_unitaskhashes()

        if self.state is runQueueSceneInit:
            self.rqdata.init_progress_reporter.next_stage()

            # we are ready to run,  emit dependency info to any UI or class which
            # needs it
            depgraph = self.cooker.buildDependTree(self, self.rqdata.taskData)
            self.rqdata.init_progress_reporter.next_stage()
            bb.event.fire(bb.event.DepTreeGenerated(depgraph), self.cooker.data)

            if not self.dm_event_handler_registered:
                 res = bb.event.register(self.dm_event_handler_name,
                                         lambda x: self.dm.check(self) if self.state in [runQueueRunning, runQueueCleanUp] else False,
                                         ('bb.event.HeartbeatEvent',), data=self.cfgData)
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

        if self.state is runQueueSceneInit:
            self.rqdata.init_progress_reporter.next_stage()
            self.start_worker()
            self.rqdata.init_progress_reporter.next_stage()
            self.rqexe = RunQueueExecute(self)

            # If we don't have any setscene functions, skip execution
            if len(self.rqdata.runq_setscene_tids) == 0:
                logger.info('No setscene tasks')
                for tid in self.rqdata.runtaskentries:
                    if len(self.rqdata.runtaskentries[tid].depends) == 0:
                        self.rqexe.setbuildable(tid)
                    self.rqexe.tasks_notcovered.add(tid)
                self.rqexe.sqdone = True
            logger.info('Executing Tasks')
            self.state = runQueueRunning

        if self.state is runQueueRunning:
            retval = self.rqexe.execute()

        if self.state is runQueueCleanUp:
            retval = self.rqexe.finish()

        build_done = self.state is runQueueComplete or self.state is runQueueFailed

        if build_done and self.dm_event_handler_registered:
            bb.event.remove(self.dm_event_handler_name, None, data=self.cfgData)
            self.dm_event_handler_registered = False

        if build_done and self.rqexe:
            bb.parse.siggen.save_unitaskhashes()
            self.teardown_workers()
            if self.rqexe:
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
        mc = bb.runqueue.mc_from_tid(fn)
        the_data = bb_cache.loadDataFull(fn, self.cooker.collections[mc].get_file_appends(fn))
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

        noexec = []
        tocheck = set()

        for tid in self.rqdata.runtaskentries:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]

            if 'noexec' in taskdep and taskname in taskdep['noexec']:
                noexec.append(tid)
                continue

            tocheck.add(tid)

        valid_new = self.validate_hashes(tocheck, self.cooker.data, 0, True, summary=False)

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
                recout.extend(list('    ' + l for l in out2))
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
                prevh = __find_sha256__.search(latestmatch).group(0)
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

        self.sq_buildable = set()
        self.sq_running = set()
        self.sq_live = set()

        self.updated_taskhash_queue = []
        self.pending_migrations = set()

        self.runq_buildable = set()
        self.runq_running = set()
        self.runq_complete = set()
        self.runq_tasksrun = set()

        self.build_stamps = {}
        self.build_stamps2 = []
        self.failed_tids = []
        self.sq_deferred = {}

        self.stampcache = {}

        self.holdoff_tasks = set()
        self.holdoff_need_update = True
        self.sqdone = False

        self.stats = RunQueueStats(len(self.rqdata.runtaskentries), len(self.rqdata.runq_setscene_tids))

        for mc in rq.worker:
            rq.worker[mc].pipe.setrunqueueexec(self)
        for mc in rq.fakeworker:
            rq.fakeworker[mc].pipe.setrunqueueexec(self)

        if self.number_tasks <= 0:
             bb.fatal("Invalid BB_NUMBER_THREADS %s" % self.number_tasks)

        # List of setscene tasks which we've covered
        self.scenequeue_covered = set()
        # List of tasks which are covered (including setscene ones)
        self.tasks_covered = set()
        self.tasks_scenequeue_done = set()
        self.scenequeue_notcovered = set()
        self.tasks_notcovered = set()
        self.scenequeue_notneeded = set()

        # We can't skip specified target tasks which aren't setscene tasks
        self.cantskip = set(self.rqdata.target_tids)
        self.cantskip.difference_update(self.rqdata.runq_setscene_tids)
        self.cantskip.intersection_update(self.rqdata.runtaskentries)

        schedulers = self.get_schedulers()
        for scheduler in schedulers:
            if self.scheduler == scheduler.name:
                self.sched = scheduler(self, self.rqdata)
                logger.debug("Using runqueue scheduler '%s'", scheduler.name)
                break
        else:
            bb.fatal("Invalid scheduler '%s'.  Available schedulers: %s" %
                     (self.scheduler, ", ".join(obj.name for obj in schedulers)))

        #if len(self.rqdata.runq_setscene_tids) > 0:
        self.sqdata = SQData()
        build_scenequeue_data(self.sqdata, self.rqdata, self.rq, self.cooker, self.stampcache, self)

    def runqueue_process_waitpid(self, task, status, fakerootlog=None):

        # self.build_stamps[pid] may not exist when use shared work directory.
        if task in self.build_stamps:
            self.build_stamps2.remove(self.build_stamps[task])
            del self.build_stamps[task]

        if task in self.sq_live:
            if status != 0:
                self.sq_task_fail(task, status)
            else:
                self.sq_task_complete(task)
            self.sq_live.remove(task)
            self.stats.updateActiveSetscene(len(self.sq_live))
        else:
            if status != 0:
                self.task_fail(task, status, fakerootlog=fakerootlog)
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

        active = self.stats.active + len(self.sq_live)
        if active > 0:
            bb.event.fire(runQueueExitWait(active), self.cfgData)
            self.rq.read_workers()
            return self.rq.active_fds()

        if len(self.failed_tids) != 0:
            self.rq.state = runQueueFailed
            return True

        self.rq.state = runQueueComplete
        return True

    # Used by setscene only
    def check_dependencies(self, task, taskdeps):
        if not self.rq.depvalidate:
            return False

        # Must not edit parent data
        taskdeps = set(taskdeps)

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

    def can_start_task(self):
        active = self.stats.active + len(self.sq_live)
        can_start = active < self.number_tasks
        return can_start

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
            alldeps = True
            for dep in self.rqdata.runtaskentries[revdep].depends:
                if dep not in self.runq_complete:
                    alldeps = False
                    break
            if alldeps:
                self.setbuildable(revdep)
                logger.debug("Marking task %s as buildable", revdep)

        for t in self.sq_deferred.copy():
            if self.sq_deferred[t] == task:
                logger.debug2("Deferred task %s now buildable" % t)
                del self.sq_deferred[t]
                update_scenequeue_data([t], self.sqdata, self.rqdata, self.rq, self.cooker, self.stampcache, self, summary=False)

    def task_complete(self, task):
        self.stats.taskCompleted()
        bb.event.fire(runQueueTaskCompleted(task, self.stats, self.rq), self.cfgData)
        self.task_completeoutright(task)
        self.runq_tasksrun.add(task)

    def task_fail(self, task, exitcode, fakerootlog=None):
        """
        Called when a task has failed
        Updates the state engine with the failure
        """
        self.stats.taskFailed()
        self.failed_tids.append(task)

        fakeroot_log = ""
        if fakerootlog and os.path.exists(fakerootlog):
            with open(fakerootlog) as fakeroot_log_file:
                fakeroot_failed = False
                for line in reversed(fakeroot_log_file.readlines()):
                    for fakeroot_error in ['mismatch', 'error', 'fatal']:
                        if fakeroot_error in line.lower():
                            fakeroot_failed = True
                    if 'doing new pid setup and server start' in line:
                        break
                    fakeroot_log = line + fakeroot_log

            if not fakeroot_failed:
                fakeroot_log = None

        bb.event.fire(runQueueTaskFailed(task, self.stats, exitcode, self.rq, fakeroot_log=fakeroot_log), self.cfgData)

        if self.rqdata.taskData[''].abort:
            self.rq.state = runQueueCleanUp

    def task_skip(self, task, reason):
        self.runq_running.add(task)
        self.setbuildable(task)
        bb.event.fire(runQueueTaskSkipped(task, self.stats, self.rq, reason), self.cfgData)
        self.task_completeoutright(task)
        self.stats.taskSkipped()
        self.stats.taskCompleted()

    def summarise_scenequeue_errors(self):
        err = False
        if not self.sqdone:
            logger.debug('We could skip tasks %s', "\n".join(sorted(self.scenequeue_covered)))
            completeevent = sceneQueueComplete(self.stats, self.rq)
            bb.event.fire(completeevent, self.cfgData)
        if self.sq_deferred:
            logger.error("Scenequeue had deferred entries: %s" % pprint.pformat(self.sq_deferred))
            err = True
        if self.updated_taskhash_queue:
            logger.error("Scenequeue had unprocessed changed taskhash entries: %s" % pprint.pformat(self.updated_taskhash_queue))
            err = True
        if self.holdoff_tasks:
            logger.error("Scenequeue had holdoff tasks: %s" % pprint.pformat(self.holdoff_tasks))
            err = True

        for tid in self.scenequeue_covered.intersection(self.scenequeue_notcovered):
            # No task should end up in both covered and uncovered, that is a bug.
            logger.error("Setscene task %s in both covered and notcovered." % tid)

        for tid in self.rqdata.runq_setscene_tids:
            if tid not in self.scenequeue_covered and tid not in self.scenequeue_notcovered:
                err = True
                logger.error("Setscene Task %s was never marked as covered or not covered" % tid)
            if tid not in self.sq_buildable:
                err = True
                logger.error("Setscene Task %s was never marked as buildable" % tid)
            if tid not in self.sq_running:
                err = True
                logger.error("Setscene Task %s was never marked as running" % tid)

        for x in self.rqdata.runtaskentries:
            if x not in self.tasks_covered and x not in self.tasks_notcovered:
                logger.error("Task %s was never moved from the setscene queue" % x)
                err = True
            if x not in self.tasks_scenequeue_done:
                logger.error("Task %s was never processed by the setscene code" % x)
                err = True
            if len(self.rqdata.runtaskentries[x].depends) == 0 and x not in self.runq_buildable:
                logger.error("Task %s was never marked as buildable by the setscene code" % x)
                err = True
        return err


    def execute(self):
        """
        Run the tasks in a queue prepared by prepare_runqueue
        """

        self.rq.read_workers()
        if self.updated_taskhash_queue or self.pending_migrations:
            self.process_possible_migrations()

        if not hasattr(self, "sorted_setscene_tids"):
            # Don't want to sort this set every execution
            self.sorted_setscene_tids = sorted(self.rqdata.runq_setscene_tids)

        task = None
        if not self.sqdone and self.can_start_task():
            # Find the next setscene to run
            for nexttask in self.sorted_setscene_tids:
                if nexttask in self.sq_buildable and nexttask not in self.sq_running and self.sqdata.stamps[nexttask] not in self.build_stamps.values():
                    if nexttask not in self.sqdata.unskippable and len(self.sqdata.sq_revdeps[nexttask]) > 0 and self.sqdata.sq_revdeps[nexttask].issubset(self.scenequeue_covered) and self.check_dependencies(nexttask, self.sqdata.sq_revdeps[nexttask]):
                        if nexttask not in self.rqdata.target_tids:
                            logger.debug2("Skipping setscene for task %s" % nexttask)
                            self.sq_task_skip(nexttask)
                            self.scenequeue_notneeded.add(nexttask)
                            if nexttask in self.sq_deferred:
                                del self.sq_deferred[nexttask]
                            return True
                    # If covered tasks are running, need to wait for them to complete
                    for t in self.sqdata.sq_covered_tasks[nexttask]:
                        if t in self.runq_running and t not in self.runq_complete:
                            continue
                    if nexttask in self.sq_deferred:
                        if self.sq_deferred[nexttask] not in self.runq_complete:
                            continue
                        logger.debug("Task %s no longer deferred" % nexttask)
                        del self.sq_deferred[nexttask]
                        valid = self.rq.validate_hashes(set([nexttask]), self.cooker.data, 0, False, summary=False)
                        if not valid:
                            logger.debug("%s didn't become valid, skipping setscene" % nexttask)
                            self.sq_task_failoutright(nexttask)
                            return True
                    if nexttask in self.sqdata.outrightfail:
                        logger.debug2('No package found, so skipping setscene task %s', nexttask)
                        self.sq_task_failoutright(nexttask)
                        return True
                    if nexttask in self.sqdata.unskippable:
                        logger.debug2("Setscene task %s is unskippable" % nexttask)
                    task = nexttask
                    break
        if task is not None:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(task)
            taskname = taskname + "_setscene"
            if self.rq.check_stamp_task(task, taskname_from_tid(task), recurse = True, cache=self.stampcache):
                logger.debug2('Stamp for underlying task %s is current, so skipping setscene variant', task)
                self.sq_task_failoutright(task)
                return True

            if self.cooker.configuration.force:
                if task in self.rqdata.target_tids:
                    self.sq_task_failoutright(task)
                    return True

            if self.rq.check_stamp_task(task, taskname, cache=self.stampcache):
                logger.debug2('Setscene stamp current task %s, so skip it and its dependencies', task)
                self.sq_task_skip(task)
                return True

            if self.cooker.configuration.skipsetscene:
                logger.debug2('No setscene tasks should be executed. Skipping %s', task)
                self.sq_task_failoutright(task)
                return True

            startevent = sceneQueueTaskStarted(task, self.stats, self.rq)
            bb.event.fire(startevent, self.cfgData)

            taskdepdata = self.sq_build_taskdepdata(task)

            taskdep = self.rqdata.dataCaches[mc].task_deps[taskfn]
            taskhash = self.rqdata.get_task_hash(task)
            unihash = self.rqdata.get_task_unihash(task)
            if 'fakeroot' in taskdep and taskname in taskdep['fakeroot'] and not self.cooker.configuration.dry_run:
                if not mc in self.rq.fakeworker:
                    self.rq.start_fakeworker(self, mc)
                self.rq.fakeworker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, taskhash, unihash, True, self.cooker.collections[mc].get_file_appends(taskfn), taskdepdata, False)) + b"</runtask>")
                self.rq.fakeworker[mc].process.stdin.flush()
            else:
                self.rq.worker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, taskhash, unihash, True, self.cooker.collections[mc].get_file_appends(taskfn), taskdepdata, False)) + b"</runtask>")
                self.rq.worker[mc].process.stdin.flush()

            self.build_stamps[task] = bb.build.stampfile(taskname, self.rqdata.dataCaches[mc], taskfn, noextra=True)
            self.build_stamps2.append(self.build_stamps[task])
            self.sq_running.add(task)
            self.sq_live.add(task)
            self.stats.updateActiveSetscene(len(self.sq_live))
            if self.can_start_task():
                return True

        self.update_holdofftasks()

        if not self.sq_live and not self.sqdone and not self.sq_deferred and not self.updated_taskhash_queue and not self.holdoff_tasks:
            hashequiv_logger.verbose("Setscene tasks completed")

            err = self.summarise_scenequeue_errors()
            if err:
                self.rq.state = runQueueFailed
                return True

            if self.cooker.configuration.setsceneonly:
                self.rq.state = runQueueComplete
                return True
            self.sqdone = True

            if self.stats.total == 0:
                # nothing to do
                self.rq.state = runQueueComplete
                return True

        if self.cooker.configuration.setsceneonly:
            task = None
        else:
            task = self.sched.next()
        if task is not None:
            (mc, fn, taskname, taskfn) = split_tid_mcfn(task)

            if self.rqdata.setscenewhitelist is not None:
                if self.check_setscenewhitelist(task):
                    self.task_fail(task, "setscene whitelist")
                    return True

            if task in self.tasks_covered:
                logger.debug2("Setscene covered task %s", task)
                self.task_skip(task, "covered")
                return True

            if self.rq.check_stamp_task(task, taskname, cache=self.stampcache):
                logger.debug2("Stamp current task %s", task)

                self.task_skip(task, "existing")
                self.runq_tasksrun.add(task)
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
            taskhash = self.rqdata.get_task_hash(task)
            unihash = self.rqdata.get_task_unihash(task)
            if 'fakeroot' in taskdep and taskname in taskdep['fakeroot'] and not (self.cooker.configuration.dry_run or self.rqdata.setscene_enforce):
                if not mc in self.rq.fakeworker:
                    try:
                        self.rq.start_fakeworker(self, mc)
                    except OSError as exc:
                        logger.critical("Failed to spawn fakeroot worker to run %s: %s" % (task, str(exc)))
                        self.rq.state = runQueueFailed
                        self.stats.taskFailed()
                        return True
                self.rq.fakeworker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, taskhash, unihash, False, self.cooker.collections[mc].get_file_appends(taskfn), taskdepdata, self.rqdata.setscene_enforce)) + b"</runtask>")
                self.rq.fakeworker[mc].process.stdin.flush()
            else:
                self.rq.worker[mc].process.stdin.write(b"<runtask>" + pickle.dumps((taskfn, task, taskname, taskhash, unihash, False, self.cooker.collections[mc].get_file_appends(taskfn), taskdepdata, self.rqdata.setscene_enforce)) + b"</runtask>")
                self.rq.worker[mc].process.stdin.flush()

            self.build_stamps[task] = bb.build.stampfile(taskname, self.rqdata.dataCaches[mc], taskfn, noextra=True)
            self.build_stamps2.append(self.build_stamps[task])
            self.runq_running.add(task)
            self.stats.taskActive()
            if self.can_start_task():
                return True

        if self.stats.active > 0 or len(self.sq_live) > 0:
            self.rq.read_workers()
            return self.rq.active_fds()

        # No more tasks can be run. If we have deferred setscene tasks we should run them.
        if self.sq_deferred:
            tid = self.sq_deferred.pop(list(self.sq_deferred.keys())[0])
            logger.warning("Runqeueue deadlocked on deferred tasks, forcing task %s" % tid)
            if tid not in self.runq_complete:
                self.sq_task_failoutright(tid)
            return True

        if len(self.failed_tids) != 0:
            self.rq.state = runQueueFailed
            return True

        # Sanity Checks
        err = self.summarise_scenequeue_errors()
        for task in self.rqdata.runtaskentries:
            if task not in self.runq_buildable:
                logger.error("Task %s never buildable!", task)
                err = True
            elif task not in self.runq_running:
                logger.error("Task %s never ran!", task)
                err = True
            elif task not in self.runq_complete:
                logger.error("Task %s never completed!", task)
                err = True

        if err:
            self.rq.state = runQueueFailed
        else:
            self.rq.state = runQueueComplete

        return True

    def filtermcdeps(self, task, mc, deps):
        ret = set()
        for dep in deps:
            thismc = mc_from_tid(dep)
            if thismc != mc:
                continue
            ret.add(dep)
        return ret

    # We filter out multiconfig dependencies from taskdepdata we pass to the tasks
    # as most code can't handle them
    def build_taskdepdata(self, task):
        taskdepdata = {}
        mc = mc_from_tid(task)
        next = self.rqdata.runtaskentries[task].depends.copy()
        next.add(task)
        next = self.filtermcdeps(task, mc, next)
        while next:
            additional = []
            for revdep in next:
                (mc, fn, taskname, taskfn) = split_tid_mcfn(revdep)
                pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
                deps = self.rqdata.runtaskentries[revdep].depends
                provides = self.rqdata.dataCaches[mc].fn_provides[taskfn]
                taskhash = self.rqdata.runtaskentries[revdep].hash
                unihash = self.rqdata.runtaskentries[revdep].unihash
                deps = self.filtermcdeps(task, mc, deps)
                taskdepdata[revdep] = [pn, taskname, fn, deps, provides, taskhash, unihash]
                for revdep2 in deps:
                    if revdep2 not in taskdepdata:
                        additional.append(revdep2)
            next = additional

        #bb.note("Task %s: " % task + str(taskdepdata).replace("], ", "],\n"))
        return taskdepdata

    def update_holdofftasks(self):

        if not self.holdoff_need_update:
            return

        notcovered = set(self.scenequeue_notcovered)
        notcovered |= self.cantskip
        for tid in self.scenequeue_notcovered:
            notcovered |= self.sqdata.sq_covered_tasks[tid]
        notcovered |= self.sqdata.unskippable.difference(self.rqdata.runq_setscene_tids)
        notcovered.intersection_update(self.tasks_scenequeue_done)

        covered = set(self.scenequeue_covered)
        for tid in self.scenequeue_covered:
            covered |= self.sqdata.sq_covered_tasks[tid]
        covered.difference_update(notcovered)
        covered.intersection_update(self.tasks_scenequeue_done)

        for tid in notcovered | covered:
            if len(self.rqdata.runtaskentries[tid].depends) == 0:
                self.setbuildable(tid)
            elif self.rqdata.runtaskentries[tid].depends.issubset(self.runq_complete):
                 self.setbuildable(tid)

        self.tasks_covered = covered
        self.tasks_notcovered = notcovered

        self.holdoff_tasks = set()

        for tid in self.rqdata.runq_setscene_tids:
            if tid not in self.scenequeue_covered and tid not in self.scenequeue_notcovered:
                self.holdoff_tasks.add(tid)

        for tid in self.holdoff_tasks.copy():
            for dep in self.sqdata.sq_covered_tasks[tid]:
                if dep not in self.runq_complete:
                    self.holdoff_tasks.add(dep)

        self.holdoff_need_update = False

    def process_possible_migrations(self):

        changed = set()
        toprocess = set()
        for tid, unihash in self.updated_taskhash_queue.copy():
            if tid in self.runq_running and tid not in self.runq_complete:
                continue

            self.updated_taskhash_queue.remove((tid, unihash))

            if unihash != self.rqdata.runtaskentries[tid].unihash:
                # Make sure we rehash any other tasks with the same task hash that we're deferred against.
                torehash = [tid]
                for deftid in self.sq_deferred:
                    if self.sq_deferred[deftid] == tid:
                        torehash.append(deftid)
                for hashtid in torehash:
                    hashequiv_logger.verbose("Task %s unihash changed to %s" % (hashtid, unihash))
                    self.rqdata.runtaskentries[hashtid].unihash = unihash
                    bb.parse.siggen.set_unihash(hashtid, unihash)
                    toprocess.add(hashtid)

        # Work out all tasks which depend upon these
        total = set()
        next = set()
        for p in toprocess:
            next |= self.rqdata.runtaskentries[p].revdeps
        while next:
            current = next.copy()
            total = total | next
            next = set()
            for ntid in current:
                next |= self.rqdata.runtaskentries[ntid].revdeps
            next.difference_update(total)

        # Now iterate those tasks in dependency order to regenerate their taskhash/unihash
        next = set()
        for p in total:
            if len(self.rqdata.runtaskentries[p].depends) == 0:
                next.add(p)
            elif self.rqdata.runtaskentries[p].depends.isdisjoint(total):
                next.add(p)

        # When an item doesn't have dependencies in total, we can process it. Drop items from total when handled
        while next:
            current = next.copy()
            next = set()
            for tid in current:
                if len(self.rqdata.runtaskentries[p].depends) and not self.rqdata.runtaskentries[tid].depends.isdisjoint(total):
                    continue
                orighash = self.rqdata.runtaskentries[tid].hash
                dc = bb.parse.siggen.get_data_caches(self.rqdata.dataCaches, mc_from_tid(tid))
                newhash = bb.parse.siggen.get_taskhash(tid, self.rqdata.runtaskentries[tid].depends, dc)
                origuni = self.rqdata.runtaskentries[tid].unihash
                newuni = bb.parse.siggen.get_unihash(tid)
                # FIXME, need to check it can come from sstate at all for determinism?
                remapped = False
                if newuni == origuni:
                    # Nothing to do, we match, skip code below
                    remapped = True
                elif tid in self.scenequeue_covered or tid in self.sq_live:
                    # Already ran this setscene task or it running. Report the new taskhash
                    bb.parse.siggen.report_unihash_equiv(tid, newhash, origuni, newuni, self.rqdata.dataCaches)
                    hashequiv_logger.verbose("Already covered setscene for %s so ignoring rehash (remap)" % (tid))
                    remapped = True

                if not remapped:
                    #logger.debug("Task %s hash changes: %s->%s %s->%s" % (tid, orighash, newhash, origuni, newuni))
                    self.rqdata.runtaskentries[tid].hash = newhash
                    self.rqdata.runtaskentries[tid].unihash = newuni
                    changed.add(tid)

                next |= self.rqdata.runtaskentries[tid].revdeps
                total.remove(tid)
                next.intersection_update(total)

        if changed:
            for mc in self.rq.worker:
                self.rq.worker[mc].process.stdin.write(b"<newtaskhashes>" + pickle.dumps(bb.parse.siggen.get_taskhashes()) + b"</newtaskhashes>")
            for mc in self.rq.fakeworker:
                self.rq.fakeworker[mc].process.stdin.write(b"<newtaskhashes>" + pickle.dumps(bb.parse.siggen.get_taskhashes()) + b"</newtaskhashes>")

            hashequiv_logger.debug(pprint.pformat("Tasks changed:\n%s" % (changed)))

        for tid in changed:
            if tid not in self.rqdata.runq_setscene_tids:
                continue
            if tid not in self.pending_migrations:
                self.pending_migrations.add(tid)

        update_tasks = []
        for tid in self.pending_migrations.copy():
            if tid in self.runq_running or tid in self.sq_live:
                # Too late, task already running, not much we can do now
                self.pending_migrations.remove(tid)
                continue

            valid = True
            # Check no tasks this covers are running
            for dep in self.sqdata.sq_covered_tasks[tid]:
                if dep in self.runq_running and dep not in self.runq_complete:
                    hashequiv_logger.debug2("Task %s is running which blocks setscene for %s from running" % (dep, tid))
                    valid = False
                    break
            if not valid:
                continue

            self.pending_migrations.remove(tid)
            changed = True

            if tid in self.tasks_scenequeue_done:
                self.tasks_scenequeue_done.remove(tid)
            for dep in self.sqdata.sq_covered_tasks[tid]:
                if dep in self.runq_complete and dep not in self.runq_tasksrun:
                    bb.error("Task %s marked as completed but now needing to rerun? Aborting build." % dep)
                    self.failed_tids.append(tid)
                    self.rq.state = runQueueCleanUp
                    return

                if dep not in self.runq_complete:
                    if dep in self.tasks_scenequeue_done and dep not in self.sqdata.unskippable:
                        self.tasks_scenequeue_done.remove(dep)

            if tid in self.sq_buildable:
                self.sq_buildable.remove(tid)
            if tid in self.sq_running:
                self.sq_running.remove(tid)
            harddepfail = False
            for t in self.sqdata.sq_harddeps:
                if tid in self.sqdata.sq_harddeps[t] and t in self.scenequeue_notcovered:
                    harddepfail = True
                    break
            if not harddepfail and self.sqdata.sq_revdeps[tid].issubset(self.scenequeue_covered | self.scenequeue_notcovered):
                if tid not in self.sq_buildable:
                    self.sq_buildable.add(tid)
            if len(self.sqdata.sq_revdeps[tid]) == 0:
                self.sq_buildable.add(tid)

            if tid in self.sqdata.outrightfail:
                self.sqdata.outrightfail.remove(tid)
            if tid in self.scenequeue_notcovered:
                self.scenequeue_notcovered.remove(tid)
            if tid in self.scenequeue_covered:
                self.scenequeue_covered.remove(tid)
            if tid in self.scenequeue_notneeded:
                self.scenequeue_notneeded.remove(tid)

            (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
            self.sqdata.stamps[tid] = bb.build.stampfile(taskname + "_setscene", self.rqdata.dataCaches[mc], taskfn, noextra=True)

            if tid in self.stampcache:
                del self.stampcache[tid]

            if tid in self.build_stamps:
                del self.build_stamps[tid]

            update_tasks.append((tid, harddepfail, tid in self.sqdata.valid))

        if update_tasks:
            self.sqdone = False
            for tid in [t[0] for t in update_tasks]:
                h = pending_hash_index(tid, self.rqdata)
                if h in self.sqdata.hashes and tid != self.sqdata.hashes[h]:
                    self.sq_deferred[tid] = self.sqdata.hashes[h]
                    bb.note("Deferring %s after %s" % (tid, self.sqdata.hashes[h]))
            update_scenequeue_data([t[0] for t in update_tasks], self.sqdata, self.rqdata, self.rq, self.cooker, self.stampcache, self, summary=False)

        for (tid, harddepfail, origvalid) in update_tasks:
            if tid in self.sqdata.valid and not origvalid:
                hashequiv_logger.verbose("Setscene task %s became valid" % tid)
            if harddepfail:
                self.sq_task_failoutright(tid)

        if changed:
            self.stats.updateCovered(len(self.scenequeue_covered), len(self.scenequeue_notcovered))
            self.holdoff_need_update = True

    def scenequeue_updatecounters(self, task, fail=False):

        for dep in sorted(self.sqdata.sq_deps[task]):
            if fail and task in self.sqdata.sq_harddeps and dep in self.sqdata.sq_harddeps[task]:
                if dep in self.scenequeue_covered or dep in self.scenequeue_notcovered:
                    # dependency could be already processed, e.g. noexec setscene task
                    continue
                noexec, stamppresent = check_setscene_stamps(dep, self.rqdata, self.rq, self.stampcache)
                if noexec or stamppresent:
                    continue
                logger.debug2("%s was unavailable and is a hard dependency of %s so skipping" % (task, dep))
                self.sq_task_failoutright(dep)
                continue
            if self.sqdata.sq_revdeps[dep].issubset(self.scenequeue_covered | self.scenequeue_notcovered):
                if dep not in self.sq_buildable:
                    self.sq_buildable.add(dep)

        next = set([task])
        while next:
            new = set()
            for t in sorted(next):
                self.tasks_scenequeue_done.add(t)
                # Look down the dependency chain for non-setscene things which this task depends on
                # and mark as 'done'
                for dep in self.rqdata.runtaskentries[t].depends:
                    if dep in self.rqdata.runq_setscene_tids or dep in self.tasks_scenequeue_done:
                        continue
                    if self.rqdata.runtaskentries[dep].revdeps.issubset(self.tasks_scenequeue_done):
                        new.add(dep)
            next = new

        self.stats.updateCovered(len(self.scenequeue_covered), len(self.scenequeue_notcovered))
        self.holdoff_need_update = True

    def sq_task_completeoutright(self, task):
        """
        Mark a task as completed
        Look at the reverse dependencies and mark any task with
        completed dependencies as buildable
        """

        logger.debug('Found task %s which could be accelerated', task)
        self.scenequeue_covered.add(task)
        self.scenequeue_updatecounters(task)

    def sq_check_taskfail(self, task):
        if self.rqdata.setscenewhitelist is not None:
            realtask = task.split('_setscene')[0]
            (mc, fn, taskname, taskfn) = split_tid_mcfn(realtask)
            pn = self.rqdata.dataCaches[mc].pkg_fn[taskfn]
            if not check_setscene_enforce_whitelist(pn, taskname, self.rqdata.setscenewhitelist):
                logger.error('Task %s.%s failed' % (pn, taskname + "_setscene"))
                self.rq.state = runQueueCleanUp

    def sq_task_complete(self, task):
        bb.event.fire(sceneQueueTaskCompleted(task, self.stats, self.rq), self.cfgData)
        self.sq_task_completeoutright(task)

    def sq_task_fail(self, task, result):
        bb.event.fire(sceneQueueTaskFailed(task, self.stats, result, self), self.cfgData)
        self.scenequeue_notcovered.add(task)
        self.scenequeue_updatecounters(task, True)
        self.sq_check_taskfail(task)

    def sq_task_failoutright(self, task):
        self.sq_running.add(task)
        self.sq_buildable.add(task)
        self.scenequeue_notcovered.add(task)
        self.scenequeue_updatecounters(task, True)

    def sq_task_skip(self, task):
        self.sq_running.add(task)
        self.sq_buildable.add(task)
        self.sq_task_completeoutright(task)

    def sq_build_taskdepdata(self, task):
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
                unihash = self.rqdata.runtaskentries[revdep].unihash
                taskdepdata[revdep] = [pn, taskname, fn, deps, provides, taskhash, unihash]
                for revdep2 in deps:
                    if revdep2 not in taskdepdata:
                        additional.append(revdep2)
            next = additional

        #bb.note("Task %s: " % task + str(taskdepdata).replace("], ", "],\n"))
        return taskdepdata

    def check_setscenewhitelist(self, tid):
        # Check task that is going to run against the whitelist
        (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
        # Ignore covered tasks
        if tid in self.tasks_covered:
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
            if tid in self.rqdata.runq_setscene_tids:
                msg = 'Task %s.%s attempted to execute unexpectedly and should have been setscened' % (pn, taskname)
            else:
                msg = 'Task %s.%s attempted to execute unexpectedly' % (pn, taskname)
            for t in self.scenequeue_notcovered:
                msg = msg + "\nTask %s, unihash %s, taskhash %s" % (t, self.rqdata.runtaskentries[t].unihash, self.rqdata.runtaskentries[t].hash)
            logger.error(msg + '\nThis is usually due to missing setscene tasks. Those missing in this build were: %s' % pprint.pformat(self.scenequeue_notcovered))
            return True
        return False

class SQData(object):
    def __init__(self):
        # SceneQueue dependencies
        self.sq_deps = {}
        # SceneQueue reverse dependencies
        self.sq_revdeps = {}
        # Injected inter-setscene task dependencies
        self.sq_harddeps = {}
        # Cache of stamp files so duplicates can't run in parallel
        self.stamps = {}
        # Setscene tasks directly depended upon by the build
        self.unskippable = set()
        # List of setscene tasks which aren't present
        self.outrightfail = set()
        # A list of normal tasks a setscene task covers
        self.sq_covered_tasks = {}

def build_scenequeue_data(sqdata, rqdata, rq, cooker, stampcache, sqrq):

    sq_revdeps = {}
    sq_revdeps_squash = {}
    sq_collated_deps = {}

    # We need to construct a dependency graph for the setscene functions. Intermediate
    # dependencies between the setscene tasks only complicate the code. This code
    # therefore aims to collapse the huge runqueue dependency tree into a smaller one
    # only containing the setscene functions.

    rqdata.init_progress_reporter.next_stage()

    # First process the chains up to the first setscene task.
    endpoints = {}
    for tid in rqdata.runtaskentries:
        sq_revdeps[tid] = copy.copy(rqdata.runtaskentries[tid].revdeps)
        sq_revdeps_squash[tid] = set()
        if (len(sq_revdeps[tid]) == 0) and tid not in rqdata.runq_setscene_tids:
            #bb.warn("Added endpoint %s" % (tid))
            endpoints[tid] = set()

    rqdata.init_progress_reporter.next_stage()

    # Secondly process the chains between setscene tasks.
    for tid in rqdata.runq_setscene_tids:
        sq_collated_deps[tid] = set()
        #bb.warn("Added endpoint 2 %s" % (tid))
        for dep in rqdata.runtaskentries[tid].depends:
                if tid in sq_revdeps[dep]:
                    sq_revdeps[dep].remove(tid)
                if dep not in endpoints:
                    endpoints[dep] = set()
                #bb.warn("  Added endpoint 3 %s" % (dep))
                endpoints[dep].add(tid)

    rqdata.init_progress_reporter.next_stage()

    def process_endpoints(endpoints):
        newendpoints = {}
        for point, task in endpoints.items():
            tasks = set()
            if task:
                tasks |= task
            if sq_revdeps_squash[point]:
                tasks |= sq_revdeps_squash[point]
            if point not in rqdata.runq_setscene_tids:
                for t in tasks:
                    sq_collated_deps[t].add(point)
            sq_revdeps_squash[point] = set()
            if point in rqdata.runq_setscene_tids:
                sq_revdeps_squash[point] = tasks
                tasks = set()
                continue
            for dep in rqdata.runtaskentries[point].depends:
                if point in sq_revdeps[dep]:
                    sq_revdeps[dep].remove(point)
                if tasks:
                    sq_revdeps_squash[dep] |= tasks
                if len(sq_revdeps[dep]) == 0 and dep not in rqdata.runq_setscene_tids:
                    newendpoints[dep] = task
        if len(newendpoints) != 0:
            process_endpoints(newendpoints)

    process_endpoints(endpoints)

    rqdata.init_progress_reporter.next_stage()

    # Build a list of tasks which are "unskippable"
    # These are direct endpoints referenced by the build upto and including setscene tasks
    # Take the build endpoints (no revdeps) and find the sstate tasks they depend upon
    new = True
    for tid in rqdata.runtaskentries:
        if len(rqdata.runtaskentries[tid].revdeps) == 0:
            sqdata.unskippable.add(tid)
    sqdata.unskippable |= sqrq.cantskip
    while new:
        new = False
        orig = sqdata.unskippable.copy()
        for tid in sorted(orig, reverse=True):
            if tid in rqdata.runq_setscene_tids:
                continue
            if len(rqdata.runtaskentries[tid].depends) == 0:
                # These are tasks which have no setscene tasks in their chain, need to mark as directly buildable
                sqrq.setbuildable(tid)
            sqdata.unskippable |= rqdata.runtaskentries[tid].depends
            if sqdata.unskippable != orig:
                new = True

    sqrq.tasks_scenequeue_done |= sqdata.unskippable.difference(rqdata.runq_setscene_tids)

    rqdata.init_progress_reporter.next_stage(len(rqdata.runtaskentries))

    # Sanity check all dependencies could be changed to setscene task references
    for taskcounter, tid in enumerate(rqdata.runtaskentries):
        if tid in rqdata.runq_setscene_tids:
            pass
        elif len(sq_revdeps_squash[tid]) != 0:
            bb.msg.fatal("RunQueue", "Something went badly wrong during scenequeue generation, aborting. Please report this problem.")
        else:
            del sq_revdeps_squash[tid]
        rqdata.init_progress_reporter.update(taskcounter)

    rqdata.init_progress_reporter.next_stage()

    # Resolve setscene inter-task dependencies
    # e.g. do_sometask_setscene[depends] = "targetname:do_someothertask_setscene"
    # Note that anything explicitly depended upon will have its reverse dependencies removed to avoid circular dependencies
    for tid in rqdata.runq_setscene_tids:
        (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
        realtid = tid + "_setscene"
        idepends = rqdata.taskData[mc].taskentries[realtid].idepends
        sqdata.stamps[tid] = bb.build.stampfile(taskname + "_setscene", rqdata.dataCaches[mc], taskfn, noextra=True)
        for (depname, idependtask) in idepends:

            if depname not in rqdata.taskData[mc].build_targets:
                continue

            depfn = rqdata.taskData[mc].build_targets[depname][0]
            if depfn is None:
                continue
            deptid = depfn + ":" + idependtask.replace("_setscene", "")
            if deptid not in rqdata.runtaskentries:
                bb.msg.fatal("RunQueue", "Task %s depends upon non-existent task %s:%s" % (realtid, depfn, idependtask))

            if not deptid in sqdata.sq_harddeps:
                sqdata.sq_harddeps[deptid] = set()
            sqdata.sq_harddeps[deptid].add(tid)

            sq_revdeps_squash[tid].add(deptid)
            # Have to zero this to avoid circular dependencies
            sq_revdeps_squash[deptid] = set()

    rqdata.init_progress_reporter.next_stage()

    for task in sqdata.sq_harddeps:
        for dep in sqdata.sq_harddeps[task]:
            sq_revdeps_squash[dep].add(task)

    rqdata.init_progress_reporter.next_stage()

    #for tid in sq_revdeps_squash:
    #    data = ""
    #    for dep in sq_revdeps_squash[tid]:
    #        data = data + "\n   %s" % dep
    #    bb.warn("Task %s_setscene: is %s " % (tid, data))

    sqdata.sq_revdeps = sq_revdeps_squash
    sqdata.sq_covered_tasks = sq_collated_deps

    # Build reverse version of revdeps to populate deps structure
    for tid in sqdata.sq_revdeps:
        sqdata.sq_deps[tid] = set()
    for tid in sqdata.sq_revdeps:
        for dep in sqdata.sq_revdeps[tid]:
            sqdata.sq_deps[dep].add(tid)

    rqdata.init_progress_reporter.next_stage()

    sqdata.multiconfigs = set()
    for tid in sqdata.sq_revdeps:
        sqdata.multiconfigs.add(mc_from_tid(tid))
        if len(sqdata.sq_revdeps[tid]) == 0:
            sqrq.sq_buildable.add(tid)

    rqdata.init_progress_reporter.finish()

    sqdata.noexec = set()
    sqdata.stamppresent = set()
    sqdata.valid = set()

    sqdata.hashes = {}
    sqrq.sq_deferred = {}
    for mc in sorted(sqdata.multiconfigs):
        for tid in sorted(sqdata.sq_revdeps):
            if mc_from_tid(tid) != mc:
                continue
            h = pending_hash_index(tid, rqdata)
            if h not in sqdata.hashes:
                sqdata.hashes[h] = tid
            else:
                sqrq.sq_deferred[tid] = sqdata.hashes[h]
                bb.note("Deferring %s after %s" % (tid, sqdata.hashes[h]))

    update_scenequeue_data(sqdata.sq_revdeps, sqdata, rqdata, rq, cooker, stampcache, sqrq, summary=True)

    # Compute a list of 'stale' sstate tasks where the current hash does not match the one
    # in any stamp files. Pass the list out to metadata as an event.
    found = {}
    for tid in rqdata.runq_setscene_tids:
        (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)
        stamps = bb.build.find_stale_stamps(taskname, rqdata.dataCaches[mc], taskfn)
        if stamps:
            if mc not in found:
                found[mc] = {}
            found[mc][tid] = stamps
    for mc in found:
        event = bb.event.StaleSetSceneTasks(found[mc])
        bb.event.fire(event, cooker.databuilder.mcdata[mc])

def check_setscene_stamps(tid, rqdata, rq, stampcache, noexecstamp=False):

    (mc, fn, taskname, taskfn) = split_tid_mcfn(tid)

    taskdep = rqdata.dataCaches[mc].task_deps[taskfn]

    if 'noexec' in taskdep and taskname in taskdep['noexec']:
        bb.build.make_stamp(taskname + "_setscene", rqdata.dataCaches[mc], taskfn)
        return True, False

    if rq.check_stamp_task(tid, taskname + "_setscene", cache=stampcache):
        logger.debug2('Setscene stamp current for task %s', tid)
        return False, True

    if rq.check_stamp_task(tid, taskname, recurse = True, cache=stampcache):
        logger.debug2('Normal stamp current for task %s', tid)
        return False, True

    return False, False

def update_scenequeue_data(tids, sqdata, rqdata, rq, cooker, stampcache, sqrq, summary=True):

    tocheck = set()

    for tid in sorted(tids):
        if tid in sqdata.stamppresent:
            sqdata.stamppresent.remove(tid)
        if tid in sqdata.valid:
            sqdata.valid.remove(tid)
        if tid in sqdata.outrightfail:
            sqdata.outrightfail.remove(tid)

        noexec, stamppresent = check_setscene_stamps(tid, rqdata, rq, stampcache, noexecstamp=True)

        if noexec:
            sqdata.noexec.add(tid)
            sqrq.sq_task_skip(tid)
            continue

        if stamppresent:
            sqdata.stamppresent.add(tid)
            sqrq.sq_task_skip(tid)
            continue

        tocheck.add(tid)

    sqdata.valid |= rq.validate_hashes(tocheck, cooker.data, len(sqdata.stamppresent), False, summary=summary)

    for tid in tids:
        if tid in sqdata.stamppresent:
            continue
        if tid in sqdata.valid:
            continue
        if tid in sqdata.noexec:
            continue
        if tid in sqrq.scenequeue_covered:
            continue
        if tid in sqrq.scenequeue_notcovered:
            continue
        if tid in sqrq.sq_deferred:
            continue
        sqdata.outrightfail.add(tid)

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
    def __init__(self, task, stats, exitcode, rq, fakeroot_log=None):
        runQueueEvent.__init__(self, task, stats, rq)
        self.exitcode = exitcode
        self.fakeroot_log = fakeroot_log

    def __str__(self):
        if self.fakeroot_log:
            return "Task (%s) failed with exit code '%s' \nPseudo log:\n%s" % (self.taskstring, self.exitcode, self.fakeroot_log)
        else:
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

class taskUniHashUpdate(bb.event.Event):
    """
    Base runQueue event class
    """
    def __init__(self, task, unihash):
        self.taskid = task
        self.unihash = unihash
        bb.event.Event.__init__(self)

class runQueuePipe():
    """
    Abstraction for a pipe between a worker thread and the server
    """
    def __init__(self, pipein, pipeout, d, rq, rqexec, fakerootlogs=None):
        self.input = pipein
        if pipeout:
            pipeout.close()
        bb.utils.nonblockingfd(self.input)
        self.queue = b""
        self.d = d
        self.rq = rq
        self.rqexec = rqexec
        self.fakerootlogs = fakerootlogs

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
                except (ValueError, pickle.UnpicklingError, AttributeError, IndexError) as e:
                    if isinstance(e, pickle.UnpicklingError) and "truncated" in str(e):
                        # The pickled data could contain "</event>" so search for the next occurance
                        # unpickling again, this should be the only way an unpickle error could occur
                        index = self.queue.find(b"</event>", index + 1)
                        continue
                    bb.msg.fatal("RunQueue", "failed load pickle '%s': '%s'" % (e, self.queue[7:index]))
                bb.event.fire_from_worker(event, self.d)
                if isinstance(event, taskUniHashUpdate):
                    self.rqexec.updated_taskhash_queue.append((event.taskid, event.unihash))
                found = True
                self.queue = self.queue[index+8:]
                index = self.queue.find(b"</event>")
            index = self.queue.find(b"</exitcode>")
            while index != -1 and self.queue.startswith(b"<exitcode>"):
                try:
                    task, status = pickle.loads(self.queue[10:index])
                except (ValueError, pickle.UnpicklingError, AttributeError, IndexError) as e:
                    bb.msg.fatal("RunQueue", "failed load pickle '%s': '%s'" % (e, self.queue[10:index]))
                (_, _, _, taskfn) = split_tid_mcfn(task)
                fakerootlog = None
                if self.fakerootlogs and taskfn and taskfn in self.fakerootlogs:
                    fakerootlog = self.fakerootlogs[taskfn]
                self.rqexec.runqueue_process_waitpid(task, status, fakerootlog=fakerootlog)
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

def get_setscene_enforce_whitelist(d, targets):
    if d.getVar('BB_SETSCENE_ENFORCE') != '1':
        return None
    whitelist = (d.getVar("BB_SETSCENE_ENFORCE_WHITELIST") or "").split()
    outlist = []
    for item in whitelist[:]:
        if item.startswith('%:'):
            for (mc, target, task, fn) in targets:
                outlist.append(target + ':' + item.split(':')[1])
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
