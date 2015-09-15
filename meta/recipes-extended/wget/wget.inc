SUMMARY = "Console URL download utility supporting HTTP, FTP, etc"
HOMEPAGE = "https://www.gnu.org/software/wget/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "gnutls zlib libpcre"

INC_PR = "r16"

inherit autotools gettext texinfo update-alternatives pkgconfig

EXTRA_OECONF = "--enable-ipv6 --with-ssl=gnutls --disable-rpath --disable-iri \
                --without-libgnutls-prefix ac_cv_header_uuid_uuid_h=no"

ALTERNATIVE_${PN} = "wget"
ALTERNATIVE_${PN}_class-nativesdk = ""
ALTERNATIVE_PRIORITY = "100"

RRECOMMENDS_${PN} += "ca-certificates"

BBCLASSEXTEND += "nativesdk"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libuuid] = "--with-libuuid, --without-libuuid,util-linux"
