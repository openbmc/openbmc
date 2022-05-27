SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1dc2732bdc5e50382737979791cbb3b7"

SRC_URI[sha256sum] = "a78acc0f0150013755398ae185c8c7063d9db0777aa09733de22242a746bd30c"

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
