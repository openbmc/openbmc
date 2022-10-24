#!/bin/bash
#
# shellcheck disable=SC2046

if [ $# -lt 2 ]; then
	exit 1
fi

case "$1" in
	1) GPIO_UARTx_MODE0="uart1-mode0"
		GPIO_UARTx_MODE1="uart1-mode1"
		# CPU0 UART0 connects to BMC UART1
		CONSOLE_PORT=0
	;;
	*) echo "Invalid UART port selection"
		exit 1
	;;
esac

# Only switch the MUX when there is no active connection. This means we only
# switch the MUX before the first session starts and after the last session
# closes. We do this by querying number of connected sessions to the socket
# of requested console port.
# Example format:  Accepted: 1; Connected: 1;
CONNECTED=$(systemctl --no-pager status obmc-console-ttyS${CONSOLE_PORT}-ssh.socket | grep -w Connected | cut -d ':' -f 3 | tr -d ' ;')
if [ ! "$CONNECTED" -le 1 ]; then
	echo "Please close all connected session to ttyS${CONSOLE_PORT} !"
	exit 0
fi

echo "Ampere UART MUX CTRL UART port $1 to mode $2"

case "$2" in
	# To HDR
	1) gpioset $(gpiofind "$GPIO_UARTx_MODE0")=1
		gpioset $(gpiofind "$GPIO_UARTx_MODE1")=0
		exit 0
	;;
	# To BMC
	2) gpioset $(gpiofind "$GPIO_UARTx_MODE0")=0
		gpioset $(gpiofind "$GPIO_UARTx_MODE1")=1
		exit 0
	;;
	*) echo "Invalid UART mode selection"
		exit 1
	;;
esac
