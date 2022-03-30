SUMMARY = "Various helpers to pass trusted data to untrusted environments and back."
HOMEPAGE = "http://github.com/mitsuhiko/itsdangerous"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=4cda9a0ebd516714f360b0e9418cfb37"

SRC_URI[sha256sum] = "7b7d3023cd35d9cb0c1fd91392f8c95c6fa02c59bf8ad64b8849be3401b95afb"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-simplejson \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-json \
"
