SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "http://cheeseshop.python.org/pypi/simplejson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"

SRC_URI[md5sum] = "6e2f1bd5fb0a926facf5d89d217a7183"
SRC_URI[sha256sum] = "01a22d49ddd9a168b136f26cac87d9a335660ce07aa5c630b8e3607d6f4325e7"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-re \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
"
