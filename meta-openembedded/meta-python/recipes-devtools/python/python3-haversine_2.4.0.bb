SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[sha256sum] = "6dcdee48b854b7bd0a121a04a2cb775dc7d9e2354f1e22d29ff62110189305b0"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
