#!/bin/bash

# Disable check for splitting
# shellcheck disable=SC2207

BMC_CPLD_VER_FILE="/run/cpld0.version"
MB_CPLD_VER_FILE="/run/cpld1.version"
ver=''

function fw_rev() {
    case $1 in
    cpldb)
        rsp=($(i2cget -y -f -a "${I2C_BMC_CPLD[0]}" 0x"${I2C_BMC_CPLD[1]}" 0x00 i 5))
        ver=$(printf '%d.%d.%d.%d' "${rsp[5]}" "${rsp[4]}" "${rsp[3]}" "${rsp[2]}")
        ;;
    cpldm)
        rsp=($(i2cget -y -f -a "${I2C_MB_CPLD[0]}" 0x"${I2C_MB_CPLD[1]}" 0x00 i 5))
        ver=$(printf '%d.%d.%d.%d' "${rsp[5]}" "${rsp[4]}" "${rsp[3]}" "${rsp[2]}")
        ;;
    *)
        ;;
    esac
}

fw_rev cpldb
echo "BMC CPLD version : ${ver}"
echo "${ver}" > "${BMC_CPLD_VER_FILE}"
fw_rev cpldm
echo "MB CPLD version  : ${ver}"
echo "${ver}" > "${MB_CPLD_VER_FILE}"
