SUMMARY = "A Python utility / library to sort Python imports."
HOMEPAGE = "https://pypi.python.org/pypi/isort"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "c729845434366216d320e936b8ad6f9d681aab72dc7cbc2d51bedc3582f3ad1e"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
