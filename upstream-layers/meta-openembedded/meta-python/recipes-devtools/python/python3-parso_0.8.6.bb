SUMMARY = "A Python Parser"
HOMEPAGE = "https://github.com/davidhalter/parso"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbaa2675b2424d771451332a7a69503f"

PYPI_PACKAGE = "parso"

SRC_URI[sha256sum] = "2b9a0332696df97d454fa67b81618fd69c35a7b90327cbe6ba5c92d2c68a7bfd"

CVE_PRODUCT = "parso"

inherit setuptools3 pypi

RDEPENDS:${PN} = " \
	python3-crypt \
	python3-difflib \
	python3-logging \
"
