inherit setuptools
require python-robotframework.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-zlib \
"
