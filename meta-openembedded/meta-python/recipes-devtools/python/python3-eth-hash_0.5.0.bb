SUMMARY = "The Ethereum hashing function, keccak256, sometimes (erroneously) called sha3."
HOMEPAGE = "https://github.com/ethereum/eth-hash"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "ea0fd4e264c97c8aa739ae1cea7199db2e1f3bdf387cc9b81ef03c660f871335"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-pycryptodome \
"
