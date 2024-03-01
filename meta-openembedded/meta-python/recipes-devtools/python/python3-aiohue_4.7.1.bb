DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "29b5e5ae05938cac195b1969e70bd6ad4e4e2e105d0e565849803d2a99ff47d1"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
	python3-aiohttp \
	python3-asyncio-throttle \
	python3-profile \
        python3-awesomeversion \
"
