SUMMARY = "High-level GnuPG encryption/signing API"
DESCRIPTION = "GnuPG Made Easy (GPGME) is a library designed to make access to GnuPG easier for applications. It provides a High-Level Crypto API for encryption, decryption, signing, signature verification and key management"
HOMEPAGE = "http://www.gnupg.org/gpgme.html"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LESSER;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://src/gpgme.h.in;endline=23;md5=71ba2ae8d6ca034ed10bd099a8cf473c \
                    file://src/engine.h;endline=22;md5=4b6d8ba313d9b564cc4d4cfb1640af9d"

SRC_URI = "ftp://ftp.gnupg.org/gcrypt/gpgme/${BP}.tar.bz2 \
           file://gpgme.pc \
           file://pkgconfig.patch \
          "

SRC_URI[md5sum] = "feafa03ea064e1d1dc11bc2b88404623"
SRC_URI[sha256sum] = "bb38c0ec8815c9e94e6047b484984808a8dad9d6bec8df33dc5339fd55ffea6c"

DEPENDS = "libgpg-error libassuan"

BINCONFIG = "${bindir}/gpgme-config"

inherit autotools texinfo binconfig-disabled pkgconfig

PACKAGES =+ "${PN}-pthread"
FILES_${PN}-pthread = "${libdir}/libgpgme-pthread.so.*"
FILES_${PN}-dev += "${datadir}/common-lisp/source/gpgme/*"

do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
	rm -f ${S}/m4/libassuan.m4
}

do_install_append () {
        install -d ${D}${libdir}/pkgconfig
        install -m 0644 ${WORKDIR}/gpgme.pc ${D}${libdir}/pkgconfig/
}
