DESCRIPTION = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88ae122f3925d8bde8319060f2ddb8e"

inherit pypi setuptools3

SRC_URI = "https://files.pythonhosted.org/packages/e2/ae/0b037584024c1557e537d25482c306cf6327b5a09b6c4b893579292c1c38/importlib_metadata-1.7.0.tar.gz"
S = "${WORKDIR}/importlib_metadata-${PV}"
SRC_URI[md5sum] = "4505ea85600cca1e693a4f8f5dd27ba8"
SRC_URI[sha256sum] = "90bb658cdbbf6d1735b6341ce708fc7024a3e14e99ffdc5783edea9f9b077f83"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "${PYTHON_PN}-zipp ${PYTHON_PN}-pathlib2"
RDEPENDS_${PN}_append_class-target = " python3-misc"
RDEPENDS_${PN}_append_class-nativesdk = " python3-misc"

BBCLASSEXTEND = "native nativesdk"
