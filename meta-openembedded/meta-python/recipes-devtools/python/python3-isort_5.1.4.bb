SUMMARY = "A Python utility / library to sort Python imports."
HOMEPAGE = "https://pypi.python.org/pypi/isort"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[md5sum] = "64d552bbcef48219572cc39649c48fbd"
SRC_URI[sha256sum] = "145072eedc4927cc9c1f9478f2d83b2fc1e6469df4129c02ef4e8c742207a46c"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
