inherit pypi setuptools
require python-twisted.inc

RDEPENDS_${PN}-core += "${PYTHON_PN}-contextlib"

# Not yet ported to py3 yet available in py2
# See src/twisted/python/_setup.py -- notPortedModules
RDEPENDS_${PN} += "\
    ${PN}-news \
    "
