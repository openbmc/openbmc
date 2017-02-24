inherit pypi setuptools
require python-werkzeug.inc

RDEPENDS_${PN} += "${PYTHON_PN}-zlib"
