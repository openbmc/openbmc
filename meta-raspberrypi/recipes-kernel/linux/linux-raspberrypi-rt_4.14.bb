LINUX_VERSION ?= "4.14.81"

SRCREV = "acf578d07d57480674d5361df9171fe9528765cb"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.14.y-rt \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
