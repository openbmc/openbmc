inherit setuptools3
require python-dnspython.inc

# Requires _pydecimal.py which is in misc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-enum \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-selectors \
"
