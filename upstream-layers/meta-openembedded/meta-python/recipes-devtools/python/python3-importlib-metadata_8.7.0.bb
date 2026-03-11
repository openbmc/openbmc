SUMMARY = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "importlib_metadata"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "d13b81ad223b890aa16c5471f2ac3056cf76c5f10f82d6f9292f0b415f389000"

S = "${UNPACKDIR}/importlib_metadata-${PV}"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-zipp"
RDEPENDS:${PN}:append:class-target = " python3-misc"
RDEPENDS:${PN}:append:class-nativesdk = " python3-misc"

BBCLASSEXTEND = "native nativesdk"
