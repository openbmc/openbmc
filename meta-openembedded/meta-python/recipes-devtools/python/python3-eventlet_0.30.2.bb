DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[md5sum] = "ebf5794e3f7a9f1778fb4f2a58eb7614"
SRC_URI[sha256sum] = "1811b122d9a45eb5bafba092d36911bca825f835cb648a862bbf984030acff9d"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-dnspython"
