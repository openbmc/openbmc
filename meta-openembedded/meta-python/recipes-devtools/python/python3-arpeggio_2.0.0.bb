SUMMARY = "Arpeggio is a recursive descent parser with memoization based on PEG grammars (aka Packrat parser)"
HOMEPAGE = "https://pypi.org/project/Arpeggio/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33b8d1ba459a2fa4d801acfd1d1b7ceb"

SRC_URI[sha256sum] = "d6b03839019bb8a68785f9292ee6a36b1954eb84b925b84a6b8a5e1e26d3ed3d"

PYPI_PACKAGE = "Arpeggio"
inherit pypi setuptools3

# setup.py of Arpeggio needs this.
DEPENDS += "\
    ${PYTHON_PN}-pytest-runner-native \
    ${PYTHON_PN}-wheel-native \
"

BBCLASSEXTEND = "native nativesdk"
