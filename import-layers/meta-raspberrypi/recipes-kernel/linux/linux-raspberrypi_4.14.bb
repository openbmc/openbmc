LINUX_VERSION ?= "4.14.39"

SRCREV = "865ddc1393f558198e7e7ce70928ff2e49c4f7f6"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
