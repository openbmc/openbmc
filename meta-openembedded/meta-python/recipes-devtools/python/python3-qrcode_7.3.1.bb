SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

PYPI_PACKAGE = "qrcode"
SRC_URI[sha256sum] = "375a6ff240ca9bd41adc070428b5dfc1dcfbb0f2507f1ac848f6cded38956578"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-six python3-pillow"
