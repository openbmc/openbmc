DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI = "${KSRC}"
SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"
SRC_URI += "file://0001-fsi-Match-fsi-slaves-engines-to-available-device-tre.patch"
SRC_URI += "file://linux-dev-4.10-v2-1-6-drivers-i2c-Add-FSI-attached-I2C-master-algorithm.patch"
SRC_URI += "file://linux-dev-4.10-v2-2-6-drivers-i2c-Add-port-structure-to-FSI-algorithm.patch"
SRC_URI += "file://linux-dev-4.10-v2-3-6-drivers-i2c-Add-transfer-implementation-for-FSI-algorithm.patch"
SRC_URI += "file://linux-dev-4.10-v2-4-6-drivers-i2c-Add-I2C-master-locking-to-FSI-algorithm.patch"
SRC_URI += "file://linux-dev-4.10-v2-5-6-drivers-i2c-Add-bus-recovery-for-FSI-algorithm.patch"
SRC_URI += "file://linux-dev-4.10-drivers-fsi-Add-FSI-SBEFIFO-driver.patch"
SRC_URI += "file://linux-dev-4.10-1-3-drivers-fsi-sbefifo-Add-in-kernel-API.patch"
SRC_URI += "file://linux-dev-4.10-2-3-drivers-fsi-sbefifo-Add-OCC-driver.patch"
SRC_URI += "file://0001-arm-dts-aspeed-Add-FSI-devices.patch"
SRC_URI += "file://0001-hwmon-Add-support-for-MAX31785-intelligent-fan-contr.patch"
SRC_URI += "file://v2-0001-arm-dts-aspeed-Add-missing-clock-sources-for-barr.patch"
SRC_URI += "file://0001-arm-configs-aspeed-Disable-CONFIG_MTD_SPI_NOR_USE_4K.patch"

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
