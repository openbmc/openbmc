SUMMARY = "Utilities for managing LZMA compressed files"
HOMEPAGE = "https://tukaani.org/xz/"
DESCRIPTION = "XZ Utils is free general-purpose data compression software with a high compression ratio. XZ Utils were written for POSIX-like systems, but also work on some not-so-POSIX systems. XZ Utils are the successor to LZMA Utils."
SECTION = "base"

# The source includes bits of PD, GPL-2.0, GPL-3.0, LGPL-2.1-or-later, but the
# only file which is GPL-3.0 is an m4 macro which isn't shipped in any of our
# packages, and the LGPL bits are under lib/, which appears to be used for
# libgnu, which appears to be used for DOS builds. So we're left with
# GPL-2.0-or-later and PD.
LICENSE = "GPL-2.0-or-later & GPL-3.0-with-autoconf-exception & LGPL-2.1-or-later & PD"
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:${PN}-dev = "GPL-2.0-or-later"
LICENSE:${PN}-staticdev = "GPL-2.0-or-later"
LICENSE:${PN}-doc = "GPL-2.0-or-later"
LICENSE:${PN}-dbg = "GPL-2.0-or-later"
LICENSE:${PN}-locale = "GPL-2.0-or-later"
LICENSE:liblzma = "PD"

LIC_FILES_CHKSUM = "file://COPYING;md5=97d554a32881fee0aa283d96e47cb24a \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://lib/getopt.c;endline=23;md5=2069b0ee710572c03bb3114e4532cd84 \
                    "

SRC_URI = "https://tukaani.org/xz/xz-${PV}.tar.gz"
SRC_URI[sha256sum] = "a2105abee17bcd2ebd15ced31b4f5eda6e17efd6b10f921a01cda4a44c91b3a0"
UPSTREAM_CHECK_REGEX = "xz-(?P<pver>\d+(\.\d+)+)\.tar"

CACHED_CONFIGUREVARS += "gl_cv_posix_shell=/bin/sh"

inherit autotools gettext

PACKAGES =+ "liblzma"

FILES:liblzma = "${libdir}/liblzma*${SOLIBS}"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "xz xzcat unxz \
                     lzma lzcat unlzma"

BBCLASSEXTEND = "native nativesdk"
