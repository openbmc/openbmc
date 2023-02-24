#!/bin/bash

string=''
pmbus_read() {
    data=$(i2cget -f -y $1 $2 $3 i $4)

    if [[ -z "$data" ]]; then
        echo "i2c$1 device $2 command $3 error" >&2
        exit 1
    fi

	arry=$(echo ${data} | sed -e "s/$4\: //" | sed -e "s/\0x00//g" | sed -e "s/\0xff//g" | sed -e "s/\0x7f//g" | sed -e "s/\0x0f//g" | sed -e "s/\0x14//g" | sed -e "s/\0xfe//g")

    string=''
    for d in ${arry}
    do
        hex=$(echo $d | sed -e "s/0\x//")
        string+=$(echo -e "\x${hex}");
    done
}

updaet_inventory() {
      INVENTORY_SERVICE='xyz.openbmc_project.Inventory.Manager'
      INVENTORY_OBJECT='/xyz/openbmc_project/inventory'
      INVENTORY_PATH='xyz.openbmc_project.Inventory.Manager'
      OBJECT_PATH="/system/chassis/motherboard/powersupply$1"
      busctl call \
          ${INVENTORY_SERVICE} \
          ${INVENTORY_OBJECT} \
          ${INVENTORY_PATH} \
          Notify a{oa{sa{sv}}} 1 \
          ${OBJECT_PATH} 1 $2 $3 \
          $4 $5 $6
}

if [ $# -eq 0 ]; then
    echo 'No PSU device is given' >&2
    exit 1
fi

input=$(echo $1 | tr "-" " ")
arr=(${input// / });


#pmbus_read ${arr[1]} ${arr[2]} 0x99 11
updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "Manufacturer" "s" "Nuvoton"

#pmbus_read ${arr[1]} ${arr[2]} 0x9a 11
updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "Model" "s" "Nuvoton"

if [ ${arr[0]} -eq 0 ]; then
    #pmbus_read ${arr[1]} ${arr[2]} 0xad 21
    updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "PartNumber" "s" "X9F7K2T6Y3P8J4R5"

    #pmbus_read ${arr[1]} ${arr[2]} 0x9e 16
    updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "SerialNumber" "s" "SN78594K3TQ2P7"
else
    #pmbus_read ${arr[1]} ${arr[2]} 0xad 21
    updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "PartNumber" "s" "M9T5C8W2N1Z7Q6B4"

    #pmbus_read ${arr[1]} ${arr[2]} 0x9e 16
    updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "SerialNumber" "s" "SN8461K3V9F2T7"
fi

updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Cacheable" 1 "Cached" "b" "true"
updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Decorator.Replaceable" 1 "FieldReplaceable" "b" "true"
updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Item" 1 "Present" "b" "true"
updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Item" 1 "PrettyName" "s" "powersupply${arr[0]}"
updaet_inventory ${arr[0]} "xyz.openbmc_project.Inventory.Item.PowerSupply" 0