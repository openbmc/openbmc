SUMMARY = "Varlink C library and command line tool"
HOMEPAGE = "https://varlink.org/"
DESCRIPTION = "Varlink is an interface description format and protocol that aims \
to make services accessible to both humans and machines in the simplest feasible way."
SECION = "devel"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "git://github.com/varlink/libvarlink.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "2ad4ec7ca62e148dbf0ad98646ec68c2e7e8a88e"

inherit meson bash-completion lib_package

do_install:append() {
    # vim integration is not needed
    rm -rf ${D}${datadir}/vim
}
