SUMMARY = "Python bytes subclass that decodes hex, with a readable console output."
HOMEPAGE = "https://github.com/ethereum/hexbytes"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a26e64020156e806cf0054a6d504b301"

SRC_URI[sha256sum] = "01312fcd5c57e8a8d2d7dd3274dcf84ea50422aff2abcc2d9fd89ad6a32498e5"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-wheel-native \
    python3-setuptools-scm-native \
"

RDEPENDS:${PN} += " \
    python3-core \
    python3-email \
    python3-compression \
"
