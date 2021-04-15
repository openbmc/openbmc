DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI = "git://github.com/MISP/PyMISP.git;protocol=https;branch=main"
SRCREV = "62cd5173f087e88834e88472060181b681b0b4d2"
S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"

# Fixes: python3-pymisp requires /bin/bash, but no
# providers found in RDEPENDS_python3-pymisp? [file-rdep]
RDEPENDS_${PN} += "bash"
