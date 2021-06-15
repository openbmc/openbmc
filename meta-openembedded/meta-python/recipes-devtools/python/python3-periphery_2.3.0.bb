DESCRIPTION = "A pure Python 2/3 library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux."
HOMEPAGE = "http://pythonhosted.org/python-periphery/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=fed1784a083509430580a913df680706"

SRC_URI[sha256sum] = "8a8ec019d9b330a6a6f69a7de61d14b4c98b102d76359047c5ce0263e12246a6"

inherit pypi setuptools3

PYPI_PACKAGE = "python-periphery"

RDEPENDS_${PN} += "${PYTHON_PN}-mmap \
		${PYTHON_PN}-ctypes \
		${PYTHON_PN}-fcntl"
