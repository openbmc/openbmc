SUMMARY = "Assign Ethereum transactions and messages with local private keys."
HOMEPAGE = "https://github.com/ethereum/eth-account"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16ffc86adf4293d4cfb204e77d62cfe6"

SRC_URI[sha256sum] = "0374c5886f35780e1f23e50ce6b0482f3387646eceda544e97d69f17f6c216ee"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-bitarray \
    python3-eth-abi \
    python3-eth-keyfile \
    python3-eth-keys \
    python3-eth-rlp \
    python3-eth-utils \
    python3-hexbytes \
    python3-rlp \
"
