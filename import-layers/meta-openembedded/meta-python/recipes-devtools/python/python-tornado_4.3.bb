inherit pypi setuptools
require python-tornado.inc
RDEPENDS_${PN} += "${PYTHON_PN}-backports-ssl"
