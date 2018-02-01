inherit pypi setuptools3
require python-jsonschema.inc

DEPENDS += "python3-vcversioner"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
