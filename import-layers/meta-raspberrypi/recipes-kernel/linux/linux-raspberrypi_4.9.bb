FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.9.59"

SRCREV = "e7976b2affa8c05bd299ead0ad9c36ddaf135b9d"
SRC_URI = "git://github.com/raspberrypi/linux.git;branch=rpi-4.9.y"

require linux-raspberrypi.inc
