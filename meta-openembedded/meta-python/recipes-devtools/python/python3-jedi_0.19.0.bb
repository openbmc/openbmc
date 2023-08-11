SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[sha256sum] = "bcf9894f1753969cbac8022a8c2eaee06bfa3724e4192470aaffe7eb6272b0c4"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-parso \
	${PYTHON_PN}-core \
	${PYTHON_PN}-compression \
	${PYTHON_PN}-pydoc \
	${PYTHON_PN}-compile \
	${PYTHON_PN}-json \
"

inherit setuptools3 pypi
