SUMMARY = "The Ethereum hashing function, keccak256, sometimes (erroneously) called sha3."
HOMEPAGE = "https://github.com/ethereum/eth-hash"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d7bdfe69b1ffbde073ca6e96f5c53f7"

SRC_URI[sha256sum] = "ae72889e60db6acbb3872c288cfa02ed157f4c27630fcd7f9c8442302c31e478"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-pycryptodome \
"
