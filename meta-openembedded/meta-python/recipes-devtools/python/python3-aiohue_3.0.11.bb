DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "31c0b90d7dc2b62f5e1a93e78b2c0d6360ef6c0a87485c217a4f394c937fce7d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-aiohttp \
	${PYTHON_PN}-asyncio-throttle \
	${PYTHON_PN}-profile \
"
