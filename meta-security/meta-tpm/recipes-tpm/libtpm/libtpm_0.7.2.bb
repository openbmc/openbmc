SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "7325acb4777f70419fe10a1d9621c2666e977e73"
SRC_URI = "git://github.com/stefanberger/libtpms.git;branch=stable-0.7.0"

PE = "1"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig perlnative

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

BBCLASSEXTEND = "native"
