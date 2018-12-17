require recipes-graphics/xorg-lib/xorg-lib-common.inc
SUMMARY = "X Athena Widget Set"
DEPENDS += "xorgproto virtual/libx11 libxext libxt libxmu libxpm libxau xmlto-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=1c65719d42900bb81b83e8293c20a364"

PE = "1"
PR = "r2"

SRC_URI[md5sum] = "e5e06eb14a608b58746bdd1c0bd7b8e3"
SRC_URI[sha256sum] = "8ef8067312571292ccc2bbe94c41109dcf022ea5a4ec71656a83d8cce9edb0cd"

do_install_append () {
    ln -sf libXaw6.so.6 ${D}${libdir}/libXaw.so.6
    ln -sf libXaw7.so.7 ${D}${libdir}/libXaw.so.7
    ln -sf libXaw7.so.7 ${D}${libdir}/libXaw.so
}

PACKAGES =+ "libxaw6 libxaw7 libxaw8"

FILES_libxaw6 = "${libdir}/libXaw*.so.6*"
FILES_libxaw7 = "${libdir}/libXaw*.so.7*"
FILES_libxaw8 = "${libdir}/libXaw8.so.8*"

# Avoid dependency on libxaw as it is not build
RDEPENDS_${PN}-dev = ""

XORG_PN = "libXaw"
