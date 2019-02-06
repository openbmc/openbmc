# Copyright (C) 2015 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# This module provides a class for starting qemu images of poky tiny.
# It's used by testimage.bbclass.

import subprocess
import os
import time
import signal
import re
import socket
import select
import bb
from .qemurunner import QemuRunner

class QemuTinyRunner(QemuRunner):

    def __init__(self, machine, rootfs, display, tmpdir, deploy_dir_image, logfile, kernel, boottime, logger):

        # Popen object for runqemu
        self.runqemu = None
        # pid of the qemu process that runqemu will start
        self.qemupid = None
        # target ip - from the command line
        self.ip = None
        # host ip - where qemu is running
        self.server_ip = None

        self.machine = machine
        self.rootfs = rootfs
        self.display = display
        self.tmpdir = tmpdir
        self.deploy_dir_image = deploy_dir_image
        self.logfile = logfile
        self.boottime = boottime

        self.runqemutime = 60
        self.socketfile = "console.sock"
        self.server_socket = None
        self.kernel = kernel
        self.logger = logger


    def create_socket(self):
        tries = 3
        while tries > 0:
            try:
                self.server_socket = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
                self.server_socket.connect(self.socketfile)
                bb.note("Created listening socket for qemu serial console.")
                tries = 0
            except socket.error as msg:
                self.server_socket.close()
                bb.fatal("Failed to create listening socket.")
                tries -= 1

    def log(self, msg):
        if self.logfile:
            with open(self.logfile, "a") as f:
                f.write("%s" % msg)

    def start(self, qemuparams = None, ssh=True, extra_bootparams=None, runqemuparams='', discard_writes=True):

        if self.display:
            os.environ["DISPLAY"] = self.display
        else:
            bb.error("To start qemu I need a X desktop, please set DISPLAY correctly (e.g. DISPLAY=:1)")
            return False
        if not os.path.exists(self.rootfs):
            bb.error("Invalid rootfs %s" % self.rootfs)
            return False
        if not os.path.exists(self.tmpdir):
            bb.error("Invalid TMPDIR path %s" % self.tmpdir)
            return False
        else:
            os.environ["OE_TMPDIR"] = self.tmpdir
        if not os.path.exists(self.deploy_dir_image):
            bb.error("Invalid DEPLOY_DIR_IMAGE path %s" % self.deploy_dir_image)
            return False
        else:
            os.environ["DEPLOY_DIR_IMAGE"] = self.deploy_dir_image

        # Set this flag so that Qemu doesn't do any grabs as SDL grabs interact
        # badly with screensavers.
        os.environ["QEMU_DONT_GRAB"] = "1"
        self.qemuparams = '--append "root=/dev/ram0 console=ttyS0" -nographic -serial unix:%s,server,nowait' % self.socketfile

        launch_cmd = 'qemu-system-i386 -kernel %s -initrd %s %s' % (self.kernel, self.rootfs, self.qemuparams)
        self.runqemu = subprocess.Popen(launch_cmd,shell=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,preexec_fn=os.setpgrp)

        bb.note("runqemu started, pid is %s" % self.runqemu.pid)
        bb.note("waiting at most %s seconds for qemu pid" % self.runqemutime)
        endtime = time.time() + self.runqemutime
        while not self.is_alive() and time.time() < endtime:
            time.sleep(1)

        if self.is_alive():
            bb.note("qemu started - qemu procces pid is %s" % self.qemupid)
            self.create_socket()
        else:
            bb.note("Qemu pid didn't appeared in %s seconds" % self.runqemutime)
            output = self.runqemu.stdout
            self.stop()
            bb.note("Output from runqemu:\n%s" % output.read().decode("utf-8"))
            return False

        return self.is_alive()

    def run_serial(self, command, timeout=60):
        self.server_socket.sendall(command+'\n')
        data = ''
        status = 0
        stopread = False
        endtime = time.time()+timeout
        while time.time()<endtime and not stopread:
                try:
                        sread, _, _ = select.select([self.server_socket],[],[],1)
                except InterruptedError:
                        continue
                for sock in sread:
                        answer = sock.recv(1024)
                        if answer:
                                data += answer
                        else:
                                sock.close()
                                stopread = True
        if not data:
            status = 1
        if not stopread:
            data += "<<< run_serial(): command timed out after %d seconds without output >>>\r\n\r\n" % timeout
        return (status, str(data))

    def find_child(self,parent_pid):
        #
        # Walk the process tree from the process specified looking for a qemu-system. Return its [pid'cmd]
        #
        ps = subprocess.Popen(['ps', 'axww', '-o', 'pid,ppid,command'], stdout=subprocess.PIPE).communicate()[0]
        processes = ps.decode("utf-8").split('\n')
        nfields = len(processes[0].split()) - 1
        pids = {}
        commands = {}
        for row in processes[1:]:
            data = row.split(None, nfields)
            if len(data) != 3:
                continue
            if data[1] not in pids:
                pids[data[1]] = []

            pids[data[1]].append(data[0])
            commands[data[0]] = data[2]

        if parent_pid not in pids:
            return []

        parents = []
        newparents = pids[parent_pid]
        while newparents:
            next = []
            for p in newparents:
                if p in pids:
                    for n in pids[p]:
                        if n not in parents and n not in next:
                            next.append(n)
                if p not in parents:
                    parents.append(p)
                    newparents = next
        #print("Children matching %s:" % str(parents))
        for p in parents:
            # Need to be careful here since runqemu runs "ldd qemu-system-xxxx"
            # Also, old versions of ldd (2.11) run "LD_XXXX qemu-system-xxxx"
            basecmd = commands[p].split()[0]
            basecmd = os.path.basename(basecmd)
            if "qemu-system" in basecmd and "-serial unix" in commands[p]:
                return [int(p),commands[p]]
