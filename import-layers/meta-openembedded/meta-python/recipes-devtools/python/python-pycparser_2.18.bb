inherit pypi setuptools
require python-pycparser.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
