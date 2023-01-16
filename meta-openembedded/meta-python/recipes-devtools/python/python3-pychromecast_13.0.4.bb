SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI[sha256sum] = "792fba3f31e4968a5e99c19c771774083a2aa7e897ebf6f5e218e308e33a461f"

PYPI_PACKAGE = "PyChromecast"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-zeroconf \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-protobuf \
    ${PYTHON_PN}-compression \
"
