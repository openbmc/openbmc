SUMMARY = "JavaScript unobfuscator and beautifier."
HOMEPAGE = "https://beautifier.io/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6b632581ea60dd1c133cd25a48ad187b4b91f526623c4b0fb5443ef805250505"

PYPI_PACKAGE="jsbeautifier"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
