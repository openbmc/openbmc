inherit setuptools
require python-numpy.inc

RDEPENDS_${PN}_class-target_append = " \
    ${PYTHON_PN}-subprocess \
"
