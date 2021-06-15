SUMMARY = "An autocompletion tool for Python that can be used for text editors."
HOMEPAGE = "https://github.com/davidhalter/jedi"
AUTHOR = "David Halter <davidhalter88@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5ed06eebfcb244cd66ebf6cef9c23ab4"

PYPI_PACKAGE = "jedi"

SRC_URI[sha256sum] = "92550a404bad8afed881a137ec9a461fed49eca661414be45059329614ed0707"

RDEPENDS_${PN} = "${PYTHON_PN}-parso"

inherit setuptools3 pypi
