require recipes-graphics/xorg-lib/xorg-lib-common.inc
SUMMARY = "X Athena Widget Set"
DEPENDS += "xorgproto virtual/libx11 libxext libxt libxmu libxpm libxau xmlto-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=1c65719d42900bb81b83e8293c20a364"

PE = "1"

XORG_PN = "libXaw"

SRC_URI[sha256sum] = "ab35f70fde9fb02cc71b342f654846a74328b74cb3a0703c02d20eddb212754a"

do_install:append () {
    ln -sf libXaw6.so.6 ${D}${libdir}/libXaw.so.6
    ln -sf libXaw7.so.7 ${D}${libdir}/libXaw.so.7
    ln -sf libXaw7.so.7 ${D}${libdir}/libXaw.so
}

PACKAGES =+ "libxaw6 libxaw7 libxaw8"

FILES:libxaw6 = "${libdir}/libXaw*.so.6*"
FILES:libxaw7 = "${libdir}/libXaw*.so.7*"
FILES:libxaw8 = "${libdir}/libXaw8.so.8*"

# Avoid dependency on libxaw as it is not build
RDEPENDS:${PN}-dev = ""
