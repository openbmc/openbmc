SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a26e64020156e806cf0054a6d504b301"

SRC_URI[sha256sum] = "2c8291b1a8fcbd29a55b07f75f0a0aaffd04704d3ec28d1396587f18a5541c6d"

CVE_PRODUCT = "eth-account"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-wheel-native \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-keys \
    python3-eth-rlp \
    python3-eth-utils \
    python3-hexbytes \
    python3-rlp \
    python3-ckzg \
"
