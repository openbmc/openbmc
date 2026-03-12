SUMMARY = "Easy to use CLI and C library for communicating with Microsemi's Switchtec management interface"
HOMEPAGE = "https://github.com/Microsemi/switchtec-user"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d6b07c89629cff2990d2e8e1f4c2382"

DEPENDS = "ncurses openssl"

SRC_URI = "git://github.com/Microsemi/switchtec-user.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "abe2a1d2367a118469a7b94bc4dd856aaf856eec"


inherit autotools-brokensep pkgconfig

EXTRA_OEMAKE = "DESTDIR='${D}' PREFIX='${prefix}' LDCONFIG='true' LIBDIR='${D}${libdir}'"

do_install () {
     oe_runmake install
}
