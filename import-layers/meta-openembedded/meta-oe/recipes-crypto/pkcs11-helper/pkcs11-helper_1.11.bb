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
    file://COPYING;md5=4948810631bcac142af53d32df5b6ee1 \
    file://COPYING.GPL;md5=8a71d0475d08eee76d8b6d0c6dbec543 \
    file://COPYING.BSD;md5=f79f90ea7a106796af80b5d05f1f8da1 \
"
SRC_URI = "git://github.com/OpenSC/${BPN}.git"
SRC_URI[md5sum] = "9f62af9f475901b89355266141306673"
SRC_URI[sha256sum] = "494ec59c93e7c56c528f335d9353849e2e7c94a6b1b41c89604694e738113386"

S = "${WORKDIR}/git"
SRCREV = "e7adf8f35be232a4f04c53b4ac409be52792093e"

DEPENDS = "zlib nettle gnutls gmp openssl nss nspr"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-static"
