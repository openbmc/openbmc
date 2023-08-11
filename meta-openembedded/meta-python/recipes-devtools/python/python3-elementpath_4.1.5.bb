DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "c2d6dc524b29ef751ecfc416b0627668119d8812441c555d7471da41d4bacb8d"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-stringold \
"

BBCLASSEXTEND = "native nativesdk"
