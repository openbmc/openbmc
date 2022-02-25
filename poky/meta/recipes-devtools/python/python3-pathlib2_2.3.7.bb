DESCRIPTION = "Object-oriented filesystem paths"
HOMEPAGE = "https://github.com/mcmtroffaes/pathlib2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=2dc08586cce3ab91bfa091b655c0e440"

SRC_URI[sha256sum] = "7a4329d67beff9a712e1d3ae147e4e3e108b0bfd284ffdea03a635126c76b3c0"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-six ${PYTHON_PN}-ctypes"

BBCLASSEXTEND = "native nativesdk"
