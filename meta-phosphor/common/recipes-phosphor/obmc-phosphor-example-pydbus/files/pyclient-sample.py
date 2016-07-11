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

import dbus
import dbus.service
import dbus.mainloop.glib
import gobject

SERVICE_PREFIX = 'org.openbmc.examples'
IFACE_PREFIX = 'org.openbmc.examples'
BASE_OBJ_PATH = '/org/openbmc/examples/'

def signal_callback(*a, **kw):
	print "Got signal:"
	print "intf: " + kw.get('intf', 'none')
	print "path: " + kw.get('path', 'none')
	print "member: " + kw.get('member', 'none')

if __name__ == '__main__':
	dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

	bus = dbus.SystemBus()

	obj0 = bus.get_object(SERVICE_PREFIX + '.PythonService',
		                      BASE_OBJ_PATH + 'path0/PythonObj')
	obj1 = bus.get_object(SERVICE_PREFIX + '.PythonService',
		                      BASE_OBJ_PATH + 'path1/PythonObj')
	echo0= dbus.Interface(obj0,
		    dbus_interface=IFACE_PREFIX + '.Echo')
	echo1= dbus.Interface(obj0,
		    dbus_interface=IFACE_PREFIX + '.Echo')

	print "Invoking Echo methods...."
	print(echo0.Echo("hello"))
	print(echo1.Echo("there"))
	
	bus.add_signal_receiver(signal_callback, interface_keyword = 'intf', 
			path_keyword = 'path',
			member_keyword = 'member')

	print "Waiting for signals... (call a sample method to see something here)"
	print "Press Ctrl^C to quit"
	mainloop = gobject.MainLoop()

	mainloop.run()
