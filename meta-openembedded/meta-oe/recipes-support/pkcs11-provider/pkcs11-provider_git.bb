SUMMARY = "An OpenSSL provider that allows direct interfacing with pkcs11 drivers"
DESCRIPTION = "\
This is an Openssl 3.x provider to access Hardware or Software Tokens using \
the PKCS#11 Cryptographic Token Interface\
\
This code targets version 3.1 of the interface but should be backwards \
compatible to previous versions as well.\
"
HOMEPAGE = "https://github.com/latchset/pkcs11-provider"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b53b787444a60266932bd270d1cf2d45"
DEPENDS = "\
    autoconf-archive \
    openssl \
    p11-kit \
"

SRCREV = "e2abc4a7ae33159f2fb21b9d87a05ea9e79ef584"
PV = "0.2+git"

SRC_URI = "git://github.com/latchset/${BPN}.git;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES:${PN} += "${libdir}/ossl-modules/pkcs11.so"
