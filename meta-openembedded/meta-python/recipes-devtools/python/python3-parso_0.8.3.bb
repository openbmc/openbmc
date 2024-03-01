SUMMARY = "A Python Parser"
HOMEPAGE = "https://github.com/davidhalter/parso"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbaa2675b2424d771451332a7a69503f"

PYPI_PACKAGE = "parso"

SRC_URI[sha256sum] = "8c07be290bb59f03588915921e29e8a50002acaf2cdc5fa0e0114f91709fafa0"

inherit setuptools3 pypi

RDEPENDS:${PN} = " \
	python3-crypt \
	python3-difflib \
	python3-logging \
"
