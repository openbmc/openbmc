inherit pypi setuptools3
require python-tornado.inc

# Requires _compression which is currently located in misc
RDEPENDS_${PN} += "\
    ${PYTHON_PN}-misc \
    "
