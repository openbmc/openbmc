DESCRIPTION = "Object-oriented filesystem paths"
HOMEPAGE = "https://github.com/mcmtroffaes/pathlib2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=2dc08586cce3ab91bfa091b655c0e440"

SRC_URI[sha256sum] = "7d8bcb5555003cdf4a8d2872c538faa3a0f5d20630cb360e518ca3b981795e5f"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-six ${PYTHON_PN}-ctypes"

BBCLASSEXTEND = "native nativesdk"
