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
import tempfile
from collections import defaultdict
from contextlib import contextmanager
import importlib
import traceback

# Get Unicode non printable control chars
control_range = list(range(0,32))+list(range(127,160))
control_chars = [chr(x) for x in control_range
                if chr(x) not in string.printable]
re_control_char = re.compile('[%s]' % re.escape("".join(control_chars)))
# Regex to remove the ANSI (color) control codes from console strings in order to match the text only
re_vt100 = re.compile(r'(\x1b\[|\x9b)[^@-_a-z]*[@-_a-z]|\x1b[@-_a-z]')

def getOutput(o):
    import fcntl
    fl = fcntl.fcntl(o, fcntl.F_GETFL)
    fcntl.fcntl(o, fcntl.F_SETFL, fl | os.O_NONBLOCK)
    try:
        return os.read(o.fileno(), 1000000).decode("utf-8")
    except BlockingIOError:
        return ""

class QemuRunner:

    def __init__(self, machine, rootfs, display, tmpdir, deploy_dir_image, logfile, boottime, dump_dir, use_kvm, logger, use_slirp=False,
     serial_ports=2, boot_patterns = defaultdict(str), use_ovmf=False, workdir=None, tmpfsdir=None):

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
        self.threadsock = None
        self.use_kvm = use_kvm
        self.use_ovmf = use_ovmf
        self.use_slirp = use_slirp
        self.serial_ports = serial_ports
        self.msg = ''
        self.boot_patterns = boot_patterns
        self.tmpfsdir = tmpfsdir

        self.runqemutime = 300
        if not workdir:
            workdir = os.getcwd()
        self.qemu_pidfile = workdir + '/pidfile_' + str(os.getpid())
        self.monitorpipe = None

        self.logger = logger
        # Whether we're expecting an exit and should show related errors
        self.canexit = False

        # Enable testing other OS's
        # Set commands for target communication, and default to Linux ALWAYS
        # Other OS's or baremetal applications need to provide their
        # own implementation passing it through QemuRunner's constructor
        # or by passing them through TESTIMAGE_BOOT_PATTERNS[flag]
        # provided variables, where <flag> is one of the mentioned below.
        accepted_patterns = ['search_reached_prompt', 'send_login_user', 'search_login_succeeded', 'search_cmd_finished']
        default_boot_patterns = defaultdict(str)
        # Default to the usual paterns used to communicate with the target
        default_boot_patterns['search_reached_prompt'] = ' login:'
        default_boot_patterns['send_login_user'] = 'root\n'
        default_boot_patterns['search_login_succeeded'] = r"root@[a-zA-Z0-9\-]+:~#"
        default_boot_patterns['search_cmd_finished'] = r"[a-zA-Z0-9]+@[a-zA-Z0-9\-]+:~#"

        # Only override patterns that were set e.g. login user TESTIMAGE_BOOT_PATTERNS[send_login_user] = "webserver\n"
        for pattern in accepted_patterns:
            if pattern not in self.boot_patterns or not self.boot_patterns[pattern]:
                self.boot_patterns[pattern] = default_boot_patterns[pattern]

    def create_socket(self):
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.setblocking(0)
            sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
            sock.bind(("127.0.0.1",0))
            sock.listen(2)
            port = sock.getsockname()[1]
            self.logger.debug("Created listening socket for qemu serial console on: 127.0.0.1:%s" % port)
            return (sock, port)

        except socket.error:
            sock.close()
            raise

    def decode_qemulog(self, todecode):
        # Sanitize the data received from qemu as it may contain control characters
        msg = todecode.decode("utf-8", errors='backslashreplace')
        msg = re_control_char.sub('', msg)
        return msg

    def log(self, msg, extension=""):
        if self.logfile:
            with codecs.open(self.logfile + extension, "ab") as f:
                f.write(msg)
        self.msg += self.decode_qemulog(msg)

    def handleSIGCHLD(self, signum, frame):
        if self.runqemu and self.runqemu.poll():
            if self.runqemu.returncode:
                self.logger.error('runqemu exited with code %d' % self.runqemu.returncode)
                self.logger.error('Output from runqemu:\n%s' % getOutput(self.runqemu.stdout))
                self.stop()

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

        if self.tmpfsdir:
            env["RUNQEMU_TMPFS_DIR"] = self.tmpfsdir

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
            launch_cmd += ' %s %s' % (runqemuparams, self.machine)
            if self.rootfs.endswith('.vmdk'):
                self.logger.debug('Bypassing VMDK rootfs for runqemu')
            else:
                launch_cmd += ' %s' % (self.rootfs)

        return self.launch(launch_cmd, qemuparams=qemuparams, get_ip=get_ip, extra_bootparams=extra_bootparams, env=env)

    def launch(self, launch_cmd, get_ip = True, qemuparams = None, extra_bootparams = None, env = None):
        # use logfile to determine the recipe-sysroot-native path and
        # then add in the site-packages path components and add that
        # to the python sys.path so the qmp module can be found.
        python_path = os.path.dirname(os.path.dirname(self.logfile))
        python_path += "/recipe-sysroot-native/usr/lib/qemu-python"
        sys.path.append(python_path)
        importlib.invalidate_caches()
        try:
            qmp = importlib.import_module("qmp")
        except Exception as e:
            self.logger.error("qemurunner: qmp module missing, please ensure it's installed in %s (%s)" % (python_path, str(e)))
            return False
        # Path relative to tmpdir used as cwd for qemu below to avoid unix socket path length issues
        qmp_file = "." + next(tempfile._get_candidate_names())
        qmp_param = ' -S -qmp unix:./%s,server,wait' % (qmp_file)
        qmp_port = self.tmpdir + "/" + qmp_file
        # Create a second socket connection for debugging use,
        # note this will NOT cause qemu to block waiting for the connection
        qmp_file2 = "." + next(tempfile._get_candidate_names())
        qmp_param += ' -qmp unix:./%s,server,nowait' % (qmp_file2)
        qmp_port2 = self.tmpdir + "/" + qmp_file2
        self.logger.info("QMP Available for connection at %s" % (qmp_port2))

        try:
            if self.serial_ports >= 2:
                self.threadsock, threadport = self.create_socket()
            self.server_socket, self.serverport = self.create_socket()
        except socket.error as msg:
            self.logger.error("Failed to create listening socket: %s" % msg[1])
            return False

        bootparams = ' printk.time=1'
        if extra_bootparams:
            bootparams = bootparams + ' ' + extra_bootparams

        # Ask QEMU to store the QEMU process PID in file, this way we don't have to parse running processes
        # and analyze descendents in order to determine it.
        if os.path.exists(self.qemu_pidfile):
            os.remove(self.qemu_pidfile)
        self.qemuparams = 'bootparams="{0}" qemuparams="-pidfile {1} {2}"'.format(bootparams, self.qemu_pidfile, qmp_param)

        if qemuparams:
            self.qemuparams = self.qemuparams[:-1] + " " + qemuparams + " " + '\"'

        if self.serial_ports >= 2:
            launch_cmd += ' tcpserial=%s:%s %s' % (threadport, self.serverport, self.qemuparams)
        else:
            launch_cmd += ' tcpserial=%s %s' % (self.serverport, self.qemuparams)

        self.origchldhandler = signal.getsignal(signal.SIGCHLD)
        signal.signal(signal.SIGCHLD, self.handleSIGCHLD)

        self.logger.debug('launchcmd=%s' % (launch_cmd))

        # FIXME: We pass in stdin=subprocess.PIPE here to work around stty
        # blocking at the end of the runqemu script when using this within
        # oe-selftest (this makes stty error out immediately). There ought
        # to be a proper fix but this will suffice for now.
        self.runqemu = subprocess.Popen(launch_cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, stdin=subprocess.PIPE, preexec_fn=os.setpgrp, env=env, cwd=self.tmpdir)
        output = self.runqemu.stdout
        launch_time = time.time()

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
            os._exit(0)

        self.logger.debug("runqemu started, pid is %s" % self.runqemu.pid)
        self.logger.debug("waiting at most %d seconds for qemu pid (%s)" %
                          (self.runqemutime, time.strftime("%D %H:%M:%S")))
        endtime = time.time() + self.runqemutime
        while not self.is_alive() and time.time() < endtime:
            if self.runqemu.poll():
                if self.runqemu_exited:
                    self.logger.warning("runqemu during is_alive() test")
                    return False
                if self.runqemu.returncode:
                    # No point waiting any longer
                    self.logger.warning('runqemu exited with code %d' % self.runqemu.returncode)
                    self.logger.warning("Output from runqemu:\n%s" % getOutput(output))
                    self.stop()
                    return False
            time.sleep(0.5)

        if self.runqemu_exited:
            self.logger.warning("runqemu after timeout")

        if self.runqemu.returncode:
            self.logger.warning('runqemu exited with code %d' % self.runqemu.returncode)

        if not self.is_alive():
            self.logger.error("Qemu pid didn't appear in %d seconds (%s)" %
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
            op = getOutput(output)
            self.stop()
            if op:
                self.logger.error("Output from runqemu:\n%s" % op)
            else:
                self.logger.error("No output from runqemu.\n")
            return False

        # Create the client socket for the QEMU Monitor Control Socket
        # This will allow us to read status from Qemu if the the process
        # is still alive
        self.logger.debug("QMP Initializing to %s" % (qmp_port))
        # chdir dance for path length issues with unix sockets
        origpath = os.getcwd()
        try:
            os.chdir(os.path.dirname(qmp_port))
            try:
                from qmp.legacy import QEMUMonitorProtocol
                self.qmp = QEMUMonitorProtocol(os.path.basename(qmp_port))
            except OSError as msg:
                self.logger.warning("Failed to initialize qemu monitor socket: %s File: %s" % (msg, msg.filename))
                return False

            self.logger.debug("QMP Connecting to %s" % (qmp_port))
            if not os.path.exists(qmp_port) and self.is_alive():
                self.logger.debug("QMP Port does not exist waiting for it to be created")
                endtime = time.time() + self.runqemutime
                while not os.path.exists(qmp_port) and self.is_alive() and time.time() < endtime:
                    self.logger.info("QMP port does not exist yet!")
                    time.sleep(0.5)
                if not os.path.exists(qmp_port) and self.is_alive():
                    self.logger.warning("QMP Port still does not exist but QEMU is alive")
                    return False

            try:
                # set timeout value for all QMP calls
                self.qmp.settimeout(self.runqemutime)
                self.qmp.connect()
                connect_time = time.time()
                self.logger.info("QMP connected to QEMU at %s and took %.2f seconds" %
                                  (time.strftime("%D %H:%M:%S"),
                                   time.time() - launch_time))
            except OSError as msg:
                self.logger.warning("Failed to connect qemu monitor socket: %s File: %s" % (msg, msg.filename))
                return False
            except qmp.legacy.QMPError as msg:
                self.logger.warning("Failed to communicate with qemu monitor: %s" % (msg))
                return False
        finally:
            os.chdir(origpath)

        # We worry that mmap'd libraries may cause page faults which hang the qemu VM for periods
        # causing failures. Before we "start" qemu, read through it's mapped files to try and 
        # ensure we don't hit page faults later
        mapdir = "/proc/" + str(self.qemupid) + "/map_files/"
        try:
            for f in os.listdir(mapdir):
                try:
                    linktarget = os.readlink(os.path.join(mapdir, f))
                    if not linktarget.startswith("/") or linktarget.startswith("/dev") or "deleted" in linktarget:
                        continue
                    with open(linktarget, "rb") as readf:
                        data = True
                        while data:
                            data = readf.read(4096)
                except FileNotFoundError:
                    continue
        # Centos7 doesn't allow us to read /map_files/
        except PermissionError:
            pass

        # Release the qemu process to continue running
        self.run_monitor('cont')
        self.logger.info("QMP released QEMU at %s and took %.2f seconds from connect" %
                          (time.strftime("%D %H:%M:%S"),
                           time.time() - connect_time))

        # We are alive: qemu is running
        out = getOutput(output)
        netconf = False # network configuration is not required by default
        self.logger.debug("qemu started in %.2f seconds - qemu procces pid is %s (%s)" %
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
                    tcp_ports = cmdline.split("hostfwd=tcp:")[1]
                    ip, tcp_ports = tcp_ports.split(":")[:2]
                    host_port = tcp_ports[:tcp_ports.find('-')]
                    self.ip = "%s:%s" % (ip, host_port)
                else:
                    ips = re.findall(r"((?:[0-9]{1,3}\.){3}[0-9]{1,3})", cmdline.split("ip=")[1])
                    self.ip = ips[0]
                    self.server_ip = ips[1]
                self.logger.debug("qemu cmdline used:\n{}".format(cmdline))
            except (IndexError, ValueError):
                # Try to get network configuration from runqemu output
                match = re.match(r'.*Network configuration: (?:ip=)*([0-9.]+)::([0-9.]+):([0-9.]+).*',
                                 out, re.MULTILINE | re.DOTALL)
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
                    self.stop()
                    return False

        self.logger.debug("Target IP: %s" % self.ip)
        self.logger.debug("Server IP: %s" % self.server_ip)

        self.thread = LoggingThread(self.log, self.threadsock, self.logger, self.runqemu.stdout)
        self.thread.start()

        if self.serial_ports >= 2:
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
        filelist = [self.server_socket]
        reachedlogin = False
        stopread = False
        qemusock = None
        bootlog = b''
        data = b''
        while time.time() < endtime and not stopread:
            try:
                sread, swrite, serror = select.select(filelist, [], [], 5)
            except InterruptedError:
                continue
            for file in sread:
                if file is self.server_socket:
                    qemusock, addr = self.server_socket.accept()
                    qemusock.setblocking(False)
                    filelist.append(qemusock)
                    filelist.remove(self.server_socket)
                    self.logger.debug("Connection from %s:%s" % addr)
                else:
                    # try to avoid reading only a single character at a time
                    time.sleep(0.1)
                    if hasattr(file, 'read'):
                        read = file.read(1024)
                    elif hasattr(file, 'recv'):
                        read = file.recv(1024)
                    else:
                        self.logger.error('Invalid file type: %s\n%s' % (file))
                        read = b''

                    self.logger.debug2('Partial boot log:\n%s' % (read.decode('utf-8', errors='backslashreplace')))
                    data = data + read
                    if data:
                        bootlog += data
                        self.log(data, extension = ".2")
                        data = b''

                        if bytes(self.boot_patterns['search_reached_prompt'], 'utf-8') in bootlog:
                            self.server_socket.close()
                            self.server_socket = qemusock
                            stopread = True
                            reachedlogin = True
                            self.logger.debug("Reached login banner in %.2f seconds (%s)" %
                                              (time.time() - (endtime - self.boottime),
                                              time.strftime("%D %H:%M:%S")))
                    else:
                        # no need to check if reachedlogin unless we support multiple connections
                        self.logger.debug("QEMU socket disconnected before login banner reached. (%s)" %
                                          time.strftime("%D %H:%M:%S"))
                        filelist.remove(file)
                        file.close()
                        stopread = True

        if not reachedlogin:
            if time.time() >= endtime:
                self.logger.warning("Target didn't reach login banner in %d seconds (%s)" %
                                  (self.boottime, time.strftime("%D %H:%M:%S")))
            tail = lambda l: "\n".join(l.splitlines()[-25:])
            bootlog = self.decode_qemulog(bootlog)
            self.logger.warning("Last 25 lines of login console (%d):\n%s" % (len(bootlog), tail(bootlog)))
            self.logger.warning("Last 25 lines of all logging (%d):\n%s" % (len(self.msg), tail(self.msg)))
            self.logger.warning("Check full boot log: %s" % self.logfile)
            self.stop()
            data = True
            while data:
                try:
                    time.sleep(1)
                    data = qemusock.recv(1024)
                    self.log(data, extension = ".2")
                    self.logger.warning('Extra log data read: %s\n' % (data.decode('utf-8', errors='backslashreplace')))
                except Exception as e:
                    self.logger.warning('Extra log data exception %s' % repr(e))
                    data = None
            return False

        with self.thread.serial_lock:
            self.thread.set_serialsock(self.server_socket)

        # If we are not able to login the tests can continue
        try:
            (status, output) = self.run_serial(self.boot_patterns['send_login_user'], raw=True, timeout=120)
            if re.search(self.boot_patterns['search_login_succeeded'], output):
                self.logged = True
                self.logger.debug("Logged in as %s in serial console" % self.boot_patterns['send_login_user'].replace("\n", ""))
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
                            " as %s using blank password" % self.boot_patterns['send_login_user'].replace("\n", ""))
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
            try:
                outs, errs = self.runqemu.communicate(timeout=self.runqemutime)
                if outs:
                    self.logger.info("Output from runqemu:\n%s", outs.decode("utf-8"))
                if errs:
                    self.logger.info("Stderr from runqemu:\n%s", errs.decode("utf-8"))
            except subprocess.TimeoutExpired:
                self.logger.debug("Sending SIGKILL to runqemu")
                os.killpg(os.getpgid(self.runqemu.pid), signal.SIGKILL)
            if not self.runqemu.stdout.closed:
                self.logger.info("Output from runqemu:\n%s" % getOutput(self.runqemu.stdout))
            self.runqemu.stdin.close()
            self.runqemu.stdout.close()
            self.runqemu_exited = True

        if hasattr(self, 'qmp') and self.qmp:
            self.qmp.close()
            self.qmp = None
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

    def allowexit(self):
        self.canexit = True
        if self.thread:
            self.thread.allowexit()

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
                try:
                    with open(self.qemu_pidfile, 'r') as f:
                        qemu_pid = f.read().strip()
                except FileNotFoundError:
                    # Can be used to detect shutdown so the pid file can disappear
                    return False
                # file created but not yet written contents
                if not qemu_pid:
                    time.sleep(0.5)
                    continue
                else:
                    if os.path.exists("/proc/" + qemu_pid):
                        self.qemupid = int(qemu_pid)
                        return True
        return False

    def run_monitor(self, command, args=None, timeout=60):
        if hasattr(self, 'qmp') and self.qmp:
            self.qmp.settimeout(timeout)
            if args is not None:
                return self.qmp.cmd_raw(command, args)
            else:
                return self.qmp.cmd_raw(command)

    def run_serial(self, command, raw=False, timeout=60):
        # Returns (status, output) where status is 1 on success and 0 on error

        # We assume target system have echo to get command status
        if not raw:
            command = "%s; echo $?\n" % command

        data = ''
        status = 0
        with self.thread.serial_lock:
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
                    # try to avoid reading single character at a time
                    time.sleep(0.1)
                    answer = self.server_socket.recv(1024)
                    if answer:
                        data += re_vt100.sub("", answer.decode('utf-8'))
                        # Search the prompt to stop
                        if re.search(self.boot_patterns['search_cmd_finished'], data):
                            break
                    else:
                        if self.canexit:
                            return (1, "")
                        raise Exception("No data on serial console socket, connection closed?")

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

@contextmanager
def nonblocking_lock(lock):
    locked = lock.acquire(False)
    try:
        yield locked
    finally:
        if locked:
            lock.release()

# This class is for reading data from a socket and passing it to logfunc
# to be processed. It's completely event driven and has a straightforward
# event loop. The mechanism for stopping the thread is a simple pipe which
# will wake up the poll and allow for tearing everything down.
class LoggingThread(threading.Thread):
    def __init__(self, logfunc, sock, logger, qemuoutput):
        self.connection_established = threading.Event()
        self.serial_lock = threading.Lock()

        self.serversock = sock
        self.serialsock = None
        self.qemuoutput = qemuoutput
        self.logfunc = logfunc
        self.logger = logger
        self.readsock = None
        self.running = False
        self.canexit = False

        self.errorevents = select.POLLERR | select.POLLHUP | select.POLLNVAL
        self.readevents = select.POLLIN | select.POLLPRI

        threading.Thread.__init__(self, target=self.threadtarget)

    def set_serialsock(self, serialsock):
        self.serialsock = serialsock

    def threadtarget(self):
        try:
            self.eventloop()
        except Exception:
            exc_type, exc_value, exc_traceback = sys.exc_info()
            self.logger.warning("Exception %s in logging thread" %
                                traceback.format_exception(exc_type, exc_value, exc_traceback))
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
        if self.serversock:
            self.close_socket(self.serversock)

        if self.readsock is not None:
            self.close_socket(self.readsock)

        self.close_ignore_error(self.readpipe)
        self.close_ignore_error(self.writepipe)
        self.running = False

    def allowexit(self):
        self.canexit = True

    def eventloop(self):
        poll = select.poll()
        event_read_mask = self.errorevents | self.readevents
        if self.serversock:
            poll.register(self.serversock.fileno())
        serial_registered = False
        poll.register(self.qemuoutput.fileno())
        poll.register(self.readpipe, event_read_mask)

        breakout = False
        self.running = True
        self.logger.debug("Starting thread event loop")
        while not breakout:
            events = poll.poll(2)
            for fd, event in events:

                # An error occurred, bail out
                if event & self.errorevents:
                    raise Exception(self.stringify_event(event))

                # Event to stop the thread
                if self.readpipe == fd:
                    self.logger.debug("Stop event received")
                    breakout = True
                    break

                # A connection request was received
                elif self.serversock and self.serversock.fileno() == fd:
                    self.logger.debug("Connection request received")
                    self.readsock, _ = self.serversock.accept()
                    self.readsock.setblocking(0)
                    poll.unregister(self.serversock.fileno())
                    poll.register(self.readsock.fileno(), event_read_mask)

                    self.logger.debug("Setting connection established event")
                    self.connection_established.set()

                # Actual data to be logged
                elif self.readsock and self.readsock.fileno() == fd:
                    data = self.recv(1024, self.readsock)
                    self.logfunc(data)
                elif self.qemuoutput.fileno() == fd:
                    data = self.qemuoutput.read()
                    self.logger.debug("Data received on qemu stdout %s" % data)
                    self.logfunc(data, ".stdout")
                elif self.serialsock and self.serialsock.fileno() == fd:
                    if self.serial_lock.acquire(blocking=False):
                        try:
                            data = self.recv(1024, self.serialsock)
                            self.logger.debug("Data received serial thread %s" % data.decode('utf-8', 'replace'))
                            self.logfunc(data, ".2")
                        finally:
                            self.serial_lock.release()
                    else:
                        serial_registered = False
                        poll.unregister(self.serialsock.fileno())

            if not serial_registered and self.serialsock:
                with nonblocking_lock(self.serial_lock) as l:
                    if l:
                        serial_registered = True
                        poll.register(self.serialsock.fileno(), event_read_mask)


    # Since the socket is non-blocking make sure to honor EAGAIN
    # and EWOULDBLOCK.
    def recv(self, count, sock):
        try:
            data = sock.recv(count)
        except socket.error as e:
            if e.errno == errno.EAGAIN or e.errno == errno.EWOULDBLOCK:
                return b''
            else:
                raise

        if data is None:
            raise Exception("No data on read ready socket")
        elif not data:
            # This actually means an orderly shutdown
            # happened. But for this code it counts as an
            # error since the connection shouldn't go away
            # until qemu exits.
            if not self.canexit:
                raise Exception("Console connection closed unexpectedly")
            return b''

        return data

    def stringify_event(self, event):
        val = ''
        if select.POLLERR == event:
            val = 'POLLER'
        elif select.POLLHUP == event:
            val = 'POLLHUP'
        elif select.POLLNVAL == event:
            val = 'POLLNVAL'
        else:
            val = "0x%x" % (event)

        return val

    def close_socket(self, sock):
        sock.shutdown(socket.SHUT_RDWR)
        sock.close()

    def close_ignore_error(self, fd):
        try:
            os.close(fd)
        except OSError:
            pass
