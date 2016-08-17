DESCRIPTION = "LFTP is a sophisticated file transfer program with \
               command line interface. It supports FTP, HTTP, \
               FISH, SFTP, HTTPS and FTPS protocols"
HOMEPAGE = "http://lftp.yar.ru/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "readline"

SRC_URI = "http://fossies.org/linux/misc/lftp-${PV}.tar.gz \
          "
SRC_URI[md5sum] = "2777dd514d21fe1da764bedd1d0ab36c"
SRC_URI[sha256sum] = "a8b53e5ca2c1acbecd181c87f21a8673ca9038dc9f2be6ab8c23790bd91fd446"

inherit autotools gettext pkgconfig

EXTRA_OECONF += "--with-modules"

PACKAGECONFIG ??= "libidn openssl zlib gnutls"
PACKAGECONFIG[libidn] = "--with-libidn, --without-libidn, libidn"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
PACKAGECONFIG[zlib] = "--with-zlib, --without-zlib, zlib"
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls"

FILES_${PN}-dbg += "${libdir}/lftp/${PV}/.debug"
RDEPENDS_${PN} = "perl bash readline"
