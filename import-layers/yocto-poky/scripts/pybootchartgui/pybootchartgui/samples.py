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


class DiskStatSample:
    def __init__(self, time):
        self.time = time
        self.diskdata = [0, 0, 0]
    def add_diskdata(self, new_diskdata):
        self.diskdata = [ a + b for a, b in zip(self.diskdata, new_diskdata) ]

class CPUSample:
    def __init__(self, time, user, sys, io = 0.0, swap = 0.0):
        self.time = time
        self.user = user
        self.sys = sys
        self.io = io
        self.swap = swap

    @property
    def cpu(self):
        return self.user + self.sys

    def __str__(self):
        return str(self.time) + "\t" + str(self.user) + "\t" + \
               str(self.sys) + "\t" + str(self.io) + "\t" + str (self.swap)

class MemSample:
    used_values = ('MemTotal', 'MemFree', 'Buffers', 'Cached', 'SwapTotal', 'SwapFree',)

    def __init__(self, time):
        self.time = time
        self.records = {}

    def add_value(self, name, value):
        if name in MemSample.used_values:
            self.records[name] = value

    def valid(self):
        keys = self.records.keys()
        # discard incomplete samples
        return [v for v in MemSample.used_values if v not in keys] == []

class DrawMemSample:
    """
    Condensed version of a MemSample with exactly the values used by the drawing code.
    Initialized either from a valid MemSample or
    a tuple/list of buffer/used/cached/swap values.
    """
    def __init__(self, mem_sample):
        self.time = mem_sample.time
        if isinstance(mem_sample, MemSample):
            self.buffers = mem_sample.records['MemTotal'] - mem_sample.records['MemFree']
            self.used = mem_sample.records['MemTotal'] - mem_sample.records['MemFree'] - mem_sample.records['Buffers']
            self.cached = mem_sample.records['Cached']
            self.swap = mem_sample.records['SwapTotal'] - mem_sample.records['SwapFree']
        else:
            self.buffers, self.used, self.cached, self.swap = mem_sample

class DiskSpaceSample:
    def __init__(self, time):
        self.time = time
        self.records = {}

    def add_value(self, name, value):
        self.records[name] = value

    def valid(self):
        return bool(self.records)

class ProcessSample:
    def __init__(self, time, state, cpu_sample):
        self.time = time
        self.state = state
        self.cpu_sample = cpu_sample

    def __str__(self):
        return str(self.time) + "\t" + str(self.state) + "\t" + str(self.cpu_sample)

class ProcessStats:
    def __init__(self, writer, process_map, sample_count, sample_period, start_time, end_time):
        self.process_map = process_map
        self.sample_count = sample_count
        self.sample_period = sample_period
        self.start_time = start_time
        self.end_time = end_time
        writer.info ("%d samples, avg. sample length %f" % (self.sample_count, self.sample_period))
        writer.info ("process list size: %d" % len (self.process_map.values()))

class Process:
    def __init__(self, writer, pid, cmd, ppid, start_time):
        self.writer = writer
        self.pid = pid
        self.cmd = cmd
        self.exe = cmd
        self.args = []
        self.ppid = ppid
        self.start_time = start_time
        self.duration = 0
        self.samples = []
        self.parent = None
        self.child_list = []

        self.active = None
        self.last_user_cpu_time = None
        self.last_sys_cpu_time = None

        self.last_cpu_ns = 0
        self.last_blkio_delay_ns = 0
        self.last_swapin_delay_ns = 0

    # split this process' run - triggered by a name change
    def split(self, writer, pid, cmd, ppid, start_time):
        split = Process (writer, pid, cmd, ppid, start_time)

        split.last_cpu_ns = self.last_cpu_ns
        split.last_blkio_delay_ns = self.last_blkio_delay_ns
        split.last_swapin_delay_ns = self.last_swapin_delay_ns

        return split

    def __str__(self):
        return " ".join([str(self.pid), self.cmd, str(self.ppid), '[ ' + str(len(self.samples)) + ' samples ]' ])

    def calc_stats(self, samplePeriod):
        if self.samples:
            firstSample = self.samples[0]
            lastSample = self.samples[-1]
            self.start_time = min(firstSample.time, self.start_time)
            self.duration = lastSample.time - self.start_time + samplePeriod

        activeCount = sum( [1 for sample in self.samples if sample.cpu_sample and sample.cpu_sample.sys + sample.cpu_sample.user + sample.cpu_sample.io > 0.0] )
        activeCount = activeCount + sum( [1 for sample in self.samples if sample.state == 'D'] )
        self.active = (activeCount>2)

    def calc_load(self, userCpu, sysCpu, interval):
        userCpuLoad = float(userCpu - self.last_user_cpu_time) / interval
        sysCpuLoad = float(sysCpu - self.last_sys_cpu_time) / interval
        cpuLoad = userCpuLoad + sysCpuLoad
        # normalize
        if cpuLoad > 1.0:
            userCpuLoad = userCpuLoad / cpuLoad
            sysCpuLoad = sysCpuLoad / cpuLoad
        return (userCpuLoad, sysCpuLoad)

    def set_parent(self, processMap):
        if self.ppid != None:
            self.parent = processMap.get (self.ppid)
            if self.parent == None and self.pid // 1000 > 1 and \
                not (self.ppid == 2000 or self.pid == 2000): # kernel threads: ppid=2
                self.writer.warn("Missing CONFIG_PROC_EVENTS: no parent for pid '%i' ('%s') with ppid '%i'" \
                                 % (self.pid,self.cmd,self.ppid))

    def get_end_time(self):
        return self.start_time + self.duration

class DiskSample:
    def __init__(self, time, read, write, util):
        self.time = time
        self.read = read
        self.write = write
        self.util = util
        self.tput = read + write

    def __str__(self):
        return "\t".join([str(self.time), str(self.read), str(self.write), str(self.util)])
