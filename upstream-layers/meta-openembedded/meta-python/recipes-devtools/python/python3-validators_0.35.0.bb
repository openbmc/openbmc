SUMMARY = "Python Data Validation for Humans"
HOMEPAGE = "https://python-validators.github.io/validators"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=78327e3919fcd4e9a4a07299899c634c"
SRC_URI[sha256sum] = "992d6c48a4e77c81f1b4daba10d16c3a9bb0dbb79b3a19ea847ff0928e70497a"

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
