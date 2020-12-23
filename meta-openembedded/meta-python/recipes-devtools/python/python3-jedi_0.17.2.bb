SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
AUTHOR = "David Halter <davidhalter88@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[md5sum] = "f012668907d76cebe9c4766f3b806fcf"
SRC_URI[sha256sum] = "86ed7d9b750603e4ba582ea8edc678657fb4007894a12bcf6f4bb97892f31d20"

RDEPENDS_${PN} = "${PYTHON_PN}-parso"

inherit setuptools3 pypi
