SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "c26e8f7b08b19a69cea9e8f1f1e6639c7951fb01"
SRC_URI = "git://github.com/stefanberger/libtpms.git;branch=stable-${PV}"

PE = "1"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig perlnative

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

BBCLASSEXTEND = "native"
