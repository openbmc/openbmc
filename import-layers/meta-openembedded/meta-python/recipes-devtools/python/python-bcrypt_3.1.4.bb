inherit setuptools
require python-bcrypt.inc

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-subprocess \
"
