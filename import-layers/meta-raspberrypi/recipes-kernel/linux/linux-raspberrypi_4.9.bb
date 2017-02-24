FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.9.10"

SRCREV = "095c4480e1f623bdc8a221a171ef13b2223706b1"
SRC_URI = "git://github.com/raspberrypi/linux.git;protocol=git;branch=rpi-4.9.y \
           file://0001-build-arm64-Add-rules-for-.dtbo-files-for-dts-overla.patch \
"
require linux-raspberrypi.inc
