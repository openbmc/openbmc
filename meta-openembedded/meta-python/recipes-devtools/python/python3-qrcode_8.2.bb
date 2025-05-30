SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

SRC_URI[sha256sum] = "35c3f2a4172b33136ab9f6b3ef1c00260dd2f66f858f24d88418a015f446506c"

inherit pypi python_poetry_core

RDEPENDS:${PN} = " \
    python3-six \
    python3-pillow \
    python3-pypng \
    python3-typing-extensions \
"
