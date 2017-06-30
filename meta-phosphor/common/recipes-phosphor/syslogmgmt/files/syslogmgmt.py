#!/usr/bin/env python

import re
import gobject
import dbus
import dbus.service
import dbus.mainloop.glib
from subprocess import call
from obmc.dbuslib.bindings import DbusProperties, DbusObjectManager, get_dbus

DBUS_NAME = 'org.openbmc.SyslogManagement'
OBJ_NAME = '/org/openbmc/SyslogManagement'

# Busybox configuration file for syslog daemon
CFG_FILE = '/etc/default/busybox-syslog'


class SyslogdMgmt(DbusProperties, DbusObjectManager):
    def __init__(self, bus, name):
        super(SyslogdMgmt, self).__init__(conn=bus, object_path=name)

    @dbus.service.method(DBUS_NAME, in_signature='', out_signature='sq')
    def GetRemoteHost(self):
        """
        Get current syslogd settings, return tuple with remote host name
        and its port.
        """
        host = ''
        port = 0
        pattern = re.compile('^OPTIONS="-R (.*):(\d{1,5})"$')
        file = open(CFG_FILE, 'r')
        for line in file:
            match = re.search(pattern, line)
            if match:
                host = match.groups()[0]
                port = int(match.groups()[1])
                break
        file.close()
        return host, port

    @dbus.service.method(DBUS_NAME, in_signature='sq', out_signature='')
    def SetRemoteHost(self, host, port):
        """
        Set remote host and port to redirect syslogd messages.
        """
        host = host.strip();
        if len(host) > 255:  raise ValueError, 'Invalid host name'
        if not isinstance(port, (int, long)): raise ValueError, 'Invalid port'
        if port not in range(1, 65536): raise ValueError, 'Invalid port'
        self.__set_remote_host(host, port)
        self.__restart_syslogd()

    @dbus.service.method(DBUS_NAME, in_signature='', out_signature='')
    def Reset(self):
        """
        Reset syslogd settings (disable remote logging).
        """
        self.__set_remote_host(None, None)
        self.__restart_syslogd()

    def __set_remote_host(self, host, port):
        """
        Create configuration file for busybox syslogd
        """
        file = open(CFG_FILE, 'w')
        file.write('# This is automaticaly generated file. DO NOT MODIFY!\n')
        if host != None and port != None:
            file.write('OPTIONS="-R ' + host + ':' + str(port) + '"\n')
        file.close()

    def __restart_syslogd(self):
        """
        Restart syslogd service
        """
        call(['systemctl', 'restart', 'syslog'])


if __name__ == '__main__':
    dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
    bus = get_dbus()
    obj = SyslogdMgmt(bus, OBJ_NAME)
    name = dbus.service.BusName(DBUS_NAME, bus)
    mainloop = gobject.MainLoop()
    print 'Starting syslog settings manager'
    mainloop.run()
