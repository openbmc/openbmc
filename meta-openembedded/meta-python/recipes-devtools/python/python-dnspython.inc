DESCRIPTION = "DNS toolkit for Python"
HOMEPAGE = "http://www.dnspython.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b9d855ee0d7bea17c8b0994d37985e1e"

SRC_URI[md5sum] = "05d33ffb5d8f35ae0474bb952778a344"
SRC_URI[sha256sum] = "40f563e1f7a7b80dc5a4e76ad75c23da53d62f1e15e6e517293b04e1f84ead7c"

PYPI_PACKAGE_EXT = "zip"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
"
