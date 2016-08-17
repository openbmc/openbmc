# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2006 - 2007  Michael 'Mickey' Lauer
# Copyright (C) 2006 - 2007  Richard Purdie
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

import bb.build

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
            self.running_tasks[event.pid] = { 'title' : "%s %s" % (event._package, event._task) }
            self.running_pids.append(event.pid)
            self.needUpdate = True
        if isinstance(event, bb.build.TaskSucceeded):
            del self.running_tasks[event.pid]
            self.running_pids.remove(event.pid)
            self.needUpdate = True
        if isinstance(event, bb.build.TaskFailedSilent):
            del self.running_tasks[event.pid]
            self.running_pids.remove(event.pid)
            # Don't add to the failed tasks list since this is e.g. a setscene task failure
            self.needUpdate = True
        if isinstance(event, bb.build.TaskFailed):
            del self.running_tasks[event.pid]
            self.running_pids.remove(event.pid)
            self.failed_tasks.append( { 'title' : "%s %s" % (event._package, event._task)})
            self.needUpdate = True
        if isinstance(event, bb.runqueue.runQueueTaskStarted) or isinstance(event, bb.runqueue.sceneQueueTaskStarted):
            self.tasknumber_current = event.stats.completed + event.stats.active + event.stats.failed + 1
            self.tasknumber_total = event.stats.total
            self.needUpdate = True

    def getTasks(self):
        self.needUpdate = False
        return (self.running_tasks, self.failed_tasks)

