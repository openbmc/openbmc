SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[sha256sum] = "357e41dfddc4a0f2b1c941d92a590cac840f7ce4b3da14b45b68d968b3ad7cc7"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
