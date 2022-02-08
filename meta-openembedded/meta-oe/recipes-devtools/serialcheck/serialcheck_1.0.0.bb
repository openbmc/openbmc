SUMMARY = "Application to verify operation of serial ports"
HOMEPAGE = "http://git.breakpoint.cc/cgit/bigeasy/serialcheck.git/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://git.breakpoint.cc/bigeasy/serialcheck.git;branch=master \
           file://0001-Add-option-to-enable-internal-loopback.patch \
           file://0002-Restore-original-loopback-config.patch \
           file://0001-Makefile-Change-order-of-link-flags.patch \
           "

SRCREV = "63854a2d0c0129efab132ec328a75279e013fb84"

S = "${WORKDIR}/git"

DEPENDS_append_libc-musl = " argp-standalone"
EXTRA_OEMAKE = "-e MAKEFLAGS="

CFLAGS_prepend = "-Wall -Wextra -Wno-sign-compare -Wno-pointer-sign "
LDFLAGS_append_libc-musl = " -largp"

do_install() {
    install -d ${D}${bindir}
    install ${S}/serialcheck ${D}${bindir}
    install -d ${D}${docdir}/${BP}
    install ${S}/Readme.txt ${D}${docdir}/${BP}
}
CLEANBROKEN = "1"

BBCLASSEXTEND = "nativesdk"
