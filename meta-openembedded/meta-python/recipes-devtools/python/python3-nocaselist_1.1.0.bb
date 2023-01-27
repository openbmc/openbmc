SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "a99384abc700c409e9def7143763e18dfad332fdff7e30fae1f6d1a30b372772"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-six \
"
