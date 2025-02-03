SUMMARY = "Python Data Validation for Humans"
HOMEPAGE = "https://python-validators.github.io/validators"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b3fb4b9e6db86c69a33d5e3ee013ab59"
SRC_URI[sha256sum] = "647fe407b45af9a74d245b943b18e6a816acf4926974278f6dd617778e1e781f"

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
