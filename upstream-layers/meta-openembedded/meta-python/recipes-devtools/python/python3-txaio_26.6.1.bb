DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f3b6f0e9bfce02a086e4ddfbc8504e33"

SRC_URI[sha256sum] = "3ee900b2331c93457530fddbccc1a320c4e2d7ac8f9073d01c3fbe87762ccb35"

inherit pypi python_setuptools_build_meta python_hatchling

RDEPENDS:${PN} += " \
    python3-twisted \
"
