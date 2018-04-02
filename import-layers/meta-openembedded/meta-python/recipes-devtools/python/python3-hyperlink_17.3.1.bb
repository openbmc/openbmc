inherit setuptools3
require python-hyperlink.inc

RDEPENDS_${PN} += "${PYTHON_PN}-selectors ${PYTHON_PN}-enum"
