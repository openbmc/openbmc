SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/librelp"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp nettle libidn zlib gnutls openssl"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https \
"

SRCREV = "e96443dda3c080fa991decec26bc4ac98d24b9a2"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

CPPFLAGS += "-Wno-error"
