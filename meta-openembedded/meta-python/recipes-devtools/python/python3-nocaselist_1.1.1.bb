SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "5272b232e08246696ab26fe0ebbd28b8989dac9ee5732b50264950323b513d23"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-six \
"
