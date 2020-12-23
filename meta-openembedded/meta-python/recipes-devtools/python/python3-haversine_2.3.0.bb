SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[md5sum] = "ff2d43a74195ec00c42ccd5da2a3f3de"
SRC_URI[sha256sum] = "72c76855ac25e6ad054c7ed380e95c1a96803185f005dd11f40ccaa9620b551f"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
