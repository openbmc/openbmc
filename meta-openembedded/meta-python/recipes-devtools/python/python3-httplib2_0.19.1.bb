SUMMARY = "A comprehensive HTTP client library, httplib2 supports many features left out of other HTTP libraries."
HOMEPAGE = "https://github.com/httplib2/httplib2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56e5e931172b6164b62dc7c4aba6c8cf"

SRC_URI[sha256sum] = "0b12617eeca7433d4c396a100eaecfa4b08ee99aa881e6df6e257a7aad5d533d"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyparsing \
"
