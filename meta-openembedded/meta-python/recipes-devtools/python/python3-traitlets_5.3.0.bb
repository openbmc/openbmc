SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=9c125dfc5ff5364d40b5f56f02cd9de3"

SRC_URI[sha256sum] = "0bb9f1f9f017aa8ec187d8b1b2a7a6626a2a1d877116baba52a129bfa124f8e2"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"
