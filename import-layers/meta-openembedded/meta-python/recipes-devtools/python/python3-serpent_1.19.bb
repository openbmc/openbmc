inherit setuptools3
require python-serpent.inc

# Requires _pydecimal which is in misc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-enum \
    ${PYTHON_PN}-misc \
"
