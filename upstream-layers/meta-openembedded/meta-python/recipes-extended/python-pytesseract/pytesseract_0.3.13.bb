SUMMARY = "Python-tesseract is an optical character recognition (OCR) tool for python. That is, it will recognize and "read" the text embedded in images."

HOMEPAGE = "https://github.com/madmaze/pytesseract"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "4bf5f880c99406f52a3cfc2633e42d9dc67615e69d8a509d74867d3baddb5db9"

RDEPENDS:${PN}:append = " python3-packaging tesseract"

PYPI_PACKAGE = "pytesseract"

inherit pypi setuptools3 

BBCLASSEXTEND = "native"
