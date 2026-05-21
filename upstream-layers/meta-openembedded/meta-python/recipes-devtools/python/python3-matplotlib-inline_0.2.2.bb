SUMMARY = "Inline Matplotlib backend for Jupyter"
HOMEPAGE = "https://pypi.org/project/matplotlib-inline/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4692a0eb42ca54892399db2cb35e61e"

SRC_URI[sha256sum] = "72f3fe8fce36b70d4a5b612f899090cd0401deddc4ea90e1572b9f4bfb058c79"

PYPI_PACKAGE = "matplotlib_inline"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-traitlets"
