inherit setuptools
require python-requests.inc

RDEPENDS_${PN} += "${PYTHON_PN}-zlib"
