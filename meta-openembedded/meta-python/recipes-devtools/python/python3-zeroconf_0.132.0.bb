SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=6517bdc8f2416f27ab725d4702f7aac3"

SRC_URI[sha256sum] = "e2dddb9b8e6a9de3c43f943d8547300e6bd49b2043fd719ae830cfe0f2908a5c"

DEPENDS += "python3-cython-native"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-ifaddr (>=0.1.7) \
    python3-async-timeout \
"
