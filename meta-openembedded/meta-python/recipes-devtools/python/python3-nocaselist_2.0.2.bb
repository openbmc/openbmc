SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "32708b700a1a53133e6bb5cc53332c9775b0c7c959a5f9725793171fd2f4c8a5"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-six \
"
