#!/bin/bash
# Rescan the ipmb busses for slots fru.

DBUS_SERVICE="xyz.openbmc_project.Ipmb.FruDevice"
DBUS_OBJECT="/xyz/openbmc_project/Ipmb/FruDevice"
DBUS_INTERFACE="xyz.openbmc_project.Ipmb.FruDeviceManager"

busctl call $DBUS_SERVICE $DBUS_OBJECT $DBUS_INTERFACE ReScan
