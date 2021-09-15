SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=eec4de4d599518742e54e75954e33b46"

PYPI_PACKAGE = "traitlets"

SRC_URI[sha256sum] = "bd382d7ea181fbbcce157c133db9a829ce06edffe097bcf3ab945b435452b46d"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"

inherit setuptools3 pypi
