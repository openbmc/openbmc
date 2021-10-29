DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "ce9c240ca3eb1394c56503b403589f4d0ee7f93445a578b78da8b7879a65c863"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-aiohttp"
