inherit setuptools
require python-pyperclip.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-subprocess \
"
