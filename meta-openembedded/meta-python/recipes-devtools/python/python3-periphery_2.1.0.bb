DESCRIPTION = "A pure Python 2/3 library for peripheral I/O (GPIO, LED, PWM, SPI, I2C, MMIO, Serial) in Linux."
HOMEPAGE = "http://pythonhosted.org/python-periphery/"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=fea92e2e010ccb151792c29fadc2db7b"

SRC_URI[md5sum] = "27cdd7e026438067a238fb04ed5766a3"
SRC_URI[sha256sum] = "57baa82e6bc59b67747317d16ad0cf9626826e8d43233af13bce924660500bd6"

inherit pypi setuptools3

PYPI_PACKAGE = "python-periphery"

RDEPENDS_${PN} += "${PYTHON_PN}-mmap"
