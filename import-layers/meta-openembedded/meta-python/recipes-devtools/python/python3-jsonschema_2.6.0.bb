inherit pypi setuptools3
require python-jsonschema.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
