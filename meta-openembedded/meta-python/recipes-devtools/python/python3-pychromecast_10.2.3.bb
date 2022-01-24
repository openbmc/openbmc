SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI[sha256sum] = "ddb86c5acdc13e8bdadd2b7f5738fda36b32c1750548f7b629ca8d178f05e0da"

PYPI_PACKAGE = "PyChromecast"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-zeroconf \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-protobuf \
"
