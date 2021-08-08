SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[sha256sum] = "75a7f859b3fb6df746564ca66ad1fd5b4052cdbab3d74ff16e8f1a7c3d4a26a5"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
