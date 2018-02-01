inherit pypi setuptools3
require python-cryptography.inc

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-lang \
"
