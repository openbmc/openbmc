#
# BitBake XMLRPC Server Interface
#
# Copyright (C) 2006 - 2007  Michael 'Mickey' Lauer
# Copyright (C) 2006 - 2008  Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import hashlib
import time
import inspect
from xmlrpc.server import SimpleXMLRPCServer, SimpleXMLRPCRequestHandler
import bb.server.xmlrpcclient

import bb
import bb.cooker
import bb.event

# This request handler checks if the request has a "Bitbake-token" header
# field (this comes from the client side) and compares it with its internal
# "Bitbake-token" field (this comes from the server). If the two are not
# equal, it is assumed that a client is trying to connect to the server
# while another client is connected to the server. In this case, a 503 error
# ("service unavailable") is returned to the client.
class BitBakeXMLRPCRequestHandler(SimpleXMLRPCRequestHandler):
    def __init__(self, request, client_address, server):
        self.server = server
        SimpleXMLRPCRequestHandler.__init__(self, request, client_address, server)

    def do_POST(self):
        try:
            remote_token = self.headers["Bitbake-token"]
        except:
            remote_token = None
        if 0 and remote_token != self.server.connection_token and remote_token != "observer":
            self.report_503()
        else:
            if remote_token == "observer":
                self.server.readonly = True
            else:
                self.server.readonly = False
            SimpleXMLRPCRequestHandler.do_POST(self)

    def report_503(self):
        self.send_response(503)
        response = 'No more client allowed'
        self.send_header("Content-type", "text/plain")
        self.send_header("Content-length", str(len(response)))
        self.end_headers()
        self.wfile.write(bytes(response, 'utf-8'))

class BitBakeXMLRPCServer(SimpleXMLRPCServer):
    # remove this when you're done with debugging
    # allow_reuse_address = True

    def __init__(self, interface, cooker, parent):
        # Use auto port configuration
        if interface[1] == -1:
            interface = (interface[0], 0)
        SimpleXMLRPCServer.__init__(self, interface,
                                    requestHandler=BitBakeXMLRPCRequestHandler,
                                    logRequests=False, allow_none=True)
        self.host, self.port = self.socket.getsockname()
        self.interface = interface

        self.connection_token = None
        self.commands = BitBakeXMLRPCServerCommands(self)
        self.register_functions(self.commands, "")

        self.cooker = cooker
        self.parent = parent


    def register_functions(self, context, prefix):
        """
        Convenience method for registering all functions in the scope
        of this class that start with a common prefix
        """
        methodlist = inspect.getmembers(context, inspect.ismethod)
        for name, method in methodlist:
            if name.startswith(prefix):
                self.register_function(method, name[len(prefix):])

    def get_timeout(self, delay):
        socktimeout = self.socket.gettimeout() or delay
        return min(socktimeout, delay)

    def handle_requests(self):
        self._handle_request_noblock()

class BitBakeXMLRPCServerCommands:

    def __init__(self, server):
        self.server = server
        self.has_client = False
        self.event_handle = None

    def registerEventHandler(self, host, port):
        """
        Register a remote UI Event Handler
        """
        s, t = bb.server.xmlrpcclient._create_server(host, port)

        # we don't allow connections if the cooker is running
        if self.server.cooker.state in [bb.cooker.State.PARSING, bb.cooker.State.RUNNING]:
            return None, f"Cooker is busy: {self.server.cooker.state.name}"

        self.event_handle = bb.event.register_UIHhandler(s, True)
        return self.event_handle, 'OK'

    def unregisterEventHandler(self, handlerNum):
        """
        Unregister a remote UI Event Handler
        """
        ret = bb.event.unregister_UIHhandler(handlerNum, True)
        self.event_handle = None
        return ret

    def runCommand(self, command):
        """
        Run a cooker command on the server
        """
        return self.server.cooker.command.runCommand(command, self.server.parent, self.server.readonly)

    def getEventHandle(self):
        return self.event_handle

    def terminateServer(self):
        """
        Trigger the server to quit
        """
        self.server.parent.quit = True
        print("XMLRPC Server triggering exit")
        return

    def addClient(self):
        if self.server.parent.haveui:
            return None
        token = hashlib.md5(str(time.time()).encode("utf-8")).hexdigest()
        self.server.connection_token = token
        self.server.parent.haveui = True
        return token

    def removeClient(self):
        if self.server.parent.haveui:
            self.server.connection_token = None
            self.server.parent.haveui = False

