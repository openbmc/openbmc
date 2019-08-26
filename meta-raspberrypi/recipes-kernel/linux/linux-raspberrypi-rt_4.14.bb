LINUX_VERSION ?= "4.14.91"

SRCREV = "0b520d5f1f580d36a742a9457a5673fa1578fff3"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y-rt \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
