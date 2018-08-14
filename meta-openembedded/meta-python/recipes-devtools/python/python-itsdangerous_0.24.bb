inherit pypi setuptools
require python-itsdangerous.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-zlib \
"
