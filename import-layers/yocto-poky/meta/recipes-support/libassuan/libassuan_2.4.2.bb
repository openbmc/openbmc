SUMMARY = "IPC library used by GnuPG and GPGME"
HOMEPAGE = "http://www.gnupg.org/related_software/libassuan/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-doc = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/assuan.c;endline=20;md5=0f465544183405055ec179869fc5b5ba \
                    file://src/assuan-defs.h;endline=20;md5=20cd55535260ca1779edae5c7b80b21e"

DEPENDS = "libgpg-error"

SRC_URI = "ftp://ftp.gnupg.org/gcrypt/libassuan/libassuan-${PV}.tar.bz2 \
	   file://libassuan-add-pkgconfig-support.patch"

SRC_URI[md5sum] = "70151995c2330ae1261c0a7ca34cf478"
SRC_URI[sha256sum] = "bb06dc81380b74bf1b64d5849be5c0409a336f3b4c45f20ac688e86d1b5bcb20"

BINCONFIG = "${bindir}/libassuan-config"

inherit autotools texinfo binconfig-disabled pkgconfig

do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/*.m4
}

BBCLASSEXTEND = "native"
