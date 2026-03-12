SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "b06b3d6fec1abc05c607aa8e813599388727a08e18c223431d7469cf6eb0c06a"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	python3-setuptools-scm-native \
	python3-toml-native \
"

RDEPENDS:${PN} += " \
	python3-six \
"
