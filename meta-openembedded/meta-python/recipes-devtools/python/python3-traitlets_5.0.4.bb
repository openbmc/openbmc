SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
AUTHOR = "IPython Development Team <ipython-dev@scipy.org>"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=eec4de4d599518742e54e75954e33b46"

PYPI_PACKAGE = "traitlets"

SRC_URI[md5sum] = "592dde72b6b706d09ea624cce9af3cf5"
SRC_URI[sha256sum] = "86c9351f94f95de9db8a04ad8e892da299a088a64fd283f9f6f18770ae5eae1b"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"

inherit setuptools3 pypi
