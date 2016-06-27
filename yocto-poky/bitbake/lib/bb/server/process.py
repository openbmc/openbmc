#
# BitBake Process based server.
#
# Copyright (C) 2010 Bob Foerster <robert@erafx.com>
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

"""
    This module implements a multiprocessing.Process based server for bitbake.
"""

import bb
import bb.event
import itertools
import logging
import multiprocessing
import os
import signal
import sys
import time
import select
from Queue import Empty
from multiprocessing import Event, Process, util, Queue, Pipe, queues, Manager

from . import BitBakeBaseServer, BitBakeBaseServerConnection, BaseImplServer

logger = logging.getLogger('BitBake')

class ServerCommunicator():
    def __init__(self, connection, event_handle, server):
        self.connection = connection
        self.event_handle = event_handle
        self.server = server

    def runCommand(self, command):
        # @todo try/except
        self.connection.send(command)

        if not self.server.is_alive():
            raise SystemExit

        while True:
            # don't let the user ctrl-c while we're waiting for a response
            try:
                for idx in range(0,4): # 0, 1, 2, 3
                    if self.connection.poll(5):
                        return self.connection.recv()
                    else:
                        bb.warn("Timeout while attempting to communicate with bitbake server")
                bb.fatal("Gave up; Too many tries: timeout while attempting to communicate with bitbake server")
            except KeyboardInterrupt:
                pass

    def getEventHandle(self):
        return self.event_handle.value

class EventAdapter():
    """
    Adapter to wrap our event queue since the caller (bb.event) expects to
    call a send() method, but our actual queue only has put()
    """
    def __init__(self, queue):
        self.queue = queue

    def send(self, event):
        try:
            self.queue.put(event)
        except Exception as err:
            print("EventAdapter puked: %s" % str(err))


class ProcessServer(Process, BaseImplServer):
    profile_filename = "profile.log"
    profile_processed_filename = "profile.log.processed"

    def __init__(self, command_channel, event_queue, featurelist):
        BaseImplServer.__init__(self)
        Process.__init__(self)
        self.command_channel = command_channel
        self.event_queue = event_queue
        self.event = EventAdapter(event_queue)
        self.featurelist = featurelist
        self.quit = False

        self.quitin, self.quitout = Pipe()
        self.event_handle = multiprocessing.Value("i")

    def run(self):
        for event in bb.event.ui_queue:
            self.event_queue.put(event)
        self.event_handle.value = bb.event.register_UIHhandler(self, True)

        bb.cooker.server_main(self.cooker, self.main)

    def main(self):
        # Ignore SIGINT within the server, as all SIGINT handling is done by
        # the UI and communicated to us
        self.quitin.close()
        signal.signal(signal.SIGINT, signal.SIG_IGN)
        bb.utils.set_process_name("Cooker")
        while not self.quit:
            try:
                if self.command_channel.poll():
                    command = self.command_channel.recv()
                    self.runCommand(command)
                if self.quitout.poll():
                    self.quitout.recv()
                    self.quit = True
                    try:
                        self.runCommand(["stateForceShutdown"])
                    except:
                        pass

                self.idle_commands(.1, [self.command_channel, self.quitout])
            except Exception:
                logger.exception('Running command %s', command)

        self.event_queue.close()
        bb.event.unregister_UIHhandler(self.event_handle.value)
        self.command_channel.close()
        self.cooker.shutdown(True)
        self.quitout.close()

    def idle_commands(self, delay, fds=None):
        nextsleep = delay
        if not fds:
            fds = []

        for function, data in self._idlefuns.items():
            try:
                retval = function(self, data, False)
                if retval is False:
                    del self._idlefuns[function]
                    nextsleep = None
                elif retval is True:
                    nextsleep = None
                elif isinstance(retval, float):
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

        if nextsleep is not None:
            select.select(fds,[],[],nextsleep)

    def runCommand(self, command):
        """
        Run a cooker command on the server
        """
        self.command_channel.send(self.cooker.command.runCommand(command))

    def stop(self):
        self.quitin.send("quit")
        self.quitin.close()

class BitBakeProcessServerConnection(BitBakeBaseServerConnection):
    def __init__(self, serverImpl, ui_channel, event_queue):
        self.procserver = serverImpl
        self.ui_channel = ui_channel
        self.event_queue = event_queue
        self.connection = ServerCommunicator(self.ui_channel, self.procserver.event_handle, self.procserver)
        self.events = self.event_queue
        self.terminated = False

    def sigterm_terminate(self):
        bb.error("UI received SIGTERM")
        self.terminate()

    def terminate(self):
        if self.terminated:
            return
        self.terminated = True
        def flushevents():
            while True:
                try:
                    event = self.event_queue.get(block=False)
                except (Empty, IOError):
                    break
                if isinstance(event, logging.LogRecord):
                    logger.handle(event)

        signal.signal(signal.SIGINT, signal.SIG_IGN)
        self.procserver.stop()

        while self.procserver.is_alive():
            flushevents()
            self.procserver.join(0.1)

        self.ui_channel.close()
        self.event_queue.close()
        self.event_queue.setexit()

# Wrap Queue to provide API which isn't server implementation specific
class ProcessEventQueue(multiprocessing.queues.Queue):
    def __init__(self, maxsize):
        multiprocessing.queues.Queue.__init__(self, maxsize)
        self.exit = False
        bb.utils.set_process_name("ProcessEQueue")

    def setexit(self):
        self.exit = True

    def waitEvent(self, timeout):
        if self.exit:
            sys.exit(1)
        try:
            if not self.server.is_alive():
                self.setexit()
                return None
            return self.get(True, timeout)
        except Empty:
            return None

    def getEvent(self):
        try:
            if not self.server.is_alive():
                self.setexit()
                return None
            return self.get(False)
        except Empty:
            return None


class BitBakeServer(BitBakeBaseServer):
    def initServer(self, single_use=True):
        # establish communication channels.  We use bidirectional pipes for
        # ui <--> server command/response pairs
        # and a queue for server -> ui event notifications
        #
        self.ui_channel, self.server_channel = Pipe()
        self.event_queue = ProcessEventQueue(0)
        self.serverImpl = ProcessServer(self.server_channel, self.event_queue, None)
        self.event_queue.server = self.serverImpl

    def detach(self):
        self.serverImpl.start()
        return

    def establishConnection(self, featureset):

        self.connection = BitBakeProcessServerConnection(self.serverImpl, self.ui_channel, self.event_queue)

        _, error = self.connection.connection.runCommand(["setFeatures", featureset])
        if error:
            logger.error("Unable to set the cooker to the correct featureset: %s" % error)
            raise BaseException(error)
        signal.signal(signal.SIGTERM, lambda i, s: self.connection.sigterm_terminate())
        return self.connection
