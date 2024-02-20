SUMMARY = "A Python library for interacting with Ethereum."
HOMEPAGE = "https://github.com/ethereum/web3.py"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=373fede350846fdffd23648fba504635"

SRC_URI[sha256sum] = "f9e7eefc1b3c3d194868a4ef9583b625c18ea3f31a48ebe143183db74898f381"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-aiohttp \
    ${PYTHON_PN}-eth-abi \
    ${PYTHON_PN}-eth-account \
    ${PYTHON_PN}-eth-hash \
    ${PYTHON_PN}-eth-typing \
    ${PYTHON_PN}-eth-utils \
    ${PYTHON_PN}-hexbytes \
    ${PYTHON_PN}-jsonschema \
    ${PYTHON_PN}-protobuf \
    ${PYTHON_PN}-pydantic \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-typing-extensions \
    ${PYTHON_PN}-websockets \
    ${PYTHON_PN}-pyunormalize \
"
