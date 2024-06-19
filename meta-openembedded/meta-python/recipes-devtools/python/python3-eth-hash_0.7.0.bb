SUMMARY = "The Ethereum hashing function, keccak256, sometimes (erroneously) called sha3."
HOMEPAGE = "https://github.com/ethereum/eth-hash"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d7bdfe69b1ffbde073ca6e96f5c53f7"

SRC_URI[sha256sum] = "bacdc705bfd85dadd055ecd35fd1b4f846b671add101427e089a4ca2e8db310a"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
	python3-logging \
	python3-pycryptodome \
"
