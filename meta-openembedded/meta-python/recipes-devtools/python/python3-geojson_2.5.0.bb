SUMMARY = "Python bindings and utilities for GeoJSON"
HOMEPAGE = "https://pypi.org/project/geojson/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=f48972abe5cddee79e301574742ed745"

SRC_URI[md5sum] = "14753ed28678828b1de73f68b04e2324"
SRC_URI[sha256sum] = "6e4bb7ace4226a45d9c8c8b1348b3fc43540658359f93c3f7e03efa9f15f658a"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-simplejson python3-math"

BBCLASSEXTEND = "native nativesdk"
