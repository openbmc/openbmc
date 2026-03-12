DESCRIPTION = "Asynchronous library to control Philips Hue"
HOMEPAGE = "https://pypi.org/project/aiohue/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dab31a1d28183826937f4b152143a33f"

SRC_URI[sha256sum] = "7fddc328add07715001bd3d75a37c730a5befcb4348b120c5259583fb9fa34bb"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-aiohttp \
    python3-asyncio-throttle \
    python3-awesomeversion \
"
