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

SERVICE_PREFIX = 'org.openbmc.examples.services'
IFACE_PREFIX = 'org.openbmc.examples.interfaces'
BASE_OBJ_PATH = '/org/openbmc/examples/'

class SampleObjectOne(dbus.service.Object):
	def __init__(self, bus, name):
		super(SampleObjectOne, self).__init__(bus, name)

	@dbus.service.method(IFACE_PREFIX + '.Interface0', 's', 's')
	def echo(self, val):
		return str(type(self).__name__) + ": " + val

class SampleObjectTwo(SampleObjectOne):
	def __init__(self, bus, name):
		super(SampleObjectTwo, self).__init__(bus, name)
		self.map = {}

	@dbus.service.method(IFACE_PREFIX + '.Interface1', 'ss', '')
	def set_a_value_in_the_dict(self, key, value):
		self.map[key] = value

	@dbus.service.method(IFACE_PREFIX + '.Interface1', 's', 's')
	def get_a_value_from_the_dict(self, key):
		return self.map.get(key, "set a value first")

	@dbus.service.method(IFACE_PREFIX + '.Interface1', '', 's')
	def get_all_values_from_the_dict(self):
		return " ".join( [ x+ ':' + self.map[x] for x in self.map.keys() ] )

if __name__ == '__main__':
	dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

	bus = dbus.SystemBus()

	services = []
	services.append(dbus.service.BusName(SERVICE_PREFIX + '.service0', bus))
	services.append(dbus.service.BusName(SERVICE_PREFIX + '.service1', bus))

	objs = []
	objs.append(SampleObjectOne(bus, BASE_OBJ_PATH + 'path0/obj'))
	objs.append(SampleObjectTwo(bus, BASE_OBJ_PATH + 'path1/obj'))

	mainloop = gobject.MainLoop()

	print "obmc-phosphor-qemu starting..."
	mainloop.run()

