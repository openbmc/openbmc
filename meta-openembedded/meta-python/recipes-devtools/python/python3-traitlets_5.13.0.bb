SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13bed0ee6f46a6f6dbf1f9f9572f250a"

SRC_URI[sha256sum] = "9b232b9430c8f57288c1024b34a8f0251ddcc47268927367a0dd3eeaca40deb5"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"
