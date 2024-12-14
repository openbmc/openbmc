SUMMARY = "IPC library used by GnuPG and GPGME"
DESCRIPTION = "A small library implementing the so-called Assuan protocol. \
This protocol is used for IPC between most newer GnuPG components. \
Both, server and client side functions are provided. "
HOMEPAGE = "http://www.gnupg.org/related_software/libassuan/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPL-3.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LICENSE:${PN}-doc = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/assuan.c;endline=20;md5=ab92143a5a2adabd06d7994d1467ea5c\
                    file://src/assuan-defs.h;endline=20;md5=15d950c83e82978e35b35e790d7e4d39"

DEPENDS = "libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libassuan/libassuan-${PV}.tar.bz2 \
           file://libassuan-add-pkgconfig-support.patch \
          "

SRC_URI[sha256sum] = "c8f0f42e6103dea4b1a6a483cb556654e97302c7465308f58363778f95f194b1"

BINCONFIG = "${bindir}/libassuan-config"

inherit autotools texinfo binconfig-disabled pkgconfig multilib_header

do_configure:prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/*.m4
}

do_install:append () {
    oe_multilib_header assuan.h
}

BBCLASSEXTEND = "native nativesdk"
