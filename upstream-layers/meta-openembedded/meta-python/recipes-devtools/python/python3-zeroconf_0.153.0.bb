SUMMARY = "Pure Python Multicast DNS Service Discovery Library (Bonjour/Avahi compatible)"
HOMEPAGE = "https://github.com/jstasiak/python-zeroconf"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ede793c8b5508eab1acfad7c0c4e335"

SRC_URI[sha256sum] = "28bd0baa208f4ea481a386e9eefdd3a65663fada34233432301a0c221d741d1d"

SRC_URI += "file://run-ptest"

inherit pypi python_poetry_core cython ptest

RDEPENDS:${PN} += " \
    python3-ifaddr (>=0.1.7) \
    python3-async-timeout \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-pytest-asyncio \
    python3-pytest-codspeed \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
