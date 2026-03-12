DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f3b6f0e9bfce02a086e4ddfbc8504e33"

SRC_URI[sha256sum] = "9f232c21e12aa1ff52690e365b5a0ecfd42cc27a6ec86e1b92ece88f763f4b78"

inherit pypi python_setuptools_build_meta python_hatchling

RDEPENDS:${PN} += " \
    python3-twisted \
"
