DESCRIPTION = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[md5sum] = "87a53f0bf48671f6bf6d3e9938218509"
SRC_URI[sha256sum] = "64ad89efee774d1897a58607895d80789c59778ea02185dd846ac38394a8642b"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-toml-native"

RDEPENDS_${PN} += "${PYTHON_PN}-compression \
                   ${PYTHON_PN}-math \
                   ${PYTHON_PN}-more-itertools"

BBCLASSEXTEND = "native nativesdk"
