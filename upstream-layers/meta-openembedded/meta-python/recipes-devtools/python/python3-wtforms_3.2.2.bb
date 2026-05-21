DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=17ed54aa95f4a6cd0d7a4747d14b64d0"

SRC_URI[sha256sum] = "7b00c73f8670f35d4edb0293dcd81b980528bee72fd662b182aaba27ae570b93"

inherit pypi python_hatchling

DEPENDS += "\
    python3-pip-native \
    python3-babel-native \
    "

RDEPENDS:${PN} += "\
    python3-netserver \
    python3-numbers \
    python3-markupsafe \
    "
