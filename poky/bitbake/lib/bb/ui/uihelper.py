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
        self.tasknumber_current = 0
        self.tasknumber_total = 0

    def eventHandler(self, event):
        if isinstance(event, bb.build.TaskStarted):
            if event._mc != "default":
                self.running_tasks[event.pid] = { 'title' : "mc:%s:%s %s" % (event._mc, event._package, event._task), 'starttime' : time.time() }
            else:
                self.running_tasks[event.pid] = { 'title' : "%s %s" % (event._package, event._task), 'starttime' : time.time() }
            self.running_pids.append(event.pid)
            self.needUpdate = True
        elif isinstance(event, bb.build.TaskSucceeded):
            del self.running_tasks[event.pid]
            self.running_pids.remove(event.pid)
            self.needUpdate = True
        elif isinstance(event, bb.build.TaskFailedSilent):
            del self.running_tasks[event.pid]
            self.running_pids.remove(event.pid)
            # Don't add to the failed tasks list since this is e.g. a setscene task failure
            self.needUpdate = True
        elif isinstance(event, bb.build.TaskFailed):
            del self.running_tasks[event.pid]
            self.running_pids.remove(event.pid)
            self.failed_tasks.append( { 'title' : "%s %s" % (event._package, event._task)})
            self.needUpdate = True
        elif isinstance(event, bb.runqueue.runQueueTaskStarted):
            self.tasknumber_current = event.stats.completed + event.stats.active + event.stats.failed + 1
            self.tasknumber_total = event.stats.total
            self.needUpdate = True
        elif isinstance(event, bb.build.TaskProgress):
            if event.pid > 0:
                self.running_tasks[event.pid]['progress'] = event.progress
                self.running_tasks[event.pid]['rate'] = event.rate
                self.needUpdate = True
        else:
            return False
        return True

    def getTasks(self):
        self.needUpdate = False
        return (self.running_tasks, self.failed_tasks)
