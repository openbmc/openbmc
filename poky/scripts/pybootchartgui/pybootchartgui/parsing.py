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

import os
import string
import re
import sys
import tarfile
from time import clock
from collections import defaultdict
from functools import reduce

from .samples import *
from .process_tree import ProcessTree

if sys.version_info >= (3, 0):
    long = int

# Parsing produces as its end result a 'Trace'

class Trace:
    def __init__(self, writer, paths, options):
        self.processes = {}
        self.start = {}
        self.end = {}
        self.min = None
        self.max = None
        self.headers = None
        self.disk_stats =  []
        self.ps_stats = None
        self.taskstats = None
        self.cpu_stats = []
        self.cmdline = None
        self.kernel = None
        self.kernel_tree = None
        self.filename = None
        self.parent_map = None
        self.mem_stats = []
        self.monitor_disk = None
        self.times = [] # Always empty, but expected by draw.py when drawing system charts.

        if len(paths):
            parse_paths (writer, self, paths)
            if not self.valid():
                raise ParseError("empty state: '%s' does not contain a valid bootchart" % ", ".join(paths))

            if options.full_time:
                self.min = min(self.start.keys())
                self.max = max(self.end.keys())


        # Rendering system charts depends on start and end
        # time. Provide them where the original drawing code expects
        # them, i.e. in proc_tree.
        class BitbakeProcessTree:
            def __init__(self, start_time, end_time):
                self.start_time = start_time
                self.end_time = end_time
                self.duration = self.end_time - self.start_time
        self.proc_tree = BitbakeProcessTree(min(self.start.keys()),
                                            max(self.end.keys()))


        return

        # Turn that parsed information into something more useful
        # link processes into a tree of pointers, calculate statistics
        self.compile(writer)

        # Crop the chart to the end of the first idle period after the given
        # process
        if options.crop_after:
            idle = self.crop (writer, options.crop_after)
        else:
            idle = None

        # Annotate other times as the first start point of given process lists
        self.times = [ idle ]
        if options.annotate:
            for procnames in options.annotate:
                names = [x[:15] for x in procnames.split(",")]
                for proc in self.ps_stats.process_map.values():
                    if proc.cmd in names:
                        self.times.append(proc.start_time)
                        break
                    else:
                        self.times.append(None)

        self.proc_tree = ProcessTree(writer, self.kernel, self.ps_stats,
                                     self.ps_stats.sample_period,
                                     self.headers.get("profile.process"),
                                     options.prune, idle, self.taskstats,
                                     self.parent_map is not None)

        if self.kernel is not None:
            self.kernel_tree = ProcessTree(writer, self.kernel, None, 0,
                                           self.headers.get("profile.process"),
                                           False, None, None, True)

    def valid(self):
        return len(self.processes) != 0
        return self.headers != None and self.disk_stats != None and \
               self.ps_stats != None and self.cpu_stats != None

    def add_process(self, process, start, end):
        self.processes[process] = [start, end]
        if start not in self.start:
            self.start[start] = []
        if process not in self.start[start]:
            self.start[start].append(process)
        if end not in self.end:
            self.end[end] = []
        if process not in self.end[end]:
            self.end[end].append(process)

    def compile(self, writer):

        def find_parent_id_for(pid):
            if pid is 0:
                return 0
            ppid = self.parent_map.get(pid)
            if ppid:
                # many of these double forks are so short lived
                # that we have no samples, or process info for them
                # so climb the parent hierarcy to find one
                if int (ppid * 1000) not in self.ps_stats.process_map:
#                    print "Pid '%d' short lived with no process" % ppid
                    ppid = find_parent_id_for (ppid)
#                else:
#                    print "Pid '%d' has an entry" % ppid
            else:
#                print "Pid '%d' missing from pid map" % pid
                return 0
            return ppid

        # merge in the cmdline data
        if self.cmdline is not None:
            for proc in self.ps_stats.process_map.values():
                rpid = int (proc.pid // 1000)
                if rpid in self.cmdline:
                    cmd = self.cmdline[rpid]
                    proc.exe = cmd['exe']
                    proc.args = cmd['args']
#                else:
#                    print "proc %d '%s' not in cmdline" % (rpid, proc.exe)

        # re-parent any stray orphans if we can
        if self.parent_map is not None:
            for process in self.ps_stats.process_map.values():
                ppid = find_parent_id_for (int(process.pid // 1000))
                if ppid:
                    process.ppid = ppid * 1000

        # stitch the tree together with pointers
        for process in self.ps_stats.process_map.values():
            process.set_parent (self.ps_stats.process_map)

        # count on fingers variously
        for process in self.ps_stats.process_map.values():
            process.calc_stats (self.ps_stats.sample_period)

    def crop(self, writer, crop_after):

        def is_idle_at(util, start, j):
            k = j + 1
            while k < len(util) and util[k][0] < start + 300:
                k += 1
            k = min(k, len(util)-1)

            if util[j][1] >= 0.25:
                return False

            avgload = sum(u[1] for u in util[j:k+1]) / (k-j+1)
            if avgload < 0.25:
                return True
            else:
                return False
        def is_idle(util, start):
            for j in range(0, len(util)):
                if util[j][0] < start:
                    continue
                return is_idle_at(util, start, j)
            else:
                return False

        names = [x[:15] for x in crop_after.split(",")]
        for proc in self.ps_stats.process_map.values():
            if proc.cmd in names or proc.exe in names:
                writer.info("selected proc '%s' from list (start %d)"
                            % (proc.cmd, proc.start_time))
                break
        if proc is None:
            writer.warn("no selected crop proc '%s' in list" % crop_after)


        cpu_util = [(sample.time, sample.user + sample.sys + sample.io) for sample in self.cpu_stats]
        disk_util = [(sample.time, sample.util) for sample in self.disk_stats]

        idle = None
        for i in range(0, len(cpu_util)):
            if cpu_util[i][0] < proc.start_time:
                continue
            if is_idle_at(cpu_util, cpu_util[i][0], i) \
               and is_idle(disk_util, cpu_util[i][0]):
                idle = cpu_util[i][0]
                break

        if idle is None:
            writer.warn ("not idle after proc '%s'" % crop_after)
            return None

        crop_at = idle + 300
        writer.info ("cropping at time %d" % crop_at)
        while len (self.cpu_stats) \
                    and self.cpu_stats[-1].time > crop_at:
            self.cpu_stats.pop()
        while len (self.disk_stats) \
                    and self.disk_stats[-1].time > crop_at:
            self.disk_stats.pop()

        self.ps_stats.end_time = crop_at

        cropped_map = {}
        for key, value in self.ps_stats.process_map.items():
            if (value.start_time <= crop_at):
                cropped_map[key] = value

        for proc in cropped_map.values():
            proc.duration = min (proc.duration, crop_at - proc.start_time)
            while len (proc.samples) \
                        and proc.samples[-1].time > crop_at:
                proc.samples.pop()

        self.ps_stats.process_map = cropped_map

        return idle



class ParseError(Exception):
    """Represents errors during parse of the bootchart."""
    def __init__(self, value):
        self.value = value

    def __str__(self):
        return self.value

def _parse_headers(file):
    """Parses the headers of the bootchart."""
    def parse(acc, line):
        (headers, last) = acc
        if '=' in line:
            last, value = map (lambda x: x.strip(), line.split('=', 1))
        else:
            value = line.strip()
        headers[last] += value
        return headers, last
    return reduce(parse, file.read().decode('utf-8').split('\n'), (defaultdict(str),''))[0]

def _parse_timed_blocks(file):
    """Parses (ie., splits) a file into so-called timed-blocks. A
    timed-block consists of a timestamp on a line by itself followed
    by zero or more lines of data for that point in time."""
    def parse(block):
        lines = block.split('\n')
        if not lines:
            raise ParseError('expected a timed-block consisting a timestamp followed by data lines')
        try:
            return (int(lines[0]), lines[1:])
        except ValueError:
            raise ParseError("expected a timed-block, but timestamp '%s' is not an integer" % lines[0])
    blocks = file.read().decode('utf-8').split('\n\n')
    return [parse(block) for block in blocks if block.strip() and not block.endswith(' not running\n')]

def _parse_proc_ps_log(writer, file):
    """
     * See proc(5) for details.
     *
     * {pid, comm, state, ppid, pgrp, session, tty_nr, tpgid, flags, minflt, cminflt, majflt, cmajflt, utime, stime,
     *  cutime, cstime, priority, nice, 0, itrealvalue, starttime, vsize, rss, rlim, startcode, endcode, startstack,
     *  kstkesp, kstkeip}
    """
    processMap = {}
    ltime = 0
    timed_blocks = _parse_timed_blocks(file)
    for time, lines in timed_blocks:
        for line in lines:
            if not line: continue
            tokens = line.split(' ')
            if len(tokens) < 21:
                continue

            offset = [index for index, token in enumerate(tokens[1:]) if token[-1] == ')'][0]
            pid, cmd, state, ppid = int(tokens[0]), ' '.join(tokens[1:2+offset]), tokens[2+offset], int(tokens[3+offset])
            userCpu, sysCpu, stime = int(tokens[13+offset]), int(tokens[14+offset]), int(tokens[21+offset])

            # magic fixed point-ness ...
            pid *= 1000
            ppid *= 1000
            if pid in processMap:
                process = processMap[pid]
                process.cmd = cmd.strip('()') # why rename after latest name??
            else:
                process = Process(writer, pid, cmd.strip('()'), ppid, min(time, stime))
                processMap[pid] = process

            if process.last_user_cpu_time is not None and process.last_sys_cpu_time is not None and ltime is not None:
                userCpuLoad, sysCpuLoad = process.calc_load(userCpu, sysCpu, max(1, time - ltime))
                cpuSample = CPUSample('null', userCpuLoad, sysCpuLoad, 0.0)
                process.samples.append(ProcessSample(time, state, cpuSample))

            process.last_user_cpu_time = userCpu
            process.last_sys_cpu_time = sysCpu
        ltime = time

    if len (timed_blocks) < 2:
        return None

    startTime = timed_blocks[0][0]
    avgSampleLength = (ltime - startTime)/(len (timed_blocks) - 1)

    return ProcessStats (writer, processMap, len (timed_blocks), avgSampleLength, startTime, ltime)

def _parse_taskstats_log(writer, file):
    """
     * See bootchart-collector.c for details.
     *
     * { pid, ppid, comm, cpu_run_real_total, blkio_delay_total, swapin_delay_total }
     *
    """
    processMap = {}
    pidRewrites = {}
    ltime = None
    timed_blocks = _parse_timed_blocks(file)
    for time, lines in timed_blocks:
        # we have no 'stime' from taskstats, so prep 'init'
        if ltime is None:
            process = Process(writer, 1, '[init]', 0, 0)
            processMap[1000] = process
            ltime = time
#                       continue
        for line in lines:
            if not line: continue
            tokens = line.split(' ')
            if len(tokens) != 6:
                continue

            opid, ppid, cmd = int(tokens[0]), int(tokens[1]), tokens[2]
            cpu_ns, blkio_delay_ns, swapin_delay_ns = long(tokens[-3]), long(tokens[-2]), long(tokens[-1]),

            # make space for trees of pids
            opid *= 1000
            ppid *= 1000

            # when the process name changes, we re-write the pid.
            if opid in pidRewrites:
                pid = pidRewrites[opid]
            else:
                pid = opid

            cmd = cmd.strip('(').strip(')')
            if pid in processMap:
                process = processMap[pid]
                if process.cmd != cmd:
                    pid += 1
                    pidRewrites[opid] = pid
#                                       print "process mutation ! '%s' vs '%s' pid %s -> pid %s\n" % (process.cmd, cmd, opid, pid)
                    process = process.split (writer, pid, cmd, ppid, time)
                    processMap[pid] = process
                else:
                    process.cmd = cmd;
            else:
                process = Process(writer, pid, cmd, ppid, time)
                processMap[pid] = process

            delta_cpu_ns = (float) (cpu_ns - process.last_cpu_ns)
            delta_blkio_delay_ns = (float) (blkio_delay_ns - process.last_blkio_delay_ns)
            delta_swapin_delay_ns = (float) (swapin_delay_ns - process.last_swapin_delay_ns)

            # make up some state data ...
            if delta_cpu_ns > 0:
                state = "R"
            elif delta_blkio_delay_ns + delta_swapin_delay_ns > 0:
                state = "D"
            else:
                state = "S"

            # retain the ns timing information into a CPUSample - that tries
            # with the old-style to be a %age of CPU used in this time-slice.
            if delta_cpu_ns + delta_blkio_delay_ns + delta_swapin_delay_ns > 0:
#                               print "proc %s cpu_ns %g delta_cpu %g" % (cmd, cpu_ns, delta_cpu_ns)
                cpuSample = CPUSample('null', delta_cpu_ns, 0.0,
                                      delta_blkio_delay_ns,
                                      delta_swapin_delay_ns)
                process.samples.append(ProcessSample(time, state, cpuSample))

            process.last_cpu_ns = cpu_ns
            process.last_blkio_delay_ns = blkio_delay_ns
            process.last_swapin_delay_ns = swapin_delay_ns
        ltime = time

    if len (timed_blocks) < 2:
        return None

    startTime = timed_blocks[0][0]
    avgSampleLength = (ltime - startTime)/(len(timed_blocks)-1)

    return ProcessStats (writer, processMap, len (timed_blocks), avgSampleLength, startTime, ltime)

def _parse_proc_stat_log(file):
    samples = []
    ltimes = None
    for time, lines in _parse_timed_blocks(file):
        # skip emtpy lines
        if not lines:
            continue
        # CPU times {user, nice, system, idle, io_wait, irq, softirq}
        tokens = lines[0].split()
        times = [ int(token) for token in tokens[1:] ]
        if ltimes:
            user = float((times[0] + times[1]) - (ltimes[0] + ltimes[1]))
            system = float((times[2] + times[5] + times[6]) - (ltimes[2] + ltimes[5] + ltimes[6]))
            idle = float(times[3] - ltimes[3])
            iowait = float(times[4] - ltimes[4])

            aSum = max(user + system + idle + iowait, 1)
            samples.append( CPUSample(time, user/aSum, system/aSum, iowait/aSum) )

        ltimes = times
        # skip the rest of statistics lines
    return samples

def _parse_reduced_log(file, sample_class):
    samples = []
    for time, lines in _parse_timed_blocks(file):
        samples.append(sample_class(time, *[float(x) for x in lines[0].split()]))
    return samples

def _parse_proc_disk_stat_log(file):
    """
    Parse file for disk stats, but only look at the whole device, eg. sda,
    not sda1, sda2 etc. The format of relevant lines should be:
    {major minor name rio rmerge rsect ruse wio wmerge wsect wuse running use aveq}
    """
    disk_regex_re = re.compile ('^([hsv]d.|mtdblock\d|mmcblk\d|cciss/c\d+d\d+.*)$')

    # this gets called an awful lot.
    def is_relevant_line(linetokens):
        if len(linetokens) != 14:
            return False
        disk = linetokens[2]
        return disk_regex_re.match(disk)

    disk_stat_samples = []

    for time, lines in _parse_timed_blocks(file):
        sample = DiskStatSample(time)
        relevant_tokens = [linetokens for linetokens in map (lambda x: x.split(),lines) if is_relevant_line(linetokens)]

        for tokens in relevant_tokens:
            disk, rsect, wsect, use = tokens[2], int(tokens[5]), int(tokens[9]), int(tokens[12])
            sample.add_diskdata([rsect, wsect, use])

        disk_stat_samples.append(sample)

    disk_stats = []
    for sample1, sample2 in zip(disk_stat_samples[:-1], disk_stat_samples[1:]):
        interval = sample1.time - sample2.time
        if interval == 0:
            interval = 1
        sums = [ a - b for a, b in zip(sample1.diskdata, sample2.diskdata) ]
        readTput = sums[0] / 2.0 * 100.0 / interval
        writeTput = sums[1] / 2.0 * 100.0 / interval
        util = float( sums[2] ) / 10 / interval
        util = max(0.0, min(1.0, util))
        disk_stats.append(DiskSample(sample2.time, readTput, writeTput, util))

    return disk_stats

def _parse_reduced_proc_meminfo_log(file):
    """
    Parse file for global memory statistics with
    'MemTotal', 'MemFree', 'Buffers', 'Cached', 'SwapTotal', 'SwapFree' values
    (in that order) directly stored on one line.
    """
    used_values = ('MemTotal', 'MemFree', 'Buffers', 'Cached', 'SwapTotal', 'SwapFree',)

    mem_stats = []
    for time, lines in _parse_timed_blocks(file):
        sample = MemSample(time)
        for name, value in zip(used_values, lines[0].split()):
            sample.add_value(name, int(value))

        if sample.valid():
            mem_stats.append(DrawMemSample(sample))

    return mem_stats

def _parse_proc_meminfo_log(file):
    """
    Parse file for global memory statistics.
    The format of relevant lines should be: ^key: value( unit)?
    """
    used_values = ('MemTotal', 'MemFree', 'Buffers', 'Cached', 'SwapTotal', 'SwapFree',)

    mem_stats = []
    meminfo_re = re.compile(r'([^ \t:]+):\s*(\d+).*')

    for time, lines in _parse_timed_blocks(file):
        sample = MemSample(time)

        for line in lines:
            match = meminfo_re.match(line)
            if not match:
                raise ParseError("Invalid meminfo line \"%s\"" % line)
            sample.add_value(match.group(1), int(match.group(2)))

        if sample.valid():
            mem_stats.append(DrawMemSample(sample))

    return mem_stats

def _parse_monitor_disk_log(file):
    """
    Parse file with information about amount of diskspace used.
    The format of relevant lines should be: ^volume path: number-of-bytes?
    """
    disk_stats = []
    diskinfo_re = re.compile(r'^(.+):\s*(\d+)$')

    for time, lines in _parse_timed_blocks(file):
        sample = DiskSpaceSample(time)

        for line in lines:
            match = diskinfo_re.match(line)
            if not match:
                raise ParseError("Invalid monitor_disk line \"%s\"" % line)
            sample.add_value(match.group(1), int(match.group(2)))

        if sample.valid():
            disk_stats.append(sample)

    return disk_stats


# if we boot the kernel with: initcall_debug printk.time=1 we can
# get all manner of interesting data from the dmesg output
# We turn this into a pseudo-process tree: each event is
# characterised by a
# we don't try to detect a "kernel finished" state - since the kernel
# continues to do interesting things after init is called.
#
# sample input:
# [    0.000000] ACPI: FACP 3f4fc000 000F4 (v04 INTEL  Napa     00000001 MSFT 01000013)
# ...
# [    0.039993] calling  migration_init+0x0/0x6b @ 1
# [    0.039993] initcall migration_init+0x0/0x6b returned 1 after 0 usecs
def _parse_dmesg(writer, file):
    timestamp_re = re.compile ("^\[\s*(\d+\.\d+)\s*]\s+(.*)$")
    split_re = re.compile ("^(\S+)\s+([\S\+_-]+) (.*)$")
    processMap = {}
    idx = 0
    inc = 1.0 / 1000000
    kernel = Process(writer, idx, "k-boot", 0, 0.1)
    processMap['k-boot'] = kernel
    base_ts = False
    max_ts = 0
    for line in file.read().decode('utf-8').split('\n'):
        t = timestamp_re.match (line)
        if t is None:
#                       print "duff timestamp " + line
            continue

        time_ms = float (t.group(1)) * 1000
        # looks like we may have a huge diff after the clock
        # has been set up. This could lead to huge graph:
        # so huge we will be killed by the OOM.
        # So instead of using the plain timestamp we will
        # use a delta to first one and skip the first one
        # for convenience
        if max_ts == 0 and not base_ts and time_ms > 1000:
            base_ts = time_ms
            continue
        max_ts = max(time_ms, max_ts)
        if base_ts:
#                       print "fscked clock: used %f instead of %f" % (time_ms - base_ts, time_ms)
            time_ms -= base_ts
        m = split_re.match (t.group(2))

        if m is None:
            continue
#               print "match: '%s'" % (m.group(1))
        type = m.group(1)
        func = m.group(2)
        rest = m.group(3)

        if t.group(2).startswith ('Write protecting the') or \
           t.group(2).startswith ('Freeing unused kernel memory'):
            kernel.duration = time_ms / 10
            continue

#               print "foo: '%s' '%s' '%s'" % (type, func, rest)
        if type == "calling":
            ppid = kernel.pid
            p = re.match ("\@ (\d+)", rest)
            if p is not None:
                ppid = float (p.group(1)) // 1000
#                               print "match: '%s' ('%g') at '%s'" % (func, ppid, time_ms)
            name = func.split ('+', 1) [0]
            idx += inc
            processMap[func] = Process(writer, ppid + idx, name, ppid, time_ms / 10)
        elif type == "initcall":
#                       print "finished: '%s' at '%s'" % (func, time_ms)
            if func in processMap:
                process = processMap[func]
                process.duration = (time_ms / 10) - process.start_time
            else:
                print("corrupted init call for %s" % (func))

        elif type == "async_waiting" or type == "async_continuing":
            continue # ignore

    return processMap.values()

#
# Parse binary pacct accounting file output if we have one
# cf. /usr/include/linux/acct.h
#
def _parse_pacct(writer, file):
    # read LE int32
    def _read_le_int32(file):
        byts = file.read(4)
        return (ord(byts[0]))       | (ord(byts[1]) << 8) | \
               (ord(byts[2]) << 16) | (ord(byts[3]) << 24)

    parent_map = {}
    parent_map[0] = 0
    while file.read(1) != "": # ignore flags
        ver = file.read(1)
        if ord(ver) < 3:
            print("Invalid version 0x%x" % (ord(ver)))
            return None

        file.seek (14, 1)     # user, group etc.
        pid = _read_le_int32 (file)
        ppid = _read_le_int32 (file)
#               print "Parent of %d is %d" % (pid, ppid)
        parent_map[pid] = ppid
        file.seek (4 + 4 + 16, 1) # timings
        file.seek (16, 1)         # acct_comm
    return parent_map

def _parse_paternity_log(writer, file):
    parent_map = {}
    parent_map[0] = 0
    for line in file.read().decode('utf-8').split('\n'):
        if not line:
            continue
        elems = line.split(' ') # <Child> <Parent>
        if len (elems) >= 2:
#                       print "paternity of %d is %d" % (int(elems[0]), int(elems[1]))
            parent_map[int(elems[0])] = int(elems[1])
        else:
            print("Odd paternity line '%s'" % (line))
    return parent_map

def _parse_cmdline_log(writer, file):
    cmdLines = {}
    for block in file.read().decode('utf-8').split('\n\n'):
        lines = block.split('\n')
        if len (lines) >= 3:
#                       print "Lines '%s'" % (lines[0])
            pid = int (lines[0])
            values = {}
            values['exe'] = lines[1].lstrip(':')
            args = lines[2].lstrip(':').split('\0')
            args.pop()
            values['args'] = args
            cmdLines[pid] = values
    return cmdLines

def _parse_bitbake_buildstats(writer, state, filename, file):
    paths = filename.split("/")
    task = paths[-1]
    pn = paths[-2]
    start = None
    end = None
    for line in file:
        if line.startswith("Started:"):
            start = int(float(line.split()[-1]))
        elif line.startswith("Ended:"):
            end = int(float(line.split()[-1]))
    if start and end:
        state.add_process(pn + ":" + task, start, end)

def get_num_cpus(headers):
    """Get the number of CPUs from the system.cpu header property. As the
    CPU utilization graphs are relative, the number of CPUs currently makes
    no difference."""
    if headers is None:
        return 1
    if headers.get("system.cpu.num"):
        return max (int (headers.get("system.cpu.num")), 1)
    cpu_model = headers.get("system.cpu")
    if cpu_model is None:
        return 1
    mat = re.match(".*\\((\\d+)\\)", cpu_model)
    if mat is None:
        return 1
    return max (int(mat.group(1)), 1)

def _do_parse(writer, state, filename, file):
    writer.info("parsing '%s'" % filename)
    t1 = clock()
    name = os.path.basename(filename)
    if name == "proc_diskstats.log":
        state.disk_stats = _parse_proc_disk_stat_log(file)
    elif name == "reduced_proc_diskstats.log":
        state.disk_stats = _parse_reduced_log(file, DiskSample)
    elif name == "proc_stat.log":
        state.cpu_stats = _parse_proc_stat_log(file)
    elif name == "reduced_proc_stat.log":
        state.cpu_stats = _parse_reduced_log(file, CPUSample)
    elif name == "proc_meminfo.log":
        state.mem_stats = _parse_proc_meminfo_log(file)
    elif name == "reduced_proc_meminfo.log":
        state.mem_stats = _parse_reduced_proc_meminfo_log(file)
    elif name == "cmdline2.log":
        state.cmdline = _parse_cmdline_log(writer, file)
    elif name == "monitor_disk.log":
        state.monitor_disk = _parse_monitor_disk_log(file)
    elif not filename.endswith('.log'):
        _parse_bitbake_buildstats(writer, state, filename, file)
    t2 = clock()
    writer.info("  %s seconds" % str(t2-t1))
    return state

def parse_file(writer, state, filename):
    if state.filename is None:
        state.filename = filename
    basename = os.path.basename(filename)
    with open(filename, "rb") as file:
        return _do_parse(writer, state, filename, file)

def parse_paths(writer, state, paths):
    for path in paths:
        if state.filename is None:
            state.filename = path
        root, extension = os.path.splitext(path)
        if not(os.path.exists(path)):
            writer.warn("warning: path '%s' does not exist, ignoring." % path)
            continue
        #state.filename = path
        if os.path.isdir(path):
            files = sorted([os.path.join(path, f) for f in os.listdir(path)])
            state = parse_paths(writer, state, files)
        elif extension in [".tar", ".tgz", ".gz"]:
            if extension == ".gz":
                root, extension = os.path.splitext(root)
                if extension != ".tar":
                    writer.warn("warning: can only handle zipped tar files, not zipped '%s'-files; ignoring" % extension)
                    continue
            tf = None
            try:
                writer.status("parsing '%s'" % path)
                tf = tarfile.open(path, 'r:*')
                for name in tf.getnames():
                    state = _do_parse(writer, state, name, tf.extractfile(name))
            except tarfile.ReadError as error:
                raise ParseError("error: could not read tarfile '%s': %s." % (path, error))
            finally:
                if tf != None:
                    tf.close()
        else:
            state = parse_file(writer, state, path)
    return state

def split_res(res, options):
    """ Split the res into n pieces """
    res_list = []
    if options.num > 1:
        s_list = sorted(res.start.keys())
        frag_size = len(s_list) / float(options.num)
        # Need the top value
        if frag_size > int(frag_size):
            frag_size = int(frag_size + 1)
        else:
            frag_size = int(frag_size)

        start = 0
        end = frag_size
        while start < end:
            state = Trace(None, [], None)
            if options.full_time:
                state.min = min(res.start.keys())
                state.max = max(res.end.keys())
            for i in range(start, end):
                # Add this line for reference
                #state.add_process(pn + ":" + task, start, end)
                for p in res.start[s_list[i]]:
                    state.add_process(p, s_list[i], res.processes[p][1])
            start = end
            end = end + frag_size
            if end > len(s_list):
                end = len(s_list)
            res_list.append(state)
    else:
        res_list.append(res)
    return res_list
