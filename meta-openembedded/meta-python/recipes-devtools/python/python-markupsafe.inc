DESCRIPTION = "Implements a XML/HTML/XHTML Markup safe string for Python"
HOMEPAGE = "http://github.com/mitsuhiko/markupsafe"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6d1adcf45d69359f256c1cea3254127"

SRC_URI[md5sum] = "2fcedc9284d50e577b5192e8e3578355"
SRC_URI[sha256sum] = "a6be69091dac236ea9c6bc7d012beab42010fa914c459791d627dad4910eb665"

PYPI_PACKAGE = "MarkupSafe"
inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-stringold"
