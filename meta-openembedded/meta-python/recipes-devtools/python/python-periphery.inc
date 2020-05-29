DESCRIPTION = "A pure Python 2/3 library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux."
HOMEPAGE = "http://pythonhosted.org/python-periphery/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://PKG-INFO;md5=1ecf5c2354c22fb5bfd53eefb8f9e65b"

PYPI_PACKAGE = "python-periphery"

RDEPENDS_${PN} += "${PYTHON_PN}-mmap"
