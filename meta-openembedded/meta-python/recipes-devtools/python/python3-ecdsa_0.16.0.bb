SUMMARY = "ECDSA cryptographic signature library (pure python)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ffc5e30f76cbb5358fe54b645e5a1d"

DEPENDS += "python3-pip python3-pbr"

PYPI_PACKAGE = "ecdsa"
SRC_URI[md5sum] = "d593df1ab57da611dca76f7328c47b21"
SRC_URI[sha256sum] = "494c6a853e9ed2e9be33d160b41d47afc50a6629b993d2b9c5ad7bb226add892"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-six python3-gmpy2 python3-pbr"
