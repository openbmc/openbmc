SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "456aa000c6777c5d21b029c52e532f94328d4fb4f15ad2a4dd3dd62db30b3892"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-six \
"
