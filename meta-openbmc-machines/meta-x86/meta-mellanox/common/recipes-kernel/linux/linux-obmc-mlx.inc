DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

KBRANCH = "dev-4.7"
KSRC ?= "git://github.com/mellanoxbmc/linux.git;protocol=git;branch=${KBRANCH}"

SRC_URI = "${KSRC}"

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
require linux-yocto-mlx.inc
