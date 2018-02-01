inherit setuptools3
require python-decorator.inc

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-enum \
"
