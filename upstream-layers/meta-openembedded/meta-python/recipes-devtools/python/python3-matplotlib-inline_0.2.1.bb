SUMMARY = "Inline Matplotlib backend for Jupyter"
HOMEPAGE = "https://pypi.org/project/matplotlib-inline/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4692a0eb42ca54892399db2cb35e61e"

SRC_URI[sha256sum] = "e1ee949c340d771fc39e241ea75683deb94762c8fa5f2927ec57c83c4dffa9fe"

PYPI_PACKAGE = "matplotlib_inline"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-traitlets"
