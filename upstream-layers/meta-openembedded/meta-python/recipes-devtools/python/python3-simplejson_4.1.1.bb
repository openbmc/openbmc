SUMMARY = "Simple, fast, extensible JSON encoder/decoder for Python"
HOMEPAGE = "https://cheeseshop.python.org/pypi/simplejson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c6338d7abd321c0b50a2a547e441c52e"

SRC_URI[sha256sum] = "c08eb9f7a90f77ae470e19a07472e9a79ebc0d1c2315d86a72767665bd5ba79f"

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
