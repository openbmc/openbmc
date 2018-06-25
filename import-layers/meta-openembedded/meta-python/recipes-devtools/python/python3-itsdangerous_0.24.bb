inherit pypi setuptools3
require python-itsdangerous.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-compression \
"
