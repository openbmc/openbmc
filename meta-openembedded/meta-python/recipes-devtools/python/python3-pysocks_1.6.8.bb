inherit pypi setuptools3
require python-pysocks.inc

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-netclient \
"
