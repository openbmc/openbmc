HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/sdbusplus"
SRCREV = "3e71ab90ae1bfbc5cf2d4b632c2b29d49fe36c77"
