#
# SPDX-License-Identifier: GPL-2.0-only
#
# Implements system state sampling. Called by buildstats.bbclass.
# Because it is a real Python module, it can hold persistent state,
# like open log files and the time of the last sampling.

import time
import re
import bb.event

class SystemStats:
    def __init__(self, d):
        bn = d.getVar('BUILDNAME')
        bsdir = os.path.join(d.getVar('BUILDSTATS_BASE'), bn)
        bb.utils.mkdirhier(bsdir)

        self.proc_files = []
        for filename, handler in (
                ('diskstats', self._reduce_diskstats),
                ('meminfo', self._reduce_meminfo),
                ('stat', self._reduce_stat),
        ):
            # The corresponding /proc files might not exist on the host.
            # For example, /proc/diskstats is not available in virtualized
            # environments like Linux-VServer. Silently skip collecting
            # the data.
            if os.path.exists(os.path.join('/proc', filename)):
                # In practice, this class gets instantiated only once in
                # the bitbake cooker process.  Therefore 'append' mode is
                # not strictly necessary, but using it makes the class
                # more robust should two processes ever write
                # concurrently.
                destfile = os.path.join(bsdir, '%sproc_%s.log' % ('reduced_' if handler else '', filename))
                self.proc_files.append((filename, open(destfile, 'ab'), handler))
        self.monitor_disk = open(os.path.join(bsdir, 'monitor_disk.log'), 'ab')
        # Last time that we sampled /proc data resp. recorded disk monitoring data.
        self.last_proc = 0
        self.last_disk_monitor = 0
        # Minimum number of seconds between recording a sample. This
        # becames relevant when we get called very often while many
        # short tasks get started. Sampling during quiet periods
        # depends on the heartbeat event, which fires less often.
        self.min_seconds = 1

        self.meminfo_regex = re.compile(rb'^(MemTotal|MemFree|Buffers|Cached|SwapTotal|SwapFree):\s*(\d+)')
        self.diskstats_regex = re.compile(rb'^([hsv]d.|mtdblock\d|mmcblk\d|cciss/c\d+d\d+.*)$')
        self.diskstats_ltime = None
        self.diskstats_data = None
        self.stat_ltimes = None

    def close(self):
        self.monitor_disk.close()
        for _, output, _ in self.proc_files:
            output.close()

    def _reduce_meminfo(self, time, data):
        """
        Extracts 'MemTotal', 'MemFree', 'Buffers', 'Cached', 'SwapTotal', 'SwapFree'
        and writes their values into a single line, in that order.
        """
        values = {}
        for line in data.split(b'\n'):
            m = self.meminfo_regex.match(line)
            if m:
                values[m.group(1)] = m.group(2)
        if len(values) == 6:
            return (time,
                    b' '.join([values[x] for x in
                               (b'MemTotal', b'MemFree', b'Buffers', b'Cached', b'SwapTotal', b'SwapFree')]) + b'\n')

    def _diskstats_is_relevant_line(self, linetokens):
        if len(linetokens) != 14:
            return False
        disk = linetokens[2]
        return self.diskstats_regex.match(disk)

    def _reduce_diskstats(self, time, data):
        relevant_tokens = filter(self._diskstats_is_relevant_line, map(lambda x: x.split(), data.split(b'\n')))
        diskdata = [0] * 3
        reduced = None
        for tokens in relevant_tokens:
            # rsect
            diskdata[0] += int(tokens[5])
            # wsect
            diskdata[1] += int(tokens[9])
            # use
            diskdata[2] += int(tokens[12])
        if self.diskstats_ltime:
            # We need to compute information about the time interval
            # since the last sampling and record the result as sample
            # for that point in the past.
            interval = time - self.diskstats_ltime
            if interval > 0:
                sums = [ a - b for a, b in zip(diskdata, self.diskstats_data) ]
                readTput = sums[0] / 2.0 * 100.0 / interval
                writeTput = sums[1] / 2.0 * 100.0 / interval
                util = float( sums[2] ) / 10 / interval
                util = max(0.0, min(1.0, util))
                reduced = (self.diskstats_ltime, (readTput, writeTput, util))

        self.diskstats_ltime = time
        self.diskstats_data = diskdata
        return reduced


    def _reduce_nop(self, time, data):
        return (time, data)

    def _reduce_stat(self, time, data):
        if not data:
            return None
        # CPU times {user, nice, system, idle, io_wait, irq, softirq} from first line
        tokens = data.split(b'\n', 1)[0].split()
        times = [ int(token) for token in tokens[1:] ]
        reduced = None
        if self.stat_ltimes:
            user = float((times[0] + times[1]) - (self.stat_ltimes[0] + self.stat_ltimes[1]))
            system = float((times[2] + times[5] + times[6]) - (self.stat_ltimes[2] + self.stat_ltimes[5] + self.stat_ltimes[6]))
            idle = float(times[3] - self.stat_ltimes[3])
            iowait = float(times[4] - self.stat_ltimes[4])

            aSum = max(user + system + idle + iowait, 1)
            reduced = (time, (user/aSum, system/aSum, iowait/aSum))

        self.stat_ltimes = times
        return reduced

    def sample(self, event, force):
        now = time.time()
        if (now - self.last_proc > self.min_seconds) or force:
            for filename, output, handler in self.proc_files:
                with open(os.path.join('/proc', filename), 'rb') as input:
                    data = input.read()
                    if handler:
                        reduced = handler(now, data)
                    else:
                        reduced = (now, data)
                    if reduced:
                        if isinstance(reduced[1], bytes):
                            # Use as it is.
                            data = reduced[1]
                        else:
                            # Convert to a single line.
                            data = (' '.join([str(x) for x in reduced[1]]) + '\n').encode('ascii')
                        # Unbuffered raw write, less overhead and useful
                        # in case that we end up with concurrent writes.
                        os.write(output.fileno(),
                                 ('%.0f\n' % reduced[0]).encode('ascii') +
                                 data +
                                 b'\n')
            self.last_proc = now

        if isinstance(event, bb.event.MonitorDiskEvent) and \
           ((now - self.last_disk_monitor > self.min_seconds) or force):
            os.write(self.monitor_disk.fileno(),
                     ('%.0f\n' % now).encode('ascii') +
                     ''.join(['%s: %d\n' % (dev, sample.total_bytes - sample.free_bytes)
                              for dev, sample in event.disk_usage.items()]).encode('ascii') +
                     b'\n')
            self.last_disk_monitor = now
