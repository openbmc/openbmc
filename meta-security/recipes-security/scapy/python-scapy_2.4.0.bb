inherit setuptools
require python-scapy.inc

SRC_URI += "file://run-ptest"

RDEPENDS_${PN} += "${PYTHON_PN}-subprocess"
