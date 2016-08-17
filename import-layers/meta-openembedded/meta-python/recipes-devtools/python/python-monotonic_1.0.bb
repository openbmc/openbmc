SUMMARY = "An implementation of time.monotonic() for Python 2.0 through 3.2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[md5sum] = "a692bff2c5e07dde22ad2128d818d15c"
SRC_URI[sha256sum] = "47d7d045b3f2a08bffe683d761ef7f9131a2598db1cec7532a06720656cf719d"

inherit pypi setuptools

RDEPENDS_${PN} += "${PYTHON_PN}-ctypes ${PYTHON_PN}-re"
