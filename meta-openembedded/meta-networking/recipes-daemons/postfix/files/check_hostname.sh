#! /bin/sh

HOSTNAME=$(/bin/hostname)

if [ -z "$HOSTNAME" -o "$HOSTNAME" = "(none)" -o ! -z "`echo $HOSTNAME | sed -n '/^[0-9]*\.[0-9].*/p'`" ]; then
	# If hostname is invalid, and myhostname not existed in main.cf
	/usr/sbin/postconf -h "myhostname" 2>/dev/null
	if [ $? -ne 0 ]; then
		# Set "localhost" to main.cf
		/usr/sbin/postconf -e "myhostname=localhost"
	fi
fi

