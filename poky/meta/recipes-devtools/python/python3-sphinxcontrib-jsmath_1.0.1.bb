DESCRIPTION = "A sphinx extension which renders display math in HTML via JavaScript"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f0064c10bd544bcffccbc67a41c108d3"

SRC_URI[sha256sum] = "a9925e4a4587247ed2191a22df5f6970656cb8ca2bd6284309578f2153e0c4b8"

PYPI_PACKAGE = "sphinxcontrib-jsmath"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
