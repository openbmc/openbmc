SUMMARY = "ECDSA cryptographic signature library (pure python)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ffc5e30f76cbb5358fe54b645e5a1d"

DEPENDS += "python3-pip python3-pbr"

PYPI_PACKAGE = "ecdsa"
SRC_URI[sha256sum] = "cfc046a2ddd425adbd1a78b3c46f0d1325c657811c0f45ecc3a0a6236c1e50ff"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-six python3-gmpy2 python3-pbr"
