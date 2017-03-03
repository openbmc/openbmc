# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# Copyright (C) 2006 - 2007  Michael 'Mickey' Lauer
# Copyright (C) 2006 - 2007  Richard Purdie
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
Use this class to fork off a thread to recieve event callbacks from the bitbake
server and queue them for the UI to process. This process must be used to avoid
client/server deadlocks.
"""

import socket, threading, pickle, collections
from xmlrpc.server import SimpleXMLRPCServer, SimpleXMLRPCRequestHandler

class BBUIEventQueue:
    def __init__(self, BBServer, clientinfo=("localhost, 0")):

        self.eventQueue = []
        self.eventQueueLock = threading.Lock()
        self.eventQueueNotify = threading.Event()

        self.BBServer = BBServer
        self.clientinfo = clientinfo

        server = UIXMLRPCServer(self.clientinfo)
        self.host, self.port = server.socket.getsockname()

        server.register_function( self.system_quit, "event.quit" )
        server.register_function( self.send_event, "event.sendpickle" )
        server.socket.settimeout(1)

        self.EventHandle = None

        # the event handler registration may fail here due to cooker being in invalid state
        # this is a transient situation, and we should retry a couple of times before
        # giving up

        for count_tries in range(5):
            ret = self.BBServer.registerEventHandler(self.host, self.port)

            if isinstance(ret, collections.Iterable):
                self.EventHandle, error = ret
            else:
                self.EventHandle = ret
                error = ""

            if self.EventHandle != None:
                break

            errmsg = "Could not register UI event handler. Error: %s, host %s, "\
                     "port %d" % (error, self.host, self.port)
            bb.warn("%s, retry" % errmsg)

            import time
            time.sleep(1)
        else:
            raise Exception(errmsg)

        self.server = server

        self.t = threading.Thread()
        self.t.setDaemon(True)
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

        self.server.timeout = 1
        bb.utils.set_process_name("UIEventQueue")
        while not self.server.quit:
            try:
                self.server.handle_request()
            except Exception as e:
                import traceback
                logger.error("BBUIEventQueue.startCallbackHandler: Exception while trying to handle request: %s\n%s" % (e, traceback.format_exc()))

        self.server.server_close()

    def system_quit( self ):
        """
        Shut down the callback thread
        """
        try:
            self.BBServer.unregisterEventHandler(self.EventHandle)
        except:
            pass
        self.server.quit = True

class UIXMLRPCServer (SimpleXMLRPCServer):

    def __init__( self, interface ):
        self.quit = False
        SimpleXMLRPCServer.__init__( self,
                                    interface,
                                    requestHandler=SimpleXMLRPCRequestHandler,
                                    logRequests=False, allow_none=True, use_builtin_types=True)

    def get_request(self):
        while not self.quit:
            try:
                sock, addr = self.socket.accept()
                sock.settimeout(1)
                return (sock, addr)
            except socket.timeout:
                pass
        return (None, None)

    def close_request(self, request):
        if request is None:
            return
        SimpleXMLRPCServer.close_request(self, request)

    def process_request(self, request, client_address):
        if request is None:
            return
        SimpleXMLRPCServer.process_request(self, request, client_address)

