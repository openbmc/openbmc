#!/bin/sh -e

# shellcheck disable=SC2039
# shellcheck disable=SC2112
# shellcheck disable=SC3010
# shellcheck disable=SC3030
# shellcheck disable=SC3054

export obmc_console_tty=("ttyS0" "ttyS1" "ttyS2" "ttyS3" "ttyS7" "ttyS8")

function get_uart_port()
{
   tty=$1
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
   echo $uart
}

function uart_console_setup()
{
   # Default the host routing through the mux to use the BMC (2)
   # This allows the SoL console in webui, and the ssh port 2200, to work
   # upon startup. If UART transcievers are installed on the header and required,
   # this value should be set to 1
   for tty in "${obmc_console_tty[@]}"; do
      uart=$(get_uart_port "$tty")
      if [ "${uart}" -ne 0 ]
      then
         /usr/sbin/ampere_uartmux_ctrl.sh "${uart}" 2
      fi
   done
}
