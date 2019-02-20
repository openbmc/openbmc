LINUX_VERSION ?= "4.14.98"

SRCREV = "5d63a4595d32a8505590d5fea5c4ec1ca79fd49d"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
