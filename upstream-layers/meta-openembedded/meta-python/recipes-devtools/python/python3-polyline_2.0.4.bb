SUMMARY = "A Python implementation of Google's Encoded Polyline Algorithm Format"
HOMEPAGE = "https://github.com/frederickjansen/polyline"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1fb8d1dc685695195bb3c1e48adfef48"

SRC_URI[sha256sum] = "f05ade694522bf1720febebe1672f820f43a13c6a1664751e7769d47e8ca9b1b"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += "python3-six"

BBCLASSEXTEND = "native nativesdk"

