SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI += "file://0001-Allow-newer-version-of-wheel-and-setuptools.patch"
SRC_URI[sha256sum] = "dc4f8b052e760a978da9b2568b79020cb79af608a026465f07544cffe43973cf"

PYPI_PACKAGE = "PyChromecast"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-zeroconf (>=0.131.0) \
    python3-protobuf (>=4.25.2) \
    python3-casttube (>=0.2.1) \
"
