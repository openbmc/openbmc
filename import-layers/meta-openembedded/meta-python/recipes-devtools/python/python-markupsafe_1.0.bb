inherit setuptools
require python-markupsafe.inc

RDEPENDS_${PN} += "${PYTHON_PN}-re"

BBCLASSEXTEND = "native"
