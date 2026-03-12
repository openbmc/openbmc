SUMMARY = "Arpeggio is a recursive descent parser with memoization based on PEG grammars (aka Packrat parser)"
HOMEPAGE = "https://pypi.org/project/Arpeggio/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33b8d1ba459a2fa4d801acfd1d1b7ceb"

SRC_URI[sha256sum] = "9e85ad35cfc6c938676817c7ae9a1000a7c72a34c71db0c687136c460d12b85e"

PYPI_PACKAGE = "Arpeggio"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

# setup.py of Arpeggio needs this.
DEPENDS += "\
    python3-pytest-runner-native \
    python3-wheel-native \
"

BBCLASSEXTEND = "native nativesdk"
