inherit setuptools
require python-pytest-runner.inc

# Dependency only exists for python2
RDEPENDS_${PN} += "python-compiler"
