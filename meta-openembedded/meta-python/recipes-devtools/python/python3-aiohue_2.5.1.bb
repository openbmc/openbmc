DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "3ee8e857b07364516f8b9f0e5c52d4cd775036f3ace37c2769de1e8579f4dc07"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-aiohttp"
