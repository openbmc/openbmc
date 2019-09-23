DESCRIPTION = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88ae122f3925d8bde8319060f2ddb8e"

SRC_URI = "https://files.pythonhosted.org/packages/5d/44/636bcd15697791943e2dedda0dbe098d8530a38d113b202817133e0b06c0/importlib_metadata-0.23.tar.gz"
S = "${WORKDIR}/importlib_metadata-${PV}"
SRC_URI[md5sum] = "80d677d744995336c9c22d21a85ddeb8"
SRC_URI[sha256sum] = "aa18d7378b00b40847790e7c27e11673d7fed219354109d0e7b9e5b25dc3ad26"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "${PYTHON_PN}-zipp ${PYTHON_PN}-pathlib2"
