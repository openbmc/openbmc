inherit setuptools
require python-attrs.inc

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-subprocess \
"
