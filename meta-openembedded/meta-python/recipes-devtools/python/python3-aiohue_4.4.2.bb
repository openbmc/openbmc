DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "871c27da3d5a5b7d7c5049e6d23713425a2f251639ff1fb5fac460724ba42f15"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-aiohttp \
	${PYTHON_PN}-asyncio-throttle \
	${PYTHON_PN}-profile \
        ${PYTHON_PN}-awesomeversion \
"
