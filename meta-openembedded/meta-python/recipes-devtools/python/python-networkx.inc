DESCRIPTION = "Python package for creating and manipulating graphs and networks"
LICENSE = "BSD-3-Clause"

inherit pypi

PYPI_PACKAGE_EXT = "zip"

RDEPENDS_${PN} += "\
                   ${PYTHON_PN}-decorator \
                   "
