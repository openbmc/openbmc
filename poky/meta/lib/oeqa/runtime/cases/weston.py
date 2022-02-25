#
# SPDX-License-Identifier: MIT
#

from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.data import skipIfNotFeature
from oeqa.runtime.decorator.package import OEHasPackage
import threading
import time

class WestonTest(OERuntimeTestCase):
    weston_log_file = '/tmp/weston-2.log'

    @classmethod
    def tearDownClass(cls):
        cls.tc.target.run('rm %s' % cls.weston_log_file)

    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['weston'])
    def test_weston_running(self):
        cmd ='%s | grep [w]eston-desktop-shell' % self.tc.target_cmds['ps']
        status, output = self.target.run(cmd)
        msg = ('Weston does not appear to be running %s' %
              self.target.run(self.tc.target_cmds['ps'])[1])
        self.assertEqual(status, 0, msg=msg)

    def get_processes_of(self, target, error_msg):
        status, output = self.target.run('pidof %s' % target)
        self.assertEqual(status, 0, msg='Retrieve %s (%s) processes error: %s' % (target, error_msg, output))
        return output.split(" ")

    def get_weston_command(self, cmd):
        return 'export XDG_RUNTIME_DIR=/run/user/`id -u weston`; export WAYLAND_DISPLAY=wayland-1; %s' % cmd

    def run_weston_init(self):
        if 'systemd' in self.tc.td['VIRTUAL-RUNTIME_init_manager']:
            self.target.run('systemd-run --collect --unit=weston-ptest.service --uid=0 -p PAMName=login -p TTYPath=/dev/tty6 -E XDG_RUNTIME_DIR=/tmp -E WAYLAND_DISPLAY=wayland-0 /usr/bin/weston --socket=wayland-1 --log=%s' % self.weston_log_file)
        else:
            self.target.run(self.get_weston_command('openvt -- weston --socket=wayland-2 --log=%s' % self.weston_log_file))

    def get_new_wayland_processes(self, existing_wl_processes):
        try_cnt = 0
        while try_cnt < 5:
            time.sleep(5 + 5*try_cnt)
            try_cnt += 1
            wl_processes = self.get_processes_of('weston-desktop-shell', 'existing and new')
            new_wl_processes = [x for x in wl_processes if x not in existing_wl_processes]
            if new_wl_processes:
                return new_wl_processes, try_cnt

        return new_wl_processes, try_cnt

    @OEHasPackage(['wayland-utils'])
    def test_wayland_info(self):
        if 'systemd' in self.tc.td['VIRTUAL-RUNTIME_init_manager']:
            command = 'XDG_RUNTIME_DIR=/run wayland-info'
        else:
            command = self.get_weston_command('wayland-info')
        status, output = self.target.run(command)
        self.assertEqual(status, 0, msg='wayland-info error: %s' % output)

    @OEHasPackage(['weston'])
    def test_weston_can_initialize_new_wayland_compositor(self):
        existing_wl_processes = self.get_processes_of('weston-desktop-shell', 'existing')
        existing_weston_processes = self.get_processes_of('weston', 'existing')

        weston_thread = threading.Thread(target=self.run_weston_init)
        weston_thread.start()
        new_wl_processes, try_cnt = self.get_new_wayland_processes(existing_wl_processes)
        existing_and_new_weston_processes = self.get_processes_of('weston', 'existing and new')
        new_weston_processes = [x for x in existing_and_new_weston_processes if x not in existing_weston_processes]
        if 'systemd' in self.tc.td['VIRTUAL-RUNTIME_init_manager']:
            self.target.run('systemctl stop weston-ptest.service')
        else:
            for w in new_weston_processes:
                self.target.run('kill -9 %s' % w)
        __, weston_log = self.target.run('cat %s' % self.weston_log_file)
        self.assertTrue(new_wl_processes, msg='Could not get new weston-desktop-shell processes (%s, try_cnt:%s) weston log: %s' % (new_wl_processes, try_cnt, weston_log))
