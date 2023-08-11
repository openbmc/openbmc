SUMMARY = "Command line tools and C library providing a simple interface to inotify"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "c8bdbc0a2ed822fc7c67c5c3e102d89fe27fb2d0"

SRC_URI = "git://github.com/${BPN}/${BPN};branch=master;protocol=https \
           file://0002-libinotifytools-Bridge-differences-between-musl-glib.patch \
           file://0002-configure-Add-AC_SYS_LARGEFILE-autoconf-macro.patch \
           file://0003-replace-stat64-lstat64-with-stat-lstat.patch \
           "

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF = "--disable-doxygen"

# workaround until glibc 2.35 is fixed for this [1]
# [1] https://sourceware.org/pipermail/libc-alpha/2021-December/134215.html
CFLAGS += "-Wno-error"

PACKAGES =+ "libinotifytools"

FILES:libinotifytools = "${libdir}/lib*.so.*"
