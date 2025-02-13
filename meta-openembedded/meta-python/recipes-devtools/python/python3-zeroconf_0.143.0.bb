SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9fe712b1bc27c5c4e9ecd7f31d208900"

SRC_URI[sha256sum] = "35f9390e4b862789f2d62da864bbf6ac26ad1d6dc8cf1e64a4d3554c85a05002"

inherit pypi python_poetry_core cython

RDEPENDS:${PN} += " \
    python3-ifaddr (>=0.1.7) \
    python3-async-timeout \
"
