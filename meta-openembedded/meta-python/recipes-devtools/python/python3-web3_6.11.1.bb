SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=373fede350846fdffd23648fba504635"

SRC_URI[sha256sum] = "d301d7120922d5b9e5c9535ef9780012ea25ea4011c2b177490ba7d3ef886b92"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-aiohttp \
    python3-distutils \
    python3-eth-account \
    python3-idna \
    python3-jsonschema \
    python3-google-api-core \
    python3-lru-dict \
    python3-requests \
    python3-setuptools \
    python3-websockets \
"
