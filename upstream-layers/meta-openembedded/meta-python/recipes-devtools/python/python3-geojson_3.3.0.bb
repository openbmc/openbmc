SUMMARY = "Python bindings and utilities for GeoJSON"
HOMEPAGE = "https://pypi.org/project/geojson/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=127b4a863485faf7c57d12dc2c1f60b3"

SRC_URI[sha256sum] = "92e83b9cb378a450b42f1207bb9b2a031f9fc89185f335153c44369b8b8b71fd"

inherit pypi setuptools3 ptest-python-pytest

RDEPENDS:${PN} += "python3-simplejson python3-math"

BBCLASSEXTEND = "native nativesdk"
