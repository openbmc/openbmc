#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# SPDX-License-Identifier:     BSD-3-Clause
# Copyright (C) 2015 Enea Software AB
# Author: Thomas Lundström <thomas.lundstrom@enea.com>

# The script measures interrupt latency together with different types of
# system load. This is done using the programs cyclictest and stress.
#
# The main output is:
#
#   Best case (min) latency
#     This has very limited value, but is presented since it can be done
#     easily
#
#   Average latency
#     This value is of interrest for throughput oriented systems. Limited
#     value for a real-time system. Also presented because it is easy to do.
#
#   Worst case (max) latency
#     This is the interesting number for a real-time system. The number
#     presented is (of cource) the largest number observed. The challenge
#     is to know how the observed worst case relates to the actual worst case.
#
#     To get an indication of the confidence, the following method is used:
#     1) Instead of one long run, the measurement is made as a set of shorter
#        runs. The number of runs must be a power of 2 for reasons that will
#        shorlty be obvious
#
#     2) First, a list of the max values are created.
#
#     3) The smallest value in that list is recorded.
#
#     4) Then a new list is create by taking the max value of each pair of
#        values in the original list. In this list the smallest value is
#        recorded.
#
#     5) Step 3 is repeated until there is only one value in the list. See
#        example below:
#
#        Samples:
#          |  44 |     |     |     |     |
#          |  77 |  77 |     |     |     |
#          | 118 |     |     |     |     |
#          | 119 | 119 | 119 |     |     |
#          | 138 |     |     |     |     |
#          |  57 | 138 |     |     |     |
#          | 175 |     |     |     |     |
#          | 130 | 175 | 175 | 175 |     |
#          |  54 |     |     |     |     |
#          | 150 | 150 |     |     |     |
#          |  47 |     |     |     |     |
#          |  59 |  59 | 150 |     |     |
#          | 199 |     |     |     |     |
#          | 115 | 199 |     |     |     |
#          | 177 |     |     |     |     |
#          | 129 | 177 | 199 | 199 | 199 |
#
#        Smallest value:
#          |  44 |  59 | 119 | 175 | 199 |
#
#     6) The generated list of smallest values is analyzed. In this case, it
#        can be observed that the values are increasing significantly through
#        the entire list, which leads to the conclusion that the number of
#        samples is too small.
#        If instead the list had been (167, 191, 196, 199, 199), there had
#        been a very small, or no, increase at the end of the list. We might
#        then suspect that the number of samples is probably large enough.
#        There is however no guarantee for that.
#
#     Steps 1-2 are done in run_cyclictest_suite
#     Steps 3-5 are done in gen_minmax_list.
#     Step 6 needs to be done manually since there is (yet) no well defined
#     FAIL criterion and a theoretically solid PASS criterion may never be
#     available.

import multiprocessing
import os
import re
import signal
import subprocess
import time
import traceback

# See comment on the function set_hung_tmo
has_hung_task_detection = True

#-------------------------------------------------------------------------------

class TestFail(Exception):
        def __init__(self, msg):
                self.msg = msg

        def __str__(self):
                return "Test failure: (" + self.msg + ")"

#-------------------------------------------------------------------------------

def tc_name(sub_name):
        return "rt_bmark.intlat." + sub_name

#-------------------------------------------------------------------------------
# log() does the same job as print except that a '#' is added at the beginning
# of each line. This causes TEFEL to ignore it

def log(*msg):
        tmp = "".join(map(str, msg)) # 'map(str, ...' allows numbers
        for line in tmp.splitlines():
                print("#", line)

#-------------------------------------------------------------------------------
# Like log(), but with a timestamp added

def log_ts(*msg):
        ts = time.localtime()
        stamp = "%2d:%02d:%02d: " % (ts.tm_hour, ts.tm_min, ts.tm_sec)
        log(stamp, *msg)

#-------------------------------------------------------------------------------

def log_test_header(seq_no, nr_of_tests, name):
        log("=" * 78)
        log()
        log("  Test case (%d/%d): %s" % (seq_no, nr_of_tests, tc_name(name)))
        log()
        log("." * 78)
        log()

#-------------------------------------------------------------------------------

def start_stress(*args):
        stress_cmd         = [ "stress-ng" ]
        added_stress_types = []
        req_stress_types   = set(args)
        cpu_cnt            = str(multiprocessing.cpu_count())

        # The function cond_add_stress appends the options to the stress
        # command if the stress type is in the set of requested stress types

        def cond_add_stress(stress_type, options):
                if stress_type in req_stress_types:
                        req_stress_types.remove(stress_type)
                        added_stress_types.append(stress_type)
                        stress_cmd.extend(options)

        #----------

        cond_add_stress("io",  ["-i", cpu_cnt])
        cond_add_stress("cpu", ["-c", cpu_cnt])
        cond_add_stress("hdd", ["-d", cpu_cnt, "--hdd-bytes", "20M"])
        cond_add_stress("vm",  ["-m", cpu_cnt, "--vm-bytes", "10M"])

        unknown = ", ".join(req_stress_types)
        if unknown != "":
            raise TestFail("Unknown stress type(s): %s" % unknown)

        if not added_stress_types:
                log("No stress requested")
                return None

        added          = "+".join(added_stress_types)
        stress_cmd_str = " ".join(stress_cmd)

        log("Starting stress(", added, ")")
        log("  Command: '", stress_cmd_str, "'")
        log()

        # start_new_session causes stress to be executed in a separate
        # session, => it gets a new process group (incl. children). It
        # can then be terminated using os.killpg in end_stress without
        # terminating this script.

        p = subprocess.Popen(stress_cmd, start_new_session=True)

        return p

#-------------------------------------------------------------------------------

def end_stress(p):
        if p is None:
                # The value None indicates that no stress scenario was started
                return

        if p.poll() is not None:
                raise TestFail("stress prematurely terminated.")

        os.killpg(os.getpgid(p.pid), signal.SIGTERM)
        log("Terminated stress")

#-------------------------------------------------------------------------------

def us2hms_str(us):
        s = (us+500000) // 1000000 # Round microseconds to s
        m = s//60
        s -= 60*m;
        h = m//60
        m -= 60*h

        return "%d:%02d:%02d" % (h, m, s)

#-------------------------------------------------------------------------------
# Sometime the hung task supervision is triggered during execution of
# cyclictest (cyclictest starves stress). To avoid that, the supervision
# is temporarily disabled

def set_hung_tmo(new_tmo):
        global has_hung_task_detection

        tmo_file = "/proc/sys/kernel/hung_task_timeout_secs"

        if not has_hung_task_detection:
                return

        if not os.access(tmo_file, os.W_OK):
                log("Hung task detection not supported")
                log("  (File ", tmo_file, " not found)")
                has_hung_task_detection = False
                return

        orig_tmo = int(subprocess.check_output(["cat", tmo_file]).strip())
        if new_tmo != orig_tmo:
                cmd = ( "echo " + str(new_tmo) + " > " + tmo_file )
                subprocess.check_output(cmd, shell=True)
                log("Changed timeout for detection of hung tasks: ",
                    orig_tmo, " -> ", new_tmo)

        return orig_tmo

#-------------------------------------------------------------------------------

def gen_minmax_list(max_list):
        res = [min(max_list)]

        while True:
                tmp = max_list
                max_list = []
                while tmp:
                        max_list.append(max(tmp.pop(0), tmp.pop(0)))

                res.append(min(max_list))

                if len(max_list) < 2:
                        return res

#-------------------------------------------------------------------------------
# Parameters for cyclictest:
#
# On the -S option (from cyclictest.c):
#  -S implies options -a -t -n and same priority of all threads
#    -a: One thread per core
#    -n: use clock_nanosleep instead of posix interval timers
#    -t: (without argument) Set number of threads to the number
#         of cpus

interval_core_0 = 100     # Timer interval on core 0 [us]
interval_delta  = 20      # Interval increment for each core [us]
loop_count      = 30000   # Number of loops (on core 0).

cmd = ("cyclictest",
       "-S",             # Standard SMP testing. See below
       "-p", "99",       # RT priority 99
       "-q",             # Quiet mode, i.e. print only a summary
       "-i", str(interval_core_0),
       "-d", str(interval_delta),
       "-l", str(loop_count)
       )
rex = re.compile(r"C:\s*(\d+).*Min:\s*(\d+).*Avg:\s*(\d+).*Max:\s*(\d+)")

def run_cyclictest_once():
        res = subprocess.check_output(cmd)

        # minlist and maxlist are lists with the extremes for each core
        # avg_cnt is the sum of cycles for all cores
        # avg_sum is the sum of (cycle count*average) for each core
        #     Since cyclictest runs different number of cycles on
        #     different cores, avg_sum/avg_cnt gives a more accurate
        #     value of the overall average than just taking the average
        #     of each core's averages

        minlist = []
        maxlist = []
        avg_sum = 0
        avg_cnt = 0

        for line in res.splitlines():
                m = rex.search(line)
                if m is not None:
                        minlist.append(int(m.group(2)))
                        maxlist.append(int(m.group(4)))
                        n = int(m.group(1))
                        avg_sum += n * int(m.group(3))
                        avg_cnt += n

        return min(minlist), [avg_sum, avg_cnt], max(maxlist)

#-------------------------------------------------------------------------------
# A precondition for the tracking of min-max values is that
# the suite size os a power of 2.

N          = 5
suite_size = 2**N

est_exec_time_once  = interval_core_0 * loop_count
est_exec_time_suite = suite_size * est_exec_time_once

def run_cyclictest_suite():
        log("Starting cyclictest")
        log("  Command          : ", " ".join(cmd))
        log("  Number of cycles : ", loop_count*suite_size,
            " (", suite_size, " sets of ", loop_count, " cycles)")
        log("  Exec. time (est) : ", us2hms_str(est_exec_time_suite))
        log()

        orig_tmo = set_hung_tmo(0) # 0 => Disable

        # float('inf') emulates infinity. At least in the sense that it is
        # guaranteed to be larger than any actual number.
        ack_min = float('inf')
        ack_avg = [0, 0]

        log()
        log_ts("Start of execution")
        t = time.time()
        max_list = []

        for i in range(0, suite_size):
                tmp_min, tmp_avg, tmp_max = run_cyclictest_once()

                msg = "%2d/%2d:" % (i+1, suite_size)
                msg += " min: %4d" % tmp_min
                msg += " avg: %5.1f" % (float(tmp_avg[0])/tmp_avg[1])
                msg += " max: %4d" % tmp_max
                log_ts(msg)

                # Track minimum value
                if tmp_min < ack_min:
                        ack_min = tmp_min

                # Track smallest max value
                max_list.append(tmp_max)

                ack_avg[0] += tmp_avg[0]
                ack_avg[1] += tmp_avg[1]

        t = time.time()-t
        log_ts("Cyclictest completed. Actual execution time:",
               us2hms_str(t*1000000))
        log()
        set_hung_tmo(orig_tmo)

        return ack_min, float(ack_avg[0])/ack_avg[1], gen_minmax_list(max_list)

#-------------------------------------------------------------------------------

class cyclictest_runner:
        def run_test(self, seq_no, nr_of_tests, name, stressparams):

                try:
                        log_test_header(seq_no, nr_of_tests, name)

                        p = start_stress(*stressparams)

                        bm_min, bm_avg, bm_max_list = run_cyclictest_suite()

                        end_stress(p)

                        bm_max = bm_max_list[-1]

                        log()
                        log("Min: %d us" % bm_min)
                        log("Avg: %.1f us" % bm_avg)
                        log("Max: %d us" % bm_max)
                        log()
                        log("Max list: ", bm_max_list)
                        log()
                        log("PASS")

                        print()
                        print(tc_name(name), "[Min/us,Avg/us,Max/us]:",)
                        print("%d,%.1f,%d" % (bm_min,bm_avg, bm_max))
                        print("PASS:", tc_name(name))
                        print()

                except Exception:
                        log()
                        log("Exception!")
                        log()
                        log("Traceback:", traceback.format_exc())
                        log()
                        log("WD: ", os.getcwd())
                        log()
                        log("FAIL")
                        print()
                        print("FAIL:", tc_name(name))
                        print()

#-------------------------------------------------------------------------------

runner = cyclictest_runner()

tests = (("no_stress", []),
         ("cpu",  ["cpu"]),
         ("hdd",  ["hdd"]),
         ("io",   ["io"]),
         ("vm",   ["vm"]),
         ("full", ["io", "cpu", "hdd", "vm"]),
         )

nr_of_tests = len(tests)
for seq_no, params in enumerate(tests, start=1):
        runner.run_test(seq_no, nr_of_tests, *params)
