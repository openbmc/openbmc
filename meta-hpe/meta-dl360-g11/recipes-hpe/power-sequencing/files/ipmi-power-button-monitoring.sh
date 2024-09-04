#!/bin/sh
# shellcheck disable=SC3061
dbus-monitor --system --profile "type='method_call',path='/xyz/openbmc_project/state/host0',interface='org.freedesktop.DBus.Properties',member='Set'" | awk '/xyz.openbmc_project.State.Host/ { print "DONE" }' | while read -r; do
/usr/bin/dl360Start.sh
done
