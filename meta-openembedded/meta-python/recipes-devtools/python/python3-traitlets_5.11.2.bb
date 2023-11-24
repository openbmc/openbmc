SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13bed0ee6f46a6f6dbf1f9f9572f250a"

SRC_URI[sha256sum] = "7564b5bf8d38c40fa45498072bf4dc5e8346eb087bbf1e2ae2d8774f6a0f078e"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"
