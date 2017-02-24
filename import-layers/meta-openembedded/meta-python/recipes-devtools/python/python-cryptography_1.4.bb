inherit pypi setuptools
require python-cryptography.inc

SRC_URI += " \
    file://run-ptest \
"

DEPENDS += " \
    ${PYTHON_PN}-enum34 \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-enum34 \
    ${PYTHON_PN}-ipaddress \
"
