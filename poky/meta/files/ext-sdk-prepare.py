#!/usr/bin/env python3

# Prepare the build system within the extensible SDK

import sys
import os
import subprocess
import signal

def reenable_sigint():
    signal.signal(signal.SIGINT, signal.SIG_DFL)

def run_command_interruptible(cmd):
    """
    Run a command with output displayed on the console, but ensure any Ctrl+C is
    processed only by the child process.
    """
    signal.signal(signal.SIGINT, signal.SIG_IGN)
    try:
        ret = subprocess.call(cmd, shell=True, preexec_fn=reenable_sigint)
    finally:
        signal.signal(signal.SIGINT, signal.SIG_DFL)
    return ret

def get_last_consolelog():
    '''Return the most recent console log file'''
    logdir = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'tmp', 'log', 'cooker')
    if os.path.exists(logdir):
        mcdir = os.listdir(logdir)
        if mcdir:
            logdir = os.path.join(logdir, mcdir[0])
            logfiles = [os.path.join(logdir, fn) for fn in os.listdir(logdir)]
            logfiles.sort(key=os.path.getmtime)
            if logfiles:
                return os.path.join(logdir, logfiles[-1])
    return None

def main():
    if len(sys.argv) < 2:
        print('Please specify output log file')
        return 1
    logfile = sys.argv[1]
    if len(sys.argv) < 3:
        sdk_targets = []
    else:
        sdk_targets = ' '.join(sys.argv[2:]).split()

    prserv = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'conf', 'prserv.inc')
    if os.path.isfile(prserv):
        with open(logfile, 'a') as logf:
            logf.write('Importing PR data...\n')

            ret = run_command_interruptible('bitbake-prserv-tool import %s' % prserv)

            lastlog = get_last_consolelog()
            if lastlog:
                with open(lastlog, 'r') as f:
                    for line in f:
                        logf.write(line)
            if ret:
                print('ERROR: PR data import failed: error log written to %s' % logfile)
                return ret

    if not sdk_targets:
        # Just do a parse so the cache is primed
        ret = run_command_interruptible('bitbake -p --quiet')
        return ret

    with open(logfile, 'a') as logf:
        logf.write('Preparing SDK for %s...\n' % ', '.join(sdk_targets))

        ret = run_command_interruptible('BB_SETSCENE_ENFORCE=1 bitbake --quiet %s' % ' '.join(sdk_targets))
        if not ret:
            ret = run_command_interruptible('bitbake --quiet build-sysroots')
        lastlog = get_last_consolelog()
        if lastlog:
            with open(lastlog, 'r') as f:
                for line in f:
                    logf.write(line)
        if ret:
            print('ERROR: SDK preparation failed: error log written to %s' % logfile)
            return ret

if __name__ == "__main__":
    try:
        ret = main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
