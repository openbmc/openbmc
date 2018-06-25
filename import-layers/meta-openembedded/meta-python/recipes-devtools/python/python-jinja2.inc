DESCRIPTION = "Python Jinja2: A small but fast and easy to use stand-alone template engine written in pure python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20c831f91dd3bd486020f672ba2be386"

PYPI_PACKAGE = "Jinja2"

SRC_URI[md5sum] = "61ef1117f945486472850819b8d1eb3d"
SRC_URI[sha256sum] = "f84be1bb0040caca4cea721fcbbbbd61f9be9464ca236387158b0feea01914a4"

RDEPENDS_${PN} += "${PYTHON_PN}-io ${PYTHON_PN}-pickle ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-math ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pprint ${PYTHON_PN}-shell ${PYTHON_PN}-markupsafe \
    ${PYTHON_PN}-json ${PYTHON_PN}-threading ${PYTHON_PN}-numbers"

CLEANBROKEN = "1"
