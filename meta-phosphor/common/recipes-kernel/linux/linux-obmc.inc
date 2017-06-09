DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI = "${KSRC}"
SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"
SRC_URI += "file://v2-0001-drivers-fsi-occ-Fix-1-byte-response-and-rc-for-O_.patch"
SRC_URI += "file://v2-0002-drivers-hwmon-occ-Add-new-sensor-versions-and-non.patch"
SRC_URI += "file://v2-0003-drivers-hwmon-occ-Add-P8-OCC-access.patch"
SRC_URI += "file://v2-0004-drivers-occ-hwmon-and-fsi-probing-fix.patch"
SRC_URI += "file://v2-0005-aspeed-update-power9-dts-and-defconfigs-for-OCC.patch"

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
