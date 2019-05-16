LINUX_VERSION ?= "4.14.112"

SRCREV = "6b5c4a2508403839af29ef44059d04acbe0ee204"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
