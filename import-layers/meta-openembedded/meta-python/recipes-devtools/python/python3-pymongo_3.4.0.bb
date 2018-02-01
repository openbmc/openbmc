inherit pypi setuptools3
require python-pymongo.inc

# Requires _pydecimal which is in misc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-enum \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-selectors \
    ${PYTHON_PN}-subprocess \
"
