inherit pypi setuptools3
require python-cryptography.inc

LDSHARED += "-pthread"

SRC_URI += " \
    file://run-ptest \
"
