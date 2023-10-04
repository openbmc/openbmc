SUMMARY = "Traitlets Python config system"
HOMEPAGE = "http://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13bed0ee6f46a6f6dbf1f9f9572f250a"

SRC_URI[sha256sum] = "db9c4aa58139c3ba850101913915c042bdba86f7c8a0dda1c6f7f92c5da8e542"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ipython-genutils \
    ${PYTHON_PN}-decorator \
"
