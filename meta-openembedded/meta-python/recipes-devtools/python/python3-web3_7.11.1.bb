SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d34d9701a1461e4bd71a904ac4cf7be"

SRC_URI[sha256sum] = "1b23f323cb939c3c9c16a92228d1a62f109fed026089b0d8ce5829eca26031a3"

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
