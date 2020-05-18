HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/sdbusplus"
SRCREV = "1b1321cf54851a4d8a8fc39fe05707659a54aa90"
