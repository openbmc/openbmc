DESCRIPTION = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "a7a22e05929290a67401440b39690ae6563279bced5f314609d9d03798f56766"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

DEPENDS += "${PYTHON_PN}-toml-native"

RDEPENDS:${PN} += "${PYTHON_PN}-compression \
                   ${PYTHON_PN}-math \
                   ${PYTHON_PN}-more-itertools"

BBCLASSEXTEND = "native nativesdk"
