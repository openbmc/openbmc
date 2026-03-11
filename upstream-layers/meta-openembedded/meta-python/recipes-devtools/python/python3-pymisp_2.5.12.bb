DESCRIPTION = "Python API for MISP"
HOMEPAGE = "https://github.com/MISP/PyMISP"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3639cf5780f71b125d3e9d1dc127c20"

SRC_URI[sha256sum] = "75dd505d96e1d67b9bfec7a5e1aea3b967426da348e69bdbaa1e554821e10fd6"

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
