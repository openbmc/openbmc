SUMMARY = "Command line tools and C library providing a simple interface to inotify"
AUTHOR = "Rohan McGovern <rohan@mcgovern.id.au>"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "1df9af4d6cd0f4af4b1b19254bcf056aed4ae395"
PV = "3.14+git${SRCPV}"

SRC_URI = "git://github.com/rvoicilas/${BPN} \
           file://inotifywait-fix-compile-error-with-GCC-6.patch \
           file://inotify-nosys-fix-system-call-number.patch \
          "

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF = "--disable-doxygen"

PACKAGES =+ "libinotifytools"

FILES_libinotifytools = "${libdir}/lib*.so.*"
