#!/bin/sh

# We get two "add" events for hostap cards due to wifi0
echo "$INTERFACE" | grep -q wifi && exit 0

# udevd does clearenv(). Export shell PATH to children.
export PATH

# if this interface has an entry in /etc/network/interfaces, let ifupdown
# handle it
if grep -q "iface \+$INTERFACE" /etc/network/interfaces; then
  case $ACTION in
    add)
    	ifconfig | grep -q "^$INTERFACE" || ifup $INTERFACE
    	;;
    remove)
    	ifdown $INTERFACE
    	;;
  esac
  
  exit 0
fi
