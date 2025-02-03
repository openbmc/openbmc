SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c8694da5a97575618474d9caf8c812da"

SRC_URI[sha256sum] = "4d0332b9a78b855e57ccebd9e1e74c8e855b95869ac7b8fe5878731593e76408"

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
