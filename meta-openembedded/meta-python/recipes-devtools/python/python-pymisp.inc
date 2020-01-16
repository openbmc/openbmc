DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI[md5sum] = "57184785340e25469e4a80ff9ddce09e"
SRC_URI[sha256sum] = "594ea0a9e150052232425009eac6dd104a80f494d0e273cc48dd114d7ea8482b"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
