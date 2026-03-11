SUMMARY = "Pretty-print tabular data"
HOMEPAGE = "https://github.com/astanin/python-tabulate"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ad1430c0c4824ec6a5dbb9754b011d7"

SRC_URI[sha256sum] = "0095b12bf5966de529c0feb1fa08671671b3368eec77d7ef7ab114be2c068b3c"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
	python3-setuptools-scm-native \
	python3-toml-native \
"

RDEPENDS:${PN} += " \
	python3-html \
	python3-core \
	python3-io \
	python3-math \
	python3-profile \
"
