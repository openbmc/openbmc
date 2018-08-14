inherit pypi setuptools
require python-whoosh.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
