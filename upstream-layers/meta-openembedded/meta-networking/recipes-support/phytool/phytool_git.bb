SUMMARY = "PHY interface tool for Linux"
DESCRIPTION = "A tool to read and write PHY registers on Linux."
HOMEPAGE = "https://github.com/wkz/phytool"
BUGTRACKER = "https://github.com/wkz/phytool/issues"
SECTION = "console/network"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=39bba7d2cf0ba1036f2a6e2be52fe3f0"

PV = "2+git"
SRC_URI = "git://github.com/wkz/phytool.git;branch=master;protocol=https"
SRCREV = "bcf23b0261aa9f352ee4b944e30e3482158640a4"

# The Makefile has "$PREFIX/bin" hardcoded into it, hence not using $bindir here
do_install() {
    install -d ${D}${prefix}/bin
    oe_runmake 'DESTDIR=${D}' 'PREFIX=${prefix}' install
}
