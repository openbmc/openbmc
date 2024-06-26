DESCRIPTION = "Linux kernel for the FSP2 system on a chip"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"

KCONFIG_MODE="--alldefconfig"

KSRC ?= "git://github.com/openbmc/linux;protocol=https;branch=dev-4.17"
SRC_URI = "${KSRC}"

LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "^${MACHINE}$"
KERNEL_CLASSES ?= "kernel-uimage"
KERNEL_IMAGETYPE ?= "uImage"

do_patch:append() {
    for DTB in "${KERNEL_DEVICETREE}"; do
        DT=`basename ${DTB} .dtb`
        if [ -r "${UNPACKDIR}/${DT}.dts" ]; then
            cp ${UNPACKDIR}/${DT}.dts \
                ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts
        fi
    done
}

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

INSANE_SKIP:kernel-vmlinux = "ldflags"
