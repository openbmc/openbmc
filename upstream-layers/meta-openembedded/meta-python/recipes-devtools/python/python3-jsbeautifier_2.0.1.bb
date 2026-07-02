SUMMARY = "JavaScript unobfuscator and beautifier."
HOMEPAGE = "https://beautifier.io/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

SRC_URI[sha256sum] = "45603b2097410feee8d3a6ef8ad5a8e0a0e89f347331888a97e46f332ce8d953"

PYPI_PACKAGE = "jsbeautifier"

RDEPENDS:${PN} += "\
    python3-core \
    python3-stringold \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
