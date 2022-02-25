SUMMARY = "Various helpers to pass trusted data to untrusted environments and back."
HOMEPAGE = "http://github.com/mitsuhiko/itsdangerous"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=4cda9a0ebd516714f360b0e9418cfb37"

SRC_URI[sha256sum] = "d848fcb8bc7d507c4546b448574e8a44fc4ea2ba84ebf8d783290d53e81992f5"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-simplejson \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-json \
"
