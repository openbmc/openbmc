DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "5b6801b3be94d48d213beb7b8ebad96addb35c95fc6a9c062c80e033b4a32fe8"

PYPI_PACKAGE = "elementpath"
inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
