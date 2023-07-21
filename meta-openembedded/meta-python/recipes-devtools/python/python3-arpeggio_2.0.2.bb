SUMMARY = "Arpeggio is a recursive descent parser with memoization based on PEG grammars (aka Packrat parser)"
HOMEPAGE = "https://pypi.org/project/Arpeggio/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33b8d1ba459a2fa4d801acfd1d1b7ceb"

SRC_URI[sha256sum] = "c790b2b06e226d2dd468e4fbfb5b7f506cec66416031fde1441cf1de2a0ba700"

PYPI_PACKAGE = "Arpeggio"
inherit pypi setuptools3

# setup.py of Arpeggio needs this.
DEPENDS += "\
    ${PYTHON_PN}-pytest-runner-native \
    ${PYTHON_PN}-wheel-native \
"

BBCLASSEXTEND = "native nativesdk"
