SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

PYPI_PACKAGE = "qrcode"
SRC_URI[sha256sum] = "505253854f607f2abf4d16092c61d4e9d511a3b4392e60bff957a68592b04369"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-six python3-pillow"
