SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bb705b228ea4a14ea2728215b780d80"

SRC_URI[sha256sum] = "10c501b25d8881b656e56c34674d98fe6bc752240a572e74f918bc849c93ba9c"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-ifaddr \
    ${PYTHON_PN}-asyncio \
"
