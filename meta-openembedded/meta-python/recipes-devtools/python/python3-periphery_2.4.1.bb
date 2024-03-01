DESCRIPTION = "A pure Python 2/3 library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux."
HOMEPAGE = "http://pythonhosted.org/python-periphery/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=30fe6f023a80fb33989fb3b9d773fea0"

SRC_URI[sha256sum] = "61d461d736982a6f766e878720ab10a68151e2e8c1086600d9389ac47e40e88a"

inherit pypi setuptools3

PYPI_PACKAGE = "python-periphery"

RDEPENDS:${PN} += "python3-mmap \
		python3-ctypes \
		python3-fcntl"
