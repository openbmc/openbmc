LINUX_VERSION ?= "6.12.1"
LINUX_RPI_BRANCH ?= ""
LINUX_RPI_KMETA_BRANCH ?= "yocto-6.12"

SRCREV_machine = "614fa9b0b1a21c0cc320b9915393bdaa31357de9"
SRCREV_meta = "96ce9b7ee67702aec75816c4d44a527061c418c5"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;nobranch=1;protocol=https \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=${LINUX_RPI_KMETA_BRANCH};destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

KERNEL_DTC_FLAGS += "-@ -H epapr"

RDEPENDS:${KERNEL_PACKAGE_NAME}:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}"
RDEPENDS:${KERNEL_PACKAGE_NAME}-base:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}-base"
RDEPENDS:${KERNEL_PACKAGE_NAME}-image:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}-image"
RDEPENDS:${KERNEL_PACKAGE_NAME}-dev:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}-dev"
RDEPENDS:${KERNEL_PACKAGE_NAME}-vmlinux:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}-vmlinux"
RDEPENDS:${KERNEL_PACKAGE_NAME}-modules:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}-modules"
RDEPENDS:${KERNEL_PACKAGE_NAME}-dbg:raspberrypi-armv7:append = " ${RASPBERRYPI_v7_KERNEL_PACKAGE_NAME}-dbg"

DEPLOYDEP = ""
DEPLOYDEP:raspberrypi-armv7 = "${RASPBERRYPI_v7_KERNEL}:do_deploy"
do_deploy[depends] += "${DEPLOYDEP}"
