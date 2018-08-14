#!/bin/sh

# We get two "add" events for hostap cards due to wifi0
echo "$INTERFACE" | grep -q wifi && exit 0

# udevd does clearenv(). Export shell PATH to children.
export PATH

# Check if /etc/init.d/network has been run yet to see if we are 
# called by starting /etc/rcS.d/S03udev and not by hotplugging a device
#
# At this stage, network interfaces should not be brought up
# automatically because:
# a)	/etc/init.d/network has not been run yet (security issue)
# b) 	/var has not been populated yet so /etc/resolv,conf points to 
#	oblivion, making the network unusable
#

spoofp="`grep ^spoofprotect /etc/network/options`"
if test -z "$spoofp"
then
	# This is the default from /etc/init.d/network
	spoofp_val=yes
else
	spoofp_val=${spoofp#spoofprotect=}
fi

test "$spoofp_val" = yes && spoofp_val=1 || spoofp_val=0

# I think it is safe to assume that "lo" will always be there ;)
if test "`cat /proc/sys/net/ipv4/conf/lo/rp_filter`" != "$spoofp_val" -a -n "$spoofp_val"
then
	echo "$INTERFACE" >> /dev/udev_network_queue	
	exit 0
fi

#
# Code taken from pcmcia-cs:/etc/pcmcia/network
#

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
