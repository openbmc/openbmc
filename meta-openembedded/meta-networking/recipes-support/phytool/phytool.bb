SUMMARY = "PHY interface tool for Linux"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

PV = "1.0.1+git${SRCPV}"
SRCREV = "3149bfdb4f513e2f0da0a7d0bc5d0873578696f2"
SRC_URI = "git://github.com/wkz/phytool.git"

S = "${WORKDIR}/git"

# The Makefile has "$PREFIX/bin" hardcoded into it, hence not using $bindir here
do_install() {
    install -d ${D}${prefix}/bin
    oe_runmake 'DESTDIR=${D}' 'PREFIX=${prefix}' install
}
