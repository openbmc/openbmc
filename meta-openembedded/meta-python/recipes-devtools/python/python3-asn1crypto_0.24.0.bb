inherit setuptools3
require python-asn1crypto.inc

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-shell \
"
