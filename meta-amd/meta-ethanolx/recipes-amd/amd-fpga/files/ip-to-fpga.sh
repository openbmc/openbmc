#!/bin/bash

OLD_IP=""

while true
do
  IP=$(ip a | awk '/inet.*global/ {split ($2,A,"/"); print A[1]}')

  if [ "${IP}" != "${OLD_IP}" ]
  then
    if [ -n "${IP}" ]
    then
      IP_1=$(echo "${IP}" | cut -d "." -f 1)
      IP_2=$(echo "${IP}" | cut -d "." -f 2)
      IP_3=$(echo "${IP}" | cut -d "." -f 3)
      IP_4=$(echo "${IP}" | cut -d "." -f 4)
    else
      IP_1=0
      IP_2=0
      IP_3=0
      IP_4=0
    fi

    echo "Transfer current IP address (${IP_1}.${IP_2}.${IP_3}.${IP_4}) to the FPGA"

    i2cset -y 2 0x50 0 "${IP_1}"
    i2cset -y 2 0x50 1 "${IP_2}"
    i2cset -y 2 0x50 2 "${IP_3}"
    i2cset -y 2 0x50 3 "${IP_4}"
    OLD_IP=${IP}
  fi
  sleep 5
done
