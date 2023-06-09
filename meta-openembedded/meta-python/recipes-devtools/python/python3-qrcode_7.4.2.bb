SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

PYPI_PACKAGE = "qrcode"
SRC_URI[sha256sum] = "9dd969454827e127dbd93696b20747239e6d540e082937c90f14ac95b30f5845"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-six \
    python3-pillow \
    python3-pypng \
    python3-typing-extensions \
"
