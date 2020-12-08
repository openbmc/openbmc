SUMMARY = "Command line tools and C library providing a simple interface to inotify"
AUTHOR = "Rohan McGovern <rohan@mcgovern.id.au>"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "cfa93aa19f81d85b63cd64da30c7499890d4c07d"
PV = "3.20.2.2"

SRC_URI = "git://github.com/rvoicilas/${BPN} \
           file://0001-Makefile.am-add-build-rule-for-README.patch \
           "

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF = "--disable-doxygen"

PACKAGES =+ "libinotifytools"

FILES_libinotifytools = "${libdir}/lib*.so.*"
