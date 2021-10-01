HOMEPAGE = "http://github.com/openbmc/google-misc"
PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "git://github.com/openbmc/google-misc"
SRCREV = "9a76b6fbfbec70fa5d3cf7874599542692cc98f6"

S = "${WORKDIR}/git/subprojects/${GOOGLE_MISC_PROJ}"
inherit meson
