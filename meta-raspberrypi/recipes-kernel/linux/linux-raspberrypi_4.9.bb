FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.9.80"

SRCREV = "ffd7bf4085b09447e5db96edd74e524f118ca3fe"
SRC_URI = " \
    git://github.com/raspberrypi/linux.git;branch=rpi-4.9.y \
    file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
    "

require linux-raspberrypi.inc
