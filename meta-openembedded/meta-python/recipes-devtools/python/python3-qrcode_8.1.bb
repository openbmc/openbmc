SUMMARY = "QR Code image generator"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b802d2a65df4626623c79757f486af9"

SRC_URI[sha256sum] = "e8df73caf72c3bace3e93d9fa0af5aa78267d4f3f5bc7ab1b208f271605a5e48"

inherit pypi python_poetry_core

RDEPENDS:${PN} = " \
    python3-six \
    python3-pillow \
    python3-pypng \
    python3-typing-extensions \
"
