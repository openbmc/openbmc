DESCRIPTION = "python bindings for the lz4 compression library by Yann Collet"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6231efa4dd4811e62407314d90a57573"

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-pkgconfig-native \
"
SRC_URI[sha256sum] = "9c9f6a8b71c18c24bd83537a4d616f0301623a5e98db7c7ca956d608e1bcd4c7"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
