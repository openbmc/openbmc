DESCRIPTION = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "f5812b1e007e48cff63449a5e9f4e7ebea716b4111f9c4f9a645f91d579bf0c4"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-toml-native"

RDEPENDS:${PN} += "${PYTHON_PN}-compression \
                   ${PYTHON_PN}-math \
                   ${PYTHON_PN}-more-itertools"

BBCLASSEXTEND = "native nativesdk"
