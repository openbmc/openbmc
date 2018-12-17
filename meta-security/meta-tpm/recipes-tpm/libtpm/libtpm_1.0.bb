SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "4111bd1bcf721e6e7b5f11ed9c2b93083677aa25"
SRC_URI = "git://github.com/stefanberger/libtpms.git"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

PV = "1.0+git${SRCPV}"

BBCLASSEXTEND = "native"
