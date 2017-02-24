SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp nettle libidn zlib gnutls"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https"

SRCREV = "02c3be4f5c39fec59d05cd8b75b08dbba04098ad"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
