inherit setuptools
require python-pandas.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-future \
    ${PYTHON_PN}-json \
"
