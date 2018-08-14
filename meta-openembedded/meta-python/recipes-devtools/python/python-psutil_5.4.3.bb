inherit pypi setuptools
require python-psutil.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
