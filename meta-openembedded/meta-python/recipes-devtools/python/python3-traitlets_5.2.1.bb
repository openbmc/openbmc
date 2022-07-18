SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=9c125dfc5ff5364d40b5f56f02cd9de3"

SRC_URI[sha256sum] = "a415578cde1985f1b773faefe49e9f078d345f38665ce3e9e914ec7b41150ce9"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"
