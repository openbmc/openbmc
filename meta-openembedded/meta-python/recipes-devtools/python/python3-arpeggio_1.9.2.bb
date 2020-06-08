SUMMARY = "Arpeggio is a recursive descent parser with memoization based on PEG grammars (aka Packrat parser)"
HOMEPAGE = "https://pypi.org/project/Arpeggio/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33b8d1ba459a2fa4d801acfd1d1b7ceb"

SRC_URI[md5sum] = "39667a626217c670bc634444be6e904a"
SRC_URI[sha256sum] = "948ce06163a48a72c97f4fe79ad3d1c1330b6fec4f22ece182fb60ef60bd022b"

PYPI_PACKAGE = "Arpeggio"
inherit pypi setuptools3

# setup.py of Arpeggio needs this.
DEPENDS += "${PYTHON_PN}-pytest-runner-native"

BBCLASSEXTEND = "native nativesdk"
