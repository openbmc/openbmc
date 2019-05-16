SUMMARY = "Command line tools and C library providing a simple interface to inotify"
AUTHOR = "Rohan McGovern <rohan@mcgovern.id.au>"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "e203934e46784bb34c213078423ba1678e0c4936"
PV = "3.20.1"

SRC_URI = "git://github.com/rvoicilas/${BPN} \
           file://inotify-nosys-fix-system-call-number.patch \
          "

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF = "--disable-doxygen"

PACKAGES =+ "libinotifytools"

FILES_libinotifytools = "${libdir}/lib*.so.*"
