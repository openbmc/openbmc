DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "b729d9376cc0f76479c9b0cef30cc0d3c9082ccb4507caa0da1a5e4964926960"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
