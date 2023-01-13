SUMMARY = "The Ethereum hashing function, keccak256, sometimes (erroneously) called sha3."
HOMEPAGE = "https://github.com/ethereum/eth-hash"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "9805075f653e114a31a99678e93b257fb4082337696f4eff7b4371fe65158409"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-pycryptodome \
"
