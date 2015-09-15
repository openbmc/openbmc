#!/usr/bin/python

# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2015 Alexandru Damian for Intel Corp.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

# Program to run the next task listed from the backlog.txt; designed to be
# run from crontab.

from __future__ import print_function
import sys, os, config, shellutils
from shellutils import ShellCmdException

# Import smtplib for the actual sending function
import smtplib

# Import the email modules we'll need
from email.mime.text import MIMEText

def _take_lockfile():
    return shellutils.lockfile(shellutils.mk_lock_filename())


def read_next_task_by_state(task_state, task_name=None):
    if not os.path.exists(os.path.join(os.path.dirname(__file__), config.BACKLOGFILE)):
        return None
    os.rename(config.BACKLOGFILE, config.BACKLOGFILE + ".tmp")
    task = None
    with open(config.BACKLOGFILE + ".tmp", "r") as f_in:
        with open(config.BACKLOGFILE, "w") as f_out:
            for line in f_in.readlines():
                if task is None:
                    fields = line.strip().split("|", 2)
                    if fields[1] == task_state:
                        if task_name is None or task_name == fields[0]:
                            task = fields[0]
                            print("Updating %s %s to %s" % (task, task_state, config.TASKS.next_task(task_state)))
                            line = "%s|%s\n" % (task, config.TASKS.next_task(task_state))
                f_out.write(line)
    os.remove(config.BACKLOGFILE + ".tmp")
    return task

def send_report(task_name, plaintext, errtext=None):
    if errtext is None:
        msg = MIMEText(plaintext)
    else:
        if plaintext is None:
            plaintext = ""
        msg = MIMEText("--STDOUT dump--\n\n%s\n\n--STDERR dump--\n\n%s" % (plaintext, errtext))

    msg['Subject'] = "[review-request] %s - smoke test results" % task_name
    msg['From'] = config.OWN_EMAIL_ADDRESS
    msg['To'] = config.REPORT_EMAIL_ADDRESS

    smtp_connection = smtplib.SMTP("localhost")
    smtp_connection.sendmail(config.OWN_EMAIL_ADDRESS, [config.REPORT_EMAIL_ADDRESS], msg.as_string())
    smtp_connection.quit()

def main():
    # we don't do anything if we have another instance of us running
    lock_file = _take_lockfile()

    if lock_file is None:
        if config.DEBUG:
            print("Concurrent script in progress, exiting")
        sys.exit(1)

    next_task = read_next_task_by_state(config.TASKS.PENDING)
    if next_task is not None:
        print("Next task is", next_task)
        errtext = None
        out = None
        try:
            out = shellutils.run_shell_cmd("%s %s" % (os.path.join(os.path.dirname(__file__), "runner.py"), next_task))
        except ShellCmdException as exc:
            print("Failed while running the test runner: %s", exc)
            errtext = exc.__str__()
        send_report(next_task, out, errtext)
        read_next_task_by_state(config.TASKS.INPROGRESS, next_task)
    else:
        print("No task")

    shellutils.unlockfile(lock_file)


if __name__ == "__main__":
    main()
