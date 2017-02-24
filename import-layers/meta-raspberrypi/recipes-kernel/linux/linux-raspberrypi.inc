require linux-rpi.inc
inherit linux-raspberrypi-base

DESCRIPTION = "Linux Kernel for Raspberry Pi"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI += " \
        file://defconfig \
        "

COMPATIBLE_MACHINE = "raspberrypi"

PE = "1"
PV = "${LINUX_VERSION}+git${SRCPV}"

# NOTE: For now we pull in the default config from the RPi kernel GIT tree.
KERNEL_DEFCONFIG_raspberrypi ?= "bcmrpi_defconfig"
KERNEL_DEFCONFIG_raspberrypi2 ?= "bcm2709_defconfig"

# CMDLINE for raspberrypi
CMDLINE = "dwc_otg.lpm_enable=0 console=serial0,115200 root=/dev/mmcblk0p2 rootfstype=ext4 rootwait"

# Add the kernel debugger over console kernel command line option if enabled
CMDLINE_append = ' ${@base_conditional("ENABLE_KGDB", "1", "kgdboc=serial0,115200", "", d)}'

UDEV_GE_141 ?= "1"

KERNEL_MODULE_AUTOLOAD += "${@bb.utils.contains("MACHINE_FEATURES", "pitft28r", "stmpe-ts", "", d)}"

# Set programmatically some variables during recipe parsing
# See http://www.yoctoproject.org/docs/current/bitbake-user-manual/bitbake-user-manual.html#anonymous-python-functions
python __anonymous () {
    kerneltype = d.getVar('KERNEL_IMAGETYPE', True)
    kerneldt = get_dts(d, d.getVar('LINUX_VERSION', True))
    d.setVar("KERNEL_DEVICETREE", kerneldt)
}

do_kernel_configme_prepend() {
    install -m 0644 ${S}/arch/${ARCH}/configs/${KERNEL_DEFCONFIG} ${WORKDIR}/defconfig || die "No default configuration for ${MACHINE} / ${KERNEL_DEFCONFIG} available."
}

do_install_prepend() {
    install -d ${D}/lib/firmware
}

do_deploy_append() {
    # Deploy cmdline.txt
    install -d ${DEPLOYDIR}/bcm2835-bootfiles
    PITFT="${@bb.utils.contains("MACHINE_FEATURES", "pitft", "1", "0", d)}"
    if [ ${PITFT} = "1" ]; then
        PITFT_PARAMS="fbcon=map:10 fbcon=font:VGA8x8"
    fi
    echo "${CMDLINE}${PITFT_PARAMS}" > ${DEPLOYDIR}/bcm2835-bootfiles/cmdline.txt
}
