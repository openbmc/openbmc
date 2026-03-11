SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[sha256sum] = "4770dc3de41bde3966b02eb84fbcf557fb33cce26ad23da12c742fb50ecb11f0"

RDEPENDS:${PN} = " \
	python3-parso \
	python3-core \
	python3-compression \
	python3-pydoc \
	python3-compile \
	python3-json \
"

inherit setuptools3 pypi
