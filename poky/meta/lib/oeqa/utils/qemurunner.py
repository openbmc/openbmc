#
# Copyright (C) 2013 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

# This module provides a class for starting qemu images using runqemu.
# It's used by testimage.bbclass.

import subprocess
import os
import sys
import time
import signal
import re
import socket
import select
import errno
import string
import threading
import codecs
import logging
from oeqa.utils.dump import HostDumper
from collections import defaultdict

# Get Unicode non printable control chars
control_range = list(range(0,32))+list(range(127,160))
control_chars = [chr(x) for x in control_range
                if chr(x) not in string.printable]
re_control_char = re.compile('[%s]' % re.escape("".join(control_chars)))

class QemuRunner:

    def __init__(self, machine, rootfs, display, tmpdir, deploy_dir_image, logfile, boottime, dump_dir, dump_host_cmds,
                 use_kvm, logger, use_slirp=False, serial_ports=2, boot_patterns = defaultdict(str), use_ovmf=False, workdir=None):

        # Popen object for runqemu
        self.runqemu = None
        self.runqemu_exited = False
        # pid of the qemu process that runqemu will start
        self.qemupid = None
        # target ip - from the command line or runqemu output
        self.ip = None
        # host ip - where qemu is running
        self.server_ip = None
        # target ip netmask
        self.netmask = None

        self.machine = machine
        self.rootfs = rootfs
        self.display = display
        self.tmpdir = tmpdir
        self.deploy_dir_image = deploy_dir_image
        self.logfile = logfile
        self.boottime = boottime
        self.logged = False
        self.thread = None
        self.use_kvm = use_kvm
        self.use_ovmf = use_ovmf
        self.use_slirp = use_slirp
        self.serial_ports = serial_ports
        self.msg = ''
        self.boot_patterns = boot_patterns

        self.runqemutime = 120
        if not workdir:
            workdir = os.getcwd()
        self.qemu_pidfile = workdir + '/pidfile_' + str(os.getpid())
        self.host_dumper = HostDumper(dump_host_cmds, dump_dir)
        self.monitorpipe = None

        self.logger = logger

        # Enable testing other OS's
        # Set commands for target communication, and default to Linux ALWAYS
        # Other OS's or baremetal applications need to provide their
        # own implementation passing it through QemuRunner's constructor
        # or by passing them through TESTIMAGE_BOOT_PATTERNS[flag]
        # provided variables, where <flag> is one of the mentioned below.
        accepted_patterns = ['search_reached_prompt', 'send_login_user', 'search_login_succeeded', 'search_cmd_finished']
        default_boot_patterns = defaultdict(str)
        # Default to the usual paterns used to communicate with the target
        default_boot_patterns['search_reached_prompt'] = b' login:'
        default_boot_patterns['send_login_user'] = 'root\n'
        default_boot_patterns['search_login_succeeded'] = r"root@[a-zA-Z0-9\-]+:~#"
        default_boot_patterns['search_cmd_finished'] = r"[a-zA-Z0-9]+@[a-zA-Z0-9\-]+:~#"

        # Only override patterns that were set e.g. login user TESTIMAGE_BOOT_PATTERNS[send_login_user] = "webserver\n"
        for pattern in accepted_patterns:
            if not self.boot_patterns[pattern]:
                self.boot_patterns[pattern] = default_boot_patterns[pattern]

    def create_socket(self):
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.setblocking(0)
            sock.bind(("127.0.0.1",0))
            sock.listen(2)
            port = sock.getsockname()[1]
            self.logger.debug("Created listening socket for qemu serial console on: 127.0.0.1:%s" % port)
            return (sock, port)

        except socket.error:
            sock.close()
            raise

    def log(self, msg):
        if self.logfile:
            # It is needed to sanitize the data received from qemu
            # because is possible to have control characters
            msg = msg.decode("utf-8", errors='ignore')
            msg = re_control_char.sub('', msg)
            self.msg += msg
            with codecs.open(self.logfile, "a", encoding="utf-8") as f:
                f.write("%s" % msg)

    def getOutput(self, o):
        import fcntl
        fl = fcntl.fcntl(o, fcntl.F_GETFL)
        fcntl.fcntl(o, fcntl.F_SETFL, fl | os.O_NONBLOCK)
        return os.read(o.fileno(), 1000000).decode("utf-8")


    def handleSIGCHLD(self, signum, frame):
        if self.runqemu and self.runqemu.poll():
            if self.runqemu.returncode:
                self.logger.error('runqemu exited with code %d' % self.runqemu.returncode)
                self.logger.error('Output from runqemu:\n%s' % self.getOutput(self.runqemu.stdout))
                self.stop()
                self._dump_host()

    def start(self, qemuparams = None, get_ip = True, extra_bootparams = None, runqemuparams='', launch_cmd=None, discard_writes=True):
        env = os.environ.copy()
        if self.display:
            env["DISPLAY"] = self.display
            # Set this flag so that Qemu doesn't do any grabs as SDL grabs
            # interact badly with screensavers.
            env["QEMU_DONT_GRAB"] = "1"
        if not os.path.exists(self.rootfs):
            self.logger.error("Invalid rootfs %s" % self.rootfs)
            return False
        if not os.path.exists(self.tmpdir):
            self.logger.error("Invalid TMPDIR path %s" % self.tmpdir)
            return False
        else:
            env["OE_TMPDIR"] = self.tmpdir
        if not os.path.exists(self.deploy_dir_image):
            self.logger.error("Invalid DEPLOY_DIR_IMAGE path %s" % self.deploy_dir_image)
            return False
        else:
            env["DEPLOY_DIR_IMAGE"] = self.deploy_dir_image

        if not launch_cmd:
            launch_cmd = 'runqemu %s' % ('snapshot' if discard_writes else '')
            if self.use_kvm:
                self.logger.debug('Using kvm for runqemu')
                launch_cmd += ' kvm'
            else:
                self.logger.debug('Not using kvm for runqemu')
            if not self.display:
                launch_cmd += ' nographic'
            if self.use_slirp:
                launch_cmd += ' slirp'
            if self.use_ovmf:
                launch_cmd += ' ovmf'
            launch_cmd += ' %s %s %s' % (runqemuparams, self.machine, self.rootfs)

        return self.launch(launch_cmd, qemuparams=qemuparams, get_ip=get_ip, extra_bootparams=extra_bootparams, env=env)

    def launch(self, launch_cmd, get_ip = True, qemuparams = None, extra_bootparams = None, env = None):
        try:
            if self.serial_ports >= 2:
                self.threadsock, threadport = self.create_socket()
            self.server_socket, self.serverport = self.create_socket()
        except socket.error as msg:
            self.logger.error("Failed to create listening socket: %s" % msg[1])
            return False

        bootparams = 'console=tty1 console=ttyS0,115200n8 printk.time=1'
        if extra_bootparams:
            bootparams = bootparams + ' ' + extra_bootparams

        # Ask QEMU to store the QEMU process PID in file, this way we don't have to parse running processes
        # and analyze descendents in order to determine it.
        if os.path.exists(self.qemu_pidfile):
            os.remove(self.qemu_pidfile)
        self.qemuparams = 'bootparams="{0}" qemuparams="-pidfile {1}"'.format(bootparams, self.qemu_pidfile)
        if qemuparams:
            self.qemuparams = self.qemuparams[:-1] + " " + qemuparams + " " + '\"'

        if self.serial_ports >= 2:
            launch_cmd += ' tcpserial=%s:%s %s' % (threadport, self.serverport, self.qemuparams)
        else:
            launch_cmd += ' tcpserial=%s %s' % (self.serverport, self.qemuparams)

        self.origchldhandler = signal.getsignal(signal.SIGCHLD)
        signal.signal(signal.SIGCHLD, self.handleSIGCHLD)

        self.logger.debug('launchcmd=%s'%(launch_cmd))

        # FIXME: We pass in stdin=subprocess.PIPE here to work around stty
        # blocking at the end of the runqemu script when using this within
        # oe-selftest (this makes stty error out immediately). There ought
        # to be a proper fix but this will suffice for now.
        self.runqemu = subprocess.Popen(launch_cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, stdin=subprocess.PIPE, preexec_fn=os.setpgrp, env=env)
        output = self.runqemu.stdout

        #
        # We need the preexec_fn above so that all runqemu processes can easily be killed
        # (by killing their process group). This presents a problem if this controlling
        # process itself is killed however since those processes don't notice the death
        # of the parent and merrily continue on.
        #
        # Rather than hack runqemu to deal with this, we add something here instead.
        # Basically we fork off another process which holds an open pipe to the parent
        # and also is setpgrp. If/when the pipe sees EOF from the parent dieing, it kills
        # the process group. This is like pctrl's PDEATHSIG but for a process group
        # rather than a single process.
        #
        r, w = os.pipe()
        self.monitorpid = os.fork()
        if self.monitorpid:
            os.close(r)
            self.monitorpipe = os.fdopen(w, "w")
        else:
            # child process
            os.setpgrp()
            os.close(w)
            r = os.fdopen(r)
            x = r.read()
            os.killpg(os.getpgid(self.runqemu.pid), signal.SIGTERM)
            sys.exit(0)

        self.logger.debug("runqemu started, pid is %s" % self.runqemu.pid)
        self.logger.debug("waiting at most %s seconds for qemu pid (%s)" %
                          (self.runqemutime, time.strftime("%D %H:%M:%S")))
        endtime = time.time() + self.runqemutime
        while not self.is_alive() and time.time() < endtime:
            if self.runqemu.poll():
                if self.runqemu_exited:
                    return False
                if self.runqemu.returncode:
                    # No point waiting any longer
                    self.logger.warning('runqemu exited with code %d' % self.runqemu.returncode)
                    self._dump_host()
                    self.logger.warning("Output from runqemu:\n%s" % self.getOutput(output))
                    self.stop()
                    return False
            time.sleep(0.5)

        if self.runqemu_exited:
            return False

        if not self.is_alive():
            self.logger.error("Qemu pid didn't appear in %s seconds (%s)" %
                              (self.runqemutime, time.strftime("%D %H:%M:%S")))

            qemu_pid = None
            if os.path.isfile(self.qemu_pidfile):
                with open(self.qemu_pidfile, 'r') as f:
                    qemu_pid = f.read().strip()

            self.logger.error("Status information, poll status: %s, pidfile exists: %s, pidfile contents %s, proc pid exists %s"
                % (self.runqemu.poll(), os.path.isfile(self.qemu_pidfile), str(qemu_pid), os.path.exists("/proc/" + str(qemu_pid))))

            # Dump all processes to help us to figure out what is going on...
            ps = subprocess.Popen(['ps', 'axww', '-o', 'pid,ppid,pri,ni,command '], stdout=subprocess.PIPE).communicate()[0]
            processes = ps.decode("utf-8")
            self.logger.debug("Running processes:\n%s" % processes)
            self._dump_host()
            op = self.getOutput(output)
            self.stop()
            if op:
                self.logger.error("Output from runqemu:\n%s" % op)
            else:
                self.logger.error("No output from runqemu.\n")
            return False

        # We are alive: qemu is running
        out = self.getOutput(output)
        netconf = False # network configuration is not required by default
        self.logger.debug("qemu started in %s seconds - qemu procces pid is %s (%s)" %
                          (time.time() - (endtime - self.runqemutime),
                           self.qemupid, time.strftime("%D %H:%M:%S")))
        cmdline = ''
        if get_ip:
            with open('/proc/%s/cmdline' % self.qemupid) as p:
                cmdline = p.read()
                # It is needed to sanitize the data received
                # because is possible to have control characters
                cmdline = re_control_char.sub(' ', cmdline)
            try:
                if self.use_slirp:
                    tcp_ports = cmdline.split("hostfwd=tcp::")[1]
                    host_port = tcp_ports[:tcp_ports.find('-')]
                    self.ip = "localhost:%s" % host_port
                else:
                    ips = re.findall(r"((?:[0-9]{1,3}\.){3}[0-9]{1,3})", cmdline.split("ip=")[1])
                    self.ip = ips[0]
                    self.server_ip = ips[1]
                self.logger.debug("qemu cmdline used:\n{}".format(cmdline))
            except (IndexError, ValueError):
                # Try to get network configuration from runqemu output
                match = re.match(r'.*Network configuration: (?:ip=)*([0-9.]+)::([0-9.]+):([0-9.]+)$.*',
                                 out, re.MULTILINE|re.DOTALL)
                if match:
                    self.ip, self.server_ip, self.netmask = match.groups()
                    # network configuration is required as we couldn't get it
                    # from the runqemu command line, so qemu doesn't run kernel
                    # and guest networking is not configured
                    netconf = True
                else:
                    self.logger.error("Couldn't get ip from qemu command line and runqemu output! "
                                 "Here is the qemu command line used:\n%s\n"
                                 "and output from runqemu:\n%s" % (cmdline, out))
                    self._dump_host()
                    self.stop()
                    return False

        self.logger.debug("Target IP: %s" % self.ip)
        self.logger.debug("Server IP: %s" % self.server_ip)

        if self.serial_ports >= 2:
            self.thread = LoggingThread(self.log, self.threadsock, self.logger)
            self.thread.start()
            if not self.thread.connection_established.wait(self.boottime):
                self.logger.error("Didn't receive a console connection from qemu. "
                             "Here is the qemu command line used:\n%s\nand "
                             "output from runqemu:\n%s" % (cmdline, out))
                self.stop_thread()
                return False

        self.logger.debug("Output from runqemu:\n%s", out)
        self.logger.debug("Waiting at most %d seconds for login banner (%s)" %
                          (self.boottime, time.strftime("%D %H:%M:%S")))
        endtime = time.time() + self.boottime
        socklist = [self.server_socket]
        reachedlogin = False
        stopread = False
        qemusock = None
        bootlog = b''
        data = b''
        while time.time() < endtime and not stopread:
            try:
                sread, swrite, serror = select.select(socklist, [], [], 5)
            except InterruptedError:
                continue
            for sock in sread:
                if sock is self.server_socket:
                    qemusock, addr = self.server_socket.accept()
                    qemusock.setblocking(0)
                    socklist.append(qemusock)
                    socklist.remove(self.server_socket)
                    self.logger.debug("Connection from %s:%s" % addr)
                else:
                    data = data + sock.recv(1024)
                    if data:
                        bootlog += data
                        if self.serial_ports < 2:
                            # this socket has mixed console/kernel data, log it to logfile
                            self.log(data)

                        data = b''
                        if self.boot_patterns['search_reached_prompt'] in bootlog:
                            self.server_socket = qemusock
                            stopread = True
                            reachedlogin = True
                            self.logger.debug("Reached login banner in %s seconds (%s)" %
                                              (time.time() - (endtime - self.boottime),
                                              time.strftime("%D %H:%M:%S")))
                    else:
                        # no need to check if reachedlogin unless we support multiple connections
                        self.logger.debug("QEMU socket disconnected before login banner reached. (%s)" %
                                          time.strftime("%D %H:%M:%S"))
                        socklist.remove(sock)
                        sock.close()
                        stopread = True


        if not reachedlogin:
            if time.time() >= endtime:
                self.logger.warning("Target didn't reach login banner in %d seconds (%s)" %
                                  (self.boottime, time.strftime("%D %H:%M:%S")))
            tail = lambda l: "\n".join(l.splitlines()[-25:])
            bootlog = bootlog.decode("utf-8")
            # in case bootlog is empty, use tail qemu log store at self.msg
            lines = tail(bootlog if bootlog else self.msg)
            self.logger.warning("Last 25 lines of text:\n%s" % lines)
            self.logger.warning("Check full boot log: %s" % self.logfile)
            self._dump_host()
            self.stop()
            return False

        # If we are not able to login the tests can continue
        try:
            (status, output) = self.run_serial(self.boot_patterns['send_login_user'], raw=True)
            if re.search(self.boot_patterns['search_login_succeeded'], output):
                self.logged = True
                self.logger.debug("Logged as root in serial console")
                if netconf:
                    # configure guest networking
                    cmd = "ifconfig eth0 %s netmask %s up\n" % (self.ip, self.netmask)
                    output = self.run_serial(cmd, raw=True)[1]
                    if re.search(r"root@[a-zA-Z0-9\-]+:~#", output):
                        self.logger.debug("configured ip address %s", self.ip)
                    else:
                        self.logger.debug("Couldn't configure guest networking")
            else:
                self.logger.warning("Couldn't login into serial console"
                            " as root using blank password")
                self.logger.warning("The output:\n%s" % output)
        except:
            self.logger.warning("Serial console failed while trying to login")
        return True

    def stop(self):
        if hasattr(self, "origchldhandler"):
            signal.signal(signal.SIGCHLD, self.origchldhandler)
        self.stop_thread()
        self.stop_qemu_system()
        if self.runqemu:
            if hasattr(self, "monitorpid"):
                os.kill(self.monitorpid, signal.SIGKILL)
                self.logger.debug("Sending SIGTERM to runqemu")
                try:
                    os.killpg(os.getpgid(self.runqemu.pid), signal.SIGTERM)
                except OSError as e:
                    if e.errno != errno.ESRCH:
                        raise
            endtime = time.time() + self.runqemutime
            while self.runqemu.poll() is None and time.time() < endtime:
                time.sleep(1)
            if self.runqemu.poll() is None:
                self.logger.debug("Sending SIGKILL to runqemu")
                os.killpg(os.getpgid(self.runqemu.pid), signal.SIGKILL)
            self.runqemu.stdin.close()
            self.runqemu.stdout.close()
            self.runqemu_exited = True

        if hasattr(self, 'server_socket') and self.server_socket:
            self.server_socket.close()
            self.server_socket = None
        if hasattr(self, 'threadsock') and self.threadsock:
            self.threadsock.close()
            self.threadsock = None
        self.qemupid = None
        self.ip = None
        if os.path.exists(self.qemu_pidfile):
            try:
                os.remove(self.qemu_pidfile)
            except FileNotFoundError as e:
                # We raced, ignore
                pass
        if self.monitorpipe:
            self.monitorpipe.close()

    def stop_qemu_system(self):
        if self.qemupid:
            try:
                # qemu-system behaves well and a SIGTERM is enough
                os.kill(self.qemupid, signal.SIGTERM)
            except ProcessLookupError as e:
                self.logger.warning('qemu-system ended unexpectedly')

    def stop_thread(self):
        if self.thread and self.thread.is_alive():
            self.thread.stop()
            self.thread.join()

    def restart(self, qemuparams = None):
        self.logger.warning("Restarting qemu process")
        if self.runqemu.poll() is None:
            self.stop()
        if self.start(qemuparams):
            return True
        return False

    def is_alive(self):
        if not self.runqemu or self.runqemu.poll() is not None or self.runqemu_exited:
            return False
        if os.path.isfile(self.qemu_pidfile):
            # when handling pidfile, qemu creates the file, stat it, lock it and then write to it
            # so it's possible that the file has been created but the content is empty
            pidfile_timeout = time.time() + 3
            while time.time() < pidfile_timeout:
                with open(self.qemu_pidfile, 'r') as f:
                    qemu_pid = f.read().strip()
                # file created but not yet written contents
                if not qemu_pid:
                    time.sleep(0.5)
                    continue
                else:
                    if os.path.exists("/proc/" + qemu_pid):
                        self.qemupid = int(qemu_pid)
                        return True
        return False

    def run_serial(self, command, raw=False, timeout=60):
        # We assume target system have echo to get command status
        if not raw:
            command = "%s; echo $?\n" % command

        data = ''
        status = 0
        self.server_socket.sendall(command.encode('utf-8'))
        start = time.time()
        end = start + timeout
        while True:
            now = time.time()
            if now >= end:
                data += "<<< run_serial(): command timed out after %d seconds without output >>>\r\n\r\n" % timeout
                break
            try:
                sread, _, _ = select.select([self.server_socket],[],[], end - now)
            except InterruptedError:
                continue
            if sread:
                answer = self.server_socket.recv(1024)
                if answer:
                    data += answer.decode('utf-8')
                    # Search the prompt to stop
                    if re.search(self.boot_patterns['search_cmd_finished'], data):
                        break
                else:
                    raise Exception("No data on serial console socket")

        if data:
            if raw:
                status = 1
            else:
                # Remove first line (command line) and last line (prompt)
                data = data[data.find('$?\r\n')+4:data.rfind('\r\n')]
                index = data.rfind('\r\n')
                if index == -1:
                    status_cmd = data
                    data = ""
                else:
                    status_cmd = data[index+2:]
                    data = data[:index]
                if (status_cmd == "0"):
                    status = 1
        return (status, str(data))


    def _dump_host(self):
        self.host_dumper.create_dir("qemu")
        self.logger.warning("Qemu ended unexpectedly, dump data from host"
                " is in %s" % self.host_dumper.dump_dir)
        self.host_dumper.dump_host()

# This class is for reading data from a socket and passing it to logfunc
# to be processed. It's completely event driven and has a straightforward
# event loop. The mechanism for stopping the thread is a simple pipe which
# will wake up the poll and allow for tearing everything down.
class LoggingThread(threading.Thread):
    def __init__(self, logfunc, sock, logger):
        self.connection_established = threading.Event()
        self.serversock = sock
        self.logfunc = logfunc
        self.logger = logger
        self.readsock = None
        self.running = False

        self.errorevents = select.POLLERR | select.POLLHUP | select.POLLNVAL
        self.readevents = select.POLLIN | select.POLLPRI

        threading.Thread.__init__(self, target=self.threadtarget)

    def threadtarget(self):
        try:
            self.eventloop()
        finally:
            self.teardown()

    def run(self):
        self.logger.debug("Starting logging thread")
        self.readpipe, self.writepipe = os.pipe()
        threading.Thread.run(self)

    def stop(self):
        self.logger.debug("Stopping logging thread")
        if self.running:
            os.write(self.writepipe, bytes("stop", "utf-8"))

    def teardown(self):
        self.logger.debug("Tearing down logging thread")
        self.close_socket(self.serversock)

        if self.readsock is not None:
            self.close_socket(self.readsock)

        self.close_ignore_error(self.readpipe)
        self.close_ignore_error(self.writepipe)
        self.running = False

    def eventloop(self):
        poll = select.poll()
        event_read_mask = self.errorevents | self.readevents
        poll.register(self.serversock.fileno())
        poll.register(self.readpipe, event_read_mask)

        breakout = False
        self.running = True
        self.logger.debug("Starting thread event loop")
        while not breakout:
            events = poll.poll()
            for event in events:
                # An error occurred, bail out
                if event[1] & self.errorevents:
                    raise Exception(self.stringify_event(event[1]))

                # Event to stop the thread
                if self.readpipe == event[0]:
                    self.logger.debug("Stop event received")
                    breakout = True
                    break

                # A connection request was received
                elif self.serversock.fileno() == event[0]:
                    self.logger.debug("Connection request received")
                    self.readsock, _ = self.serversock.accept()
                    self.readsock.setblocking(0)
                    poll.unregister(self.serversock.fileno())
                    poll.register(self.readsock.fileno(), event_read_mask)

                    self.logger.debug("Setting connection established event")
                    self.connection_established.set()

                # Actual data to be logged
                elif self.readsock.fileno() == event[0]:
                    data = self.recv(1024)
                    self.logfunc(data)

    # Since the socket is non-blocking make sure to honor EAGAIN
    # and EWOULDBLOCK.
    def recv(self, count):
        try:
            data = self.readsock.recv(count)
        except socket.error as e:
            if e.errno == errno.EAGAIN or e.errno == errno.EWOULDBLOCK:
                return ''
            else:
                raise

        if data is None:
            raise Exception("No data on read ready socket")
        elif not data:
            # This actually means an orderly shutdown
            # happened. But for this code it counts as an
            # error since the connection shouldn't go away
            # until qemu exits.
            raise Exception("Console connection closed unexpectedly")

        return data

    def stringify_event(self, event):
        val = ''
        if select.POLLERR == event:
            val = 'POLLER'
        elif select.POLLHUP == event:
            val = 'POLLHUP'
        elif select.POLLNVAL == event:
            val = 'POLLNVAL'
        return val

    def close_socket(self, sock):
        sock.shutdown(socket.SHUT_RDWR)
        sock.close()

    def close_ignore_error(self, fd):
        try:
            os.close(fd)
        except OSError:
            pass
