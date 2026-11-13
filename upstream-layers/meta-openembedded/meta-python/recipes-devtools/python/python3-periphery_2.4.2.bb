DESCRIPTION = "A pure Python 2/3 library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux."
HOMEPAGE = "https://pythonhosted.org/python-periphery/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=bf8adf497db13d58fece90db6850816c"

SRC_URI[sha256sum] = "9b334a31832ba64f74aa72700ee5fd6452d79d1d1fa6a0b48333bdb716d91575"

inherit pypi setuptools3

PYPI_PACKAGE = "python-periphery"

RDEPENDS:${PN} += "python3-mmap \
		python3-ctypes \
		python3-fcntl"
