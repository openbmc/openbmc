DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=2e1efdc433498cebd33ec277ad8741e5"

SRC_URI[md5sum] = "5320f41bf96cf22d13af7eb31e1d321f"
SRC_URI[sha256sum] = "6c7bd97f0ea9c7eb1935902f1bc6fc1ec27eba86056c5b938234a7229891d5a2"

RDEPENDS_${PN} += " ${PYTHON_PN}-requests ${PYTHON_PN}-json ${PYTHON_PN}-jsonschema"
