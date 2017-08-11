DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI = "${KSRC}"
SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"
SRC_URI += "file://phosphor-vlan.scc"
SRC_URI += "file://phosphor-vlan.cfg"
SRC_URI += "file://linux-dev-4.10-v2-1-4-hwmon-pmbus-Switch-status-registers-to-16-bit.patch"
SRC_URI += "file://linux-dev-4.10-v2-2-4-hwmon-pmbus-Access-word-data-for-STATUS_WORD.patch"
SRC_URI += "file://linux-dev-4.10-v2-3-4-hwmon-pmbus-Add-generic-alarm-bit-for-iin-and-pin.patch"
SRC_URI += "file://linux-dev-4.10-v2-4-4-hwmon-pmbus-Add-debugfs-for-status-registers.patch"
SRC_URI += "file://linux-dev-4.10-v5-1-4-dt-bindings-i2c-Document-the-IBM-CCF-power-supply-version-1.patch"
SRC_URI += "file://linux-dev-4.10-v5-2-4-hwmon-pmbus-Add-IBM-Common-Form-Factor-CFF-power-supply-driver.patch"
SRC_URI += "file://linux-dev-4.10-v5-3-4-Documentation-hwmon-Document-the-IBM-CFF-power-supply.patch"
SRC_URI += "file://linux-dev-4.10-v5-4-4-ARM-dts-aspeed-Witherspoon-Add-power-supplies-to-i2c-bus.patch"

LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "openbmc"

do_patch_append() {
        for DTB in "${KERNEL_DEVICETREE}"; do
               DT=`basename ${DTB} .dtb`
                if [ -r "${WORKDIR}/${DT}.dts" ]; then
                        cp ${WORKDIR}/${DT}.dts \
                                ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts
               fi
       done
}

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

KERNEL_FEATURES_append = " phosphor-vlan"
KERNEL_FEATURES_remove_qemuall = " phosphor-vlan"
