#  This file is part of pybootchartgui.

#  pybootchartgui is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.

#  pybootchartgui is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.

#  You should have received a copy of the GNU General Public License
#  along with pybootchartgui. If not, see <http://www.gnu.org/licenses/>.

class ProcessTree:
    """ProcessTree encapsulates a process tree.  The tree is built from log files
       retrieved during the boot process.  When building the process tree, it is
       pruned and merged in order to be able to visualize it in a comprehensible
       manner.

       The following pruning techniques are used:

        * idle processes that keep running during the last process sample
          (which is a heuristic for a background processes) are removed,
        * short-lived processes (i.e. processes that only live for the
          duration of two samples or less) are removed,
        * the processes used by the boot logger are removed,
        * exploders (i.e. processes that are known to spawn huge meaningless
          process subtrees) have their subtrees merged together,
        * siblings (i.e. processes with the same command line living
          concurrently -- thread heuristic) are merged together,
        * process runs (unary trees with processes sharing the command line)
          are merged together.

    """
    LOGGER_PROC = 'bootchart-colle'
    EXPLODER_PROCESSES = set(['hwup'])

    def __init__(self, writer, kernel, psstats, sample_period,
                 monitoredApp, prune, idle, taskstats,
                 accurate_parentage, for_testing = False):
        self.writer = writer
        self.process_tree = []
        self.taskstats = taskstats
        if psstats is None:
            process_list = kernel
        elif kernel is None:
            process_list = psstats.process_map.values()
        else:
            process_list = list(kernel) + list(psstats.process_map.values())
        self.process_list = sorted(process_list, key = lambda p: p.pid)
        self.sample_period = sample_period

        self.build()
        if not accurate_parentage:
            self.update_ppids_for_daemons(self.process_list)

        self.start_time = self.get_start_time(self.process_tree)
        self.end_time = self.get_end_time(self.process_tree)
        self.duration = self.end_time - self.start_time
        self.idle = idle

        if for_testing:
            return

        removed = self.merge_logger(self.process_tree, self.LOGGER_PROC, monitoredApp, False)
        writer.status("merged %i logger processes" % removed)

        if prune:
            p_processes = self.prune(self.process_tree, None)
            p_exploders = self.merge_exploders(self.process_tree, self.EXPLODER_PROCESSES)
            p_threads = self.merge_siblings(self.process_tree)
            p_runs = self.merge_runs(self.process_tree)
            writer.status("pruned %i process, %i exploders, %i threads, and %i runs" % (p_processes, p_exploders, p_threads, p_runs))

        self.sort(self.process_tree)

        self.start_time = self.get_start_time(self.process_tree)
        self.end_time = self.get_end_time(self.process_tree)
        self.duration = self.end_time - self.start_time

        self.num_proc = self.num_nodes(self.process_tree)

    def build(self):
        """Build the process tree from the list of top samples."""
        self.process_tree = []
        for proc in self.process_list:
            if not proc.parent:
                self.process_tree.append(proc)
            else:
                proc.parent.child_list.append(proc)

    def sort(self, process_subtree):
        """Sort process tree."""
        for p in process_subtree:
            p.child_list.sort(key = lambda p: p.pid)
            self.sort(p.child_list)

    def num_nodes(self, process_list):
        "Counts the number of nodes in the specified process tree."""
        nodes = 0
        for proc in process_list:
            nodes = nodes + self.num_nodes(proc.child_list)
        return nodes + len(process_list)

    def get_start_time(self, process_subtree):
        """Returns the start time of the process subtree.  This is the start
           time of the earliest process.

        """
        if not process_subtree:
            return 100000000
        return min( [min(proc.start_time, self.get_start_time(proc.child_list)) for proc in process_subtree] )

    def get_end_time(self, process_subtree):
        """Returns the end time of the process subtree.  This is the end time
           of the last collected sample.

        """
        if not process_subtree:
            return -100000000
        return max( [max(proc.start_time + proc.duration, self.get_end_time(proc.child_list)) for proc in process_subtree] )

    def get_max_pid(self, process_subtree):
        """Returns the max PID found in the process tree."""
        if not process_subtree:
            return -100000000
        return max( [max(proc.pid, self.get_max_pid(proc.child_list)) for proc in process_subtree] )

    def update_ppids_for_daemons(self, process_list):
        """Fedora hack: when loading the system services from rc, runuser(1)
           is used.  This sets the PPID of all daemons to 1, skewing
           the process tree.  Try to detect this and set the PPID of
           these processes the PID of rc.

        """
        rcstartpid = -1
        rcendpid = -1
        rcproc = None
        for p in process_list:
            if p.cmd == "rc" and p.ppid // 1000 == 1:
                rcproc = p
                rcstartpid = p.pid
                rcendpid = self.get_max_pid(p.child_list)
        if rcstartpid != -1 and rcendpid != -1:
            for p in process_list:
                if p.pid > rcstartpid and p.pid < rcendpid and p.ppid // 1000 == 1:
                    p.ppid = rcstartpid
                    p.parent = rcproc
            for p in process_list:
                p.child_list = []
            self.build()

    def prune(self, process_subtree, parent):
        """Prunes the process tree by removing idle processes and processes
           that only live for the duration of a single top sample.  Sibling
           processes with the same command line (i.e. threads) are merged
           together. This filters out sleepy background processes, short-lived
           processes and bootcharts' analysis tools.
        """
        def is_idle_background_process_without_children(p):
            process_end = p.start_time + p.duration
            return not p.active and \
                   process_end >= self.start_time + self.duration and \
                   p.start_time > self.start_time and \
                   p.duration > 0.9 * self.duration and \
                   self.num_nodes(p.child_list) == 0

        num_removed = 0
        idx = 0
        while idx < len(process_subtree):
            p = process_subtree[idx]
            if parent != None or len(p.child_list) == 0:

                prune = False
                if is_idle_background_process_without_children(p):
                    prune = True
                elif p.duration <= 2 * self.sample_period:
                    # short-lived process
                    prune = True

                if prune:
                    process_subtree.pop(idx)
                    for c in p.child_list:
                        process_subtree.insert(idx, c)
                    num_removed += 1
                    continue
                else:
                    num_removed += self.prune(p.child_list, p)
            else:
                num_removed += self.prune(p.child_list, p)
            idx += 1

        return num_removed

    def merge_logger(self, process_subtree, logger_proc, monitored_app, app_tree):
        """Merges the logger's process subtree.  The logger will typically
           spawn lots of sleep and cat processes, thus polluting the
           process tree.

        """
        num_removed = 0
        for p in process_subtree:
            is_app_tree = app_tree
            if logger_proc == p.cmd and not app_tree:
                is_app_tree = True
                num_removed += self.merge_logger(p.child_list, logger_proc, monitored_app, is_app_tree)
                # don't remove the logger itself
                continue

            if app_tree and monitored_app != None and monitored_app == p.cmd:
                is_app_tree = False

            if is_app_tree:
                for child in p.child_list:
                    self.merge_processes(p, child)
                    num_removed += 1
                p.child_list = []
            else:
                num_removed += self.merge_logger(p.child_list, logger_proc, monitored_app, is_app_tree)
        return num_removed

    def merge_exploders(self, process_subtree, processes):
        """Merges specific process subtrees (used for processes which usually
           spawn huge meaningless process trees).

        """
        num_removed = 0
        for p in process_subtree:
            if processes in processes and len(p.child_list) > 0:
                subtreemap = self.getProcessMap(p.child_list)
                for child in subtreemap.values():
                    self.merge_processes(p, child)
                    num_removed += len(subtreemap)
                    p.child_list = []
                    p.cmd += " (+)"
            else:
                num_removed += self.merge_exploders(p.child_list, processes)
        return num_removed

    def merge_siblings(self, process_subtree):
        """Merges thread processes.  Sibling processes with the same command
           line are merged together.

        """
        num_removed = 0
        idx = 0
        while idx < len(process_subtree)-1:
            p = process_subtree[idx]
            nextp = process_subtree[idx+1]
            if nextp.cmd == p.cmd:
                process_subtree.pop(idx+1)
                idx -= 1
                num_removed += 1
                p.child_list.extend(nextp.child_list)
                self.merge_processes(p, nextp)
            num_removed += self.merge_siblings(p.child_list)
            idx += 1
        if len(process_subtree) > 0:
            p = process_subtree[-1]
            num_removed += self.merge_siblings(p.child_list)
        return num_removed

    def merge_runs(self, process_subtree):
        """Merges process runs.  Single child processes which share the same
           command line with the parent are merged.

        """
        num_removed = 0
        idx = 0
        while idx < len(process_subtree):
            p = process_subtree[idx]
            if len(p.child_list) == 1 and p.child_list[0].cmd == p.cmd:
                child = p.child_list[0]
                p.child_list = list(child.child_list)
                self.merge_processes(p, child)
                num_removed += 1
                continue
            num_removed += self.merge_runs(p.child_list)
            idx += 1
        return num_removed

    def merge_processes(self, p1, p2):
        """Merges two process' samples."""
        p1.samples.extend(p2.samples)
        p1.samples.sort( key = lambda p: p.time )
        p1time = p1.start_time
        p2time = p2.start_time
        p1.start_time = min(p1time, p2time)
        pendtime = max(p1time + p1.duration, p2time + p2.duration)
        p1.duration = pendtime - p1.start_time
