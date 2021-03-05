DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "326643aca7beff0864d022cd591736b8550a70fccafd67a89198166baed1c2c4"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
