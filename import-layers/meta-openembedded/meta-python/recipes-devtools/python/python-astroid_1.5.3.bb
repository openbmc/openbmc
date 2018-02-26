inherit setuptools
require python-astroid.inc

RDEPENDS_${PN}_class-target += "${PYTHON_PN}-enum34 \
                                ${PYTHON_PN}-importlib \
                                ${PYTHON_PN}-singledispatch \
                               "
