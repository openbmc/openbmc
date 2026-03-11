DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dbb7fb7d72da3921202dd7b995d3ecf"

SRC_URI[sha256sum] = "61040ca014769d507ce19d26521a4bf1c64d2bd0776466e45030dbfe181f7062"

PYPI_PACKAGE = "elementpath"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-xml \
    python3-core \
    python3-numbers \
    python3-datetime \
    python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
