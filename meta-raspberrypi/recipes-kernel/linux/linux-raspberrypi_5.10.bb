LINUX_VERSION ?= "5.10.78"
LINUX_RPI_BRANCH ?= "rpi-5.10.y"
LINUX_RPI_KMETA_BRANCH ?= "yocto-5.10"

SRCREV_machine = "b2c047ab7e17a4ed702d313581620e826c58cc3c"
SRCREV_meta = "e1979ceb171bc91ef2cb71cfcde548a101dab687"

KMETA = "kernel-meta"

SRC_URI = " \
    git://github.com/raspberrypi/linux.git;name=machine;branch=${LINUX_RPI_BRANCH};protocol=https \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=${LINUX_RPI_KMETA_BRANCH};destsuffix=${KMETA} \
    file://powersave.cfg \
    file://android-drivers.cfg \
    "

require linux-raspberrypi.inc

KERNEL_DTC_FLAGS += "-@ -H epapr"
