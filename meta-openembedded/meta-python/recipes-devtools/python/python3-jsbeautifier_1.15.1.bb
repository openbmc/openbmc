SUMMARY = "JavaScript unobfuscator and beautifier."
HOMEPAGE = "https://beautifier.io/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

SRC_URI[sha256sum] = "ebd733b560704c602d744eafc839db60a1ee9326e30a2a80c4adb8718adc1b24"

PYPI_PACKAGE = "jsbeautifier"

RDEPENDS:${PN} += "\
    python3-core \
    python3-stringold \
    python3-shell \
"

BBCLASSEXTEND = "native nativesdk"
