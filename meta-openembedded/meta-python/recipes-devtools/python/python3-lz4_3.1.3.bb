DESCRIPTION = "python bindings for the lz4 compression library by Yann Collet"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6231efa4dd4811e62407314d90a57573"

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-pkgconfig-native \
"

SRC_URI[sha256sum] = "081ef0a3b5941cb03127f314229a1c78bd70c9c220bb3f4dd80033e707feaa18"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
