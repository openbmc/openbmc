SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

PYPI_PACKAGE = "qrcode"
SRC_URI[sha256sum] = "d72861b65e26b611609f0547f0febe58aed8ae229d6bf4e675834f40742915b3"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-six python3-pillow"
