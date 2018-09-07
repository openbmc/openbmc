LINUX_VERSION ?= "4.14.68"

SRCREV = "8c8666ff6c1254d325cfa300d16f9928b3f31fc0"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
