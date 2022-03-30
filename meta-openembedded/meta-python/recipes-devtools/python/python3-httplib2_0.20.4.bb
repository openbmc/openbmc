SUMMARY = "A comprehensive HTTP client library, httplib2 supports many features left out of other HTTP libraries."
HOMEPAGE = "https://github.com/httplib2/httplib2"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56e5e931172b6164b62dc7c4aba6c8cf"

SRC_URI[sha256sum] = "58a98e45b4b1a48273073f905d2961666ecf0fbac4250ea5b47aef259eb5c585"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyparsing \
"
