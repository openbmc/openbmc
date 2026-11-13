SUMMARY = "small helper program to extract X.509 certificates from PKCS#11 tokens"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "openssl"

SRC_URI = "git://git.pengutronix.de/git/extract-cert;protocol=https;branch=master; \
           file://0001-Do-not-use-the-ENGINE-API-with-OpenSSL-4.patch \
           "
SRCREV = "d652b4e8279aef2a85f58676ab472744bafeafc9"


inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"
