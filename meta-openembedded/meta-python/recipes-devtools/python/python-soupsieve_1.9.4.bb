inherit setuptools
require python-soupsieve.inc

RDEPENDS_${PN} += "${PYTHON_PN}-backports-functools-lru-cache"
