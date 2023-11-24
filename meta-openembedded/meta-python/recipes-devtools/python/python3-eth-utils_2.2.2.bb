SUMMARY = "Common utility functions for codebases which interact with ethereum."
HOMEPAGE = "https://github.com/ethereum/eth-utils"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6749008d847c14b9718949c2e24d5c0a"

SRC_URI[sha256sum] = "5ca6265177ce544d9d43cdf2272ae2227e5d6d9529c270bbb707d17339087101"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-eth-hash \
    python3-eth-typing \
    python3-setuptools \
    python3-toolz \
"
