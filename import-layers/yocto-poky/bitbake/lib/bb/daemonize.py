"""
Python Daemonizing helper

Originally based on code Copyright (C) 2005 Chad J. Schroeder but now heavily modified
to allow a function to be daemonized and return for bitbake use by Richard Purdie
"""

import os
import sys
import io
import traceback

def createDaemon(function, logfile):
    """
    Detach a process from the controlling terminal and run it in the
    background as a daemon, returning control to the caller.
    """

    try:
        # Fork a child process so the parent can exit.  This returns control to
        # the command-line or shell.  It also guarantees that the child will not
        # be a process group leader, since the child receives a new process ID
        # and inherits the parent's process group ID.  This step is required
        # to insure that the next call to os.setsid is successful.
        pid = os.fork()
    except OSError as e:
        raise Exception("%s [%d]" % (e.strerror, e.errno))

    if (pid == 0):      # The first child.
        # To become the session leader of this new session and the process group
        # leader of the new process group, we call os.setsid().  The process is
        # also guaranteed not to have a controlling terminal.
        os.setsid()
        try:
            # Fork a second child and exit immediately to prevent zombies.  This
            # causes the second child process to be orphaned, making the init
            # process responsible for its cleanup.  And, since the first child is
            # a session leader without a controlling terminal, it's possible for
            # it to acquire one by opening a terminal in the future (System V-
            # based systems).  This second fork guarantees that the child is no
            # longer a session leader, preventing the daemon from ever acquiring
            # a controlling terminal.
            pid = os.fork()     # Fork a second child.
        except OSError as e:
            raise Exception("%s [%d]" % (e.strerror, e.errno))

        if (pid != 0):
            # Parent (the first child) of the second child.
            # exit() or _exit()?
            # _exit is like exit(), but it doesn't call any functions registered
            # with atexit (and on_exit) or any registered signal handlers.  It also
            # closes any open file descriptors.  Using exit() may cause all stdio
            # streams to be flushed twice and any temporary files may be unexpectedly
            # removed.  It's therefore recommended that child branches of a fork()
            # and the parent branch(es) of a daemon use _exit().
            os._exit(0)
    else:
        os.waitpid(pid, 0)
        return

    # The second child.

    # Replace standard fds with our own
    si = open('/dev/null', 'r')
    os.dup2(si.fileno(), sys.stdin.fileno())

    try:
        so = open(logfile, 'a+')
        se = so
        os.dup2(so.fileno(), sys.stdout.fileno())
        os.dup2(se.fileno(), sys.stderr.fileno())
    except io.UnsupportedOperation:
        sys.stdout = open(logfile, 'a+')
        sys.stderr = sys.stdout

    try:
        function()
    except Exception as e:
        traceback.print_exc()
    finally:
        bb.event.print_ui_queue()
        os._exit(0)
