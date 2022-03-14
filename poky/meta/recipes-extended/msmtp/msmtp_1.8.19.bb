SUMMARY = "msmtp is an SMTP client"
DESCRIPTION = "A sendmail replacement for use in MTAs like mutt"
HOMEPAGE = "https://marlam.de/msmtp/"
SECTION = "console/network"

LICENSE = "GPLv3"
DEPENDS = "zlib gnutls"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

UPSTREAM_CHECK_URI = "https://marlam.de/msmtp/download/"

SRC_URI = "https://marlam.de/${BPN}/releases/${BP}.tar.xz"
SRC_URI[sha256sum] = "34a1e1981176874dbe4ee66ee0d9103c90989aa4dcdc4861e4de05ce7e44526b"

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
