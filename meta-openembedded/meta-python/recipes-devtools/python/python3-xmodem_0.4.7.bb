DESCRIPTION = "XMODEM protocol implementation"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI[sha256sum] = "2f1068aa8676f0d1d112498b5786c4f8ea4f89d8f25d07d3a0f293cd21db1c35"

inherit pypi setuptools3

do_install:append() {
    install -d ${D}${docdir}/${PN}
    mv ${D}/usr/doc/* ${D}${docdir}/${PN}/
    rmdir ${D}/usr/doc
}

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
"
BBCLASSEXTEND = "native nativesdk"
