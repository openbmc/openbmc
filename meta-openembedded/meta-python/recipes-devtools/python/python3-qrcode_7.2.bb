SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

PYPI_PACKAGE = "qrcode"
SRC_URI[sha256sum] = "153ad96f5892e6fe2f3699296240976ac3a6d068e2eb48bbfc64b4c4c4d675ea"

inherit pypi setuptools3

RDEPENDS:${PN} = "python3-six python3-pillow"
