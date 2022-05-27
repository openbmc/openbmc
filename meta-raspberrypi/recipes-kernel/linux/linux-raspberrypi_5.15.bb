LINUX_VERSION ?= "5.15.38"
LINUX_RPI_BRANCH ?= "rpi-5.15.y"
LINUX_RPI_KMETA_BRANCH ?= "yocto-5.15"

SRCREV_machine = "d5b5cbcd17d97f35292f960bca82c0915a03e594"
SRCREV_meta = "37891dc371e83a3451781dd81a8a85cccd60084b"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;branch=${LINUX_RPI_BRANCH};protocol=https \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=${LINUX_RPI_KMETA_BRANCH};destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

KERNEL_DTC_FLAGS += "-@ -H epapr"
