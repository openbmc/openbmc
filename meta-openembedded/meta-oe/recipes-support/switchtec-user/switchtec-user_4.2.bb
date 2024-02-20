SUMMARY = "Easy to use CLI and C library for communicating with Microsemi's Switchtec management interface"
HOMEPAGE = "https://github.com/Microsemi/switchtec-user"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d6b07c89629cff2990d2e8e1f4c2382"

DEPENDS = "ncurses openssl"

SRCREV = "e7c351c6c722336b3c79b79cd002c7c2986eefb0"
SRC_URI = "git://github.com/Microsemi/switchtec-user.git;protocol=https;branch=master"
SRC_URI[sha256sum] = "a715e46d8498418dbb8a2519318ba0714ee148151d7e4a7fa5e27770a2f6888f"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

EXTRA_OEMAKE = "DESTDIR='${D}' PREFIX='${prefix}' LDCONFIG='true' LIBDIR='${D}${libdir}'"

do_install () {
     oe_runmake install
}
