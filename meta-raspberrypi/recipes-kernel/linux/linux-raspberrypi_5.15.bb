LINUX_VERSION ?= "5.15.24"
LINUX_RPI_BRANCH ?= "rpi-5.15.y"
LINUX_RPI_KMETA_BRANCH ?= "yocto-5.15"

SRCREV_machine = "770d94882ac145c81af72e9a37180806c3f70bbd"
SRCREV_meta = "e1b976ee4fb5af517cf01a9f2dd4a32f560ca894"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;branch=${LINUX_RPI_BRANCH};protocol=https \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=${LINUX_RPI_KMETA_BRANCH};destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

KERNEL_DTC_FLAGS += "-@ -H epapr"
