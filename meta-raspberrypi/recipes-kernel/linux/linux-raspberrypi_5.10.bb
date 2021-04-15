LINUX_VERSION ?= "5.10.25"
LINUX_RPI_BRANCH ?= "rpi-5.10.y"

SRCREV_machine = "d1fd8a5727908bb677c003d2ae977e9d935a6f94"
SRCREV_meta = "5833ca701711d487c9094bd1efc671e8ef7d001e"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;branch=${LINUX_RPI_BRANCH} \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.10;destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

KERNEL_DTC_FLAGS += "-@ -H epapr"
