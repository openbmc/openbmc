DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "3a27aaf3399929fccda013899cb76d3ff111734abf4281e5f9d3721ba0b9ffa3"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
