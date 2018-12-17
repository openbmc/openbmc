DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI[md5sum] = "6d549b36cbfa8c1a3323c5d6e299789e"
SRC_URI[sha256sum] = "f4ed2000cbe1a314f1f88fd8a8eba4ad05974b351b96f1235c6e532158a39fe0"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
