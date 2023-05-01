DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4266362445d56549f7b8973d02e5f22a"

SRC_URI[sha256sum] = "de346335408f84de0eada6ff9fafafff9bcda11f0a0dfaa931133debb146ab61"

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
