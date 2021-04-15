DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a24ea029adac8935699bf69b2e38c728"

SRC_URI[sha256sum] = "109cd585cac41297f71103c3c42ac6ef7379f29788eb54cb751be5a663bb235a"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
                   ${PYTHON_PN}-decorator \
                   "
