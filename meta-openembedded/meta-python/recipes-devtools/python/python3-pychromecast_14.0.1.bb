SUMMARY = "Library for Python 3.6+ to communicate with the Google Chromecast."
HOMEPAGE = "https://github.com/balloob/pychromecast"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1dbd4e85f47b389bdadee9c694669f5"

SRC_URI += "file://0001-Allow-newer-version-of-wheel-and-setuptools.patch"
SRC_URI[sha256sum] = "e16e0a7f9488319191b8b4fa21ca0beb4bf12eeda5c9bf640241188f2bea0a3e"

PYPI_PACKAGE = "PyChromecast"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-zeroconf (>=0.131.0) \
    python3-protobuf (>=4.25.2) \
    python3-casttube (>=0.2.1) \
"
