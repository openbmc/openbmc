#
# BitBake Base Server Code
#
# Copyright (C) 2006 - 2007  Michael 'Mickey' Lauer
# Copyright (C) 2006 - 2008  Richard Purdie
# Copyright (C) 2013         Alexandru Damian
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

""" Base code for Bitbake server process

Have a common base for that all Bitbake server classes ensures a consistent
approach to the interface, and minimize risks associated with code duplication.

"""

"""  BaseImplServer() the base class for all XXServer() implementations.

    These classes contain the actual code that runs the server side, i.e.
    listens for the commands and executes them. Although these implementations
    contain all the data of the original bitbake command, i.e the cooker instance,
    they may well run on a different process or even machine.

"""

class BaseImplServer():
    def __init__(self):
        self._idlefuns = {}

    def addcooker(self, cooker):
        self.cooker = cooker

    def register_idle_function(self, function, data):
        """Register a function to be called while the server is idle"""
        assert hasattr(function, '__call__')
        self._idlefuns[function] = data



""" BitBakeBaseServerConnection class is the common ancestor to all
    BitBakeServerConnection classes.

    These classes control the remote server. The only command currently
    implemented is the terminate() command.

"""

class BitBakeBaseServerConnection():
    def __init__(self, serverImpl):
        pass

    def terminate(self):
        pass


""" BitBakeBaseServer class is the common ancestor to all Bitbake servers

    Derive this class in order to implement a BitBakeServer which is the
    controlling stub for the actual server implementation

"""
class BitBakeBaseServer(object):
    def initServer(self):
        self.serverImpl = None  # we ensure a runtime crash if not overloaded
        self.connection = None
        return

    def addcooker(self, cooker):
        self.cooker = cooker
        self.serverImpl.addcooker(cooker)

    def getServerIdleCB(self):
        return self.serverImpl.register_idle_function

    def saveConnectionDetails(self):
        return

    def detach(self):
        return

    def establishConnection(self, featureset):
        raise   "Must redefine the %s.establishConnection()" % self.__class__.__name__

    def endSession(self):
        self.connection.terminate()
