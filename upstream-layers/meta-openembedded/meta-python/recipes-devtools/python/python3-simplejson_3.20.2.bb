SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "https://cheeseshop.python.org/pypi/simplejson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"

SRC_URI[sha256sum] = "5fe7a6ce14d1c300d80d08695b7f7e633de6cd72c80644021874d985b3393649"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-io \
    python3-netserver \
    python3-numbers \
"

PACKAGES =+ "${PN}-tests"
RDEPENDS:${PN}-tests = "${PN} python3-unittest"
FILES:${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/simplejson/tests \
    ${PYTHON_SITEPACKAGES_DIR}/simplejson/tool.py* \
"
CVE_PRODUCT = "simplejson"

BBCLASSEXTEND = "native nativesdk"
