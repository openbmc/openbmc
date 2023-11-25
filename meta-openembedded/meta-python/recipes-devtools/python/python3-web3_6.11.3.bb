SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=373fede350846fdffd23648fba504635"

SRC_URI[sha256sum] = "f9bec9d2339bf649fe25293435a5c897a4b035aa96d6c33670ed467acf59dbe7"

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
