DESCRIPTION = "python bindings for the lz4 compression library by Yann Collet"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6231efa4dd4811e62407314d90a57573"

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-pkgconfig-native \
"

SRC_URI[md5sum] = "1bf913acec3cb63893f522c222c8e3b1"
SRC_URI[sha256sum] = "debe75513db3eb9e5cdcd82a329ff38374b6316ab65b848b571e0404746c1e05"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
