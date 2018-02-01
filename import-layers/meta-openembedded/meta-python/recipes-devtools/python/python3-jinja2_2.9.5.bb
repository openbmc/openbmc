inherit pypi setuptools3
require python-jinja2.inc

# Requires _pydecimal which is in misc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
