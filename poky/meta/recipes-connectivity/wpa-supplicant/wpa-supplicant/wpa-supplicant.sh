#!/bin/sh


WPA_SUP_BIN="/usr/sbin/wpa_supplicant"
WPA_SUP_PNAME="wpa_supplicant"
WPA_SUP_PIDFILE="/var/run/wpa_supplicant.$IFACE.pid"
WPA_COMMON_CTRL_IFACE="/var/run/wpa_supplicant"
WPA_SUP_OPTIONS="-B -P $WPA_SUP_PIDFILE -i $IFACE"

VERBOSITY=0


if [ -s "$IF_WPA_CONF" ]; then
	WPA_SUP_CONF="-c $IF_WPA_CONF"
else
	exit 0
fi

if [ ! -x "$WPA_SUP_BIN" ]; then
	
	if [ "$VERBOSITY" = "1" ]; then
		echo "$WPA_SUP_PNAME: binaries not executable or missing from $WPA_SUP_BIN"
	fi
	
	exit 1
fi

if [ "$MODE" = "start" ] ; then
	# driver type of interface, defaults to wext when undefined
	if [ -s "/etc/wpa_supplicant/driver.$IFACE" ]; then
		IF_WPA_DRIVER=$(cat "/etc/wpa_supplicant/driver.$IFACE")
	elif [ -z "$IF_WPA_DRIVER" ]; then
		
		if [ "$VERBOSITY" = "1" ]; then
			echo "$WPA_SUP_PNAME: wpa-driver not provided, using \"wext\""
		fi
		
		IF_WPA_DRIVER="wext"
	fi
	
	# if we have passed the criteria, start wpa_supplicant
	if [ -n "$WPA_SUP_CONF" ]; then
		
		if [ "$VERBOSITY" = "1" ]; then
			echo "$WPA_SUP_PNAME: $WPA_SUP_BIN $WPA_SUP_OPTIONS $WPA_SUP_CONF -D $IF_WPA_DRIVER"
		fi
		
		start-stop-daemon --start --quiet \
			--name $WPA_SUP_PNAME --startas $WPA_SUP_BIN --pidfile $WPA_SUP_PIDFILE \
			--  $WPA_SUP_OPTIONS $WPA_SUP_CONF -D $IF_WPA_DRIVER
	fi

	# if the interface socket exists, then wpa_supplicant was invoked successfully
	if [ -S "$WPA_COMMON_CTRL_IFACE/$IFACE" ]; then
	
		if [ "$VERBOSITY" = "1" ]; then
			echo "$WPA_SUP_PNAME: ctrl_interface socket located at $WPA_COMMON_CTRL_IFACE/$IFACE"
		fi

		exit 0
		
	fi
	
elif [ "$MODE" = "stop" ]; then

	if [ -f "$WPA_SUP_PIDFILE" ]; then
		
		if [ "$VERBOSITY" = "1" ]; then
			echo "$WPA_SUP_PNAME: terminating $WPA_SUP_PNAME daemon"
		fi
		
		start-stop-daemon --stop --quiet \
			--name $WPA_SUP_PNAME --pidfile	$WPA_SUP_PIDFILE
			
		if [ -S "$WPA_COMMON_CTRL_IFACE/$IFACE" ]; then
			rm -f $WPA_COMMON_CTRL_IFACE/$IFACE
		fi
			
		if [ -f "$WPA_SUP_PIDFILE" ]; then
			rm -f $WPA_SUP_PIDFILE
		fi
	fi

fi

exit 0
