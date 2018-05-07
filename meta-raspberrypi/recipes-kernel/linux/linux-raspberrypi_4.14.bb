LINUX_VERSION ?= "4.14.58"

SRCREV = "a06f9e522301dfacc1f382d72e6a9792d8350328"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
