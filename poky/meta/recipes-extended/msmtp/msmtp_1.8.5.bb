SUMMARY = "msmtp is an SMTP client"
DESCRIPTION = "A sendmail replacement for use in MTAs like mutt"
HOMEPAGE = "https://marlam.de/msmtp/"
SECTION = "console/network"

LICENSE = "GPLv3"
DEPENDS = "zlib gnutls"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

UPSTREAM_CHECK_URI = "https://marlam.de/msmtp/download/"

SRC_URI = "https://marlam.de/${BPN}/releases/${BP}.tar.xz"
SRC_URI[md5sum] = "5d7bb10606fbceeb2e0687379c75234b"
SRC_URI[sha256sum] = "1613daced9c47b8c028224fc076799c2a4d72923e242be4e9e5c984cbbbb9f39"

inherit gettext autotools update-alternatives pkgconfig

EXTRA_OECONF += "--without-libsecret --without-libgsasl --without-libidn"

ALTERNATIVE_${PN} = "sendmail"
# /usr/lib/sendmial is required by LSB core test
ALTERNATIVE_${PN}_linuxstdbase = "sendmail usr-lib-sendmail"
ALTERNATIVE_TARGET[sendmail] = "${bindir}/msmtp"
ALTERNATIVE_LINK_NAME[sendmail] = "${sbindir}/sendmail"
ALTERNATIVE_TARGET[usr-lib-sendmail] = "${bindir}/msmtp"
ALTERNATIVE_LINK_NAME[usr-lib-sendmail] = "/usr/lib/sendmail"
ALTERNATIVE_PRIORITY = "100"
