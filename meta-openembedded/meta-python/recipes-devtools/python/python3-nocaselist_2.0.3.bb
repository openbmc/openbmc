SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "55714da8433fb4843ce797404977e4385d5e3df9e4aa00f7dde983fd87410fef"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-six \
"
