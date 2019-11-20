SUMMARY = "A tablet description library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=40a21fffb367c82f39fd91a3b137c36e"

DEPENDS = " \
    libxml2-native \
    libgudev \
"

inherit autotools pkgconfig

SRC_URI = "git://github.com/linuxwacom/libwacom.git"
SRCREV = "87cc710e21a6220e267dd08936bbec2932aa3658"
S = "${WORKDIR}/git"
