#!/bin/bash
#
# Copyright (c) 2021 Ampere Computing LLC
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

# shellcheck source=meta-ampere/meta-jade/recipes-ampere/platform/ampere-utils/gpio-lib.sh
source /usr/sbin/gpio-lib.sh

if [ $# -lt 2 ]; then
	exit 1
fi

case "$1" in
	1) GPIO_UARTx_MODE0=56
	;;
	2) GPIO_UARTx_MODE0=57
	;;
	3) GPIO_UARTx_MODE0=58
	;;
	4) GPIO_UARTx_MODE0=59
	;;
	*) echo "Invalid UART port selection"
	   exit 1
	;;
esac

echo "Ampere UART MUX CTRL UART port $1 to mode $2"

case "$2" in
	1) gpio_configure_output "${GPIO_UARTx_MODE0}" 0
	   exit 0
	;;
	2) gpio_configure_output "${GPIO_UARTx_MODE0}" 1
	   exit 0
	;;
	*) echo "Invalid UART mode selection"
	   exit 1
	;;
esac
