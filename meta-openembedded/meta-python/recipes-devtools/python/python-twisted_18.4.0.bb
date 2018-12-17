inherit pypi setuptools
require python-twisted.inc

RDEPENDS_${PN}-core += "${PYTHON_PN}-contextlib"

RDEPENDS_${PN} += " \
    ${PN}-news \
"
