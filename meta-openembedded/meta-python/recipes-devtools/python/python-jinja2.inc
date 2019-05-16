DESCRIPTION = "Python Jinja2: A small but fast and easy to use stand-alone template engine written in pure python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20c831f91dd3bd486020f672ba2be386"

PYPI_PACKAGE = "Jinja2"

SRC_URI[md5sum] = "0ae535be40fd215a8114a090c8b68e5a"
SRC_URI[sha256sum] = "065c4f02ebe7f7cf559e49ee5a95fb800a9e4528727aec6f24402a5374c65013"

RDEPENDS_${PN} += "${PYTHON_PN}-io ${PYTHON_PN}-pickle ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-math ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pprint ${PYTHON_PN}-shell ${PYTHON_PN}-markupsafe \
    ${PYTHON_PN}-json ${PYTHON_PN}-threading ${PYTHON_PN}-numbers"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native nativesdk"
