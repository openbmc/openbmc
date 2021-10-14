SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "f6dd8f55eab4910131ec6a6a570dcd7951bd10e4"
SRC_URI = "git://github.com/stefanberger/libtpms.git;branch=stable-0.8"

PE = "1"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig perlnative

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

BBCLASSEXTEND = "native"
