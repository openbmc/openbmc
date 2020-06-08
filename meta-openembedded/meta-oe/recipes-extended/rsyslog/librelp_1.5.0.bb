SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/librelp"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp nettle libidn zlib gnutls openssl"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https \
"

SRCREV = "0beb2258e12e4131dc31e261078ea53d18f787d7"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

CPPFLAGS += "-Wno-error"
