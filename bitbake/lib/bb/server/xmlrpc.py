#
# BitBake XMLRPC Server
#
# Copyright (C) 2006 - 2007  Michael 'Mickey' Lauer
# Copyright (C) 2006 - 2008  Richard Purdie
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
    This module implements an xmlrpc server for BitBake.

    Use this by deriving a class from BitBakeXMLRPCServer and then adding
    methods which you want to "export" via XMLRPC. If the methods have the
    prefix xmlrpc_, then registering those function will happen automatically,
    if not, you need to call register_function.

    Use register_idle_function() to add a function which the xmlrpc server
    calls from within server_forever when no requests are pending. Make sure
    that those functions are non-blocking or else you will introduce latency
    in the server's main loop.
"""

import bb
import xmlrpclib, sys
from bb import daemonize
from bb.ui import uievent
import hashlib, time
import socket
import os, signal
import threading
try:
    import cPickle as pickle
except ImportError:
    import pickle

DEBUG = False

from SimpleXMLRPCServer import SimpleXMLRPCServer, SimpleXMLRPCRequestHandler
import inspect, select, httplib

from . import BitBakeBaseServer, BitBakeBaseServerConnection, BaseImplServer

class BBTransport(xmlrpclib.Transport):
    def __init__(self, timeout):
        self.timeout = timeout
        self.connection_token = None
        xmlrpclib.Transport.__init__(self)

    # Modified from default to pass timeout to HTTPConnection
    def make_connection(self, host):
        #return an existing connection if possible.  This allows
        #HTTP/1.1 keep-alive.
        if self._connection and host == self._connection[0]:
            return self._connection[1]

        # create a HTTP connection object from a host descriptor
        chost, self._extra_headers, x509 = self.get_host_info(host)
        #store the host argument along with the connection object
        self._connection = host, httplib.HTTPConnection(chost, timeout=self.timeout)
        return self._connection[1]

    def set_connection_token(self, token):
        self.connection_token = token

    def send_content(self, h, body):
        if self.connection_token:
            h.putheader("Bitbake-token", self.connection_token)
        xmlrpclib.Transport.send_content(self, h, body)

def _create_server(host, port, timeout = 60):
    t = BBTransport(timeout)
    s = xmlrpclib.ServerProxy("http://%s:%d/" % (host, port), transport=t, allow_none=True)
    return s, t

class BitBakeServerCommands():

    def __init__(self, server):
        self.server = server
        self.has_client = False

    def registerEventHandler(self, host, port):
        """
        Register a remote UI Event Handler
        """
        s, t = _create_server(host, port)

        # we don't allow connections if the cooker is running
        if (self.cooker.state in [bb.cooker.state.parsing, bb.cooker.state.running]):
            return None, "Cooker is busy: %s" % bb.cooker.state.get_name(self.cooker.state)

        self.event_handle = bb.event.register_UIHhandler(s, True)
        return self.event_handle, 'OK'

    def unregisterEventHandler(self, handlerNum):
        """
        Unregister a remote UI Event Handler
        """
        return bb.event.unregister_UIHhandler(handlerNum)

    def runCommand(self, command):
        """
        Run a cooker command on the server
        """
        return self.cooker.command.runCommand(command, self.server.readonly)

    def getEventHandle(self):
        return self.event_handle

    def terminateServer(self):
        """
        Trigger the server to quit
        """
        self.server.quit = True
        print("Server (cooker) exiting")
        return

    def addClient(self):
        if self.has_client:
            return None
        token = hashlib.md5(str(time.time())).hexdigest()
        self.server.set_connection_token(token)
        self.has_client = True
        return token

    def removeClient(self):
        if self.has_client:
            self.server.set_connection_token(None)
            self.has_client = False
            if self.server.single_use:
                self.server.quit = True

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
        if remote_token != self.server.connection_token and remote_token != "observer":
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
        self.wfile.write(response)


class XMLRPCProxyServer(BaseImplServer):
    """ not a real working server, but a stub for a proxy server connection

    """
    def __init__(self, host, port):
        self.host = host
        self.port = port

class XMLRPCServer(SimpleXMLRPCServer, BaseImplServer):
    # remove this when you're done with debugging
    # allow_reuse_address = True

    def __init__(self, interface, single_use=False):
        """
        Constructor
        """
        BaseImplServer.__init__(self)
        self.single_use = single_use
        # Use auto port configuration
        if (interface[1] == -1):
            interface = (interface[0], 0)
        SimpleXMLRPCServer.__init__(self, interface,
                                    requestHandler=BitBakeXMLRPCRequestHandler,
                                    logRequests=False, allow_none=True)
        self.host, self.port = self.socket.getsockname()
        self.connection_token = None
        #self.register_introspection_functions()
        self.commands = BitBakeServerCommands(self)
        self.autoregister_all_functions(self.commands, "")
        self.interface = interface

    def addcooker(self, cooker):
        BaseImplServer.addcooker(self, cooker)
        self.commands.cooker = cooker

    def autoregister_all_functions(self, context, prefix):
        """
        Convenience method for registering all functions in the scope
        of this class that start with a common prefix
        """
        methodlist = inspect.getmembers(context, inspect.ismethod)
        for name, method in methodlist:
            if name.startswith(prefix):
                self.register_function(method, name[len(prefix):])


    def serve_forever(self):
        # Start the actual XMLRPC server
        bb.cooker.server_main(self.cooker, self._serve_forever)

    def _serve_forever(self):
        """
        Serve Requests. Overloaded to honor a quit command
        """
        self.quit = False
        while not self.quit:
            fds = [self]
            nextsleep = 0.1
            for function, data in self._idlefuns.items():
                retval = None
                try:
                    retval = function(self, data, False)
                    if retval is False:
                        del self._idlefuns[function]
                    elif retval is True:
                        nextsleep = 0
                    elif isinstance(retval, float):
                        if (retval < nextsleep):
                            nextsleep = retval
                    else:
                        fds = fds + retval
                except SystemExit:
                    raise
                except:
                    import traceback
                    traceback.print_exc()
                    if retval == None:
                        # the function execute failed; delete it
                        del self._idlefuns[function]
                    pass

            socktimeout = self.socket.gettimeout() or nextsleep
            socktimeout = min(socktimeout, nextsleep)
            # Mirror what BaseServer handle_request would do
            try:
                fd_sets = select.select(fds, [], [], socktimeout)
                if fd_sets[0] and self in fd_sets[0]:
                    self._handle_request_noblock()
            except IOError:
                # we ignore interrupted calls
                pass

        # Tell idle functions we're exiting
        for function, data in self._idlefuns.items():
            try:
                retval = function(self, data, True)
            except:
                pass
        self.server_close()
        return

    def set_connection_token(self, token):
        self.connection_token = token

class BitBakeXMLRPCServerConnection(BitBakeBaseServerConnection):
    def __init__(self, serverImpl, clientinfo=("localhost", 0), observer_only = False, featureset = None):
        self.connection, self.transport = _create_server(serverImpl.host, serverImpl.port)
        self.clientinfo = clientinfo
        self.serverImpl = serverImpl
        self.observer_only = observer_only
        if featureset:
            self.featureset = featureset
        else:
            self.featureset = []

    def connect(self, token = None):
        if token is None:
            if self.observer_only:
                token = "observer"
            else:
                token = self.connection.addClient()

        if token is None:
            return None

        self.transport.set_connection_token(token)
        return self

    def setupEventQueue(self):
        self.events = uievent.BBUIEventQueue(self.connection, self.clientinfo)
        for event in bb.event.ui_queue:
            self.events.queue_event(event)

        _, error = self.connection.runCommand(["setFeatures", self.featureset])
        if error:
            # disconnect the client, we can't make the setFeature work
            self.connection.removeClient()
            # no need to log it here, the error shall be sent to the client
            raise BaseException(error)

    def removeClient(self):
        if not self.observer_only:
            self.connection.removeClient()

    def terminate(self):
        # Don't wait for server indefinitely
        import socket
        socket.setdefaulttimeout(2)
        try:
            self.events.system_quit()
        except:
            pass
        try:
            self.connection.removeClient()
        except:
            pass

class BitBakeServer(BitBakeBaseServer):
    def initServer(self, interface = ("localhost", 0), single_use = False):
        self.interface = interface
        self.serverImpl = XMLRPCServer(interface, single_use)

    def detach(self):
        daemonize.createDaemon(self.serverImpl.serve_forever, "bitbake-cookerdaemon.log")
        del self.cooker

    def establishConnection(self, featureset):
        self.connection = BitBakeXMLRPCServerConnection(self.serverImpl, self.interface, False, featureset)
        return self.connection.connect()

    def set_connection_token(self, token):
        self.connection.transport.set_connection_token(token)

class BitBakeXMLRPCClient(BitBakeBaseServer):

    def __init__(self, observer_only = False, token = None):
        self.token = token

        self.observer_only = observer_only
        # if we need extra caches, just tell the server to load them all
        pass

    def saveConnectionDetails(self, remote):
        self.remote = remote

    def establishConnection(self, featureset):
        # The format of "remote" must be "server:port"
        try:
            [host, port] = self.remote.split(":")
            port = int(port)
        except Exception as e:
            bb.warn("Failed to read remote definition (%s)" % str(e))
            raise e

        # We need our IP for the server connection. We get the IP
        # by trying to connect with the server
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect((host, port))
            ip = s.getsockname()[0]
            s.close()
        except Exception as e:
            bb.warn("Could not create socket for %s:%s (%s)" % (host, port, str(e)))
            raise e
        try:
            self.serverImpl = XMLRPCProxyServer(host, port)
            self.connection = BitBakeXMLRPCServerConnection(self.serverImpl, (ip, 0), self.observer_only, featureset)
            return self.connection.connect(self.token)
        except Exception as e:
            bb.warn("Could not connect to server at %s:%s (%s)" % (host, port, str(e)))
            raise e

    def endSession(self):
        self.connection.removeClient()
