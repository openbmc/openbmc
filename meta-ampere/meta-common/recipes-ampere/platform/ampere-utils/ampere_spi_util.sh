#!/bin/bash

# shellcheck disable=SC2046

spi_address="1e630000.spi"
spi_bind="/sys/bus/platform/drivers/spi-aspeed-smc/bind"
spi_unbind="/sys/bus/platform/drivers/spi-aspeed-smc/unbind"
spi_lock="/run/platform/spi.lock"
spi_lock_dir="/run/platform"

bind_aspeed_smc_driver() {
	if [ -f "${spi_lock}" ]; then
		pid=$1
		pid_lock=$(cat "${spi_lock}")
		if [[ "${pid}" != "${pid_lock}" ]]; then
			echo "SPI-NOR resoure is lock by process $pid_lock"
			return 1
		fi
	fi

	# BMC access SPI-NOR resource
	gpioset $(gpiofind spi0-program-sel)=1
	sleep 0.1
	echo "Bind the ASpeed SMC driver"
	echo "${spi_address}" > "${spi_bind}"  2>/dev/null
	# Check the HNOR partition available
	HOST_MTD=$(< /proc/mtd grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
	if [ -z "$HOST_MTD" ]; then
		echo "${spi_address}" > "${spi_unbind}"
		sleep 0.1
		echo "${spi_address}" > "${spi_bind}"
	fi
	# BMC release SPI-NOR resource
	gpioset $(gpiofind spi0-program-sel)=0
	return 0
}

unbind_aspeed_smc_driver() {
	if [ -f "${spi_lock}" ]; then
		pid=$1
		pid_lock=$(cat "${spi_lock}")
		if [[ "${pid}" != "${pid_lock}" ]]; then
			echo "SPI-NOR resoure is lock by process $pid_lock . Wait 10s"
			# Wait maximum 10 seconds for unlock SPI-NOR
			cnt=10
			while [ $cnt -gt 0 ]
			do
				if [ -f "${spi_lock}" ]; then
					sleep 1
					cnt=$((cnt - 1))
				else
					break
				fi
			done
			if [ "$cnt" -eq "0" ]; then
				echo "Timeout 10 seconds, SPI-NOR still busy. Force unlock to access SPI"
				rm -f "${spi_lock}"
			fi
		fi
	fi

	HOST_MTD=$(< /proc/mtd grep "pnor" | sed -n 's/^\(.*\):.*/\1/p')
	if [ -n "$HOST_MTD" ]; then
		# If the HNOR partition is available, then unbind driver
		# BMC access SPI-NOR resource
		gpioset $(gpiofind spi0-program-sel)=1
		sleep 0.1
		echo "Unbind the ASpeed SMC driver"
		echo "${spi_address}" > "${spi_unbind}"
	fi
	# BMC release SPI-NOR resource
	gpioset $(gpiofind spi0-program-sel)=0
	# Deassert BMC access SPI-NOR pin
	gpioset $(gpiofind spi-nor-access)=0
	sleep 0.5
	return 0
}

lock_spi_resource() {
	# Wait maximum 10 seconds to lock SPI-NOR
	cnt=10
	while [ $cnt -gt 0 ]
	do
		if [ -f "${spi_lock}" ]; then
			sleep 1
			cnt=$((cnt - 1))
		else
			echo "$1" > "${spi_lock}"
			break
		fi
	done

	if [ "$cnt" -eq "0" ]; then
		echo "Timeout 10 seconds, SPI-NOR is still locked by another process"
		return 1
	fi
	return 0
}

unlock_spi_resource() {
	if [ ! -f "${spi_lock}" ]; then
		echo "SPI-NOR is already unlocked"
		return 0
	fi

	pid=$1
	pid_lock=$(cat "${spi_lock}")
	if [[ "${pid}" == "${pid_lock}" ]]; then
		rm -f "${spi_lock}"
	else
		echo "Cannot unlock, SPI-NOR is locked by another process"
		return 1
	fi
	return 0
}

start_handshake_spi() {
	if [ -f "${spi_lock}" ]; then
		pid=$1
		pid_lock=$(cat "${spi_lock}")
		if [[ "${pid}" != "${pid_lock}" ]]; then
			echo "SPI-NOR resoure is lock by process $pid_lock"
			return 1
		fi
	fi

	# Wait maximum 10 seconds to grant access SPI
	cnt=10
	while [ $cnt -gt 0 ]
	do
		spinor_access=$(gpioget $(gpiofind soc-spi-nor-access))
		if [ "$spinor_access" == "1" ]; then
			sleep 1
			cnt=$((cnt - 1))
		else
			break
		fi
	done

	if [ "$cnt" -eq "0" ]; then
		echo "Timeout 10 seconds, host is still hold SPI-NOR."
		return 1
	fi
	echo "Start handshake SPI-NOR"
	# Grant BMC access SPI-NOR. The process call the scripts should only
	# claim the bus for only maximum period 500ms.
	gpioset $(gpiofind spi-nor-access)=1
	# Switch the Host SPI-NOR to BMC
	gpioset $(gpiofind spi0-program-sel)=1
}

stop_handshake_spi() {
	if [ -f "${spi_lock}" ]; then
		pid=$1
		pid_lock=$(cat "${spi_lock}")
		if [[ "${pid}" != "${pid_lock}" ]]; then
			echo "SPI-NOR resoure is lock by process $pid_lock"
			return 1
		fi
	fi
	echo "Stop handshake SPI-NOR"
	# Switch the Host SPI-NOR to HOST
	gpioset $(gpiofind spi0-program-sel)=0
	# Deassert BMC access SPI-NOR pin
	gpioset $(gpiofind spi-nor-access)=0
}


if [ $# -eq 0 ]; then
	echo "Usage:"
	echo "  - Handshake access SPI-NOR "
	echo "     $(basename "$0") cmd pid"
	echo "    <cmd>:"
	echo "        lock   - lock the SPI-NOR resource"
	echo "        unlock - unlock the SPI-NOR resource"
	echo "        bind   - bind the SPI-NOR resource"
	echo "        unbind - unbind the SPI-NOR resource"
	echo "        start_handshake - start handshake between BMC and Host"
	echo "        stop_handshake - release handshake between BMC and Host"
	echo "    <pid>: Optional - PID of the process call script"
	exit 0
fi

CMD=$1

if [ ! -d "${spi_lock_dir}" ]; then
	mkdir -p "${spi_lock_dir}"
fi

if [ -z "$2" ]; then
	PID=$$
else
	PID=$2
fi

if [[ "${CMD}" == "lock" ]]; then
	lock_spi_resource "${PID}"
	ret=$?
	if [[ "${ret}" == "1" ]]; then
		echo "Cannot lock SPI-NOR, the resource is busy"
		exit 1
	fi
elif [[ "${CMD}" == "unlock" ]]; then
	unlock_spi_resource "${PID}"
	ret=$?
	if [[ "${ret}" == "1" ]]; then
		echo "Cannot unlock SPI-NOR, the resource is busy"
		exit 1
	fi
elif [[ "${CMD}" == "bind" ]]; then
	bind_aspeed_smc_driver "${PID}"
	ret=$?
	if [[ "${ret}" == "1" ]]; then
		echo "Cannot bind SPI-NOR, the resource is busy"
		exit 1
	fi
elif [[ "${CMD}" == "unbind" ]]; then
	unbind_aspeed_smc_driver "${PID}"
	ret=$?
	if [[ "${ret}" == "1" ]]; then
		echo "Cannot unbind SPI-NOR, the resource is busy"
		exit 1
	fi
elif [[ "${CMD}" == "start_handshake" ]]; then
	start_handshake_spi "${PID}"
	ret=$?
	if [[ "${ret}" == "1" ]]; then
		echo "Cannot start handshake SPI-NOR"
		exit 1
	fi
elif [[ "${CMD}" == "stop_handshake" ]]; then
	stop_handshake_spi "${PID}"
	ret=$?
	if [[ "${ret}" == "1" ]]; then
		echo "Cannot stop handshake SPI-NOR"
		exit 1
	fi
fi

exit 0
