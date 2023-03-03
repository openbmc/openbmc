#!/bin/bash

function usage()
{
    echo "Usage: $(basename "$0") init|reset"
}

if [ "$#" -ne "1" ] ; then
    usage
    exit 1;
fi

UART_ROUTING_SYSFS="/sys/bus/platform/drivers/aspeed-uart-routing/1e789098.uart-routing"

case "$1" in
    init)
        echo -n "uart1" > ${UART_ROUTING_SYSFS}/uart4
        echo -n "uart4" > ${UART_ROUTING_SYSFS}/uart1
    ;;
    reset)
        echo -n "io1"   > ${UART_ROUTING_SYSFS}/uart1
        echo -n "io4"   > ${UART_ROUTING_SYSFS}/uart4
    ;;
    *)
        usage
        exit 1;
    ;;
esac
