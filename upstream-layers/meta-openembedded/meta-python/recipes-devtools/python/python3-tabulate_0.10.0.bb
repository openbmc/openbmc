SUMMARY = "Pretty-print tabular data"
HOMEPAGE = "https://github.com/astanin/python-tabulate"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ad1430c0c4824ec6a5dbb9754b011d7"

SRC_URI[sha256sum] = "e2cfde8f79420f6deeffdeda9aaec3b6bc5abce947655d17ac662b126e48a60d"

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
