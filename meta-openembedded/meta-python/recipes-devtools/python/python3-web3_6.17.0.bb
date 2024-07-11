SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=373fede350846fdffd23648fba504635"

SRC_URI[sha256sum] = "1b535272a40da3d8d2b120856edb53b84b0c08bcc8fe1a5bbd5f816fd72f4ec6"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-aiohttp \
    python3-eth-abi \
    python3-eth-account \
    python3-eth-hash \
    python3-eth-typing \
    python3-eth-utils \
    python3-hexbytes \
    python3-jsonschema \
    python3-protobuf \
    python3-pydantic \
    python3-requests \
    python3-typing-extensions \
    python3-websockets \
    python3-pyunormalize \
"
