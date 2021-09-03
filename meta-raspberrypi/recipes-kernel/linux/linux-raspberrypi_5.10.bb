LINUX_VERSION ?= "5.10.59"
LINUX_RPI_BRANCH ?= "rpi-5.10.y"
LINUX_RPI_KMETA_BRANCH ?= "yocto-5.10"

SRCREV_machine = "7f4de43d1e8f06f63697c7b571b509348fb29642"
SRCREV_meta = "e44a7f9801f8fc6b670bf121038d912257a83d7e"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;branch=${LINUX_RPI_BRANCH} \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=${LINUX_RPI_KMETA_BRANCH};destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

KERNEL_DTC_FLAGS += "-@ -H epapr"
