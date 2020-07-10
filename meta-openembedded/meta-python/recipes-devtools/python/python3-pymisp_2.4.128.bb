DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI[md5sum] = "a90a25def591c6a265bc904f801646f1"
SRC_URI[sha256sum] = "714b8b5c8debb4b5e0e602a5fe8a599206e89c1313244ed0e69d9e0bc816f1f7"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"

# Fixes: python3-pymisp requires /bin/bash, but no
# providers found in RDEPENDS_python3-pymisp? [file-rdep
RDEPENDS_${PN} += "bash"
