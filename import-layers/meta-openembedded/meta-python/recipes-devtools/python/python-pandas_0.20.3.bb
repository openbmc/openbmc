inherit setuptools
require python-pandas.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-importlib \
"
