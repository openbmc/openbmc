DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "e2ae49be45261283a899cc1b95786f07fe5076be9a311d250dbe2de1b8c38f0f"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-aiohttp"
