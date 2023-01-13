SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
AUTHOR = "David Halter <davidhalter88@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[sha256sum] = "bae794c30d07f6d910d32a7048af09b5a39ed740918da923c6b780790ebac612"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-parso \
	${PYTHON_PN}-core \
	${PYTHON_PN}-compression \
	${PYTHON_PN}-pydoc \
	${PYTHON_PN}-compile \
	${PYTHON_PN}-json \
"

inherit setuptools3 pypi
