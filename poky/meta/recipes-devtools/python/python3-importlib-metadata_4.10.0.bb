DESCRIPTION = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88ae122f3925d8bde8319060f2ddb8e"

inherit pypi setuptools3

PYPI_PACKAGE = "importlib_metadata"
UPSTREAM_CHECK_REGEX = "/importlib-metadata/(?P<pver>(\d+[\.\-_]*)+)/"

SRC_URI[sha256sum] = "92a8b58ce734b2a4494878e0ecf7d79ccd7a128b5fc6014c401e0b61f006f0f6"

S = "${WORKDIR}/importlib_metadata-${PV}"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native ${PYTHON_PN}-toml-native"
RDEPENDS:${PN} += "${PYTHON_PN}-zipp ${PYTHON_PN}-pathlib2"
RDEPENDS:${PN}:append:class-target = " python3-misc"
RDEPENDS:${PN}:append:class-nativesdk = " python3-misc"

BBCLASSEXTEND = "native nativesdk"
