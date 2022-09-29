DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "9a5aaf2fa97a889746142093e74143d5e3e02209fae428d1e2dbcc34a5c36efd"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-aiohttp \
	${PYTHON_PN}-asyncio-throttle \
	${PYTHON_PN}-profile \
        ${PYTHON_PN}-awesomeversion \
"
