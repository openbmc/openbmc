SUMMARY = "Python client for Redis key-value store"
DESCRIPTION = "The Python interface to the Redis key-value store."
HOMEPAGE = "http://github.com/andymccurdy/redis-py"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51d9ad56299ab60ba7be65a621004f27"

SRC_URI[sha256sum] = "ccf692811f2c1fc7a92b466aa2599e4a6d2d73d5f736a2c70be600657c0da34a"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-cryptography \
"
