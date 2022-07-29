#
# BitBake Process based server.
#
# Copyright (C) 2010 Bob Foerster <robert@erafx.com>
#
# SPDX-License-Identifier: GPL-2.0-only
#

"""
    This module implements a multiprocessing.Process based server for bitbake.
"""

import bb
import bb.event
import logging
import multiprocessing
import threading
import array
import os
import sys
import time
import select
import socket
import subprocess
import errno
import re
import datetime
import pickle
import traceback
import gc
import bb.server.xmlrpcserver
from bb import daemonize
from multiprocessing import queues

logger = logging.getLogger('BitBake')

class ProcessTimeout(SystemExit):
    pass

def serverlog(msg):
    print(str(os.getpid()) + " " +  datetime.datetime.now().strftime('%H:%M:%S.%f') + " " + msg)
    sys.stdout.flush()

class ProcessServer():
    profile_filename = "profile.log"
    profile_processed_filename = "profile.log.processed"

    def __init__(self, lock, lockname, sock, sockname, server_timeout, xmlrpcinterface):
        self.command_channel = False
        self.command_channel_reply = False
        self.quit = False
        self.heartbeat_seconds = 1 # default, BB_HEARTBEAT_EVENT will be checked once we have a datastore.
        self.next_heartbeat = time.time()

        self.event_handle = None
        self.hadanyui = False
        self.haveui = False
        self.maxuiwait = 30
        self.xmlrpc = False

        self._idlefuns = {}

        self.bitbake_lock = lock
        self.bitbake_lock_name = lockname
        self.sock = sock
        self.sockname = sockname

        self.server_timeout = server_timeout
        self.timeout = self.server_timeout
        self.xmlrpcinterface = xmlrpcinterface

    def register_idle_function(self, function, data):
        """Register a function to be called while the server is idle"""
        assert hasattr(function, '__call__')
        self._idlefuns[function] = data

    def run(self):

        if self.xmlrpcinterface[0]:
            self.xmlrpc = bb.server.xmlrpcserver.BitBakeXMLRPCServer(self.xmlrpcinterface, self.cooker, self)

            serverlog("Bitbake XMLRPC server address: %s, server port: %s" % (self.xmlrpc.host, self.xmlrpc.port))

        try:
            self.bitbake_lock.seek(0)
            self.bitbake_lock.truncate()
            if self.xmlrpc:
                self.bitbake_lock.write("%s %s:%s\n" % (os.getpid(), self.xmlrpc.host, self.xmlrpc.port))
            else:
                self.bitbake_lock.write("%s\n" % (os.getpid()))
            self.bitbake_lock.flush()
        except Exception as e:
            serverlog("Error writing to lock file: %s" % str(e))
            pass

        if self.cooker.configuration.profile:
            try:
                import cProfile as profile
            except:
                import profile
            prof = profile.Profile()

            ret = profile.Profile.runcall(prof, self.main)

            prof.dump_stats("profile.log")
            bb.utils.process_profilelog("profile.log")
            serverlog("Raw profiling information saved to profile.log and processed statistics to profile.log.processed")

        else:
            ret = self.main()

        return ret

    def main(self):
        self.cooker.pre_serve()

        bb.utils.set_process_name("Cooker")

        ready = []
        newconnections = []

        self.controllersock = False
        fds = [self.sock]
        if self.xmlrpc:
            fds.append(self.xmlrpc)
        seendata = False
        serverlog("Entering server connection loop")

        def disconnect_client(self, fds):
            serverlog("Disconnecting Client")
            if self.controllersock:
                fds.remove(self.controllersock)
                self.controllersock.close()
                self.controllersock = False
            if self.haveui:
                fds.remove(self.command_channel)
                bb.event.unregister_UIHhandler(self.event_handle, True)
                self.command_channel_reply.writer.close()
                self.event_writer.writer.close()
                self.command_channel.close()
                self.command_channel = False
                del self.event_writer
                self.lastui = time.time()
                self.cooker.clientComplete()
                self.haveui = False
            ready = select.select(fds,[],[],0)[0]
            if newconnections:
                serverlog("Starting new client")
                conn = newconnections.pop(-1)
                fds.append(conn)
                self.controllersock = conn
            elif not self.timeout and not ready:
                serverlog("No timeout, exiting.")
                self.quit = True

        self.lastui = time.time()
        while not self.quit:
            if self.sock in ready:
                while select.select([self.sock],[],[],0)[0]:
                    controllersock, address = self.sock.accept()
                    if self.controllersock:
                        serverlog("Queuing %s (%s)" % (str(ready), str(newconnections)))
                        newconnections.append(controllersock)
                    else:
                        serverlog("Accepting %s (%s)" % (str(ready), str(newconnections)))
                        self.controllersock = controllersock
                        fds.append(controllersock)
            if self.controllersock in ready:
                try:
                    serverlog("Processing Client")
                    ui_fds = recvfds(self.controllersock, 3)
                    serverlog("Connecting Client")

                    # Where to write events to
                    writer = ConnectionWriter(ui_fds[0])
                    self.event_handle = bb.event.register_UIHhandler(writer, True)
                    self.event_writer = writer

                    # Where to read commands from
                    reader = ConnectionReader(ui_fds[1])
                    fds.append(reader)
                    self.command_channel = reader

                    # Where to send command return values to
                    writer = ConnectionWriter(ui_fds[2])
                    self.command_channel_reply = writer

                    self.haveui = True
                    self.hadanyui = True

                except (EOFError, OSError):
                    disconnect_client(self, fds)

            if not self.timeout == -1.0 and not self.haveui and self.timeout and \
                    (self.lastui + self.timeout) < time.time():
                serverlog("Server timeout, exiting.")
                self.quit = True

            # If we don't see a UI connection within maxuiwait, its unlikely we're going to see
            # one. We have had issue with processes hanging indefinitely so timing out UI-less
            # servers is useful.
            if not self.hadanyui and not self.xmlrpc and not self.timeout and (self.lastui + self.maxuiwait) < time.time():
                serverlog("No UI connection within max timeout, exiting to avoid infinite loop.")
                self.quit = True

            if self.command_channel in ready:
                try:
                    command = self.command_channel.get()
                except EOFError:
                    # Client connection shutting down
                    ready = []
                    disconnect_client(self, fds)
                    continue
                if command[0] == "terminateServer":
                    self.quit = True
                    continue
                try:
                    serverlog("Running command %s" % command)
                    self.command_channel_reply.send(self.cooker.command.runCommand(command))
                    serverlog("Command Completed")
                except Exception as e:
                   stack = traceback.format_exc()
                   serverlog('Exception in server main event loop running command %s (%s)' % (command, stack))
                   logger.exception('Exception in server main event loop running command %s (%s)' % (command, stack))

            if self.xmlrpc in ready:
                self.xmlrpc.handle_requests()

            if not seendata and hasattr(self.cooker, "data"):
                heartbeat_event = self.cooker.data.getVar('BB_HEARTBEAT_EVENT')
                if heartbeat_event:
                    try:
                        self.heartbeat_seconds = float(heartbeat_event)
                    except:
                        bb.warn('Ignoring invalid BB_HEARTBEAT_EVENT=%s, must be a float specifying seconds.' % heartbeat_event)

                self.timeout = self.server_timeout or self.cooker.data.getVar('BB_SERVER_TIMEOUT')
                try:
                    if self.timeout:
                        self.timeout = float(self.timeout)
                except:
                    bb.warn('Ignoring invalid BB_SERVER_TIMEOUT=%s, must be a float specifying seconds.' % self.timeout)
                seendata = True

            ready = self.idle_commands(.1, fds)

        serverlog("Exiting")
        # Remove the socket file so we don't get any more connections to avoid races
        try:
            os.unlink(self.sockname)
        except:
            pass
        self.sock.close()

        try:
            self.cooker.shutdown(True)
            self.cooker.notifier.stop()
            self.cooker.confignotifier.stop()
        except:
            pass

        self.cooker.post_serve()

        if len(threading.enumerate()) != 1:
            serverlog("More than one thread left?: " + str(threading.enumerate()))

        # Flush logs before we release the lock
        sys.stdout.flush()
        sys.stderr.flush()

        # Finally release the lockfile but warn about other processes holding it open
        lock = self.bitbake_lock
        lockfile = self.bitbake_lock_name

        def get_lock_contents(lockfile):
            try:
                with open(lockfile, "r") as f:
                    return f.readlines()
            except FileNotFoundError:
                return None

        lockcontents = get_lock_contents(lockfile)
        serverlog("Original lockfile contents: " + str(lockcontents))

        lock.close()
        lock = None

        while not lock:
            i = 0
            lock = None
            while not lock and i < 30:
                lock = bb.utils.lockfile(lockfile, shared=False, retry=False, block=False)
                if not lock:
                    newlockcontents = get_lock_contents(lockfile)
                    if newlockcontents != lockcontents:
                        # A new server was started, the lockfile contents changed, we can exit
                        serverlog("Lockfile now contains different contents, exiting: " + str(newlockcontents))
                        return
                    time.sleep(0.1)
                i += 1
            if lock:
                # We hold the lock so we can remove the file (hide stale pid data)
                # via unlockfile.
                bb.utils.unlockfile(lock)
                serverlog("Exiting as we could obtain the lock")
                return

            if not lock:
                # Some systems may not have lsof available
                procs = None
                try:
                    procs = subprocess.check_output(["lsof", '-w', lockfile], stderr=subprocess.STDOUT)
                except subprocess.CalledProcessError:
                    # File was deleted?
                    continue
                except OSError as e:
                    if e.errno != errno.ENOENT:
                        raise
                if procs is None:
                    # Fall back to fuser if lsof is unavailable
                    try:
                        procs = subprocess.check_output(["fuser", '-v', lockfile], stderr=subprocess.STDOUT)
                    except subprocess.CalledProcessError:
                        # File was deleted?
                        continue
                    except OSError as e:
                        if e.errno != errno.ENOENT:
                            raise

                msg = ["Delaying shutdown due to active processes which appear to be holding bitbake.lock"]
                if procs:
                    msg.append(":\n%s" % str(procs.decode("utf-8")))
                serverlog("".join(msg))

    def idle_commands(self, delay, fds=None):
        nextsleep = delay
        if not fds:
            fds = []

        for function, data in list(self._idlefuns.items()):
            try:
                retval = function(self, data, False)
                if retval is False:
                    del self._idlefuns[function]
                    nextsleep = None
                elif retval is True:
                    nextsleep = None
                elif isinstance(retval, float) and nextsleep:
                    if (retval < nextsleep):
                        nextsleep = retval
                elif nextsleep is None:
                    continue
                else:
                    fds = fds + retval
            except SystemExit:
                raise
            except Exception as exc:
                if not isinstance(exc, bb.BBHandledException):
                    logger.exception('Running idle function')
                del self._idlefuns[function]
                self.quit = True

        # Create new heartbeat event?
        now = time.time()
        if now >= self.next_heartbeat:
            # We might have missed heartbeats. Just trigger once in
            # that case and continue after the usual delay.
            self.next_heartbeat += self.heartbeat_seconds
            if self.next_heartbeat <= now:
                self.next_heartbeat = now + self.heartbeat_seconds
            if hasattr(self.cooker, "data"):
                heartbeat = bb.event.HeartbeatEvent(now)
                try:
                    bb.event.fire(heartbeat, self.cooker.data)
                except Exception as exc:
                    if not isinstance(exc, bb.BBHandledException):
                        logger.exception('Running heartbeat function')
                    self.quit = True
        if nextsleep and now + nextsleep > self.next_heartbeat:
            # Shorten timeout so that we we wake up in time for
            # the heartbeat.
            nextsleep = self.next_heartbeat - now

        if nextsleep is not None:
            if self.xmlrpc:
                nextsleep = self.xmlrpc.get_timeout(nextsleep)
            try:
                return select.select(fds,[],[],nextsleep)[0]
            except InterruptedError:
                # Ignore EINTR
                return []
        else:
            return select.select(fds,[],[],0)[0]


class ServerCommunicator():
    def __init__(self, connection, recv):
        self.connection = connection
        self.recv = recv

    def runCommand(self, command):
        self.connection.send(command)
        if not self.recv.poll(30):
            logger.info("No reply from server in 30s")
            if not self.recv.poll(30):
                raise ProcessTimeout("Timeout while waiting for a reply from the bitbake server (60s)")
        ret, exc = self.recv.get()
        # Should probably turn all exceptions in exc back into exceptions?
        # For now, at least handle BBHandledException
        if exc and ("BBHandledException" in exc or "SystemExit" in exc):
            raise bb.BBHandledException()
        return ret, exc

    def updateFeatureSet(self, featureset):
        _, error = self.runCommand(["setFeatures", featureset])
        if error:
            logger.error("Unable to set the cooker to the correct featureset: %s" % error)
            raise BaseException(error)

    def getEventHandle(self):
        handle, error = self.runCommand(["getUIHandlerNum"])
        if error:
            logger.error("Unable to get UI Handler Number: %s" % error)
            raise BaseException(error)

        return handle

    def terminateServer(self):
        self.connection.send(['terminateServer'])
        return

class BitBakeProcessServerConnection(object):
    def __init__(self, ui_channel, recv, eq, sock):
        self.connection = ServerCommunicator(ui_channel, recv)
        self.events = eq
        # Save sock so it doesn't get gc'd for the life of our connection
        self.socket_connection = sock

    def terminate(self):
        self.socket_connection.close()
        self.connection.connection.close()
        self.connection.recv.close()
        return

start_log_format = '--- Starting bitbake server pid %s at %s ---'
start_log_datetime_format = '%Y-%m-%d %H:%M:%S.%f'

class BitBakeServer(object):

    def __init__(self, lock, sockname, featureset, server_timeout, xmlrpcinterface):

        self.server_timeout = server_timeout
        self.xmlrpcinterface = xmlrpcinterface
        self.featureset = featureset
        self.sockname = sockname
        self.bitbake_lock = lock
        self.readypipe, self.readypipein = os.pipe()

        # Place the log in the builddirectory alongside the lock file
        logfile = os.path.join(os.path.dirname(self.bitbake_lock.name), "bitbake-cookerdaemon.log")
        self.logfile = logfile

        startdatetime = datetime.datetime.now()
        bb.daemonize.createDaemon(self._startServer, logfile)
        self.bitbake_lock.close()
        os.close(self.readypipein)

        ready = ConnectionReader(self.readypipe)
        r = ready.poll(5)
        if not r:
            bb.note("Bitbake server didn't start within 5 seconds, waiting for 90")
            r = ready.poll(90)
        if r:
            try:
                r = ready.get()
            except EOFError:
                # Trap the child exiting/closing the pipe and error out
                r = None
        if not r or r[0] != "r":
            ready.close()
            bb.error("Unable to start bitbake server (%s)" % str(r))
            if os.path.exists(logfile):
                logstart_re = re.compile(start_log_format % ('([0-9]+)', '([0-9-]+ [0-9:.]+)'))
                started = False
                lines = []
                lastlines = []
                with open(logfile, "r") as f:
                    for line in f:
                        if started:
                            lines.append(line)
                        else:
                            lastlines.append(line)
                            res = logstart_re.search(line.rstrip())
                            if res:
                                ldatetime = datetime.datetime.strptime(res.group(2), start_log_datetime_format)
                                if ldatetime >= startdatetime:
                                    started = True
                                    lines.append(line)
                        if len(lastlines) > 60:
                            lastlines = lastlines[-60:]
                if lines:
                    if len(lines) > 60:
                        bb.error("Last 60 lines of server log for this session (%s):\n%s" % (logfile, "".join(lines[-60:])))
                    else:
                        bb.error("Server log for this session (%s):\n%s" % (logfile, "".join(lines)))
                elif lastlines:
                        bb.error("Server didn't start, last 60 loglines (%s):\n%s" % (logfile, "".join(lastlines)))
            else:
                bb.error("%s doesn't exist" % logfile)

            raise SystemExit(1)

        ready.close()

    def _startServer(self):
        os.close(self.readypipe)
        os.set_inheritable(self.bitbake_lock.fileno(), True)
        os.set_inheritable(self.readypipein, True)
        serverscript = os.path.realpath(os.path.dirname(__file__) + "/../../../bin/bitbake-server")
        os.execl(sys.executable, "bitbake-server", serverscript, "decafbad", str(self.bitbake_lock.fileno()), str(self.readypipein), self.logfile, self.bitbake_lock.name, self.sockname,  str(self.server_timeout or 0), str(self.xmlrpcinterface[0]), str(self.xmlrpcinterface[1]))

def execServer(lockfd, readypipeinfd, lockname, sockname, server_timeout, xmlrpcinterface):

    import bb.cookerdata
    import bb.cooker

    serverlog(start_log_format % (os.getpid(), datetime.datetime.now().strftime(start_log_datetime_format)))

    try:
        bitbake_lock = os.fdopen(lockfd, "w")

        # Create server control socket
        if os.path.exists(sockname):
            os.unlink(sockname)

        sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        # AF_UNIX has path length issues so chdir here to workaround
        cwd = os.getcwd()
        try:
            os.chdir(os.path.dirname(sockname))
            sock.bind(os.path.basename(sockname))
        finally:
            os.chdir(cwd)
        sock.listen(1)

        server = ProcessServer(bitbake_lock, lockname, sock, sockname, server_timeout, xmlrpcinterface)
        writer = ConnectionWriter(readypipeinfd)
        try:
            featureset = []
            cooker = bb.cooker.BBCooker(featureset, server.register_idle_function)
        except bb.BBHandledException:
            return None
        writer.send("r")
        writer.close()
        server.cooker = cooker
        serverlog("Started bitbake server pid %d" % os.getpid())

        server.run()
    finally:
        # Flush any messages/errors to the logfile before exit
        sys.stdout.flush()
        sys.stderr.flush()

def connectProcessServer(sockname, featureset):
    # Connect to socket
    sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    # AF_UNIX has path length issues so chdir here to workaround
    cwd = os.getcwd()

    readfd = writefd = readfd1 = writefd1 = readfd2 = writefd2 = None
    eq = command_chan_recv = command_chan = None

    sock.settimeout(10)

    try:
        try:
            os.chdir(os.path.dirname(sockname))
            finished = False
            while not finished:
                try:
                    sock.connect(os.path.basename(sockname))
                    finished = True
                except IOError as e:
                    if e.errno == errno.EWOULDBLOCK:
                        pass
                    raise
        finally:
            os.chdir(cwd)

        # Send an fd for the remote to write events to
        readfd, writefd = os.pipe()
        eq = BBUIEventQueue(readfd)
        # Send an fd for the remote to recieve commands from
        readfd1, writefd1 = os.pipe()
        command_chan = ConnectionWriter(writefd1)
        # Send an fd for the remote to write commands results to
        readfd2, writefd2 = os.pipe()
        command_chan_recv = ConnectionReader(readfd2)

        sendfds(sock, [writefd, readfd1, writefd2])

        server_connection = BitBakeProcessServerConnection(command_chan, command_chan_recv, eq, sock)

        # Close the ends of the pipes we won't use
        for i in [writefd, readfd1, writefd2]:
            os.close(i)

        server_connection.connection.updateFeatureSet(featureset)

    except (Exception, SystemExit) as e:
        if command_chan_recv:
            command_chan_recv.close()
        if command_chan:
            command_chan.close()
        for i in [writefd, readfd1, writefd2]:
            try:
                if i:
                    os.close(i)
            except OSError:
                pass
        sock.close()
        raise

    return server_connection

def sendfds(sock, fds):
        '''Send an array of fds over an AF_UNIX socket.'''
        fds = array.array('i', fds)
        msg = bytes([len(fds) % 256])
        sock.sendmsg([msg], [(socket.SOL_SOCKET, socket.SCM_RIGHTS, fds)])

def recvfds(sock, size):
        '''Receive an array of fds over an AF_UNIX socket.'''
        a = array.array('i')
        bytes_size = a.itemsize * size
        msg, ancdata, flags, addr = sock.recvmsg(1, socket.CMSG_LEN(bytes_size))
        if not msg and not ancdata:
            raise EOFError
        try:
            if len(ancdata) != 1:
                raise RuntimeError('received %d items of ancdata' %
                                   len(ancdata))
            cmsg_level, cmsg_type, cmsg_data = ancdata[0]
            if (cmsg_level == socket.SOL_SOCKET and
                cmsg_type == socket.SCM_RIGHTS):
                if len(cmsg_data) % a.itemsize != 0:
                    raise ValueError
                a.frombytes(cmsg_data)
                assert len(a) % 256 == msg[0]
                return list(a)
        except (ValueError, IndexError):
            pass
        raise RuntimeError('Invalid data received')

class BBUIEventQueue:
    def __init__(self, readfd):

        self.eventQueue = []
        self.eventQueueLock = threading.Lock()
        self.eventQueueNotify = threading.Event()

        self.reader = ConnectionReader(readfd)

        self.t = threading.Thread()
        self.t.daemon = True
        self.t.run = self.startCallbackHandler
        self.t.start()

    def getEvent(self):
        self.eventQueueLock.acquire()

        if len(self.eventQueue) == 0:
            self.eventQueueLock.release()
            return None

        item = self.eventQueue.pop(0)

        if len(self.eventQueue) == 0:
            self.eventQueueNotify.clear()

        self.eventQueueLock.release()
        return item

    def waitEvent(self, delay):
        self.eventQueueNotify.wait(delay)
        return self.getEvent()

    def queue_event(self, event):
        self.eventQueueLock.acquire()
        self.eventQueue.append(event)
        self.eventQueueNotify.set()
        self.eventQueueLock.release()

    def send_event(self, event):
        self.queue_event(pickle.loads(event))

    def startCallbackHandler(self):
        bb.utils.set_process_name("UIEventQueue")
        while True:
            try:
                self.reader.wait()
                event = self.reader.get()
                self.queue_event(event)
            except EOFError:
                # Easiest way to exit is to close the file descriptor to cause an exit
                break
        self.reader.close()

class ConnectionReader(object):

    def __init__(self, fd):
        self.reader = multiprocessing.connection.Connection(fd, writable=False)
        self.rlock = multiprocessing.Lock()

    def wait(self, timeout=None):
        return multiprocessing.connection.wait([self.reader], timeout)

    def poll(self, timeout=None):
        return self.reader.poll(timeout)

    def get(self):
        with self.rlock:
            res = self.reader.recv_bytes()
        return multiprocessing.reduction.ForkingPickler.loads(res)

    def fileno(self):
        return self.reader.fileno()

    def close(self):
        return self.reader.close()


class ConnectionWriter(object):

    def __init__(self, fd):
        self.writer = multiprocessing.connection.Connection(fd, readable=False)
        self.wlock = multiprocessing.Lock()
        # Why bb.event needs this I have no idea
        self.event = self

    def _send(self, obj):
        gc.disable()
        with self.wlock:
            self.writer.send_bytes(obj)
        gc.enable()

    def send(self, obj):
        obj = multiprocessing.reduction.ForkingPickler.dumps(obj)
        # See notes/code in CookerParser
        # We must not terminate holding this lock else processes will hang.
        # For SIGTERM, raising afterwards avoids this.
        # For SIGINT, we don't want to have written partial data to the pipe.
        # pthread_sigmask block/unblock would be nice but doesn't work, https://bugs.python.org/issue47139
        process = multiprocessing.current_process()
        if process and hasattr(process, "queue_signals"):
            with process.signal_threadlock:
                process.queue_signals = True
                self._send(obj)
                process.queue_signals = False
                try:
                    for sig in process.signal_received.pop():
                        process.handle_sig(sig, None)
                except IndexError:
                    pass
        else:
            self._send(obj)

    def fileno(self):
        return self.writer.fileno()

    def close(self):
        return self.writer.close()
