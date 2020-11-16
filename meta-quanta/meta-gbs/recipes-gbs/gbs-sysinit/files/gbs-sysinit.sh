#!/bin/bash
# Copyright 2020 Google LLC
# Copyright 2020 Quanta Computer Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


source /usr/libexec/gbs-gpio-common.sh

WD1RCR_ADDR=0xf080103c
CORSTC_ADDR=0xf080105c
BOARD_VER="" # Set by check_board_ver
pe_eeprom_addr=( 50 54 )

SERVICE_NAME="xyz.openbmc_project.Inventory.Manager"
INTERFACE_NAME="xyz.openbmc_project.Inventory.Item"

PE_PRESENT_OBJPATH=("/xyz/openbmc_project/inventory/system/chassis/entity/pe_slot0_prsnt"
"/xyz/openbmc_project/inventory/system/chassis/entity/pe_slot1_prsnt")
SATA0_PRESENT_OBJPATH="/xyz/openbmc_project/inventory/system/chassis/entity/sata0_prsnt"

set_gpio_persistence() {
  reg_val=$(devmem ${WD1RCR_ADDR} 32)
  # Clear bit 16-23 to perserve all GPIO states across warm resets
  reg_val=$(printf "0x%08x" $((reg_val & ~0xff0000)))
  echo "Setting WD1RCR_ADDR to ${reg_val}"
  devmem "${WD1RCR_ADDR}" 32 "${reg_val}"

  reg_val=$(devmem ${CORSTC_ADDR} 32)
  # Clear bit 16-23 of CORSTC
  reg_val=$(printf "0x%08x" $((reg_val & ~0xff0000)))
  echo "Setting CORSTC_ADDR to ${reg_val}"
  devmem "${CORSTC_ADDR}" 32 "${reg_val}"
}

get_board_rev_id() {
    echo $(get_gpio_value 'BMC_BRD_REV_ID7')\
    $(get_gpio_value 'BMC_BRD_REV_ID6')\
    $(get_gpio_value 'BMC_BRD_REV_ID5')\
    $(get_gpio_value 'BMC_BRD_REV_ID4')\
    $(get_gpio_value 'BMC_BRD_REV_ID3')\
    $(get_gpio_value 'BMC_BRD_REV_ID2')\
    $(get_gpio_value 'BMC_BRD_REV_ID1')\
    $(get_gpio_value 'BMC_BRD_REV_ID0')\
    | sed 's/ //g' > ~/board_rev_id.txt
}

get_board_sku_id() {
    echo $(get_gpio_value 'BMC_BRD_SKU_ID3')\
    $(get_gpio_value 'BMC_BRD_SKU_ID2')\
    $(get_gpio_value 'BMC_BRD_SKU_ID1')\
    $(get_gpio_value 'BMC_BRD_SKU_ID0')\
    | sed 's/ //g' > ~/board_sku_id.txt
}

get_hsbp_board_rev_id() {
    echo $(get_gpio_value 'HSBP_BRD_REV_ID3')\
    $(get_gpio_value 'HSBP_BRD_REV_ID2')\
    $(get_gpio_value 'HSBP_BRD_REV_ID1')\
    $(get_gpio_value 'HSBP_BRD_REV_ID0')\
    | sed 's/ //g' > ~/hsbp_board_rev_id.txt
}

get_fan_board_rev_id() {
    echo $(get_gpio_value 'FAN_BRD_REV_ID1')\
    $(get_gpio_value 'FAN_BRD_REV_ID0')\
    | sed 's/ //g' > ~/fan_board_rev_id.txt
}

check_board_ver() {
  # Sets BOARD_VER to either "PREPVT" or "PVT"
  #
  # BOARD_REV_ID[7:6] =
  # 0x00 - EVT
  # 0x01 - DVT
  # 0x10 - PVT
  # 0x11 - MP

  rev7_val=$(get_gpio_value 'BMC_BRD_REV_ID7')
  if (( rev7_val == 0 )); then
    echo "EVT/DVT rev!"
    BOARD_VER="PREPVT"
  else
    echo "PVT/MP rev!"
    BOARD_VER="PVT"
  fi
}

check_board_sku() {
  sku1_val=$(get_gpio_value 'BMC_BRD_SKU_ID1')
  if (( sku1_val == 1 )); then
    echo "GBS SKU!"
    BOARD_SKU="GBS"
  else
    echo "Other SKU!"
    BOARD_SKU="TBD"
  fi
}

set_uart_en_low() {
  # GPIO76 UART_EN polarity inverted between DVT/PVT
  # Pin direction was set high in the kernel.
  set_gpio_direction 'FM_BMC_CPU_UART_EN' low
}

set_hdd_prsnt() {
  # On PVT need to forward SATA0_PRSNT_N to HDD_PRSNT_N
  # The signal is safe to set on DVT boards so just set universally.
  mapper wait ${SATA0_PRESENT_OBJPATH}
  sata_prsnt_n="$(busctl get-property $SERVICE_NAME ${SATA0_PRESENT_OBJPATH} \
                 $INTERFACE_NAME Present)"

  # sata_prsnt_n is active low => value "true" means low
  if [[ ${sata_prsnt_n} == "b true" ]]; then
    set_gpio_direction 'HDD_PRSNT_N' low
  else
    set_gpio_direction 'HDD_PRSNT_N' high
  fi
}

KERNEL_FIU_ID="c0000000.fiu"
KERNEL_SYSFS_FIU="/sys/bus/platform/drivers/NPCM-FIU"

bind_host_mtd() {
  set_gpio_direction 'SPI_SW_SELECT' high
  if [[ -d ${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID} ]]; then
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
  fi
  echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/bind
}

unbind_host_mtd() {
  if [[ -d ${KERNEL_SYSFS_FIU}/${KERNEL_FIU_ID} ]]; then
    echo "${KERNEL_FIU_ID}" > "${KERNEL_SYSFS_FIU}"/unbind
  fi
  set_gpio_direction 'SPI_SW_SELECT' low
}
trap unbind_host_mtd EXIT SIGHUP SIGINT SIGTERM

# Taken from /run/initramfs/update
# Given label name, return mtd node. e.g. `findmtd bmc` returns 'mtd0'
findmtd() {
  m=$(grep -xl "$1" /sys/class/mtd/*/name)
  m=${m%/name}
  m=${m##*/}
  echo $m
}

verify_host_bios() {
  echo "BIOS verification start!"

  # placeholder for verifying host BIOS. For now time BIOS read
  # with dd
  bind_host_mtd || { echo "Failed to bind FIU driver for host MTD"; return 1; }

  pnor_mtd=$(findmtd pnor)
  [[ -z "${pnor_mtd}" ]] && { echo "Failed to find host MTD  partition!"; return 1; }

  # Test timing by computing SHA256SUM.
  sha256sum /dev/${pnor_mtd}ro

  echo "BIOS verification complete!"
  unbind_host_mtd
}

reset_phy() {
  ifconfig eth1 down
  set_gpio_direction 'RST_BMC_PHY_N' low
  set_gpio_direction 'RST_BMC_PHY_N' high
  ifconfig eth1 up
}

parse_pe_fru() {
  pe_fruid=3
  for i in {1..2};
  do
     mapper wait ${PE_PRESENT_OBJPATH[$(($i-1))]}
     pe_prsnt_n="$(busctl get-property $SERVICE_NAME ${PE_PRESENT_OBJPATH[$(($i-1))]} \
                  $INTERFACE_NAME Present)"

     if [[ ${pe_prsnt_n} == "b false" ]]; then
         pe_fruid=$(($pe_fruid+1))
         continue
     fi

     # Output is the i2c bus number for the PCIE cards on PE0/PE1
     # i2c-0 -> i2c mux (addr: 0x71) -> PE0/PE1
     # PE0: channel 0
     # PE1: channel 1
     pe_fru_bus="$(ls -al /sys/bus/i2c/drivers/pca954x/0-0071/ | grep channel \
                   | awk -F "/" '{print $(NF)}' | awk -F "-" '{print $2}' | sed -n "${i}p")"

     # If the PE FRU EEPROM syspath does not exist, create it ("24c02" is the
     # EEPROM part number) and perform a phosphor-read-eeprom
     for ((j=0; j < ${#pe_eeprom_addr[@]}; j++));
     do
        i2cget -f -y $pe_fru_bus "0x${pe_eeprom_addr[$j]}" 0x01 > /dev/null 2>&1
        if [ $? -eq 0 ]; then
          if [ ! -f "/sys/bus/i2c/devices/$pe_fru_bus-00${pe_eeprom_addr[$j]}/eeprom" ]; then
            echo 24c02 "0x${pe_eeprom_addr[$j]}" > "/sys/bus/i2c/devices/i2c-$pe_fru_bus/new_device"
          fi
          pe_fru_bus="/sys/bus/i2c/devices/$pe_fru_bus-00${pe_eeprom_addr[$j]}/eeprom"
          phosphor-read-eeprom --eeprom $pe_fru_bus --fruid $pe_fruid
          break
        fi
     done
     pe_fruid=$(($pe_fruid+1))
  done
}

check_power_status() {
    res0="$(busctl get-property -j xyz.openbmc_project.State.Chassis \
        /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis \
        CurrentPowerState | jq -r '.["data"]')"
    echo $res0
}

main() {
  get_board_rev_id
  get_board_sku_id
  get_hsbp_board_rev_id
  get_fan_board_rev_id

  check_board_ver
  if [[ "${BOARD_VER}" == "PREPVT" ]]; then
    set_uart_en_low
  fi

  check_board_sku

  reset_phy

  if [[ $(check_power_status) != \
       'xyz.openbmc_project.State.Chassis.PowerState.On' ]]; then
    verify_host_bios

    echo "Release host from reset!" >&2
    set_gpio_direction 'RST_BMC_RSMRST_N' high
    set_gpio_direction 'RST_KBRST_BMC_CPLD_N' high
    # TODO: remove the hack once kernel driver is ready
    # Set the GPIO states to preserve across reboots
    set_gpio_persistence

    echo "Starting host power!" >&2
    busctl set-property xyz.openbmc_project.State.Host \
        /xyz/openbmc_project/state/host0 \
        xyz.openbmc_project.State.Host \
        RequestedHostTransition s \
        xyz.openbmc_project.State.Host.Transition.On
  else
    echo "Host is already running, doing nothing!" >&2
  fi

  set_hdd_prsnt
  parse_pe_fru
}

# Exit without running main() if sourced
return 0 2>/dev/null

main "$@"
