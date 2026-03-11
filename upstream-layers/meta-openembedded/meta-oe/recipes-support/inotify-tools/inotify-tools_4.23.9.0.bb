SUMMARY = "Command line tools and C library providing a simple interface to inotify"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "5c2ab772338bcbe516306bf8cf1059ddf2fa6bb7"

SRC_URI = "git://github.com/${BPN}/${BPN};branch=master;protocol=https \
           "


inherit autotools

EXTRA_OECONF = "--disable-doxygen"

# workaround until glibc 2.35 is fixed for this [1]
# [1] https://sourceware.org/pipermail/libc-alpha/2021-December/134215.html
CFLAGS += "-Wno-error"

PACKAGES =+ "libinotifytools"

FILES:libinotifytools = "${libdir}/lib*.so.*"
