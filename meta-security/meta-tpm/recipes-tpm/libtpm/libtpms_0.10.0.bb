SUMMARY = "LIBPM - Software TPM Library"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e73f0786a936da3814896df06ad225a9"

SRCREV = "17f253a767f6b5b7813ae33f12bc79c479576cdc"
SRC_URI = "git://github.com/stefanberger/libtpms.git;branch=stable-0.10;protocol=https"

PE = "2"

S = "${WORKDIR}/git"
inherit autotools-brokensep pkgconfig perlnative

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT = "libtpms_project:libtpms"
