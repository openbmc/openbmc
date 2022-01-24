SUMMARY = "Command line tools and C library providing a simple interface to inotify"
AUTHOR = "Rohan McGovern <rohan@mcgovern.id.au>"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "8f8fcdffb7e80928bea6c1fccd3527a9f5d8fe77"

SRC_URI = "git://github.com/${BPN}/${BPN};branch=master;protocol=https \
           file://0001-libinotifytools-Include-limit.h-for-PATH_MAX.patch \
           file://0002-libinotifytools-Bridge-differences-between-musl-glib.patch \
          "

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF = "--disable-doxygen"

# workaround until glibc 2.35 is fixed for this [1]
# [1] https://sourceware.org/pipermail/libc-alpha/2021-December/134215.html
CFLAGS += "-Wno-error"

PACKAGES =+ "libinotifytools"

FILES:libinotifytools = "${libdir}/lib*.so.*"
