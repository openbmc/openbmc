SUMMARY = "A library for using PKCS"
DESCRIPTION = "pkcs11-helper is a library that simplifies the interaction with PKCS \
providers for end-user applications using a simple API and optional OpenSSL \
engine. The library allows using multiple PKCS enumerating available token \
certificates, or selecting a certificate directly by serialized id, handling \
card removal and card insert events, handling card ie-insert to a different \
slot, supporting session expiration and much more all using a simple API."

HOMEPAGE = "http://www.opensc-project.org/pkcs11-helper/"
SECTION = "Development/Libraries"

LICENSE = "GPLv2 & BSD"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=30f10d22cfb1ba98ccd714a41ad3311a \
    file://COPYING.GPL;md5=8a71d0475d08eee76d8b6d0c6dbec543 \
    file://COPYING.BSD;md5=66b7a37c3c10483c1fd86007726104d7 \
"
SRC_URI = "git://github.com/OpenSC/${BPN}.git"

S = "${WORKDIR}/git"
# v1.26
SRCREV = "c7a0cfa08ddc75d963a835d3588170af0e5f1115"

UPSTREAM_CHECK_GITTAGREGEX = "pkcs11-helper-(?P<pver>\d+(\.\d+)+)"

DEPENDS = "zlib nettle gnutls gmp openssl nss nspr"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-static"
