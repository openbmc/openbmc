SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[sha256sum] = "cf0496f3651bc65d7174ac1b7d043eff454892c708a87d1b683e57b569927ffd"

RDEPENDS:${PN} = " \
	python3-parso \
	python3-core \
	python3-compression \
	python3-pydoc \
	python3-compile \
	python3-json \
"

inherit setuptools3 pypi
