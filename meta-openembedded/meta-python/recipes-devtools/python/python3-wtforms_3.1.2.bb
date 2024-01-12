DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=17ed54aa95f4a6cd0d7a4747d14b64d0"

SRC_URI[sha256sum] = "f8d76180d7239c94c6322f7990ae1216dae3659b7aa1cee94b6318bdffb474b9"

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
