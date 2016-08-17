FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.4.13"

SRCREV = "680be5e27a9593cf26079c6e5744a04cc2809d13"
SRC_URI = "git://github.com/raspberrypi/linux.git;protocol=git;branch=rpi-4.4.y \
"
require linux-raspberrypi.inc
