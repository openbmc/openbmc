#!/bin/sh -e
#Control UART mux for obmc-console-client
echo 0 > /sys/class/gpio/gpio325/value
obmc-console-client
echo 1 > /sys/class/gpio/gpio325/value


