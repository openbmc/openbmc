DESCRIPTION = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[md5sum] = "ae81f228995578b840d76d1b7d87fede"
SRC_URI[sha256sum] = "ed5eee1974372595f9e416cc7bbeeb12335201d8081ca8a0743c954d4446e5cb"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-toml-native"

RDEPENDS_${PN} += "${PYTHON_PN}-compression \
                   ${PYTHON_PN}-math \
                   ${PYTHON_PN}-more-itertools"

BBCLASSEXTEND = "native nativesdk"
