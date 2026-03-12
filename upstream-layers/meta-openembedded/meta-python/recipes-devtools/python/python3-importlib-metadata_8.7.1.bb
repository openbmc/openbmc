SUMMARY = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cf73015ea0156450506e8000d1f7fa37"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "importlib_metadata"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "49fef1ae6440c182052f407c8d34a68f72efc36db9ca90dc0113398f2fdde8bb"

S = "${UNPACKDIR}/importlib_metadata-${PV}"

DEPENDS += "python3-setuptools-scm-native python3-coherent-licensed-native"
RDEPENDS:${PN} += "python3-zipp"
RDEPENDS:${PN}:append:class-target = " python3-misc"
RDEPENDS:${PN}:append:class-nativesdk = " python3-misc"

BBCLASSEXTEND = "native nativesdk"
