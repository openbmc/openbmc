SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=943eb67718222db21d44a4ef1836675f"

PYPI_PACKAGE = "Mako"

inherit pypi setuptools3

SRC_URI[sha256sum] = "169fa52af22a91900d852e937400e79f535496191c63712e3b9fda5a9bed6fc3"

RDEPENDS:${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
