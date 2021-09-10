SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bb705b228ea4a14ea2728215b780d80"

SRC_URI[sha256sum] = "549f685a318c06d5345ed6533f54f33b206816b564ed7f7876e7b9522e77bf29"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-ifaddr \
    ${PYTHON_PN}-asyncio \
"
