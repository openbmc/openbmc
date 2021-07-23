SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

PYPI_PACKAGE = "qrcode"
SRC_URI[sha256sum] = "59ba630fa2adb637b06571e6ceec1bb0ecf372c458c4447ceba763061bd3af72"

inherit pypi setuptools3

RDEPENDS_${PN} = "python3-six python3-pillow"
