FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.4.48"

SRCREV = "7ddf96fbb7d637b79b449c7bd1c8d35f00571e4b"
SRC_URI = "git://github.com/raspberrypi/linux.git;protocol=git;branch=rpi-4.4.y \
           file://0001-fix-dtbo-rules.patch \
"
require linux-raspberrypi.inc
