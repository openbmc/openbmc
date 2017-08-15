#!/bin/sh
# set and unset occ active for all occ's in system

if [ $# -eq 0 ]; then
    echo "enable:  set occ's to acive state"
    echo "disable: set occ's to inactive state"
    exit -1
fi

if [ $1 == 'disable' ]; then
    echo "disable"
    busctl tree org.open_power.OCC.Control --list | grep occ | xargs -n1 -I{} busctl set-property org.open_power.OCC.Control {} org.open_power.OCC.Status OccActive b false
    exit 0
elif [ $1 = 'enable' ]; then
    echo "enable"
    busctl tree org.open_power.OCC.Control --list | grep occ | xargs -n1 -I{} busctl set-property org.open_power.OCC.Control {} org.open_power.OCC.Status OccActive b true
    exit 0
fi
