#!/usr/bin/env python

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

if len(sys.argv) != 3:
    print("Incorrect parameters")
    sys.exit(1)

pty = open(sys.argv[1], "w+b", 0)
parent = int(sys.argv[2])

# Don't buffer output by line endings
sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)
sys.stdin = os.fdopen(sys.stdin.fileno(), 'r', 0)
nonblockingfd(pty)
nonblockingfd(sys.stdin)


histfile = os.path.expanduser("~/.oedevpyshell-history")
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
    pty.write(str(os.getpid()) + "\n")
    while True:
        try:
            writers = []
            if i:
                writers.append(sys.stdout)
            (ready, _, _) = select.select([pty, sys.stdin], writers , [], 0)
            try:
                if pty in ready:
                    i = i + pty.read()
                if i:
                    # Write a page at a time to avoid overflowing output 
                    # d.keys() is a good way to do that
                    sys.stdout.write(i[:4096])
                    i = i[4096:]
                if sys.stdin in ready:
                    echonocbreak(sys.stdin.fileno())
                    o = raw_input()
                    cbreaknoecho(sys.stdin.fileno())
                    pty.write(o + "\n")
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
