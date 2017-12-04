DESCRIPTION = "LFTP is a sophisticated file transfer program with \
               command line interface. It supports FTP, HTTP, \
               FISH, SFTP, HTTPS and FTPS protocols"
HOMEPAGE = "http://lftp.yar.ru/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://fossies.org/linux/misc/lftp-${PV}.tar.gz \
           file://fix-gcc-6-conflicts-signbit.patch \
          "
SRC_URI[md5sum] = "8eb1fe5f113126b60f172643c7f6c2e6"
SRC_URI[sha256sum] = "ce6519831603c19c2cf2e3c10b41d6ddc87a16049b99383e7b9b77fbc7707214"

inherit autotools gettext pkgconfig

acpaths = "-I ./m4"

EXTRA_OECONF += "--with-modules"

PACKAGECONFIG ??= "libidn openssl zlib gnutls readline expat"
PACKAGECONFIG[libidn] = "--with-libidn, --without-libidn, libidn"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_INCDIR}/.., --without-zlib, zlib"
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls"
PACKAGECONFIG[readline] = "--with-readline=${STAGING_INCDIR}/.. --with-readline-inc=${STAGING_INCDIR} --with-readline-lib=-lreadline, --with-readline=no, readline"
PACKAGECONFIG[expat] = "--with-expat=${STAGING_INCDIR}/.. --with-expat-inc=${STAGING_INCDIR} --with-expat-lib=-lexpat, , expat"

FILES_${PN}-dbg += "${libdir}/lftp/${PV}/.debug"
RDEPENDS_${PN} = "perl bash readline"
