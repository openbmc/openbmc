SUMMARY = "A library for using PKCS"
DESCRIPTION = "pkcs11-helper is a library that simplifies the interaction with PKCS \
providers for end-user applications using a simple API and optional OpenSSL \
engine. The library allows using multiple PKCS enumerating available token \
certificates, or selecting a certificate directly by serialized id, handling \
card removal and card insert events, handling card ie-insert to a different \
slot, supporting session expiration and much more all using a simple API."

HOMEPAGE = "https://github.com/OpenSC/pkcs11-helper"
SECTION = "Development/Libraries"

LICENSE = "GPL-2.0-only & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=008a5b65f5d167bd0f646530ad3d3293 \
                    file://COPYING.GPL;md5=8a71d0475d08eee76d8b6d0c6dbec543 \
                    file://COPYING.BSD;md5=66b7a37c3c10483c1fd86007726104d7 \
                    "
SRC_URI = "git://github.com/OpenSC/${BPN}.git;branch=master;protocol=https"

# master
SRCREV = "286b4415b95f298ab35d62508a37c2aefc055037"
PV .= "+1.30.0+git"

UPSTREAM_CHECK_GITTAGREGEX = "pkcs11-helper-(?P<pver>\d+(\.\d+)+)"

DEPENDS = "zlib nettle gnutls gmp openssl nss nspr"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-static"
