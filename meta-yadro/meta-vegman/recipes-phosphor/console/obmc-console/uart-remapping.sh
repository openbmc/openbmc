#!/bin/bash -eu

function usage()
{
    echo "Usage: $(basename "$0") init|reset"
}

if [ "$#" -ne "1" ] ; then
    usage
    exit 1;
fi

cmd="${1}"
UART_ROUTING_PATH="/sys/bus/platform/drivers/aspeed-uart-routing/1e78909c.uart-routing"

case "${cmd}" in
    init)
        echo -n "uart3" > ${UART_ROUTING_PATH}/uart1
        echo -n "uart1" > ${UART_ROUTING_PATH}/uart3
        echo -n "io1"   > ${UART_ROUTING_PATH}/uart4
        echo -n "uart4" > ${UART_ROUTING_PATH}/io1
    ;;
    reset)
        echo -n "io1"   > ${UART_ROUTING_PATH}/uart1
        echo -n "io3"   > ${UART_ROUTING_PATH}/uart3
        echo -n "io4"   > ${UART_ROUTING_PATH}/uart4
        echo -n "uart1" > ${UART_ROUTING_PATH}/io1
    ;;
    *)
        usage
        exit 1;
    ;;
esac
