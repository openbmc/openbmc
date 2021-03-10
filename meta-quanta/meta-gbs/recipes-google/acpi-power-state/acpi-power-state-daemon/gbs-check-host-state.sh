#!/bin/bash

state="xyz.openbmc_project.State.Chassis.PowerState.Off"

dbus-monitor --system type='signal',interface='org.freedesktop.DBus.Properties',\
member='PropertiesChanged',arg0namespace='xyz.openbmc_project.State.Chassis' | \
while read -r line; do
  grep -q member <<< $line && continue
  if grep -q $state <<< $line; then
    echo "Setting failsafe assuming host is off" >&2
    systemctl start --no-block gbs-host-s5-set-failsafe
  fi
done
