DESCRIPTION = "Python Jinja2: A small but fast and easy to use stand-alone template engine written in pure python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20c831f91dd3bd486020f672ba2be386"

PYPI_PACKAGE = "Jinja2"

SRC_URI[md5sum] = "6411537324b4dba0956aaa8109f3c77b"
SRC_URI[sha256sum] = "ddaa01a212cd6d641401cb01b605f4a4d9f37bfc93043d7f760ec70fb99ff9ff"

RDEPENDS_${PN} += "${PYTHON_PN}-io ${PYTHON_PN}-pickle ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-math ${PYTHON_PN}-netclient ${PYTHON_PN}-re ${PYTHON_PN}-textutils \
    ${PYTHON_PN}-lang ${PYTHON_PN}-pprint ${PYTHON_PN}-shell ${PYTHON_PN}-markupsafe \
    ${PYTHON_PN}-json ${PYTHON_PN}-threading ${PYTHON_PN}-numbers"

CLEANBROKEN = "1"
