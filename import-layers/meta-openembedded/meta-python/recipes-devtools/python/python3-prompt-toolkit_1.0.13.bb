inherit pypi setuptools3
require python-prompt-toolkit.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-enum \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-textutils \
"
