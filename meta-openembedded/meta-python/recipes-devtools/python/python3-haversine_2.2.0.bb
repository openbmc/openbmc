SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[md5sum] = "6b1badeb63aac6214c978d07a4ecd171"
SRC_URI[sha256sum] = "b710aaf32c442a6d04aa89678be55e3f6c11f9752fc01c216e89b13120b36269"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
