DESCRIPTION = "Linux kernel for OpenBMC"
SECTION = "kernel"
LICENSE = "GPLv2"

KCONFIG_MODE="--alldefconfig"

KSRC ?= "git://github.com/openbmc/linux;protocol=git;branch=${KBRANCH}"
SRC_URI = "${KSRC}"

LINUX_VERSION_EXTENSION ?= "-${SRCREV}"

PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_${MACHINE} = "openbmc"

do_patch[depends] += "${@cf_enabled('obmc-mrw', 'mrw-native:do_populate_sysroot', d)}"
do_patch[depends] += "${@cf_enabled('obmc-mrw', 'mrw-devtree-native:do_populate_sysroot', d)}"
do_patch[depends] += "${@cf_enabled('obmc-mrw', 'devtree-config-native:do_populate_sysroot', d)}"
do_patch[depends] += "${@cf_enabled('obmc-mrw', 'perl-native:do_populate_sysroot', d)}"

inherit obmc-phosphor-utils
INHERITS += "${@cf_enabled('obmc-mrw', 'perlnative', d)}"
inherit ${INHERITS}

do_patch_append() {
        for DTB in "${KERNEL_DEVICETREE}"; do
               DT=`basename ${DTB} .dtb`
                if [ -r "${WORKDIR}/${DT}.dts" ]; then
                        cp ${WORKDIR}/${DT}.dts \
                                ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts
               fi
       done

        USE_MRW="${@cf_enabled('obmc-mrw', 'yes', d)}"
        if [ "${USE_MRW}" = "yes" ]; then

            for DTB in "${KERNEL_DEVICETREE}"; do
                DT=`basename ${DTB} .dtb`
                ${STAGING_BINDIR_NATIVE}/perl-native/perl \
                    ${STAGING_BINDIR_NATIVE}/gen_devtree.pl \
                    -x ${STAGING_DATADIR_NATIVE}/obmc-mrw/${MACHINE}.xml \
                    -y ${STAGING_DATADIR_NATIVE}/devtree/config.yaml \
                    -o ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/${DT}.dts
            done
        fi
}

inherit kernel
require recipes-kernel/linux/linux-yocto.inc
