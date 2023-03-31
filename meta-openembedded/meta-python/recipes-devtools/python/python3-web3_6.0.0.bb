SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dc2732bdc5e50382737979791cbb3b7"

SRC_URI[sha256sum] = "6b925a19e4a0001337d8b2faa72577d6b7e8f9a8a9a0b98d8834cdf698cfc045"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-aiohttp \
    python3-distutils \
    python3-eth-account \
    python3-idna \
    python3-lru-dict \
    python3-requests \
    python3-setuptools \
    python3-websockets \
"
