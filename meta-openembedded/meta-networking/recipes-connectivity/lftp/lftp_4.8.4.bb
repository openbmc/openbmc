DESCRIPTION = "LFTP is a sophisticated file transfer program with \
               command line interface. It supports FTP, HTTP, \
               FISH, SFTP, HTTPS and FTPS protocols"
HOMEPAGE = "http://lftp.yar.ru/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://lftp.yar.ru/ftp/lftp-${PV}.tar.bz2 \
           file://fix-gcc-6-conflicts-signbit.patch \
          "
SRC_URI[md5sum] = "a56b5047dbfda052df4c1dfd197aa092"
SRC_URI[sha256sum] = "a853edbd075b008c315679c7882b6dcc6821ed2365d2ed843a412acd3d40da0e"

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
