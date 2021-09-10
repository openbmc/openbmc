SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "http://cheeseshop.python.org/pypi/simplejson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"

SRC_URI[sha256sum] = "91cfb43fb91ff6d1e4258be04eee84b51a4ef40a28d899679b9ea2556322fb50"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
"

PACKAGES =+ "${PN}-tests"
RDEPENDS:${PN}-tests = "${PN} ${PYTHON_PN}-unittest"
FILES:${PN}-tests+= " \
    ${PYTHON_SITEPACKAGES_DIR}/simplejson/tests \
    ${PYTHON_SITEPACKAGES_DIR}/simplejson/tool.py* \
"

BBCLASSEXTEND = "native nativesdk"
