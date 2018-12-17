inherit setuptools
require python-behave.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-enum34 \
    ${PYTHON_PN}-traceback2 \
    "
