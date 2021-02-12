DESCRIPTION = "A pure Python 2/3 library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux."
HOMEPAGE = "http://pythonhosted.org/python-periphery/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=fea92e2e010ccb151792c29fadc2db7b"

SRC_URI[md5sum] = "0a5b866443edab0dab62cea56ed96f1e"
SRC_URI[sha256sum] = "391e5bdfe2511aa7369002d4861df795dbb2889426b1b2cc1e6c9d85939da4dd"

inherit pypi setuptools3

PYPI_PACKAGE = "python-periphery"

RDEPENDS_${PN} += "${PYTHON_PN}-mmap \
		${PYTHON_PN}-ctypes \
		${PYTHON_PN}-fcntl"
