HOMEPAGE = "http://github.com/openbmc/google-misc"
PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "git://github.com/openbmc/google-misc;branch=master;protocol=https"
SRCREV = "c66ebc35bf44912992e18de47198ba98a9a34b97"

S = "${WORKDIR}/git/subprojects/${GOOGLE_MISC_PROJ}"
inherit meson pkgconfig

EXTRA_OEMESON += "-Dwerror=false"
