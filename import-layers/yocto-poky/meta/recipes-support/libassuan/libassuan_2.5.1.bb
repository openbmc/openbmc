SUMMARY = "IPC library used by GnuPG and GPGME"
HOMEPAGE = "http://www.gnupg.org/related_software/libassuan/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-doc = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/assuan.c;endline=20;md5=ab92143a5a2adabd06d7994d1467ea5c\
                    file://src/assuan-defs.h;endline=20;md5=15d950c83e82978e35b35e790d7e4d39"

DEPENDS = "libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libassuan/libassuan-${PV}.tar.bz2 \
           file://libassuan-add-pkgconfig-support.patch \
          "

SRC_URI[md5sum] = "4354b7ae296894f232ada226a062d7d7"
SRC_URI[sha256sum] = "47f96c37b4f2aac289f0bc1bacfa8bd8b4b209a488d3d15e2229cb6cc9b26449"

BINCONFIG = "${bindir}/libassuan-config"

inherit autotools texinfo binconfig-disabled pkgconfig

do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/*.m4
}

BBCLASSEXTEND = "native nativesdk"
