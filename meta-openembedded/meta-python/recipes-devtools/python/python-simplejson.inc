SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "http://cheeseshop.python.org/pypi/simplejson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"

SRC_URI[md5sum] = "744fa99aed207478f8fbf9dd9f04fad3"
SRC_URI[sha256sum] = "b1f329139ba647a9548aa05fb95d046b4a677643070dc2afc05fa2e975d09ca5"

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
