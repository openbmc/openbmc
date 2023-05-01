SUMMARY = "Rich is a Python library for rich text and beautiful formatting in the terminal"
DESCRIPTION = "The Rich API makes it easy to add color and style to terminal output. \
Rich can also render pretty tables, progress bars, markdown, syntax highlighted source code, \
tracebacks, and more."
HOMEPAGE="https://github.com/Textualize/rich"
SECTION = "devel/python"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b5f0b94fbc94f5ad9ae4efcf8a778303"

SRC_URI[sha256sum] = "dc84400a9d842b3a9c5ff74addd8eb798d155f36c1c91303888e0a66850d2a15"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "${PYTHON_PN}-pygments"
