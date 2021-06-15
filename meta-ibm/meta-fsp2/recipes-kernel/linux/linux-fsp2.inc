DESCRIPTION = "Linux kernel for the FSP2 system on a chip"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=dev-4.17"
SRC_URI = "${KSRC}"

LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "^${MACHINE}$"
KERNEL_CLASSES ?= "kernel-uimage"
KERNEL_IMAGETYPE ?= "uImage"

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

INSANE_SKIP_kernel-vmlinux = "ldflags"
