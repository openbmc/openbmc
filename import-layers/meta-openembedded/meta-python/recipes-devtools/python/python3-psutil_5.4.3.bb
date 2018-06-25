inherit pypi setuptools3
require python-psutil.inc

RDEPENDS_${PN} += "${PYTHON_PN}-netclient"
