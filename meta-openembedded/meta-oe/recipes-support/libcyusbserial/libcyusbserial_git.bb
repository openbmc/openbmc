SUMMARY = "Enable userspace control of Cypress USB-Serial bridge devices"
HOMEPAGE = "https://github.com/cyrozap/libcyusbserial"
BUGTRACKER = "https://github.com/cyrozap/libcyusbserial/issues"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LESSER.txt;md5=4fbd65380cdd255951079008b364516c"
DEPENDS = "libusb udev"

PV = "1.0.0+git${SRCPV}"

SRCREV = "655e2d544183d094f0e2d119c7e0c6206a0ddb3f"
SRC_URI = "git://github.com/cyrozap/${BPN}.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit cmake

PACKAGES =+ "${PN}-utils"
FILES_${PN}-utils = "${bindir}/*"
