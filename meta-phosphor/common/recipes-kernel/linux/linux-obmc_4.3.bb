DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KBRANCH ?= "dev-4.3"
KCONFIG_MODE="--alldefconfig"

SRC_URI = "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"

LINUX_VERSION ?= "4.3"
LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

SRCREV="7fbee5252ddea512c208727377de7df5968ee8f1"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "openbmc"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

do_patch_append() {
        for DTB in "${KERNEL_DEVICETREE}"; do
		DT=`basename ${DTB} .dtb`
                if [ -r "${WORKDIR}/${DT}.dts" ]; then
                        cp ${WORKDIR}/${DT}.dts \
                                ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts
		fi
	done
}
