SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=9c125dfc5ff5364d40b5f56f02cd9de3"

PYPI_PACKAGE = "traitlets"

SRC_URI[sha256sum] = "60474f39bf1d39a11e0233090b99af3acee93bbc2281777e61dd8c87da8a0014"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"

inherit pypi python_setuptools_build_meta
