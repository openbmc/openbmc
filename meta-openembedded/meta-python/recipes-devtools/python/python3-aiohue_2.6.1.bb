DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "1374f7fc50bac46375e18ce7d511515265ce83c9180f312e60a36d63055f0104"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-aiohttp"
