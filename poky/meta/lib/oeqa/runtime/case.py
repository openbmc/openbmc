#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

import os
import subprocess
import time
from oeqa.core.case import OETestCase
from oeqa.utils.package_manager import install_package, uninstall_package

class OERuntimeTestCase(OETestCase):
    # target instance set by OERuntimeTestLoader.
    target = None

    def setUp(self):
        super(OERuntimeTestCase, self).setUp()
        install_package(self)

    def tearDown(self):
        super(OERuntimeTestCase, self).tearDown()
        uninstall_package(self)

def run_network_serialdebug(runner):
    status, output = runner.run_serial("ip addr")
    print("ip addr on target: %s %s" % (output, status))
    status, output = runner.run_serial("ping -c 1 %s" % self.target.server_ip)
    print("ping on target for %s: %s %s" % (self.target.server_ip, output, status))
    status, output = runner.run_serial("ping -c 1 %s" % self.target.ip)
    print("ping on target for %s: %s %s" % (self.target.ip, output, status))
    # Have to use a full path for netstat which isn't in HOSTTOOLS
    subprocess.call(["/usr/bin/netstat", "-tunape"])
    subprocess.call(["/usr/bin/netstat", "-ei"])
    subprocess.call(["ps", "-awx"], shell=True)
    print("PID: %s %s" % (str(os.getpid()), time.time()))
