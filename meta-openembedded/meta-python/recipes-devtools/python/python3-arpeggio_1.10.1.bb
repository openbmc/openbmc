SUMMARY = "Arpeggio is a recursive descent parser with memoization based on PEG grammars (aka Packrat parser)"
HOMEPAGE = "https://pypi.org/project/Arpeggio/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33b8d1ba459a2fa4d801acfd1d1b7ceb"

SRC_URI[sha256sum] = "920d12cc762edb2eb56daae64a14c93e43dc181b481c88fc79314c0df6ee639e"

PYPI_PACKAGE = "Arpeggio"
inherit pypi setuptools3

# setup.py of Arpeggio needs this.
DEPENDS += "\
    ${PYTHON_PN}-pytest-runner-native \
    ${PYTHON_PN}-wheel-native \
"

BBCLASSEXTEND = "native nativesdk"
