SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9fe712b1bc27c5c4e9ecd7f31d208900"

SRC_URI[sha256sum] = "c0c41cfe1331cf885f132dd2400e4091f59ae0a571cae14f3db66919a055489b"

inherit pypi python_poetry_core cython

RDEPENDS:${PN} += " \
    python3-ifaddr (>=0.1.7) \
    python3-async-timeout \
"
