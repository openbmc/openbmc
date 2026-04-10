SUMMARY = "Common type annotations for ethereum python packages."
HOMEPAGE = "https://github.com/ethereum/eth-typing"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ef06eb4d4373222e59a338e2eb9a795"

SRC_URI[sha256sum] = "315dd460dc0b71c15a6cd51e3c0b70d237eec8771beb844144f3a1fb4adb2392"

PYPI_PACKAGE = "eth_typing"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-typing-extensions \
"
