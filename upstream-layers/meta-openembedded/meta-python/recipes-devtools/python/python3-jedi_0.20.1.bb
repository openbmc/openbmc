SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"


SRC_URI[sha256sum] = "2f71208c3f9c1bca057c0e90d3f272aba44ace88fc4067d7587e9e069331b7e5"

RDEPENDS:${PN} = " \
	python3-parso \
	python3-core \
	python3-compression \
	python3-pydoc \
	python3-compile \
	python3-json \
"

inherit setuptools3 pypi
