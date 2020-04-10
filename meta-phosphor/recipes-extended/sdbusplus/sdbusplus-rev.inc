HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/sdbusplus"
SRCREV = "afe80cf2e5dc4aefe3b041adeb0230e61929bf12"
