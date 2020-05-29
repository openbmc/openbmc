DESCRIPTION = "XMODEM protocol implementation"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[sha256sum] = "089737298f5738eabc43f2519efdc80b402693768f16383f7013b9e6f8f279d7"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
