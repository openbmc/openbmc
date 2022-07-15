DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=44614b6df7cf3c19be69d0a945e29904"

SRC_URI[sha256sum] = "5e53f027c0d567cf1f884dbb283224df525644e43afd1145d64c9d88a3584762"

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
