DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=17ed54aa95f4a6cd0d7a4747d14b64d0"

SRC_URI[sha256sum] = "df3e6b70f3192e92623128123ec8dca3067df9cfadd43d59681e210cfb8d4682"

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
