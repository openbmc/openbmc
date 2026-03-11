#! /usr/bin/env python3
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os

from django.test import TestCase
from django.core import management

from orm.models import signal_runbuilds

import threading
import time
import subprocess
import signal

import logging


class KillRunbuilds(threading.Thread):
    """ Kill the runbuilds process after an amount of time """
    def __init__(self, *args, **kwargs):
        super(KillRunbuilds, self).__init__(*args, **kwargs)
        self.daemon = True

    def run(self):
        time.sleep(5)
        signal_runbuilds()
        time.sleep(1)

        pidfile_path = os.path.join(os.environ.get("BUILDDIR", "."),
                                    ".runbuilds.pid")

        try:
            with open(pidfile_path) as pidfile:
                pid = pidfile.read()
                os.kill(int(pid), signal.SIGTERM)
        except ProcessLookupError:
            logging.warning("Runbuilds not running or already killed")


class TestCommands(TestCase):
    """ Sanity test that runbuilds executes OK """

    def setUp(self):
        os.environ.setdefault("DJANGO_SETTINGS_MODULE",
                              "toastermain.settings_test")
        os.environ.setdefault("BUILDDIR",
                              "/tmp/")

        # Setup a real database if needed for runbuilds process
        # to connect to
        management.call_command('migrate')

    def test_runbuilds_command(self):
        kill_runbuilds = KillRunbuilds()
        kill_runbuilds.start()

        manage_py = os.path.join(
            os.path.dirname(os.path.abspath(__file__)),
            os.pardir,
            os.pardir,
            "manage.py")

        command = "%s runbuilds" % manage_py

        process = subprocess.Popen(command,
                                   shell=True,
                                   stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE)

        (out, err) = process.communicate()
        process.wait()

        self.assertNotEqual(process.returncode, 1,
                            "Runbuilds returned an error %s" % err)
