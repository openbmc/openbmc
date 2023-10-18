#!/bin/bash
#
# Ampere Computing LLC: UART MUX/DEMUX for CPU0 UART0,1,4 and CPU1 UART1
# Usage: ampere_uartmux_ctrl.sh <CPU UART port number> <UARTx_MODE>
#        <UARTx_MODE> of 1 sets CPU To HDR_CONN
#        <UARTx_MODE> of 2 sets BMC to CPU (eg dropbear ssh server on port 2200)
#
# This can be called to set uart mux manually

# shellcheck disable=SC2046

if [ $# -lt 2 ]; then
	exit 1
fi

case "$1" in
	1) GPIO_UARTx_MODE0="uart1-mode0"
		GPIO_UARTx_MODE1="uart1-mode1"
	;;
	2) GPIO_UARTx_MODE0="uart2-mode0"
		GPIO_UARTx_MODE1="uart2-mode1"
	;;
	3) GPIO_UARTx_MODE0="uart3-mode0"
		GPIO_UARTx_MODE1="uart3-mode1"
	;;
	4) GPIO_UARTx_MODE0="uart4-mode0"
		GPIO_UARTx_MODE1="uart4-mode1"
	;;
	*) echo "Invalid UART port selection"
		exit 1
	;;
esac

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
