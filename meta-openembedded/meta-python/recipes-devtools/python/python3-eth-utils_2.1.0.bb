SUMMARY = "Common utility functions for codebases which interact with ethereum."
HOMEPAGE = "https://github.com/ethereum/eth-utils"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=287820ad3553117aa2f92bf84c219324"

SRC_URI[sha256sum] = "fcb4c3c1b32947ba92970963f9aaf40da73b04ea1034964ff8c0e70595127138"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-hash \
    python3-eth-typing \
    python3-setuptools \
    python3-toolz \
"
