SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=eec4de4d599518742e54e75954e33b46"

PYPI_PACKAGE = "traitlets"

SRC_URI[sha256sum] = "059f456c5a7c1c82b98c2e8c799f39c9b8128f6d0d46941ee118daace9eb70c7"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"

inherit pypi python_setuptools_build_meta
