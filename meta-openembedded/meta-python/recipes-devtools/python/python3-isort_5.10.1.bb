SUMMARY = "A Python utility / library to sort Python imports."
HOMEPAGE = "https://pypi.python.org/pypi/isort"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "e8443a5e7a020e9d7f97f1d7d9cd17c88bcb3bc7e218bf9cf5095fe550be2951"

inherit pypi python_poetry_core

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-difflib \
"

BBCLASSEXTEND = "native nativesdk"
