SUMMARY = "ECDSA cryptographic signature library (pure python)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ffc5e30f76cbb5358fe54b645e5a1d"

PYPI_PACKAGE = "ecdsa"
SRC_URI[sha256sum] = "190348041559e21b22a1d65cee485282ca11a6f81d503fddb84d5017e9ed1e49"

inherit pypi setuptools3 python3native

RDEPENDS:${PN} += "python3-six python3-gmpy2"

BBCLASSEXTEND = "native nativesdk"

do_install:append() {
    rm ${D}${PYTHON_SITEPACKAGES_DIR}/ecdsa/test_*.py
}
