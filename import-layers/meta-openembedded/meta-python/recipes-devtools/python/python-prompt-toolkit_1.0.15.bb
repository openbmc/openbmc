inherit pypi setuptools
require python-prompt-toolkit.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
