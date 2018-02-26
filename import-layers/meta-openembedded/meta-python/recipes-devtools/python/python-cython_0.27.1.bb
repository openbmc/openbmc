inherit setuptools
require python-cython.inc

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/Cython/"
UPSTREAM_CHECK_REGEX = "/Cython/(?P<pver>(\d+[\.\-_]*)+)"

RDEPENDS_${PN} += "\
    python-distribute \
"
