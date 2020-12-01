DESCRIPTION = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88ae122f3925d8bde8319060f2ddb8e"

inherit pypi setuptools3

SRC_URI = "https://files.pythonhosted.org/packages/7d/d4/dbc58eed92be61bae65a7d80a7604d35bf6ded3e3c53c14f2d45b4a28831/importlib_metadata-3.1.0.tar.gz"
SRC_URI[sha256sum] = "d9b8a46a0885337627a6430db287176970fff18ad421becec1d64cfc763c2099"

S = "${WORKDIR}/importlib_metadata-${PV}"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "${PYTHON_PN}-zipp ${PYTHON_PN}-pathlib2"
RDEPENDS_${PN}_append_class-target = " python3-misc"
RDEPENDS_${PN}_append_class-nativesdk = " python3-misc"

BBCLASSEXTEND = "native nativesdk"
