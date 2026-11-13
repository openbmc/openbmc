SUMMARY = "A case-insensitive list for Python"
HOMEPAGE = "https://nocaselist.readthedocs.io/en/latest/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "bade0e96a104ac8ddae49c261aa7c27b5ae8d9434661c6cd8ec16f3cd5c677a1"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

DEPENDS += " \
	python3-setuptools-scm-native \
	python3-toml-native \
"
