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

# Ampere Computing LLC mtjade: UART MUX/DEMUX for CPU0 UART0,1,4 and CPU1 UART1
# Usage: ampere_uartmux_ctrl.sh <CPU UART port number> <UARTx_MODE>
#        <UARTx_MODE> of 1 sets CPU To BSP
#        <UARTx_MODE> of 2 sets SCP1 to SI2

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

if [ $# -lt 1 ]; then
  exit 1
fi

echo "Ampere UART MUX CTRL UART port $1 to mode" > /dev/ttyS0

case "$1" in
  ttyS1)
    set_gpio_ctrl 167 out 1
    ;;
  ttyS3)
    set_gpio_ctrl 161 out 1
    set_gpio_ctrl 183 out 1 
    set_gpio_ctrl 198 out 0
    ;;
  *)
    echo "Invalid tty passed to $0. Exiting!" > /dev/ttyS0
    ;;
esac
