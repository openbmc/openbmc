#!/usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import sys
import time
import select
import fcntl
import termios
import readline
import signal

def nonblockingfd(fd):
    fcntl.fcntl(fd, fcntl.F_SETFL, fcntl.fcntl(fd, fcntl.F_GETFL) | os.O_NONBLOCK)

def echonocbreak(fd):
    old = termios.tcgetattr(fd)
    old[3] = old[3] | termios.ECHO | termios.ICANON
    termios.tcsetattr(fd, termios.TCSADRAIN, old)

def cbreaknoecho(fd):
    old = termios.tcgetattr(fd)
    old[3] = old[3] &~ termios.ECHO &~ termios.ICANON
    termios.tcsetattr(fd, termios.TCSADRAIN, old)

if len(sys.argv) != 3 or sys.argv[1] in ('-h', '--help'):
    print('oepydevshell-internal.py: error: the following arguments are required: pty, pid\n'
          'Usage: oepydevshell-internal.py pty pid\n\n'
          'OpenEmbedded oepydevshell-internal.py - internal script called from meta/classes/devshell.bbclass\n\n'
          'arguments:\n'
          '  pty                   pty device name\n'
          '  pid                   parent process id\n\n'
          'options:\n'
          '  -h, --help            show this help message and exit\n')
    sys.exit(2)

pty = open(sys.argv[1], "w+b", 0)
parent = int(sys.argv[2])

nonblockingfd(pty)
nonblockingfd(sys.stdin)


histfile = os.path.expanduser("~/.oepydevshell-history")
readline.parse_and_bind("tab: complete")
try:
    readline.read_history_file(histfile)
except IOError:
    pass

try:

    i = ""
    o = ""
    # Need cbreak/noecho whilst in select so we trigger on any keypress
    cbreaknoecho(sys.stdin.fileno())
    # Send our PID to the other end so they can kill us.
    pty.write(str(os.getpid()).encode('utf-8') + b"\n")
    while True:
        try:
            writers = []
            if i:
                writers.append(sys.stdout)
            (ready, _, _) = select.select([pty, sys.stdin], writers , [], 0)
            try:
                if pty in ready:
                    readdata = pty.read()
                    if readdata:
                        i = i + readdata.decode('utf-8')
                if i:
                    # Write a page at a time to avoid overflowing output 
                    # d.keys() is a good way to do that
                    sys.stdout.write(i[:4096])
                    sys.stdout.flush()
                    i = i[4096:]
                if sys.stdin in ready:
                    echonocbreak(sys.stdin.fileno())
                    o = input().encode('utf-8')
                    cbreaknoecho(sys.stdin.fileno())
                    pty.write(o + b"\n")
            except (IOError, OSError) as e:
                if e.errno == 11:
                    continue
                if e.errno == 5:
                    sys.exit(0)
                raise
            except EOFError:
                sys.exit(0)
        except KeyboardInterrupt:
            os.kill(parent, signal.SIGINT)

except SystemExit:
    pass
except Exception as e:
    import traceback
    print("Exception in oepydehshell-internal: " + str(e))
    traceback.print_exc()
    time.sleep(5)
finally:
    readline.write_history_file(histfile)
