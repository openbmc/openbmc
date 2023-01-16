#!/bin/sh -e

# shellcheck disable=SC3010

tty="$1"
uart=0

case "${tty}" in
 "ttyS0") uart=1
 ;;
 "ttyS1") uart=2
 ;;
 "ttyS2") uart=3
 ;;
 "ttyS3") uart=4
 ;;
 "ttyS7") uart=0
 ;;
 "ttyS8") uart=0
 ;;
 *) echo "Invalid tty passed to $0. Exiting!"
    exit 1;
 ;;
esac

# Default the host routing through the mux to use the BMC (2)
# This allows the SoL console in webui, and the ssh port 2200, to work
# upon startup. If UART transcievers are installed on the header and required,
# this value should be set to 1
if [[ ${uart} -ne 0 ]]
then
	/usr/sbin/ampere_uartmux_ctrl.sh ${uart} 2
fi

/usr/sbin/obmc-console-server --config /etc/obmc-console/server."${tty}".conf "${tty}"
