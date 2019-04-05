SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "9dc915572b51db0714640ba1ddf8cca9c0f24f05"
SRC_URI = "git://github.com/stefanberger/libtpms.git;branch=stable-${PV}"

PE = "1"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

BBCLASSEXTEND = "native"
