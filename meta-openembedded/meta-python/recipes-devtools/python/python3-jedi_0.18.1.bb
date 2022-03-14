SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
AUTHOR = "David Halter <davidhalter88@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[sha256sum] = "74137626a64a99c8eb6ae5832d99b3bdd7d29a3850fe2aa80a4126b2a7d949ab"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-parso \
	${PYTHON_PN}-core \
	${PYTHON_PN}-compression \
	${PYTHON_PN}-pydoc \
	${PYTHON_PN}-compile \
	${PYTHON_PN}-json \
"

inherit setuptools3 pypi
