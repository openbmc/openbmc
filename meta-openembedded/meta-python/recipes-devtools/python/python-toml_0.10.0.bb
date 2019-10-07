inherit setuptools
require python-toml.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
"
