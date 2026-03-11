SUMMARY = "JavaScript unobfuscator and beautifier."
HOMEPAGE = "https://beautifier.io/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

SRC_URI[sha256sum] = "5bb18d9efb9331d825735fbc5360ee8f1aac5e52780042803943aa7f854f7592"

PYPI_PACKAGE = "jsbeautifier"

RDEPENDS:${PN} += "\
    python3-core \
    python3-stringold \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
