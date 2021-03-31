DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "c7b996c5624926f329f4379fbcffd5787629e08b2f8d7159d23525e9243ba637"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
