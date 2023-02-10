#!/bin/sh

if [ "$1" = "udc0" ]; then
  function=$(cat /sys/class/udc/80401000.udc/function)
  if [ "func-$function" != "func-" ]; then
    echo "UDC0 owner is changed"
    echo disconnect > /sys/class/udc/80401000.udc/soft_connect
    sleep 3
    echo connect > /sys/class/udc/80401000.udc/soft_connect
  else
    echo "UDC0 is not attached"
  fi
else
 if [ "$1" = "udc1" ]; then
  function=$(cat /sys/class/udc/80402000.udc/function)
  if [ "func-$function" != "func-" ]; then
    echo "UDC1 owner is changed"
    echo disconnect > /sys/class/udc/80402000.udc/soft_connect
    sleep 3
    echo connect > /sys/class/udc/80402000.udc/soft_connect
  else
    echo "UDC1 is not attached"
  fi
 else
  if [ "$1" = "udc2" ]; then
  function=$(cat /sys/class/udc/80403000.udc/function)
   if [ "func-$function" != "func-" ]; then
    echo "UDC2 owner is changed"
    echo disconnect > /sys/class/udc/80403000.udc/soft_connect
    sleep 3
    echo connect > /sys/class/udc/80403000.udc/soft_connect
   else
    echo "UDC2 is not attached"
   fi
  fi
 fi
fi
