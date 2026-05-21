SUMMARY = "A Python Parser"
HOMEPAGE = "https://github.com/davidhalter/parso"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbaa2675b2424d771451332a7a69503f"

PYPI_PACKAGE = "parso"

SRC_URI[sha256sum] = "eaaac4c9fdd5e9e8852dc778d2d7405897ec510f2a298071453e5e3a07914bb1"

CVE_PRODUCT = "parso"

inherit setuptools3 pypi

RDEPENDS:${PN} = " \
	python3-crypt \
	python3-difflib \
	python3-logging \
"
