inherit pypi setuptools
require python-zopeinterface.inc

RDEPENDS_${PN}-test += "python-unittest python-doctest"
