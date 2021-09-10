DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5cfa4d8f5881e0d053bb497130b9c73a"

SRC_URI[sha256sum] = "2306f1950ce772c5a59a57f5486d59bb9cab98497c45fc49cbc45ac0dec119bb"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
                   ${PYTHON_PN}-decorator \
                   ${PYTHON_PN}-netclient \
                   ${PYTHON_PN}-compression \
                   ${PYTHON_PN}-numbers \
                   ${PYTHON_PN}-pickle \
                   ${PYTHON_PN}-html \
                   ${PYTHON_PN}-xml \
                   ${PYTHON_PN}-json \
                   "
