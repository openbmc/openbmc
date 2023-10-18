#!/bin/sh -e
#
# Platform can add tty ports for console server to OBMC_CONSOLE_TTYS in their
# bbappend. The target tty devices exposed by kernel will then start their
# corresponding obmc-console-server instance and the
# obmc-console@<tty-port>.service will execute this script to direct the uart
# port of the <tty-port> to BMC

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
 "ttyS7")
   # Socket 1 UART 1 connects directly to BMC UART 8
   echo "Ampere UART MUX CTRL UART port 8 to mode 2"
   exit 0
 ;;
 "ttyS8")
   # Socket 1 UART 4 connects directly to BMC UART 9
   echo "Ampere UART MUX CTRL UART port 9 to mode 2"
   exit 0
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
