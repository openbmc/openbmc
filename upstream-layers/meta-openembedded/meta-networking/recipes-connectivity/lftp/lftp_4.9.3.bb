DESCRIPTION = "LFTP is a sophisticated file transfer program with \
               command line interface. It supports FTP, HTTP, \
               FISH, SFTP, HTTPS and FTPS protocols"
HOMEPAGE = "http://lftp.yar.ru/"
SECTION = "console/network"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://lftp.yar.ru/ftp/lftp-${PV}.tar.bz2 \
           file://0001-gnulib-fix-lftp-build-with-clang-lld-and-gcc16.patch \
           "

SRC_URI[sha256sum] = "adceaef1bd21a38d07c973233fab603813c431f0a8dcbd23239fa9a41ae17b6e"

inherit autotools gettext pkgconfig

EXTRA_OECONF += "--with-modules --disable-rpath"

PACKAGECONFIG ??= "openssl zlib gnutls readline expat"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_INCDIR}/.., --without-zlib, zlib"
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls"
PACKAGECONFIG[readline] = "--with-readline=${STAGING_INCDIR}/.. --with-readline-inc=${STAGING_INCDIR} --with-readline-lib=-lreadline, --with-readline=no, readline"
PACKAGECONFIG[expat] = "--with-expat=${STAGING_INCDIR}/.. --with-expat-inc=${STAGING_INCDIR} --with-expat-lib=-lexpat, , expat"

do_install:append() {
	rm -rf ${D}${libdir}/charset.alias
}
PACKAGES =+ "${PN}-zsh-completion"

FILES:${PN} += "${datadir}/icons/hicolor"
FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"

RDEPENDS:${PN} = "perl bash readline"
