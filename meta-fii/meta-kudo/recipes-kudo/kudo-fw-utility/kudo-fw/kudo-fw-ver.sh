#!/bin/bash

# Disable check for splitting
# shellcheck disable=SC2207
# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

BMC_CPLD_VER_FILE="/run/cpld0.version"
MB_CPLD_VER_FILE="/run/cpld1.version"
ver=''

function fw_rev() {
    case $1 in
    cpldb)
        rsp=($(i2cget -y -f -a "${I2C_BMC_CPLD[0]}" 0x"${I2C_BMC_CPLD[1]}" 0x00 i 5))
        ver=$(printf '%d.%d.%d.%d' "${rsp[4]}" "${rsp[3]}" "${rsp[2]}" "${rsp[1]}")
        ;;
    cpldm)
        rsp=($(i2cget -y -f -a "${I2C_MB_CPLD[0]}" 0x"${I2C_MB_CPLD[1]}" 0x00 i 5))
        ver=$(printf '%d.%d.%d.%d' "${rsp[4]}" "${rsp[3]}" "${rsp[2]}" "${rsp[1]}")
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
