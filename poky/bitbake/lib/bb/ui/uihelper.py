#
# Copyright (C) 2006 - 2007  Michael 'Mickey' Lauer
# Copyright (C) 2006 - 2007  Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb.build
import time

class BBUIHelper:
    def __init__(self):
        self.needUpdate = False
        self.running_tasks = {}
        # Running PIDs preserves the order tasks were executed in
        self.running_pids = []
        self.failed_tasks = []
        self.pidmap = {}
        self.tasknumber_current = 0
        self.tasknumber_total = 0

    def eventHandler(self, event):
        # PIDs are a bad idea as they can be reused before we process all UI events.
        # We maintain a 'fuzzy' match for TaskProgress since there is no other way to match
        def removetid(pid, tid):
            self.running_pids.remove(tid)
            del self.running_tasks[tid]
            if self.pidmap[pid] == tid:
                del self.pidmap[pid]
            self.needUpdate = True

        if isinstance(event, bb.build.TaskStarted):
            tid = event._fn + ":" + event._task
            if event._mc != "default":
                self.running_tasks[tid] = { 'title' : "mc:%s:%s %s" % (event._mc, event._package, event._task), 'starttime' : time.time(), 'pid' : event.pid }
            else:
                self.running_tasks[tid] = { 'title' : "%s %s" % (event._package, event._task), 'starttime' : time.time(), 'pid' : event.pid }
            self.running_pids.append(tid)
            self.pidmap[event.pid] = tid
            self.needUpdate = True
        elif isinstance(event, bb.build.TaskSucceeded):
            tid = event._fn + ":" + event._task
            removetid(event.pid, tid)
        elif isinstance(event, bb.build.TaskFailedSilent):
            tid = event._fn + ":" + event._task
            removetid(event.pid, tid)
            # Don't add to the failed tasks list since this is e.g. a setscene task failure
        elif isinstance(event, bb.build.TaskFailed):
            tid = event._fn + ":" + event._task
            removetid(event.pid, tid)
            self.failed_tasks.append( { 'title' : "%s %s" % (event._package, event._task)})
        elif isinstance(event, bb.runqueue.runQueueTaskStarted):
            self.tasknumber_current = event.stats.completed + event.stats.active + event.stats.failed + 1
            self.tasknumber_total = event.stats.total
            self.needUpdate = True
        elif isinstance(event, bb.build.TaskProgress):
            if event.pid > 0 and event.pid in self.pidmap:
                self.running_tasks[self.pidmap[event.pid]]['progress'] = event.progress
                self.running_tasks[self.pidmap[event.pid]]['rate'] = event.rate
                self.needUpdate = True
        else:
            return False
        return True

    def getTasks(self):
        self.needUpdate = False
        return (self.running_tasks, self.failed_tasks)
