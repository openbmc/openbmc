SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d34d9701a1461e4bd71a904ac4cf7be"

SRC_URI[sha256sum] = "b4a75a3fa94fef4d23d502eb3c2244146ef9a1ee0082cf1cb0a91586ba0510c3"

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
