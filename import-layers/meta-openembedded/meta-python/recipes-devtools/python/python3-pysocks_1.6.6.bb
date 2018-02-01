inherit pypi setuptools3
require python-pysocks.inc

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-enum \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-selectors \
"
