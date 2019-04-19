#!/bin/sh
# set and unset occ active for all occ's in system

if [ "$1" == "disable" ]; then
    value='false'
elif [ "$1" == "enable" ]; then
    value='true'
else
    echo "Usage: occ-active.sh [argument]"
    echo "  enable  - set occ's to active state"
    echo "  disable - set occ's to inactive state"
    exit -1
fi

OCC_CONTROL_SERVICE="org.open_power.OCC.Control"

busctl tree $OCC_CONTROL_SERVICE --list | grep -i occ | xargs -r -n1 -I{} \
    busctl set-property $OCC_CONTROL_SERVICE {} org.open_power.OCC.Status \
    OccActive b $value
exit 0
