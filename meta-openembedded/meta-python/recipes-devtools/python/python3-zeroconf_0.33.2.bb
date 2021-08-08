SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bb705b228ea4a14ea2728215b780d80"

SRC_URI[sha256sum] = "5a59425d225a1f5fba0196766fccf856d4686f653037108cbc643a76c1a884fd"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-ifaddr \
    ${PYTHON_PN}-asyncio \
"
