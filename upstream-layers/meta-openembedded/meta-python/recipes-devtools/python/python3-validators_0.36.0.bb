SUMMARY = "Python Data Validation for Humans"
DESCRIPTION = "Python Data Validation for Humans"
HOMEPAGE = "https://python-validators.github.io/validators"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=728e6b6dd8ebb4906421b89b00645e6e"
SRC_URI[sha256sum] = "92ad9ed00bbed320b784cec0ea90b213cf08904cf605b3d0852cfa4108e32a2c"

CVE_PRODUCT = "validators"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
	python3-decorator \
	python3-eth-hash \
	python3-isort \
	python3-pycryptodome \
"

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
