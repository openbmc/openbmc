DESCRIPTION = "XStatic support for flask"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=659968f6ebd4b70b6c3190d20b4a924c"

SRC_URI[sha256sum] = "226ea8e97065a9488b59bfe5c94af4c6e2ea70a25052e301fb231a1381490133"

SRC_URI += "file://remove-pip-requires.patch"

PYPI_PACKAGE = "Flask-XStatic"

RDEPENDS:${PN} += "\
    python3-flask \
    python3-xstatic \
    "

inherit pypi setuptools3
