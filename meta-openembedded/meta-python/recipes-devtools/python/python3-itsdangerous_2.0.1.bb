SUMMARY = "Various helpers to pass trusted data to untrusted environments and back."
HOMEPAGE = "http://github.com/mitsuhiko/itsdangerous"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=4cda9a0ebd516714f360b0e9418cfb37"

SRC_URI[sha256sum] = "9e724d68fc22902a1435351f84c3fb8623f303fffcc566a4cb952df8c572cff0"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-simplejson \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-json \
"
