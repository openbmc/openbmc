SUMMARY = "msmtp is an SMTP client"
DESCRIPTION = "A sendmail replacement for use in MTAs like mutt"
HOMEPAGE = "https://marlam.de/msmtp/"
SECTION = "console/network"

LICENSE = "GPL-3.0-only"
DEPENDS = "zlib gnutls"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

UPSTREAM_CHECK_URI = "https://marlam.de/msmtp/download/"

SRC_URI = "https://marlam.de/${BPN}/releases/${BP}.tar.xz"
SRC_URI[sha256sum] = "bd6644b1aaab17d61b86647993e3efad860b23c54283b00ddc579c1f5110aa59"

inherit gettext autotools update-alternatives pkgconfig

EXTRA_OECONF += "--without-libsecret --without-libgsasl --without-libidn"

ALTERNATIVE:${PN} = "sendmail"
# /usr/lib/sendmial is required by LSB core test
ALTERNATIVE:${PN}:linuxstdbase = "sendmail usr-lib-sendmail"
ALTERNATIVE_TARGET[sendmail] = "${bindir}/msmtp"
ALTERNATIVE_LINK_NAME[sendmail] = "${sbindir}/sendmail"
ALTERNATIVE_TARGET[usr-lib-sendmail] = "${bindir}/msmtp"
ALTERNATIVE_LINK_NAME[usr-lib-sendmail] = "/usr/lib/sendmail"
ALTERNATIVE_PRIORITY = "100"
