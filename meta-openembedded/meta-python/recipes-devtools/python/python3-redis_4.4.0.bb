SUMMARY = "Python client for Redis key-value store"
DESCRIPTION = "The Python interface to the Redis key-value store."
HOMEPAGE = "http://github.com/andymccurdy/redis-py"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1db1f331d351900707368237cc4880cf"

SRC_URI[sha256sum] = "7b8c87d19c45d3f1271b124858d2a5c13160c4e74d4835e28273400fa34d5228"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-packaging \
    ${PYTHON_PN}-asyncio \
"
