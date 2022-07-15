DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "b8aeb6f27dddc10fb9201b62090628a846cbae8577f3544cb1075fa38d0817f6"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
