#!/bin/bash

registry=$1
msgarg=$2

if [ -z "$registry" ]; then
	echo "Usage:"
	echo "     $0 <redfish registry> <argument>"
	exit
fi

# Check if logger-systemd exist. Do nothing if not exists
if ! command -v logger-systemd;
then
	echo "logger-systemd does not exist. Skip log events for $registry $msgarg"
	exit
fi

# Log events
logger-systemd --journald << EOF
MESSAGE=
PRIORITY=
SEVERITY=
REDFISH_MESSAGE_ID=$registry
REDFISH_MESSAGE_ARGS=$msgarg
EOF
