SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=9c125dfc5ff5364d40b5f56f02cd9de3"

SRC_URI[sha256sum] = "3f2c4e435e271592fe4390f1746ea56836e3a080f84e7833f0f801d9613fec39"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"
