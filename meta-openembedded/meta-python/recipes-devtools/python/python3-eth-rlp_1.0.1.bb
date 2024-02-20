SUMMARY = "RLP definitions for common Ethereum objects in Python"
HOMEPAGE = "https://github.com/ethereum/eth-rlp"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d7bdfe69b1ffbde073ca6e96f5c53f7"

SRC_URI[sha256sum] = "d61dbda892ee1220f28fb3663c08f6383c305db9f1f5624dc585c9cd05115027"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-eth-utils \
    ${PYTHON_PN}-hexbytes \
    ${PYTHON_PN}-rlp \
    ${PYTHON_PN}-typing-extensions \
"
