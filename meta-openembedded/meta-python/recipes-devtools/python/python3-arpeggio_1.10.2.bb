SUMMARY = "Arpeggio is a recursive descent parser with memoization based on PEG grammars (aka Packrat parser)"
HOMEPAGE = "https://pypi.org/project/Arpeggio/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33b8d1ba459a2fa4d801acfd1d1b7ceb"

SRC_URI[sha256sum] = "bfe349f252f82f82d84cb886f1d5081d1a31451e6045275e9f90b65d0daa06f1"

PYPI_PACKAGE = "Arpeggio"
inherit pypi setuptools3

# setup.py of Arpeggio needs this.
DEPENDS += "\
    ${PYTHON_PN}-pytest-runner-native \
    ${PYTHON_PN}-wheel-native \
"

BBCLASSEXTEND = "native nativesdk"
