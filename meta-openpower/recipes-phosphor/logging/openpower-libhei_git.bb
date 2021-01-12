inherit meson

SUMMARY = "Hardware Error Isolator Library"
DESCRIPTION = "Hardware Error Isolator Library (libhei)"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/openpower-libhei"

PV = "0.1+git${SRCPV}"
SRCREV = "cef6349ad57fd1db16f3206262a0a77734e5548c"

S = "${WORKDIR}/git"

inherit perlnative

DEPENDS += "libxml2-native libxml-simple-perl-native"
