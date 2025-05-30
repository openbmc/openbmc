SUMMARY = "Common type annotations for ethereum python packages."
HOMEPAGE = "https://github.com/ethereum/eth-typing"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ef06eb4d4373222e59a338e2eb9a795"

SRC_URI[sha256sum] = "7557300dbf02a93c70fa44af352b5c4a58f94e997a0fd6797fb7d1c29d9538ee"

PYPI_PACKAGE = "eth_typing"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-typing-extensions \
"
