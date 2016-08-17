FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "3.18.16"

SRCREV = "1bb18c8f721ef674a447f3622273f2e2de7a205c"
SRC_URI = "git://github.com/raspberrypi/linux.git;protocol=git;branch=rpi-3.18.y \
           file://0001-dts-add-overlay-for-pitft22.patch \
          "
require linux-raspberrypi.inc

# Create missing out of tree 'overlays' directory prior to install step
do_compile_prepend() {
  mkdir -p ${B}/arch/arm/boot/dts/overlays
}
