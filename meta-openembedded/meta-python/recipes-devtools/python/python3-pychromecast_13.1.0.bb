SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI[sha256sum] = "08e61a8b54bd2119d3c9ab1ec0136d3d8563aa97e0a3b57841588b9be60c2676"

PYPI_PACKAGE = "PyChromecast"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-logging \
    python3-zeroconf \
    python3-json \
    python3-requests \
    python3-protobuf \
    python3-compression \
    python3-casttube \
"
