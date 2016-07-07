#!/usr/bin/env python

# Contributors Listed Below - COPYRIGHT 2015
# [+] International Business Machines Corp.
#
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied. See the License for the specific language governing
# permissions and limitations under the License.

import sys
import dbus
import dbus.service
import dbus.mainloop.glib
import gobject

SERVICE_PREFIX = 'org.openbmc.examples'
IFACE_PREFIX = 'org.openbmc.examples'
BASE_OBJ_PATH = '/org/openbmc/examples/'

class SampleObjectOne(dbus.service.Object):
	def __init__(self, bus, name):
		super(SampleObjectOne, self).__init__(bus, name)

	@dbus.service.method(IFACE_PREFIX + '.Echo', 's', 's')
	def Echo(self, val):
		self.MethodInvoked("Echo method was invoked", self._object_path)
		return self._object_path + " says " + val
	
	@dbus.service.signal(IFACE_PREFIX + '.Echo', 'ss')
	def MethodInvoked(self, message, path):
		pass

class SampleObjectTwo(SampleObjectOne):
	def __init__(self, bus, name):
		super(SampleObjectTwo, self).__init__(bus, name)
		self.map = { 'empty' : 'add values to me' }

	@dbus.service.signal(IFACE_PREFIX + '.Dict', 'sss')
	def DictMethodCalled(self, message, key, value):
		pass

	@dbus.service.method(IFACE_PREFIX + '.Dict', 'ss', '')
	def SetAValueInTheDict(self, key, value):
		self.DictMethodCalled("Dict method was invoked", key, value)
		self.map[key] = value

	@dbus.service.method(IFACE_PREFIX + '.Dict', 's', 's')
	def GetAValueFromTheDict(self, key):
		return self.map.get(key, "set a value first")

	@dbus.service.method(IFACE_PREFIX + '.Dict', '', 's')
	def GetAllValuesFromTheDict(self):
		return " ".join( [ x+ ':' + self.map[x] for x in self.map.keys() ] )

	@dbus.service.method(dbus.PROPERTIES_IFACE, 'ss', 'v')
	def Get(self, interface, prop):
		return self.GetAll(interface)[prop]

	@dbus.service.method(dbus.PROPERTIES_IFACE, 's', 'a{sv}')
	def GetAll(self, interface):
		if interface == IFACE_PREFIX + '.Dict':
			return { 'Dict': self.map }

	@dbus.service.method(dbus.PROPERTIES_IFACE, 'ssv')
	def Set(self, interface, prop, value):
		if prop == 'Dict':
			self.map = value
			self.PropertiesChanged(interface, { prop : value }, [])

	@dbus.service.signal(dbus.PROPERTIES_IFACE, 'sa{sv}as')
	def PropertiesChanged(self, interface, properties, invalidated_properties):
		pass

if __name__ == '__main__':
	dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

	bus = dbus.SystemBus()

	services = []
	services.append(dbus.service.BusName(SERVICE_PREFIX + '.PythonService', bus))

	objs = []
	objs.append(SampleObjectOne(bus, BASE_OBJ_PATH + 'path0/PythonObj'))
	objs.append(SampleObjectTwo(bus, BASE_OBJ_PATH + 'path1/PythonObj'))

	mainloop = gobject.MainLoop()

	print "obmc-phosphor-example-pydbus starting..."
	mainloop.run()

