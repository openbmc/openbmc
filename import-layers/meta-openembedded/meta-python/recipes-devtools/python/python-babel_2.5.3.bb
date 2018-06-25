inherit setuptools
require python-babel.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
