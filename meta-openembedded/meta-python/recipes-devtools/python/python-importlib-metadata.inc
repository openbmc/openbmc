DESCRIPTION = "Read metadata from Python packages"
HOMEPAGE = "https://pypi.org/project/importlib-metadata/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88ae122f3925d8bde8319060f2ddb8e"

SRC_URI = "https://files.pythonhosted.org/packages/d7/cd/3b1dffa46b19dac269d586b9d45090588a8bd7a5741602a369d45e1bdf65/importlib_metadata-1.5.2.tar.gz"
S = "${WORKDIR}/importlib_metadata-${PV}"
SRC_URI[md5sum] = "35a85a81c7d86605f4f49397f4e7e39c"
SRC_URI[sha256sum] = "dfc83688553a91a786c6c91eeb5f3b1d31f24d71877bbd94ecbf5484e57690a2"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "${PYTHON_PN}-zipp ${PYTHON_PN}-pathlib2"

BBCLASSEXTEND = "native nativesdk"
