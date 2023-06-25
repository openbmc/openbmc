DESCRIPTION = "Simple, easy-to-use throttler for asyncio."
HOMEPAGE = "https://github.com/hallazzang/asyncio-throttle"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7906e56b70808e1ade6ca05e0bb48d5"

SRC_URI[sha256sum] = "2675282e99d9129ecc446f917e174bc205c65e36c602aa18603b4948567fcbd4"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-asyncio"
