#!/bin/bash

string=''
pmbus_read() {
    data=$(i2cget -f -y "$1" "$2" "$3" i "$4")

    if [[ -z "$data" ]]; then
        echo "i2c$1 device $2 command $3 error" >&2
        exit 1
    fi

	arry=$(echo "${data}" | sed -e "s/$4\: //" | sed -e "s/\0x00//g" | sed -e "s/\0xff//g" | sed -e "s/\0x7f//g" | sed -e "s/\0x0f//g" | sed -e "s/\0x14//g")

    string=''
    for d in ${arry}
    do
        hex=${d/0x}
        string+=$(echo -e "\x${hex}");
    done
}

update_inventory() {
      INVENTORY_SERVICE='xyz.openbmc_project.Inventory.Manager'
      INVENTORY_OBJECT='/xyz/openbmc_project/inventory'
      INVENTORY_PATH='xyz.openbmc_project.Inventory.Manager'
      OBJECT_PATH="/system/chassis/motherboard/powersupply$1"
      busctl call \
          ${INVENTORY_SERVICE} \
          ${INVENTORY_OBJECT} \
          ${INVENTORY_PATH} \
          Notify "a{oa{sa{sv}}}" 1 \
          "${OBJECT_PATH}" 1 "$2" "$3" \
          "$4" "$5" "$6"
}

if [ $# -eq 0 ]; then
    echo 'No PSU device is given' >&2
    exit 1
fi

IFS=" " read -ra arr <<< "${1//- /}"

pmbus_read "${arr[1]}" "${arr[2]}" 0x99 11
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "Manufacturer" "s" "$string"

pmbus_read "${arr[1]}" "${arr[2]}" 0x9a 11
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "Model" "s" "$string"

pmbus_read "${arr[1]}" "${arr[2]}" 0xad 21
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "PartNumber" "s" "$string"

pmbus_read "${arr[1]}" "${arr[2]}" 0x9e 18
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Decorator.Asset" 1 "SerialNumber" "s" "$string"

update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Decorator.Cacheable" 1 "Cached" "b" "true"
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Decorator.Replaceable" 1 "FieldReplaceable" "b" "true"
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Item" 1 "Present" "b" "true"
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Item" 1 "PrettyName" "s" "powersupply${arr[0]}"
update_inventory "${arr[0]}" "xyz.openbmc_project.Inventory.Item.PowerSupply" 0
