DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=17ed54aa95f4a6cd0d7a4747d14b64d0"

SRC_URI[sha256sum] = "5e51df8af9a60f6beead75efa10975e97768825a82146a65c7cbf5b915990620"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/WTForms"
UPSTREAM_CHECK_REGEX = "/WTForms/(?P<pver>(\d+[\.\-_]*)+)"

inherit pypi python_hatchling

DEPENDS += "\
    ${PYTHON_PN}-pip-native \
    ${PYTHON_PN}-babel-native \
    "

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-markupsafe \
    "
