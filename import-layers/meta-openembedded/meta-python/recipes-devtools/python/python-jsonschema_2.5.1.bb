inherit pypi setuptools
require python-jsonschema.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-functools32 \
"
