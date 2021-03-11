SUMMARY = "YAML/JSON validation library"
DESCRIPTION = "pykwalify is a schema validator for YAML and JSON"
HOMEPAGE = "https://pypi.org/project/pykwalify/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a72ea5159364a2cd7f45c6dcbee37872"

SRC_URI[md5sum] = "58357f1d0f77de976e73dbd3660af75b"
SRC_URI[sha256sum] = "7e8b39c5a3a10bc176682b3bd9a7422c39ca247482df198b402e8015defcceb2"

SRC_URI += "file://0001-rule.py-fix-missing-comma.patch"

PYPI_PACKAGE = "pykwalify"

inherit setuptools3 pypi

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-pyyaml \
"

BBCLASSEXTEND = "native"
