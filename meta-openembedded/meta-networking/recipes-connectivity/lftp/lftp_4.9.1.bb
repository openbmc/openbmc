DESCRIPTION = "LFTP is a sophisticated file transfer program with \
               command line interface. It supports FTP, HTTP, \
               FISH, SFTP, HTTPS and FTPS protocols"
HOMEPAGE = "http://lftp.yar.ru/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://lftp.yar.ru/ftp/lftp-${PV}.tar.bz2"
SRC_URI[md5sum] = "19ce64012bc722ad61667372fa262382"
SRC_URI[sha256sum] = "a692fd081c19e2cc045869ab721a4fb3d7571040849a4406c4042f735232414c"

inherit autotools gettext pkgconfig

acpaths = "-I ./m4"

EXTRA_OECONF += "--with-modules --disable-rpath"

PACKAGECONFIG ??= "openssl zlib gnutls readline expat"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_INCDIR}/.., --without-zlib, zlib"
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls"
PACKAGECONFIG[readline] = "--with-readline=${STAGING_INCDIR}/.. --with-readline-inc=${STAGING_INCDIR} --with-readline-lib=-lreadline, --with-readline=no, readline"
PACKAGECONFIG[expat] = "--with-expat=${STAGING_INCDIR}/.. --with-expat-inc=${STAGING_INCDIR} --with-expat-lib=-lexpat, , expat"

do_install_append() {
	rm -rf ${D}${libdir}/charset.alias
}
FILES_${PN} += "${datadir}/icons/hicolor"
FILES_${PN}-dbg += "${libdir}/lftp/${PV}/.debug"
RDEPENDS_${PN} = "perl bash readline"
