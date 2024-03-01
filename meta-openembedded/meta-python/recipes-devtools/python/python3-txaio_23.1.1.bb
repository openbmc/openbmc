DESCRIPTION = "Compatibility API between asyncio/Twisted/Trollius"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3e2c2c2cc2915edc5321b0e6b1d3f5f8"

SRC_URI[sha256sum] = "f9a9216e976e5e3246dfd112ad7ad55ca915606b60b84a757ac769bd404ff704"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-twisted \
"
