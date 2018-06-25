inherit pypi setuptools
require python-pyasn1.inc

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-lang \
"
