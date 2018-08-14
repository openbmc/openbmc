inherit setuptools
require python-pyinotify.inc

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-subprocess \
"
