SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "3f8fbc831b7bc3a6cc8422c432f577596b4cf3df"
SRC_URI = "git://github.com/stefanberger/libtpms.git;branch=stable-0.9;protocol=https"

PE = "1"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig perlnative

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

BBCLASSEXTEND = "native"
