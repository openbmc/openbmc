inherit pypi setuptools3
require python-pycparser.inc

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-netclient \
    " 
