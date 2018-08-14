SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "http://cheeseshop.python.org/pypi/simplejson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"

SRC_URI[md5sum] = "870bf6257465ce2ee8cad14e2394f7eb"
SRC_URI[sha256sum] = "4c4ecf20e054716cc1e5a81cadc44d3f4027108d8dd0861d8b1e3bd7a32d4f0a"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
"

PACKAGES =+ "${PN}-tests"
RDEPENDS_${PN}-tests = "${PN} ${PYTHON_PN}-unittest"
FILES_${PN}-tests+= " \
    ${PYTHON_SITEPACKAGES_DIR}/simplejson/tests \
    ${PYTHON_SITEPACKAGES_DIR}/simplejson/tool.py* \
"
