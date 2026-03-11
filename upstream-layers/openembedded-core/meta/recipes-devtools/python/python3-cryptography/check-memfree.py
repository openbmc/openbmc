#!/usr/bin/env python3
# https://stackoverflow.com/questions/22102999/get-total-physical-memory-in-python/28161352
import sys
meminfo = dict((i.split()[0].rstrip(':'),int(i.split()[1])) for i in open('/proc/meminfo').readlines())
mem_free = meminfo['MemTotal']/1024./1024.
if mem_free < 2.:
    print("Insufficient free memory({:.3f}): requires > 2 GB".format(mem_free))
    sys.exit(1)
else:
    print("Free memory: {:.3f} GB".format(mem_free))
