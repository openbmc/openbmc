SUMMARY = "A comprehensive HTTP client library, httplib2 supports many features left out of other HTTP libraries."
HOMEPAGE = "https://github.com/httplib2/httplib2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56e5e931172b6164b62dc7c4aba6c8cf"

SRC_URI[sha256sum] = "e404681d2fbcec7506bcb52c503f2b021e95bee0ef7d01e5c221468a2406d8dc"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyparsing \
"
