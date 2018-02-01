inherit setuptools3
require python-simplejson.inc

# Requires _pydecimal which is in misc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-misc \
"
