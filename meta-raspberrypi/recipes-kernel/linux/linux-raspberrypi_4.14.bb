LINUX_VERSION ?= "4.14.114"

SRCREV = "7688b39276ff9952df381d79de63b258e73971ce"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
