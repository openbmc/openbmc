SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=eec4de4d599518742e54e75954e33b46"

PYPI_PACKAGE = "traitlets"

SRC_URI[md5sum] = "2ffe54aee5d0d87890127dd28ce3f6c4"
SRC_URI[sha256sum] = "178f4ce988f69189f7e523337a3e11d91c786ded9360174a3d9ca83e79bc5396"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"

inherit setuptools3 pypi
