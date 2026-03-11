SUMMARY = "Inline Matplotlib backend for Jupyter"
HOMEPAGE = "https://pypi.org/project/matplotlib-inline/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4692a0eb42ca54892399db2cb35e61e"

SRC_URI[sha256sum] = "8423b23ec666be3d16e16b60bdd8ac4e86e840ebd1dd11a30b9f117f2fa0ab90"

PYPI_PACKAGE = "matplotlib_inline"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-traitlets"
