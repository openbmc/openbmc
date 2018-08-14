#! /usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016 Intel Corporation
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

import os

from django.test import TestCase
from django.core import management

from orm.models import signal_runbuilds

import threading
import time
import subprocess
import signal


class KillRunbuilds(threading.Thread):
    """ Kill the runbuilds process after an amount of time """
    def __init__(self, *args, **kwargs):
        super(KillRunbuilds, self).__init__(*args, **kwargs)
        self.setDaemon(True)

    def run(self):
        time.sleep(5)
        signal_runbuilds()
        time.sleep(1)

        pidfile_path = os.path.join(os.environ.get("BUILDDIR", "."),
                                    ".runbuilds.pid")

        with open(pidfile_path) as pidfile:
            pid = pidfile.read()
            os.kill(int(pid), signal.SIGTERM)


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
