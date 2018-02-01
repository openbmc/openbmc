inherit setuptools3
require python-cassandra-driver.inc

# Requires concurrent which is currently in -misc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-numbers \
"
