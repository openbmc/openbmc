SUMMARY = "High-level GnuPG encryption/signing API"
DESCRIPTION = "GnuPG Made Easy (GPGME) is a library designed to make access to GnuPG easier for applications. It provides a High-Level Crypto API for encryption, decryption, signing, signature verification and key management"
HOMEPAGE = "http://www.gnupg.org/gpgme.html"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & GPL-3.0-or-later"
LICENSE:${PN} = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN}-tool = "GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LESSER;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://src/gpgme.h.in;endline=23;md5=c0d051fa63f5a5514f4ab190d7ca495e \
                    file://src/engine.h;endline=21;md5=f58f7a0b6488edae41b925ac9c890068 \
                    file://src/gpgme-tool.c;endline=21;md5=66c5381e0e05475792e24982d15e7ce8 \
                    "

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/gpgme/${BP}.tar.bz2 \
           file://0001-Revert-build-Make-gpgme.m4-use-gpgrt-config-with-.pc.patch \
           file://0001-pkgconfig.patch \
           file://0005-gpgme-config-skip-all-lib-or-usr-lib-directories-in-.patch \
           file://0001-use-closefrom-on-linux-and-glibc-2.34.patch \
           file://0001-posix-io.c-Use-off_t-instead-of-off64_t.patch \
           file://0001-autogen.sh-remove-unknown-in-version.patch \
           "

SRC_URI[sha256sum] = "821ab0695c842eab51752a81980c92b0410c7eadd04103f791d5d2a526784966"

DEPENDS = "libgpg-error-native libgpg-error libassuan"

RRECOMMENDS:${PN} += "${PN}-tool"

BINCONFIG = "${bindir}/gpgme-config"

# Default in configure.ac: "cl"
# Supported: "cl"
DEFAULT_LANGUAGES = ""
LANGUAGES ?= "${DEFAULT_LANGUAGES}"

EXTRA_OECONF += '--enable-languages="${LANGUAGES}" \
                 --disable-gpgconf-test \
                 --disable-gpg-test \
                 --disable-gpgsm-test \
                 --disable-g13-test \
'

inherit autotools texinfo binconfig-disabled pkgconfig multilib_header

export PKG_CONFIG = 'pkg-config'

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "${PN}-tool"

FILES:${PN}-tool = "${bindir}/gpgme-tool"
FILES:${PN}-dev += "${datadir}/common-lisp/source/gpgme/*"

CFLAGS:append:libc-musl = " -D__error_t_defined "
CACHED_CONFIGUREVARS:libc-musl = "ac_cv_sys_file_offset_bits=no"

do_configure:prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
	rm -f ${S}/m4/libassuan.m4
}

do_install:append() {
       oe_multilib_header gpgme.h
}
