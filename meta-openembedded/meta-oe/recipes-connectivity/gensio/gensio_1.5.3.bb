SUMMARY = "A library to abstract stream I/O like serial port, TCP, telnet, etc"
HOMEPAGE = "https://github.com/cminyard/gensio"
LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=a0fd36908af843bcee10cb6dfc47fa67 \
                    file://COPYING;md5=bae3019b4c6dc4138c217864bd04331f \
                    "

SRCREV = "95ec1ab31ee97411fc37156d12061adcf0331598"
PV = "1.5.3+git${SRCPV}"

SRC_URI = "git://github.com/cminyard/gensio;protocol=https \
           file://0001-filter-Rename-some-variables-to-tr_stdxxx.patch \
           "

S = "${WORKDIR}/git"

inherit autotools

PACKAGECONFIG ??= "openssl tcp-wrappers"

PACKAGECONFIG[openssl] = "--with-openssl=${STAGING_DIR_HOST}${prefix},--without-openssl, openssl"
PACKAGECONFIG[tcp-wrappers] = "--with-tcp-wrappers,--without-tcp-wrappers, tcp-wrappers"
PACKAGECONFIG[swig] = "--with-swig,--without-swig, swig"

EXTRA_OECONF = "--without-python"

RDEPENDS_${PN} += "bash"
