require python-can.inc
inherit pypi setuptools

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-zlib \
"    
