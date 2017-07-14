DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-utils

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI = "${KSRC}"
SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"
SRC_URI += "file://phosphor-vlan.scc"
SRC_URI += "file://phosphor-vlan.cfg"
SRC_URI += "${@mf_enabled(d, 'obmc-ubi-fs', 'file://0001-ARM-dts-Aspeed-Witherspoon-Update-BMC-partitioning.patch')}"


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
