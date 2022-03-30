SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/librelp"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp nettle libidn zlib gnutls openssl"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https;branch=stable \
"

SRCREV = "9e749453d51d602d8159717f8a7c27971dcb4c6c"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

CPPFLAGS += "-Wno-error"
