# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# This module provides a class for starting qemu images using runqemu.
# It's used by testimage.bbclass.

import subprocess
import os
import time
import signal
import re
import socket
import select
import errno
import string
import threading
import codecs
from oeqa.utils.dump import HostDumper

import logging
logger = logging.getLogger("BitBake.QemuRunner")

# Get Unicode non printable control chars
control_range = range(0,32)+range(127,160)
control_chars = [unichr(x) for x in control_range
                if unichr(x) not in string.printable]
re_control_char = re.compile('[%s]' % re.escape("".join(control_chars)))

class QemuRunner:

    def __init__(self, machine, rootfs, display, tmpdir, deploy_dir_image, logfile, boottime, dump_dir, dump_host_cmds):

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
        self.logged = False
        self.thread = None

        self.runqemutime = 60
        self.host_dumper = HostDumper(dump_host_cmds, dump_dir)

    def create_socket(self):
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.setblocking(0)
            sock.bind(("127.0.0.1",0))
            sock.listen(2)
            port = sock.getsockname()[1]
            logger.info("Created listening socket for qemu serial console on: 127.0.0.1:%s" % port)
            return (sock, port)

        except socket.error:
            sock.close()
            raise

    def log(self, msg):
        if self.logfile:
            # It is needed to sanitize the data received from qemu
            # because is possible to have control characters
            msg = re_control_char.sub('', unicode(msg, 'utf-8'))
            with codecs.open(self.logfile, "a", encoding="utf-8") as f:
                f.write("%s" % msg)

    def getOutput(self, o):
        import fcntl
        fl = fcntl.fcntl(o, fcntl.F_GETFL)
        fcntl.fcntl(o, fcntl.F_SETFL, fl | os.O_NONBLOCK)
        return os.read(o.fileno(), 1000000)


    def handleSIGCHLD(self, signum, frame):
        if self.runqemu and self.runqemu.poll():
            if self.runqemu.returncode:
                logger.info('runqemu exited with code %d' % self.runqemu.returncode)
                logger.info("Output from runqemu:\n%s" % self.getOutput(self.runqemu.stdout))
                self.stop()
                self._dump_host()
                raise SystemExit

    def start(self, qemuparams = None, get_ip = True):
        if self.display:
            os.environ["DISPLAY"] = self.display
            # Set this flag so that Qemu doesn't do any grabs as SDL grabs
            # interact badly with screensavers.
            os.environ["QEMU_DONT_GRAB"] = "1"
        if not os.path.exists(self.rootfs):
            logger.error("Invalid rootfs %s" % self.rootfs)
            return False
        if not os.path.exists(self.tmpdir):
            logger.error("Invalid TMPDIR path %s" % self.tmpdir)
            return False
        else:
            os.environ["OE_TMPDIR"] = self.tmpdir
        if not os.path.exists(self.deploy_dir_image):
            logger.error("Invalid DEPLOY_DIR_IMAGE path %s" % self.deploy_dir_image)
            return False
        else:
            os.environ["DEPLOY_DIR_IMAGE"] = self.deploy_dir_image

        try:
            threadsock, threadport = self.create_socket()
            self.server_socket, self.serverport = self.create_socket()
        except socket.error, msg:
            logger.error("Failed to create listening socket: %s" % msg[1])
            return False


        self.qemuparams = 'bootparams="console=tty1 console=ttyS0,115200n8 printk.time=1" qemuparams="-serial tcp:127.0.0.1:{}"'.format(threadport)
        if not self.display:
            self.qemuparams = 'nographic ' + self.qemuparams
        if qemuparams:
            self.qemuparams = self.qemuparams[:-1] + " " + qemuparams + " " + '\"'

        self.origchldhandler = signal.getsignal(signal.SIGCHLD)
        signal.signal(signal.SIGCHLD, self.handleSIGCHLD)

        launch_cmd = 'runqemu tcpserial=%s %s %s %s' % (self.serverport, self.machine, self.rootfs, self.qemuparams)
        # FIXME: We pass in stdin=subprocess.PIPE here to work around stty
        # blocking at the end of the runqemu script when using this within
        # oe-selftest (this makes stty error out immediately). There ought
        # to be a proper fix but this will suffice for now.
        self.runqemu = subprocess.Popen(launch_cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, stdin=subprocess.PIPE, preexec_fn=os.setpgrp)
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

        logger.info("runqemu started, pid is %s" % self.runqemu.pid)
        logger.info("waiting at most %s seconds for qemu pid" % self.runqemutime)
        endtime = time.time() + self.runqemutime
        while not self.is_alive() and time.time() < endtime:
            if self.runqemu.poll():
                if self.runqemu.returncode:
                    # No point waiting any longer
                    logger.info('runqemu exited with code %d' % self.runqemu.returncode)
                    self._dump_host()
                    self.stop()
                    logger.info("Output from runqemu:\n%s" % self.getOutput(output))
                    return False
            time.sleep(1)

        if self.is_alive():
            logger.info("qemu started - qemu procces pid is %s" % self.qemupid)
            if get_ip:
                cmdline = ''
                with open('/proc/%s/cmdline' % self.qemupid) as p:
                    cmdline = p.read()
                    # It is needed to sanitize the data received
                    # because is possible to have control characters
                    cmdline = re_control_char.sub('', cmdline)
                try:
                    ips = re.findall("((?:[0-9]{1,3}\.){3}[0-9]{1,3})", cmdline.split("ip=")[1])
                    if not ips or len(ips) != 3:
                        raise ValueError
                    else:
                        self.ip = ips[0]
                        self.server_ip = ips[1]
                except IndexError, ValueError:
                    logger.info("Couldn't get ip from qemu process arguments! Here is the qemu command line used:\n%s\nand output from runqemu:\n%s" % (cmdline, self.getOutput(output)))
                    self._dump_host()
                    self.stop()
                    return False
                logger.info("qemu cmdline used:\n{}".format(cmdline))
                logger.info("Target IP: %s" % self.ip)
                logger.info("Server IP: %s" % self.server_ip)

            self.thread = LoggingThread(self.log, threadsock, logger)
            self.thread.start()
            if not self.thread.connection_established.wait(self.boottime):
                logger.error("Didn't receive a console connection from qemu. "
                             "Here is the qemu command line used:\n%s\nand "
                             "output from runqemu:\n%s" % (cmdline,
                                                           self.getOutput(output)))
                self.stop_thread()
                return False

            logger.info("Output from runqemu:\n%s", self.getOutput(output))
            logger.info("Waiting at most %d seconds for login banner" % self.boottime)
            endtime = time.time() + self.boottime
            socklist = [self.server_socket]
            reachedlogin = False
            stopread = False
            qemusock = None
            bootlog = ''
            while time.time() < endtime and not stopread:
                sread, swrite, serror = select.select(socklist, [], [], 5)
                for sock in sread:
                    if sock is self.server_socket:
                        qemusock, addr = self.server_socket.accept()
                        qemusock.setblocking(0)
                        socklist.append(qemusock)
                        socklist.remove(self.server_socket)
                        logger.info("Connection from %s:%s" % addr)
                    else:
                        data = sock.recv(1024)
                        if data:
                            bootlog += data
                            if re.search(".* login:", bootlog):
                                self.server_socket = qemusock
                                stopread = True
                                reachedlogin = True
                                logger.info("Reached login banner")
                        else:
                            socklist.remove(sock)
                            sock.close()
                            stopread = True

            if not reachedlogin:
                logger.info("Target didn't reached login boot in %d seconds" % self.boottime)
                lines = "\n".join(bootlog.splitlines()[-25:])
                logger.info("Last 25 lines of text:\n%s" % lines)
                logger.info("Check full boot log: %s" % self.logfile)
                self._dump_host()
                self.stop()
                return False

            # If we are not able to login the tests can continue
            try:
                (status, output) = self.run_serial("root\n", raw=True)
                if re.search("root@[a-zA-Z0-9\-]+:~#", output):
                    self.logged = True
                    logger.info("Logged as root in serial console")
                else:
                    logger.info("Couldn't login into serial console"
                            " as root using blank password")
            except:
                logger.info("Serial console failed while trying to login")

        else:
            logger.info("Qemu pid didn't appeared in %s seconds" % self.runqemutime)
            self._dump_host()
            self.stop()
            logger.info("Output from runqemu:\n%s" % self.getOutput(output))
            return False

        return self.is_alive()

    def stop(self):
        self.stop_thread()
        if hasattr(self, "origchldhandler"):
            signal.signal(signal.SIGCHLD, self.origchldhandler)
        if self.runqemu:
            os.kill(self.monitorpid, signal.SIGKILL)
            logger.info("Sending SIGTERM to runqemu")
            try:
                os.killpg(os.getpgid(self.runqemu.pid), signal.SIGTERM)
            except OSError as e:
                if e.errno != errno.ESRCH:
                    raise
            endtime = time.time() + self.runqemutime
            while self.runqemu.poll() is None and time.time() < endtime:
                time.sleep(1)
            if self.runqemu.poll() is None:
                logger.info("Sending SIGKILL to runqemu")
                os.killpg(os.getpgid(self.runqemu.pid), signal.SIGKILL)
            self.runqemu = None
        if hasattr(self, 'server_socket') and self.server_socket:
            self.server_socket.close()
            self.server_socket = None
        self.qemupid = None
        self.ip = None

    def stop_thread(self):
        if self.thread and self.thread.is_alive():
            self.thread.stop()
            self.thread.join()

    def restart(self, qemuparams = None):
        logger.info("Restarting qemu process")
        if self.runqemu.poll() is None:
            self.stop()
        if self.start(qemuparams):
            return True
        return False

    def is_alive(self):
        if not self.runqemu:
            return False
        qemu_child = self.find_child(str(self.runqemu.pid))
        if qemu_child:
            self.qemupid = qemu_child[0]
            if os.path.exists("/proc/" + str(self.qemupid)):
                return True
        return False

    def find_child(self,parent_pid):
        #
        # Walk the process tree from the process specified looking for a qemu-system. Return its [pid'cmd]
        #
        ps = subprocess.Popen(['ps', 'axww', '-o', 'pid,ppid,command'], stdout=subprocess.PIPE).communicate()[0]
        processes = ps.split('\n')
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
        #print "Children matching %s:" % str(parents)
        for p in parents:
            # Need to be careful here since runqemu-internal runs "ldd qemu-system-xxxx"
            # Also, old versions of ldd (2.11) run "LD_XXXX qemu-system-xxxx"
            basecmd = commands[p].split()[0]
            basecmd = os.path.basename(basecmd)
            if "qemu-system" in basecmd and "-serial tcp" in commands[p]:
                return [int(p),commands[p]]

    def run_serial(self, command, raw=False):
        # We assume target system have echo to get command status
        if not raw:
            command = "%s; echo $?\n" % command

        data = ''
        status = 0
        self.server_socket.sendall(command)
        keepreading = True
        while keepreading:
            sread, _, _ = select.select([self.server_socket],[],[],5)
            if sread:
                answer = self.server_socket.recv(1024)
                if answer:
                    data += answer
                    # Search the prompt to stop
                    if re.search("[a-zA-Z0-9]+@[a-zA-Z0-9\-]+:~#", data):
                        keepreading = False
                else:
                    raise Exception("No data on serial console socket")
            else:
                keepreading = False

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
        logger.warn("Qemu ended unexpectedly, dump data from host"
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
        self.logger.info("Starting logging thread")
        self.readpipe, self.writepipe = os.pipe()
        threading.Thread.run(self)

    def stop(self):
        self.logger.info("Stopping logging thread")
        if self.running:
            os.write(self.writepipe, "stop")

    def teardown(self):
        self.logger.info("Tearing down logging thread")
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
        self.logger.info("Starting thread event loop")
        while not breakout:
            events = poll.poll()
            for event in events:
                # An error occurred, bail out
                if event[1] & self.errorevents:
                    raise Exception(self.stringify_event(event[1]))

                # Event to stop the thread
                if self.readpipe == event[0]:
                    self.logger.info("Stop event received")
                    breakout = True
                    break

                # A connection request was received
                elif self.serversock.fileno() == event[0]:
                    self.logger.info("Connection request received")
                    self.readsock, _ = self.serversock.accept()
                    self.readsock.setblocking(0)
                    poll.unregister(self.serversock.fileno())
                    poll.register(self.readsock.fileno(), event_read_mask)

                    self.logger.info("Setting connection established event")
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
