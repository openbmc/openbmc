inherit setuptools3
require python-babel.inc

# Requires _pydecimal.py which is in misc
RDEPENDS_${PN} += "${PYTHON_PN}-misc"
