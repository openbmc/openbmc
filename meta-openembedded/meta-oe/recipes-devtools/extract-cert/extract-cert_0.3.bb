SUMMARY = "small helper program to extract X.509 certificates from PKCS#11 tokens"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "openssl"

SRC_URI = "git://git.pengutronix.de/git/extract-cert;protocol=https;branch=master;"
SRCREV = "d652b4e8279aef2a85f58676ab472744bafeafc9"

S = "${WORKDIR}/git"

inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"
