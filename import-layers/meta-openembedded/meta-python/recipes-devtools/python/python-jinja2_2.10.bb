inherit pypi setuptools
require python-jinja2.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-lang \
    ${PYTHON_PN}-re \
    ${PYTHON_PN}-textutils \
"
