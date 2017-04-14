DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI = "${KSRC}"
SRC_URI += "file://v3-0001-drivers-fsi-Increase-delay-before-sampling-input-.patch"
SRC_URI += "file://v2-0001-drivers-fsi-Change-interrupt-handler-device-traversa.patch"
SRC_URI += "file://v2-0002-drivers-fsi-Look-for-Hub-sourced-IRQs.patch"
SRC_URI += "file://v2-0003-drivers-fsi-Scan-for-hub-link-IRQ-sources.patch"
SRC_URI += "file://v2-0004-drivers-fsi-Set-IRQ-masks-along-hub-path.patch"
SRC_URI += "file://0001-drivers-fsi-Increase-clock-delays.patch"

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
