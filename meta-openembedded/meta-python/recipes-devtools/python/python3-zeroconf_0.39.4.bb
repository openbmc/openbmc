SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bb705b228ea4a14ea2728215b780d80"

SRC_URI[sha256sum] = "701e4d697f89fe952aa9c13a512ed6bf472dcf4f0a6d275e71085604b3882295"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-ifaddr \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-async-timeout \
"
