DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=44614b6df7cf3c19be69d0a945e29904"

SRC_URI[sha256sum] = "15a7b81a360791c458c55a417418ea136c13378cfdc06a2dcdc12bd2f9cf09c1"

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
                   ${PYTHON_PN}-profile \
                   ${PYTHON_PN}-threading \
                   "
