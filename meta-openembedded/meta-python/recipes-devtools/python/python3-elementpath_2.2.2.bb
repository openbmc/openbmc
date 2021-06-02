DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "68de07c1aec3b1d33126111252cc699600dd1e45802625344aa6eb9e680ef157"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
