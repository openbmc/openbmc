inherit pypi setuptools
require python-jsonschema.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-functools32 \
    ${PYTHON_PN}-lang \
    ${PYTHON_PN}-re \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-textutils \ 
"
