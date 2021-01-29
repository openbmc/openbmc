DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[md5sum] = "887f60e9c4fb9b0804a38372b2798101"
SRC_URI[sha256sum] = "23e1fed8d196d9a6cc4d220ef11fbe7eb8cff3a27848621c447e9d96134b2085"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
