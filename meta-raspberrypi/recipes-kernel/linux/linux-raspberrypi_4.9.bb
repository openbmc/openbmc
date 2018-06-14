FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.9.80"

SRCREV = "7f9c648dad6473469b4133898fa6bb8d818ecff9"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.9.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
