SUMMARY = "The Ethereum hashing function, keccak256, sometimes (erroneously) called sha3."
HOMEPAGE = "https://github.com/ethereum/eth-hash"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "8cde211519ff1a98b46e9057cb909f12ab62e263eb30a0a94e2f7e1f46ac67a0"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
	${PYTHON_PN}-logging \
	${PYTHON_PN}-pycryptodome \
"
