SUMMARY="Pylint is a Python source code analyzer"
HOMEPAGE= "http://www.pylint.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c107cf754550e65755c42985a5d4e9c9"

SRC_URI[sha256sum] = "911d3a97c808f7554643bcc5416028cfdc42eae34ed129b150741888c688d5d5"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-astroid \
                   ${PYTHON_PN}-isort \
                   ${PYTHON_PN}-numbers \
                   ${PYTHON_PN}-shell \
                   ${PYTHON_PN}-json \
                   ${PYTHON_PN}-pkgutil \
                   ${PYTHON_PN}-difflib \
                   ${PYTHON_PN}-netserver \
                  "
