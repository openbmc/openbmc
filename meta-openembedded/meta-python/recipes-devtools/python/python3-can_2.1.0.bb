require python-can.inc
inherit pypi setuptools3

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-compression \
"
