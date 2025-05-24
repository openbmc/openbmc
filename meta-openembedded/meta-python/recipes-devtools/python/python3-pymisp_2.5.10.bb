DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI[sha256sum] = "369595f98783ea60e97fefb2d56c3c7ef60ea9e56ad13aa10ece924db2343252"

inherit python_poetry_core pypi

RDEPENDS:${PN} += " \
    python3-dateutil \
    python3-json \
    python3-jsonschema \
    python3-requests \
    python3-six \
    python3-deprecated \
    python3-wrapt \
"

# Fixes: python3-pymisp requires /bin/bash, but no
# providers found in RDEPENDS:python3-pymisp? [file-rdep]
RDEPENDS:${PN} += "bash"
