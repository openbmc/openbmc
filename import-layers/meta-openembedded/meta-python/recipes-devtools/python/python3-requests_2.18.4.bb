inherit setuptools3
require python-requests.inc

# Add the runtime depends for selectors.py
RDEPENDS_${PN} += "${PYTHON_PN}-misc"
