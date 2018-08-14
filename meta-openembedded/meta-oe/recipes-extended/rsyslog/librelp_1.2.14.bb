SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp nettle libidn zlib gnutls"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https"

SRCREV = "fc512e337bfc7c92770246dbff5f482b879498b9"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
