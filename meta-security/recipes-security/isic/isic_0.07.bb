SUMMARY = "ISIC -- IP Stack Integrity Checker"
DESCRIPTION = "ISIC is a suite of utilities to exercise the stability of an IP Stack and its component stacks (TCP, UDP, ICMP et. al.)"
HOMEPAGE = "http://isic.sourceforge.net/"
SECTION = "security"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"

DEPENDS = "libnet"
PARALLEL_MAKE = ""

SRC_URI = "http://prdownloads.sourceforge.net/isic/${BPN}-${PV}.tgz \
    file://configure_fix.patch \
    file://isic-0.07-netinet.patch \
    file://isic-0.07-make.patch \
    "

SRC_URI[md5sum] = "29f70c9bde9aa9128b8f7e66a315f9a4"
SRC_URI[sha256sum] = "e033c53e03e26a4c72b723e2a5a1c433ee70eb4d23a1ba0d7d7e14ee1a80429d"

S="${UNPACKDIR}/${BPN}-${PV}"

inherit autotools-brokensep

EXTRA_OECONF += "--with-libnet-dir=${STAGING_DIR_HOST}${libdir} "

# many configure tests are failing with gcc-14
CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"
BUILD_CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"

do_configure () {
    oe_runconf
}

RDEPNEDS += "libnet"
