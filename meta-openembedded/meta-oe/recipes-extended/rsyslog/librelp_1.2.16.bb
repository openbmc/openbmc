SUMMARY = "A reliable logging library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1fb9c10ed9fd6826757615455ca893a9"

DEPENDS = "gmp nettle libidn zlib gnutls"

SRC_URI = "git://github.com/rsyslog/librelp.git;protocol=https \
           file://0001-src-tcp.c-fix-jump-misses-init-error.patch \
           file://0001-src-tcp.c-increase-the-size-of-szHname.patch \
"

SRCREV = "5e849ff060be0c7dce972e194c54fdacfee0adc2"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
