SUMMARY = "A Python utility / library to sort Python imports."
HOMEPAGE = "https://pypi.python.org/pypi/isort"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "f65ce5bd4cbc6abdfbe29afc2f0245538ab358c14590912df638033f157d555e"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-difflib \
"

BBCLASSEXTEND = "native nativesdk"
