inherit pypi setuptools
require python-cryptography.inc

SRC_URI += " \
    file://run-ptest \
"

DEPENDS += " \
    ${PYTHON_PN}-enum34 \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-enum34 \
    ${PYTHON_PN}-ipaddress \
"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-enum34 \
    ${PYTHON_PN}-ipaddress \
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-subprocess \
"
