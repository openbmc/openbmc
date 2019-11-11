DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI[md5sum] = "77aef367e57ba7dcc6f9b3d445a960ed"
SRC_URI[sha256sum] = "de67196f6a8916b9c52a84a1c45ea967c53fa9d2b3795b070ad2c1cbc28d79d7"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
