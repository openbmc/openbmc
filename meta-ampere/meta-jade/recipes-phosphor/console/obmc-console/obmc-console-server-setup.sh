#!/bin/sh -e
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

set -euo pipefail

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
 *) echo "Invalid tty passed to $0. Exiting!"
    exit 1;
 ;;
esac

# Default the host routing through the mux to use the BMC (2)
# This allows the SoL console in webui, and the ssh port 2200, to work
# upon startup. If UART transcievers are installed on the header and required,
# this value should be set to 1
/usr/sbin/ampere_uartmux_ctrl.sh ${uart} 2

/usr/sbin/obmc-console-server --config /etc/obmc-console/server.${tty}.conf ${tty}
