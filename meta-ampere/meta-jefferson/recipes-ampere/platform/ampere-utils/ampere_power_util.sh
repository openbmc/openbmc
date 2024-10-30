#!/bin/bash

# shellcheck disable=SC2046

# Usage of this utility
function usage() {
	echo "usage: ampere_power_util mb [shutdown_ack|soft_off|force_reset]"
}

shutdown_ack() {
	if [ -f "/run/openbmc/host@0-softpoweroff" ]; then
		echo "Receive shutdown ACK triggered after softportoff the host."
		touch /run/openbmc/host@0-softpoweroff-shutdown-ack
	else
		echo "Receive shutdown ACK triggered"
		sleep 3
		systemctl start obmc-chassis-poweroff@0.target
	fi
}

soft_off() {
	# Trigger shutdown_req
	touch /run/openbmc/host@0-softpoweroff
	gpioset $(gpiofind host0-shd-req-n)=0
	sleep 0.05
	gpioset $(gpiofind host0-shd-req-n)=1

	# Wait for shutdown_ack from the host in 30 seconds
	cnt=30
	while [ $cnt -gt 0 ];
	do
		# Wait for SHUTDOWN_ACK and create the host@0-softpoweroff-shutdown-ack
		if [ -f "/run/openbmc/host@0-softpoweroff-shutdown-ack" ]; then
			break
		fi
		sleep 1
		cnt=$((cnt - 1))
	done

	# Softpoweroff is successful
	sleep 2
	rm -rf /run/openbmc/host@0-softpoweroff
	if [ -f "/run/openbmc/host@0-softpoweroff-shutdown-ack" ]; then
		rm -rf /run/openbmc/host@0-softpoweroff-shutdown-ack
	fi
	echo 0
}

force_reset() {
	if [ -f "/run/openbmc/host@0-softpoweroff" ]; then
		# In graceful host reset, after trigger os shutdown,
		# the phosphor-state-manager will call force-warm-reset
		# in this case the force_reset should wait for shutdown_ack from host
		cnt=30
		while [ $cnt -gt 0 ];
		do
			if [ -f "/run/openbmc/host@0-softpoweroff-shutdown-ack" ]; then
				break
			fi
			echo "Waiting for shutdown-ack count down $cnt"
			sleep 1
			cnt=$((cnt - 1))
		done
		# The host OS is failed to shutdown
		if [ $cnt == 0 ]; then
			echo "Shutdown-ack time out after 30s."
			exit 0
		fi
	fi
	rm -f /run/openbmc/host@0-on
	echo "Triggering sysreset pin"
	gpioset $(gpiofind host0-sysreset-n)=0
	sleep 1
	gpioset $(gpiofind host0-sysreset-n)=1
}

mkdir -p "/run/openbmc/"

if [ "$2" == "shutdown_ack" ]; then
	shutdown_ack
elif [ "$2" == "force_reset" ]; then
	force_reset
elif [ "$2" == "soft_off" ]; then
	ret=$(soft_off)
	if [ "$ret" == 0 ]; then
		echo "The host is already softoff"
	else
		echo "Failed to softoff the host"
	fi
	exit "$ret";
else
	echo "Invalid parameter2=$2"
	usage;
fi

exit 0;
