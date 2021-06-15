#!/bin/bash
#
# Copyright (c) 2020 Ampere Computing LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Ampere Computing LLC: UART MUX/DEMUX for CPU0 UART0,1,4 and CPU1 UART1
# Usage: ampere_uartmux_ctrl.sh <CPU UART port number> <UARTx_MODE>
#        <UARTx_MODE> of 1 sets CPU To HDR_CONN
#        <UARTx_MODE> of 2 sets BMC to CPU (eg dropbear ssh server on port 2200)

if [ $# -lt 2 ]; then
	exit 1
fi

function set_gpio_active_low() {
  if [ $# -ne 2 ]; then
    echo "set_gpio_active_low: need both GPIO# and initial level";
    return;
  fi

  if [ ! -d /sys/class/gpio/gpio$1 ]; then
    echo $1 > /sys/class/gpio/export
  fi
  echo $2 > /sys/class/gpio/gpio$1/direction
}

GPIO_BASE=$(cat /sys/class/gpio/gpio*/base)

case "$1" in
	1) GPIO_UARTx_MODE0=56
	   # CPU0 UART0 connects to BMC UART1
	   CONSOLE_PORT=0
	;;
	2) GPIO_UARTx_MODE0=57
	   # CPU0 UART1 connects to BMC UART2
	   CONSOLE_PORT=1
	;;
	3) GPIO_UARTx_MODE0=58
	   # CPU0 UART4 connects to BMC UART3
	   CONSOLE_PORT=2
	;;
	4) GPIO_UARTx_MODE0=59
	   # CPU1 UART1 connects to BMC UART4
	   CONSOLE_PORT=3
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
if [ ! $CONNECTED -le 1 ]; then
	exit 0
fi

echo "Ampere UART MUX CTRL UART port $1 to mode $2"

case "$2" in
	1) set_gpio_active_low $((${GPIO_BASE} + ${GPIO_UARTx_MODE0})) low
	   exit 0
	;;
	2) set_gpio_active_low $((${GPIO_BASE} + ${GPIO_UARTx_MODE0})) high
	   exit 0
	;;
	*) echo "Invalid UART mode selection"
	   exit 1
	;;
esac
