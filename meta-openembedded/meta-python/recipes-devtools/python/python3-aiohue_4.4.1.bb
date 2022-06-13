DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "fa3bb0cf68aefdd0710704443c896abda69ae227500ad9539ac6c3d1d6dad804"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-aiohttp \
	${PYTHON_PN}-asyncio-throttle \
	${PYTHON_PN}-profile \
        ${PYTHON_PN}-awesomeversion \
"
