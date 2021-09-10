SUMMARY = "ECDSA cryptographic signature library (pure python)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66ffc5e30f76cbb5358fe54b645e5a1d"

DEPENDS += "python3-pip python3-pbr"

PYPI_PACKAGE = "ecdsa"
SRC_URI[sha256sum] = "b9f500bb439e4153d0330610f5d26baaf18d17b8ced1bc54410d189385ea68aa"

inherit pypi setuptools3 python3native

RDEPENDS:${PN} += "python3-six python3-gmpy2 python3-pbr"

BBCLASSEXTEND = "native nativesdk"
