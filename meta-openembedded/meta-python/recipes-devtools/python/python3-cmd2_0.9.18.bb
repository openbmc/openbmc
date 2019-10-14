inherit setuptools3
require python-cmd2.inc

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pyperclip \
    ${PYTHON_PN}-colorama \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-wcwidth \
    "
