inherit setuptools3
require python-cmd2.inc

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pyperclip \
    ${PYTHON_PN}-colorama \
    ${PYTHON_PN}-attrs \
    ${PYTHON_PN}-wcwidth \
    "
PNBLACKLIST[python3-cmd2] = "Nothing RPROVIDES 'python3-colorama' (but python3-cmd2_0.9.16.bb RDEPENDS on or otherwise requires it)"
