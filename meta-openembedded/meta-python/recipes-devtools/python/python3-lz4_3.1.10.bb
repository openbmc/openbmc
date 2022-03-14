DESCRIPTION = "python bindings for the lz4 compression library by Yann Collet"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6231efa4dd4811e62407314d90a57573"

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-pkgconfig-native \
"

SRC_URI[sha256sum] = "439e575ecfa9ecffcbd63cfed99baefbe422ab9645b1e82278024d8a21d9720b"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
