SUMMARY = "PHY interface tool for Linux"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

PV = "2+git"
SRCREV = "8882328c08ba2efb13c049812098f1d0cb8adf0c"
SRC_URI = "git://github.com/wkz/phytool.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

# The Makefile has "$PREFIX/bin" hardcoded into it, hence not using $bindir here
do_install() {
    install -d ${D}${prefix}/bin
    oe_runmake 'DESTDIR=${D}' 'PREFIX=${prefix}' install
}
