DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=588502cb4ffc65da2b26780d6baa5a40"

SRC_URI[sha256sum] = "d8c03dca823515c9bca920df33504923ae54f2dabf476cc5a9ed5cc1691ed687"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-twisted \
"
